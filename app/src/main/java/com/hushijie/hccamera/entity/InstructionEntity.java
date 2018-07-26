package com.hushijie.hccamera.entity;

import java.io.Serializable;

/**
 * 指令集实体
 * Created by lichao on 2018/7/13.
 */

public class InstructionEntity implements Serializable {

    /**
     * idenKey : ble_jkadjdkfjkdf
     * equipmentNo : 1231243123123
     * idenAccountId : 1
     * accountId : 1
     * instruction : api_find_device
     * bleAddress : "ab:cd:ef"
     * bleName : "手环"
     * organId : 1
     * isRaplace : true
     * lastBleAddress : ab:cd:ef
     * sendTime : "150121252112121"
     */
    private String idenKey;
    private long equipmentNo;
    private int idenAccountId;
    private int organId;
    private int accountId;
    private String instruction;
    private String bleAddress;
    private String bleName;
    private boolean isReplace;
    private String lastBleAddress;

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    private String sendTime;

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getIdenKey() {
        return idenKey;
    }

    public void setIdenKey(String idenKey) {
        this.idenKey = idenKey;
    }

    public long getEquipmentNo() {
        return equipmentNo;
    }

    public void setEquipmentNo(long equipmentNo) {
        this.equipmentNo = equipmentNo;
    }

    public int getIdenAccountId() {
        return idenAccountId;
    }

    public void setIdenAccountId(int idenAccountId) {
        this.idenAccountId = idenAccountId;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }


    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getOrganId() {
        return organId;
    }

    public void setOrganId(int organId) {
        this.organId = organId;
    }


    public boolean isReplace() {
        return isReplace;
    }

    public void setReplace(boolean replace) {
        isReplace = replace;
    }

    public String getLastBleAddress() {
        return lastBleAddress;
    }

    public void setLastBleAddress(String lastBleAddress) {
        this.lastBleAddress = lastBleAddress;
    }

}
