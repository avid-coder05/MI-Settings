package com.android.settings.projection;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.special.GameBoosterController;
import com.android.settings.widget.ImagePreference;
import java.net.URISyntaxException;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes2.dex */
public class ScreenProjectionExampleFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    private Intent getDocumentProjectionIntent() {
        if (0 != 0) {
            Intent intent = new Intent("com.mi.android.globalFileexplorer.action.FILE_CATEGORY");
            intent.putExtra("inner_from", YellowPageContract.Settings.DIRECTORY);
            intent.putExtra("file_category", 4);
            return intent;
        }
        Intent intent2 = new Intent("android.intent.action.MAIN");
        intent2.setPackage("com.android.fileexplorer");
        intent2.putExtra("extraTabIndex", 2);
        return intent2;
    }

    private Intent getGameProjectionIntent() {
        try {
            return Intent.parseUri(GameBoosterController.JUMP_GAME_ACTION, 0);
        } catch (URISyntaxException unused) {
            Log.e("ScreenProjectionExampleFragment", "URI invalid");
            return null;
        }
    }

    private boolean isIntentValid(Intent intent) {
        return intent != null && MiuiUtils.isIntentActivityExistAsUser(getContext(), intent, UserHandle.myUserId());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.screen_projection_examples);
        ((ImagePreference) findPreference("pref_key_screen_projection_example_document")).setContentClickListener(this);
        ImagePreference imagePreference = (ImagePreference) findPreference("pref_key_screen_projection_example_game");
        if (MiuiUtils.isSecondSpace(getContext())) {
            removePreference("pref_key_screen_projection_example_game");
        } else {
            imagePreference.setContentClickListener(this);
        }
        ((ImagePreference) findPreference("pref_key_screen_projection_example_gallery")).setContentClickListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Intent documentProjectionIntent = key.equals("pref_key_screen_projection_example_document") ? getDocumentProjectionIntent() : key.equals("pref_key_screen_projection_example_game") ? getGameProjectionIntent() : key.equals("pref_key_screen_projection_example_gallery") ? new Intent("com.miui.gallery.action.VIEW_ALBUM") : null;
        if (isIntentValid(documentProjectionIntent)) {
            startActivity(documentProjectionIntent);
            return true;
        }
        return true;
    }
}
