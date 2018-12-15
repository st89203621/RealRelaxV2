package com.ehear.aiot.cloud.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ehear.aiot.cloud.common.Constonts;
import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JdbcUtils {
	private static Logger log = Logger.getLogger(JdbcUtils.class);

	private static ComboPooledDataSource ds = null;

	private static ComboPooledDataSource ds_alarm_push = null;

	// 使用ThreadLocal存储当前线程中的Connection对象
	private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

	// 在静态代码块中创建数据库连接池
	static {
		try {
			Thread.currentThread().setName("jdbc_initial_thread");

			// 通过代码创建C3P0数据库连接池
			ds = new ComboPooledDataSource();

			ds.setDriverClass(Constonts.JDBC_DRIVER);
			ds.setJdbcUrl(Constonts.JDBC_URL);
			ds.setUser(Constonts.JDBC_USERNAME);
			ds.setPassword(Constonts.JDBC_PASSWORD);

			ds.setInitialPoolSize(50);
			ds.setMinPoolSize(50);
			ds.setMaxPoolSize(80);

			ds.setMaxIdleTime(25000);
			ds.setMaxStatements(100);
			ds.setCheckoutTimeout(60000);

			ds.setPreferredTestQuery("SELECT 1");
			ds.setIdleConnectionTestPeriod(30);
			ds.setTestConnectionOnCheckout(true);

			log.info("JDBC utils init success.");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * @Method: getConnection
	 * @Description: 从数据源中获取数据库连接
	 * @Anthor:孤傲苍狼
	 * @return Connection
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		// 从当前线程中获取Connection
		Connection conn = threadLocal.get();
		if (conn == null) {
			// 从数据源中获取数据库连接
			conn = getDataSource().getConnection();
			// 将conn绑定到当前线程
			threadLocal.set(conn);
		}
		return conn;
	}

	/**
	 * @Method: startTransaction
	 * @Description: 开启事务
	 * @Anthor:孤傲苍狼
	 * 
	 */
	public static void startTransaction() {
		try {
			Connection conn = threadLocal.get();
			if (conn == null) {
				conn = getConnection();
				// 把 conn绑定到当前线程上
				threadLocal.set(conn);
			}
			// 开启事务
			conn.setAutoCommit(false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: rollback
	 * @Description:回滚事务
	 * @Anthor:孤傲苍狼
	 * 
	 */
	public static void rollback() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				// 回滚事务
				conn.rollback();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: commit
	 * @Description:提交事务
	 * @Anthor:孤傲苍狼
	 * 
	 */
	public static void commit() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				// 提交事务
				conn.commit();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: close
	 * @Description:关闭数据库连接(注意，并不是真的关闭，而是把连接还给数据库连接池)
	 * @Anthor:孤傲苍狼
	 * 
	 */
	public static void close() {
		try {
			// 从当前线程中获取Connection
			Connection conn = threadLocal.get();
			if (conn != null) {
				conn.close();
				// 解除当前线程上绑定conn
				threadLocal.remove();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Method: getDataSource
	 * @Description: 获取数据源
	 * @Anthor:孤傲苍狼
	 * @return DataSource
	 */
	public static DataSource getDataSource() {
		// 从数据源中获取数据库连接
		return ds;
	}

	public static DataSource getDataSource_alarm_push() {
		// 从数据源中获取数据库连接
		return ds_alarm_push;
	}
}
