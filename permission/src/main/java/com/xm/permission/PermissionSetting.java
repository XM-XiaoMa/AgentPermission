package com.xm.permission;

/**
 * 权限等级划分：
 * NECESSARY : 必要权限 - 缺少该种权限,游戏将无法进行,系统自主展示退出提示框
 * UNNECESSARY : 非必要权限 - 缺少该种权限,只会影响游戏中的部分功能
 */
public class PermissionSetting {

    public class RequestCode {
        public static final int SETTING_UNNECESSARY_PERMISSION = 1;// 非必要权限，少了只会影响部分功能
        public static final int SETTING_NECESSARY_PERMISSION = 2;// 必要权限，用户未同意情况下，游戏将直接退出
    }

    public class ResultCode {
        public static final int UNNECESSARY_PERMISSION_ALL_GRANTED = 0;// 危险权限 - 经拒绝的权限全部设置允许
        public static final int UNNECESSARY_PERMISSION_ALL_DENIED = 1;// 危险权限 - 经拒绝权限全部未设置允许
        public static final int UNNECESSARY_PERMISSION_BOTH_HAS = 2; // 危险权限 - 经拒绝的权限部分设置允许，部分未设置允许
        public static final int NECESSARY_PERMISSION_ALL_GRANTED = 3; // 必要权限 - 经拒绝的权限部分设置允许，部分未设置允许
        public static final int NECESSARY_PERMISSION_ALL_DENIED = 4; // 必要权限 - 经拒绝的权限部分设置允许，部分未设置允许
    }
}
