package com.android.settingslib.wifi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.R$attr;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.StandardWifiEntry;
import com.android.wifitrackerlib.WifiEntry;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/* loaded from: classes2.dex */
public class WifiEntryPreference extends RadioButtonPreference implements WifiEntry.WifiEntryCallback, View.OnClickListener {
    public static volatile Method methodGetSummary;
    public static volatile Method methodIsSupportMiWill;
    private CharSequence mContentDescription;
    private Context mContext;
    private boolean mForSlaveWifi;
    private final StateListDrawable mFrictionSld;
    private boolean mHe8ssCapableAp;
    private final IconInjector mIconInjector;
    private boolean mIsConnected;
    private boolean mIsMeteredHint;
    private boolean mIsSlaveConnected;
    private int mLevel;
    private OnButtonClickListener mOnButtonClickListener;
    private boolean mShowX;
    private boolean mVhtMax8SpatialStreamsSupport;
    private WifiEntry mWifiEntry;
    private WifiManager mWifiManager;
    protected int mWifiStandard;
    private static final int[] STATE_SECURED = {R$attr.state_encrypted};
    private static final int[] FRICTION_ATTRS = {R$attr.wifi_friction};
    public static final int[] WIFI_CONNECTION_STRENGTH = {R$string.accessibility_no_wifi, R$string.accessibility_wifi_one_bar, R$string.accessibility_wifi_two_bars, R$string.accessibility_wifi_three_bars, R$string.accessibility_wifi_signal_full};

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class IconInjector {
        private final Context mContext;

        IconInjector(Context context) {
            this.mContext = context;
        }

        public Drawable getIcon(boolean z, int i) {
            return this.mContext.getDrawable(WifiUtils.getInternetIconResource(i, z));
        }
    }

    /* loaded from: classes2.dex */
    public interface OnButtonClickListener {
        void onButtonClick(WifiEntryPreference wifiEntryPreference);
    }

    /* loaded from: classes2.dex */
    public static class UserBadgeCache {
        private final SparseArray<Drawable> mBadges = new SparseArray<>();
        private final PackageManager mPm;

        public UserBadgeCache(PackageManager packageManager) {
            this.mPm = packageManager;
        }
    }

    static {
        methodIsSupportMiWill = null;
        methodGetSummary = null;
        try {
            Class<?> cls = Class.forName("com.android.wifitrackerlib.StandardWifiEntry");
            methodIsSupportMiWill = cls.getMethod("isSupportMiWill", null);
            Class<?> cls2 = Boolean.TYPE;
            methodGetSummary = cls.getMethod("getSummary", cls2, cls2);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        }
    }

    public WifiEntryPreference(Context context, WifiEntry wifiEntry) {
        this(context, wifiEntry, new IconInjector(context));
    }

    WifiEntryPreference(Context context, WifiEntry wifiEntry, IconInjector iconInjector) {
        super(context);
        this.mLevel = -1;
        this.mForSlaveWifi = false;
        this.mIsMeteredHint = false;
        setLayoutResource(R$layout.preference_access_point);
        setWidgetLayoutResource(R$layout.access_point_friction_widget);
        this.mFrictionSld = getFrictionStateListDrawable();
        this.mWifiEntry = wifiEntry;
        wifiEntry.setListener(this);
        this.mIconInjector = iconInjector;
        this.mWifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        this.mContext = context;
        refresh();
    }

    public WifiEntryPreference(Context context, WifiEntry wifiEntry, boolean z) {
        this(context, wifiEntry, new IconInjector(context));
        this.mForSlaveWifi = z;
    }

    private void bindFrictionImage(ImageView imageView) {
        if (imageView == null || this.mFrictionSld == null) {
            return;
        }
        if (this.mWifiEntry.getSecurity() != 0 && this.mWifiEntry.getSecurity() != 4) {
            this.mFrictionSld.setState(STATE_SECURED);
        }
        imageView.setImageDrawable(this.mFrictionSld.getCurrent());
    }

    private Drawable getDrawable(int i) {
        try {
            return getContext().getDrawable(i);
        } catch (Resources.NotFoundException unused) {
            return null;
        }
    }

    private StateListDrawable getFrictionStateListDrawable() {
        TypedArray typedArray;
        try {
            typedArray = getContext().getTheme().obtainStyledAttributes(FRICTION_ATTRS);
        } catch (Resources.NotFoundException unused) {
            typedArray = null;
        }
        if (typedArray != null) {
            return (StateListDrawable) typedArray.getDrawable(0);
        }
        return null;
    }

    private boolean isMiWillWifiEntry(WifiEntry wifiEntry) {
        if (methodIsSupportMiWill != null && (wifiEntry instanceof StandardWifiEntry)) {
            try {
                if (((Boolean) methodIsSupportMiWill.invoke((StandardWifiEntry) wifiEntry, null)).booleanValue()) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        return false;
    }

    CharSequence buildContentDescription() {
        Context context = getContext();
        CharSequence title = getTitle();
        CharSequence summary = getSummary();
        if (!TextUtils.isEmpty(summary)) {
            title = TextUtils.concat(title, ",", summary);
        }
        int level = this.mWifiEntry.getLevel();
        if (level >= 0) {
            int[] iArr = WIFI_CONNECTION_STRENGTH;
            if (level < iArr.length) {
                title = TextUtils.concat(title, ",", context.getString(iArr[level]));
            }
        }
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = title;
        charSequenceArr[1] = ",";
        charSequenceArr[2] = this.mWifiEntry.getSecurity() == 0 ? context.getString(R$string.accessibility_wifi_security_type_none) : context.getString(R$string.accessibility_wifi_security_type_secured);
        return TextUtils.concat(charSequenceArr);
    }

    protected int getIconColorAttr() {
        return this.mWifiEntry.hasInternetAccess() && this.mWifiEntry.getConnectedState() == 2 ? 16843829 : 16843817;
    }

    public String getPrimaryWifiTitleForSlave() {
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        boolean z = connectionInfo != null && WifiUtils.is24GHz(connectionInfo.getFrequency());
        boolean z2 = connectionInfo != null && WifiUtils.is5GHz(connectionInfo.getFrequency());
        StringBuilder sb = new StringBuilder();
        sb.append(this.mWifiEntry.getTitle());
        if (z) {
            sb.append(this.mContext.getString(R$string.band_24G));
        } else if (z2) {
            sb.append(this.mContext.getString(R$string.band_5G));
        }
        return sb.toString();
    }

    public WifiEntry getWifiEntry() {
        return this.mWifiEntry;
    }

    public boolean isConnected() {
        return this.mIsConnected;
    }

    protected boolean isMeteredHint(Set<ScanResult> set) {
        return false;
    }

    public boolean isSlaveConnected() {
        return this.mIsSlaveConnected;
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Drawable icon = getIcon();
        if (icon != null) {
            icon.setLevel(this.mLevel);
        }
        preferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
        ImageButton imageButton = (ImageButton) preferenceViewHolder.findViewById(R$id.icon_button);
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(R$id.friction_icon);
        if (this.mWifiEntry.getHelpUriString() == null || this.mWifiEntry.getConnectedState() != 0) {
            imageButton.setVisibility(8);
            if (imageView != null) {
                imageView.setVisibility(0);
                bindFrictionImage(imageView);
                return;
            }
            return;
        }
        Drawable drawable = getDrawable(R$drawable.ic_help);
        drawable.setTintList(Utils.getColorAttr(getContext(), 16843817));
        imageButton.setImageDrawable(drawable);
        imageButton.setVisibility(0);
        imageButton.setOnClickListener(this);
        imageButton.setContentDescription(getContext().getText(R$string.help_label));
        if (imageView != null) {
            imageView.setVisibility(8);
        }
    }

    public void onClick(View view) {
        OnButtonClickListener onButtonClickListener;
        if (view.getId() != R$id.icon_button || (onButtonClickListener = this.mOnButtonClickListener) == null) {
            return;
        }
        onButtonClickListener.onButtonClick(this);
    }

    public void onUpdated() {
        refresh();
    }

    public void refresh() {
        String summary;
        boolean isMiWillWifiEntry = isMiWillWifiEntry(this.mWifiEntry);
        setConnected(getWifiEntry().getConnectedState() == 2 || getWifiEntry().getConnectedState() == 1);
        setSlaveConnected(getWifiEntry().getSlaveConnectedState() == 2 || getWifiEntry().getSlaveConnectedState() == 1);
        if (this.mIsConnected && this.mForSlaveWifi && !isMiWillWifiEntry) {
            setTitle(getPrimaryWifiTitleForSlave());
        } else {
            setTitle(this.mWifiEntry.getTitle());
        }
        int level = this.mWifiEntry.getLevel();
        int wifiStandard = this.mWifiEntry.getWifiStandard();
        boolean isVhtMax8SpatialStreamsSupported = Wifi6ApiCompatible.isVhtMax8SpatialStreamsSupported(this.mWifiEntry);
        boolean isHe8ssCapableAp = Wifi6ApiCompatible.isHe8ssCapableAp(this.mWifiEntry);
        boolean shouldShowXLevelIcon = this.mWifiEntry.shouldShowXLevelIcon();
        boolean isMeteredHint = isMeteredHint(this.mWifiEntry.getScanResults());
        if (level != this.mLevel || wifiStandard != this.mWifiStandard || isHe8ssCapableAp != this.mHe8ssCapableAp || isVhtMax8SpatialStreamsSupported != this.mVhtMax8SpatialStreamsSupport || this.mIsMeteredHint != isMeteredHint) {
            this.mLevel = level;
            this.mWifiStandard = wifiStandard;
            this.mHe8ssCapableAp = isHe8ssCapableAp;
            this.mVhtMax8SpatialStreamsSupport = isVhtMax8SpatialStreamsSupported;
            this.mShowX = shouldShowXLevelIcon;
            this.mIsMeteredHint = isMeteredHint;
            updateIcon(shouldShowXLevelIcon, level, wifiStandard, isHe8ssCapableAp && isVhtMax8SpatialStreamsSupported);
            notifyChanged();
        }
        String key = getKey();
        if (methodGetSummary == null || !(this.mWifiEntry instanceof StandardWifiEntry)) {
            summary = this.mWifiEntry.getSummary(false);
        } else {
            try {
                Method method = methodGetSummary;
                StandardWifiEntry standardWifiEntry = (StandardWifiEntry) this.mWifiEntry;
                Object[] objArr = new Object[2];
                objArr[0] = Boolean.FALSE;
                objArr[1] = Boolean.valueOf(key != null && key.startsWith("slave-"));
                summary = (String) method.invoke(standardWifiEntry, objArr);
            } catch (Exception e) {
                String summary2 = this.mWifiEntry.getSummary(false);
                Log.e("WifiEntryPreference", "methodGetSummary catch:" + e);
                summary = summary2;
            }
        }
        if (this.mIsConnected && this.mForSlaveWifi && !BaseWifiTracker.isVerboseLoggingEnabled()) {
            setSummary("");
        } else {
            setSummary(summary);
        }
        this.mContentDescription = buildContentDescription();
    }

    public void setConnected(boolean z) {
        this.mIsConnected = z;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.mOnButtonClickListener = onButtonClickListener;
        notifyChanged();
    }

    public void setSlaveConnected(boolean z) {
        this.mIsSlaveConnected = z;
    }

    protected void updateIcon(boolean z, int i, int i2, boolean z2) {
        if (i == -1) {
            setIcon((Drawable) null);
            return;
        }
        Drawable icon = this.mIconInjector.getIcon(z, i);
        if (icon == null) {
            setIcon((Drawable) null);
            return;
        }
        icon.setTint(Utils.getColorAttrDefaultColor(getContext(), getIconColorAttr()));
        setIcon(icon);
    }
}
