package com.ehear.aiot.cloud.util;

import java.util.Random;

public class CmdUtil {
	public static String generateIndex() {

		return randomHexString(4);

	}

	public static String randomHexString(int len) {
		try {
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < len; i++) {
				result.append(Integer.toHexString(new Random().nextInt(16)));
			}
			return result.toString().toUpperCase();

		} catch (Exception e) {

			e.printStackTrace();

		}
		return null;

	}

}
