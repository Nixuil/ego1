package com.ego.auth.untils;




import com.ego.auth.entity.UserInfo;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;

public class JwtTest {

    private final static String pubKeyPath = "E:\\tmp\\rsa\\rsa.pub";

    private final static String priKeyPath = "E:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "34234234983458dfkgjdfg");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        String token = JwtUtils.generateToken(new UserInfo(20L, "bzy"), privateKey, 5);
        System.out.println(token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiYnp5IiwiZXhwIjoxNjAyOTIzODkwfQ.Cwux4TEfCssavsK0lQ_o0LSTgbeYSI0xdi5sF9LljxEMOrpO6UHE3tjHhIpb_qkuRnoUSufuGnSLukO2ahd3wwh3hJb-i240-re-UUR35JQ0BovcXAAjbwAOvFo1xnZXpNZxJhbor2ogmehwkF8QcWfREnh-OSGMfXbBVATBMl4";
        UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println(infoFromToken.getId());
        System.out.println(infoFromToken.getUsername());
    }
}
