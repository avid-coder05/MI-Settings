package com.android.settingslib.wifi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import com.android.settingslib.R$attr;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$string;
import com.android.settingslib.TronUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;

/* loaded from: classes2.dex */
public class AccessPointPreference extends RadioButtonPreference {
    protected AccessPoint mAccessPoint;
    private Drawable mBadge;
    private final UserBadgeCache mBadgeCache;
    private final int mBadgePadding;
    private CharSequence mContentDescription;
    private int mDefaultIconResId;
    private boolean mForSavedNetworks;
    private boolean mForSlaveWifi;
    private final StateListDrawable mFrictionSld;
    protected boolean mHe8ssCapableAp;
    private final IconInjector mIconInjector;
    private boolean mIsConnected;
    private boolean mIsSlaveConnected;
    private int mLevel;
    private final Runnable mNotifyChanged;
    private boolean mShowDivider;
    private TextView mTitleView;
    protected boolean mVhtMax8SpatialStreamsSupport;
    private int mWifiSpeed;
    protected int mWifiStandard;
    private static final int[] STATE_SECURED = {R$attr.state_encrypted};
    private static final int[] STATE_METERED = {R$attr.state_metered};
    private static final int[] FRICTION_ATTRS = {R$attr.wifi_friction};
    public static final int[] WIFI_CONNECTION_STRENGTH = {R$string.accessibility_no_wifi, R$string.accessibility_wifi_one_bar, R$string.accessibility_wifi_two_bars, R$string.accessibility_wifi_three_bars, R$string.accessibility_wifi_signal_full};

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class IconInjector {
        private final Context mContext;

        public IconInjector(Context context) {
            this.mContext = context;
        }

        public Drawable getIcon(int i) {
            return this.mContext.getDrawable(Utils.getWifiIconResource(i));
        }
    }

    /* loaded from: classes2.dex */
    public static class UserBadgeCache {
        private final SparseArray<Drawable> mBadges = new SparseArray<>();
        private final PackageManager mPm;

        public UserBadgeCache(PackageManager packageManager) {
            this.mPm = packageManager;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Drawable getUserBadge(int i) {
            int indexOfKey = this.mBadges.indexOfKey(i);
            if (indexOfKey < 0) {
                Drawable userBadgeForDensity = this.mPm.getUserBadgeForDensity(new UserHandle(i), 0);
                this.mBadges.put(i, userBadgeForDensity);
                return userBadgeForDensity;
            }
            return this.mBadges.valueAt(indexOfKey);
        }
    }

    public AccessPointPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mForSavedNetworks = false;
        this.mForSlaveWifi = false;
        this.mWifiSpeed = 0;
        this.mNotifyChanged = new Runnable() { // from class: com.android.settingslib.wifi.AccessPointPreference.1
            @Override // java.lang.Runnable
            public void run() {
                AccessPointPreference.this.notifyChanged();
            }
        };
        this.mFrictionSld = null;
        this.mBadgePadding = 0;
        this.mBadgeCache = null;
        this.mIconInjector = new IconInjector(context);
    }

    public AccessPointPreference(AccessPoint accessPoint, Context context, UserBadgeCache userBadgeCache, int i, boolean z) {
        this(accessPoint, context, userBadgeCache, i, z, getFrictionStateListDrawable(context), -1, new IconInjector(context));
    }

    AccessPointPreference(AccessPoint accessPoint, Context context, UserBadgeCache userBadgeCache, int i, boolean z, StateListDrawable stateListDrawable, int i2, IconInjector iconInjector) {
        super(context);
        this.mForSavedNetworks = false;
        this.mForSlaveWifi = false;
        this.mWifiSpeed = 0;
        this.mNotifyChanged = new Runnable() { // from class: com.android.settingslib.wifi.AccessPointPreference.1
            @Override // java.lang.Runnable
            public void run() {
                AccessPointPreference.this.notifyChanged();
            }
        };
        setLayoutResource(R$layout.preference_access_point);
        setWidgetLayoutResource(getWidgetLayoutResourceId());
        this.mBadgeCache = userBadgeCache;
        this.mAccessPoint = accessPoint;
        this.mForSavedNetworks = z;
        accessPoint.setTag(this);
        this.mLevel = i2;
        this.mDefaultIconResId = i;
        this.mFrictionSld = stateListDrawable;
        this.mIconInjector = iconInjector;
        this.mBadgePadding = context.getResources().getDimensionPixelSize(R$dimen.wifi_preference_badge_padding);
    }

    private void bindFrictionImage(ImageView imageView) {
        if (imageView == null || this.mFrictionSld == null) {
            return;
        }
        if (this.mAccessPoint.getSecurity() != 0 && this.mAccessPoint.getSecurity() != 4) {
            this.mFrictionSld.setState(STATE_SECURED);
        } else if (this.mAccessPoint.isMetered()) {
            this.mFrictionSld.setState(STATE_METERED);
        }
        imageView.setImageDrawable(this.mFrictionSld.getCurrent());
    }

    static CharSequence buildContentDescription(Context context, Preference preference, AccessPoint accessPoint) {
        CharSequence title = preference.getTitle();
        CharSequence summary = preference.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            title = TextUtils.concat(title, ",", summary);
        }
        int level = accessPoint.getLevel();
        if (level >= 0) {
            int[] iArr = WIFI_CONNECTION_STRENGTH;
            if (level < iArr.length) {
                title = TextUtils.concat(title, ",", context.getString(iArr[level]));
            }
        }
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = title;
        charSequenceArr[1] = ",";
        charSequenceArr[2] = accessPoint.getSecurity() == 0 ? context.getString(R$string.accessibility_wifi_security_type_none) : context.getString(R$string.accessibility_wifi_security_type_secured);
        return TextUtils.concat(charSequenceArr);
    }

    private static StateListDrawable getFrictionStateListDrawable(Context context) {
        TypedArray typedArray;
        try {
            typedArray = context.getTheme().obtainStyledAttributes(FRICTION_ATTRS);
        } catch (Resources.NotFoundException unused) {
            typedArray = null;
        }
        if (typedArray != null) {
            return (StateListDrawable) typedArray.getDrawable(0);
        }
        return null;
    }

    private void postNotifyChanged() {
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.post(this.mNotifyChanged);
        }
    }

    private void safeSetDefaultIcon() {
        int i = this.mDefaultIconResId;
        if (i != 0) {
            setIcon(i);
        } else {
            setIcon((Drawable) null);
        }
    }

    static void setTitle(AccessPointPreference accessPointPreference, AccessPoint accessPoint) {
        accessPointPreference.setTitle(accessPoint.getTitle());
    }

    public AccessPoint getAccessPoint() {
        return this.mAccessPoint;
    }

    public String getAccessPointSummary() {
        return this.mForSavedNetworks ? this.mAccessPoint.getSavedNetworkSummary() : (this.mForSlaveWifi || (isSlaveConnected() && !isConnected())) ? this.mAccessPoint.getSlaveSettingsSummary(false) : this.mAccessPoint.getSettingsSummary();
    }

    protected int getWidgetLayoutResourceId() {
        return R$layout.access_point_friction_widget;
    }

    public boolean isConnected() {
        return this.mIsConnected;
    }

    public boolean isSlaveConnected() {
        return this.mIsSlaveConnected;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.preference.RadioButtonPreference, androidx.preference.Preference
    public void notifyChanged() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            postNotifyChanged();
        } else {
            super.notifyChanged();
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (this.mAccessPoint == null) {
            return;
        }
        Drawable icon = getIcon();
        if (icon != null) {
            icon.setLevel(this.mLevel);
        }
        this.mTitleView = (TextView) view.findViewById(16908310);
        view.setContentDescription(this.mContentDescription);
        bindFrictionImage((ImageView) view.findViewById(R$id.friction_icon));
    }

    public void onLevelChanged() {
        postNotifyChanged();
    }

    public void refresh() {
        WifiInfo info = this.mAccessPoint.getInfo();
        boolean z = info != null && ((this.mAccessPoint.networkId != -1 && info.getNetworkId() == this.mAccessPoint.networkId) || (this.mAccessPoint.isPasspoint() && this.mAccessPoint.getDetailedState() == NetworkInfo.DetailedState.CONNECTED));
        setConnected(z);
        WifiInfo slaveInfo = this.mAccessPoint.getSlaveInfo();
        setSlaveConnected((slaveInfo == null || this.mAccessPoint.networkId == -1 || slaveInfo.getNetworkId() != this.mAccessPoint.networkId) ? false : true);
        if (z && this.mForSlaveWifi) {
            setTitle(this.mAccessPoint.getPrimaryWifiTitleForSlave());
        } else {
            setTitle(this, this.mAccessPoint);
        }
        Context context = getContext();
        int level = this.mAccessPoint.getLevel();
        int speed = this.mAccessPoint.getSpeed();
        int wifiStandard = this.mAccessPoint.getWifiStandard();
        boolean isVhtMax8SpatialStreamsSupported = this.mAccessPoint.isVhtMax8SpatialStreamsSupported();
        boolean isHe8ssCapableAp = this.mAccessPoint.isHe8ssCapableAp();
        if (level != this.mLevel || speed != this.mWifiSpeed || wifiStandard != this.mWifiStandard || this.mVhtMax8SpatialStreamsSupport != isVhtMax8SpatialStreamsSupported || this.mHe8ssCapableAp != isHe8ssCapableAp) {
            this.mLevel = level;
            this.mWifiSpeed = speed;
            this.mWifiStandard = wifiStandard;
            this.mVhtMax8SpatialStreamsSupport = isVhtMax8SpatialStreamsSupported;
            this.mHe8ssCapableAp = isHe8ssCapableAp;
            updateIcon(level, wifiStandard, isVhtMax8SpatialStreamsSupported && isHe8ssCapableAp, context);
            notifyChanged();
        }
        updateBadge(context);
        setSummary(this.mForSavedNetworks ? this.mAccessPoint.getSavedNetworkSummary() : (this.mForSlaveWifi || (isSlaveConnected() && !isConnected())) ? this.mAccessPoint.getSlaveSettingsSummary(false) : this.mAccessPoint.getSettingsSummary());
        this.mContentDescription = buildContentDescription(getContext(), this, this.mAccessPoint);
    }

    public void setConnected(boolean z) {
        this.mIsConnected = z;
    }

    public void setShowDivider(boolean z) {
        this.mShowDivider = z;
        notifyChanged();
    }

    public void setSlaveConnected(boolean z) {
        this.mIsSlaveConnected = z;
    }

    protected void updateBadge(Context context) {
        WifiConfiguration config = this.mAccessPoint.getConfig();
        if (config != null) {
            this.mBadge = this.mBadgeCache.getUserBadge(config.creatorUid);
        }
    }

    protected void updateIcon(int i, int i2, boolean z, Context context) {
        if (i == -1) {
            safeSetDefaultIcon();
            return;
        }
        TronUtils.logWifiSettingsSpeed(context, this.mWifiSpeed);
        Drawable icon = this.mIconInjector.getIcon(i);
        if (this.mForSavedNetworks || icon == null) {
            safeSetDefaultIcon();
            return;
        }
        icon.setTintList(Utils.getColorAttr(context, 16843817));
        setIcon(icon);
    }
}
