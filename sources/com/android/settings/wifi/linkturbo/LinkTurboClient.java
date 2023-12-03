package com.android.settings.wifi.linkturbo;

import android.content.Context;
import android.content.res.Resources;
import java.lang.reflect.Method;
import miuix.util.Log;

/* loaded from: classes2.dex */
public class LinkTurboClient {
    private Object mSlaManager;
    private Method method_addUidToLinkTurboWhiteList;
    private Method method_checkServiceIsConnected;
    private Method method_getLinkTurboAppDayTraffic;
    private Method method_getLinkTurboAppMonthTraffic;
    private Method method_getLinkTurboAppsTotalDayTraffic;
    private Method method_getLinkTurboAppsTotalMonthTraffic;
    private Method method_getLinkTurboEnable;
    private Method method_getLinkTurboWhiteList;
    private Method method_isLinkTurboServiceConnect;
    private Method method_isLinkTurboWhiteListNull;
    private Method method_removeUidInLinkTurboWhiteList;
    private Method method_setLinkTurboEnable;
    private Method method_shutdown;
    LinkTurboClient sLinkTurboClient;

    public LinkTurboClient(Context context) {
        this.mSlaManager = null;
        this.method_isLinkTurboWhiteListNull = null;
        this.method_isLinkTurboServiceConnect = null;
        this.method_setLinkTurboEnable = null;
        this.method_getLinkTurboEnable = null;
        this.method_shutdown = null;
        this.method_addUidToLinkTurboWhiteList = null;
        this.method_removeUidInLinkTurboWhiteList = null;
        this.method_getLinkTurboWhiteList = null;
        this.method_getLinkTurboAppDayTraffic = null;
        this.method_getLinkTurboAppsTotalDayTraffic = null;
        this.method_getLinkTurboAppMonthTraffic = null;
        this.method_getLinkTurboAppsTotalMonthTraffic = null;
        this.method_checkServiceIsConnected = null;
        try {
            Class<?> cls = Class.forName("com.qti.slalib.SlaManager");
            this.mSlaManager = cls.getConstructor(Context.class).newInstance(context);
            this.method_isLinkTurboWhiteListNull = cls.getDeclaredMethod("isLinkTurboWhiteListNull", null);
            this.method_isLinkTurboServiceConnect = cls.getDeclaredMethod("isLinkTurboServiceConnect", null);
            this.method_setLinkTurboEnable = cls.getDeclaredMethod("setLinkTurboEnable", Boolean.TYPE);
            this.method_getLinkTurboEnable = cls.getDeclaredMethod("getLinkTurboEnable", null);
            this.method_shutdown = cls.getDeclaredMethod("shutdown", null);
            this.method_addUidToLinkTurboWhiteList = cls.getDeclaredMethod("addUidToLinkTurboWhiteList", String.class);
            this.method_removeUidInLinkTurboWhiteList = cls.getDeclaredMethod("removeUidInLinkTurboWhiteList", String.class);
            this.method_getLinkTurboWhiteList = cls.getDeclaredMethod("getLinkTurboWhiteList", null);
            Class<?> cls2 = Integer.TYPE;
            this.method_getLinkTurboAppDayTraffic = cls.getDeclaredMethod("getLinkTurboAppDayTraffic", cls2);
            this.method_getLinkTurboAppsTotalDayTraffic = cls.getDeclaredMethod("getLinkTurboAppsTotalDayTraffic", null);
            this.method_getLinkTurboAppMonthTraffic = cls.getDeclaredMethod("getLinkTurboAppMonthTraffic", cls2);
            this.method_getLinkTurboAppsTotalMonthTraffic = cls.getDeclaredMethod("getLinkTurboAppsTotalMonthTraffic", null);
            this.method_checkServiceIsConnected = cls.getDeclaredMethod("checkServiceIsConnected", null);
        } catch (Exception e) {
            Log.e("LinkTurboClient", "the device don't support LinkTurbo, return " + e);
        }
    }

    public static boolean isLinkTurboSupported(Context context) {
        try {
            Resources resources = context.getResources();
            return resources.getBoolean(resources.getIdentifier("config_net_slm_supported", "bool", "android.miui"));
        } catch (Exception unused) {
            return false;
        }
    }

    private void resetLinkTurboClient() {
        this.sLinkTurboClient = null;
    }

    public void ShutDownLinkTurboService() {
        try {
            this.method_shutdown.invoke(this.mSlaManager, null);
        } catch (Exception e) {
            Log.e("LinkTurboClient", "ShutDownLinkTurboService Exception:" + e);
        }
        resetLinkTurboClient();
    }

    public boolean addUidToLinkTurboWhiteList(int i) {
        String num = Integer.toString(i);
        Boolean bool = new Boolean("false");
        if (isUidInLinkTurboWhiteList(i)) {
            return true;
        }
        Log.d("LinkTurboClient", "addUidToLinkTurboWhiteList:" + num);
        try {
            bool = (Boolean) this.method_addUidToLinkTurboWhiteList.invoke(this.mSlaManager, num);
        } catch (Exception e) {
            Log.e("LinkTurboClient", "addUidToLinkTurboWhiteList Exception:" + e);
        }
        return bool.booleanValue();
    }

    public void checkServiceIsConnected() {
        try {
            this.method_checkServiceIsConnected.invoke(this.mSlaManager, null);
        } catch (Exception e) {
            Log.e("LinkTurboClient", "checkServiceIsConnected Exception:" + e);
        }
    }

    public long getLinkTurboAppDayTraffic(int i) {
        try {
            return ((Long) this.method_getLinkTurboAppDayTraffic.invoke(this.mSlaManager, Integer.valueOf(i))).longValue();
        } catch (Exception e) {
            Log.e("LinkTurboClient", "getLinkTurboAppDayTraffic Exception:" + e);
            return 0L;
        }
    }

    public long getLinkTurboAppMonthTraffic(int i) {
        try {
            return ((Long) this.method_getLinkTurboAppMonthTraffic.invoke(this.mSlaManager, Integer.valueOf(i))).longValue();
        } catch (Exception e) {
            Log.e("LinkTurboClient", "getLinkTurboAppMonthTraffic Exception:" + e);
            return 0L;
        }
    }

    public boolean getLinkTurboEnable() {
        Boolean bool = new Boolean("false");
        try {
            bool = (Boolean) this.method_getLinkTurboEnable.invoke(this.mSlaManager, null);
        } catch (Exception e) {
            Log.e("LinkTurboClient", "getLinkTurboEnable Exception:" + e);
        }
        return bool.booleanValue();
    }

    public String getLinkTurboWhiteList() {
        String str = null;
        try {
            str = (String) this.method_getLinkTurboWhiteList.invoke(this.mSlaManager, null);
        } catch (Exception e) {
            Log.e("LinkTurboClient", "getLinkTurboWhiteList Exception:" + e);
        }
        Log.d("LinkTurboClient", "getLinkTurboWhiteList:" + str);
        return str;
    }

    public boolean isLinkTurboWhiteListReachMax() {
        String linkTurboWhiteList = getLinkTurboWhiteList();
        return linkTurboWhiteList != null && linkTurboWhiteList.split(",").length >= 15;
    }

    public boolean isUidInLinkTurboWhiteList(int i) {
        String linkTurboWhiteList = getLinkTurboWhiteList();
        return (linkTurboWhiteList == null || linkTurboWhiteList.indexOf(Integer.toString(i)) == -1) ? false : true;
    }

    public boolean removeUidInLinkTurboWhiteList(int i) {
        String num = Integer.toString(i);
        Boolean bool = new Boolean("false");
        if (isUidInLinkTurboWhiteList(i)) {
            Log.d("LinkTurboClient", "removeUidInLinkTurboWhiteList:" + num);
            try {
                bool = (Boolean) this.method_removeUidInLinkTurboWhiteList.invoke(this.mSlaManager, num);
            } catch (Exception e) {
                Log.e("LinkTurboClient", "removeUidInLinkTurboWhiteList Exception:" + e);
            }
            return bool.booleanValue();
        }
        return false;
    }

    public boolean setLinkTurboEnable(boolean z) {
        Boolean bool = new Boolean("false");
        try {
            bool = (Boolean) this.method_setLinkTurboEnable.invoke(this.mSlaManager, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("LinkTurboClient", "setLinkTurboEnable Exception:" + e);
        }
        return bool.booleanValue();
    }
}
