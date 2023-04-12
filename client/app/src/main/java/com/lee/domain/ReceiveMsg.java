package com.lee.domain;

//对话界面的消息构造
public class ReceiveMsg {
    private String msg;
    private boolean flag;
    public ReceiveMsg() {
    }

    public ReceiveMsg(String msg, boolean flag) {
        this.msg = msg;
        this.flag = flag;
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
     * @return flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * 设置
     * @param flag
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String toString() {
        return "ReceiveMsg{msg = " + msg + ", flag = " + flag + "}";
    }
}
