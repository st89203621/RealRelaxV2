package com.ehear.aiot.cloud.util;

import java.util.Collections;
import java.util.List;

import com.ehear.aiot.cloud.dao.CustomDao;
import com.realrelax.alexa.bean.OperationBean;
import com.realrelax.alexa.dao.OperationDao;

public class CustomUtil {
	public static List<OperationBean> handlecustom(String custom_name, String username) {
		int customId = CustomDao.getCustomIdByNameAndUser(username, custom_name);
		// 根据customId查询该自定义模式下的所有operation
		List<OperationBean> OperationBeanList = OperationDao.getOperationListByCustomId(customId);
		Collections.sort(OperationBeanList);
		return OperationBeanList;
	}
}
