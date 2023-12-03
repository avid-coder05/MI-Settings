package com.android.settings.inputmethod;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.UserDictionary;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

/* loaded from: classes.dex */
public class UserDictionaryListPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart {
    public static final String USER_DICTIONARY_SETTINGS_INTENT_ACTION = "android.settings.USER_DICTIONARY_SETTINGS";
    private final String KEY_ALL_LANGUAGE;
    private String mLocale;
    private PreferenceScreen mScreen;

    public UserDictionaryListPreferenceController(Context context, String str) {
        super(context, str);
        this.KEY_ALL_LANGUAGE = "all_languages";
    }

    private void createUserDictSettings() {
        TreeSet<String> userDictLocalesSet = getUserDictLocalesSet(this.mContext);
        int preferenceCount = this.mScreen.getPreferenceCount();
        String str = this.mLocale;
        if (str != null) {
            userDictLocalesSet.add(str);
        }
        if (userDictLocalesSet.size() > 1) {
            userDictLocalesSet.add("");
        }
        if (preferenceCount > 0) {
            for (int i = preferenceCount - 1; i >= 0; i--) {
                String key = this.mScreen.getPreference(i).getKey();
                if (!TextUtils.isEmpty(key) && !TextUtils.equals("all_languages", key)) {
                    if (userDictLocalesSet.isEmpty() || !userDictLocalesSet.contains(key)) {
                        PreferenceScreen preferenceScreen = this.mScreen;
                        preferenceScreen.removePreference(preferenceScreen.findPreference(key));
                    } else {
                        userDictLocalesSet.remove(key);
                    }
                }
            }
        }
        if (userDictLocalesSet.isEmpty() && preferenceCount == 0) {
            this.mScreen.addPreference(createUserDictionaryPreference(null));
            return;
        }
        Iterator<String> it = userDictLocalesSet.iterator();
        while (it.hasNext()) {
            Preference createUserDictionaryPreference = createUserDictionaryPreference(it.next());
            if (this.mScreen.findPreference(createUserDictionaryPreference.getKey()) == null) {
                this.mScreen.addPreference(createUserDictionaryPreference);
            }
        }
    }

    private Preference createUserDictionaryPreference(String str) {
        Preference preference = new Preference(this.mScreen.getContext());
        Intent intent = new Intent(USER_DICTIONARY_SETTINGS_INTENT_ACTION);
        if (str == null) {
            preference.setTitle(Locale.getDefault().getDisplayName());
            preference.setKey(Locale.getDefault().toString());
        } else {
            if (TextUtils.isEmpty(str)) {
                preference.setTitle(this.mContext.getString(R.string.user_dict_settings_all_languages));
                preference.setKey("all_languages");
                preference.setOrder(0);
            } else {
                preference.setTitle(Utils.createLocaleFromString(str).getDisplayName());
                preference.setKey(str);
            }
            intent.putExtra("locale", str);
            preference.getExtras().putString("locale", str);
        }
        preference.setIntent(intent);
        preference.setFragment(UserDictionarySettings.class.getName());
        return preference;
    }

    public static TreeSet<String> getUserDictionaryLocalesSet(Context context) {
        Cursor query = context.getContentResolver().query(UserDictionary.Words.CONTENT_URI, new String[]{"locale"}, null, null, null);
        TreeSet<String> treeSet = new TreeSet<>();
        if (query == null) {
            return treeSet;
        }
        try {
            if (query.moveToFirst()) {
                int columnIndex = query.getColumnIndex("locale");
                do {
                    String string = query.getString(columnIndex);
                    if (string == null) {
                        string = "";
                    }
                    treeSet.add(string);
                } while (query.moveToNext());
            }
            query.close();
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService("input_method");
            Iterator<InputMethodInfo> it = inputMethodManager.getEnabledInputMethodList().iterator();
            while (it.hasNext()) {
                Iterator<InputMethodSubtype> it2 = inputMethodManager.getEnabledInputMethodSubtypeList(it.next(), true).iterator();
                while (it2.hasNext()) {
                    String locale = it2.next().getLocale();
                    if (!TextUtils.isEmpty(locale)) {
                        treeSet.add(locale);
                    }
                }
            }
            if (!treeSet.contains(Locale.getDefault().getLanguage())) {
                treeSet.add(Locale.getDefault().toString());
            }
            return treeSet;
        } catch (Throwable th) {
            query.close();
            throw th;
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.setOrderingAsAdded(false);
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    TreeSet<String> getUserDictLocalesSet(Context context) {
        return getUserDictionaryLocalesSet(context);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        createUserDictSettings();
    }

    public void setLocale(String str) {
        this.mLocale = str;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
