package com.ehear.aiot.cloud.dao;


import com.ehear.aiot.cloud.util.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CustomDao {

    public static boolean addCustom(com.realrelax.alexa.bean.CustomBean cb) {
        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "INSERT INTO  custom (custom_name, custom_user_name, custom_create_time) VALUES (?,?,?)";
        Object params[] = { cb.getCustom_name(), cb.getCustom_user_name(), cb.getCustom_create_time() };
        try {
            result = runner.update(sql, params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return (result > 0) ? true : false;
    }

    public static List<com.realrelax.alexa.bean.CustomBean> getCustomListByUsername(String username) {
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        List<com.realrelax.alexa.bean.CustomBean> CustomBeanList = new ArrayList<com.realrelax.alexa.bean.CustomBean>();

        String sql = "SELECT custom_id custom_id, custom_name custom_name, custom_user_name custom_user_name, custom_create_time custom_create_time FROM custom WHERE custom_user_name = ? ";

        try {
            CustomBeanList = runner.query(sql, new BeanListHandler<com.realrelax.alexa.bean.CustomBean>(com.realrelax.alexa.bean.CustomBean.class), username);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return CustomBeanList;
    }

    public static boolean delCustom(int customId) {
        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());

        String sql = "DELETE FROM custom WHERE custom_id = ?";
        Object params[] = { customId };

        try {
            result = runner.update(sql, params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        // 相应的operation也要删除
        OperationDao.delOperation(customId);
        return (result > 0) ? true : false;
    }

    public static boolean updateCustomName(String customname, int customId) {
        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());

        String sql = "UPDATE custom SET custom_name = ? WHERE custom_id = ?";
        Object params[] = { customname, customId };

        try {
            result = runner.update(sql, params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return (result > 0) ? true : false;
    }

    public static int getCustomIdByNameAndUser(String username, String customname) {
        List<com.realrelax.alexa.bean.CustomBean> CustomBeanList = new ArrayList<com.realrelax.alexa.bean.CustomBean>();
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "SELECT custom_id custom_id, custom_name custom_name, custom_user_name custom_user_name, custom_create_time custom_create_time FROM custom WHERE custom_user_name = ? and custom_name = ?";
        Object params[] = { username, customname };
        try {
            CustomBeanList = runner.query(sql, new BeanListHandler<com.realrelax.alexa.bean.CustomBean>(com.realrelax.alexa.bean.CustomBean.class), params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        if (CustomBeanList.isEmpty()) {
            return 0;
        }
        return CustomBeanList.get(0).getCustom_id();
    }

    // 根据用户名获取最新的custom_name
    public static String getCustomNameByUser(String username) {
        List<com.realrelax.alexa.bean.CustomBean> CustomBeanList = new ArrayList<com.realrelax.alexa.bean.CustomBean>();
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "SELECT custom_id custom_id, custom_name custom_name, custom_user_name custom_user_name, custom_create_time custom_create_time FROM custom WHERE custom_user_name = ? order by custom_create_time desc";
        Object params[] = { username };
        try {
            CustomBeanList = runner.query(sql, new BeanListHandler<com.realrelax.alexa.bean.CustomBean>(com.realrelax.alexa.bean.CustomBean.class), params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        if (CustomBeanList.isEmpty()) {
            return "";
        }
        return CustomBeanList.get(0).getCustom_name();
    }

}
