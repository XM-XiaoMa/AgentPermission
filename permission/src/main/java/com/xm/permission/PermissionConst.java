package com.xm.permission;

public class PermissionConst {

    public class ResultCode {
        public static final int REQUEST_PERMISSION_ALL_GRANTED = 0;// 权限全部申请成功
        public static final int REQUEST_PERMISSION_ALL_DENIED = 1;// 权限全部申请失败
        public static final int REQUEST_PERMISSION_BOTH_HAS = 2; // 权限申请有成功的,也有失败的
    }

}
