package com.android.wifitrackerlib;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.PasspointR1Provider;
import com.android.settingslib.wifi.PasspointUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes2.dex */
public class PasspointUtilsStub {
    private static volatile PasspointUtilsStub mInstance;
    private static volatile Class mPasspointUtils;
    private static volatile IPasspointUtils mUtils;

    static {
        try {
            mPasspointUtils = PasspointUtils.class;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PasspointUtilsStub(Context context) {
        try {
            if (mPasspointUtils != null) {
                mUtils = (IPasspointUtils) mPasspointUtils.getConstructor(Context.class).newInstance(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final PasspointUtilsStub getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PasspointUtilsStub.class) {
                if (mInstance == null) {
                    mInstance = new PasspointUtilsStub(context);
                }
            }
        }
        return mInstance;
    }

    public Map<PasspointR1Provider, PasspointConfiguration> getMatchingPasspointConfigsForPasspointR1Providers(Set<PasspointR1Provider> set) {
        return mUtils != null ? mUtils.getMatchingPasspointConfigsForPasspointR1Providers(set) : new HashMap();
    }

    public Map<PasspointR1Provider, List<ScanResult>> getMatchingPasspointR1Providers(List<ScanResult> list) {
        return mUtils != null ? mUtils.getMatchingPasspointR1Providers(list) : new HashMap();
    }
}
