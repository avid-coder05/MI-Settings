package com.android.settings.display;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindowManager;
import com.android.settingslib.R$array;
import com.android.settingslib.R$string;
import java.util.ArrayList;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class ScreenResolutionDialogActivity extends AppCompatActivity {
    private int mCurrentResolutionIndex;
    private Display mDisplay;
    CharSequence[] mEntries;
    CharSequence[] mEntriesValue;
    private int mInitalDensity;
    private Point mInitalPoint;
    private SparseArray<String> mResolutionFullTexts;
    private SparseArray<String> mResolutionTexts;
    private int[] mScreenResolutionsSupported;
    private IWindowManager mWindowManager;

    private void createResolutionDialog() {
        this.mCurrentResolutionIndex = getCurrentResolution();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R$string.screen_resolution_dialog_title);
        builder.setSingleChoiceItems(this.mEntries, this.mCurrentResolutionIndex, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.ScreenResolutionDialogActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ScreenResolutionDialogActivity screenResolutionDialogActivity = ScreenResolutionDialogActivity.this;
                if (i < screenResolutionDialogActivity.mEntries.length && i >= 0) {
                    screenResolutionDialogActivity.switchResolution(screenResolutionDialogActivity.mScreenResolutionsSupported[i]);
                }
                dialogInterface.dismiss();
                ScreenResolutionDialogActivity.this.finish();
            }
        });
        builder.setNeutralButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.ScreenResolutionDialogActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ScreenResolutionDialogActivity.this.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.display.ScreenResolutionDialogActivity.3
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                ScreenResolutionDialogActivity.this.finish();
            }
        });
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchResolution(int i) {
        Display.Mode mode = this.mDisplay.getMode();
        switchResolution(0, i, (int) (((mode.getPhysicalHeight() * 1.0f) / mode.getPhysicalWidth()) * i), Math.round(((this.mInitalDensity * i) * 1.0f) / this.mInitalPoint.x));
    }

    private void switchResolution(int i, int i2, int i3, int i4) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("android.view.IWindowManager");
            obtain.writeInt(i);
            obtain.writeInt(i2);
            obtain.writeInt(i3);
            obtain.writeInt(i4);
            this.mWindowManager.asBinder().transact(255, obtain, obtain2, 0);
            obtain2.readException();
        } catch (RemoteException unused) {
        } catch (Throwable th) {
            obtain2.recycle();
            obtain.recycle();
            throw th;
        }
        obtain2.recycle();
        obtain.recycle();
    }

    public int findIndexOfValue(String str) {
        CharSequence[] charSequenceArr;
        if (str == null || (charSequenceArr = this.mEntriesValue) == null) {
            return -1;
        }
        for (int length = charSequenceArr.length - 1; length >= 0; length--) {
            if (this.mEntriesValue[length].equals(str)) {
                return length;
            }
        }
        return -1;
    }

    public int getCurrentResolution() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return findIndexOfValue(this.mResolutionTexts.get(Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int i = 0;
        this.mDisplay = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mInitalPoint = new Point();
        try {
            this.mInitalDensity = this.mWindowManager.getInitialDisplayDensity(0);
            this.mWindowManager.getInitialDisplaySize(0, this.mInitalPoint);
            this.mScreenResolutionsSupported = FeatureParser.getIntArray("screen_resolution_supported");
        } catch (Exception e) {
            Log.e("ResolutionList", "ResolutionListPreference: ", e);
        }
        int[] iArr = this.mScreenResolutionsSupported;
        if (iArr != null && iArr.length > 1) {
            this.mResolutionTexts = new SparseArray<>();
            this.mResolutionFullTexts = new SparseArray<>();
            Resources resources = getResources();
            int[] intArray = resources.getIntArray(R$array.screen_resolution);
            String[] stringArray = resources.getStringArray(R$array.screen_resolution_text);
            String[] stringArray2 = resources.getStringArray(R$array.screen_resolution_format);
            for (int i2 = 0; i2 < intArray.length; i2++) {
                this.mResolutionTexts.put(intArray[i2], stringArray[i2]);
                this.mResolutionFullTexts.put(intArray[i2], String.format(stringArray2[i2], stringArray[i2]));
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            while (true) {
                int[] iArr2 = this.mScreenResolutionsSupported;
                if (i >= iArr2.length) {
                    break;
                }
                String str = this.mResolutionFullTexts.get(iArr2[i]);
                if (str != null) {
                    arrayList.add(str);
                }
                String str2 = this.mResolutionTexts.get(this.mScreenResolutionsSupported[i]);
                if (str2 != null) {
                    arrayList2.add(str2);
                }
                i++;
            }
            if (arrayList.size() > 1 && arrayList2.size() > 1) {
                this.mEntries = new CharSequence[arrayList.size()];
                this.mEntriesValue = new CharSequence[arrayList2.size()];
                this.mEntries = (CharSequence[]) arrayList.toArray(this.mEntries);
                this.mEntriesValue = (CharSequence[]) arrayList2.toArray(this.mEntriesValue);
            }
        }
        createResolutionDialog();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        setVisible(true);
    }
}
