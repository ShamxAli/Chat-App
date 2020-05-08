package com.startup.chatapp.model;

public class MessageModelClass {
    private String msg;
    private String msgId;
    private String uid;
    private long timestamp;


    /*Constructors*/
    public MessageModelClass(String msg, String msgId, String uid, long timestamp) {
        this.msg = msg;
        this.msgId = msgId;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public MessageModelClass() {
    }

    public MessageModelClass(String msg, String msgId, long timestamp) {
        this.msg = msg;
        this.msgId = msgId;
        this.timestamp = timestamp;
    }


    public MessageModelClass(String msg, long timestamp) {
        this.msg = msg;
        this.timestamp = timestamp;
    }



    /*Getter Setters*/
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
