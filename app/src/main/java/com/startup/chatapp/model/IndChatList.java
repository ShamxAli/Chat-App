package com.startup.chatapp.model;

import java.io.Serializable;

public class IndChatList implements Serializable {
    private String lastmsg;
    private String mypushid;
    private String otherpushid;
    private String otheruid;
    private String msguid;
    private long timestamp;
    private String phone;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IndChatList(String lastmsg, String mypushid, String otherpushid, String otheruid, String msguid, long timestamp, String phone, String name) {
        this.lastmsg = lastmsg;
        this.mypushid = mypushid;
        this.otherpushid = otherpushid;
        this.otheruid = otheruid;
        this.msguid = msguid;
        this.timestamp = timestamp;
        this.phone = phone;
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public IndChatList(String lastmsg, String mypushid, String otherpushid, String otheruid, String msguid, long timestamp, String phone) {
        this.lastmsg = lastmsg;
        this.mypushid = mypushid;
        this.otherpushid = otherpushid;
        this.otheruid = otheruid;
        this.msguid = msguid;
        this.timestamp = timestamp;
        this.phone = phone;
    }

    public IndChatList() {
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public String getMypushid() {
        return mypushid;
    }

    public void setMypushid(String mypushid) {
        this.mypushid = mypushid;
    }

    public String getOtherpushid() {
        return otherpushid;
    }

    public void setOtherpushid(String otherpushid) {
        this.otherpushid = otherpushid;
    }

    public String getOtheruid() {
        return otheruid;
    }

    public void setOtheruid(String otheruid) {
        this.otheruid = otheruid;
    }

    public String getMsguid() {
        return msguid;
    }

    public void setMsguid(String msguid) {
        this.msguid = msguid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public IndChatList(String lastmsg, String mypushid, String otherpushid, String otheruid, String msguid, long timestamp) {
        this.lastmsg = lastmsg;
        this.mypushid = mypushid;
        this.otherpushid = otherpushid;
        this.otheruid = otheruid;
        this.msguid = msguid;
        this.timestamp = timestamp;
    }
}
