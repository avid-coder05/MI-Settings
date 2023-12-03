package com.android.settings.network.telephony;

import android.content.Context;
import android.telephony.SignalStrength;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/* loaded from: classes2.dex */
public class SignalStrengthListener {
    private TelephonyManager mBaseTelephonyManager;
    private Callback mCallback;
    private Context mContext;
    Map<Integer, SignalStrengthTelephonyCallback> mTelephonyCallbacks = new TreeMap();

    /* loaded from: classes2.dex */
    public interface Callback {
        void onSignalStrengthChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class SignalStrengthTelephonyCallback extends TelephonyCallback implements TelephonyCallback.SignalStrengthsListener {
        SignalStrengthTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.SignalStrengthsListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            SignalStrengthListener.this.mCallback.onSignalStrengthChanged();
        }
    }

    public SignalStrengthListener(Context context, Callback callback) {
        this.mBaseTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mCallback = callback;
        this.mContext = context;
    }

    private void startListening(int i) {
        this.mBaseTelephonyManager.createForSubscriptionId(i).registerTelephonyCallback(this.mContext.getMainExecutor(), this.mTelephonyCallbacks.get(Integer.valueOf(i)));
    }

    private void stopListening(int i) {
        this.mBaseTelephonyManager.createForSubscriptionId(i).unregisterTelephonyCallback(this.mTelephonyCallbacks.get(Integer.valueOf(i)));
    }

    public void pause() {
        Iterator<Integer> it = this.mTelephonyCallbacks.keySet().iterator();
        while (it.hasNext()) {
            stopListening(it.next().intValue());
        }
    }

    public void resume() {
        Iterator<Integer> it = this.mTelephonyCallbacks.keySet().iterator();
        while (it.hasNext()) {
            startListening(it.next().intValue());
        }
    }

    public void updateSubscriptionIds(Set<Integer> set) {
        ArraySet arraySet = new ArraySet(this.mTelephonyCallbacks.keySet());
        UnmodifiableIterator it = Sets.difference(arraySet, set).iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            stopListening(intValue);
            this.mTelephonyCallbacks.remove(Integer.valueOf(intValue));
        }
        UnmodifiableIterator it2 = Sets.difference(set, arraySet).iterator();
        while (it2.hasNext()) {
            int intValue2 = ((Integer) it2.next()).intValue();
            this.mTelephonyCallbacks.put(Integer.valueOf(intValue2), new SignalStrengthTelephonyCallback());
            startListening(intValue2);
        }
    }
}
