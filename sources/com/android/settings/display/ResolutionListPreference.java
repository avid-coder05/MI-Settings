package com.android.settings.display;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindowManager;
import android.view.View;
import android.widget.Button;
import com.android.settings.CustomListPreference;
import com.android.settings.R;
import java.util.ArrayList;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ResolutionListPreference extends CustomListPreference {
    private int mClickedEntryIndex;
    private Display mDisplay;
    CharSequence[] mEntries;
    CharSequence[] mEntriesValue;
    private int mInitalDensity;
    private Point mInitalPoint;
    private SparseArray<String> mResolutionFullTexts;
    private SparseArray<String> mResolutionTexts;
    private int[] mScreenResolutionsSupported;
    private IWindowManager mWindowManager;

    public ResolutionListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int i = 0;
        this.mDisplay = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mInitalPoint = new Point();
        try {
            this.mInitalDensity = this.mWindowManager.getInitialDisplayDensity(0);
            this.mWindowManager.getInitialDisplaySize(0, this.mInitalPoint);
            this.mScreenResolutionsSupported = getContext().getResources().getIntArray(285409379);
        } catch (Exception e) {
            Log.e("ResolutionList", "ResolutionListPreference: ", e);
        }
        int[] iArr = this.mScreenResolutionsSupported;
        if (iArr == null || iArr.length <= 1) {
            return;
        }
        this.mResolutionTexts = new SparseArray<>();
        this.mResolutionFullTexts = new SparseArray<>();
        Resources resources = getContext().getResources();
        int[] intArray = resources.getIntArray(R.array.screen_resolution);
        String[] stringArray = resources.getStringArray(R.array.screen_resolution_text);
        String[] stringArray2 = resources.getStringArray(R.array.screen_resolution_format);
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
        if (arrayList.size() <= 1 || arrayList2.size() <= 1) {
            return;
        }
        this.mEntries = new CharSequence[arrayList.size()];
        this.mEntriesValue = new CharSequence[arrayList2.size()];
        this.mEntries = (CharSequence[]) arrayList.toArray(this.mEntries);
        this.mEntriesValue = (CharSequence[]) arrayList2.toArray(this.mEntriesValue);
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

    public boolean isSuported() {
        return (this.mEntriesValue == null || this.mEntries == null) ? false : true;
    }

    @Override // com.android.settings.CustomListPreference
    protected void onBindDialogView(View view) {
        ((AlertDialog) getDialog()).getButton(-1).setEnabled(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onDialogClosed(boolean z) {
        if (z) {
            Display.Mode mode = this.mDisplay.getMode();
            int i = this.mScreenResolutionsSupported[this.mClickedEntryIndex];
            switchResolution(0, i, (int) (((mode.getPhysicalHeight() * 1.0f) / mode.getPhysicalWidth()) * i), Math.round(((this.mInitalDensity * i) * 1.0f) / this.mInitalPoint.x));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        if (isSuported()) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            final String str = this.mResolutionTexts.get(Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels));
            setEntries(this.mEntries);
            setEntryValues(this.mEntriesValue);
            setValue(str);
            super.onPrepareDialogBuilder(builder);
            builder.setItems(getEntries(), new DialogInterface.OnClickListener() { // from class: com.android.settings.display.ResolutionListPreference.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ResolutionListPreference.this.mClickedEntryIndex = i;
                    Button button = ((AlertDialog) ResolutionListPreference.this.getDialog()).getButton(-1);
                    String str2 = str;
                    if (str2 == null || !str2.equals(ResolutionListPreference.this.getEntryValues()[i])) {
                        button.setEnabled(true);
                    } else {
                        button.setEnabled(false);
                    }
                }
            });
            builder.setPositiveButton(getPositiveButtonText(), this);
            builder.setTitle(R.string.screen_resolution_dialog_title);
        }
    }
}
