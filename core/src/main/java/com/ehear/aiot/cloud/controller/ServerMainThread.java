package com.ehear.aiot.cloud.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.realrelax.alexa.global.Constonts;

public class ServerMainThread extends Thread {
    private static Logger log = Logger.getLogger(ServerMainThread.class);
    ServerSocket mServerSocket;
    SessionManager mSessionManager;

    public void run() {
        try {
            mServerSocket = new ServerSocket(Constonts.SERVER_PORT);
            mSessionManager = new SessionManager();
            mSessionManager.start();
            log.info("Socket Server started!");
            while (true) {
                Socket socket = mServerSocket.accept();
                System.out.println("服务端" + socket);
                SessionConnection mSessionConnection = new SessionConnection(socket, mSessionManager);
                mSessionConnection.registerSession();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerMainThread mMainThread = new ServerMainThread();
        mMainThread.start();
    }
}
