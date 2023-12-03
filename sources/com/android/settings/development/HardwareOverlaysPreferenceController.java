package com.android.settings.development;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.recommend.PageIndexManager;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class HardwareOverlaysPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SURFACE_FLINGER_READ_CODE = 1010;
    private final IBinder mSurfaceFlinger;

    public HardwareOverlaysPreferenceController(Context context) {
        super(context);
        this.mSurfaceFlinger = ServiceManager.getService("SurfaceFlinger");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "disable_overlays";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        SwitchPreference switchPreference = (SwitchPreference) this.mPreference;
        if (switchPreference.isChecked()) {
            writeHardwareOverlaysSetting(false);
            switchPreference.setChecked(false);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeHardwareOverlaysSetting(((Boolean) obj).booleanValue());
        return true;
    }

    void updateHardwareOverlaysSetting() {
        if (this.mSurfaceFlinger == null) {
            return;
        }
        try {
            Parcel obtain = Parcel.obtain();
            Parcel obtain2 = Parcel.obtain();
            obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
            this.mSurfaceFlinger.transact(1010, obtain, obtain2, 0);
            obtain2.readInt();
            obtain2.readInt();
            obtain2.readInt();
            obtain2.readInt();
            ((SwitchPreference) this.mPreference).setChecked(obtain2.readInt() != 0);
            obtain2.recycle();
            obtain.recycle();
        } catch (RemoteException unused) {
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateHardwareOverlaysSetting();
    }

    void writeHardwareOverlaysSetting(boolean z) {
        if (this.mSurfaceFlinger == null) {
            return;
        }
        try {
            Parcel obtain = Parcel.obtain();
            obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
            obtain.writeInt(z ? 1 : 0);
            this.mSurfaceFlinger.transact(PageIndexManager.PAGE_FACTORY_RESET, obtain, null, 0);
            obtain.recycle();
        } catch (RemoteException unused) {
        }
        updateHardwareOverlaysSetting();
    }
}
