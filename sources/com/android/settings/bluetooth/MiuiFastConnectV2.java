package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import com.milink.api.v1.type.DeviceType;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.util.HashUtils;

/* loaded from: classes.dex */
public class MiuiFastConnectV2 {
    private static MiuiFastConnectV2 mMiuiFastConnectV2;
    private byte[] mAES128Key;
    private byte[] mAccountKey;
    private BluetoothGattCharacteristic mAccountKeyCharacteristic;
    private String mAccountKeyCloudScrambled;
    private BluetoothGattDescriptor mAccountkeyCCCD;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothManager mBluetoothManager;
    private Context mContext;
    private ServiceMessageHandler mHandler;
    private BluetoothGattCharacteristic mKeyBasedCharacteristic;
    private PublicKey mLocalPublicKey;
    private byte[] mLocalPublickeyByte;
    private MiuiOnLineBluetoothDevicePreference mMiuiOnLineBluetoothDevicePreference;
    private PairingChangeReceiver mPairingChangeReceiver;
    private BluetoothGattDescriptor mPasskeyCCCD;
    private BluetoothGattCharacteristic mPasskeyCharacteristic;
    private BluetoothDevice mRemoteDeviceBREDR;
    private BluetoothDevice mRemoteDeviceLE;
    private ScanCallback mScanCallback;
    private byte[] mSeekerPasskey;
    private List<BluetoothGattService> mServiceList;
    private BluetoothGattDescriptor mkeybasedCCCD;
    private final String TAG = "MiuiFastConnectV2_Settings";
    private boolean DBG = true;
    private KeyPairGenerator kpg = null;
    private KeyPair kp = null;
    private Object mHandlerLock = new Object();
    private HashMap<String, String> mHashmapCloudInfo = new HashMap<>();
    private boolean mPairing = false;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() { // from class: com.android.settings.bluetooth.MiuiFastConnectV2.1
        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "onCharacteristicChanged " + bluetoothGattCharacteristic.getUuid());
            }
            if (bluetoothGattCharacteristic.getUuid().equals(MiuiAdvDataConstantsV2.UUID_CHAR_KEYBASEDPAIRING.getUuid())) {
                byte[] value = bluetoothGattCharacteristic.getValue();
                if (MiuiFastConnectV2.this.DBG) {
                    MiuiAdvDataConstantsV2.printBytes("onCharacteristicChanged: KEYBASEDPAIRING", value);
                }
                MiuiFastConnectV2.this.kbpResponseHandle(value);
            } else if (!bluetoothGattCharacteristic.getUuid().equals(MiuiAdvDataConstantsV2.UUID_CHAR_PASSKEY.getUuid())) {
                if (bluetoothGattCharacteristic.getUuid().equals(MiuiAdvDataConstantsV2.UUID_CHAR_ACCOUNTKEY.getUuid())) {
                    if (MiuiFastConnectV2.this.DBG) {
                        Log.d("MiuiFastConnectV2_Settings", "received account key notification!");
                    }
                    byte[] value2 = bluetoothGattCharacteristic.getValue();
                    if (MiuiFastConnectV2.this.DBG) {
                        MiuiAdvDataConstantsV2.printBytes("characteristic.getValue(): ", value2);
                    }
                    byte[] decrypt = MiuiAES.decrypt(value2, MiuiFastConnectV2.this.mAES128Key);
                    if (MiuiFastConnectV2.this.DBG) {
                        MiuiAdvDataConstantsV2.printBytes("account key notification: ", decrypt);
                    }
                    MiuiFastConnectV2.this.pairDone();
                }
            } else {
                byte[] decrypt2 = MiuiAES.decrypt(bluetoothGattCharacteristic.getValue(), MiuiFastConnectV2.this.mAES128Key);
                byte[] bArr = new byte[3];
                if (decrypt2.length > 4) {
                    bArr[0] = decrypt2[1];
                    bArr[1] = decrypt2[2];
                    bArr[2] = decrypt2[3];
                }
                if (MiuiFastConnectV2.this.DBG) {
                    MiuiAdvDataConstantsV2.printBytes("Provider's passkey: ", bArr);
                }
                if (MiuiFastConnectV2.this.DBG) {
                    MiuiAdvDataConstantsV2.printBytes("Seek's passkey: ", MiuiFastConnectV2.this.mSeekerPasskey);
                }
                if (Arrays.equals(bArr, MiuiFastConnectV2.this.mSeekerPasskey)) {
                    MiuiFastConnectV2.this.mRemoteDeviceBREDR.setPairingConfirmation(true);
                    if (MiuiFastConnectV2.this.DBG) {
                        Log.d("MiuiFastConnectV2_Settings", "passkey is same, setPairingConfirmation!");
                    }
                }
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "onCharacteristicRead: " + bluetoothGattCharacteristic.getUuid() + "  " + bluetoothGattCharacteristic.getValue() + " status:" + i);
            }
            if (i == 0 && MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "onCharacteristicRead SUCCESS");
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "onCharacteristicWrite check " + bluetoothGattCharacteristic.getUuid() + " value:" + bluetoothGattCharacteristic.getValue());
            }
            boolean executeReliableWrite = MiuiFastConnectV2.this.mBluetoothGatt.executeReliableWrite();
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", ": executeReliableWrite: " + executeReliableWrite);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "onConnectionStateChange oldstatus: " + i + " newState: " + i2);
            }
            if (i2 == 2) {
                if (MiuiFastConnectV2.this.DBG) {
                    Log.d("MiuiFastConnectV2_Settings", "Connected to GATT server.");
                }
                MiuiFastConnectV2.this.mBluetoothGatt.discoverServices();
            } else if (i2 == 0 && MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "Disconnected from GATT server.");
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "onDescriptorRead status:" + i);
            }
            byte[] value = bluetoothGattDescriptor.getValue();
            if (MiuiFastConnectV2.this.DBG) {
                MiuiAdvDataConstantsV2.printBytes("read descriptor value: ", value);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "onDescriptorWrite status:" + i);
            }
            byte[] value = bluetoothGattDescriptor.getValue();
            if (MiuiFastConnectV2.this.DBG) {
                MiuiAdvDataConstantsV2.printBytes("write descriptor value: ", value);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onReliableWriteCompleted(BluetoothGatt bluetoothGatt, int i) {
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", ": onReliableWriteCompleted: " + i);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "onServicesDiscovered:" + i);
            }
            if (i == 0) {
                MiuiFastConnectV2.this.mServiceList = bluetoothGatt.getServices();
                if (MiuiFastConnectV2.this.mServiceList != null && MiuiFastConnectV2.this.DBG) {
                    if (MiuiFastConnectV2.this.DBG) {
                        Log.d("MiuiFastConnectV2_Settings", "Services num: " + MiuiFastConnectV2.this.mServiceList.size());
                    }
                    if (MiuiFastConnectV2.this.DBG) {
                        Log.d("MiuiFastConnectV2_Settings", "mServiceList: " + MiuiFastConnectV2.this.mServiceList + " Services num:" + MiuiFastConnectV2.this.mServiceList.size());
                    }
                }
                for (BluetoothGattService bluetoothGattService : MiuiFastConnectV2.this.mServiceList) {
                    if (MiuiFastConnectV2.this.DBG) {
                        Log.d("MiuiFastConnectV2_Settings", "Service: " + bluetoothGattService.getUuid());
                    }
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                        if (MiuiFastConnectV2.this.DBG) {
                            Log.d("MiuiFastConnectV2_Settings", "    characteristic: " + bluetoothGattCharacteristic.getUuid());
                        }
                        if (bluetoothGattCharacteristic.getUuid().equals(MiuiAdvDataConstantsV2.UUID_CHAR_KEYBASEDPAIRING.getUuid())) {
                            MiuiFastConnectV2.this.mKeyBasedCharacteristic = bluetoothGattCharacteristic;
                            Log.d("MiuiFastConnectV2_Settings", "key base: " + MiuiFastConnectV2.this.mKeyBasedCharacteristic.getProperties());
                            for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()) {
                                if (bluetoothGattDescriptor.getUuid().equals(MiuiAdvDataConstantsV2.UUID_CCCD.getUuid())) {
                                    MiuiFastConnectV2.this.mkeybasedCCCD = bluetoothGattDescriptor;
                                    MiuiFastConnectV2.this.sendMessageDelay(102, 0L);
                                    if (MiuiFastConnectV2.this.DBG) {
                                        Log.d("MiuiFastConnectV2_Settings", "send MSG_ENABLE_KBP_CCCD ");
                                    }
                                }
                            }
                        }
                        if (bluetoothGattCharacteristic.getUuid().equals(MiuiAdvDataConstantsV2.UUID_CHAR_PASSKEY.getUuid())) {
                            MiuiFastConnectV2.this.mPasskeyCharacteristic = bluetoothGattCharacteristic;
                            Iterator<BluetoothGattDescriptor> it = bluetoothGattCharacteristic.getDescriptors().iterator();
                            while (it.hasNext()) {
                                MiuiFastConnectV2.this.mPasskeyCCCD = it.next();
                                MiuiFastConnectV2.this.sendMessageDelay(103, 300L);
                                if (MiuiFastConnectV2.this.DBG) {
                                    Log.d("MiuiFastConnectV2_Settings", "send MSG_ENABLE_PASSKEY_CCCD ");
                                }
                            }
                        }
                        if (bluetoothGattCharacteristic.getUuid().equals(MiuiAdvDataConstantsV2.UUID_CHAR_ACCOUNTKEY.getUuid())) {
                            MiuiFastConnectV2.this.mAccountKeyCharacteristic = bluetoothGattCharacteristic;
                            Iterator<BluetoothGattDescriptor> it2 = bluetoothGattCharacteristic.getDescriptors().iterator();
                            while (it2.hasNext()) {
                                MiuiFastConnectV2.this.mAccountkeyCCCD = it2.next();
                                MiuiFastConnectV2.this.sendMessageDelay(117, 400L);
                                if (MiuiFastConnectV2.this.DBG) {
                                    Log.d("MiuiFastConnectV2_Settings", "send MSG_ENABLE_ACCOUNTKEY_CCCD ");
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    /* loaded from: classes.dex */
    private class LeScanCallback extends ScanCallback {
        private LeScanCallback() {
        }

        @Override // android.bluetooth.le.ScanCallback
        public void onScanResult(int i, ScanResult scanResult) {
            if (scanResult != null) {
                if (MiuiFastConnectV2.this.DBG) {
                    Log.d("MiuiFastConnectV2_Settings", "onScanResult: " + scanResult.getDevice().getName() + "  " + scanResult.getDevice().getAddress());
                }
                byte[] accountKeyFromScanResult = MiuiFastConnectV2.this.getAccountKeyFromScanResult(scanResult);
                if (MiuiFastConnectV2.this.DBG) {
                    MiuiAdvDataConstantsV2.printBytes("result: scrambledAccountKey", accountKeyFromScanResult);
                }
                if (MiuiFastConnectV2.this.DBG) {
                    Log.d("MiuiFastConnectV2_Settings", "mAccountKeyCloudScrambled: " + MiuiFastConnectV2.this.mAccountKeyCloudScrambled);
                }
                if (accountKeyFromScanResult == null || !MiuiFastConnectV2.this.mAccountKeyCloudScrambled.equals(MiuiFastConnectV2.bytesToHexString(accountKeyFromScanResult))) {
                    Log.e("MiuiFastConnectV2_Settings", "the device not found !!!!");
                    return;
                }
                if (MiuiFastConnectV2.this.DBG) {
                    Log.d("MiuiFastConnectV2_Settings", "found the device ");
                }
                MiuiFastConnectV2.this.mRemoteDeviceLE = scanResult.getDevice();
                MiuiFastConnectV2.this.mBluetoothGatt = scanResult.getDevice().connectGatt(MiuiFastConnectV2.this.mContext, false, MiuiFastConnectV2.this.mGattCallback);
                MiuiFastConnectV2.this.mBluetoothLeScanner.stopScan(MiuiFastConnectV2.this.mScanCallback);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class PairingChangeReceiver extends BroadcastReceiver {
        PairingChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (MiuiFastConnectV2.this.mPairing) {
                Bundle extras = intent.getExtras();
                int i = extras.getInt("android.bluetooth.device.extra.PAIRING_KEY");
                if (MiuiFastConnectV2.this.DBG) {
                    Log.d("MiuiFastConnectV2_Settings", "passkey :" + i);
                }
                Message obtain = Message.obtain();
                obtain.what = 105;
                obtain.setData(extras);
                MiuiFastConnectV2.this.mHandler.sendMessageDelayed(obtain, 100L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ServiceMessageHandler extends Handler {
        private ServiceMessageHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (MiuiFastConnectV2.this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "ServiceMessageHandler what= " + i);
            }
            if (i == 1) {
                if (MiuiFastConnectV2.this.DBG) {
                    Log.d("MiuiFastConnectV2_Settings", " received MSG_BLE_SCAN_START");
                }
                MiuiFastConnectV2.this.performLEScan();
                MiuiFastConnectV2.this.sendMessageDelay(2, 30000L);
            } else if (i == 2) {
                if (MiuiFastConnectV2.this.DBG) {
                    Log.d("MiuiFastConnectV2_Settings", " received MSG_BLE_SCAN_STOP!!!");
                }
                MiuiFastConnectV2.this.mBluetoothLeScanner.stopScan(MiuiFastConnectV2.this.mScanCallback);
                if (MiuiFastConnectV2.this.mMiuiOnLineBluetoothDevicePreference != null) {
                    MiuiFastConnectV2.this.mMiuiOnLineBluetoothDevicePreference.sendMessageDelay(3, 0L);
                }
            } else if (i == 117) {
                if (MiuiFastConnectV2.this.DBG) {
                    Log.d("MiuiFastConnectV2_Settings", " received MSG_ENABLE_ACCOUNTKEY_CCCD");
                }
                MiuiFastConnectV2.this.enableAccountkeyCCCD();
            } else {
                switch (i) {
                    case 100:
                        MiuiFastConnectV2.this.initBluetooth();
                        return;
                    case 101:
                        if (MiuiFastConnectV2.this.DBG) {
                            Log.d("MiuiFastConnectV2_Settings", " received MSG_CONNECT_GATT");
                            return;
                        }
                        return;
                    case 102:
                        if (MiuiFastConnectV2.this.DBG) {
                            Log.d("MiuiFastConnectV2_Settings", " received MSG_ENABLE_KBP_CCCD");
                        }
                        MiuiFastConnectV2.this.enableKeybasedCCCD();
                        return;
                    case 103:
                        if (MiuiFastConnectV2.this.DBG) {
                            Log.d("MiuiFastConnectV2_Settings", " received MSG_ENABLE_PASSKEY_CCCD");
                        }
                        MiuiFastConnectV2.this.enablePasskeyCCCD();
                        return;
                    case 104:
                        if (MiuiFastConnectV2.this.DBG) {
                            Log.d("MiuiFastConnectV2_Settings", " received MSG_START_KBP_NEGOTIATION");
                        }
                        MiuiFastConnectV2.this.startKBPNegotiation();
                        return;
                    case 105:
                        String format = String.format("%06x", Integer.valueOf(message.getData().getInt("android.bluetooth.device.extra.PAIRING_KEY")));
                        if (MiuiFastConnectV2.this.DBG) {
                            Log.d("MiuiFastConnectV2_Settings", "    Bundle get passkey String: " + format);
                        }
                        MiuiFastConnectV2.this.writePassKey(format);
                        MiuiFastConnectV2.this.sendMessageDelay(106, 500L);
                        return;
                    case 106:
                        MiuiFastConnectV2.this.writeAccountKey();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private MiuiFastConnectV2(Context context, MiuiOnLineBluetoothDevicePreference miuiOnLineBluetoothDevicePreference) {
        this.mContext = context;
        this.mMiuiOnLineBluetoothDevicePreference = miuiOnLineBluetoothDevicePreference;
        HandlerThread handlerThread = new HandlerThread("FastConnectServiceHandlerSettings");
        handlerThread.start();
        this.mHandler = new ServiceMessageHandler(handlerThread.getLooper());
        sendMessageDelay(100, 0L);
        this.mScanCallback = new LeScanCallback();
        registerReceiver();
    }

    private byte[] accountKeyGenerator() {
        byte[] bArr = new byte[15];
        new SecureRandom().nextBytes(bArr);
        return byteMerger(MiuiAdvDataConstantsV2.ACCOUNT_KEY_HEADER, bArr);
    }

    public static byte[] byteMerger(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[bArr.length + bArr2.length];
        System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        System.arraycopy(bArr2, 0, bArr3, bArr.length, bArr2.length);
        return bArr3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String bytesToHexString(byte[] bArr) {
        char[] cArr = new char[bArr.length * 2];
        int i = 0;
        for (byte b : bArr) {
            int i2 = i + 1;
            char[] cArr2 = MiuiAdvDataConstantsV2.HEX_CHAR;
            cArr[i] = cArr2[(b >>> 4) & 15];
            i = i2 + 1;
            cArr[i2] = cArr2[b & 15];
        }
        return new String(cArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean enableAccountkeyCCCD() {
        this.mAccountkeyCCCD.setValue(MiuiAdvDataConstantsV2.ENABLE_CCCD);
        boolean writeDescriptor = this.mBluetoothGatt.writeDescriptor(this.mAccountkeyCCCD);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "    write account key CCCD result: " + writeDescriptor);
        }
        if (!writeDescriptor) {
            sendMessageDelay(117, 100L);
            if (this.DBG) {
                Log.d("MiuiFastConnectV2_Settings", "send MSG_ENABLE_ACCOUNTKEY_CCCD again!!");
            }
        }
        boolean characteristicNotification = this.mBluetoothGatt.setCharacteristicNotification(this.mAccountKeyCharacteristic, true);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "enable account key notification characteristic " + characteristicNotification);
        }
        sendMessageDelay(104, 500L);
        return characteristicNotification;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean enableKeybasedCCCD() {
        this.mkeybasedCCCD.setValue(MiuiAdvDataConstantsV2.ENABLE_CCCD);
        boolean writeDescriptor = this.mBluetoothGatt.writeDescriptor(this.mkeybasedCCCD);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "    write key based CCCD result: " + writeDescriptor);
        }
        boolean characteristicNotification = this.mBluetoothGatt.setCharacteristicNotification(this.mKeyBasedCharacteristic, true);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "enable keybased notification  characteristic " + characteristicNotification);
        }
        return characteristicNotification;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean enablePasskeyCCCD() {
        this.mPasskeyCCCD.setValue(MiuiAdvDataConstantsV2.ENABLE_CCCD);
        boolean writeDescriptor = this.mBluetoothGatt.writeDescriptor(this.mPasskeyCCCD);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "    write passkey CCCD result: " + writeDescriptor);
        }
        boolean characteristicNotification = this.mBluetoothGatt.setCharacteristicNotification(this.mPasskeyCharacteristic, true);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "enable passkey notification characteristic " + characteristicNotification);
        }
        return characteristicNotification;
    }

    private byte[] encrypt(byte[] bArr) {
        if (this.DBG) {
            MiuiAdvDataConstantsV2.printBytes("encrypt: AES128Key is ", this.mAES128Key);
        }
        return MiuiAES.encrypt(bArr, this.mAES128Key);
    }

    private byte[] getSalt(int i) throws NoSuchAlgorithmException {
        byte[] bArr = new byte[i];
        SecureRandom.getInstance("SHA1PRNG").nextBytes(bArr);
        for (int i2 = 0; i2 < i; i2++) {
            System.out.print(bArr[i2] & 255);
            System.out.print(" ");
        }
        return bArr;
    }

    private String getScrambledAccountKey(String str) {
        try {
            return bytesToHexString(MessageDigest.getInstance(HashUtils.SHA1).digest(hexStringToBytes(str))).substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] hexStringToBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bArr = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            try {
                int i2 = i * 2;
                bArr[i] = (byte) Integer.parseInt(str.substring(i2, i2 + 2), 16);
            } catch (Exception e) {
                Log.e("MiuiFastConnectV2_Settings", "Exception: " + e);
                return new byte[0];
            }
        }
        return bArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService(DeviceType.BLUETOOTH);
        this.mBluetoothManager = bluetoothManager;
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        this.mBluetoothAdapter = adapter;
        if (adapter == null) {
            Log.e("MiuiFastConnectV2_Settings", "Bluetooth is not support!");
        } else if (adapter.getState() == 10) {
            this.mBluetoothAdapter.enable();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void kbpResponseHandle(byte[] bArr) {
        byte[] bArr2 = new byte[6];
        byte[] decrypt = MiuiAES.decrypt(bArr, this.mAES128Key);
        if (this.DBG) {
            MiuiAdvDataConstantsV2.printBytes("decrypted data is: ", decrypt);
        }
        if (decrypt != null && decrypt.length == 16 && decrypt[0] == 1) {
            bArr2[0] = decrypt[1];
            bArr2[1] = decrypt[2];
            bArr2[2] = decrypt[3];
            bArr2[3] = decrypt[4];
            bArr2[4] = decrypt[5];
            bArr2[5] = decrypt[6];
            if (this.DBG) {
                MiuiAdvDataConstantsV2.printBytes("addressBRDER is: ", bArr2);
            }
        }
        this.mRemoteDeviceBREDR = this.mBluetoothAdapter.getRemoteDevice(bArr2);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "createBond");
        }
        this.mRemoteDeviceBREDR.createBond();
        sentIgnorePairDialogIntentXFPS(this.mRemoteDeviceBREDR.getAddress());
        this.mPairing = true;
    }

    public static MiuiFastConnectV2 make(Context context, MiuiOnLineBluetoothDevicePreference miuiOnLineBluetoothDevicePreference) {
        MiuiFastConnectV2 miuiFastConnectV2 = new MiuiFastConnectV2(context, miuiOnLineBluetoothDevicePreference);
        mMiuiFastConnectV2 = miuiFastConnectV2;
        return miuiFastConnectV2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pairDone() {
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "pairDone");
        }
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        this.mPairing = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performLEScan() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ScanFilter.Builder().setServiceData(MiuiAdvDataConstantsV2.UUID_SERVICE_FAST_PAIR, new byte[0]).build());
        ScanSettings build = new ScanSettings.Builder().setScanMode(2).build();
        BluetoothLeScanner bluetoothLeScanner = this.mBluetoothAdapter.getBluetoothLeScanner();
        this.mBluetoothLeScanner = bluetoothLeScanner;
        bluetoothLeScanner.startScan(arrayList, build, this.mScanCallback);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "Perform le scan with filters: " + arrayList);
        }
    }

    private boolean registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("miui.bluetooth.ACTION_XFPS_PAIRING_REQUEST");
        PairingChangeReceiver pairingChangeReceiver = new PairingChangeReceiver();
        this.mPairingChangeReceiver = pairingChangeReceiver;
        this.mContext.registerReceiver(pairingChangeReceiver, intentFilter);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "registerReceiver  pairingChangeReceiver");
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean sendMessageDelay(int i, long j) {
        synchronized (this.mHandlerLock) {
            ServiceMessageHandler serviceMessageHandler = this.mHandler;
            if (serviceMessageHandler == null) {
                Log.e("MiuiFastConnectV2_Settings", "sendMessageDelay handler null");
                return false;
            }
            if (serviceMessageHandler.hasMessages(i)) {
                this.mHandler.removeMessages(i);
            }
            if (j >= 0) {
                this.mHandler.sendEmptyMessageDelayed(i, j);
            }
            return true;
        }
    }

    private void sentIgnorePairDialogIntentXFPS(String str) {
        BluetoothDevice bluetoothDevice = this.mRemoteDeviceBREDR;
        String address = bluetoothDevice != null ? bluetoothDevice.getAddress() : "00:00:00:00:00:00";
        if (!"00:00:00:00:00:00".equals(str) && !address.equals(str)) {
            Log.e("MiuiFastConnectV2_Settings", "should not be here : connectDevice: " + str + ", currentAddress: " + address + ", peerAddress: " + address);
        }
        Settings.Global.putLong(this.mContext.getContentResolver(), "fast_connect_show_dialog", System.currentTimeMillis());
        Intent intent = new Intent("miui.bluetooth.FAST_CONNECT_DEVICE_BOND");
        intent.putExtra("FAST_CONNECT_CURRENT_DEVICE", address);
        intent.putExtra("FAST_CONNECT_PEER_DEVICE", address);
        intent.putExtra("android.intent.extra.PACKAGE_NAME", "com.xiaomi.bluetooth");
        intent.putExtra("XFPS_SUPPORTED", true);
        intent.setPackage("com.android.bluetooth");
        this.mContext.sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startKBPNegotiation() {
        byte[] bArr;
        ByteBuffer wrap;
        String address = this.mRemoteDeviceLE.getAddress();
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "remote address string: " + address);
        }
        String[] split = address.split(":");
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "remote address strings: " + split.length);
        }
        byte[] bytes = address.getBytes();
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "remote address bytes: " + bytes.length + " " + Arrays.toString(bytes));
        }
        if (split.length == 6) {
            MiuiAdvDataConstantsV2.PAIRING_REQUEST[2] = strToByte(split[0]);
            MiuiAdvDataConstantsV2.PAIRING_REQUEST[3] = strToByte(split[1]);
            MiuiAdvDataConstantsV2.PAIRING_REQUEST[4] = strToByte(split[2]);
            MiuiAdvDataConstantsV2.PAIRING_REQUEST[5] = strToByte(split[3]);
            MiuiAdvDataConstantsV2.PAIRING_REQUEST[6] = strToByte(split[4]);
            MiuiAdvDataConstantsV2.PAIRING_REQUEST[7] = strToByte(split[5]);
        }
        try {
            bArr = getSalt(2);
        } catch (Exception e) {
            e.printStackTrace();
            bArr = null;
        }
        byte[] bArr2 = MiuiAdvDataConstantsV2.PAIRING_REQUEST;
        bArr2[14] = bArr[0];
        bArr2[15] = bArr[1];
        if (this.DBG) {
            MiuiAdvDataConstantsV2.printBytes("pairingRequestData is: ", bArr2);
        }
        byte[] encrypt = encrypt(MiuiAdvDataConstantsV2.PAIRING_REQUEST);
        if (this.mLocalPublicKey != null) {
            wrap = ByteBuffer.wrap(new byte[encrypt.length + MiuiAdvDataConstantsV2.ALICE_PUBLIC_DATA.length]);
            wrap.put(encrypt);
            wrap.put(this.mLocalPublickeyByte);
        } else {
            wrap = ByteBuffer.wrap(new byte[encrypt.length]);
            wrap.put(encrypt);
        }
        byte[] array = wrap.array();
        if (this.DBG) {
            MiuiAdvDataConstantsV2.printBytes("write data is: ", array);
        }
        this.mKeyBasedCharacteristic.setValue(array);
        writeCharacteristic(this.mKeyBasedCharacteristic);
    }

    private byte strToByte(String str) {
        int i = 0;
        for (int i2 = 0; i2 < 2; i2++) {
            char charAt = str.charAt(i2);
            i = (i * 16) + ((charAt < '0' || charAt > '9') ? ((charAt < 'A' || charAt > 'F') && (charAt < 'a' || charAt > 'f')) ? 0 : (charAt - 'A') + 10 : charAt - '0');
        }
        return (byte) (i & 255);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeAccountKey() {
        if (this.mAccountKey == null) {
            this.mAccountKey = accountKeyGenerator();
        }
        if (this.DBG) {
            MiuiAdvDataConstantsV2.printBytes("write account key: ", this.mAccountKey);
        }
        this.mAccountKeyCharacteristic.setValue(encrypt(this.mAccountKey));
        writeCharacteristic(this.mAccountKeyCharacteristic);
    }

    private boolean writeCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        BluetoothGatt bluetoothGatt;
        if (this.mBluetoothAdapter == null || (bluetoothGatt = this.mBluetoothGatt) == null) {
            Log.e("MiuiFastConnectV2_Settings", "BluetoothAdapter not initialized");
            return false;
        }
        boolean beginReliableWrite = bluetoothGatt.beginReliableWrite();
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", ": beginReliableWrite: " + beginReliableWrite);
        }
        boolean writeCharacteristic = this.mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "writeKeyBasedPairingCharacteristic: " + writeCharacteristic);
        }
        return writeCharacteristic;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writePassKey(String str) {
        byte[] hexStringToBytes = hexStringToBytes(str);
        this.mSeekerPasskey = hexStringToBytes;
        if (hexStringToBytes.length >= 3) {
            MiuiAdvDataConstantsV2.printBytes("mpasskeySeeker: ", hexStringToBytes);
            byte[] bArr = MiuiAdvDataConstantsV2.PASSKEY;
            byte[] bArr2 = this.mSeekerPasskey;
            bArr[1] = bArr2[0];
            bArr[2] = bArr2[1];
            bArr[3] = bArr2[2];
        }
        this.mPasskeyCharacteristic.setValue(encrypt(MiuiAdvDataConstantsV2.PASSKEY));
        writeCharacteristic(this.mPasskeyCharacteristic);
    }

    public void cleanup() {
        PairingChangeReceiver pairingChangeReceiver;
        if (this.DBG) {
            Log.d("MiuiFastConnectV2_Settings", "cleanup");
        }
        pairDone();
        Context context = this.mContext;
        if (context != null && (pairingChangeReceiver = this.mPairingChangeReceiver) != null) {
            context.unregisterReceiver(pairingChangeReceiver);
            this.mPairingChangeReceiver = null;
        }
        synchronized (this.mHandlerLock) {
            ServiceMessageHandler serviceMessageHandler = this.mHandler;
            if (serviceMessageHandler != null) {
                serviceMessageHandler.removeCallbacksAndMessages(null);
                Looper looper = this.mHandler.getLooper();
                if (looper != null) {
                    looper.quit();
                }
                this.mHandler = null;
            }
        }
    }

    public byte[] getAccountKeyFromScanResult(ScanResult scanResult) {
        ScanRecord scanRecord = scanResult.getScanRecord();
        if (scanRecord == null) {
            return null;
        }
        byte[] serviceData = scanRecord.getServiceData(MiuiAdvDataConstantsV2.UUID_SERVICE_FAST_PAIR);
        if (scanResult.getDevice() == null || serviceData == null || serviceData.length <= 14) {
            return null;
        }
        return new byte[]{serviceData[10], serviceData[9], serviceData[8], serviceData[7], serviceData[6]};
    }

    public void startPair(String str) {
        this.mAccountKeyCloudScrambled = getScrambledAccountKey(str);
        this.mAES128Key = hexStringToBytes(str);
        sendMessageDelay(1, 0L);
    }
}
