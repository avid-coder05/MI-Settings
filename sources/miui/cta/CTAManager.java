package miui.cta;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.Log;
import com.miui.internal.util.DeviceHelper;
import com.miui.internal.util.PackageConstants;
import com.miui.system.internal.R;
import java.util.ArrayList;
import miui.cta.CTAConfig;
import miui.util.ResourceHelper;

/* loaded from: classes3.dex */
public class CTAManager {
    private static final String CTA_CONFIG_NAME = "miui_cta";
    private static final String EXTRA_KEY_ACCEPT = "extra_accept";
    private static final String INTENT_ACTION_ACCEPT_CHANGED_SUFFIX = ".intent.action.ACCEPT_CHANGED";
    private static final String META_KEY_CTA = "com.miui.system.cta";
    private static final String TAG = "CTAManager";
    private String mAcceptChangedAction;
    private boolean mAccepted;
    private CTAConfig mConfig;
    private ArrayList<CTAListener> mListeners;

    /* loaded from: classes3.dex */
    public interface CTAListener {
        void onAccept();

        void onReject();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CTAReceiver extends BroadcastReceiver {
        private CTAReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean booleanExtra;
            if (!CTAManager.this.mAcceptChangedAction.equals(intent.getAction()) || (booleanExtra = intent.getBooleanExtra(CTAManager.EXTRA_KEY_ACCEPT, false)) == CTAManager.this.mAccepted) {
                return;
            }
            CTAManager.this.mAccepted = booleanExtra;
            if (CTAManager.this.mAccepted) {
                CTAManager.this.notifyAccept();
            } else {
                CTAManager.this.notifyReject();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class Holder {
        static final CTAManager INSTANCE = new CTAManager(PackageConstants.getCurrentApplication());

        private Holder() {
        }
    }

    private CTAManager(Context context) {
        this.mAcceptChangedAction = context.getPackageName() + INTENT_ACTION_ACCEPT_CHANGED_SUFFIX;
        this.mListeners = new ArrayList<>();
        registerReceiver(context);
        initialize(context);
    }

    public static CTAManager getInstance() {
        return Holder.INSTANCE;
    }

    private CTAListener[] getListenersCopy() {
        CTAListener[] cTAListenerArr;
        synchronized (this.mListeners) {
            cTAListenerArr = new CTAListener[this.mListeners.size()];
            this.mListeners.toArray(cTAListenerArr);
        }
        return cTAListenerArr;
    }

    private void initialize(Context context) {
        boolean z = DeviceHelper.IS_INTERNATIONAL_BUILD || CTAPreference.isAccepted(context);
        this.mAccepted = z;
        if (z) {
            return;
        }
        loadConfig(context);
        CTAConfig cTAConfig = this.mConfig;
        if (cTAConfig == null) {
            this.mAccepted = true;
            return;
        }
        boolean z2 = !cTAConfig.canMatch();
        this.mAccepted = z2;
        if (z2) {
            CTAPreference.setAccepted(context, true);
        }
    }

    private void loadConfig(Context context) {
        XmlResourceParser loadXml = ResourceHelper.loadXml(context, META_KEY_CTA, CTA_CONFIG_NAME);
        if (loadXml == null) {
            this.mConfig = CTAConfig.EMPTY;
            return;
        }
        this.mConfig = new CTAConfig(context, loadXml);
        loadXml.close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAccept() {
        for (CTAListener cTAListener : getListenersCopy()) {
            cTAListener.onAccept();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyReject() {
        for (CTAListener cTAListener : getListenersCopy()) {
            cTAListener.onReject();
        }
    }

    private void registerReceiver(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (applicationContext != null) {
            context = applicationContext;
        }
        context.registerReceiver(new CTAReceiver(), new IntentFilter(this.mAcceptChangedAction));
    }

    private void sendBroadcast() {
        Intent intent = new Intent(this.mAcceptChangedAction);
        intent.putExtra(EXTRA_KEY_ACCEPT, this.mAccepted);
        PackageConstants.getCurrentApplication().sendBroadcast(intent);
    }

    private void showAgreementDialog(Activity activity, String str, boolean z) {
        if (((CTADialogFragment) activity.getFragmentManager().findFragmentByTag("CTADialog")) == null) {
            new CTADialogFragment().showDialog(activity, str, z);
        }
    }

    public static void showAgreementIfNeed(Activity activity) {
        CTAManager cTAManager = getInstance();
        if (cTAManager.isAccepted()) {
            return;
        }
        cTAManager.showAgreement(activity);
    }

    public void addListener(CTAListener cTAListener) {
        synchronized (this.mListeners) {
            this.mListeners.add(cTAListener);
        }
    }

    public boolean isAccepted() {
        return this.mAccepted;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAccept(Activity activity) {
        this.mAccepted = true;
        CTAPreference.setAccepted(activity, true);
        notifyAccept();
        sendBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onReject() {
        notifyReject();
        sendBroadcast();
    }

    public void removeListener(CTAListener cTAListener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(cTAListener);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void showAgreement(Activity activity) {
        CTAConfig.MatchResult match = this.mConfig.match(activity.getClass());
        if (match == null) {
            return;
        }
        int i = match.messageId;
        String charSequence = i == 0 ? null : activity.getText(i).toString();
        if (TextUtils.isEmpty(charSequence)) {
            String message = CTAPermission.getMessage(activity, match.permission);
            if (message == null) {
                Log.e(TAG, "Fail to show agreement for permission message is empty");
                return;
            }
            charSequence = activity.getString(R.string.cta_message_permission, new Object[]{activity.getString(activity.getApplicationInfo().labelRes), message});
        }
        showAgreementDialog(activity, charSequence, match.optional);
    }
}
