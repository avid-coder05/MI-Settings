package com.android.settingslib.core.lifecycle;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.lifecycle.Lifecycle;
import miuix.appcompat.app.Fragment;

/* loaded from: classes2.dex */
public class ObservableFragment extends Fragment {
    private final Lifecycle mLifecycle = new Lifecycle(this);

    @Override // androidx.fragment.app.Fragment, androidx.lifecycle.LifecycleOwner
    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    public Lifecycle getSettingsLifecycle() {
        return this.mLifecycle;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mLifecycle.onAttach(context);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        this.mLifecycle.onCreate(bundle);
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        this.mLifecycle.onCreateOptionsMenu(menu, menuInflater);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean onOptionsItemSelected = this.mLifecycle.onOptionsItemSelected(menuItem);
        return !onOptionsItemSelected ? super.onOptionsItemSelected(menuItem) : onOptionsItemSelected;
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        super.onPause();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        this.mLifecycle.onPrepareOptionsMenu(menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mLifecycle.onSaveInstanceState(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
        super.onStart();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        this.mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        super.onStop();
    }
}
