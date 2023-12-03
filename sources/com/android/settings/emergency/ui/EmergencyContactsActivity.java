package com.android.settings.emergency.ui;

import android.app.PendingIntent;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.emergency.ui.view.ContactsListViewAdapter;
import com.android.settings.emergency.ui.view.SimpleItemTouchHelperCallback;
import com.android.settings.emergency.util.CommonUtils;
import com.android.settings.emergency.util.Config;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.provider.ExtraContacts;
import miui.telephony.SubscriptionInfo;
import miui.vip.VipService;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.core.util.IOUtils;

/* loaded from: classes.dex */
public class EmergencyContactsActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isFirst;
    private LoadContactsTask loadContactsTask;
    private ActionBar mActionBar;
    private ContactsListViewAdapter mAdapter;
    private Drawable mCancelIcon;
    private RecyclerView mContactsListView;
    private AlertDialog mDialog;
    private Drawable mDoneIcon;
    private ImageView mEditCancel;
    private ImageView mEditConfirm;
    private Menu mMenu;
    private BroadcastReceiver smsReceiver;
    private static final String TAG = EmergencyContactsActivity.class.getSimpleName();
    private static final Boolean NEED_STORE_DEFAULT = Boolean.TRUE;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class LoadContactsTask extends AsyncTask<List<String>, Void, List<Pair<String, String>>> {
        private WeakReference<EmergencyContactsActivity> mActivity;

        LoadContactsTask(EmergencyContactsActivity emergencyContactsActivity) {
            this.mActivity = new WeakReference<>(emergencyContactsActivity);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<Pair<String, String>> doInBackground(List<String>... listArr) {
            Cursor cursor;
            Cursor query;
            EmergencyContactsActivity emergencyContactsActivity = this.mActivity.get();
            Cursor cursor2 = null;
            if (emergencyContactsActivity == null || emergencyContactsActivity.isFinishing() || emergencyContactsActivity.isDestroyed() || isCancelled()) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < listArr[0].size(); i++) {
                String str = "";
                try {
                    if (listArr[1].size() <= 0 || i >= listArr[1].size()) {
                        query = emergencyContactsActivity.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, listArr[0].get(i)), new String[]{"display_name"}, null, null, null);
                        if (query != null) {
                            try {
                                if (query.moveToFirst()) {
                                    str = query.getString(0);
                                    if (TextUtils.isEmpty(str)) {
                                        str = emergencyContactsActivity.getResources().getString(R.string.miui_sos_unknow_contract);
                                    }
                                }
                            } catch (Exception e) {
                                cursor = query;
                                e = e;
                                try {
                                    Log.e(EmergencyContactsActivity.TAG, e.toString());
                                    IOUtils.closeQuietly(cursor);
                                    arrayList.add(new Pair(str, listArr[0].get(i)));
                                } catch (Throwable th) {
                                    th = th;
                                    cursor2 = cursor;
                                    IOUtils.closeQuietly(cursor2);
                                    throw th;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                cursor2 = query;
                                IOUtils.closeQuietly(cursor2);
                                throw th;
                            }
                        }
                    } else {
                        str = listArr[1].get(i);
                        query = null;
                    }
                    IOUtils.closeQuietly(query);
                } catch (Exception e2) {
                    e = e2;
                    cursor = null;
                } catch (Throwable th3) {
                    th = th3;
                }
                arrayList.add(new Pair(str, listArr[0].get(i)));
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<Pair<String, String>> list) {
            EmergencyContactsActivity emergencyContactsActivity = this.mActivity.get();
            if (emergencyContactsActivity != null) {
                emergencyContactsActivity.mAdapter.setDataList(list);
                emergencyContactsActivity.mAdapter.updateEmergencyContacts();
            }
        }
    }

    private void loadContacts(List<String> list, List<String> list2) {
        if (list == null || list2 == null) {
            return;
        }
        LoadContactsTask loadContactsTask = new LoadContactsTask(this);
        this.loadContactsTask = loadContactsTask;
        loadContactsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list, list2);
    }

    private void pickFromAndroidContacts() {
        try {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setData(ContactsContract.Contacts.CONTENT_URI);
            intent.setPackage("com.google.android.contacts");
            intent.addCategory("android.intent.category.DEFAULT");
            startActivityForResult(intent, 1001);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerSmsReceiver() {
        if (this.smsReceiver != null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("DELIVERED_SMS_ACTION0");
        intentFilter.addAction("DELIVERED_SMS_ACTION1");
        intentFilter.addAction("DELIVERED_SMS_ACTION2");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.settings.emergency.ui.EmergencyContactsActivity.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String stringExtra = intent.getStringExtra("name");
                String stringExtra2 = intent.getStringExtra("number");
                if (getResultCode() == -1) {
                    if (TextUtils.isEmpty(stringExtra) || stringExtra.replace(" ", "").equals(stringExtra2)) {
                        EmergencyContactsActivity emergencyContactsActivity = EmergencyContactsActivity.this;
                        Toast.makeText(emergencyContactsActivity, emergencyContactsActivity.getString(R.string.miui_sos_emergency_delivered_sms_noname, new Object[]{stringExtra2}), 0).show();
                        return;
                    }
                    EmergencyContactsActivity emergencyContactsActivity2 = EmergencyContactsActivity.this;
                    Toast.makeText(emergencyContactsActivity2, emergencyContactsActivity2.getString(R.string.miui_sos_emergency_delivered_sms, new Object[]{stringExtra, stringExtra2}), 0).show();
                } else if (TextUtils.isEmpty(stringExtra) || stringExtra.replace(" ", "").equals(stringExtra2)) {
                    EmergencyContactsActivity emergencyContactsActivity3 = EmergencyContactsActivity.this;
                    Toast.makeText(emergencyContactsActivity3, emergencyContactsActivity3.getString(R.string.miui_sos_emergency_failed_sms_noname, new Object[]{stringExtra2}), 0).show();
                } else {
                    EmergencyContactsActivity emergencyContactsActivity4 = EmergencyContactsActivity.this;
                    Toast.makeText(emergencyContactsActivity4, emergencyContactsActivity4.getString(R.string.miui_sos_emergency_failed_sms, new Object[]{stringExtra, stringExtra2}), 0).show();
                }
            }
        };
        this.smsReceiver = broadcastReceiver;
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Uri data;
        Cursor cursor;
        Cursor cursor2;
        Cursor cursor3;
        String string;
        if (intent == null) {
            return;
        }
        ArrayList<String> arrayList = new ArrayList();
        Cursor cursor4 = null;
        if (i == 1000) {
            Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra("com.android.contacts.extra.PHONE_URIS");
            if (parcelableArrayExtra == null || parcelableArrayExtra.length == 0) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Parcelable parcelable : parcelableArrayExtra) {
                Uri uri = (Uri) parcelable;
                if ("content".equals(uri.getScheme())) {
                    if (sb.length() > 0) {
                        sb.append(',');
                        sb.append(uri.getLastPathSegment());
                    } else {
                        sb.append(uri.getLastPathSegment());
                    }
                } else if ("tel".equals(uri.getScheme())) {
                    arrayList.add(uri.getSchemeSpecificPart().trim());
                }
            }
            cursor2 = sb.length() > 0 ? getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{"data1"}, "_id IN (" + sb.toString() + ")", null, null) : null;
            if (cursor2 != null) {
                while (cursor2.moveToNext()) {
                    try {
                        if (cursor2.getString(0) != null) {
                            arrayList.add(cursor2.getString(0).trim());
                        }
                    } finally {
                        cursor2.close();
                    }
                }
            }
        } else if (i != 1001 || (data = intent.getData()) == null) {
            return;
        } else {
            try {
                Cursor query = getContentResolver().query(data, null, null, null, null);
                cursor = null;
                if (query != null) {
                    while (query.moveToNext()) {
                        try {
                            String string2 = query.getString(query.getColumnIndex("_id"));
                            if (!TextUtils.isEmpty(string2)) {
                                cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id = " + string2, null, null);
                                if (cursor != null) {
                                    while (cursor.moveToNext() && arrayList.size() < 3) {
                                        String string3 = cursor.getString(cursor.getColumnIndex("data1"));
                                        if (!TextUtils.isEmpty(string3)) {
                                            String replace = string3.trim().replace(" ", "");
                                            if (!arrayList.contains(replace)) {
                                                arrayList.add(replace);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            cursor4 = query;
                            if (cursor2 != null) {
                            }
                            if (cursor2 != null) {
                            }
                            throw th;
                        }
                    }
                }
                if (query != null) {
                    query.close();
                }
                if (cursor2 != null) {
                }
            } catch (Throwable th2) {
                th = th2;
                cursor = null;
            }
        }
        boolean z = false;
        for (Pair<String, String> pair : this.mAdapter.getDataList()) {
            if (arrayList.contains(pair.second)) {
                arrayList.remove(pair.second);
                if (!z) {
                    Toast.makeText(this, R.string.miui_sos_settings_alert_contacts_repeat, 0).show();
                    z = true;
                }
            }
        }
        if (this.mAdapter.getItemCount() + arrayList.size() > 3) {
            Toast.makeText(this, R.string.miui_sos_settings_alert_contacts_too_many, 0).show();
        } else if (arrayList.size() != 0) {
            final ArrayList arrayList2 = new ArrayList();
            for (String str : arrayList) {
                try {
                    cursor3 = getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, str), new String[]{"display_name"}, null, null, null);
                } catch (Exception e) {
                    e = e;
                    cursor3 = null;
                } catch (Throwable th3) {
                    th = th3;
                }
                if (cursor3 != null) {
                    try {
                        try {
                        } catch (Throwable th4) {
                            th = th4;
                            cursor4 = cursor3;
                            IOUtils.closeQuietly(cursor4);
                            throw th;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        Log.e(TAG, e.toString());
                        IOUtils.closeQuietly(cursor3);
                    }
                    if (cursor3.moveToFirst()) {
                        string = cursor3.getString(0);
                        if (TextUtils.isEmpty(string)) {
                            string = getResources().getString(R.string.miui_sos_unknow_contract);
                        }
                        IOUtils.closeQuietly(cursor3);
                        arrayList2.add(new Pair(string, str));
                    }
                }
                string = getResources().getString(R.string.miui_sos_unknow_contract);
                IOUtils.closeQuietly(cursor3);
                arrayList2.add(new Pair(string, str));
            }
            if (arrayList2.isEmpty()) {
                return;
            }
            this.mAdapter.addDataItems(arrayList2);
            this.mAdapter.updateEmergencyContacts();
            Config.setSosEnable(this, true);
            final SubscriptionInfo currentEnableSubInfo = CommonUtils.getCurrentEnableSubInfo();
            if (currentEnableSubInfo != null) {
                new AlertDialog.Builder(this).setTitle(R.string.miui_sos_remind_title).setMessage(R.string.miui_sos_remind_sendinfo).setPositiveButton(R.string.miui_sos_remind_sendnow, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.EmergencyContactsActivity.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i3) {
                        EmergencyContactsActivity.this.registerSmsReceiver();
                        for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                            Pair pair2 = (Pair) arrayList2.get(i4);
                            Intent intent2 = new Intent("DELIVERED_SMS_ACTION" + i4);
                            intent2.putExtra("name", (String) pair2.first);
                            intent2.putExtra("number", (String) pair2.second);
                            CommonUtils.sendTextMessage((String) pair2.second, EmergencyContactsActivity.this.getString(R.string.miui_sos_remind_infocontent), PendingIntent.getBroadcast(EmergencyContactsActivity.this.getApplicationContext(), 0, intent2, 201326592), currentEnableSubInfo.getSlotId());
                        }
                    }
                }).setNegativeButton(R.string.miui_sos_remind_sendnow_donot_send, (DialogInterface.OnClickListener) null).show();
            } else {
                Toast.makeText(this, R.string.miui_sos_call_warning_sim_unable, 0).show();
            }
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (!this.mAdapter.getEditMode()) {
            super.onBackPressed();
            return;
        }
        try {
            this.mActionBar.setDisplayHomeAsUpEnabled(true);
            getAppCompatActionBar().setStartView(null);
            getAppCompatActionBar().setEndView(null);
        } catch (Exception unused) {
        }
        this.mAdapter.setEditMode(false, false);
        this.mMenu.getItem(0).setVisible(true);
        this.mAdapter.updateEmergencyContacts();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mAdapter.getEditMode()) {
            if (view.equals(this.mEditCancel)) {
                this.mAdapter.setEditMode(false, false);
            } else if (view.equals(this.mEditConfirm)) {
                this.mAdapter.setEditMode(false, NEED_STORE_DEFAULT.booleanValue());
            }
            try {
                this.mActionBar.setDisplayHomeAsUpEnabled(true);
                getAppCompatActionBar().setStartView(null);
                getAppCompatActionBar().setEndView(null);
            } catch (Exception unused) {
            }
            this.mMenu.getItem(0).setVisible(true);
            this.mAdapter.updateEmergencyContacts();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.emergency_contacts_activity);
        this.mActionBar = getAppCompatActionBar();
        boolean z = ((UiModeManager) getSystemService("uimode")).getNightMode() == 2;
        this.mDoneIcon = getDrawable(R.drawable.miuix_appcompat_action_mode_immersion_done_light);
        Drawable drawable = getDrawable(R.drawable.miuix_appcompat_action_mode_immersion_close_light);
        this.mCancelIcon = drawable;
        if (z) {
            drawable.setTint(-1);
            this.mDoneIcon.setTint(-1);
        }
        try {
            this.mEditConfirm = new ImageView(this);
            this.mEditConfirm.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.mEditConfirm.setImageDrawable(this.mDoneIcon);
            this.mEditConfirm.setContentDescription(getString(R.string.done));
            this.mEditConfirm.setOnClickListener(this);
            this.mEditCancel = new ImageView(this);
            this.mEditCancel.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.mEditCancel.setImageDrawable(this.mCancelIcon);
            this.mEditCancel.setContentDescription(getString(R.string.cancel));
            this.mEditCancel.setOnClickListener(this);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        this.isFirst = getIntent().getBooleanExtra("first_open", false);
        this.mAdapter = new ContactsListViewAdapter(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        this.mContactsListView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.mContactsListView.setAdapter(this.mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(this.mAdapter));
        itemTouchHelper.attachToRecyclerView(this.mContactsListView);
        this.mAdapter.setItemTouchHelper(itemTouchHelper);
        String sosEmergencyContacts = Config.getSosEmergencyContacts(this);
        String sosEmergencyContactNames = Config.getSosEmergencyContactNames(this);
        if (TextUtils.isEmpty(sosEmergencyContacts)) {
            return;
        }
        ArrayList arrayList = new ArrayList(Arrays.asList(sosEmergencyContacts.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)));
        ArrayList arrayList2 = new ArrayList();
        if (!TextUtils.isEmpty(sosEmergencyContactNames)) {
            arrayList2 = new ArrayList(Arrays.asList(sosEmergencyContactNames.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)));
        }
        loadContacts(arrayList, arrayList2);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        menu.add(0, 101, 0, R.string.miui_sos_menu_edit).setIcon(R.drawable.action_button_edit).setShowAsAction(2);
        this.mMenu.add(0, 100, 1, R.string.miui_sos_menu_add).setIcon(R.drawable.action_button_new).setShowAsAction(2);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        Config.setSosEnable(this, !TextUtils.isEmpty(Config.getSosEmergencyContacts(this)));
        BroadcastReceiver broadcastReceiver = this.smsReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        LoadContactsTask loadContactsTask = this.loadContactsTask;
        if (loadContactsTask != null) {
            loadContactsTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 100) {
            pickFromContacts();
            return true;
        }
        if (itemId == 101) {
            try {
                this.mActionBar.setDisplayHomeAsUpEnabled(false);
                getAppCompatActionBar().setStartView(this.mEditCancel);
                getAppCompatActionBar().setEndView(this.mEditConfirm);
            } catch (Exception unused) {
            }
            this.mAdapter.setEditMode(true, NEED_STORE_DEFAULT.booleanValue());
            menuItem.setVisible(false);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(Config.getSosEmergencyContacts(this))) {
            if (!this.isFirst) {
                showAddContactsDialog();
                return;
            }
            pickFromContacts();
            this.isFirst = false;
        }
    }

    public void pickFromContacts() {
        if (CommonUtils.isPreLoadGoogleCsp()) {
            pickFromAndroidContacts();
            return;
        }
        try {
            Intent intent = new Intent("com.android.contacts.action.GET_MULTIPLE_PHONES");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setPackage(ContactsSyncInfoProvider.AUTHORITY);
            intent.setType("vnd.android.cursor.dir/phone_v2");
            intent.putExtra("android.intent.extra.include_unknown_numbers", true);
            intent.putExtra("android.intent.extra.initial_picker_tab", 1);
            intent.putExtra("com.android.contacts.extra.MAX_COUNT", 3);
            startActivityForResult(intent, VipService.VIP_SERVICE_FAILURE);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void showAddContactsDialog() {
        if (this.mDialog == null) {
            this.mDialog = new AlertDialog.Builder(this).setTitle(R.string.miui_sos_remind_title).setMessage(R.string.miui_sos_remind_open).setPositiveButton(R.string.miui_sos_remind_add, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.EmergencyContactsActivity.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    EmergencyContactsActivity.this.pickFromContacts();
                }
            }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.EmergencyContactsActivity.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Config.setSosEnable(EmergencyContactsActivity.this.getApplicationContext(), false);
                    EmergencyContactsActivity.this.finish();
                }
            }).setCancelable(false).create();
        }
        this.mDialog.show();
    }
}
