package com.ehear.aiot.cloud.model;

public class DeviceBean {

    private String device_token;
    private String mac_address;
    private String device_nick;
    private String device_type;
    private String device_psd;

    @Override
    public String toString() {
        return "DeviceBean [device_token=" + device_token + ", mac_address=" + mac_address + ", device_nick=" + device_nick
                + ", device_type=" + device_type + ", device_psd=" + device_psd + ", device_owner=" + device_owner + "]";
    }

    public String getDevice_owner() {
        return device_owner;
    }

    public void setDevice_owner(String device_owner) {
        this.device_owner = device_owner;
    }

    private String device_owner;

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getmac_address() {
        return mac_address;
    }

    public void setmac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getDevice_nick() {
        return device_nick;
    }

    public void setDevice_nick(String device_nick) {
        this.device_nick = device_nick;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_psd() {
        return device_psd;
    }

    public void setDevice_psd(String device_psd) {
        this.device_psd = device_psd;
    }

}
