package com.ehear.aiot.cloud.model;

import com.ehear.aiot.cloud.util.MsgException;

public class UserBean {

    private String uname;
    private String upassword;
    private String upassword2;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    public String getUnick() {
        return unick;
    }

    public void setUnick(String unick) {
        this.unick = unick;
    }

    public String getUemail() {
        return uemail;
    }

    public void setUemail(String uemail) {
        this.uemail = uemail;
    }

    private String unick;
    private String uemail;

    public UserBean() {

    }

    public UserBean(String username, String password, String password2, String nickname, String email) {

        this.uname = username;
        this.upassword = password;
        this.upassword2 = password2;
        this.unick = nickname;
        this.uemail = email;
    }

    @Override
    public String toString() {
        return uname + ":" + upassword;
    }

    public void checkValue() throws MsgException {
        if (uname == null || "".equals(uname)) {
            throw new MsgException("Username is empty.");
        }
        if (upassword == null || "".equals(upassword)) {
            throw new MsgException("Password is empty.");
        }
        if (upassword2 == null || "".equals(upassword2)) {
            throw new MsgException("Password2 is empty.");
        }
        if (!upassword.equals(upassword2)) {
            throw new MsgException("The second input password and the first mismatch.");
        }
        if (unick == null || "".equals(unick)) {
            throw new MsgException("Nickname is empty.");
        }
        if (uemail == null || "".equals(uemail)) {
            throw new MsgException("Email id empty.");
        }
    }

    public String getUpassword2() {
        return upassword2;
    }

    public void setUpassword2(String upassword2) {
        this.upassword2 = upassword2;
    }

}
