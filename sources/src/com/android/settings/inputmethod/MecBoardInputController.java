package src.com.android.settings.inputmethod;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.inputmethod.InputMethodFunctionSelectUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.util.ToastUtil;

/* loaded from: classes5.dex */
public class MecBoardInputController extends BasePreferenceController {
    private static final String TAG = "MecBoardInputController";

    public MecBoardInputController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (InputMethodFunctionSelectUtils.isSupportMechKeyboard(this.mContext)) {
            return InputMethodFunctionSelectUtils.isMechKeyboardUsable(this.mContext) ? 0 : 5;
        }
        return 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!this.mPreferenceKey.equals(preference.getKey()) || this.mContext == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        Intent intent = new Intent();
        String currentInputMethod = InputMethodFunctionSelectUtils.getCurrentInputMethod(this.mContext);
        currentInputMethod.hashCode();
        char c = 65535;
        switch (currentInputMethod.hashCode()) {
            case -1136343887:
                if (currentInputMethod.equals("com.baidu.input_mi")) {
                    c = 0;
                    break;
                }
                break;
            case 501979550:
                if (currentInputMethod.equals("com.iflytek.inputmethod.miui")) {
                    c = 1;
                    break;
                }
                break;
            case 696092339:
                if (currentInputMethod.equals("com.sohu.inputmethod.sogou.xiaomi")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                intent.setClassName(currentInputMethod, "com.baidu.input.mechanicalkb.MlKbActivity");
                break;
            case 1:
                intent.setClassName(currentInputMethod, "com.iflytek.inputmethod.mechanical.view.MecSkinActivity");
                break;
            case 2:
                intent.setClassName(currentInputMethod, "com.sohu.inputmethod.settings.activity.MecSkinSettingsActivity");
                break;
            default:
                ToastUtil.show(this.mContext, R.string.mechanical_ime_hint, 0);
                return false;
        }
        try {
            this.mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "onPreferenceTreeClick: " + e.toString());
        }
        return true;
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
