package com.ehear.aiot.cloud.dao;

import com.ehear.aiot.cloud.model.UserBean;
import com.ehear.aiot.cloud.util.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserDao {


    public UserBean findUserByUserName(String username) {

        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "SELECT * FROM user WHERE uname = ? ";
        Object params[] = { username };
        List<UserBean> pushDataList = new ArrayList<UserBean>();
        try {
            pushDataList = runner.query(sql, new BeanListHandler<UserBean>(UserBean.class), params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        if (pushDataList.size() > 0) {
            UserBean userEle = pushDataList.get(0);
            return userEle;
        } else {
            return null;
        }
    }

    public boolean addUser(UserBean user) {

        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "INSERT INTO  user (uname, upassword, uemail, unick) VALUES (?,?, ?, ?)";
        Object params[] = { user.getUname(), user.getUpassword(), user.getUemail(), user.getUnick() };
        try {
            result = runner.update(sql, params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return (result > 0) ? true : false;
    }

    public UserBean findUserByUNAndPWD(String username, String password) {

        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "SELECT * FROM user WHERE uname = ? and upassword = ?";
        Object params[] = { username, password };
        List<UserBean> pushDataList = new ArrayList<UserBean>();
        try {
            pushDataList = runner.query(sql, new BeanListHandler<UserBean>(UserBean.class), params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        if (pushDataList.size() > 0) {
            UserBean userEle = pushDataList.get(0);
            return userEle;
        } else {
            return null;
        }
    }

}
