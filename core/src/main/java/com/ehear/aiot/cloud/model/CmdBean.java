package com.ehear.aiot.cloud.model;


import com.ehear.aiot.cloud.common.Constonts;
import com.ehear.aiot.cloud.util.CmdUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CmdBean {

    private String Head;
    private String MacAddr;
    private String Index;
    private String CMD;
    private String DataLen;
    private String DATA;
    private String CheckSum;
    private String Tail;

    public String getFinalCMD() {
        return Head + MacAddr + Index + CMD + DataLen + DATA + CheckSum + Tail;
    }

    @Override
    public String toString() {
        return "CmdBean [Head=" + Head + ", MacAddr=" + MacAddr + ", Index=" + Index + ", CMD=" + CMD + ", DataLen=" + DataLen
                + ", DATA=" + DATA + ", CheckSum=" + CheckSum + ", Tail=" + Tail + "]";
    }

    public String getHead() {
        return Head;
    }

    public void setHead(String head) {
        Head = head;
    }

    public String getMacAddr() {
        return MacAddr;
    }

    public void setMacAddr(String macAddr) {
        MacAddr = macAddr;
    }

    public String getIndex() {
        return Index;
    }

    public void setIndex(String index) {
        Index = index;
    }

    public String getCMD() {
        return CMD;
    }

    public void setCMD(String cMD) {
        CMD = cMD;
    }

    public String getDataLen() {
        return DataLen;
    }

    public void setDataLen(String dataLen) {
        DataLen = dataLen;
    }

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String dATA) {
        DATA = dATA;
    }

    public String getCheckSum() {
        return CheckSum;
    }

    public void setCheckSum(String checkSum) {
        CheckSum = checkSum;
    }

    public String getTail() {
        return Tail;
    }

    public void setTail(String tail) {
        Tail = tail;
    }

    public CmdBean(String cmd, String maccode, String raoddata, String openflag, String reverse, String lowtohigh) {
        this.Head = Constonts.HEAD;
        this.MacAddr = maccode;
        this.Index = CmdUtil.generateIndex();
        this.CMD = cmd;
        this.Tail = Constonts.TAIL;
        if ("0010".equals(cmd)) {
            this.DataLen = "0008";
            this.DATA = raoddata;
            this.CheckSum = "000A";
        } else if ("0011".equals(cmd) || "0012".equals(cmd) || "0013".equals(cmd) || "0014".equals(cmd) || "0015".equals(cmd)) {
            this.DataLen = "0002";
            this.DATA = reverse + lowtohigh;
            this.CheckSum = "0004";
        } else if ("0016".equals(cmd)) {
            this.DataLen = "0002";
            this.DATA = openflag;
            this.CheckSum = "0004";
        } else if ("0017".equals(cmd)) {
            this.DataLen = "0001";
            this.DATA = openflag;
            this.CheckSum = "0003";
        }
    }

    public CmdBean(String cmdString) {
        this.Head = cmdString.substring(0, 4);
        if (!HeadCheck(Head)) {
            log.info("Head error");
            return;
        }
        this.MacAddr = cmdString.substring(4, 16);
        this.Index = cmdString.substring(16, 20);
        this.CMD = cmdString.substring(20, 24);
        if (!cmdStringCheck(CMD)) {
            log.info("CMD error");
            return;
        }

        this.DataLen = cmdString.substring(24, 28);
        int len = convertHextoInt(this.DataLen);
        this.DATA = cmdString.substring(28, 29 + len * 2 - 1);
        this.CheckSum = cmdString.substring(28 + len * 2, 32 + len * 2);

        // 取cmdString前n-8的，每个字节的值相加
        int sum = 0;
        String cmdSubString = cmdString.substring(0, cmdString.length() - 8);
        for (int i = 0; i < cmdSubString.length() / 2; i++) {
            sum += convertHextoInt(cmdString.substring(2 * i, 2 * i + 2));
        }

        if (!(convertHextoInt(CheckSum) == sum)) {
            log.info("checkSum error");
        }

        this.Tail = cmdString.substring(32 + len * 2, 36 + len * 2);
        if (!TailCheck(Tail)) {
            log.info("Tail error");
            return;
        }
    }

    public CmdBean() {
        this.Head = Constonts.HEAD;
    }

    boolean HeadCheck(String head) {
        return head.equals(Constonts.HEAD) ? true : false;
    }

    boolean cmdStringCheck(String cmd) {
        if (cmd.equals(Constonts.MUSIC) || cmd.equals(Constonts.HEART_BEATING) || cmd.equals(Constonts.AIRBAG)
                || cmd.equals(Constonts.BACK_MASSAGE) || cmd.equals(Constonts.WAIST_MSSAGE) || cmd.equals(Constonts.FOOT_MASSAGE)
                || cmd.equals(Constonts.MOTOR_UNIT) || cmd.equals(Constonts.PUSH_ROD_MOTOR) || cmd.equals(Constonts.HEATING)
                || cmd.equals(Constonts.VIBRATING) || cmd.equals(Constonts.AIRBAG_MODE) || cmd.equals(Constonts.RESETALL)
                || cmd.equals(Constonts.WHOLE_STATE) || cmd.equals(Constonts.AUTO_STATE) || cmd.equals(Constonts.ZERO)
                || cmd.equals(Constonts.MUTE)) {
            return true;
        } else {
            return false;
        }

    }

    boolean TailCheck(String tail) {
        return tail.equals(Constonts.TAIL) ? true : false;
    }

    public static int convertHextoInt(String hexString) {
        int len = hexString.length();
        int sum = 0;
        for (int i = 0; i < len; i++) {
            char c = hexString.charAt(len - 1 - i);
            int n = Character.digit(c, 16);
            sum += n * (1 << (4 * i));
        }
        return sum;
    }

}
