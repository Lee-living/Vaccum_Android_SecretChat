package com.lee.service;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;

public class DHKeyExchange {
    public PublicKey publicKey;
    private PrivateKey privateKey;
    private byte[] secretKey;

    public DHKeyExchange() {
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }

    //生成密钥对
    public void generateKeyPair() {
        try {
            KeyPairGenerator kpGen = KeyPairGenerator.getInstance("DH");
            kpGen.initialize(512);
            KeyPair kp = kpGen.generateKeyPair();
            this.privateKey = kp.getPrivate();
            this.publicKey = kp.getPublic();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    //生成共享密钥
    public byte[] generateSecretKey(byte[] receivedPubKeyBytes) {
        try {
            // 从byte[]恢复PublicKey:
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(receivedPubKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("DH");
            PublicKey receivedPublicKey = kf.generatePublic(keySpec);
            // 生成本地密钥:
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(this.privateKey); // 自己的PrivateKey
            keyAgreement.doPhase(receivedPublicKey, true); // 对方的PublicKey
            // 生成SecretKey密钥:
            this.secretKey = keyAgreement.generateSecret();
            return keyAgreement.generateSecret();

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    //消息加密方法
    public static byte[] encrypt(byte[] plaintext, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        return cipher.doFinal(plaintext);
    }
    //消息解密方法
    public static byte[] decrypt(byte[] ciphertext, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        return cipher.doFinal(ciphertext);
    }
}




/*

    private KeyPairGenerator keyGen;
    private KeyAgreement keyAgree;
    private KeyPair keyPair;
    private byte[] sharedSecret;

    public DHKeyExchange() throws NoSuchAlgorithmException, InvalidKeyException {
        // 生成DH密钥对
        keyGen = KeyPairGenerator.getInstance("DH");
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();

        // 初始化KeyAgreement对象
        keyAgree = KeyAgreement.getInstance("DH");
        keyAgree.init(keyPair.getPrivate());
    }


    public byte[] generateSharedSecret(PublicKey otherPublicKey) throws InvalidKeyException {
        keyAgree.doPhase(otherPublicKey, true);
        return keyAgree.generateSecret();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey getPublicKey(String publicKeyStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            return keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }


    public static byte[] encrypt(byte[] plaintext, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        return cipher.doFinal(plaintext);
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        return cipher.doFinal(ciphertext);
    }
*/



/*    public static void main(String[] args) throws Exception {
        // 示例代码
        DHKeyExchange sender = new DHKeyExchange();
        DHKeyExchange receiver = new DHKeyExchange();


        // 假设从CA获取的公钥为caPublicKey
        sender.setReceiverPublicKey(caPublicKey);


        // 获取共享密钥
        byte[] sharedSecretSender = sender.getSharedSecret();
        byte[] sharedSecretReceiver = receiver.getSharedSecret();

        // 验证共享密钥是否相同
        if (MessageDigest.isEqual(sharedSecretSender, sharedSecretReceiver)) {
            System.out.println("共享密钥验证成功");
        }

        // 对消息进行加密
        String message = "Hello, World!";
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(sharedSecretSender, "AES");
        IvParameterSpec iv = new IvParameterSpec(sharedSecretSender, 0, 16);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

        // 对消息进行解密
        byte[] decodedMessage = Base64.getDecoder().decode(encodedMessage);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        byte[] decryptedMessage = cipher.doFinal(decodedMessage);
        System.out.println(new String(decryptedMessage));
    }
}*/

/*
    public static void main(String[] args) throws Exception {
        // 示例代码
        DHKeyExchange sender = new DHKeyExchange();
        DHKeyExchange receiver = new DHKeyExchange();

        // 发送方将自己的公钥发送给接收方
        PublicKey senderPublicKey = sender.getPublicKey();

        // 接收方将自己的公钥发送给发送方
        PublicKey receiverPublicKey = receiver.getPublicKey();

        // 发送方将接收方的公钥传入，并生成共享密钥
        sender.setReceiverPublicKey(receiverPublicKey);

        // 接收方将发送方的公钥传入，并生成共享密钥
        receiver.setReceiverPublicKey(senderPublicKey);

        // 获取共享密钥
        byte[] sharedSecretSender = sender.getSharedSecret();
        byte[] sharedSecretReceiver = receiver.getSharedSecret();

        // 验证共享密钥是否相同
        if (MessageDigest.isEqual(sharedSecretSender, sharedSecretReceiver)) {
            System.out.println("共享密钥验证成功");
        }

        // 对消息进行加密
        String message = "Hello, World!";
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(sharedSecretSender, "AES");
        IvParameterSpec iv = new IvParameterSpec(sharedSecretSender, 0, 16);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

        // 对消息进行解密
        byte[] decodedMessage = Base64.getDecoder().decode(encodedMessage);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        byte[] decryptedMessage = cipher.doFinal(decodedMessage);
        System.out.println(new String(decryptedMessage));
    }*/
