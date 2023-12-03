package com.android.settings.search;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
import com.android.settings.aidl.IRemoteSearchService;
import java.util.ArrayList;
import java.util.List;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class RemoteSearchService extends Service {
    private final IRemoteSearchService.Stub mBinder = new IRemoteSearchService.Stub() { // from class: com.android.settings.search.RemoteSearchService.1
        @Override // com.android.settings.aidl.IRemoteSearchService
        public boolean change(String str, int i) {
            return i == RemoteSearchService.this.action(str, i);
        }

        @Override // com.android.settings.aidl.IRemoteSearchService
        public int enquiry(String str) {
            return RemoteSearchService.this.action(str, -1);
        }

        @Override // com.android.settings.aidl.IRemoteSearchService
        public List<RemoteSearchResult> search(String str) {
            return new ArrayList();
        }

        @Override // com.android.settings.aidl.IRemoteSearchService
        public boolean visit(String str, int i) {
            return false;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public int action(String str, int i) {
        String lowerCase = str.toLowerCase();
        for (String str2 : getString(R.string.keywords_silent).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str2)) {
                return MiuiOptionUtils$Sound.touchSilentState(this, i);
            }
        }
        for (String str3 : getString(R.string.keywords_auto_rotate).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str3)) {
                return MiuiOptionUtils$Display.touchRotationLockState(this, i);
            }
        }
        for (String str4 : getString(R.string.keywords_bluetooth).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str4)) {
                return MiuiOptionUtils$Wireless.touchBluetoothState(i);
            }
        }
        for (String str5 : getString(R.string.keywords_wifi).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str5)) {
                return MiuiOptionUtils$Wifi.touchWLANState(this, i);
            }
        }
        for (String str6 : getString(R.string.keywords_data).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str6)) {
                return MiuiOptionUtils$Mobile.touchDataState(this, i);
            }
        }
        for (String str7 : getString(R.string.keywords_torch).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str7)) {
                return changeTorch(i);
            }
        }
        for (String str8 : getString(R.string.keywords_do_not_disturb).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str8)) {
                return MiuiOptionUtils$DoNotDisturb.touchDoNotDisturbState(this, i);
            }
        }
        for (String str9 : getString(R.string.keywords_airplane).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str9)) {
                return MiuiOptionUtils$Wireless.touchAirplaneState(this, i);
            }
        }
        for (String str10 : getString(R.string.keywords_vibrate).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str10)) {
                return MiuiOptionUtils$Sound.touchVibrateState(this, i);
            }
        }
        for (String str11 : getString(R.string.keywords_gps).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str11)) {
                return MiuiOptionUtils$Wireless.touchGPSState(this, i);
            }
        }
        for (String str12 : getString(R.string.keywords_hotspot).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str12)) {
                return MiuiOptionUtils$Wifi.touchHotspotState(this, i);
            }
        }
        for (String str13 : getString(R.string.keywords_sync).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str13)) {
                return MiuiOptionUtils$Account.touchSyncState(i);
            }
        }
        for (String str14 : getString(R.string.keywords_paper_mode).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str14)) {
                return MiuiOptionUtils$Display.touchPaperModeState(this, i);
            }
        }
        for (String str15 : getString(R.string.keywords_quick_ball).toLowerCase().split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
            if (lowerCase.contains(str15)) {
                return changeQuickBall(i);
            }
        }
        return -1;
    }

    private int changeQuickBall(int i) {
        return -1;
    }

    private int changeTorch(int i) {
        int i2 = Settings.Global.getInt(getContentResolver(), "torch_state", 0);
        if (i == -1 || i == i2) {
            return i2;
        }
        Intent intent = new Intent("miui.intent.action.TOGGLE_TORCH");
        intent.putExtra("miui.intent.extra.IS_TOGGLE", true);
        sendBroadcastAsUser(intent, UserHandle.CURRENT);
        return i;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}
