package com.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiBandMode extends AppCompatActivity {
    private static final String[] BAND_NAMES = {"LTE B1 Preferred", "LTE B2 Preferred", "LTE B3 Preferred", "LTE B4 Preferred", "LTE B5 Preferred", "LTE B7 Preferred", "LTE B8 Preferred", "LTE B20 Preferred", "LTE B28 Preferred", "LTE B38 Preferred", "LTE B40 Preferred", "LTE B41 Preferred", "GSM 900", "GSM 1800", "GSM 1900", "WCDMA I 2100", "WCDMA II 1900", "WCDMA IV 1700", "WCDMA V 850", "WCDMA V VIII 900", "LTE B1", "LTE B2", "LTE B3", "LTE B4", "LTE B5", "LTE B7", "LTE B8", "LTE B20", "LTE B28", "LTE B38", "LTE B40", "LTE B41"};
    public static final long[] BAND_VALUES = {0, 1, 2, 3, 4, 6, 7, 25, 38, 20, 22, 29, 512, 128, PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE, PlaybackStateCompat.ACTION_SET_PLAYBACK_SPEED, 8388608, 33554432, 67108864, 562949953421312L, 1, 2, 4, 8, 16, 64, 128, PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED, 134217728, 137438953472L, 549755813888L, 1099511627776L};
    private ListView mBandList;
    private CustimizedArrayAdapter mBandListAdapter;
    private Switch mSelection;
    ArrayList<BandListItem> mBandsToSet = new ArrayList<>();
    private Phone mPhone = null;
    private AdapterView.OnItemClickListener mBandSelectionHandler = new AdapterView.OnItemClickListener() { // from class: com.android.settings.MiuiBandMode.3
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView adapterView, View view, int i, long j) {
            BandListItem bandListItem = (BandListItem) adapterView.getAdapter().getItem(i);
            if (MiuiBandMode.this.mBandsToSet.contains(bandListItem)) {
                MiuiBandMode.this.mBandsToSet.remove(bandListItem);
                bandListItem.mChecked = false;
                return;
            }
            MiuiBandMode.this.mBandsToSet.add(bandListItem);
            bandListItem.mChecked = true;
        }
    };
    private Handler mHandler = new Handler() { // from class: com.android.settings.MiuiBandMode.4
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 200 && !MiuiBandMode.this.isFinishing()) {
                MiuiBandMode.this.displayBandSelectionResult(null);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BandListItem {
        public long mBandMode;
        public boolean mChecked;
        public int mPosition;

        public BandListItem() {
            this.mPosition = -1;
            this.mBandMode = 0L;
            this.mChecked = false;
        }

        public BandListItem(int i, long j, boolean z) {
            this.mPosition = -1;
            this.mBandMode = 0L;
            this.mChecked = false;
            this.mPosition = i;
            this.mBandMode = j;
            this.mChecked = z;
        }

        public String toString() {
            if (this.mPosition >= MiuiBandMode.BAND_NAMES.length) {
                return "Band mode " + this.mBandMode;
            }
            return MiuiBandMode.BAND_NAMES[this.mPosition];
        }
    }

    /* loaded from: classes.dex */
    private class CustimizedArrayAdapter<T> extends ArrayAdapter {
        public CustimizedArrayAdapter(Context context, int i) {
            super(context, i);
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            if (MiuiBandMode.this.mSelection == null || !MiuiBandMode.this.mSelection.isChecked()) {
                return super.areAllItemsEnabled();
            }
            return false;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int i) {
            if (MiuiBandMode.this.mSelection == null || !MiuiBandMode.this.mSelection.isChecked()) {
                return super.isEnabled(i);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayBandSelectionResult(Throwable th) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBandsSet() {
        ArrayList arrayList = new ArrayList();
        Iterator<BandListItem> it = this.mBandsToSet.iterator();
        long j = 0;
        long j2 = 0;
        while (it.hasNext()) {
            BandListItem next = it.next();
            int i = next.mPosition;
            long j3 = next.mBandMode;
            Log.i("MiuiBandMode", "handleBandsSet  position:" + i + " band:" + next.toString() + " mode:" + j3);
            if (i >= 0 && i <= 11) {
                arrayList.add(Long.valueOf(j3));
            } else if (i < 12 || i > 19) {
                j2 |= j3;
            } else {
                j |= j3;
            }
        }
        Log.i("MiuiBandMode", "=============BANDSETBEGIN======================");
        if (!arrayList.isEmpty()) {
            int[] iArr = new int[arrayList.size() + 1];
            iArr[0] = arrayList.size();
            int i2 = 0;
            while (i2 < arrayList.size()) {
                int i3 = i2 + 1;
                iArr[i3] = (int) ((Long) arrayList.get(i2)).longValue();
                Log.i("MiuiBandMode", "handleBandsSet LtePrefBandMode " + iArr[i3]);
                i2 = i3;
            }
            VendorUtils.setLteBandPref(iArr, this.mPhone.getPhoneId());
        }
        if (j != 0 || j2 != 0) {
            VendorUtils.setXiaomiBandMode(new long[]{j, j2}, this.mPhone.getPhoneId());
            Log.i("MiuiBandMode", "handleBandsSet gwBand " + j + " lteBand:" + j2);
        }
        Log.i("MiuiBandMode", "==============BANDSETEND=====================");
    }

    private boolean isLtePrefBandSetBefore() {
        Iterator<BandListItem> it = this.mBandsToSet.iterator();
        while (it.hasNext()) {
            int i = it.next().mPosition;
            Log.i("MiuiBandMode", "isLtePrefBandSetBefore position: " + i);
            if (i >= 0 && i <= 11) {
                Log.i("MiuiBandMode", "isLtePrefBandSetBefore Yes");
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetBands() {
        long j = 0;
        int i = 0;
        while (true) {
            long[] jArr = BAND_VALUES;
            if (i >= jArr.length) {
                final int[] iArr = {0};
                final long[] jArr2 = {1152921504606846975L, j};
                final boolean isLtePrefBandSetBefore = isLtePrefBandSetBefore();
                Log.i("MiuiBandMode", "resetBands  shouldResetLtePref:" + isLtePrefBandSetBefore);
                new Thread(new Runnable() { // from class: com.android.settings.MiuiBandMode.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (isLtePrefBandSetBefore) {
                            Log.i("MiuiBandMode", "resetBands  setLteBandPref");
                            VendorUtils.setLteBandPref(iArr, MiuiBandMode.this.mPhone.getPhoneId());
                        }
                        VendorUtils.setXiaomiBandMode(jArr2, MiuiBandMode.this.mPhone.getPhoneId());
                    }
                }).start();
                return;
            }
            if (i > 19) {
                j |= jArr[i];
            }
            i++;
        }
    }

    private void saveBandPosition() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        Iterator<BandListItem> it = this.mBandsToSet.iterator();
        String str = "";
        while (it.hasNext()) {
            str = str + it.next().mPosition + ",";
        }
        Log.i("MiuiBandMode", "saveBandPosition bandPosition:" + str);
        edit.putBoolean("BAND_INFO_SELECTION" + this.mPhone.getPhoneId(), this.mSelection.isChecked());
        edit.putString("BAND_INFO_STORE" + this.mPhone.getPhoneId(), str);
        edit.commit();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.miui_band_mode);
        this.mPhone = PhoneFactory.getPhone(getIntent().getIntExtra("phone_id", 0));
        this.mBandList = (ListView) findViewById(R.id.miui_band);
        this.mBandListAdapter = new CustimizedArrayAdapter(this, 17367056);
        for (int i = 0; i < BAND_NAMES.length; i++) {
            this.mBandListAdapter.add(new BandListItem(i, BAND_VALUES[i], false));
        }
        this.mBandList.setAdapter((ListAdapter) this.mBandListAdapter);
        this.mBandList.setChoiceMode(2);
        this.mBandList.setOnItemClickListener(this.mBandSelectionHandler);
        Switch r7 = (Switch) findViewById(R.id.btn_select);
        this.mSelection = r7;
        r7.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiBandMode.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MiuiBandMode.this.mSelection.isChecked()) {
                    if (MiuiBandMode.this.mBandsToSet.isEmpty()) {
                        return;
                    }
                    new Thread(new Runnable() { // from class: com.android.settings.MiuiBandMode.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MiuiBandMode.this.handleBandsSet();
                            MiuiBandMode.this.mPhone.setRadioPower(false);
                            MiuiBandMode.this.mPhone.setRadioPower(true);
                            MiuiBandMode.this.mHandler.sendMessageDelayed(MiuiBandMode.this.mHandler.obtainMessage(200), 500L);
                        }
                    }).start();
                    return;
                }
                MiuiBandMode.this.resetBands();
                MiuiBandMode.this.mBandList.clearChoices();
                MiuiBandMode.this.mBandsToSet.clear();
                MiuiBandMode.this.mBandListAdapter.notifyDataSetChanged();
            }
        });
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String string = defaultSharedPreferences.getString("BAND_INFO_STORE" + this.mPhone.getPhoneId(), "");
        Boolean valueOf = Boolean.valueOf(defaultSharedPreferences.getBoolean("BAND_INFO_SELECTION" + this.mPhone.getPhoneId(), false));
        if (!string.isEmpty()) {
            Log.i("MiuiBandMode", "bandInfoStored: " + string);
            for (String str : string.split(",")) {
                this.mBandList.setItemChecked(Integer.parseInt(str), true);
                this.mBandListAdapter.notifyDataSetChanged();
            }
        }
        this.mSelection.setChecked(valueOf.booleanValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        Iterator<BandListItem> it = this.mBandsToSet.iterator();
        while (it.hasNext()) {
            this.mBandList.setItemChecked(it.next().mPosition, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        saveBandPosition();
    }
}
