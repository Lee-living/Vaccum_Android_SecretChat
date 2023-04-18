package com.lee.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lee.domain.Message;
import com.lee.domain.ReceiveMsg;
import com.lee.service.CA;
import com.lee.service.DHKeyExchange;
import com.lee.service.WebSocketManager;

import javax.crypto.interfaces.DHPublicKey;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MsgActivity extends AppCompatActivity {
    private List<ReceiveMsg> msgList = new ArrayList<ReceiveMsg>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    WebSocketManager client = WebSocketManager.getInstance(this);
    Gson gson = new Gson();
    //存加密消息的key
    byte[] key;
    //三个接受广播的方法
    private BroadcastReceiver receiver29;
    private BroadcastReceiver receiver30;
    private BroadcastReceiver receiver31;

    DBHelper dbHelper = new DBHelper(this);


    public MsgActivity() throws IllegalAccessException, InstantiationException {
    }

//    public byte[] getKey(){
//        return key;
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
//        initMsgs();  // 初始化测试消息数据
        inputText = (EditText) findViewById(R.id.et_input);
        send = (Button) findViewById(R.id.btn_send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.rv_chat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);

        //把 聊天对象用户名放到标题
        String friendName = getIntent().getStringExtra("friendName");
        setTitle(friendName);

        msgList = dbHelper.getFriendMessages(friendName);

        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);


        key = Base64.getDecoder().decode(dbHelper.getFriendPublicKey(friendName));



/*

        //获取对面的公钥。

        CompletableFuture<String> publicKeyFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return CA.getPublicKey(friendName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // 等待返回结果
        String publicKeyString = publicKeyFuture.join();
        //System.out.println(publicKeyString);
        //把String 的公钥base64转成byte[]方便使用
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        //用对方的公钥 和自己的私钥 生成共享密钥
        byte[] sharedSecret = LoginActivity.DHCode.generateSecretKey(publicKeyBytes);
        //再用sha256和共享密钥生成key
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        key = sha256.digest(sharedSecret);*/

        receiver29 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                inputText.setText("");
            }
        };
        // 接收到29的广播执行操作 发送失败，清空输入框
        IntentFilter filter29 = new IntentFilter("29");
        registerReceiver(receiver29, filter29);


        // 注册广播接收器
        receiver30 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = inputText.getText().toString();
//                ReceiveMsg msg = new ReceiveMsg(message, false);
                dbHelper.insertMessage(friendName,2,message);

                msgList.clear(); // 清空msgList
                msgList.addAll(dbHelper.getFriendMessages(friendName)); // 添加新的聊天记录
//                msgList.add(msg);
// 通知adapter数据已改变
                adapter.notifyDataSetChanged();
// 定位到最后一行
                msgRecyclerView.scrollToPosition(msgList.size() - 1);

//                dbHelper.clearMessages("_user");

                inputText.setText("");
            }
        };
        // 接收到30的广播执行操作   清空输入框
        IntentFilter filter30 = new IntentFilter("30");
        registerReceiver(receiver30, filter30);


        receiver31 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = inputText.getText().toString();
//                ReceiveMsg msg = new ReceiveMsg(message, false);

                msgList.clear(); // 清空msgList
                msgList.addAll(dbHelper.getFriendMessages(friendName)); // 添加新的聊天记录
// 通知adapter数据已改变
                adapter.notifyDataSetChanged();
// 定位到最后一行
                msgRecyclerView.scrollToPosition(msgList.size() - 1);

//                dbHelper.clearMessages("_user");

            }
        };
        // 接收到31的广播执行操作   清空输入框
        IntentFilter filter31 = new IntentFilter("31");
        registerReceiver(receiver31, filter31);



    /*    receiver31 = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onReceive(Context context, Intent intent) {

                String message = intent.getStringExtra("message");
                //解密消息
                byte[] secretMsg31 = Base64.getDecoder().decode(message);
                byte[] messageBytes31 =  new byte[0];
                try {
                    System.out.println(secretMsg31);
                    System.out.println(key);
                    messageBytes31 = DHKeyExchange.decrypt(secretMsg31,key);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                String message31 = new String(messageBytes31, StandardCharsets.UTF_8);
                ReceiveMsg msg = new ReceiveMsg(message31, true);
                msgList.add(msg);
                System.out.println(msgList);
                // 当有新消息时，刷新RecyclerView中的显示
                adapter.notifyItemInserted(msgList.size() - 1);
                // 将RecyclerView定位到最后一行
                msgRecyclerView.scrollToPosition(msgList.size() - 1);
            }
        };
        // 接收到31的广播执行操作   解密消息  刷新对话列表
        IntentFilter filter31 = new IntentFilter("31");
        registerReceiver(receiver31, filter31);*/
        //发消息的点击事件
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    // 对消息进行加密
                    byte[] plaintext = content.getBytes(StandardCharsets.UTF_8);
                    byte[] ciphertext = new byte[0];
                    try {
                        ciphertext = DHKeyExchange.encrypt(plaintext, key);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
//                    System.out.println(ciphertext);
                    Message msg = new Message(3,friendName,null, ciphertext);
                    client.sendMessage(gson.toJson(msg));

//                    msgList = dbHelper.getFriendMessages("_user");
//                    System.out.println(msgList);
//                    adapter.notifyItemInserted(msgList.size() - 1);
//                    // 将RecyclerView定位到最后一行
//                    msgRecyclerView.scrollToPosition(msgList.size() - 1);


                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        if (receiver30 != null) {
            unregisterReceiver(receiver30);
            receiver30 = null;
        }
    }

}

//    private void initMsgs() {
//        Message msg1 = new Message("Hello guy.", false);
//        msgList.add(msg1);
//        Message msg2 = new Message("Hello. Who are you?", true);
//        msgList.add(msg2);
//        Message msg3 = new Message("This is Tom. Nice talking to you. ", true);
//        msgList.add(msg3);
//    }

  /*     // 从字节数组中重构公钥对象
        System.out.println(publicKeyBytes);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }*/


 /*  PublicKey publicKey = null;
        try {
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        System.out.println(publicKey);
        System.out.println(publicKey);
        */

/*
        byte[] sharedSecret;
        try {
            sharedSecret = DHCode.generateSharedSecret(publicKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        System.out.println(sharedSecret);
        */


  /*      msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);  // 当有新消息时，刷新RecyclerView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);  // 将RecyclerView定位到最后一行
                    inputText.setText("");
                    */

//页面的加载 也等返回值回
                 /*   adapter = new MsgAdapter(msgList);
                    msgRecyclerView.setAdapter(adapter);

                    */


     /*
        // 对消息进行加密
        byte[] plaintext = "Hello, world!".getBytes(StandardCharsets.UTF_8);
        byte[] ciphertext = new byte[0];
        try {
            ciphertext = DHKeyExchange.encrypt(plaintext, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(ciphertext);
        */

        /*
        // 对消息进行解密
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        byte[] decryptedText = new byte[0];
        try {
            decryptedText = DHKeyExchange.decrypt(ciphertext, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String message = new String(decryptedText, StandardCharsets.UTF_8);
        System.out.println(message);

*/
     /*   PublicKey receiverPublicKey;
        try {
            receiverPublicKey = future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(receiverPublicKey);
        // 使用共享密钥进行加密/解密等操作
        */


   /*    okhttp发送
                            try {
                                SendMsg.send(msg);

                                //接受返回消息再拆出来状态码判断发送成功没
                                //成功 刷新自己发送消息的对话框，失败返回原因

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MsgActivity.this, "在发", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (Exception e) {
                                //抛网路异常
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MsgActivity.this, "网络连接问题", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }*/


                    /*
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MyWebSocketClient client = new MyWebSocketClient("ws://10.0.2.2:8088/websocket");
                                client.connectBlocking();
                                client.send(stringMsg);
                                client.closeBlocking();
                            } catch (Exception e) {
                                // 处理异常
                            }
                        }
                    }).start();

*/