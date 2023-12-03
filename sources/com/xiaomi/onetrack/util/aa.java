package com.xiaomi.onetrack.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.onetrack.e.a;

/* loaded from: classes2.dex */
public class aa {
    private static SharedPreferences c;
    private static SharedPreferences.Editor d;

    public static String A() {
        return a("last_app_version", "");
    }

    public static long B() {
        return a("first_launch_time", 0L);
    }

    private static void C() {
        if (d != null) {
            return;
        }
        synchronized (aa.class) {
            if (d == null) {
                SharedPreferences sharedPreferences = a.a().getSharedPreferences("one_track_pref", 0);
                c = sharedPreferences;
                d = sharedPreferences.edit();
            }
        }
    }

    private static long a(String str, long j) {
        C();
        return c.getLong(str, j);
    }

    public static String a(Context context) {
        return a("custom_id", "");
    }

    private static String a(String str, String str2) {
        C();
        return c.getString(str, str2);
    }

    public static void a(long j) {
        b("last_upload_active_time", j);
    }

    public static void a(String str) {
        b("secret_key_data", str);
    }

    public static long b() {
        return a("last_collect_crash_time", 0L);
    }

    public static void b(String str) {
        b("region_rul", str);
    }

    private static void b(String str, long j) {
        C();
        d.putLong(str, j).apply();
    }

    private static void b(String str, String str2) {
        C();
        d.putString(str, str2).apply();
    }

    private static boolean b(String str, boolean z) {
        C();
        return c.getBoolean(str, z);
    }

    public static long c() {
        return a("report_crash_ticket", 0L);
    }

    public static void c(long j) {
        b("last_collect_crash_time", j);
    }

    public static void c(String str) {
        b("common_config_hash", str);
    }

    private static void c(String str, boolean z) {
        C();
        d.putBoolean(str, z).apply();
    }

    public static void c(boolean z) {
        c("onetrack_first_open", z);
    }

    public static void d(long j) {
        b("report_crash_ticket", j);
    }

    public static void d(String str) {
        b("common_cloud_data", str);
    }

    public static void e(String str) {
        b("pref_instance_id", str);
        k(ac.a());
    }

    public static String g() {
        return a("secret_key_data", "");
    }

    public static String h() {
        return a("region_rul", "");
    }

    public static void i(long j) {
        b("last_secret_key_time", j);
    }

    public static void i(String str) {
        b("page_end", str);
    }

    public static long j() {
        return a("next_update_common_conf_time", 0L);
    }

    public static void j(long j) {
        b("next_update_common_conf_time", j);
    }

    public static void j(String str) {
        b("last_app_version", str);
    }

    public static void k(long j) {
        b("pref_instance_id_last_use_time", j);
    }

    public static boolean k(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        return b("pref_custom_privacy_policy_" + str, true);
    }

    public static String l() {
        return a("common_cloud_data", "");
    }

    public static String m() {
        return a("pref_instance_id", "");
    }

    public static void m(long j) {
        b("dau_last_time", j);
    }

    public static void n(long j) {
        b("first_launch_time", j);
    }

    public static boolean s() {
        return b("onetrack_first_open", true);
    }

    public static long t() {
        return a("dau_last_time", 0L);
    }

    public static String u() {
        return a("onetrack_user_id", "");
    }

    public static String w() {
        return a("onetrack_user_type", "");
    }

    public static String z() {
        return a("page_end", "");
    }
}
