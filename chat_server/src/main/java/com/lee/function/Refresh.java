package com.lee.function;
import com.lee.domain.Message;
import jakarta.websocket.Session;

import java.util.concurrent.CopyOnWriteArraySet;

public class Refresh {

    private CopyOnWriteArraySet<Session> SessionSet;

    public Refresh(CopyOnWriteArraySet<Session> SessionSet) {
        this.SessionSet = SessionSet;
    }

    public Message RefreshList(){
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
        Message responseMsg = new Message(10,null,usernames.toString(),null);
        return responseMsg;
    }

}
