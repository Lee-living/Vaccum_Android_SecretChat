package com.lee.function;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.domain.Message;
import jakarta.websocket.Session;

import java.util.concurrent.CopyOnWriteArraySet;

public class FriendList {
    ObjectMapper objectMapper = new ObjectMapper();
    private CopyOnWriteArraySet<Session> SessionSet;

    public FriendList(CopyOnWriteArraySet<Session> SessionSet) {
        this.SessionSet = SessionSet;
    }

    public String RefreshList() throws JsonProcessingException {
        //新建一个usernames 存用户数据
        StringBuffer usernames = new StringBuffer();

        //遍历所有连接的Session对象 获取每个session的username
        for (Session session : SessionSet) {
            // 获取当前连接的username
            String username = (String) session.getUserProperties().get("username");
            //拼接到usernames上
            usernames.append(username + ",");
        }
        //删掉最后一个逗号
        usernames.deleteCharAt(usernames.length() - 1);
        //返回好友列表消息  10
        Message refreshListMsg = new Message(10,null,usernames.toString(),null);

        String refreshListjson = objectMapper.writeValueAsString(refreshListMsg);

        return refreshListjson;
    }

}
