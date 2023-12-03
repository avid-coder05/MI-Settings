package miui.telephony;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import com.miui.internal.telephony.SubscriptionManagerAndroidImpl;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.reflect.Method;

/* loaded from: classes4.dex */
public abstract class SubscriptionManager {
    protected static final String LOG_TAG = "SubMgr";
    public static final int SLOT_ID_1 = 0;
    public static final int SLOT_ID_2 = 1;
    public static final int INVALID_SUBSCRIPTION_ID = ConstantsDefiner.getInvalidSubscriptionIdConstant();
    public static final int INVALID_PHONE_ID = ConstantsDefiner.getInvalidPhoneIdConstant();
    public static final int INVALID_SLOT_ID = ConstantsDefiner.getInvalidSlotIdConstant();
    public static final int DEFAULT_SUBSCRIPTION_ID = ConstantsDefiner.getDefaultSubscriptionIdConstant();
    public static final int DEFAULT_PHONE_ID = ConstantsDefiner.getDefaultPhoneIdConstant();
    public static final int DEFAULT_SLOT_ID = ConstantsDefiner.getDefaultSlotIdConstant();
    public static final String SUBSCRIPTION_KEY = ConstantsDefiner.getSubscriptionKeyConstant();
    public static final String PHONE_KEY = ConstantsDefiner.getPhoneKeyConstant();
    public static final String SLOT_KEY = ConstantsDefiner.getSlotKeyConstant();
    private ArrayList<OnSubscriptionsChangedListener> mListeners = null;
    private Object mLock = new Object();
    private List<SubscriptionInfo> mInsertedSubscriptionInfos = null;
    private boolean mSubscriptionsCacheEnabled = false;

    /* loaded from: classes4.dex */
    static class ConstantsDefiner {
        private static final String PHONE_ID = "phone_id";
        private static final String SLOT_ID = "slot_id";
        private static final String SUBSCRIPTION_ID = "subscription_id";

        private ConstantsDefiner() {
        }

        static int getDefaultPhoneIdConstant() {
            return Integer.MAX_VALUE;
        }

        static int getDefaultSlotIdConstant() {
            return Integer.MAX_VALUE;
        }

        static int getDefaultSubscriptionIdConstant() {
            return Integer.MAX_VALUE;
        }

        static int getInvalidPhoneIdConstant() {
            return -1;
        }

        static int getInvalidSlotIdConstant() {
            return -1;
        }

        static int getInvalidSubscriptionIdConstant() {
            return -1;
        }

        static String getPhoneKeyConstant() {
            return PHONE_ID;
        }

        static String getSlotKeyConstant() {
            return "slot_id";
        }

        static String getSubscriptionKeyConstant() {
            return "subscription_id";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class Holder {
        static final SubscriptionManager INSTANCE;

        static {
            INSTANCE = Build.IS_MIUI ? getMiuiSubscriptionManager() : SubscriptionManagerAndroidImpl.getDefault();
        }

        private Holder() {
        }

        private static SubscriptionManager getMiuiSubscriptionManager() {
            try {
                Class<?> cls = Class.forName("miui.telephony.SubscriptionManagerEx");
                return (SubscriptionManager) Method.of(cls, "getDefault", cls, new Class[0]).invokeObject(cls, (Object) null, new Object[0]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /* loaded from: classes4.dex */
    public interface OnSubscriptionsChangedListener {
        void onSubscriptionsChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureSubscriptionInfoCache(boolean z) {
        boolean z2;
        if (z || this.mInsertedSubscriptionInfos == null) {
            List<SubscriptionInfo> subscriptionInfoListInternal = getSubscriptionInfoListInternal();
            this.mInsertedSubscriptionInfos = subscriptionInfoListInternal;
            if (subscriptionInfoListInternal == null) {
                this.mInsertedSubscriptionInfos = new ArrayList();
            }
            z2 = true;
        } else {
            z2 = false;
        }
        if (z2 && PhoneDebug.VDBG) {
            StringBuilder sb = new StringBuilder();
            sb.append("ensureSubscriptionInfoCache ");
            sb.append(z ? "" : "false");
            sb.append(" insert=");
            sb.append(Arrays.toString(this.mInsertedSubscriptionInfos.toArray()));
            Rlog.i(LOG_TAG, sb.toString());
        }
    }

    public static SubscriptionManager getDefault() {
        return Holder.INSTANCE;
    }

    public static int getPhoneId(Bundle bundle, int i) {
        return bundle.getInt(PHONE_KEY, i);
    }

    public static int getPhoneIdExtra(Intent intent, int i) {
        return intent.getIntExtra(PHONE_KEY, i);
    }

    public static int getSlotId(Bundle bundle, int i) {
        return bundle.getInt(SLOT_KEY, i);
    }

    public static int getSlotIdExtra(Intent intent, int i) {
        return intent.getIntExtra(SLOT_KEY, i);
    }

    public static int getSubscriptionId(Bundle bundle, int i) {
        return bundle.getInt(SUBSCRIPTION_KEY, i);
    }

    public static int getSubscriptionIdExtra(Intent intent, int i) {
        return intent.getIntExtra(SUBSCRIPTION_KEY, i);
    }

    public static boolean isRealSlotId(int i) {
        return i >= 0 && i < TelephonyManager.getDefault().getPhoneCount();
    }

    public static boolean isValidPhoneId(int i) {
        return (i >= 0 && i < TelephonyManager.getDefault().getPhoneCount()) || i == DEFAULT_PHONE_ID;
    }

    public static boolean isValidSlotId(int i) {
        return (i >= 0 && i < TelephonyManager.getDefault().getPhoneCount()) || i == DEFAULT_SLOT_ID;
    }

    public static boolean isValidSubscriptionId(int i) {
        return i > INVALID_SUBSCRIPTION_ID;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyOnSubscriptionsChangedListeners() {
        synchronized (this.mLock) {
            if (this.mListeners != null) {
                if (PhoneDebug.VDBG) {
                    Rlog.i(LOG_TAG, "notify OnSubscriptionsChangedListener size=" + this.mListeners.size());
                }
                Iterator<OnSubscriptionsChangedListener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    it.next().onSubscriptionsChanged();
                }
            }
        }
    }

    public static void putPhoneId(Bundle bundle, int i) {
        int slotIdForPhone = getDefault().getSlotIdForPhone(i);
        putSlotIdPhoneIdAndSubId(bundle, slotIdForPhone, i, getDefault().getSubscriptionIdForSlot(slotIdForPhone));
    }

    public static void putPhoneIdExtra(Intent intent, int i) {
        int slotIdForPhone = getDefault().getSlotIdForPhone(i);
        putSlotIdPhoneIdAndSubIdExtra(intent, slotIdForPhone, i, getDefault().getSubscriptionIdForSlot(slotIdForPhone));
    }

    public static void putSlotId(Bundle bundle, int i) {
        putSlotIdPhoneIdAndSubId(bundle, i, getDefault().getPhoneIdForSlot(i), getDefault().getSubscriptionIdForSlot(i));
    }

    public static void putSlotIdExtra(Intent intent, int i) {
        putSlotIdPhoneIdAndSubIdExtra(intent, i, getDefault().getPhoneIdForSlot(i), getDefault().getSubscriptionIdForSlot(i));
    }

    public static void putSlotIdPhoneIdAndSubId(Bundle bundle, int i, int i2, int i3) {
        bundle.putInt(SUBSCRIPTION_KEY, i3);
        bundle.putInt(PHONE_KEY, i2);
        bundle.putInt(SLOT_KEY, i);
    }

    public static void putSlotIdPhoneIdAndSubIdExtra(Intent intent, int i, int i2, int i3) {
        intent.putExtra(SUBSCRIPTION_KEY, i3);
        intent.putExtra(PHONE_KEY, i2);
        intent.putExtra(SLOT_KEY, i);
    }

    public static void putSubscriptionId(Bundle bundle, int i) {
        putSlotIdPhoneIdAndSubId(bundle, getDefault().getSlotIdForSubscription(i), getDefault().getPhoneIdForSubscription(i), i);
    }

    public static void putSubscriptionIdExtra(Intent intent, int i) {
        putSlotIdPhoneIdAndSubIdExtra(intent, getDefault().getSlotIdForSubscription(i), getDefault().getPhoneIdForSubscription(i), i);
    }

    public static String toSimpleString(List<SubscriptionInfo> list) {
        int size = list == null ? 0 : list.size();
        if (size > 0) {
            SubscriptionInfo[] subscriptionInfoArr = new SubscriptionInfo[size];
            list.toArray(subscriptionInfoArr);
            StringBuilder sb = new StringBuilder(size * 64);
            sb.append("[ size=");
            sb.append(size);
            for (int i = 0; i < size; i++) {
                SubscriptionInfo subscriptionInfo = subscriptionInfoArr[i];
                if (subscriptionInfo == null) {
                    Rlog.i(LOG_TAG, "toSimpleString SubscriptionInfo size was changed");
                } else {
                    sb.append(" {id=");
                    sb.append(subscriptionInfo.getSubscriptionId());
                    sb.append(" iccid=");
                    sb.append(PhoneDebug.VDBG ? subscriptionInfo.getIccId() : TelephonyUtils.pii(subscriptionInfo.getIccId()));
                    sb.append(" slot=");
                    sb.append(subscriptionInfo.getSlotId());
                    sb.append(" active=");
                    sb.append(subscriptionInfo.isActivated());
                    sb.append('}');
                }
            }
            sb.append(']');
            return sb.toString();
        }
        return "[]";
    }

    public void addOnSubscriptionsChangedListener(OnSubscriptionsChangedListener onSubscriptionsChangedListener) {
        if (PhoneDebug.VDBG) {
            Rlog.i(LOG_TAG, "addOnSubscriptionsChangedListener listener=" + onSubscriptionsChangedListener.getClass().getName());
        }
        synchronized (this.mLock) {
            if (this.mListeners == null) {
                this.mListeners = new ArrayList<>();
            }
            if (!this.mListeners.contains(onSubscriptionsChangedListener)) {
                this.mListeners.add(onSubscriptionsChangedListener);
                addOnSubscriptionsChangedListenerInternal();
            }
        }
    }

    protected abstract void addOnSubscriptionsChangedListenerInternal();

    public void disableSubscriptionsCache() {
        synchronized (this.mLock) {
            this.mSubscriptionsCacheEnabled = false;
            this.mInsertedSubscriptionInfos = null;
            ArrayList<OnSubscriptionsChangedListener> arrayList = this.mListeners;
            if (arrayList == null || arrayList.size() == 0) {
                removeOnSubscriptionsChangedListenerInternal();
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("SubscriptionManager:");
        try {
            StringBuilder sb = new StringBuilder(512);
            sb.append("mListeners=");
            sb.append('[');
            ArrayList<OnSubscriptionsChangedListener> arrayList = this.mListeners;
            if (arrayList != null) {
                Iterator<OnSubscriptionsChangedListener> it = arrayList.iterator();
                while (it.hasNext()) {
                    OnSubscriptionsChangedListener next = it.next();
                    sb.append('{');
                    sb.append(next.getClass().getName());
                    sb.append('}');
                }
            }
            sb.append(']');
            printWriter.println(sb.toString());
            printWriter.println("mInsertedSubscriptionInfos=" + this.mInsertedSubscriptionInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printWriter.flush();
    }

    public void enableSubscriptionsCache() {
        synchronized (this.mLock) {
            this.mSubscriptionsCacheEnabled = true;
            addOnSubscriptionsChangedListenerInternal();
        }
    }

    public List<SubscriptionInfo> getActiveSubscriptionInfoList() {
        ArrayList arrayList = new ArrayList();
        for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
            if (subscriptionInfo.isActivated()) {
                arrayList.add(subscriptionInfo);
            }
        }
        return arrayList;
    }

    public int getAllSubscriptionInfoCount() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getAllSubscriptionInfoList().size();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<SubscriptionInfo> getAllSubscriptionInfoList() {
        return getAllSubscriptionInfoListInternal();
    }

    protected abstract List<SubscriptionInfo> getAllSubscriptionInfoListInternal();

    public List<SubscriptionInfo> getAvailableSubscriptionInfoList() {
        return Collections.emptyList();
    }

    public abstract int getDefaultDataSlotId();

    public abstract int getDefaultDataSubscriptionId();

    public abstract SubscriptionInfo getDefaultDataSubscriptionInfo();

    public int getDefaultSlotId() {
        int defaultVoiceSubscriptionId = TelephonyManager.getDefault().isVoiceCapable() ? getDefaultVoiceSubscriptionId() : getDefaultDataSubscriptionId();
        int i = INVALID_SLOT_ID;
        if (isValidSubscriptionId(defaultVoiceSubscriptionId)) {
            i = getSlotIdForSubscription(defaultVoiceSubscriptionId);
        }
        return (!isValidSlotId(i) || i == DEFAULT_SLOT_ID) ? getDefaultSlotIdInternal() : i;
    }

    protected abstract int getDefaultSlotIdInternal();

    public int getDefaultSmsSlotId() {
        return getSlotIdForSubscription(getDefaultSmsSubscriptionId());
    }

    public abstract int getDefaultSmsSubscriptionId();

    public abstract SubscriptionInfo getDefaultSmsSubscriptionInfo();

    public int getDefaultSubscriptionId() {
        int defaultVoiceSubscriptionId = TelephonyManager.getDefault().isVoiceCapable() ? getDefaultVoiceSubscriptionId() : getDefaultDataSubscriptionId();
        return (!isValidSubscriptionId(defaultVoiceSubscriptionId) || defaultVoiceSubscriptionId == DEFAULT_SUBSCRIPTION_ID) ? getSubscriptionIdForSlot(getDefaultSlotIdInternal()) : defaultVoiceSubscriptionId;
    }

    public SubscriptionInfo getDefaultSubscriptionInfo() {
        return getSubscriptionInfoForSubscription(getDefaultSubscriptionId());
    }

    public abstract int getDefaultVoiceSlotId();

    public abstract int getDefaultVoiceSubscriptionId();

    public abstract SubscriptionInfo getDefaultVoiceSubscriptionInfo();

    public int getPhoneIdForSlot(int i) {
        return i;
    }

    public int getPhoneIdForSubscription(int i) {
        if (isValidSubscriptionId(i)) {
            int slotId = i == DEFAULT_SUBSCRIPTION_ID ? DEFAULT_PHONE_ID : getSlotId(i);
            return !isValidPhoneId(slotId) ? INVALID_PHONE_ID : slotId;
        }
        return INVALID_PHONE_ID;
    }

    protected int getSlotId(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
                if (subscriptionInfo.getSubscriptionId() == i) {
                    return subscriptionInfo.getSlotId();
                }
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return INVALID_PHONE_ID;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getSlotIdForPhone(int i) {
        return i;
    }

    public int getSlotIdForSubscription(int i) {
        if (isValidSubscriptionId(i)) {
            int slotId = i == DEFAULT_SUBSCRIPTION_ID ? DEFAULT_SLOT_ID : getSlotId(i);
            return !isValidSlotId(slotId) ? INVALID_SLOT_ID : slotId;
        }
        return INVALID_SLOT_ID;
    }

    public int getSubscriptionIdForSlot(int i) {
        if (isValidSlotId(i)) {
            if (i == DEFAULT_SLOT_ID) {
                return DEFAULT_SUBSCRIPTION_ID;
            }
            if (TelephonyManager.getDefault().hasIccCard(i)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
                        if (subscriptionInfo.getSlotId() == i) {
                            return subscriptionInfo.getSubscriptionId();
                        }
                    }
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return INVALID_SUBSCRIPTION_ID;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return INVALID_SUBSCRIPTION_ID;
        }
        return INVALID_SUBSCRIPTION_ID;
    }

    public int getSubscriptionInfoCount() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Iterator<SubscriptionInfo> it = getSubscriptionInfoList().iterator();
            int i = 0;
            while (it.hasNext()) {
                if (it.next().isActivated()) {
                    i++;
                }
            }
            return i;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public SubscriptionInfo getSubscriptionInfoForSlot(int i) {
        if (isValidSlotId(i)) {
            if (i == DEFAULT_SLOT_ID) {
                i = getDefaultSlotId();
            }
            for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
                if (subscriptionInfo.getSlotId() == i) {
                    return subscriptionInfo;
                }
            }
            return null;
        }
        return null;
    }

    public SubscriptionInfo getSubscriptionInfoForSubscription(int i) {
        if (isValidSubscriptionId(i)) {
            if (i == DEFAULT_SUBSCRIPTION_ID) {
                return getSubscriptionInfoForSlot(getDefaultSlotId());
            }
            for (SubscriptionInfo subscriptionInfo : getSubscriptionInfoList()) {
                if (subscriptionInfo.getSubscriptionId() == i) {
                    return subscriptionInfo;
                }
            }
            return null;
        }
        return null;
    }

    public List<SubscriptionInfo> getSubscriptionInfoList() {
        if (this.mSubscriptionsCacheEnabled) {
            ensureSubscriptionInfoCache(false);
            return this.mInsertedSubscriptionInfos;
        }
        List<SubscriptionInfo> subscriptionInfoListInternal = getSubscriptionInfoListInternal();
        return subscriptionInfoListInternal == null ? new ArrayList() : subscriptionInfoListInternal;
    }

    protected abstract List<SubscriptionInfo> getSubscriptionInfoListInternal();

    protected void onSubscriptionInfoChanged() {
        if (this.mSubscriptionsCacheEnabled) {
            new AsyncTask<Void, Void, Void>() { // from class: miui.telephony.SubscriptionManager.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... voidArr) {
                    SubscriptionManager.this.ensureSubscriptionInfoCache(true);
                    return null;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Void r1) {
                    SubscriptionManager.this.notifyOnSubscriptionsChangedListeners();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        } else {
            notifyOnSubscriptionsChangedListeners();
        }
    }

    public void removeOnSubscriptionsChangedListener(OnSubscriptionsChangedListener onSubscriptionsChangedListener) {
        synchronized (this.mLock) {
            ArrayList<OnSubscriptionsChangedListener> arrayList = this.mListeners;
            if (arrayList == null) {
                return;
            }
            arrayList.remove(onSubscriptionsChangedListener);
            if (this.mListeners.size() == 0) {
                this.mListeners = null;
                if (!this.mSubscriptionsCacheEnabled) {
                    removeOnSubscriptionsChangedListenerInternal();
                }
            }
        }
    }

    protected abstract void removeOnSubscriptionsChangedListenerInternal();

    public abstract void setDefaultDataSlotId(int i);

    public void setDefaultDataSubscriptionId(int i) {
        setDefaultDataSlotId(getSlotIdForSubscription(i));
    }

    public void setDefaultSmsSlotId(int i) {
        if (!isValidSlotId(i)) {
            i = INVALID_SLOT_ID;
        }
        if (i == DEFAULT_SLOT_ID || i == getDefaultSmsSlotId()) {
            return;
        }
        setDefaultSmsSubscriptionId(getSubscriptionIdForSlot(i));
    }

    public abstract void setDefaultSmsSubscriptionId(int i);

    public abstract void setDefaultVoiceSlotId(int i);

    public void setDefaultVoiceSubscriptionId(int i) {
        setDefaultVoiceSlotId(getSlotIdForSubscription(i));
    }
}
