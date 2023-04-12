package com.lee.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lee.client.LoginActivity;
import com.lee.client.MaintestActivity;
import com.lee.client.MsgActivity;
import com.lee.domain.Message;

import com.lee.domain.ReceiveMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.Executors.*;

public class WebSocketManager {
//    Gson gson1 = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    Gson gson = new Gson();
    public static String[] splitUsernames = new String[5];
    public String[] get(){
        return splitUsernames;
    }
    private static Context mContext;
    private static final int THREAD_POOL_SIZE = 5; // 线程池大小
    private static ExecutorService executorService; // 线程池
    private static WebSocketManager instance;
    private WebSocketClient websocket;

    private WebSocketManager() throws IllegalAccessException, InstantiationException {
        // 初始化线程池
        executorService = newFixedThreadPool(THREAD_POOL_SIZE);
}

    public static synchronized WebSocketManager getInstance(Context context) throws IllegalAccessException, InstantiationException {
        mContext = context;
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }
    //连接websocket服务器
    public void connect( ) {
        // 连接到 WebSocket 服务器并设置回调
        URI uri = URI.create("ws://10.0.2.2:8088/websocket");
        websocket = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("WebSocket", "Connected");
            }

            @Override
            public void onMessage(String message) {
                // 接受到服务器发来的消息 提交任务到线程池中处理
                executorService.submit(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        // 这里放置接收到消息后的处理逻辑
                        // 接收到消息时的处理逻辑
//                         System.out.println(message);
                        //反序列化一下子先
                        ReceiveMessage msg = gson.fromJson(message, ReceiveMessage.class);
//                        System.out.println(msg);
                        //还是根据sign
                        switch (msg.getSign()) {
                            //登录失败
                            case 19:
                                //返回login界面，删掉连接重连
                                Intent intent19 = new Intent(mContext, LoginActivity.class);
                                mContext.startActivity(intent19);
                                if (mContext instanceof Activity) {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, msg.getMsg(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                break;
                                //登录成功
                            case 20:
                                //跳转到好友列表界面
                                // 创建一个Intent，用于启动登录Activity
                                Intent intent20 = new Intent(mContext, MaintestActivity.class);
                                intent20.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                // 创建一个TaskStackBuilder
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                                // 将登录Activity添加到TaskStackBuilder中
                                stackBuilder.addNextIntentWithParentStack(intent20);
                                // 获取TaskStackBuilder的PendingIntent
                                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                // 发送PendingIntent，以启动登录Activity并清除任务栈
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    throw new RuntimeException(e);
                                }
                                send(gson.toJson(new Message(1,null,null,null)));
                                mContext.startActivity(intent20);
                                if (mContext instanceof Activity) {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, msg.getMsg() , Toast.LENGTH_SHORT).show();                                        }
                                    });
                                }
                                //登录成功就把 公钥注册到 CA服务器
                                try {
//                                    System.out.println(LoginActivity.DHCode);
//                                    System.out.println( LoginActivity.DHCode.getPublicKey());
                                    CA.registerPublicKey(msg.getUser(), LoginActivity.DHCode.getPublicKey());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                                //接受在线好友列表
                            case 10:
                                //是string 的，切一下 装到 splitUsernames
                                if(msg.getMsg() != null && !msg.getMsg().isEmpty()){
                                    String usernames = msg.getMsg();
                                    splitUsernames = usernames.split(",");
                                    System.out.println(splitUsernames);
                                }
//                                Intent intent21 = new Intent(mContext, MaintestActivity.class);
//                                mContext.startActivity(intent21);
                                break;
                                //发消息失败
                            case 29:
                                if (mContext instanceof Activity) {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, "发送失败，你刷新列表试试？" , Toast.LENGTH_SHORT).show();                                        }
                                    });
                                }
                                //发广播消息到Msg界面，通知发送方
                                Intent intent29 = new Intent("29");
                                mContext.sendBroadcast(intent29);
                                break;
                                //发送消息成功的回执
                            case 30:
                                //发广播 通知消息发送成功 刷新消息界面
                                String secretMsg30 = msg.getSecretMsg();
                                Intent intent30 = new Intent("30");
                                intent30.putExtra("message", secretMsg30);
                                mContext.sendBroadcast(intent30);

                                if (mContext instanceof Activity) {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, "发送成功捏" , Toast.LENGTH_SHORT).show();                                        }
                                    });
                                }
                                break;
                                //收到别人发来消息
                            case 31:
                                String secretMsg31 = msg.getSecretMsg();
                                //发广播消息通知收到消息了
                                Intent intent31 = new Intent("31");
                                intent31.putExtra("message", secretMsg31);
                                mContext.sendBroadcast(intent31);

                                if (mContext instanceof Activity) {
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, "收到新消息捏" , Toast.LENGTH_SHORT).show();                                        }
                                    });
                                }
                                break;
                            default:
                                // 执行默认语句
//                        Toast.makeText(mContext, "可能出错了，什么错呢？我也不知道 " , Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("WebSocket", "Connection closed");
            }

            @Override
            public void onError(Exception ex) {
                Log.e("WebSocket", "Error occurred: " + ex.getMessage());
            }
        };
        websocket.connect();

    }
    //我的android项目，在login.java中完成里websocket的连接，我要怎么在 friendList.java中调用这个websocket连接或者websocket的send方法

    public void disconnect() {
        if (websocket != null) {
            websocket.close();
        }
    }

    public void sendMessage(String message) {
        if (websocket != null && websocket.isOpen()) {
            websocket.send(message);
        } else {
            Log.e("WebSocket", "Not connected or connection closed");
        }
    }

}




    /*            new Thread(new Runnable() {
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

                                // 执行语句3
                                if(msg.getDate().getMsg() != null && !msg.getDate().getMsg().isEmpty()){
                                    String usernames = msg.getDate().getMsg();
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

     */

