package com.ehear.aiot.cloud.controller;

import com.ehear.aiot.cloud.common.Constonts;
import com.ehear.aiot.cloud.util.ServerSetting;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class InitService extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static ServerSetting serverSetting = null;
    private static SocketService socketService = null;


    public void init(ServletConfig config) throws ServletException {
        String classPath = this.getClass().getClassLoader().getResource("/").getPath();
        classPath = classPath.substring(1, classPath.indexOf("classes"));
        PropertyConfigurator.configure("/" + classPath + Constonts.LOG_CONFIG_FILE_NAME);

        log.info("------------------------------------------------------------------");
        InitService.printLogo();
        log.info("------------------------------------------------------------------");

        /**
         * 加载服务器配置参数
         */
        serverSetting = new ServerSetting();
        serverSetting.load();

        /**
         * 开启socket监听
         */

        socketService = new SocketService();
        socketService.start();

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void destroy() {
    }

    private static void printLogo() {
        log.info(
                "          _____                    _____                    _____                    _____                    _____          \r\n"
                        + "         /\\    \\                  /\\    \\                  /\\    \\                  /\\    \\                  /\\    \\         \r\n"
                        + "        /::\\    \\                /::\\____\\                /::\\    \\                /::\\    \\                /::\\    \\        \r\n"
                        + "       /::::\\    \\              /:::/    /               /::::\\    \\              /::::\\    \\              /::::\\    \\       \r\n"
                        + "      /::::::\\    \\            /:::/    /               /::::::\\    \\            /::::::\\    \\            /::::::\\    \\      \r\n"
                        + "     /:::/\\:::\\    \\          /:::/    /               /:::/\\:::\\    \\          /:::/\\:::\\    \\          /:::/\\:::\\    \\     \r\n"
                        + "    /:::/__\\:::\\    \\        /:::/____/               /:::/__\\:::\\    \\        /:::/__\\:::\\    \\        /:::/__\\:::\\    \\    \r\n"
                        + "   /::::\\   \\:::\\    \\      /::::\\    \\              /::::\\   \\:::\\    \\      /::::\\   \\:::\\    \\      /::::\\   \\:::\\    \\   \r\n"
                        + "  /::::::\\   \\:::\\    \\    /::::::\\    \\   _____    /::::::\\   \\:::\\    \\    /::::::\\   \\:::\\    \\    /::::::\\   \\:::\\    \\  \r\n"
                        + " /:::/\\:::\\   \\:::\\    \\  /:::/\\:::\\    \\ /\\    \\  /:::/\\:::\\   \\:::\\    \\  /:::/\\:::\\   \\:::\\    \\  /:::/\\:::\\   \\:::\\____\\ \r\n"
                        + "/:::/__\\:::\\   \\:::\\____\\/:::/  \\:::\\    /::\\____\\/:::/__\\:::\\   \\:::\\____\\/:::/  \\:::\\   \\:::\\____\\/:::/  \\:::\\   \\:::|    |\r\n"
                        + "\\:::\\   \\:::\\   \\::/    /\\::/    \\:::\\  /:::/    /\\:::\\   \\:::\\   \\::/    /\\::/    \\:::\\  /:::/    /\\::/   |::::\\  /:::|____|\r\n"
                        + " \\:::\\   \\:::\\   \\/____/  \\/____/ \\:::\\/:::/    /  \\:::\\   \\:::\\   \\/____/  \\/____/ \\:::\\/:::/    /  \\/____|:::::\\/:::/    / \r\n"
                        + "  \\:::\\   \\:::\\    \\               \\::::::/    /    \\:::\\   \\:::\\    \\               \\::::::/    /         |:::::::::/    /  \r\n"
                        + "   \\:::\\   \\:::\\____\\               \\::::/    /      \\:::\\   \\:::\\____\\               \\::::/    /          |::|\\::::/    /   \r\n"
                        + "    \\:::\\   \\::/    /               /:::/    /        \\:::\\   \\::/    /               /:::/    /           |::| \\::/____/    \r\n"
                        + "     \\:::\\   \\/____/               /:::/    /          \\:::\\   \\/____/               /:::/    /            |::|  ~|          \r\n"
                        + "      \\:::\\    \\                  /:::/    /            \\:::\\    \\                  /:::/    /             |::|   |          \r\n"
                        + "       \\:::\\____\\                /:::/    /              \\:::\\____\\                /:::/    /              \\::|   |          \r\n"
                        + "        \\::/    /                \\::/    /                \\::/    /                \\::/    /                \\:|   |          \r\n"
                        + "         \\/____/                  \\/____/                  \\/____/                  \\/____/                  \\|___|      ");
    }

}