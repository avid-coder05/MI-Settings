package com.android.settings.display.util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.R;
import miuix.appcompat.app.Fragment;

/* loaded from: classes.dex */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    protected Context mAppContext;
    protected View mView;
    private Handler mUIHandler = new Handler();
    private MessageQueue mMsgQueue = Looper.myQueue();

    protected void applyTitle() {
        Activity activity;
        int onSetTitle = onSetTitle();
        if (onSetTitle == -1 || (activity = this.mActivity) == null || activity.getActionBar() == null) {
            return;
        }
        this.mActivity.getActionBar().setTitle(onSetTitle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View findViewById(int i) {
        return this.mView.findViewById(i);
    }

    protected abstract void initView();

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        applyTitle();
        onCustomizeActionBar(getActivity().getActionBar());
        initView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAppContext = activity.getApplicationContext();
        this.mActivity = activity;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_DayNight_Settings_NoTitle);
    }

    protected void onCreateView2(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
    }

    protected abstract int onCreateViewLayout();

    protected abstract int onCustomizeActionBar(ActionBar actionBar);

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mView = layoutInflater.inflate(onCreateViewLayout(), viewGroup, false);
        onCreateView2(layoutInflater, viewGroup, bundle);
        return this.mView;
    }

    protected int onSetTitle() {
        return -1;
    }
}
