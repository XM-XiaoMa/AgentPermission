package com.xm.permission;

import android.content.Context;

import com.xm.permission.api.Action;
import com.xm.permission.entity.PermissionParam;

import java.util.List;

public class AgentPermission {

    private static AgentPermission instance = null;
    private AgentRuntimeRational runtimePermission;

    private AgentPermission() {
    }

    public static synchronized AgentPermission getInstance() {
        if (instance == null) {
            synchronized (AgentPermission.class) {
                instance = new AgentPermission();
            }
        }
        return instance;
    }

    public AgentPermission build() {
        runtimePermission = new AgentRuntimeRational();
        return this;
    }

    public AgentPermission setPermissionList(List<PermissionParam> permissionList) {
        runtimePermission.setPermission(permissionList);
        return this;
    }

    public AgentPermission setRequestCallback(Action requestCallback) {
        runtimePermission.setRequestCallback(requestCallback);
        return this;
    }

    public void start(Context context) {
        runtimePermission.start(context);
    }
}
