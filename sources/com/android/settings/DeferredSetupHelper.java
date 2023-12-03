package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.service.settings.suggestions.ISuggestionService;
import android.service.settings.suggestions.Suggestion;
import android.util.Log;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;

/* loaded from: classes.dex */
public class DeferredSetupHelper {
    private Context mContext;
    private Handler mHandler;
    private ISuggestionService mRemoteService;
    private ServiceConnection mServiceConnection = createServiceConnection();
    private Intent mServiceIntent;

    public DeferredSetupHelper(Context context, Handler handler) {
        this.mContext = context;
        this.mServiceIntent = new Intent().setComponent(FeatureFactory.getFactory(context).getSuggestionFeatureProvider(this.mContext).getSuggestionServiceComponent());
        this.mHandler = handler;
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() { // from class: com.android.settings.DeferredSetupHelper.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                DeferredSetupHelper.this.mRemoteService = ISuggestionService.Stub.asInterface(iBinder);
                DeferredSetupHelper.this.showSuggestionIfNeed();
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                DeferredSetupHelper.this.mRemoteService = null;
            }
        };
    }

    private boolean isReady() {
        return this.mRemoteService != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuggestionIfNeed$0() {
        long currentTimeMillis = System.currentTimeMillis();
        List<Suggestion> suggestions = getSuggestions();
        long currentTimeMillis2 = System.currentTimeMillis();
        Log.i("DeferredSetupHelper", "showSuggestionIfNeed: startTime: " + currentTimeMillis + " endTime: " + currentTimeMillis2);
        SharedPreferences.Editor edit = this.mContext.getSharedPreferences("DEFERRED_SETUP", 0).edit();
        if (suggestions == null) {
            return;
        }
        Log.i("DeferredSetupHelper", "getSuggestions, size:" + suggestions.size());
        Message obtain = Message.obtain();
        if (suggestions.size() > 0) {
            for (Suggestion suggestion : suggestions) {
                if (suggestion.getPendingIntent() != null) {
                    Intent intent = suggestion.getPendingIntent().getIntent();
                    Log.d("DeferredSetupHelper", "getPendingIntent:" + intent);
                    if (intent != null && intent.toString().contains("DeferredSettingsSuggestionActivity")) {
                        obtain.what = 0;
                        obtain.obj = suggestion;
                        edit.putBoolean("isShow", true).commit();
                        this.mHandler.sendMessage(obtain);
                        return;
                    }
                }
            }
        }
        if (currentTimeMillis2 - currentTimeMillis >= 200) {
            return;
        }
        edit.putBoolean("isShow", false).commit();
        obtain.what = 1;
        this.mHandler.sendMessage(obtain);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSuggestionIfNeed() {
        if (isReady()) {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.DeferredSetupHelper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DeferredSetupHelper.this.lambda$showSuggestionIfNeed$0();
                }
            });
        }
    }

    public List<Suggestion> getSuggestions() {
        if (isReady()) {
            try {
                return this.mRemoteService.getSuggestions();
            } catch (RemoteException | RuntimeException e) {
                Log.w("DeferredSetupHelper", "Error when calling getSuggestion()", e);
                return null;
            } catch (NullPointerException e2) {
                Log.w("DeferredSetupHelper", "mRemote service detached before able to query", e2);
                return null;
            }
        }
        return null;
    }

    public void startLoad() {
        this.mContext.bindServiceAsUser(this.mServiceIntent, this.mServiceConnection, 1, Process.myUserHandle());
    }

    public void stop() {
        if (this.mRemoteService != null) {
            this.mRemoteService = null;
            this.mContext.unbindService(this.mServiceConnection);
        }
    }
}
