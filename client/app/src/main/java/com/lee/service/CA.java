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
        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8089/ca/registerPublicKey")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
    }

    //获取用户的公钥
    public static String getPublicKey(String username) throws IOException {

        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8089/ca/getPublicKey?username=" + username)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }

    }
}




    /*
    public static void registerPublicKey(String username, PublicKey publicKey) throws IOException {

            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());

            *//*RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("publicKeyStr", publicKeyStr)
                    .build();*//*

            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/registerPublicKey").newBuilder();
            urlBuilder.addQueryParameter("username", username);
            urlBuilder.addQueryParameter("publicKeyStr", publicKeyStr);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()

//                    .url(baseUrl + "/registerPublicKey")
                    .url(url)
                    .post(null)
//                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " + response);
            }
        }
*/
       /* public static String getPublicKey(String username) throws IOException {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/getPublicKey").newBuilder();
            urlBuilder.addQueryParameter("username", username);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " + response);
            }

//            Response response = client.newCall(request).execute();
            String publicKeyString = response.body().string();
            System.out.println(publicKeyString);

            return publicKeyString;
        }
    }*/



/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void registerPublicKey(String username, PublicKey publicKey) throws Exception {
//        String publicKeyString = CertificateUtils.getPublicKeyString(publicKey);
        OkHttpClient client = new OkHttpClient();
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        // 构建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("publicKeyStr", publicKeyStr)
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8089/ca/registerPublicKey")

                .post(requestBody)

                .build();

        // 发送请求并处理响应
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String message = response.body().string();
//                Log.d(TAG, "registerPublicKey response: " + message);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getPublicKey(String username) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8089/ca/getPublicKey?username=" + username)
                .build();

        Response response = client.newCall(request).execute();
        String publicKeyString = response.body().string();
        System.out.println(publicKeyString);

//        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
//            // 从字节数组中重构公钥对象
//        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
//        System.out.println(publicKey);
        return publicKeyString;
    }
*/



/*

    public static void requestPublicKey(String username, Consumer<PublicKey> callback) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求
        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8089/ca/getPublicKey?username=" + username)
                .build();

        // 发送请求并处理响应
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String publicKeyString = response.body().string();
                try {
                    PublicKey publicKey = CertificateUtils.getPublicKey(publicKeyString);
                    callback.accept(publicKey); // 请求成功后调用回调函数
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Callback function executed successfully!");
            }
        });
    }
*/

  /*  public static PublicKey requestPublicKeySync(String username) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // 构建请求
        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8089/ca/getPublicKey?username=" + username)
                .build();

        // 发送请求并处理响应
        Response response = client.newCall(request).execute();
        String publicKeyString = response.body().string();
        PublicKey publicKey = CertificateUtils.getPublicKey(publicKeyString);
        return publicKey;
    }*/

  /*  public static PublicKey requestPublicKey(String username) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求
        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8089/ca/getPublicKey?username=" + username)
                .build();

        // 发送请求并处理响应
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String publicKeyString = response.body().string();
                try {
                    PublicKey publicKey = CertificateUtils.getPublicKey(publicKeyString);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
//                Log.d(TAG, "requestPublicKey response: " + publicKey);
            }
        });
        return null;
    }*/
