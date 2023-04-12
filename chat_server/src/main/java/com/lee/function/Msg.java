package com.lee.function;

import com.lee.domain.Message;

import java.util.Arrays;

public class Msg {
    private Message msg;
    private String s;
    public Msg(Message msg,String s) {
        this.msg = msg;
        this.s = s;

    }

    //生成 发给接受者的消息体 31
    public Message sendMsg(){
        //把加密后的消息复制到 secretMsgCopy
        byte[] secretMsgCopy = Arrays.copyOf(msg.getSecretMsg(), msg.getSecretMsg().length);

//        System.out.println(secretMsgCopy);
        //构造消息
        Message SendMsg = new Message(31,s,null,secretMsgCopy);
        return SendMsg;
    }

    //生成 发给发送者的回执消息 30
    public Message responseMsgYes(){
        return new Message(30,null,"登陆成功捏",null);
    }
    //生成  发送失败 发给发送者的回执消息 29
    public Message responseMsgNot(){
        return new Message(29,null,"登陆失败捏，check下账号密码",null);
    }
}
