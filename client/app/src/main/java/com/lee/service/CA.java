package com.lee.service;

import okhttp3.*;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

//// 发送公钥到CA服务器
//      CA.registerPublicKey(username, publicKey);
//
//// 在需要获取其他用户公钥的地方调用requestPublicKey方法
//        CA.requestPublicKey(username);
//

public class CA {
        private static final OkHttpClient client = new OkHttpClient();
//        private static final String baseUrl = "ws://10.0.2.2:8089";

    //注册公钥到 CA服务器
    public static void registerPublicKey(String username, PublicKey publicKey) throws IOException {
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("publicKeyStr", publicKeyStr)
                .build();
        System.out.println(body.toString());
        Request request = new Request.Builder()
                .url("ws://47.113.145.152:8089/ca/registerPublicKey")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
    }

    //获取用户的公钥
    public static String getPublicKey(String username) throws IOException {

        Request request = new Request.Builder()
                .url("ws://47.113.145.152:8089/ca/getPublicKey?username=" + username)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }

    }
}

