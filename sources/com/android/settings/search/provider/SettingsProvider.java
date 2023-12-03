package com.android.settings.search.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.settings.MiuiOptionUtils$Account;
import com.android.settings.MiuiOptionUtils$Display;
import com.android.settings.MiuiOptionUtils$DoNotDisturb;
import com.android.settings.MiuiOptionUtils$Mobile;
import com.android.settings.MiuiOptionUtils$Sound;
import com.android.settings.MiuiOptionUtils$Wifi;
import com.android.settings.MiuiOptionUtils$Wireless;
import com.android.settings.R;
import com.android.settingslib.search.SearchUtils;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class SettingsProvider extends ContentProvider {
    public static final String ARGS_KEY = "key";
    public static final String ARGS_VALUE = "value";
    public static final String AUTHORITY = "com.miui.settings";
    public static final String FULL_SEARCH_PATH = "fullSearch";
    public static final int FUNCTION_FULL_SEARCH = 1;
    public static final String METHOD_LOAD = "load";
    public static final String METHOD_RELEASE = "release";
    public static final String METHOD_TOGGLE_SWITCH = "toggleSwitch";
    public static final String RETURN = "return";
    private static final String TAG = "SettingsProvider";
    private static final MyUriMatcher URI_MATCHER = new MyUriMatcher();

    /* loaded from: classes2.dex */
    private static class MyUriMatcher extends UriMatcher {
        private static final int FUNCTION = 0;

        MyUriMatcher() {
            super(-1);
            addURI(SettingsProvider.AUTHORITY, SettingsProvider.FULL_SEARCH_PATH, 1);
            addURI(SettingsProvider.AUTHORITY, null, 0);
            addURI(SettingsProvider.AUTHORITY, "*", 0);
        }

        @Override // android.content.UriMatcher
        public int match(Uri uri) {
            int match = super.match(uri);
            if (match == 0 || match == 1) {
                return match;
            }
            throw new IllegalArgumentException("Unknown URL: " + uri);
        }
    }

    private int changeQuickBall(int i) {
        return -1;
    }

    private int changeTorch(int i) {
        int i2 = Settings.Global.getInt(getContext().getContentResolver(), "torch_state", 0);
        if (i == -1 || i == i2) {
            return i2;
        }
        Intent intent = new Intent("miui.intent.action.TOGGLE_TORCH");
        intent.putExtra("miui.intent.extra.IS_TOGGLE", true);
        getContext().sendBroadcastAsUser(intent, UserHandle.CURRENT);
        return i;
    }

    public static Uri getSearchUri(String str) {
        return Uri.parse("content://com.miui.settings/" + Uri.encode(str));
    }

    private void load() {
        SettingsTreeHelper.getInstance(getContext());
    }

    private void release() {
        SettingsTreeHelper.releaseInstance();
    }

    private int toggleSwitch(String str, int i) {
        String lowerCase = str.toLowerCase();
        Context context = getContext();
        for (String str2 : context.getString(R.string.keywords_silent).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str2)) {
                return MiuiOptionUtils$Sound.touchSilentState(context, i);
            }
        }
        for (String str3 : context.getString(R.string.keywords_auto_rotate).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str3)) {
                return MiuiOptionUtils$Display.touchRotationLockState(context, i);
            }
        }
        for (String str4 : context.getString(R.string.keywords_bluetooth).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str4)) {
                return MiuiOptionUtils$Wireless.touchBluetoothState(i);
            }
        }
        for (String str5 : context.getString(R.string.keywords_wifi).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str5)) {
                return MiuiOptionUtils$Wifi.touchWLANState(context, i);
            }
        }
        for (String str6 : context.getString(R.string.keywords_data).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str6)) {
                return MiuiOptionUtils$Mobile.touchDataState(context, i);
            }
        }
        for (String str7 : context.getString(R.string.keywords_torch).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str7)) {
                return changeTorch(i);
            }
        }
        for (String str8 : context.getString(R.string.keywords_do_not_disturb).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str8)) {
                return MiuiOptionUtils$DoNotDisturb.touchDoNotDisturbState(context, i);
            }
        }
        for (String str9 : context.getString(R.string.keywords_airplane).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str9)) {
                return MiuiOptionUtils$Wireless.touchAirplaneState(context, i);
            }
        }
        for (String str10 : context.getString(R.string.keywords_vibrate).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str10)) {
                return MiuiOptionUtils$Sound.touchVibrateState(context, i);
            }
        }
        for (String str11 : context.getString(R.string.keywords_gps).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str11)) {
                return MiuiOptionUtils$Wireless.touchGPSState(context, i);
            }
        }
        for (String str12 : context.getString(R.string.keywords_hotspot).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str12)) {
                return MiuiOptionUtils$Wifi.touchHotspotState(context, i);
            }
        }
        for (String str13 : context.getString(R.string.keywords_sync).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str13)) {
                return MiuiOptionUtils$Account.touchSyncState(i);
            }
        }
        for (String str14 : context.getString(R.string.keywords_paper_mode).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str14)) {
                return MiuiOptionUtils$Display.touchPaperModeState(context, i);
            }
        }
        for (String str15 : context.getString(R.string.keywords_quick_ball).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str15)) {
                return changeQuickBall(i);
            }
        }
        return -1;
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        if (METHOD_TOGGLE_SWITCH.equals(str)) {
            Bundle bundle2 = new Bundle();
            bundle2.putBoolean(RETURN, bundle.getInt("value") == toggleSwitch(bundle.getString(ARGS_KEY), bundle.getInt("value")));
            return bundle2;
        } else if (METHOD_LOAD.equals(str)) {
            load();
            return null;
        } else {
            if ("release".equals(str)) {
                release();
            }
            return null;
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        URI_MATCHER.match(uri);
        if (str == null || strArr == null) {
            return 0;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int delete = SettingsTreeHelper.getInstance(getContext()).delete(str, strArr);
        SearchUtils.logCost(currentTimeMillis, System.currentTimeMillis(), Integer.valueOf(delete));
        return delete;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        URI_MATCHER.match(uri);
        if (contentValues == null) {
            return null;
        }
        return Uri.parse(SettingsTreeHelper.getInstance(getContext()).insert(contentValues));
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        int match = URI_MATCHER.match(uri);
        if (uri.getPathSegments().size() != 1) {
            return null;
        }
        String lowerCase = uri.getLastPathSegment().toLowerCase();
        long currentTimeMillis = System.currentTimeMillis();
        Cursor query = SettingsTreeHelper.getInstance(getContext()).query(strArr, lowerCase, str, strArr2, str2, match == 1);
        SearchUtils.logCost(currentTimeMillis, System.currentTimeMillis(), Integer.valueOf(query.getCount()));
        return query;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        URI_MATCHER.match(uri);
        if (contentValues == null || str == null || strArr == null) {
            return 0;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int update = SettingsTreeHelper.getInstance(getContext()).update(contentValues, str, strArr);
        SearchUtils.logCost(currentTimeMillis, System.currentTimeMillis(), Integer.valueOf(update));
        return update;
    }
}
