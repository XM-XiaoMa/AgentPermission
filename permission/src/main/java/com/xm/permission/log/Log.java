package com.xm.permission.log;


public final class Log {
    public static final int LEVEL_INFO = 0x00;
    public static final int LEVEL_DEBUG = 0x10;
    public static final int LEVEL_WARNING = 0x20;
    public static final int LEVEL_ERROR = 0x30;
    public static final int LEVEL_DISABLE = 0xFF;
    public static final int LEVEL_ENABLE = -0xff;
    public static final String TAG = "XM";

    // 默认是所有
    private static int LOG_LEVEL = LEVEL_ENABLE;

    public static void setLOG_LEVEL(int LOG_LEVEL) {
        Log.LOG_LEVEL = LOG_LEVEL;
    }

    public static void disableLog() {
        LOG_LEVEL = LEVEL_DISABLE;
    }

    public static void i(String msg) {
        if (LEVEL_INFO >= LOG_LEVEL) {
            android.util.Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (LEVEL_INFO >= LOG_LEVEL) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void d(String msg) {
        if (LEVEL_DEBUG >= LOG_LEVEL) {
            android.util.Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LEVEL_DEBUG >= LOG_LEVEL) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void w(String msg) {
        if (LEVEL_WARNING >= LOG_LEVEL) {
            android.util.Log.d(TAG, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LEVEL_WARNING >= LOG_LEVEL) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void e(String msg) {
        if (LEVEL_ERROR >= LOG_LEVEL) {
            android.util.Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LEVEL_ERROR >= LOG_LEVEL) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void enableLog() {
        LOG_LEVEL = LEVEL_ENABLE;
    }
}
