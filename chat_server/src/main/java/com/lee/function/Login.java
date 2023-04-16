package com.lee.function;

import com.lee.domain.Message;
import jakarta.websocket.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

public class Login{
    // 模拟数据库中的用户信息,存账号密码
    private static final Map<String, String> userMap = new HashMap<>();

    static {
        userMap.put("1", "123");
        userMap.put("2", "123");
        userMap.put("3", "123");
        userMap.put("4", "123");
        userMap.put("5", "123");
    }


    private CopyOnWriteArraySet<Session> SessionSet;

    private Message msg;

    //构造函数，传入msg和sessionSet，用于验证，和遍历session，判断是否有重复登陆
    public Login(Message msg,CopyOnWriteArraySet<Session> SessionSet) {
        this.msg = msg;
        this.SessionSet = SessionSet;
    }

    public Message proveLogin(){
        //从构造方法传入的消息获取账号密码
        String username = msg.getUser();
        String password = msg.getMsg();

        //判断账号密码匹配
        if (userMap.containsKey(username) && userMap.get(username).equals(password)) {
            // 用户名和密码匹配，验证通过
            //遍历SessionSet判断是否重复登陆
            for (Session session : SessionSet) {
                //把当前session的username放到sendname
                String sendname = (String) session.getUserProperties().get("username");
                //判断登陆用户是否已在其他地方登陆
                if(username.equals(sendname)){
                    //已登陆返回 19
                    Message loginResponseMsg = new Message(19,null,"账号已在其他地方登陆",null);
                    return loginResponseMsg;
                }
            }
            //通过遍历SessionSet后 返回20
            Message loginResponseMsg = new Message(20,msg.getUser(),"登陆成功捏",null);
            return loginResponseMsg;
        } else {
            // 用户名和密码不匹配，验证失败 返回19
            Message loginResponseMsg = new Message(19,null,"账号或密码 不对，登陆失败",null);
            return loginResponseMsg;
        }
    }

}
