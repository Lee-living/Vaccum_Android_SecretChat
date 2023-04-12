package com.lee.domain;

import java.util.Base64;

public class Message {
    //接受发消息 标志
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