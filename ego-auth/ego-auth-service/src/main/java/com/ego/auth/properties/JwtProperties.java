package com.ego.auth.properties;

import com.ego.auth.entity.UserInfo;
import com.ego.auth.untils.JwtUtils;
import com.ego.auth.untils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 **/
@Slf4j
@Data
@ConfigurationProperties(prefix = "ego.jwt")
public class JwtProperties {
    private String pubKeyPath;
    private String priKeyPath;
    private String cookieName;
    private String security;
    private Integer expri;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            File pubKeyFile = new File(pubKeyPath);
            File priKeyFile = new File(priKeyPath);
            if (!priKeyFile.exists() || !pubKeyFile.exists()) {
                RsaUtils.generateKey(pubKeyPath, priKeyPath, security);
            }
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化私钥失败",e);
            e.printStackTrace();
        }
    }
}
