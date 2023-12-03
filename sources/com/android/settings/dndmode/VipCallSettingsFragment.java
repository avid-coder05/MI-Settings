package com.android.settings.dndmode;

import android.app.ExtraNotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.Preference;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import com.android.settingslib.miuisettings.preference.RadioButtonPreferenceCategory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import miui.provider.ExtraTelephony;
import miui.provider.Notes;
import miui.vip.VipService;
import miuix.appcompat.app.AlertDialog;
import miuix.core.util.IOUtils;

/* loaded from: classes.dex */
public class VipCallSettingsFragment extends SettingsPreferenceFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final HashMap<String, String> mContacts = new HashMap<>();
    private ZenModeConfig mConfig;
    private Context mContext;
    private PreferenceCategory mCustomCategory;
    Future<Boolean> mFuture;
    private MenuItem mMenuItemAdd;
    private RadioButtonPreferenceCategory mOptionsCategory;
    ExecutorService mThreadPool = Executors.newSingleThreadExecutor();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CustomVipItemPreference extends Preference {
        private ImageView mDeleteBtn;
        private long mId;

        public CustomVipItemPreference(Context context) {
            super(context);
            setLayoutResource(R.xml.dndm_custom_vip_item);
        }

        @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
        public void onBindView(View view) {
            super.onBindView(view);
            ImageView imageView = (ImageView) view.findViewById(R.id.delete_btn);
            this.mDeleteBtn = imageView;
            imageView.setClickable(true);
            this.mDeleteBtn.setFocusable(true);
            this.mDeleteBtn.setTag(Long.valueOf(this.mId));
            this.mDeleteBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.dndmode.VipCallSettingsFragment.CustomVipItemPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    VipCallSettingsFragment.this.showVipListDeleteConfirmDialog(String.valueOf(view2.getTag()));
                }
            });
        }

        public void setData(Cursor cursor) {
            this.mId = cursor.getLong(0);
            String string = cursor.getString(1);
            String str = VipCallSettingsFragment.mContacts.containsKey(string) ? (String) VipCallSettingsFragment.mContacts.get(string) : null;
            if (TextUtils.isEmpty(str)) {
                setTitle(string);
                return;
            }
            setTitle(str);
            setSummary(string);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addCustomGroup() {
        getPreferenceScreen().addPreference(this.mCustomCategory);
        MenuItem menuItem = this.mMenuItemAdd;
        if (menuItem != null) {
            menuItem.setVisible(true);
        }
    }

    private void addVipList() {
        Intent intent = new Intent("com.android.contacts.action.GET_MULTIPLE_PHONES");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        intent.putExtra("android.intent.extra.include_unknown_numbers", true);
        startActivityForResult(intent, VipService.VIP_SERVICE_FAILURE);
    }

    private void createVipList() {
        getLoaderManager().initLoader(0, null, this);
        if (this.mConfig.allowCallsFrom == 3) {
            addCustomGroup();
        } else {
            removeCustomGroup();
        }
        final String[] stringArray = getResources().getStringArray(R.array.dndm_vip_list_group_array);
        Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.dndmode.VipCallSettingsFragment.2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(androidx.preference.Preference preference) {
                int indexOf = Arrays.asList(stringArray).indexOf(((RadioButtonPreference) preference).getTitle().toString());
                if (indexOf == 3) {
                    VipCallSettingsFragment.this.addCustomGroup();
                } else {
                    VipCallSettingsFragment.this.removeCustomGroup();
                }
                ZenModeConfig copy = VipCallSettingsFragment.this.mConfig.copy();
                copy.allowCallsFrom = indexOf;
                copy.allowMessagesFrom = indexOf;
                ExtraNotificationManager.setZenModeConfig(VipCallSettingsFragment.this.mContext, copy);
                return true;
            }
        };
        for (String str : stringArray) {
            RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getPrefContext());
            radioButtonPreference.setTitle(str);
            radioButtonPreference.setOnPreferenceClickListener(onPreferenceClickListener);
            this.mOptionsCategory.addPreference(radioButtonPreference);
        }
        this.mOptionsCategory.setCheckedPosition(this.mConfig.allowCallsFrom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteVipList(final String str) {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.dndmode.VipCallSettingsFragment.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                int i;
                ContentResolver contentResolver = VipCallSettingsFragment.this.mContext.getContentResolver();
                Uri uri = ExtraTelephony.Phonelist.CONTENT_URI;
                Cursor query = contentResolver.query(uri, null, " _id = " + str, null, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            i = query.getInt(query.getColumnIndex("sync_dirty"));
                            IOUtils.closeQuietly(query);
                            if (i != 3 || i == 2) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("sync_dirty", (Integer) 1);
                                VipCallSettingsFragment.this.mContext.getContentResolver().update(Uri.withAppendedPath(uri, str), contentValues, null, null);
                            } else {
                                VipCallSettingsFragment.this.mContext.getContentResolver().delete(Uri.withAppendedPath(uri, str), null, null);
                            }
                            return null;
                        }
                    } catch (Throwable th) {
                        IOUtils.closeQuietly(query);
                        throw th;
                    }
                }
                i = -1;
                IOUtils.closeQuietly(query);
                if (i != 3) {
                }
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("sync_dirty", (Integer) 1);
                VipCallSettingsFragment.this.mContext.getContentResolver().update(Uri.withAppendedPath(uri, str), contentValues2, null, null);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void queryContacts() {
        this.mFuture = this.mThreadPool.submit(new Callable<Boolean>() { // from class: com.android.settings.dndmode.VipCallSettingsFragment.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Boolean call() throws Exception {
                Cursor query = VipCallSettingsFragment.this.mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{Notes.Data.DATA4, "display_name"}, null, null, null);
                try {
                    if (query != null) {
                        try {
                            VipCallSettingsFragment.mContacts.clear();
                            while (query.moveToNext()) {
                                String string = query.getString(0);
                                String string2 = query.getString(1);
                                if (!TextUtils.isEmpty(string)) {
                                    VipCallSettingsFragment.mContacts.put(string, string2);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("VipCallSettings", "Err in queryContacts: " + e);
                        }
                    }
                    return Boolean.TRUE;
                } finally {
                    query.close();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeCustomGroup() {
        getPreferenceScreen().removePreference(this.mCustomCategory);
        MenuItem menuItem = this.mMenuItemAdd;
        if (menuItem != null) {
            menuItem.setVisible(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showVipListDeleteConfirmDialog(final String str) {
        new AlertDialog.Builder(this.mContext).setMessage(R.string.dndm_dlg_remove_vip).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.dndm_dlg_remove_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.dndmode.VipCallSettingsFragment.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                VipCallSettingsFragment.this.deleteVipList(str);
            }
        }).show();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        final Parcelable[] parcelableArrayExtra;
        if (i != 1000 || intent == null || (parcelableArrayExtra = intent.getParcelableArrayExtra("com.android.contacts.extra.PHONE_URIS")) == null || parcelableArrayExtra.length == 0) {
            return;
        }
        new AsyncTask<Void, Void, List<String>>() { // from class: com.android.settings.dndmode.VipCallSettingsFragment.5
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<String> doInBackground(Void... voidArr) {
                StringBuilder sb = new StringBuilder();
                ArrayList arrayList = new ArrayList();
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
                Cursor query = sb.length() > 0 ? VipCallSettingsFragment.this.mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{"data1"}, "_id IN (" + sb.toString() + ")", null, null) : null;
                if (query != null) {
                    while (query.moveToNext()) {
                        try {
                            arrayList.add(query.getString(0));
                        } finally {
                            query.close();
                        }
                    }
                }
                return arrayList;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<String> list) {
                if (list.size() > 0) {
                    DoNotDisturbModeUtils.startImportVipList(VipCallSettingsFragment.this.mContext, (String[]) list.toArray(new String[0]));
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.dndm_vip_call_settings_fragment);
        Context context = getContext();
        this.mContext = context;
        this.mConfig = ExtraNotificationManager.getZenModeConfig(context);
        this.mOptionsCategory = (RadioButtonPreferenceCategory) findPreference("key_vip_options_category");
        this.mCustomCategory = (PreferenceCategory) findPreference("key_vip_list_custom_category");
        setHasOptionsMenu(true);
        createVipList();
        queryContacts();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.mContext, ExtraTelephony.Phonelist.CONTENT_URI, new String[]{"_id", "number"}, "type = ? and sync_dirty <> ?", new String[]{ExtraTelephony.Phonelist.TYPE_VIP, String.valueOf(1)}, null);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 0, 0, R.string.dndm_menu_add_vip_custom);
        this.mMenuItemAdd = add;
        add.setIcon(R.drawable.dndm_menu_add_vip).setShowAsAction(1);
        this.mMenuItemAdd.setVisible(this.mConfig.allowCallsFrom == 3);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        this.mCustomCategory.removeAll();
        while (cursor.moveToNext()) {
            CustomVipItemPreference customVipItemPreference = new CustomVipItemPreference(getPrefContext());
            customVipItemPreference.setData(cursor);
            this.mCustomCategory.addPreference(customVipItemPreference);
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
        this.mCustomCategory.removeAll();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 0) {
            addVipList();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mFuture.cancel(true);
    }
}
