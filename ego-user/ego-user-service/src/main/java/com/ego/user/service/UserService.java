package com.ego.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ego.common.enums.ExceptionEnum;
import com.ego.common.exception.EgoException;
import com.ego.common.util.CodecUtils;
import com.ego.common.util.NumberUtils;
import com.ego.user.mapper.UserMapper;
import com.ego.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 **/
@Service
@Slf4j
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    static final String SMS_KEY_PRE = "user:code:phone:";


    /**
     * 检查数据是否合法
     *
     * @param data 用户名或手机号
     * @param type 1.用户名校验  2.手机号校验
     * @return 是否合法
     */
    @Transactional(readOnly = true)
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        String column = null;
        if (type == 1) {
            column = "username";
        } else if (type == 2) {
            column = "phone";
        } else {
            column = "username";
        }
        userQueryWrapper.eq(column, data);
        Integer integer = userMapper.selectCount(userQueryWrapper);

        return integer > 0 ? false : true;
    }

    /**
     * 进行必要的数据校验，将数据写入数据库
     * @param user  用户数据
     * @param code  验证码
     * @return  数据是否合法
     */
    @Transactional
    public Boolean register(User user, String code) {
        String redisCode = (String) stringRedisTemplate.opsForHash().get("egocode",user.getPhone());

        if (StringUtils.isBlank(code) || !code.equals(redisCode)){
            return false;
        }
        //校验username
        //校验phone
        if (!this.checkData(user.getUsername(),1) || !this.checkData(user.getPhone(),2)){
            return false;
        }
        //写入数据库
        user.setCreated(new Date());
        user.setPassword(CodecUtils.passwordBcryptEncode(user.getUsername(),user.getPassword()));
        userMapper.insert(user);
        //删除redis验证码
        stringRedisTemplate.opsForHash().delete("egocode",user.getPhone());
        return true;
    }

    /**
     * 异步发送短信
     * @param phone 手机号
     */
    public Boolean sendVerifyCode(String phone) {
        //1.生成验证码
        String code = NumberUtils.generateCode(6);
        try
        {
            //2.将验证码存入redis（5分钟）
            stringRedisTemplate.opsForValue().set(SMS_KEY_PRE+phone, code,5, TimeUnit.MINUTES);
            stringRedisTemplate.opsForHash().put("egocode",phone,code);
            //3.发消息到rabbitmq
            amqpTemplate.convertAndSend("exchange.sms.send","sms.send",phone+"&&&"+code);
            return true;
        }catch (Exception e){
            log.error("发送短信失败。phone：{}， code：{}", phone, code);
            throw new EgoException(ExceptionEnum.SMS_SEND_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public User login(String username, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user==null){
            log.error("用户不存在",username);
            return null;
        }
        if (!CodecUtils.passwordConfirm(username+password,user.getPassword())){
            log.error("密码错误");
            return null;
        }
        return user;
    }
}
