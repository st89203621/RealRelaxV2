package com.ehear.aiot.cloud.model;

public class CustomTask implements java.lang.Comparable<CustomTask> {
    private long second;
    private String cmd;
    private String mac_address;

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public long getSecond() {
        return second;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public int compareTo(CustomTask o) {
        int result = 0;
        // 按照时间降序
        result = (int) (this.second - o.second);
        return result;
    }

}
