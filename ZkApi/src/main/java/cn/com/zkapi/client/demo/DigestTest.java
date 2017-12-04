package cn.com.zkapi.client.demo;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;

/**
 * Created by lenovo on 2017/12/1.
 * 生成签名信息
 */
public class DigestTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String str = "";
        System.out.println(DigestAuthenticationProvider.generateDigest("libo:123456"));
    }
}
