package com.ego.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ego.auth.entity.UserInfo;
import com.ego.common.enums.ExceptionEnum;
import com.ego.common.exception.PayException;
import com.ego.common.pojo.CartDto;
import com.ego.common.util.IdWorker;
import com.ego.order.client.GoodsClient;
import com.ego.order.dto.OrderStatusEnum;
import com.ego.order.dto.PayStateEnum;
import com.ego.order.dto.SeckillMessage;
import com.ego.order.filter.LoginInterceptor;
import com.ego.order.mapper.*;
import com.ego.order.pojo.*;
import com.ego.order.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author yaorange
 * @date 2019/03/01
 */
@Service
@Slf4j
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private PayLogMapper payLogMapper;

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private IdWorker idWorker;

    @Resource
    private PayHelper payHelper;

    @Resource
    private PayLogService payLogService;

    @Resource
    private AmqpTemplate amqpTemplate;


    @Transactional
    public Long createOrder(Order order) {
        //生成订单ID，采用自己的算法生成订单ID
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);

        order.setCreateTime(new Date());
        order.setPostFee(0L);  //// TODO 调用物流信息，根据地址计算邮费

        //获取用户信息
        UserInfo user = LoginInterceptor.getLoginUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);  //卖家为留言


        //保存order
        orderMapper.insert(order);

        order.getOrderDetails().forEach(orderDetail -> {
            orderDetail.setOrderId(orderId);
        });
        //批量保存detail
        order.getOrderDetails().forEach(orderDetail -> orderDetailMapper.insert(orderDetail));


        //填充orderStatus
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
        orderStatus.setCreateTime(new Date());

        //保存orderStatus
        orderStatusMapper.insert(orderStatus);

        //保存交付日志
        payLogService.createPayLog(orderId,order.getActualPay());
        //减库存
        List<CartDto> cartDTOS = new ArrayList<>();
        order.getOrderDetails().forEach(orderDetail -> {
            CartDto cartDto= new CartDto();
            cartDto.setNum(orderDetail.getNum());
            cartDto.setSkuId(orderDetail.getSkuId());
            cartDTOS.add(cartDto);
        });
        //同步
        goodsClient.decreaseStock(cartDTOS);


        //删除购物车中已经下单的商品数据, 采用异步mq的方式通知购物车系统删除已购买的商品，传送商品ID和用户ID
        HashMap<String, Object> map = new HashMap<>();

        Long[] skuIds = (Long[]) cartDTOS.stream().map(cartDto -> cartDto.getSkuId()).toArray(Long[]::new);
        try {
            map.put("skuIds", skuIds);
            map.put("userId", user.getId());
            amqpTemplate.convertAndSend("ego.cart.exchange", "cart.delete", map);
        } catch (Exception e) {
            log.error("删除购物车消息发送异常，商品ID：{}",skuIds, e);
        }

        log.info("生成订单，订单编号：{}，用户id：{}", orderId, user.getId());
        return orderId;

    }

    public String generateUrl(Long orderId) {
        //根据订单ID查询订单
        Order order = queryById(orderId);
        //判断订单状态
        if (!order.getOrderStatus().getStatus().equals(OrderStatusEnum.INIT.value())) {
            throw new PayException(ExceptionEnum.ORDER_STATUS_EXCEPTION);
        }

        //TODO 这里传入一份钱，用于测试使用，实际中使用订单中的实付金额
        String url = payHelper.createPayUrl(orderId, "易购商城测试", /*order.getActualPay()*/1L);
        if (StringUtils.isBlank(url)) {
            throw new PayException(ExceptionEnum.CREATE_PAY_URL_ERROR);
        }

        return url;

    }

    public Order queryById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new PayException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(queryWrapper);
        order.setOrderDetails(orderDetails);
        OrderStatus orderStatus = orderStatusMapper.selectById(orderId);
        order.setOrderStatus(orderStatus);
        return order;
    }

    @Transactional
    public void handleNotify(Map<String, String> msg) {
        payHelper.handleNotify(msg);
    }



    public void updateStatus(Long id, Integer status) {
        orderStatusMapper.updateStatus(id, status);
    }


    @Transactional
    public Long createSeckillOrder(SeckillMessage seckillMessage) {
        Order order = new Order();
        //生成订单ID，采用自己的算法生成订单ID
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);

        order.setCreateTime(new Date());
        order.setPostFee(0L);
        order.setActualPay(seckillMessage.getSeckillPrice());
        order.setTotalPay(seckillMessage.getPrice());
        order.setPaymentType(1);


        //获取用户信息
        UserInfo user = seckillMessage.getUserInfo();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);  //卖家为留言


        //保存order
        orderMapper.insert(order);

        //保存秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(orderId);
        seckillOrder.setSkuId(seckillMessage.getSkuId());
        seckillOrder.setUserId(user.getId());
        seckillOrderMapper.insert(seckillOrder);

        //保存detail
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        orderDetail.setImage(seckillMessage.getImage());
        orderDetail.setPrice(seckillMessage.getSeckillPrice());
        orderDetail.setNum(1);
        orderDetail.setSkuId(seckillMessage.getSkuId());
        orderDetail.setTitle(seckillMessage.getTitle());

        orderDetailMapper.insert(orderDetail);


        //填充orderStatus
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
        orderStatus.setCreateTime(new Date());

        //保存orderStatus
        orderStatusMapper.insert(orderStatus);

        //保存支付日志
        PayLog payLog = new PayLog();
        payLog.setStatus(PayStateEnum.NOT_PAY.getValue());
        payLog.setPayType(1);
        payLog.setOrderId(orderId);
        payLog.setTotalFee(seckillMessage.getSeckillPrice());
        payLog.setCreateTime(new Date());
        //获取用户信息
        payLog.setUserId(user.getId());

        payLogMapper.insert(payLog);
        //减库存
        CartDto cartDto= new CartDto();
        cartDto.setNum(orderDetail.getNum());
        cartDto.setSkuId(orderDetail.getSkuId());
//        goodsClient.decreaseSeckillStock(cartDto);



        log.info("生成订单，订单编号：{}，用户id：{}", orderId, user.getId());
        return orderId;
    }
}
