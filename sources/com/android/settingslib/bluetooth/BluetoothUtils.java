package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.DeviceConfig;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.R$array;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$string;
import com.android.settingslib.widget.AdaptiveIcon;
import com.android.settingslib.widget.AdaptiveOutlineDrawable;
import java.io.IOException;
import java.util.List;

/* loaded from: classes2.dex */
public class BluetoothUtils {
    public static final boolean DEBUG = SystemProperties.get("persist.vendor.bluetooth.hostloglevel", "").equals("sqc");
    private static ErrorListener sErrorListener;

    /* loaded from: classes2.dex */
    public interface ErrorListener {
        void onShowError(Context context, String str, int i);
    }

    public static Drawable buildBtRainbowDrawable(Context context, Drawable drawable, int i) {
        Resources resources = context.getResources();
        int[] intArray = resources.getIntArray(R$array.bt_icon_fg_colors);
        int[] intArray2 = resources.getIntArray(R$array.bt_icon_bg_colors);
        int abs = Math.abs(i % intArray2.length);
        drawable.setTint(intArray[abs]);
        AdaptiveIcon adaptiveIcon = new AdaptiveIcon(context, drawable);
        adaptiveIcon.setBackgroundColor(intArray2[abs]);
        return adaptiveIcon;
    }

    public static Drawable getBluetoothDrawable(Context context, int i) {
        return context.getDrawable(i);
    }

    public static boolean getBooleanMetaData(BluetoothDevice bluetoothDevice, int i) {
        byte[] metadata;
        if (bluetoothDevice == null || (metadata = bluetoothDevice.getMetadata(i)) == null) {
            return false;
        }
        return Boolean.parseBoolean(new String(metadata));
    }

    public static Pair<Drawable, String> getBtClassDrawableWithDescription(Context context, BluetoothClass bluetoothClass, List<LocalBluetoothProfile> list) {
        if (bluetoothClass != null) {
            int majorDeviceClass = bluetoothClass.getMajorDeviceClass();
            if (majorDeviceClass == 256) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_laptop_bonded), context.getString(R$string.bluetooth_talkback_computer));
            }
            if (majorDeviceClass == 512) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_cellphone_bonded), context.getString(R$string.bluetooth_talkback_phone));
            }
            if (majorDeviceClass == 1280) {
                return new Pair<>(getBluetoothDrawable(context, HidProfile.getHidClassDrawable(bluetoothClass, true)), context.getString(R$string.bluetooth_talkback_input_peripheral));
            }
            if (majorDeviceClass == 1536) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_imaging_bonded), context.getString(R$string.bluetooth_talkback_imaging));
            }
            if (majorDeviceClass == 5376) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_hearing_aid), null);
            }
        }
        if (list != null) {
            for (LocalBluetoothProfile localBluetoothProfile : list) {
                int drawableResource = localBluetoothProfile.getDrawableResource(bluetoothClass);
                if (localBluetoothProfile instanceof HidProfile) {
                    drawableResource = HidProfile.getHidClassDrawable(bluetoothClass, true);
                } else if (localBluetoothProfile instanceof HeadsetProfile) {
                    drawableResource = ((HeadsetProfile) localBluetoothProfile).getDrawableResource(bluetoothClass, true);
                } else if (localBluetoothProfile instanceof A2dpProfile) {
                    drawableResource = ((A2dpProfile) localBluetoothProfile).getDrawableResource(bluetoothClass, true);
                } else if (localBluetoothProfile instanceof PanProfile) {
                    drawableResource = ((PanProfile) localBluetoothProfile).getDrawableResource(bluetoothClass, true);
                } else if (localBluetoothProfile instanceof DunServerProfile) {
                    drawableResource = ((DunServerProfile) localBluetoothProfile).getDrawableResource(bluetoothClass, true);
                }
                if (drawableResource != 0) {
                    return new Pair<>(getBluetoothDrawable(context, drawableResource), null);
                }
            }
        }
        if (bluetoothClass != null) {
            if (bluetoothClass.doesClassMatch(0)) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_headset_hfp_bonded), context.getString(R$string.bluetooth_talkback_headset));
            }
            if (bluetoothClass.doesClassMatch(1)) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_headphones_a2dp_bonded), context.getString(R$string.bluetooth_talkback_headphone));
            }
        }
        return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_bluetooth_bonded), context.getString(R$string.bluetooth_talkback_bluetooth));
    }

    public static Pair<Drawable, String> getBtClassDrawableWithDescription(Context context, CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
        boolean z = cachedBluetoothDevice.getBondState() == 12;
        if (btClass != null) {
            int majorDeviceClass = btClass.getMajorDeviceClass();
            if (majorDeviceClass == 256) {
                return new Pair<>(getBluetoothDrawable(context, z ? R$drawable.ic_bt_laptop_bonded : R$drawable.ic_bt_laptop), context.getString(R$string.bluetooth_talkback_computer));
            } else if (majorDeviceClass == 512) {
                return new Pair<>(getBluetoothDrawable(context, z ? R$drawable.ic_bt_cellphone_bonded : R$drawable.ic_bt_cellphone), context.getString(R$string.bluetooth_talkback_phone));
            } else if (majorDeviceClass == 1280) {
                return new Pair<>(getBluetoothDrawable(context, HidProfile.getHidClassDrawable(btClass, z)), context.getString(R$string.bluetooth_talkback_input_peripheral));
            } else {
                if (majorDeviceClass == 1536) {
                    return new Pair<>(getBluetoothDrawable(context, z ? R$drawable.ic_bt_imaging_bonded : R$drawable.ic_bt_imaging), context.getString(R$string.bluetooth_talkback_imaging));
                } else if (majorDeviceClass == 5376) {
                    return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_hearing_aid), null);
                }
            }
        }
        for (LocalBluetoothProfile localBluetoothProfile : cachedBluetoothDevice.getProfiles()) {
            int drawableResource = localBluetoothProfile.getDrawableResource(btClass);
            if (localBluetoothProfile instanceof HidProfile) {
                drawableResource = HidProfile.getHidClassDrawable(btClass, z);
            } else if (localBluetoothProfile instanceof HeadsetProfile) {
                drawableResource = ((HeadsetProfile) localBluetoothProfile).getDrawableResource(btClass, z);
            } else if (localBluetoothProfile instanceof A2dpProfile) {
                drawableResource = ((A2dpProfile) localBluetoothProfile).getDrawableResource(btClass, z);
            } else if (localBluetoothProfile instanceof PanProfile) {
                drawableResource = ((PanProfile) localBluetoothProfile).getDrawableResource(btClass, z);
            } else if (localBluetoothProfile instanceof DunServerProfile) {
                drawableResource = ((DunServerProfile) localBluetoothProfile).getDrawableResource(btClass, z);
            }
            if (drawableResource != 0) {
                return new Pair<>(getBluetoothDrawable(context, drawableResource), null);
            }
        }
        if (btClass != null) {
            if (btClass.doesClassMatch(0)) {
                return new Pair<>(getBluetoothDrawable(context, z ? R$drawable.ic_bt_headset_hfp_bonded : R$drawable.ic_bt_headset_hfp), context.getString(R$string.bluetooth_talkback_headset));
            } else if (btClass.doesClassMatch(1)) {
                return new Pair<>(getBluetoothDrawable(context, z ? R$drawable.ic_bt_headphones_a2dp_bonded : R$drawable.ic_bt_headphones_a2dp), context.getString(R$string.bluetooth_talkback_headphone));
            }
        }
        return new Pair<>(getBluetoothDrawable(context, z ? R$drawable.ic_bt_bluetooth_bonded : R$drawable.ic_bt_bluetooth), context.getString(R$string.bluetooth_talkback_bluetooth));
    }

    public static Pair<Drawable, String> getBtDrawableWithDescription(Context context, CachedBluetoothDevice cachedBluetoothDevice) {
        Uri uriMetaData;
        Pair<Drawable, String> btClassDrawableWithDescription = getBtClassDrawableWithDescription(context, cachedBluetoothDevice);
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.bt_nearby_icon_size);
        Resources resources = context.getResources();
        if (isAdvancedDetailsHeader(device) && (uriMetaData = getUriMetaData(device, 5)) != null) {
            try {
                context.getContentResolver().takePersistableUriPermission(uriMetaData, 1);
            } catch (SecurityException e) {
                Log.e("BluetoothUtils", "Failed to take persistable permission for: " + uriMetaData, e);
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriMetaData);
                if (bitmap != null) {
                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, dimensionPixelSize, dimensionPixelSize, false);
                    bitmap.recycle();
                    return new Pair<>(new BitmapDrawable(resources, createScaledBitmap), (String) btClassDrawableWithDescription.second);
                }
            } catch (IOException e2) {
                Log.e("BluetoothUtils", "Failed to get drawable for: " + uriMetaData, e2);
            } catch (SecurityException e3) {
                Log.e("BluetoothUtils", "Failed to get permission for: " + uriMetaData, e3);
            }
        }
        return new Pair<>((Drawable) btClassDrawableWithDescription.first, (String) btClassDrawableWithDescription.second);
    }

    public static Pair<Drawable, String> getBtRainbowDrawableWithDescription(Context context, CachedBluetoothDevice cachedBluetoothDevice) {
        Resources resources = context.getResources();
        Pair<Drawable, String> btDrawableWithDescription = getBtDrawableWithDescription(context, cachedBluetoothDevice);
        return btDrawableWithDescription.first instanceof BitmapDrawable ? new Pair<>(new AdaptiveOutlineDrawable(resources, ((BitmapDrawable) btDrawableWithDescription.first).getBitmap()), (String) btDrawableWithDescription.second) : new Pair<>(buildBtRainbowDrawable(context, (Drawable) btDrawableWithDescription.first, cachedBluetoothDevice.getAddress().hashCode()), (String) btDrawableWithDescription.second);
    }

    public static int getConnectionStateSummary(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return 0;
                    }
                    return R$string.bluetooth_disconnecting;
                }
                return R$string.bluetooth_connected;
            }
            return R$string.bluetooth_connecting;
        }
        return R$string.bluetooth_disconnected;
    }

    public static int getIntMetaData(BluetoothDevice bluetoothDevice, int i) {
        byte[] metadata;
        if (bluetoothDevice == null || (metadata = bluetoothDevice.getMetadata(i)) == null) {
            return -1;
        }
        try {
            return Integer.parseInt(new String(metadata));
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    public static String getStringMetaData(BluetoothDevice bluetoothDevice, int i) {
        byte[] metadata;
        if (bluetoothDevice == null || (metadata = bluetoothDevice.getMetadata(i)) == null) {
            return null;
        }
        return new String(metadata);
    }

    public static Uri getUriMetaData(BluetoothDevice bluetoothDevice, int i) {
        String stringMetaData = getStringMetaData(bluetoothDevice, i);
        if (stringMetaData == null) {
            return null;
        }
        return Uri.parse(stringMetaData);
    }

    public static boolean isAdvancedDetailsHeader(BluetoothDevice bluetoothDevice) {
        if (!DeviceConfig.getBoolean("settings_ui", "bt_advanced_header_enabled", true)) {
            Log.d("BluetoothUtils", "isAdvancedDetailsHeader: advancedEnabled is false");
            return false;
        } else if (getBooleanMetaData(bluetoothDevice, 6)) {
            Log.d("BluetoothUtils", "isAdvancedDetailsHeader: untetheredHeadset is true");
            return true;
        } else {
            String stringMetaData = getStringMetaData(bluetoothDevice, 17);
            if (TextUtils.equals(stringMetaData, "Untethered Headset") || TextUtils.equals(stringMetaData, "Watch") || TextUtils.equals(stringMetaData, "Default")) {
                Log.d("BluetoothUtils", "isAdvancedDetailsHeader: deviceType is " + stringMetaData);
                return true;
            }
            return false;
        }
    }

    public static void setErrorListener(ErrorListener errorListener) {
        sErrorListener = errorListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void showError(Context context, String str, int i) {
        ErrorListener errorListener = sErrorListener;
        if (errorListener != null) {
            errorListener.onShowError(context, str, i);
        }
    }
}
