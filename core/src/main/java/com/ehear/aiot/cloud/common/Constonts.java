package com.ehear.aiot.cloud.common;

public class Constonts {
	public final static String SERVER_CONFIG_FILE_NAME = "realRelax.properties";
	public final static String LOG_CONFIG_FILE_NAME = "log4j.properties";

	public static boolean DEV_ENV = false;

	public static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static String JDBC_URL = "jdbc:mysql://localhost:3306/hikam_push_server?useUnicode=true&characterEncoding=UTF-8";
	public static String JDBC_USERNAME = "root";
	public static String JDBC_PASSWORD = "root";
	public static String SUPPORT_MULTIPLE_COMMANDS = "yes";

	public static final int SERVER_PORT = 8001;
	//public static final int SERVER_PORT = 8080;

	public static final String HEART_BEATING = "0001";
	public static final String MUSIC = "0007";
	public static final String AIRBAG = "0010";
	public static final String BACK_MASSAGE = "0011";
	public static final String WAIST_MSSAGE = "0012";
	public static final String FOOT_MASSAGE = "0013";
	public static final String MOTOR_UNIT = "0014";
	public static final String PUSH_ROD_MOTOR = "0015";
	public static final String HEATING = "0016";
	public static final String VIBRATING = "0017";
	public static final String DEVICE_TYPE = "00F0";
	public static final String RESETALL = "00F1";
	public static final String AIRBAG_MODE = "000F";
	public static final String BUZZER = "0008";
	public static final String WHOLE_STATE = "001F";
	public static final String AUTO_STATE = "0006";

	public static final String HEAD = "A5A5";
	public static final String TAIL = "0D0A";
	public static final String ZERO = "0009";
	public static final String MUTE = "001E";

	public static final String HIGH_END_BACK_MASSAGE = "0020";
	public static final String HIGH_END_FOOT_ROLLER_MASSAGE = "0021";
	public static final String HIGH_END_KNOCK_MASSAGE = "0022";
	public static final String HIGH_END_WALK_MASSAGE = "0023";
	public static final String HIGH_FOOT_PUSH_MASSAGE = "0024";
	public static final String HIGH_BACK_PUSH_MASSAGE = "0024";

	public static final String heartBeatingEg = "a5a55ccf7f077e85005c0001000100045c0d0a";

    //public static final String SERVER_HOST = "139.199.80.116";
	public static final String SERVER_HOST = "localhost";

}
