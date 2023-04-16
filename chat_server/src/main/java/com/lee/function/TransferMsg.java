package com.lee.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.domain.Message;

import java.util.Arrays;

public class TransferMsg {
    ObjectMapper objectMapper = new ObjectMapper();
    private Message msg;
    private String s;
    public TransferMsg(Message msg, String s) {
        this.msg = msg;
        this.s = s;

    }

    //生成 发给接受者的消息体 31
    public String sendMsg() throws JsonProcessingException {
        //把加密后的消息复制到 secretMsgCopy
        byte[] secretMsgCopy = Arrays.copyOf(msg.getSecretMsg(), msg.getSecretMsg().length);

//        System.out.println(secretMsgCopy);
        //构造消息
        Message SendMsg = new Message(31,s,null,secretMsgCopy);
        String sendMsgjson = objectMapper.writeValueAsString(SendMsg);
        return sendMsgjson;
    }

    //生成 发给发送者的回执消息 30
    public String responseMsgYes() throws JsonProcessingException {
        Message responseMsgYes = new Message(30,null,"登陆成功捏",null);
        String sendresponsejosn = objectMapper.writeValueAsString(responseMsgYes);
        return sendresponsejosn;
    }
    //生成  发送失败 发给发送者的回执消息 29
    public String responseMsgNot() throws JsonProcessingException {
        Message responseMsgNot = new Message(29,null,"登陆失败捏，check下账号密码",null);
        String sendresponseMsgNot = objectMapper.writeValueAsString(responseMsgNot);
        return sendresponseMsgNot;
    }
}
