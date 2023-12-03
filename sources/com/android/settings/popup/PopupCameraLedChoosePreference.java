package com.android.settings.popup;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import com.android.settingslib.util.ToastUtil;
import java.util.Locale;
import miui.popupcamera.IPopupCameraManager;
import miuix.animation.Folme;

/* loaded from: classes2.dex */
public class PopupCameraLedChoosePreference extends Preference {
    private static final String TAG = PopupCameraLedChoosePreference.class.getSimpleName();
    private PopupCameraLedListAdapter mAdapter;
    private boolean mClickable;
    private Context mContext;
    private GridView mGridView;
    private Handler mHandler;
    private int[] mImages;
    private int mLastIndex;
    private final SettingsObserver mSettingsObserver;
    private IPopupCameraManager service;

    /* loaded from: classes2.dex */
    private final class SettingsObserver extends ContentObserver {
        private final Uri POPUP_TAKEBACK_OK_URI;

        public SettingsObserver() {
            super(PopupCameraLedChoosePreference.this.mHandler);
            this.POPUP_TAKEBACK_OK_URI = Settings.Secure.getUriFor("popup_takeback_ok");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            boolean z2 = Settings.Secure.getIntForUser(PopupCameraLedChoosePreference.this.getContext().getContentResolver(), "popup_takeback_ok", 1, -2) == 1;
            Log.d(PopupCameraLedChoosePreference.TAG, "onChange POPUP_TAKEBACK_OK mTakebackOk: " + z2);
            PopupCameraLedChoosePreference.this.mClickable = z2;
        }

        public void register() {
            PopupCameraLedChoosePreference.this.getContext().getContentResolver().registerContentObserver(this.POPUP_TAKEBACK_OK_URI, false, this, -1);
        }

        public void unregister() {
            PopupCameraLedChoosePreference.this.getContext().getContentResolver().unregisterContentObserver(this);
        }
    }

    public PopupCameraLedChoosePreference(Context context) {
        this(context, null);
    }

    public PopupCameraLedChoosePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PopupCameraLedChoosePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.service = null;
        SettingsObserver settingsObserver = new SettingsObserver();
        this.mSettingsObserver = settingsObserver;
        this.mClickable = true;
        this.mImages = new int[]{R.drawable.popup_led_color1, R.drawable.popup_led_color2, R.drawable.popup_led_color3, R.drawable.popup_led_color4, R.drawable.popup_led_color5};
        this.mContext = context;
        setLayoutResource(TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1 ? R.layout.popup_led_gridview_list_rtl : R.layout.popup_led_gridview_list);
        this.service = IPopupCameraManager.Stub.asInterface(ServiceManager.getService("popupcamera"));
        this.mHandler = new Handler();
        settingsObserver.register();
    }

    private void popupCamera() {
        try {
            this.service.popupMotor();
        } catch (RemoteException e) {
            Log.e(TAG, "PopupCameraManagerService connection failed", e);
        } catch (Exception e2) {
            Log.e(TAG, "error:" + e2);
        }
    }

    private void updateSelectedItem(int i) {
        Settings.System.putIntForUser(getContext().getContentResolver(), "miui_popup_led_index", i, -2);
    }

    public GridView getGridView() {
        return this.mGridView;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundColor(0);
        this.mGridView = (GridView) view.findViewById(R.id.popup_led_gridview);
        PopupCameraLedListAdapter popupCameraLedListAdapter = new PopupCameraLedListAdapter(this.mContext, this, this.mImages);
        this.mAdapter = popupCameraLedListAdapter;
        this.mGridView.setAdapter((ListAdapter) popupCameraLedListAdapter);
        int intForUser = Settings.System.getIntForUser(getContext().getContentResolver(), "miui_popup_led_index", 0, -2);
        this.mLastIndex = intForUser;
        this.mAdapter.setChooseItem(intForUser);
        final boolean isEnabled = getGridView().isEnabled();
        this.mGridView.setSelector(new ColorDrawable(0));
        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.popup.PopupCameraLedChoosePreference.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                if (!PopupCameraLedChoosePreference.this.mClickable) {
                    ToastUtil.show(PopupCameraLedChoosePreference.this.mContext, R.string.popup_led_try_later, 0);
                } else if (isEnabled) {
                    PopupCameraLedChoosePreference.this.mAdapter.setChooseItem(i);
                    PopupCameraLedChoosePreference.this.onLedSelected(i);
                }
            }
        });
    }

    public void onDestroy() {
        this.mClickable = true;
        this.mSettingsObserver.unregister();
    }

    public void onLedSelected(int i) {
        if (this.mLastIndex != i) {
            updateSelectedItem(i);
        }
        this.mLastIndex = i;
        popupCamera();
    }
}
