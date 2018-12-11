package com.xm.permission.entity;

import java.io.Serializable;

public class PermissionParam implements Serializable {

    /**
     * 权限名
     */
    private String permission = "";

    /**
     * 权限被拒绝时 - 弹窗信息
     */
    private String deniedText = "";

    /**
     * 权限之前有被拒绝的 - 弹窗信息
     */
    private String shouldShowText = "";

    /**
     * 标记该权限是否之前被拒绝过的
     */
    private boolean isRational = false;

    /**
     * 必要权限被拒绝的 - 退出弹窗信息
     */
    private String necessaryText = "";

    /**
     * 标记该权限是否为必要权限
     */
    private boolean isNecessary = false;


    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDeniedText() {
        return deniedText;
    }

    public void setDeniedText(String deniedText) {
        this.deniedText = deniedText;
    }

    public String getShouldShowText() {
        return shouldShowText;
    }

    public void setShouldShowText(String shouldShowText) {
        this.shouldShowText = shouldShowText;
    }

    public boolean isNecessary() {
        return isNecessary;
    }

    public void setNecessary(boolean necessary) {
        isNecessary = necessary;
    }

    public String getNecessaryText() {
        return necessaryText;
    }

    public void setNecessaryText(String necessaryText) {
        this.necessaryText = necessaryText;
    }

    public boolean isRational() {
        return isRational;
    }

    public void setRational(boolean rational) {
        isRational = rational;
    }
}
