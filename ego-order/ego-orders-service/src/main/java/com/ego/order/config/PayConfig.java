package com.ego.order.config;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;

import java.io.InputStream;

/**
 * @author yaorange
 * @date 2018/10/5
 */
@Data
public class PayConfig extends WXPayConfig {

    private String appID;  //公众账号ID

    private String mchID;  //商户号

    private String key;  //生成签名的密钥

    private int httpConnectTimeoutMs;   //连接超时时间

    private int httpReadTimeoutMs;  //读取超时时间

    private String notifyUrl;// 下单通知回调地址


    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
         return new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com",true);
            }
        };
    }
}
