package com.startup.chatapp.model;

import java.io.Serializable;
import java.util.Objects;

public class RecentChatsModel implements Serializable {
    private String lastMsg;
    private long timestamp;
    private String phone;
    private String name;
    //
    private String combined_uid;
    private String user1_uid;
    private String user2_uid;
    private String user1_pushid;
    private String user2_pushid;


    /*Constructors*/
    public RecentChatsModel() {
    }


    public RecentChatsModel(String lastMsg, String user1_pushid, String user2_pushid, String user2_uid, String combined_uid, long timestamp, String phone, String name) {
        this.lastMsg = lastMsg;
        this.user1_pushid = user1_pushid;
        this.user2_pushid = user2_pushid;
        this.user2_uid = user2_uid;
        this.combined_uid = combined_uid;
        this.timestamp = timestamp;
        this.phone = phone;
        this.name = name;
    }


    public RecentChatsModel(String lastMsg, String user1_pushid, String user2_pushid, String user2_uid, String combined_uid, long timestamp, String phone) {
        this.lastMsg = lastMsg;
        this.user1_pushid = user1_pushid;
        this.user2_pushid = user2_pushid;
        this.user2_uid = user2_uid;
        this.combined_uid = combined_uid;
        this.timestamp = timestamp;
        this.phone = phone;
    }

    public RecentChatsModel(String lastMsg, String user1_pushid, String user2_pushid, String user2_uid, String combined_uid, long timestamp) {
        this.lastMsg = lastMsg;
        this.user1_pushid = user1_pushid;
        this.user2_pushid = user2_pushid;
        this.user2_uid = user2_uid;
        this.combined_uid = combined_uid;
        this.timestamp = timestamp;
    }


    /*Getter Setters*/

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser1_pushid() {
        return user1_pushid;
    }

    public void setUser1_pushid(String user1_pushid) {
        this.user1_pushid = user1_pushid;
    }

    public String getUser2_pushid() {
        return user2_pushid;
    }

    public void setUser2_pushid(String user2_pushid) {
        this.user2_pushid = user2_pushid;
    }

    public String getUser2_uid() {
        return user2_uid;
    }

    public void setUser2_uid(String user2_uid) {
        this.user2_uid = user2_uid;
    }

    public String getCombined_uid() {
        return combined_uid;
    }

    public void setCombined_uid(String combined_uid) {
        this.combined_uid = combined_uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getUser1_uid() {
        return user1_uid;
    }

    public void setUser1_uid(String user1_uid) {
        this.user1_uid = user1_uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecentChatsModel that = (RecentChatsModel) o;
        return phone.equals(that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }

}
