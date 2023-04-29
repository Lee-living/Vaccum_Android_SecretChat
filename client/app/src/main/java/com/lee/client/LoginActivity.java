package com.lee.client;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.gson.Gson;
import com.lee.domain.Message;
import com.lee.service.DHKeyExchange;
import com.lee.service.WebSocketManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewStatus;
    private String username;
    private String password;
    //创建一个DHCode
    public static DHKeyExchange DHCode = new DHKeyExchange();

    //用gson 序列化反序列化用
    Gson gson = new Gson();


    public LoginActivity() throws NoSuchAlgorithmException, InvalidKeyException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewStatus = findViewById(R.id.textViewStatus);

        //创建连接服务器 的Wessocket 的 Client
        WebSocketManager client;

        try {
            client = WebSocketManager.getInstance(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        //连服务器
        client.connect();

        //创建DHCode 的公私钥匙
        DHCode.generateKeyPair();
//        System.out.println(DHCode);
//        System.out.println(DHCode.getPublicKey());

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            //发送请求到服务器处理，服务器返回结果再返回到界面。
            public void onClick(View v) {
                //获取输入框
                username = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                //测试是否为空
                if (isEmpty(username) || isEmpty(password)) {
                    textViewStatus.setText("别输入空的嗷");
//                } else if (isEmpty(password)) {
//                    textViewStatus.setText("妹输密码捏");
                } else {
                    //整个跑的方法可以抽出来，msg先创建好呗。
                    //抽出来成了 SendMsg.send(msg);
                    //构造Message对象 向服务器发消息
                    Message msg = new Message(2,username,password,null);
                    //序列化msg
                    String stringMsg = gson.toJson(msg);
                    //线程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //发消息给服务器
                                client.sendMessage(stringMsg);
                                System.out.println(stringMsg);
                            } catch (Exception e) {
                                // 处理异常
                            }
                        }
                    }).start();
                }
            }
        });
    }
}


