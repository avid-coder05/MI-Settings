package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes.dex */
public class BluetoothAudioCodecPreferenceController extends AbstractBluetoothA2dpPreferenceController {
    private final int SOURCE_CODEC_TYPE_LHDCV1;
    private final int SOURCE_CODEC_TYPE_LHDCV2;
    private final int SOURCE_CODEC_TYPE_LHDCV3;

    public BluetoothAudioCodecPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
        this.SOURCE_CODEC_TYPE_LHDCV2 = 9;
        this.SOURCE_CODEC_TYPE_LHDCV3 = 10;
        this.SOURCE_CODEC_TYPE_LHDCV1 = 11;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getCurrentA2dpSettingIndex(BluetoothCodecConfig bluetoothCodecConfig) {
        int codecType = bluetoothCodecConfig.getCodecType();
        if (codecType != 0) {
            if (codecType != 1) {
                if (codecType != 2) {
                    if (codecType != 3) {
                        if (codecType != 4) {
                            if (codecType != 100) {
                                if (codecType != 101) {
                                    switch (codecType) {
                                        case 9:
                                            return 9;
                                        case 10:
                                            return 8;
                                        case 11:
                                            return 10;
                                        default:
                                            return 0;
                                    }
                                }
                                return 7;
                            }
                            return 6;
                        }
                        return 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected int getDefaultIndex() {
        return 0;
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListSummaries() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_summaries);
    }

    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    protected String[] getListValues() {
        return this.mContext.getResources().getStringArray(R.array.bluetooth_a2dp_codec_titles);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_select_a2dp_codec";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0082  */
    @Override // com.android.settings.development.AbstractBluetoothA2dpPreferenceController
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void writeConfigurationValues(java.lang.Object r12) {
        /*
            r11 = this;
            com.android.settingslib.miuisettings.preference.miuix.DropDownPreference r0 = r11.mPreference
            java.lang.String r12 = r12.toString()
            int r12 = r0.findIndexOfValue(r12)
            r0 = 11
            r1 = 9
            r2 = 10
            r3 = 101(0x65, float:1.42E-43)
            r4 = 100
            r5 = 4
            r6 = 3
            r7 = 2
            r8 = 1
            r9 = 0
            r10 = 1000000(0xf4240, float:1.401298E-39)
            switch(r12) {
                case 0: goto L62;
                case 1: goto L5f;
                case 2: goto L5d;
                case 3: goto L5b;
                case 4: goto L59;
                case 5: goto L57;
                case 6: goto L55;
                case 7: goto L53;
                case 8: goto L51;
                case 9: goto L4f;
                case 10: goto L60;
                case 11: goto L38;
                case 12: goto L21;
                default: goto L1f;
            }
        L1f:
            goto L82
        L21:
            com.android.settings.development.BluetoothA2dpConfigStore r12 = r11.mBluetoothA2dpConfigStore
            monitor-enter(r12)
            android.bluetooth.BluetoothA2dp r0 = r11.mBluetoothA2dp     // Catch: java.lang.Throwable -> L35
            if (r0 == 0) goto L33
            android.bluetooth.BluetoothDevice r0 = r0.getActiveDevice()     // Catch: java.lang.Throwable -> L35
            if (r0 == 0) goto L33
            android.bluetooth.BluetoothA2dp r11 = r11.mBluetoothA2dp     // Catch: java.lang.Throwable -> L35
            r11.disableOptionalCodecs(r0)     // Catch: java.lang.Throwable -> L35
        L33:
            monitor-exit(r12)     // Catch: java.lang.Throwable -> L35
            return
        L35:
            r11 = move-exception
            monitor-exit(r12)     // Catch: java.lang.Throwable -> L35
            throw r11
        L38:
            com.android.settings.development.BluetoothA2dpConfigStore r12 = r11.mBluetoothA2dpConfigStore
            monitor-enter(r12)
            android.bluetooth.BluetoothA2dp r0 = r11.mBluetoothA2dp     // Catch: java.lang.Throwable -> L4c
            if (r0 == 0) goto L4a
            android.bluetooth.BluetoothDevice r0 = r0.getActiveDevice()     // Catch: java.lang.Throwable -> L4c
            if (r0 == 0) goto L4a
            android.bluetooth.BluetoothA2dp r11 = r11.mBluetoothA2dp     // Catch: java.lang.Throwable -> L4c
            r11.enableOptionalCodecs(r0)     // Catch: java.lang.Throwable -> L4c
        L4a:
            monitor-exit(r12)     // Catch: java.lang.Throwable -> L4c
            return
        L4c:
            r11 = move-exception
            monitor-exit(r12)     // Catch: java.lang.Throwable -> L4c
            throw r11
        L4f:
            r0 = r1
            goto L60
        L51:
            r0 = r2
            goto L60
        L53:
            r0 = r3
            goto L60
        L55:
            r0 = r4
            goto L60
        L57:
            r0 = r5
            goto L60
        L59:
            r0 = r6
            goto L60
        L5b:
            r0 = r7
            goto L60
        L5d:
            r0 = r8
            goto L60
        L5f:
            r0 = r9
        L60:
            r9 = r10
            goto L83
        L62:
            com.android.settingslib.miuisettings.preference.miuix.DropDownPreference r12 = r11.mPreference
            java.lang.String r12 = r12.getValue()
            com.android.settingslib.miuisettings.preference.miuix.DropDownPreference r10 = r11.mPreference
            int r12 = r10.findIndexOfValue(r12)
            switch(r12) {
                case 1: goto L82;
                case 2: goto L80;
                case 3: goto L7e;
                case 4: goto L7c;
                case 5: goto L7a;
                case 6: goto L78;
                case 7: goto L76;
                case 8: goto L74;
                case 9: goto L72;
                case 10: goto L83;
                default: goto L71;
            }
        L71:
            goto L82
        L72:
            r0 = r1
            goto L83
        L74:
            r0 = r2
            goto L83
        L76:
            r0 = r3
            goto L83
        L78:
            r0 = r4
            goto L83
        L7a:
            r0 = r5
            goto L83
        L7c:
            r0 = r6
            goto L83
        L7e:
            r0 = r7
            goto L83
        L80:
            r0 = r8
            goto L83
        L82:
            r0 = r9
        L83:
            com.android.settings.development.BluetoothA2dpConfigStore r12 = r11.mBluetoothA2dpConfigStore
            r12.setCodecType(r0)
            com.android.settings.development.BluetoothA2dpConfigStore r11 = r11.mBluetoothA2dpConfigStore
            r11.setCodecPriority(r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.BluetoothAudioCodecPreferenceController.writeConfigurationValues(java.lang.Object):void");
    }
}
