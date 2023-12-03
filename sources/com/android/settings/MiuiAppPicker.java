package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.settings.development.AppViewHolder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiAppPicker extends AppCompatActivity {
    private static final Comparator<MyApplicationInfo> sDisplayNameComparator = new Comparator<MyApplicationInfo>() { // from class: com.android.settings.MiuiAppPicker.2
        private final Collator collator = Collator.getInstance();

        @Override // java.util.Comparator
        public final int compare(MyApplicationInfo myApplicationInfo, MyApplicationInfo myApplicationInfo2) {
            return this.collator.compare(myApplicationInfo.label, myApplicationInfo2.label);
        }
    };
    private AppListAdapter mAdapter;

    /* loaded from: classes.dex */
    public class AppListAdapter extends ArrayAdapter<MyApplicationInfo> {
        private final LayoutInflater mInflater;
        private final List<MyApplicationInfo> mPackageInfoList;

        public AppListAdapter(Context context) {
            super(context, 0);
            this.mPackageInfoList = new ArrayList();
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            List<ApplicationInfo> installedApplications = context.getPackageManager().getInstalledApplications(0);
            for (int i = 0; i < installedApplications.size(); i++) {
                ApplicationInfo applicationInfo = installedApplications.get(i);
                if (applicationInfo.uid != 1000 && ((applicationInfo.flags & 2) != 0 || !"user".equals(Build.TYPE))) {
                    MyApplicationInfo myApplicationInfo = new MyApplicationInfo();
                    myApplicationInfo.info = applicationInfo;
                    myApplicationInfo.label = applicationInfo.loadLabel(MiuiAppPicker.this.getPackageManager()).toString();
                    this.mPackageInfoList.add(myApplicationInfo);
                }
            }
            Collections.sort(this.mPackageInfoList, MiuiAppPicker.sDisplayNameComparator);
            MyApplicationInfo myApplicationInfo2 = new MyApplicationInfo();
            myApplicationInfo2.label = context.getText(R.string.no_application);
            this.mPackageInfoList.add(0, myApplicationInfo2);
            addAll(this.mPackageInfoList);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            AppViewHolder createOrRecycle = AppViewHolder.createOrRecycle(this.mInflater, view);
            View view2 = createOrRecycle.rootView;
            MyApplicationInfo item = getItem(i);
            createOrRecycle.appName.setText(item.label);
            ApplicationInfo applicationInfo = item.info;
            if (applicationInfo != null) {
                createOrRecycle.appIcon.setImageDrawable(applicationInfo.loadIcon(MiuiAppPicker.this.getPackageManager()));
                createOrRecycle.appIcon.setVisibility(0);
                createOrRecycle.summary.setText(item.info.packageName);
                createOrRecycle.summary.setVisibility(0);
            } else {
                createOrRecycle.appIcon.setImageDrawable(null);
                createOrRecycle.appIcon.setVisibility(8);
                createOrRecycle.summary.setVisibility(8);
            }
            createOrRecycle.disabled.setVisibility(8);
            return view2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MyApplicationInfo {
        ApplicationInfo info;
        CharSequence label;

        MyApplicationInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.app_picker);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.MiuiAppPicker.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                MyApplicationInfo item = MiuiAppPicker.this.mAdapter.getItem(i);
                Intent intent = new Intent();
                ApplicationInfo applicationInfo = item.info;
                if (applicationInfo != null) {
                    intent.setAction(applicationInfo.packageName);
                }
                MiuiAppPicker.this.setResult(-1, intent);
                MiuiAppPicker.this.finish();
            }
        });
        AppListAdapter appListAdapter = new AppListAdapter(this);
        this.mAdapter = appListAdapter;
        if (appListAdapter.getCount() <= 0) {
            finish();
        } else {
            listView.setAdapter((ListAdapter) this.mAdapter);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        setVisible(true);
        super.onStart();
    }
}
