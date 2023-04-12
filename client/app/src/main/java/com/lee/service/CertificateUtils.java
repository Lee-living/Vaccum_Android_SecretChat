/*
package com.lee.service;

import android.os.Build;
import android.util.Base64;
import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;

public class CertificateUtils {

    */
/**
     * 根据公钥字符串获取公钥
     *
     * @param publicKeyStr 公钥字符串
     * @return 公钥
     *//*


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey getPublicKey(String base64PublicKey) throws Exception {
        byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        return keyFactory.generatePublic(x509KeySpec);
    }

    */
/**
     * 根据公钥获取公钥字符串
     *
     * @param publicKey 公钥
     * @return 公钥字符串
     *//*

    public static String getPublicKeyString(PublicKey publicKey) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        String publicKeyString = Base64.encodeToString(keySpec.getEncoded(), Base64.DEFAULT);
        return publicKeyString;
    }

    */
/**
     * 从证书字符串中获取公钥
     *
     * @param certificateStr 证书字符串
     * @return 公钥
     *//*

    public static PublicKey getPublicKeyFromCertificate(String certificateStr) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream is = new ByteArrayInputStream(certificateStr.getBytes());
        X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
        return cert.getPublicKey();
    }

}
*/
