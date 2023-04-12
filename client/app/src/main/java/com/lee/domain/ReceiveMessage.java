package com.lee.domain;

//这个类用来中转一些接收到的secretMsg String接受，方便转Message 中的byte[]
public class ReceiveMessage {
    private Integer sign;
    private String user;
    private String msg;
    private String secretMsg;

    public ReceiveMessage() {

    }

    public ReceiveMessage(Integer sign, String user, String msg, String secretMsg) {
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
    public String getSecretMsg() {
        return secretMsg;
    }

    /**
     * 设置
     * @param secretMsg
     */
    public void setSecretMsg(String secretMsg) {
        this.secretMsg = secretMsg;
    }

    public String toString() {
        return "ReceiveMessage{sign = " + sign + ", user = " + user + ", msg = " + msg + ", secretMsg = " + secretMsg + "}";
    }
}
