package com.android.settings.applications;

import android.view.View;
import android.widget.AdapterView;
import com.android.settings.applications.ApplicationsContainer;

/* compiled from: ApplicationsContainer.java */
/* loaded from: classes.dex */
interface ManageAppClickListener {
    void onItemClick(ApplicationsContainer.TabInfo tabInfo, AdapterView<?> adapterView, View view, int i, long j);
}
