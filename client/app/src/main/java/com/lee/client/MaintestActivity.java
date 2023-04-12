package com.lee.client;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.gson.Gson;
import com.lee.domain.Message;

import com.lee.service.WebSocketManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//好友列表界面
public class MaintestActivity extends AppCompatActivity {
    private ListView friendsList;
    private ImageButton refreshButton;

    private TextView textView;
    //在线好友列表
    List<String> friends = new ArrayList<>();
    WebSocketManager client = WebSocketManager.getInstance(this);
    Gson gson = new Gson();

    public MaintestActivity() throws IllegalAccessException, InstantiationException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintest);
        // 初始化控件
        friendsList = findViewById(R.id.friends_list);
        refreshButton = findViewById(R.id.refresh_button);

        //把用户列表放到friends
        if(client.get() != null && !client.get().equals(null)){
            for (int i = 0; i < client.get().length; i++) {
                if(client.get()[i] != null ){
                    friends.add(client.get()[i]);
                }else {
                    break;
                }
            }
        }

        //好友列表适配器 friends放进去
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friends);
        friendsList.setAdapter(adapter);

        // 设置刷新按钮点击事件
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 刷新好友列表数据
                //发请求 1
                String stringMsg = gson.toJson(new Message(1,null,null,null));
                client.sendMessage(stringMsg);
                //清空适配器
                adapter.clear();
                //获取在线人 放到friends
                if(client.get() != null && !client.get().equals(null)){
//                    friends.clear();
                    for (int i = 0; i < client.get().length; i++) {
                        if(client.get()[i] != null ){
                            friends.add(client.get()[i]);
                        }else {
                            break;
                        }
                    }
                }
//                    adapter.addAll(friends);
//                    friendsList.setAdapter(adapter);
                //刷新适配器
                    adapter.notifyDataSetChanged();
            }
        });

        //单击好友列表的 的跳转事件
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取要聊天的 好友名
                String selectedFriend = friends.get(position);
                Intent intent = new Intent(MaintestActivity.this, MsgActivity.class);
                intent.putExtra("friendName", selectedFriend);
                startActivity(intent);
            }
        });



    }
}
