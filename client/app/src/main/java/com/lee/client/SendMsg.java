/*
package com.lee.client;


import com.google.gson.Gson;
import com.lee.domain.Message;
import okhttp3.*;

import java.io.IOException;

//okhttp用的
class SendMsg {

        public static void send(Message msg) throws IOException {
                //设置json 接受中文
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                //连服务器
                OkHttpClient client = new OkHttpClient();
                //用gson把msg对象转换成json文件，方便服务器反序列化获取到对象
                Gson gson = new Gson();
                //对象msg转成json
                String json = gson.toJson(msg);
                //要发送的body
                RequestBody body = RequestBody.create(JSON, json);

                Request reqs = new Request.Builder()
                        //地址--后改服务器--test最好也改
                        .url("http://10.0.2.2:8089/test/test")
                        //老方法发字符串的，可以不创建body
                        //.post(RequestBody.create(MediaType.parse("text/plain"), String.valueOf(msg)))
                        .post(body)
                        .build();
                //res接收
                Response res = client.newCall(reqs).execute();
        }
}
*/
