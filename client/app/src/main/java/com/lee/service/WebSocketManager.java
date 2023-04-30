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
import com.lee.client.DBHelper;
import com.lee.client.LoginActivity;
import com.lee.client.MaintestActivity;
import com.lee.domain.Message;

import com.lee.domain.ReceiveMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.*;

public class WebSocketManager {
//    Gson gson1 = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    private static final int HEARTBEAT_INTERVAL = 5000; // 心跳间隔时间

    ScheduledExecutorService mExecutor;
    ScheduledFuture<?> mFuture;


    Gson gson = new Gson();
    public static String[] splitUsernames = new String[5];
    public String[] get(){
        return splitUsernames;
    }
    private static Context mContext;
//    WebSocketManager client = WebSocketManager.getInstance(mContext);
    private static final int THREAD_POOL_SIZE = 5; // 线程池大小
    private static ExecutorService executorService; // 线程池
    private static WebSocketManager instance;
    private WebSocketClient websocket;
    DBHelper dbHelper = new DBHelper(mContext);


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
        URI uri = URI.create("ws:///websocket");
        websocket = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("WebSocket", "Connected");

                startHeartbeat();
            }

            @Override
            public void onMessage(String message) {


                if ("heartbeat response".equals(message)) {
                    // 如果收到心跳消息，则直接回复心跳响应消息
                    return;
                }


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

                                    CA.registerPublicKey(msg.getUser(), LoginActivity.DHCode.getPublicKey());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                //System.out.println(LoginActivity.DHCode);
                                //System.out.println( LoginActivity.DHCode.getPublicKey());
                                break;
                                //接受在线好友列表
                            case 10:
                                //是string 的，切一下 装到 splitUsernames
                                if(msg.getMsg() != null && !msg.getMsg().isEmpty()){
                                    String usernames = msg.getMsg();
                                    splitUsernames = usernames.split(",");
                                    System.out.println(splitUsernames);
                                }
                                //获取在线所有好友的公钥生成共享密钥放到数据库，创建每个好友聊天记录的数据库
                                for (String splitUsername : splitUsernames) {
                                    dbHelper.createFriendMessagesTable(splitUsername);
                                    CompletableFuture<String> publicKeyFuture = CompletableFuture.supplyAsync(() -> {
                                        try {
                                            return CA.getPublicKey(splitUsername);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                    // 等待返回结果
                                    String publicKeyString = publicKeyFuture.join();
                                    System.out.println(publicKeyString);
                                    //把String 的公钥base64转成byte[]方便使用
                                    byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
                                    //用对方的公钥 和自己的私钥 生成共享密钥
                                    byte[] sharedSecret = LoginActivity.DHCode.generateSecretKey(publicKeyBytes);
                                    //再用sha256和共享密钥生成sharedSecret
                                    MessageDigest sha256;
                                    try {
                                        sha256 = MessageDigest.getInstance("SHA-256");
                                    } catch (NoSuchAlgorithmException e) {
                                        throw new RuntimeException(e);
                                    }
                                    byte[] key = sha256.digest(sharedSecret);
                                    dbHelper.insertOrUpdateFriendKey(splitUsername, Base64.getEncoder().encodeToString(key));

//                                    System.out.println("存的公钥key" + key);
//                                    System.out.println(Base64.getEncoder().encodeToString(key));
//                                    final String friendPublicKey = dbHelper.getFriendPublicKey(splitUsername);
//                                    System.out.println(friendPublicKey);

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
//                                String secretMsg30 = msg.getSecretMsg();
                                Intent intent30 = new Intent("30");
//                                intent30.putExtra("message", secretMsg30);
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

                                byte[] shareKeyBytes = Base64.getDecoder().decode(dbHelper.getFriendPublicKey(msg.getUser()));
                                System.out.println("获取到的"+ shareKeyBytes);
                                byte[] secretMsg = Base64.getDecoder().decode(msg.getSecretMsg());
                                byte[] secretDecryptMsg = new byte[0];
                                try {
                                    secretDecryptMsg = DHKeyExchange.decrypt(secretMsg,shareKeyBytes);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                String message = new String(secretDecryptMsg, StandardCharsets.UTF_8);
                                dbHelper.insertMessage(msg.getUser(),1,message);


//                                String secretMsg31 = msg.getSecretMsg();
                                //发广播消息通知收到消息了
                                Intent intent31 = new Intent("31");
//                                intent31.putExtra("message", secretMsg31);
//                                byte[] secretMsgtest = Base64.getDecoder().decode(msg.getSecretMsg());
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
                stopHeartbeat();
            }

            @Override
            public void onError(Exception ex) {
                Log.e("WebSocket", "Error occurred: " + ex.getMessage());
                stopHeartbeat();
            }

            // 构建WebSocket握手请求


        };
        websocket.connect();

    }



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

    // 在连接成功后启动定时器发送心跳包
    private void startHeartbeat() {
        mExecutor = Executors.newSingleThreadScheduledExecutor();
        mFuture = mExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (websocket != null && websocket.isOpen()) {
                    websocket.send("heartbeat"); // 发送心跳包
                }
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    // 在连接断开后停止心跳定时器
    private void stopHeartbeat() {
        if (mFuture != null) {
            mFuture.cancel(true);
            mFuture = null;
        }
        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }
    }

}
