package com.ego.sms.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.ego.sms.Property.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 **/
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsService {

    @Resource
    private SmsProperties smsProperties;

    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    public void sendCode(String phone, String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(domain);
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        //必填:待发送手机号
        request.putQueryParameter("PhoneNumbers",phone );
        //必填:短信签名-可在短信控制台中找到
        request.putQueryParameter("SignName", smsProperties.getSignName());
        //必填:短信模板-可在短信控制台中找到
        request.putQueryParameter("TemplateCode", smsProperties.getVerifyCodeTemplate());
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("发送短信状态：{}", response.getHttpStatus());
            log.info("发送短信消息：{}", response.getData());
        } catch (Exception e) {
            log.error("发送验证码短信失败.",e);
        }
    }
}
