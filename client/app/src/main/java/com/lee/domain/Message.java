package com.lee.domain;

import java.util.Base64;
import java.util.Objects;

public class Message {
    private Integer sign;
    private String user;
    private String msg;
    private byte[] secretMsg;

    public Message() {
    }

    public Message(Integer sign, String user, String msg, byte[] secretMsg) {
        this.sign = sign;
        this.user = user;
        this.msg = msg;
        this.secretMsg = secretMsg;
    }

    public static Message from(ReceiveMessage msg) {

        final Message message = new Message();
        message.setUser(msg.getUser());
        message.setSign(msg.getSign());
        message.setMsg(msg.getMsg());
//        if(msg.getSecretMsg() != null && msg.getSecretMsg().length() > 0) { // 传统做法
        if(Objects.nonNull(msg.getSecretMsg()) && msg.getSecretMsg().length() > 0) { // 传统做法优化
//        if(StringUtils.isNotBlank(msg.getSecretMsg())) { // 企业做法
            message.setSecretMsg(Base64.getDecoder().decode(msg.getSecretMsg()));
        }
        return message;
    }

    /**
     * 获取
     * @return sign
     */
    public Integer getSign() {
        return sign;
    }

    /**
     * 设置
     * @param sign
     */
    public void setSign(Integer sign) {
        this.sign = sign;
    }

    /**
     * 获取
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 获取
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取
     * @return secretMsg
     */
    public byte[] getSecretMsg() {
        return secretMsg;
    }

    /**
     * 设置
     * @param secretMsg
     */
    public void setSecretMsg(byte[] secretMsg) {
        this.secretMsg = secretMsg;
    }

    public String toString() {
        return "Message{sign = " + sign + ", user = " + user + ", msg = " + msg + ", secretMsg = " + secretMsg + "}";
    }
}
