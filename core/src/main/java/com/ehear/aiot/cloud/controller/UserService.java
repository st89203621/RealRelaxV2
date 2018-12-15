package com.ehear.aiot.cloud.controller;


import com.ehear.aiot.cloud.dao.UserDao;
import com.ehear.aiot.cloud.model.UserBean;
import com.ehear.aiot.cloud.util.MsgException;

public class UserService {

    private UserDao dao = new UserDao();

    public void registUser(UserBean user) throws MsgException {
        if (dao.findUserByUserName(user.getUname()) != null) {
            throw new MsgException("Unable to find the user !");
        }
        dao.addUser(user);
    }

    public UserBean isUser(String username, String password) {
        return dao.findUserByUNAndPWD(username, password);

    }

}
