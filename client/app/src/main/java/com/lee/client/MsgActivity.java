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
                //ReceiveMsg msg = new ReceiveMsg(message, false);
                //发送成功消息放进数据库
                dbHelper.insertMessage(friendName,2,message);

                msgList.clear(); // 清空msgList
                msgList.addAll(dbHelper.getFriendMessages(friendName)); // 添加新的聊天记录
                //msgList.add(msg);
                // 通知adapter数据已改变
                adapter.notifyDataSetChanged();
                // 定位到最后一行
                msgRecyclerView.scrollToPosition(msgList.size() - 1);

                //dbHelper.clearMessages("_user");
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
                //ReceiveMsg msg = new ReceiveMsg(message, false);
                msgList.clear(); // 清空msgList
                msgList.addAll(dbHelper.getFriendMessages(friendName)); // 添加新的聊天记录
                // 通知adapter数据已改变
                adapter.notifyDataSetChanged();
                // 定位到最后一行
                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                //dbHelper.clearMessages("_user");

            }
        };
        // 接收到31的广播执行操作   清空输入框
        IntentFilter filter31 = new IntentFilter("31");
        registerReceiver(receiver31, filter31);


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


