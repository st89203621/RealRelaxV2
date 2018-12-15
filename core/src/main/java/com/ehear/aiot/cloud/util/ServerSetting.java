package com.ehear.aiot.cloud.util;

import com.ehear.aiot.cloud.common.Constonts;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Slf4j
public class ServerSetting {
    /**
     * 加载配置参数
     */
    public void load() {
        log.info("Loading the server configuration information...");

        Properties properties = this.loadProperties();

        String env_is_development = properties.getProperty("env_is_development");
        Constonts.SUPPORT_MULTIPLE_COMMANDS = properties.getProperty("support_multiple_commands");
        if ("YES".equalsIgnoreCase(env_is_development)) {
            Constonts.DEV_ENV = true;
            Constonts.JDBC_DRIVER = properties.getProperty("development_jdbc_driver");
            Constonts.JDBC_URL = properties.getProperty("development_jdbc_url");
            Constonts.JDBC_USERNAME = properties.getProperty("development_jdbc_username");
            Constonts.JDBC_PASSWORD = properties.getProperty("development_jdbc_password");

        } else {
            Constonts.DEV_ENV = false;
            Constonts.JDBC_DRIVER = properties.getProperty("production_jdbc_driver");
            Constonts.JDBC_URL = properties.getProperty("production_jdbc_url");
            Constonts.JDBC_USERNAME = properties.getProperty("production_jdbc_username");
            Constonts.JDBC_PASSWORD = properties.getProperty("production_jdbc_password");

        }

        log.info("development environment[" + Constonts.DEV_ENV + "]");
        log.info("jdbc driver[" + Constonts.JDBC_DRIVER + "]");
        log.info("jdbc url[" + Constonts.JDBC_URL + "]");
        log.info("jdbc username[" + Constonts.JDBC_USERNAME + "]");
        log.info("jdbc password[" + Constonts.JDBC_PASSWORD + "]");
        log.info("Load the server configuration information finished.");
    }

    private Properties loadProperties() {
        String classPath = this.getClass().getClassLoader().getResource("/").getPath();
        classPath = classPath.substring(1, classPath.indexOf("classes"));
        InputStream inputstream = null;
        try {
            inputstream = new FileInputStream("/" + classPath + Constonts.SERVER_CONFIG_FILE_NAME);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Properties properties = new Properties();

        try {
            properties.load(inputstream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputstream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
