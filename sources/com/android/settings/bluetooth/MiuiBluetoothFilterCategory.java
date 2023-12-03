package com.android.settings.bluetooth;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.miuisettings.preference.PreferenceCategory;
import java.util.ArrayList;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;

/* loaded from: classes.dex */
public class MiuiBluetoothFilterCategory extends PreferenceCategory implements View.OnClickListener {
    private ImageView mAnimationBg;
    private Runnable mAnimationRunnable;
    private Drawable mDrawable;
    private Handler mHandler;
    private ImageView mImageView;
    private final Object mLock;
    private View.OnClickListener mOnSettingsClickListener;
    private boolean mRarelyPreferenceAdded;
    private int mRarelyUsedDeviceCount;
    private MiuiMiscBluetoothPreference mRarelyUsedDevicePreference;
    private List<CachedBluetoothDevice> mRarelyUsedDevices;
    private boolean mShowDevicesWithoutNames;
    private boolean mShowDivider;
    private Runnable mStopRunnable;
    private List<CachedBluetoothDevice> mUsableDevices;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MiuiBluetoothFilterCategory(Context context) {
        this(context, null);
    }

    public MiuiBluetoothFilterCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MiuiBluetoothFilterCategory(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public MiuiBluetoothFilterCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mShowDevicesWithoutNames = false;
        this.mShowDivider = false;
        this.mRarelyPreferenceAdded = false;
        this.mRarelyUsedDeviceCount = 0;
        this.mLock = new Object();
        this.mAnimationRunnable = new Runnable() { // from class: com.android.settings.bluetooth.MiuiBluetoothFilterCategory.1
            @Override // java.lang.Runnable
            public void run() {
                MiuiBluetoothFilterCategory.this.playAnimationImmediately();
            }
        };
        this.mStopRunnable = new Runnable() { // from class: com.android.settings.bluetooth.MiuiBluetoothFilterCategory.2
            @Override // java.lang.Runnable
            public void run() {
                MiuiBluetoothFilterCategory.this.stopAnimationImmediately();
            }
        };
        setLayoutResource(R.layout.preference_bt_category_filter);
        setKey("available_devices_category");
        this.mRarelyPreferenceAdded = false;
        this.mRarelyUsedDevicePreference = new MiuiMiscBluetoothPreference(getContext(), 0);
        this.mRarelyUsedDeviceCount = 0;
        this.mUsableDevices = new ArrayList();
        this.mRarelyUsedDevices = new ArrayList();
        this.mHandler = new Handler(context.getMainLooper());
    }

    private void playAnimationDelayed() {
        this.mHandler.removeCallbacks(this.mAnimationRunnable);
        this.mHandler.postDelayed(this.mAnimationRunnable, 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playAnimationImmediately() {
        if (this.mDrawable.getCallback() == null) {
            Log.w("MiuiBluetoothFilterCate", "playAnimationImmediately: callback is null");
        } else {
            ((AnimatedVectorDrawable) this.mDrawable).start();
        }
    }

    private void removeDevicePreferenceCategory(CachedBluetoothDevice cachedBluetoothDevice) {
        synchronized (this.mLock) {
            if (this.mUsableDevices.contains(cachedBluetoothDevice)) {
                this.mUsableDevices.remove(cachedBluetoothDevice);
            }
            if (this.mRarelyUsedDevices.contains(cachedBluetoothDevice)) {
                this.mRarelyUsedDevices.remove(cachedBluetoothDevice);
            }
        }
    }

    private void setAlphaFolme(View view) {
        if (view == null) {
            return;
        }
        Folme.useAt(view).touch().setAlpha(0.6f, ITouchStyle.TouchType.DOWN).handleTouchOf(view, this.mOnSettingsClickListener, new AnimConfig[0]);
    }

    private void stopAnimationDelayed() {
        this.mHandler.removeCallbacks(this.mStopRunnable);
        this.mHandler.postDelayed(this.mStopRunnable, 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopAnimationImmediately() {
        if (this.mDrawable.getCallback() == null) {
            Log.w("MiuiBluetoothFilterCate", "stopAnimationImmediately: callback is null");
        } else {
            ((AnimatedVectorDrawable) this.mDrawable).stop();
        }
    }

    public void addDeviceCache(CachedBluetoothDevice cachedBluetoothDevice) {
        synchronized (this.mLock) {
            if (!this.mRarelyUsedDevices.contains(cachedBluetoothDevice) && MiuiBTUtils.isVisibleDevice(this.mShowDevicesWithoutNames, cachedBluetoothDevice)) {
                this.mRarelyUsedDevices.add(cachedBluetoothDevice);
                updateRarelyUsedDevicePreference();
                if (!this.mUsableDevices.contains(cachedBluetoothDevice)) {
                    this.mUsableDevices.add(cachedBluetoothDevice);
                }
            }
        }
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean addPreference(Preference preference) {
        return (preference instanceof BluetoothDevicePreference) && super.addPreference(preference);
    }

    @Override // androidx.preference.PreferenceCategory, androidx.preference.Preference
    public boolean isEnabled() {
        return true;
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceCategory, androidx.preference.PreferenceCategory, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        View view = preferenceViewHolder.itemView;
        if (view != null) {
            View findViewById = view.findViewById(R.id.divider);
            if (findViewById != null) {
                if (this.mShowDivider) {
                    findViewById.setVisibility(0);
                } else {
                    findViewById.setVisibility(8);
                }
            }
            this.mImageView = (ImageView) view.findViewById(R.id.refresh_anim);
            this.mAnimationBg = (ImageView) view.findViewById(R.id.refresh_anim_bg);
            ImageView imageView = this.mImageView;
            if (imageView != null) {
                this.mDrawable = imageView.getDrawable();
                this.mImageView.setOnClickListener(this);
            }
            ImageView imageView2 = this.mAnimationBg;
            if (imageView2 != null) {
                imageView2.setOnClickListener(this);
            }
            setAlphaFolme(this.mAnimationBg);
        }
        super.onBindViewHolder(preferenceViewHolder);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        View.OnClickListener onClickListener = this.mOnSettingsClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    public void playAnimation() {
        playAnimationDelayed();
    }

    @Override // androidx.preference.PreferenceGroup
    public void removeAll() {
        super.removeAll();
        synchronized (this.mLock) {
            this.mRarelyUsedDeviceCount = 0;
            this.mRarelyPreferenceAdded = false;
            this.mUsableDevices.clear();
            this.mRarelyUsedDevices.clear();
        }
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean removePreference(Preference preference) {
        if (preference instanceof BluetoothDevicePreference) {
            removeDevicePreferenceCategory(((BluetoothDevicePreference) preference).getCachedDevice());
        }
        return super.removePreference(preference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCount(int i) {
        if (this.mRarelyUsedDevicePreference != null) {
            Log.d("MiuiBluetoothFilterCate", "set rarely used Device Count: " + i);
            this.mRarelyUsedDevicePreference.setDeviceCount(i);
        }
    }

    public void setOnSettingsClickListener(View.OnClickListener onClickListener) {
        this.mOnSettingsClickListener = onClickListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShowDevicesWithoutNames(boolean z) {
        Log.d("MiuiBluetoothFilterCate", "setShowDevicesWithoutNames = [" + z + "]");
        this.mShowDevicesWithoutNames = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShowDivider(boolean z) {
        this.mShowDivider = z;
    }

    public void stopAnimation() {
        this.mHandler.removeCallbacks(this.mAnimationRunnable);
        stopAnimationDelayed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateRarelyUsedDevicePreference() {
        synchronized (this.mLock) {
            int size = this.mRarelyUsedDevices.size();
            if (size > 0 && this.mRarelyUsedDeviceCount != size) {
                this.mRarelyUsedDeviceCount = size;
                if (!this.mRarelyPreferenceAdded) {
                    super.addPreference(this.mRarelyUsedDevicePreference);
                    this.mRarelyPreferenceAdded = true;
                }
                this.mRarelyUsedDevicePreference.setDeviceCount(size);
            } else if (this.mRarelyPreferenceAdded && (size == 0 || this.mRarelyUsedDeviceCount == 0)) {
                super.removePreference(this.mRarelyUsedDevicePreference);
                this.mRarelyPreferenceAdded = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateRefreshUI(boolean z) {
        if (this.mDrawable != null) {
            if (z) {
                playAnimation();
            } else {
                stopAnimation();
            }
        }
    }
}
