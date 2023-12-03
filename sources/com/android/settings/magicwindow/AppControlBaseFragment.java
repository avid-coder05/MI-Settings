package com.android.settings.magicwindow;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Slog;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.settings.R;
import com.android.settings.display.util.BaseFragment;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import miuix.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public abstract class AppControlBaseFragment extends BaseFragment implements SwitchCallBack {
    private RecyclerView mAllAppControlView;
    private SwitchHandler mHandler;
    private AppControlAdapter mAppControlAdapter = null;
    private Context mContext = null;
    private List mTotalList = new ArrayList();

    /* loaded from: classes.dex */
    class SwitchHandler extends Handler {
        private WeakReference<Activity> mWeakActivity;

        SwitchHandler(Activity activity) {
            this.mWeakActivity = new WeakReference<>(activity);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            WeakReference<Activity> weakReference = this.mWeakActivity;
            if ((weakReference == null ? null : weakReference.get()) != null) {
                AppControlBaseFragment.this.doMsg(message);
                return;
            }
            message.getTarget().removeCallbacksAndMessages(null);
            Slog.d("MagicWinAppControlFragment", "mActivity == null");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doMsg(Message message) {
        if (message != null && message.what == 100) {
            String string = message.getData().getString("packageName");
            boolean z = message.getData().getBoolean("isChecked");
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                if (classLoader != null) {
                    Class<?> loadClass = classLoader.loadClass("android.magicwin.MiuiMagicWindowManager");
                    loadClass.getMethod("setMiuiMagicWinEnabled", String.class, Boolean.TYPE).invoke(loadClass, string, Boolean.valueOf(z));
                }
            } catch (Exception unused) {
                Slog.d("MagicWinAppControlFragment", "setMiuiMagicWinEnabled Error: class is null .");
            }
        }
    }

    public abstract IAppController getAppController();

    @Override // com.android.settings.display.util.BaseFragment
    protected void initView() {
        this.mContext = getContext();
        this.mHandler = new SwitchHandler(getActivity());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.magic_win_list);
        this.mAllAppControlView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new AsyncTask<Void, Void, List<ChildAppItemInfo>>() { // from class: com.android.settings.magicwindow.AppControlBaseFragment.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<ChildAppItemInfo> doInBackground(Void... voidArr) {
                AppControlBaseFragment appControlBaseFragment = AppControlBaseFragment.this;
                appControlBaseFragment.mTotalList = appControlBaseFragment.getAppController().getAppControlInfoList();
                return AppControlBaseFragment.this.mTotalList;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<ChildAppItemInfo> list) {
                AppControlBaseFragment appControlBaseFragment = AppControlBaseFragment.this;
                appControlBaseFragment.mAppControlAdapter = new AppControlAdapter(appControlBaseFragment.mContext, AppControlBaseFragment.this, list);
                AppControlBaseFragment.this.mAllAppControlView.setAdapter(AppControlBaseFragment.this.mAppControlAdapter);
            }
        }.execute(new Void[0]);
    }

    @Override // com.android.settings.magicwindow.SwitchCallBack
    public void onCheckedChangedListener(String str, boolean z) {
        updateTotalList(str, z);
        this.mHandler.removeMessages(100);
        Message obtainMessage = this.mHandler.obtainMessage(100);
        Bundle bundle = new Bundle();
        bundle.putString("packageName", str);
        bundle.putBoolean("isChecked", z);
        obtainMessage.setData(bundle);
        this.mHandler.sendMessage(obtainMessage);
    }

    @Override // com.android.settings.display.util.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.display.util.BaseFragment
    protected int onCreateViewLayout() {
        return R.layout.magic_window;
    }

    @Override // com.android.settings.display.util.BaseFragment
    protected int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        SwitchHandler switchHandler = this.mHandler;
        if (switchHandler != null) {
            switchHandler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    public void updateTotalList(String str, boolean z) {
        if (this.mTotalList.size() == 0) {
            return;
        }
        for (ChildAppItemInfo childAppItemInfo : this.mTotalList) {
            if (childAppItemInfo.getPkg().equals(str)) {
                childAppItemInfo.setMagicWinEnabled(z);
            }
        }
    }
}
