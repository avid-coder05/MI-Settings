package com.android.settings.nfc;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
public class NfcSeRoute implements Preference.OnPreferenceChangeListener {
    private Context mContext;
    private String[] mEntries;
    private List<CharSequence> mEntryValues;
    private Method mGetSeRouting;
    private Constructor mMiNfcAdapterConstructor;
    private ListPreference mPreference;
    private Method mSetSeRouting;
    private List<String> mSummaries = new ArrayList();
    private String mCurrentRouteValue = "";

    public NfcSeRoute(Context context, NfcAdapter nfcAdapter, ListPreference listPreference) {
        if (isNfcSeRouteSupported()) {
            this.mContext = context;
            try {
                Class<?> cls = Class.forName("com.xiaomi.nfc.MiNfcAdapter");
                this.mMiNfcAdapterConstructor = cls.getDeclaredConstructor(Context.class);
                this.mGetSeRouting = cls.getDeclaredMethod("getSeRouting", new Class[0]);
                this.mSetSeRouting = cls.getDeclaredMethod("setSeRouting", Integer.TYPE);
            } catch (Exception e) {
                Log.d("NfcSeRoute", "MiNfcAdapter: " + e);
            }
            CharSequence[] entryValues = listPreference.getEntryValues();
            if (entryValues != null) {
                this.mEntryValues = Arrays.asList(entryValues);
                Log.d("NfcSeRoute", "entryValues array:" + this.mEntryValues);
            }
            this.mPreference = listPreference;
            String string = this.mContext.getString(Build.IS_GLOBAL_BUILD ? R.string.nfc_se_route_title : R.string.nfc_se_wallet_title);
            this.mPreference.setTitle(string);
            this.mPreference.setDialogTitle(string);
            this.mPreference.setOnPreferenceChangeListener(this);
            this.mEntries = getEntries();
            updatePreference();
        }
    }

    private int getCurrentIndex(int i) {
        if (i < 0) {
            i = this.mEntryValues.indexOf("ESE");
        }
        if (i < 0) {
            i = this.mEntryValues.indexOf("HCE");
        }
        if (i < 0) {
            i = this.mEntryValues.indexOf("UICC");
        }
        if (i < 0) {
            i = this.mEntryValues.indexOf("UICC1");
        }
        return i < 0 ? this.mEntryValues.indexOf("UICC2") : i;
    }

    private String[] getEntries() {
        ArrayList arrayList = new ArrayList();
        this.mSummaries.clear();
        Context context = this.mContext;
        boolean z = Build.IS_GLOBAL_BUILD;
        String string = context.getString(z ? R.string.se_route_ese : R.string.se_wallet_ese);
        String string2 = this.mContext.getString(R.string.se_wallet_hce);
        String string3 = this.mContext.getString(z ? R.string.current_use_ese : R.string.current_use_ese_wallet);
        String string4 = this.mContext.getString(R.string.current_use_hce_wallet);
        String string5 = this.mContext.getString(R.string.se_route_default);
        String string6 = this.mContext.getString(R.string.current_use_default_wallet);
        boolean z2 = RegionUtils.IS_JP;
        if (z2) {
            string = string5;
        }
        if (z2) {
            string3 = string6;
        }
        List<CharSequence> list = this.mEntryValues;
        if (list == null) {
            Log.i("NfcSeRoute", "entryValues array is null");
            return null;
        }
        Iterator<CharSequence> it = list.iterator();
        while (it.hasNext()) {
            String charSequence = it.next().toString();
            if (TextUtils.equals(charSequence, "ESE")) {
                arrayList.add(string);
                this.mSummaries.add(string3);
            } else if (TextUtils.equals(charSequence, "HCE")) {
                arrayList.add(string2);
                this.mSummaries.add(string4);
            } else if (TextUtils.equals(charSequence, "UICC")) {
                arrayList.add(String.format(this.mContext.getString(R.string.se_wallet_uicc), ""));
                this.mSummaries.add(String.format(this.mContext.getString(R.string.current_use_uicc_wallet), ""));
            } else if (TextUtils.equals(charSequence, "UICC1")) {
                arrayList.add(String.format(this.mContext.getString(R.string.se_wallet_uicc), "1"));
                this.mSummaries.add(String.format(this.mContext.getString(R.string.current_use_uicc_wallet), "1"));
            } else if (TextUtils.equals(charSequence, "UICC2")) {
                arrayList.add(String.format(this.mContext.getString(R.string.se_wallet_uicc), "2"));
                this.mSummaries.add(String.format(this.mContext.getString(R.string.current_use_uicc_wallet), "2"));
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private int getMappingSeRoute(String str) {
        if (TextUtils.equals(str, "HCE")) {
            return 0;
        }
        if (TextUtils.equals(str, "ESE")) {
            return 1;
        }
        if (TextUtils.equals(str, "UICC") || TextUtils.equals(str, "UICC1")) {
            return 2;
        }
        return TextUtils.equals(str, "UICC2") ? 4 : 1;
    }

    private int getSeRoute() {
        try {
            Method method = this.mGetSeRouting;
            if (method != null) {
                return ((Integer) method.invoke(this.mMiNfcAdapterConstructor.newInstance(this.mContext), new Object[0])).intValue();
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException unused) {
        }
        Log.e("NfcSeRoute", "Failed to invoke NfcSeRoute.getSeRouting()");
        return 1;
    }

    private boolean isNfcSeRouteSupported() {
        if (FeatureParser.getBoolean("support_se_route", false)) {
            return true;
        }
        Log.i("NfcSeRoute", "NfcSeRoute is not supported");
        return false;
    }

    private void setSeRoute(int i) {
        try {
            Method method = this.mSetSeRouting;
            if (method != null) {
                method.invoke(this.mMiNfcAdapterConstructor.newInstance(this.mContext), Integer.valueOf(i));
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException unused) {
            Log.e("NfcSeRoute", "Failed to invoke NfcSeRoute.setSeRouting()");
        }
    }

    private void updatePreference() {
        int indexOf;
        String[] strArr = this.mEntries;
        if (strArr == null || strArr.length == 0) {
            Log.d("NfcSeRoute", "updatePreference called! entries array is null");
            this.mPreference.setEnabled(false);
            return;
        }
        this.mPreference.setEntries(strArr);
        if (TextUtils.isEmpty(this.mCurrentRouteValue)) {
            int seRoute = getSeRoute();
            Log.d("NfcSeRoute", "get wallet as :" + seRoute);
            if (seRoute == 0) {
                indexOf = this.mEntryValues.indexOf("HCE");
            } else if (seRoute == 1) {
                indexOf = this.mEntryValues.indexOf("ESE");
            } else if (seRoute != 2) {
                indexOf = seRoute != 4 ? this.mEntryValues.indexOf("ESE") : this.mEntryValues.indexOf("UICC2");
            } else {
                indexOf = this.mEntryValues.indexOf("UICC");
                if (indexOf < 0) {
                    indexOf = this.mEntryValues.indexOf("UICC1");
                }
            }
        } else {
            indexOf = this.mEntryValues.indexOf(this.mCurrentRouteValue);
        }
        int currentIndex = getCurrentIndex(indexOf);
        if (currentIndex < 0) {
            Log.d("NfcSeRoute", "updatePreference called! not found any se.");
            this.mPreference.setEnabled(false);
            return;
        }
        setSeRoute(getMappingSeRoute(this.mEntryValues.get(currentIndex).toString()));
        this.mPreference.setValueIndex(currentIndex);
        this.mPreference.setSummary(this.mSummaries.get(currentIndex));
        this.mPreference.setEnabled(true);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (isNfcSeRouteSupported()) {
            this.mPreference.setEnabled(false);
            this.mCurrentRouteValue = (String) obj;
            Log.i("NfcSeRoute", "Set wallet before :" + this.mCurrentRouteValue);
            int mappingSeRoute = getMappingSeRoute(this.mCurrentRouteValue);
            Log.i("NfcSeRoute", "Set wallet as :" + mappingSeRoute);
            setSeRoute(mappingSeRoute);
            updatePreference();
        }
        return false;
    }

    public void pause() {
    }

    public void resume() {
        if (isNfcSeRouteSupported() && this.mPreference.isEnabled()) {
            updatePreference();
        }
    }
}
