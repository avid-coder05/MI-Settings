package com.android.settingslib.inputmethod;

import android.content.ContentResolver;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class InputMethodSettingValuesWrapper {
    private static final String TAG = "InputMethodSettingValuesWrapper";
    private static volatile InputMethodSettingValuesWrapper sInstance;
    private final ContentResolver mContentResolver;
    private final InputMethodManager mImm;
    private final ArrayList<InputMethodInfo> mMethodList = new ArrayList<>();

    private InputMethodSettingValuesWrapper(Context context) {
        this.mContentResolver = context.getContentResolver();
        this.mImm = (InputMethodManager) context.getSystemService(InputMethodManager.class);
        refreshAllInputMethodAndSubtypes();
    }

    private ArrayList<InputMethodInfo> getEnabledInputMethodList() {
        HashMap<String, HashSet<String>> enabledInputMethodsAndSubtypeList = InputMethodAndSubtypeUtil.getEnabledInputMethodsAndSubtypeList(this.mContentResolver);
        ArrayList<InputMethodInfo> arrayList = new ArrayList<>();
        Iterator<InputMethodInfo> it = this.mMethodList.iterator();
        while (it.hasNext()) {
            InputMethodInfo next = it.next();
            if (enabledInputMethodsAndSubtypeList.keySet().contains(next.getId())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static InputMethodSettingValuesWrapper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TAG) {
                if (sInstance == null) {
                    sInstance = new InputMethodSettingValuesWrapper(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public CharSequence getCurrentInputMethodName(Context context) {
        ArrayList<InputMethodInfo> enabledInputMethodList = getEnabledInputMethodList();
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "default_input_method", UserHandle.myUserId());
        if (TextUtils.isEmpty(stringForUser)) {
            return "";
        }
        for (InputMethodInfo inputMethodInfo : enabledInputMethodList) {
            if (stringForUser.equals(inputMethodInfo.getId())) {
                return getImeAndSubtypeDisplayName(context, inputMethodInfo, this.mImm.getCurrentInputMethodSubtype());
            }
        }
        Log.w(TAG, "Invalid selected imi: " + stringForUser);
        return "";
    }

    public CharSequence getImeAndSubtypeDisplayName(Context context, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
        String str;
        CharSequence loadLabel = inputMethodInfo.loadLabel(context.getPackageManager());
        if (inputMethodSubtype != null) {
            CharSequence[] charSequenceArr = new CharSequence[2];
            charSequenceArr[0] = inputMethodSubtype.getDisplayName(context, inputMethodInfo.getPackageName(), inputMethodInfo.getServiceInfo().applicationInfo);
            if (TextUtils.isEmpty(loadLabel)) {
                str = "";
            } else {
                str = " - " + ((Object) loadLabel);
            }
            charSequenceArr[1] = str;
            return TextUtils.concat(charSequenceArr);
        }
        return loadLabel;
    }

    public List<InputMethodInfo> getInputMethodList() {
        return new ArrayList(this.mMethodList);
    }

    public boolean isAlwaysCheckedIme(InputMethodInfo inputMethodInfo) {
        return getEnabledInputMethodList().size() <= 1 && isEnabledImi(inputMethodInfo);
    }

    public boolean isEnabledImi(InputMethodInfo inputMethodInfo) {
        Iterator<InputMethodInfo> it = getEnabledInputMethodList().iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(inputMethodInfo.getId())) {
                return true;
            }
        }
        return false;
    }

    public void refreshAllInputMethodAndSubtypes() {
        this.mMethodList.clear();
        this.mMethodList.addAll(this.mImm.getInputMethodList());
    }
}
