package com.ehear.aiot.cloud.dao;


import com.ehear.aiot.cloud.model.OperationBean;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OperationDao {
    public static boolean addOperation(OperationBean ob) {
        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "INSERT INTO  operation (custom_id, operation_desc, operation_time) VALUES (?,?,?)";
        Object params[] = { ob.getCustomId(), ob.getOperationDesc(), ob.getOperationTime() };
        try {
            if (0 == ob.getCustomId()) {
                result = 1;
            } else {
                result = runner.update(sql, params);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return (result > 0) ? true : false;
    }

    public static List<OperationBean> getOperationListByCustomId(int custom_id) {
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        List<OperationBean> OperationBeanList = new ArrayList<OperationBean>();
        String sql = "SELECT operation_id operationId, custom_id customId, operation_desc operationDesc, operation_time operationTime FROM operation WHERE custom_id = ? order by operation_time";
        try {
            OperationBeanList = runner.query(sql, new BeanListHandler<OperationBean>(OperationBean.class), custom_id);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return OperationBeanList;
    }

    public static boolean delOperation(int customId) {
        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "DELETE FROM operation WHERE operation_id = ?";
        Object params[] = { customId };

        try {
            result = runner.update(sql, params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return (result > 0) ? true : false;
    }

    public static boolean changeOperationState(int operationId, String flag) {
        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "UPDATE operation SET operation_desc = ? WHERE operation_id = ?";
        String desc = "";

        if (getOperationDescById(operationId).contains("start")) {
            desc = getOperationDescById(operationId).replace("start", flag);
        } else {
            desc = getOperationDescById(operationId).replace("stop", flag);
        }
        Object params[] = { desc, operationId };
        try {
            result = runner.update(sql, params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return (result > 0) ? true : false;
    }

    public static String getOperationDescById(int operationId) {
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        List<OperationBean> OperationBeanList = new ArrayList<OperationBean>();
        String sql = "SELECT operation_id operationId, custom_id customId, operation_desc operationDesc, operation_time operationTime FROM operation WHERE operation_id = ? order by operation_desc";
        try {
            OperationBeanList = runner.query(sql, new BeanListHandler<OperationBean>(OperationBean.class), operationId);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return OperationBeanList.get(0).getOperationDesc();
    }

	public static boolean changeSpeedById(int operationId, String newSpeed) {
        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "UPDATE operation SET operation_desc = ? WHERE operation_id = ?";
        String desc = getOperationDescById(operationId);
        String body = desc.split(";")[0];
        String reverse = "forward";
        if(newSpeed.contains("-"))
        {
        	 reverse = "reverse";
         newSpeed = 	 newSpeed.replace("-", "");
        }
        String newdesc = body + ";"+reverse + ";"+ newSpeed + ";"+desc.split(";")[3];
        
        Object params[] = { newdesc, operationId };
        try {
            result = runner.update(sql, params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return (result > 0) ? true : false;
	}

	public static boolean  changeTimeById(int operationId, String newTime) {
        int result = 0;
        QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "UPDATE operation SET operation_time = ? WHERE operation_id = ?";
        
        
        Object params[] = { newTime.replace(" ", ""), operationId };
        try {
            result = runner.update(sql, params);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return (result > 0) ? true : false;
		
	}

}
