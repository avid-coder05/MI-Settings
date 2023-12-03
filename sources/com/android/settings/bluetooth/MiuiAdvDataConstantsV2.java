package com.android.settings.bluetooth;

import android.os.ParcelUuid;
import miui.bluetooth.ble.MiServiceData;

/* loaded from: classes.dex */
public class MiuiAdvDataConstantsV2 {
    static final byte[] ID_CODE_XIAO_MI = {77};
    static final byte[] ID_CODE_MIOT = {22};
    static final ParcelUuid UUID_SERVICE_FAST_PAIR = ParcelUuid.fromString("0000fd2d-0000-1000-8000-00805f9b34fb");
    static final ParcelUuid UUID_CHAR_MODELID = ParcelUuid.fromString("0000ff10-0000-1000-8000-00805f9b34fb");
    static final ParcelUuid UUID_CHAR_KEYBASEDPAIRING = ParcelUuid.fromString("0000ff11-0000-1000-8000-00805f9b34fb");
    static final ParcelUuid UUID_CHAR_CCCD = ParcelUuid.fromString("00002902-0000-1000-8000-00805f9b34fb");
    static final ParcelUuid UUID_CHAR_PASSKEY = ParcelUuid.fromString("0000ff12-0000-1000-8000-00805f9b34fb");
    static final ParcelUuid UUID_CHAR_ACCOUNTKEY = ParcelUuid.fromString("0000ff13-0000-1000-8000-00805f9b34fb");
    static final ParcelUuid UUID_FIRMWARE_REVISION = ParcelUuid.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    static final ParcelUuid UUID_CCCD = ParcelUuid.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static byte[] PAIRING_REQUEST = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -60, -61};
    public static byte[] PASSKEY = {2, 48, 32, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -60, -61};
    public static byte[] PAIRING_REQUEST_HEADER = {0, 1};
    public static byte[] ACCOUNT_KEY_HEADER = {4};
    public static byte[] REMOTE_PUBLIC_KEY = {48, 89, 48, 19, 6, 7, 42, -122, 72, -50, 61, 2, 1, 6, 8, 42, -122, 72, -50, 61, 3, 1, 7, 3, 66, 0, 4, -9, -44, -106, -90, 46, -54, 65, 99, 81, 84, 10, -93, 67, -68, 105, 10, 97, 9, -11, 81, 80, 6, 102, -72, 59, 18, 81, -5, -124, -6, 40, 96, 121, 94, -67, 99, -45, -72, -125, 111, 68, -87, -93, -30, -117, -77, 64, 23, -32, 21, -11, -105, -109, 5, -40, 73, -3, -8, -34, 16, 18, 59, 97, -46};
    public static byte[] PRIVATE_HEADER_1 = {48, -127, -121, 2, 1, 0, 48, 19, 6, 7, 42, -122, 72, -50, 61, 2, 1, 6, 8, 42, -122, 72, -50, 61, 3, 1, 7, 4, 109, 48, 107, 2, 1, 1, 4, 32};
    public static byte[] PRIVATE_HEADER_2 = {-95, 68, 3, 66, 0, 4};
    public static byte[] X509_HEADER = {48, 89, 48, 19, 6, 7, 42, -122, 72, -50, 61, 2, 1, 6, 8, 42, -122, 72, -50, 61, 3, 1, 7, 3, 66, 0, 4};
    public static byte[] BOB_PRIVATE_DATA = {2, -76, 55, -80, -19, -42, -69, -44, 41, 6, 74, 78, 82, -97, -53, -15, -60, -115, 13, 98, 73, 36, -43, -110, 39, 75, 126, -40, 17, -109, -41, 99};
    public static byte[] REMOTE_PUBLIC_KEY_RAW = {-9, -44, -106, -90, 46, -54, 65, 99, 81, 84, 10, -93, 67, -68, 105, 10, 97, 9, -11, 81, 80, 6, 102, -72, 59, 18, 81, -5, -124, -6, 40, 96, 121, 94, -67, 99, -45, -72, -125, 111, 68, -87, -93, -30, -117, -77, 64, 23, -32, 21, -11, -105, -109, 5, -40, 73, -3, -8, -34, 16, 18, 59, 97, -46};
    public static byte[] ALICE_PRIVATE_DATA = {-41, 94, 84, -57, 125, 118, 36, -119, -27, 124, -6, -110, 55, 67, -15, 103, 119, -92, 40, 61, -103, Byte.MIN_VALUE, 11, -84, 85, 88, 72, 56, -109, -27, -80, 109};
    public static byte[] ALICE_PUBLIC_DATA = {54, -84, 104, 44, 80, -126, 21, 102, -113, -66, -2, 36, 125, 1, -43, -21, -106, -26, 49, -114, -123, 91, 45, 100, -75, 25, 93, 56, -18, 126, 55, -66, MiServiceData.CAPABILITY_IO, 56, -64, -71, 72, -61, -9, 85, 32, -32, 126, 112, -16, 114, -111, 65, -102, -50, 45, 40, 20, 60, 90, -37, 45, -67, -104, -18, 60, -114, 79, -65};
    public static byte[] ENABLE_CCCD = {2, 0};

    private MiuiAdvDataConstantsV2() {
    }

    public static void printBytes(String str, byte[] bArr) {
        if (bArr == null) {
            System.out.println(str + "is null");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            sb.append(String.format("(byte)0x%02X, ", Byte.valueOf(b)));
        }
        System.out.println(str + sb.toString());
    }
}
