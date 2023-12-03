package com.android.settings.sim;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.ObservableActivity;
import java.util.List;

/* loaded from: classes2.dex */
public class SimDialogActivity extends ObservableActivity {
    public static String DIALOG_TYPE_KEY = "dialog_type";
    public static String PREFERRED_SIM = "preferred_sim";
    public static String RESULT_SUB_ID = "result_sub_id";
    private static String TAG = "SimDialogActivity";

    private SimDialogFragment createFragment(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        if (getIntent().hasExtra(PREFERRED_SIM)) {
                            return PreferredSimDialogFragment.newInstance();
                        }
                        throw new IllegalArgumentException("Missing required extra " + PREFERRED_SIM);
                    } else if (i == 4) {
                        return SimListDialogFragment.newInstance(i, R.string.select_sim_for_sms, false);
                    } else {
                        throw new IllegalArgumentException("Invalid dialog type " + i + " sent.");
                    }
                }
                return SimListDialogFragment.newInstance(i, R.string.select_sim_for_sms, true);
            }
            return SimListDialogFragment.newInstance(i, R.string.select_sim_for_calls, true);
        }
        return SimListDialogFragment.newInstance(i, R.string.select_sim_for_data, false);
    }

    private void setDefaultCallsSubId(int i) {
        ((TelecomManager) getSystemService(TelecomManager.class)).setUserSelectedOutgoingPhoneAccount(subscriptionIdToPhoneAccountHandle(i));
    }

    private void setDefaultDataSubId(int i) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(SubscriptionManager.class);
        TelephonyManager createForSubscriptionId = ((TelephonyManager) getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        subscriptionManager.setDefaultDataSubId(i);
        createForSubscriptionId.setDataEnabled(true);
        Toast.makeText(this, R.string.data_switch_started, 1).show();
    }

    private void setDefaultSmsSubId(int i) {
        ((SubscriptionManager) getSystemService(SubscriptionManager.class)).setDefaultSmsSubId(i);
    }

    private void setPreferredSim(int i) {
        setDefaultDataSubId(i);
        setDefaultSmsSubId(i);
        setDefaultCallsSubId(i);
    }

    private void showOrUpdateDialog() {
        int intExtra = getIntent().getIntExtra(DIALOG_TYPE_KEY, -1);
        if (intExtra == 5) {
            finishAndRemoveTask();
            return;
        }
        String num = Integer.toString(intExtra);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        SimDialogFragment simDialogFragment = (SimDialogFragment) supportFragmentManager.findFragmentByTag(num);
        if (simDialogFragment == null) {
            createFragment(intExtra).show(supportFragmentManager, num);
        } else {
            simDialogFragment.updateDialog();
        }
    }

    private PhoneAccountHandle subscriptionIdToPhoneAccountHandle(int i) {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TelecomManager.class);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TelephonyManager.class);
        for (PhoneAccountHandle phoneAccountHandle : telecomManager.getCallCapablePhoneAccounts()) {
            if (i == telephonyManager.getSubscriptionId(phoneAccountHandle)) {
                return phoneAccountHandle;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        showOrUpdateDialog();
    }

    public void onFragmentDismissed(SimDialogFragment simDialogFragment) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 1 && fragments.get(0) == simDialogFragment) {
            finishAndRemoveTask();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showOrUpdateDialog();
    }

    public void onSubscriptionSelected(int i, int i2) {
        if (getSupportFragmentManager().findFragmentByTag(Integer.toString(i)) == null) {
            Log.w(TAG, "onSubscriptionSelected ignored because stored fragment was null");
        } else if (i == 0) {
            setDefaultDataSubId(i2);
        } else if (i == 1) {
            setDefaultCallsSubId(i2);
        } else if (i == 2) {
            setDefaultSmsSubId(i2);
        } else if (i == 3) {
            setPreferredSim(i2);
        } else if (i != 4) {
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra(RESULT_SUB_ID, i2);
            setResult(-1, intent);
        }
    }
}
