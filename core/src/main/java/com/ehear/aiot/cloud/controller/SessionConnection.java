package com.ehear.aiot.cloud.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.realrelax.alexa.bean.CmdBean;
import com.realrelax.alexa.global.Constonts;
import com.realrelax.alexa.utils.HexUtil;

public class SessionConnection {
    private static Logger log = Logger.getLogger(SessionConnection.class);
    public static ConcurrentHashMap<String, String> cmdMap = new ConcurrentHashMap<>();
    public static HashMap<Integer, String> portMap = new HashMap<>();
    public static Set<String> onlineMacAddress = new HashSet<>();
    private Socket mClient;
    private ReadThread mReadThread;
    private WriteThread mWriteThread;
    private InputStream inputStream;
    private OutputStream outputStream;
    private List<byte[]> mListSend;
    private int i = 0;
    private SessionListener mSessionListener;
    private long mlLastRcvDataTime = System.currentTimeMillis();
    private long mlLastSendDataTime = System.currentTimeMillis();

    int[] cmd = new int[8];

    public static void addCmdList(String macAddress, String cmd) {
        cmdMap.put(macAddress, cmd);
    }

    public SessionConnection(Socket mClient, SessionListener mSessionListener) {
        this.mClient = mClient;
        this.mSessionListener = mSessionListener;
        try {
            inputStream = mClient.getInputStream();
            outputStream = mClient.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mListSend = new ArrayList<byte[]>();
    }

    public Socket getmClient() {
        return mClient;
    }

    public void setmClient(Socket mClient) {
        this.mClient = mClient;
    }

    public void registerSession() {
        mReadThread = new ReadThread();
        mReadThread.start();
        mWriteThread = new WriteThread();
        mWriteThread.start();
        System.out.println("registerSession");
        mSessionListener.addSessionConnection(this);
    }

    public void releaseConnection() {
        mReadThread.interrupt();
        mWriteThread.interrupt();
        try {
            inputStream.close();
            outputStream.close();
            mClient.close();
            mSessionListener.removeSessionConnection(this);
            System.out.println("释放客户端:" + mClient + "socket连接:");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void sendDate(String obj) {
        obj = obj + i;
        byte[] buf = HexUtil.hexStringToBytes(obj);
        mListSend.add(buf);
        i++;
    }

    public void sendXT() {
        long TimeOut = Math.abs(System.currentTimeMillis() - mlLastSendDataTime);
        if (TimeOut >= 6000) {
            mlLastSendDataTime = System.currentTimeMillis();
          //  sendDate("xtb啊哈");
        }
    }

    public boolean tryToReleaseConnect2TimeOut() {
        boolean bRet = false;
        boolean bTimeOut = Math.abs(System.currentTimeMillis() - mlLastRcvDataTime) > 6000 * 3 ? true : false;
        if (bTimeOut) {
            bRet = true;
        }
        return bRet;
    }

    private class ReadThread extends Thread {

        @SuppressWarnings("unused")
        public void run() {
            String str = "";
            int n = 0;
            byte[] buffer;
            while (!isInterrupted()) {
                try {
                    buffer = new byte[50];
                    n = inputStream.read(buffer);
                    str = HexUtil.bytesToHexString(buffer);
                    str = str.toUpperCase();
                    try {
                        str = (str.substring(0, str.lastIndexOf("0D0A")) + "0D0A");
                        mlLastRcvDataTime = System.currentTimeMillis();
                        try {
                            CmdBean cb = new CmdBean(str);
                            if (cb.getTail().equals(Constonts.TAIL) && cb.getCMD().equals(Constonts.HEART_BEATING)) {
                                System.out.println("检测到心跳包:" + mClient + " 心跳内容-->" + str);
                                // 更新在线设备列表
                                for (String mac : onlineMacAddress) {
                                    System.out.println(mac);
                                }
                                onlineMacAddress.add(cb.getMacAddr());
                                portMap.put(mClient.getLocalPort(), cb.getMacAddr());
                                // 回应心跳包
                                sendDate(str);
                            } else {
                                System.out.println("收到客户端回应:" + mClient + " 回应内容-->" + str);
                                // 记录下设备回应index
                                    SocketService.last_index.put(cb.getMacAddr(), "ok");
                            }
                        } catch (Exception e) {
                        }
                    } catch (Exception e) {
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    CmdBean cb = new CmdBean(str);
                    onlineMacAddress.remove(cb.getMacAddr());
                    portMap.remove(mClient.getLocalPort(), cb.getMacAddr());
                    releaseConnection();
                    break;
                }
            }
        }
    }

    private class WriteThread extends Thread {
        public void run() {
            while (!isInterrupted()) {
                try {
                    if (cmdMap.containsKey(portMap.get(mClient.getLocalPort()))) {
                        log.info("【【【【【【【【【【【【" + cmdMap.get(portMap.get(mClient.getLocalPort())) + "】】】】】】】");
                        CmdBean cBean = new CmdBean(cmdMap.get(portMap.get(mClient.getLocalPort())));
                        System.out.println(cBean.toString());
                        sendDate(cmdMap.get(portMap.get(mClient.getLocalPort())));
                        cmdMap.remove(portMap.get(mClient.getLocalPort()));
                    }
                    for (byte[] data : mListSend) {
                        outputStream.write(data);
                    }
                    mListSend.clear();
                    // sendXT();
                } catch (IOException e) {
                    e.printStackTrace();
                    releaseConnection();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public interface SessionListener {
        public void addSessionConnection(SessionConnection mSessionConnection);

        public void removeSessionConnection(SessionConnection mSessionConnection);
    }
}
