package com.networkmanagement.networkmanagementsystem.model;

public class Connection {
    private int from;
    private int to;

    public Connection() {
    }

    public Connection(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
