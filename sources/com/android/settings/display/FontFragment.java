package com.android.settings.display;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.SparseArray;
import androidx.preference.Preference;
import com.android.settings.FontSizePreference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.ArrayList;
import miui.app.constants.ThemeManagerConstants;
import miui.os.Build;
import miui.provider.ThemeRuntimeDataContract;

@Deprecated
/* loaded from: classes.dex */
public class FontFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static SparseArray<Integer> sUiModeOrder;
    private int mSelectedOrder;
    private ArrayList<FontSizePreference> mUiModes = new ArrayList<>();

    static {
        SparseArray<Integer> sparseArray = new SparseArray<>();
        sUiModeOrder = sparseArray;
        sparseArray.put(1, 0);
        sUiModeOrder.put(13, 1);
        sUiModeOrder.put(14, 2);
        sUiModeOrder.put(15, 3);
        sUiModeOrder.put(11, 4);
    }

    public static String getCurrentUsingFontName(Context context) {
        Cursor cursor;
        Throwable th;
        try {
            cursor = context.getContentResolver().query(Uri.parse("content://com.android.thememanager.provider/" + ThemeManagerConstants.COMPONENT_CODE_FONT), new String[]{ThemeRuntimeDataContract.Projection.RESOURCE_NAME}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst() && cursor.getCount() != 0) {
                        String string = cursor.getString(0);
                        cursor.close();
                        return string;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th3) {
            cursor = null;
            th = th3;
        }
    }

    public CharSequence getFontSizeSummary(Context context) {
        String string = context.getString(R.string.font_size_summary);
        if (TextUtils.equals(Build.getRegion(), "CN")) {
            String string2 = context.getString(R.string.font_size_summary1);
            String string3 = context.getString(R.string.font_size_summary2);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) (string + string2 + string3));
            int length = string.length() + string2.length();
            int length2 = spannableStringBuilder.length();
            spannableStringBuilder.setSpan(new URLSpan(""), length, length2, 33);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.intent_highlight_text)), length, length2, 33);
            return spannableStringBuilder;
        }
        return string;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return FontFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setData(Uri.parse("theme://zhuti.xiaomi.com/list?S.REQUEST_RESOURCE_CODE=fonts&miback=true&miref=" + getActivity().getPackageName()));
        intent.putExtra(":miui:starting_window_label", "");
        addPreferencesFromResource(R.xml.font_settings2);
        ValuePreference valuePreference = (ValuePreference) findPreference("preferred_font");
        if (MiuiUtils.getInstance().canFindActivity(getActivity(), intent)) {
            valuePreference.setIntent(intent);
        }
        valuePreference.setValue(getCurrentUsingFontName(getActivity()));
        valuePreference.setShowRightArrow(true);
        Preference findPreference = findPreference("font_size_summary");
        if (Build.IS_HONGMI) {
            getPreferenceScreen().removePreference(findPreference);
        } else {
            findPreference.setSummary(getFontSizeSummary(getActivity()));
        }
        Integer num = null;
        try {
            Configuration configuration = ActivityManagerNative.getDefault().getConfiguration();
            if (configuration != null) {
                num = sUiModeOrder.get(configuration.uiMode & 15);
            }
        } catch (RemoteException unused) {
        }
        this.mSelectedOrder = num != null ? num.intValue() : 1;
        Resources resources = getResources();
        String[] stringArray = resources.getStringArray(R.array.font_size_title);
        resources.getIntArray(R.array.font_size_preference_preview_size);
        if (num != null) {
            ValuePreference valuePreference2 = (ValuePreference) findPreference("preferred_page_layout");
            valuePreference2.setValue(stringArray[num.intValue()]);
            if (Build.IS_INTERNATIONAL_BUILD && !SettingsFeatures.checkGlobalFontSettingEnable(getContext())) {
                valuePreference2.setTitle(R.string.title_layout_current2);
            }
            valuePreference2.setShowRightArrow(true);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int order = preference.getOrder();
        int i = this.mSelectedOrder;
        if (order != i) {
            this.mUiModes.get(i).setChecked(false);
            this.mSelectedOrder = order;
            SparseArray<Integer> sparseArray = sUiModeOrder;
            return LargeFontUtils.sendUiModeChangeMessage(getActivity(), sparseArray.keyAt(sparseArray.indexOfValue(Integer.valueOf(order))));
        }
        return false;
    }
}
