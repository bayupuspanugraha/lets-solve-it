package com.bayunugraha.apiaccess.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {
    @JsonProperty("id")
    private int id;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("timestamp")
    private long timeStamp;

    @JsonProperty("txntype")
    private String TxnType;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("location")
    private LocationItem location;

    @JsonProperty("ip")
    private String ip;

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setTxnType(String txnType) {
        this.TxnType = txnType;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setLocation(LocationItem location) {
        this.location = location;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public String getTxnType() {
        return this.TxnType;
    }

    public String getAmount() {
        return this.amount;
    }

    public LocationItem getLocation() {
        return this.location;
    }

    public String getIp() {
        return this.ip;
    }
}
