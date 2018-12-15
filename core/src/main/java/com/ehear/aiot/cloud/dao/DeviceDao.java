package com.ehear.aiot.cloud.dao;

import com.ehear.aiot.cloud.model.DeviceBean;
import com.ehear.aiot.cloud.util.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeviceDao {


	public static DeviceBean findDeviceByDeviceName(String devicename) {

		QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "SELECT * FROM device WHERE device_name = ? ";
		Object params[] = { devicename };
		List<DeviceBean> deviceBeanList = new ArrayList<DeviceBean>();
		try {
			deviceBeanList = runner.query(sql, new BeanListHandler<DeviceBean>(DeviceBean.class), params);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		if (deviceBeanList.size() > 0) {
			DeviceBean deviceBean = deviceBeanList.get(0);
			return deviceBean;
		} else {
			return null;
		}
	}

	public static boolean addDevice(DeviceBean Device) {

		int result = 0;
		QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "INSERT INTO  device (mac_address, device_nick, device_type, device_owner) VALUES (?,?, ?, ?)";
		Object params[] = { Device.getmac_address(), Device.getDevice_nick(), Device.getDevice_type(),
				Device.getDevice_owner() };
		try {
			result = runner.update(sql, params);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return (result > 0) ? true : false;
	}

	public static List<DeviceBean> getAllDeviceByUserName(String username) {
		QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "SELECT * FROM device WHERE device_owner = ? ";
		Object params[] = { username };
		List<DeviceBean> deviceBeanList = new ArrayList<DeviceBean>();
		try {
			deviceBeanList = runner.query(sql, new BeanListHandler<DeviceBean>(DeviceBean.class), params);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return deviceBeanList;

	}

	public static boolean delDeviceByUserName_Mac(String username, String mac) {
		int result = 0;
		QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());

		String sql = "DELETE FROM device WHERE device_owner = ? and mac_address = ?";
		Object params[] = { username, mac };

		try {
			result = runner.update(sql, params);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return (result > 0) ? true : false;
	}

}
