package miui.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import miui.util.AppConstants;
import miui.util.SoftReferenceSingleton;

/* loaded from: classes3.dex */
public class ExtraAccountManager {
    public static final String ACTION_VIEW_XIAOMI_ACCOUNT = "android.settings.XIAOMI_ACCOUNT_SYNC_SETTINGS";
    public static final int BIND_SNS_TYPE_SINA = 1;
    public static final String EXTRA_ACCOUNT = "extra_account";
    public static final String EXTRA_ADD_ACCOUNT_FROM_PROVISION = "extra_add_account_from_provision";
    public static final String EXTRA_BIND_SNS_TYPE = "extra_bind_sns_type";
    public static final String EXTRA_BUNDLE = "extra_bundle";
    public static final String EXTRA_CLEAR_WHEN_RESET = "extra_clear_when_reset";
    public static final String EXTRA_DISABLE_BACK_KEY = "extra_disable_back_key";
    public static final String EXTRA_ENTER_ACCOUNT_FORWORD_IN_PROVISION = "is_to_next_in_provision";
    public static final String EXTRA_FIND_PASSWORD_ON_PC = "extra_find_pwd_on_pc";
    public static final String EXTRA_SHOW_ACCOUNT_SETTINGS = "show_account_settings";
    public static final String EXTRA_SHOW_FIND_DEVICE = "extra_show_find_device";
    public static final String EXTRA_SHOW_SKIP_LOGIN = "extra_show_skip_login";
    public static final String EXTRA_SHOW_SYNC_SETTINGS = "show_sync_settings";
    public static final String EXTRA_UPDATE_TYPE = "extra_update_type";
    public static final String EXTRA_WIPE_DATA = "extra_wipe_data";
    public static final String EXTRA_WIPE_SYNCED_DATA = "extra_wipe_synced_data";
    private static final SoftReferenceSingleton<ExtraAccountManager> INSTANCE = new SoftReferenceSingleton<ExtraAccountManager>() { // from class: miui.accounts.ExtraAccountManager.1
        /* JADX INFO: Access modifiers changed from: protected */
        public ExtraAccountManager createInstance() {
            return new ExtraAccountManager(AppConstants.getCurrentApplication());
        }
    };
    public static final String KEY_CAPTCHA_CODE = "captcha_code";
    public static final String KEY_CAPTCHA_ICK = "captcha_ick";
    public static final String KEY_CAPTCHA_URL = "captcha_url";
    public static final String KEY_ENCRYPTED_USER_ID = "encrypted_user_id";
    public static final String KEY_SERVICE_ID = "service_id";
    public static final String KEY_TITLE = "title";
    public static final String LOGIN_ACCOUNTS_POST_CHANGED_ACTION = "android.accounts.LOGIN_ACCOUNTS_POST_CHANGED";
    public static final String LOGIN_ACCOUNTS_PRE_CHANGED_ACTION = "android.accounts.LOGIN_ACCOUNTS_PRE_CHANGED";
    private static final String TAG = "ExtraAccountManager";
    public static final int TYPE_ADD = 2;
    public static final int TYPE_REMOVE = 1;
    public static final String XIAOMI_ACCOUNT_PACKAGE_NAME = "com.xiaomi.account";
    private AccountManager mAccountManager;
    private final BroadcastReceiver mAccountsChangedBroadcastReceiver;
    private Context mContext;
    private ExecutorService mExecutorService;
    private Handler mMainHandler;
    private final HashMap<MiuiOnAccountsUpdateListener, Handler> mMiuiAccountsUpdatedListeners;

    private ExtraAccountManager(Context context) {
        this.mMiuiAccountsUpdatedListeners = new HashMap<>();
        this.mAccountsChangedBroadcastReceiver = new BroadcastReceiver() { // from class: miui.accounts.ExtraAccountManager.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, final Intent intent) {
                final BroadcastReceiver.PendingResult goAsync = goAsync();
                ExtraAccountManager.this.mExecutorService.execute(new Runnable() { // from class: miui.accounts.ExtraAccountManager.4.1
                    @Override // java.lang.Runnable
                    public void run() {
                        String action = intent.getAction();
                        if (ExtraAccountManager.LOGIN_ACCOUNTS_PRE_CHANGED_ACTION.equals(action) || ExtraAccountManager.LOGIN_ACCOUNTS_POST_CHANGED_ACTION.equals(action)) {
                            Account account = (Account) intent.getParcelableExtra(ExtraAccountManager.EXTRA_ACCOUNT);
                            Bundle bundle = (Bundle) intent.getParcelableExtra(ExtraAccountManager.EXTRA_BUNDLE);
                            int intExtra = intent.getIntExtra(ExtraAccountManager.EXTRA_UPDATE_TYPE, -1);
                            if (account == null || intExtra <= 0) {
                                Log.w(ExtraAccountManager.TAG, "account changed, but no account or type");
                            } else {
                                synchronized (ExtraAccountManager.this.mMiuiAccountsUpdatedListeners) {
                                    for (Map.Entry entry : ExtraAccountManager.this.mMiuiAccountsUpdatedListeners.entrySet()) {
                                        ExtraAccountManager.this.postToHandler((Handler) entry.getValue(), (MiuiOnAccountsUpdateListener) entry.getKey(), account, intExtra, bundle, ExtraAccountManager.LOGIN_ACCOUNTS_PRE_CHANGED_ACTION.equals(action));
                                    }
                                }
                            }
                        } else {
                            Account[] accounts = ExtraAccountManager.this.mAccountManager.getAccounts();
                            synchronized (ExtraAccountManager.this.mMiuiAccountsUpdatedListeners) {
                                for (Map.Entry entry2 : ExtraAccountManager.this.mMiuiAccountsUpdatedListeners.entrySet()) {
                                    ExtraAccountManager.this.postToHandler((Handler) entry2.getValue(), (MiuiOnAccountsUpdateListener) entry2.getKey(), accounts);
                                }
                            }
                        }
                        goAsync.finish();
                    }
                });
            }
        };
        this.mContext = context;
        this.mAccountManager = AccountManager.get(context);
        this.mMainHandler = new Handler(this.mContext.getMainLooper());
        this.mExecutorService = Executors.newSingleThreadExecutor();
    }

    public static ExtraAccountManager getInstance(Context context) {
        return (ExtraAccountManager) INSTANCE.get();
    }

    public static Account getXiaomiAccount(Context context) {
        Account[] accountsByType = AccountManager.get(context).getAccountsByType("com.xiaomi");
        if (accountsByType.length > 0) {
            return accountsByType[0];
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postToHandler(Handler handler, final MiuiOnAccountsUpdateListener miuiOnAccountsUpdateListener, final Account account, final int i, final Bundle bundle, final boolean z) {
        if (handler == null) {
            handler = this.mMainHandler;
        }
        handler.post(new Runnable() { // from class: miui.accounts.ExtraAccountManager.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (z) {
                        miuiOnAccountsUpdateListener.onPreAccountUpdated(account, i, bundle);
                    } else {
                        miuiOnAccountsUpdateListener.onPostAccountUpdated(account, i, bundle);
                    }
                } catch (SQLException e) {
                    Log.e(ExtraAccountManager.TAG, "Can't update accounts", e);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postToHandler(Handler handler, final MiuiOnAccountsUpdateListener miuiOnAccountsUpdateListener, Account[] accountArr) {
        int length = accountArr.length;
        final Account[] accountArr2 = new Account[length];
        System.arraycopy(accountArr, 0, accountArr2, 0, length);
        if (handler == null) {
            handler = this.mMainHandler;
        }
        handler.post(new Runnable() { // from class: miui.accounts.ExtraAccountManager.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    miuiOnAccountsUpdateListener.onAccountsUpdated(accountArr2);
                } catch (SQLException e) {
                    Log.e(ExtraAccountManager.TAG, "Can't update accounts", e);
                }
            }
        });
    }

    public void addOnAccountsUpdatedListener(MiuiOnAccountsUpdateListener miuiOnAccountsUpdateListener, Handler handler, boolean z) {
        if (miuiOnAccountsUpdateListener == null) {
            throw new IllegalArgumentException("the listener is null");
        }
        synchronized (this.mMiuiAccountsUpdatedListeners) {
            if (this.mMiuiAccountsUpdatedListeners.containsKey(miuiOnAccountsUpdateListener)) {
                throw new IllegalStateException("this listener is already added");
            }
            boolean isEmpty = this.mMiuiAccountsUpdatedListeners.isEmpty();
            this.mMiuiAccountsUpdatedListeners.put(miuiOnAccountsUpdateListener, handler);
            if (isEmpty) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.accounts.LOGIN_ACCOUNTS_CHANGED");
                intentFilter.addAction(LOGIN_ACCOUNTS_PRE_CHANGED_ACTION);
                intentFilter.addAction(LOGIN_ACCOUNTS_POST_CHANGED_ACTION);
                intentFilter.addAction("android.intent.action.DEVICE_STORAGE_OK");
                this.mContext.registerReceiver(this.mAccountsChangedBroadcastReceiver, intentFilter);
            }
        }
        if (z) {
            postToHandler(handler, miuiOnAccountsUpdateListener, this.mAccountManager.getAccounts());
        }
    }

    public void removeOnAccountsUpdatedListener(MiuiOnAccountsUpdateListener miuiOnAccountsUpdateListener) {
        if (miuiOnAccountsUpdateListener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        synchronized (this.mMiuiAccountsUpdatedListeners) {
            if (!this.mMiuiAccountsUpdatedListeners.containsKey(miuiOnAccountsUpdateListener)) {
                Log.e(TAG, "Listener was not previously added");
                return;
            }
            this.mMiuiAccountsUpdatedListeners.remove(miuiOnAccountsUpdateListener);
            if (this.mMiuiAccountsUpdatedListeners.isEmpty()) {
                this.mContext.unregisterReceiver(this.mAccountsChangedBroadcastReceiver);
            }
        }
    }
}
