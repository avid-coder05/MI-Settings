package com.android.settings.wifi.details2;

import java.net.InetAddress;
import java.util.function.Function;

/* loaded from: classes2.dex */
public final /* synthetic */ class WifiDetailPreferenceController2$$ExternalSyntheticLambda8 implements Function {
    public static final /* synthetic */ WifiDetailPreferenceController2$$ExternalSyntheticLambda8 INSTANCE = new WifiDetailPreferenceController2$$ExternalSyntheticLambda8();

    private /* synthetic */ WifiDetailPreferenceController2$$ExternalSyntheticLambda8() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return ((InetAddress) obj).getHostAddress();
    }
}
