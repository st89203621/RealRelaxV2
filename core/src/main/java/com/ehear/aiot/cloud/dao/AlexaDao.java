package com.ehear.aiot.cloud.dao;

import com.ehear.aiot.cloud.util.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;

@Slf4j
public class AlexaDao {


	public static boolean updateToken(String username, String alexaToken) {
		int result = 0;
		QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());

		String sql = "UPDATE user SET alexa_token = ? where uname = ?";
		Object params[] = { alexaToken, username };

		try {
			result = runner.update(sql, params);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return (result > 0) ? true : false;
	}

}
