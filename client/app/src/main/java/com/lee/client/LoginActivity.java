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

//    public DHKeyExchange getDHCode(){
//        return DHCode;
//    }

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




      /*                    okhttp   发送
                            try{
                                //send
                                SendMsg.send(msg);

                                //接受返回消息再拆出来状态码判断发送成功没
                                    //成功 跳转，没成功返回 为什么  的通知

                                //发送后的界面返回---要结合 接受返回值判断

                                //跳转主界面，还没改
                                textViewStatus.setText("Login successful.");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this,"发送成功",Toast.LENGTH_LONG).show();
                                    }
                                });

                                }catch (Exception e){
                                //抛网路异常
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this,"网络连接问题",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
*/




/*
*
*  new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                                //连服务器
                                OkHttpClient client = new OkHttpClient();

                                Gson gson = new Gson();
                                String json = gson.toJson(msg);

                                RequestBody body = RequestBody.create(JSON, json);


                                Request reqs = new Request.Builder()
        //                                        .url("http://192.168.0.100:8089/test/test")
                                        .url("http://10.0.2.2:8089/test/test")
                                        //.post(RequestBody.create(MediaType.parse("text/plain"), String.valueOf(msg)))
                                        .post(body)
                                        .build();

                                //res接收
                                Response res = client.newCall(reqs).execute();

                                // outtext.setText(res.body().toString());
                                textViewStatus.setText("Login successful.");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this,"发送成功",Toast.LENGTH_LONG).show();
                                    }
                                });

                                }catch (Exception e){
                                //抛网路异常
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this,"网络连接问题",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                }

            }).start();


*
* */