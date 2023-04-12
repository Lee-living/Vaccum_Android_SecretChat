/*

package com.lee.service;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.google.gson.Gson;
import com.lee.client.LoginActivity;
import com.lee.client.MaintestActivity;
import com.lee.domain.Date;
import com.lee.domain.Message;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;

public class MyWebSocketClient extends WebSocketClient {

    public static String[] splitUsernames = new String[5];
    Gson gson = new Gson();

    private Handler mHandler = new Handler(Looper.getMainLooper());


    private Context mContext;

    public MyWebSocketClient(String url, Context context) {
        super(URI.create(url));
        mContext = context;
    }

    public MyWebSocketClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // 连接建立时的处理逻辑
        System.out.println("链接成功");
    }

    @Override
    public void onMessage(String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 这里放置接收到消息后的处理逻辑
                // 接收到消息时的处理逻辑
                System.out.println(message);
                //反序列化一下子先
                Message msg = gson.fromJson(message, Message.class);

                switch (msg.getSign()) {
                    case 19:
                        // 执行语句1
                        Intent intent19 = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(intent19);
//                        Toast.makeText(mContext, "登录失败捏" , Toast.LENGTH_SHORT).show();
                        break;
                    case 20:
                        // 执行语句2
                        Intent intent20 = new Intent(mContext, MaintestActivity.class);
                        send(gson.toJson(new Message(1,new Date())));

                        mContext.startActivity(intent20);
//                        Toast.makeText(mContext, msg.getDate().getMsg() , Toast.LENGTH_SHORT).show();

                        //执行一个刷新maintest的列表的操作
                        break;
                    case 10:
                        String usernames = msg.getDate().getMsg();

                        // 执行语句3
                        if(usernames != null && !usernames.isEmpty()){
                            String[] splitUsernames = usernames.split(",");
                            System.out.println(splitUsernames);
                        }


                        break;
                    default:
                        // 执行默认语句

//                        Toast.makeText(mContext, "可能出错了，什么错呢？我也不知道 " , Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }).start();



    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // 连接关闭时的处理逻辑

    }

    @Override
    public void onError(Exception ex) {
        // 发生错误时的处理逻辑

    }

}

*/