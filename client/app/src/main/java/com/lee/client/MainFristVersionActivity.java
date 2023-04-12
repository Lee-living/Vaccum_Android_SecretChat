/*
package com.lee.client;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;


*/
/*
1 刷新好友列表
2 发登陆
3 发消息


10 返回好友列表
20登陆成功 19 失败

30发成功   29 失败

31收消息

未知错误99

 *//*

public class MainFristVersionActivity extends AppCompatActivity {
    Gson gson = new Gson();

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //界面绑定
        setContentView(R.layout.activity_main);
        //登陆的跳转
        Button button = findViewById(R.id.button);
        //跳转到发消息界面
        Button runtomsg= findViewById(R.id.run_to_msg);
        //主界面测试的跳转
        Button main_test= findViewById(R.id.main_test);

        //输入栏
        TextView intext = findViewById(R.id.inText);
        //发消息
        Button send = findViewById(R.id.send);



        //发消息界面跳转 按钮的点击事件
        runtomsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFristVersionActivity.this, MsgActivity.class);
                startActivity(intent);
            }
        });

        //跳转到登陆界面按钮 的点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFristVersionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //跳转到主界面的按钮 的点击事件
        main_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFristVersionActivity.this, MaintestActivity.class);
                startActivity(intent);
            }
        });


        //主界面发消息按钮 测试 点击事件
   */
/*     send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    MyWebSocketClient client = new MyWebSocketClient("ws://10.0.2.2:8088/websocket");
                    client.connectBlocking();
                    client.senpd("Hello, WebSocket!");
                    client.closeBlocking();
                } catch (Exception e) {
                    // 处理异常
                }

            }
        });

*//*


    }

}*/
