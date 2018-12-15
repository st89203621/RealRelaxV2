package com.ehear.aiot.cloud.model;

public class MultiplyCMD {
    public MultiplyCMD(String flagDevice, String flagBT, String flagHeater, String flagShakeMotor, String flagAirPumpMode,
            String flagMotor) {
        super();
        this.flagDevice = flagDevice;
        this.flagBT = flagBT;
        this.flagHeater = flagHeater;
        this.flagShakeMotor = flagShakeMotor;
        this.flagAirPumpMode = flagAirPumpMode;
        this.flagMotor = flagMotor;
    }

    public MultiplyCMD() {
        this.flagDevice = "01";
        this.flagBT = "FF";
        this.flagHeater = "FF";
        this.flagShakeMotor = "FF";
        this.flagAirPumpMode = "FFFFFFFFFFFFFFFF";
        this.flagMotor = "FFFFFFFFFFFFFFFF";
    }

    private String flagDevice;
    private String flagBT;
    private String flagHeater;
    private String flagShakeMotor;
    private String flagAirPumpMode;
    private String flagMotor;

    public String getFlagDevice() {
        return flagDevice;
    }

    public void setFlagDevice(String flagDevice) {
        this.flagDevice = flagDevice;
    }

    public String getFlagBT() {
        return flagBT;
    }

    public void setFlagBT(String flagBT) {
        this.flagBT = flagBT;
    }

    public String getFlagHeater() {
        return flagHeater;
    }

    public void setFlagHeater(String flagHeater) {
        this.flagHeater = flagHeater;
    }

    public String getFlagShakeMotor() {
        return flagShakeMotor;
    }

    public void setFlagShakeMotor(String flagShakeMotor) {
        this.flagShakeMotor = flagShakeMotor;
    }

    public String getFlagAirPumpMode() {
        return flagAirPumpMode;
    }

    public void setFlagAirPumpMode(String flagAirPumpMode) {
        this.flagAirPumpMode = flagAirPumpMode;
    }

    public String getFlagMotor() {
        return flagMotor;
    }

    public void setFlagMotor(String flagMotor) {
        this.flagMotor = flagMotor;
    }

    public String getFinalCMD() {
        return flagDevice + flagBT + flagHeater + flagShakeMotor + flagAirPumpMode + flagMotor;
    }

}
