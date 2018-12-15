package com.ehear.aiot.cloud.util;

import com.ehear.aiot.cloud.dao.CustomDao;
import com.ehear.aiot.cloud.dao.OperationDao;
import com.ehear.aiot.cloud.model.OperationBean;

import java.util.Collections;
import java.util.List;


public class CustomUtil {
	public static List<OperationBean> handlecustom(String custom_name, String username) {
		int customId = CustomDao.getCustomIdByNameAndUser(username, custom_name);
		// 根据customId查询该自定义模式下的所有operation
		List<OperationBean> OperationBeanList = OperationDao.getOperationListByCustomId(customId);
		Collections.sort(OperationBeanList);
		return OperationBeanList;
	}
}
