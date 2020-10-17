package com.ego.order.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.util.Date;

@Data
@TableName("tb_pay_log")
public class PayLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long orderId;
    private Long totalFee;
    private Long userId;
    private String transactionId;
    private Integer status;
    private Integer payType;//1.微信支付  2.货到付款
    private String bankType;
    private Date createTime;
    private Date payTime;
    private Date refundTime;
    private Date closedTime;
}
