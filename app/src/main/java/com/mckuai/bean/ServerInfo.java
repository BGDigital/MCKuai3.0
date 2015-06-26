package com.mckuai.bean;

import java.io.Serializable;

/**
 * Created by kyly on 2015/6/26.
 */
public class ServerInfo implements Serializable {
    private String address;
    private String port;
    private String name;
    private String position;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
