package com.android.settings.display;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import androidx.preference.Preference;
import com.android.settings.MiuiListPreference;
import com.android.settings.R;
import miui.util.MiuiFeatureUtils;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class FluencyModeListPreference extends MiuiListPreference {
    private String[] mConfirmArray;
    private String[] mContentArray;
    private LocalAdapter mLocalAdapter;
    private int mStatus;
    private int mStoredPosition;
    private String[] mSummaryArray;

    /* loaded from: classes.dex */
    private class LocalAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        LocalAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return FluencyModeListPreference.this.getEntryValues().length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (i >= 0 && i < FluencyModeListPreference.this.mContentArray.length) {
                if (view == null) {
                    view = this.mInflater.inflate(R.layout.fluency_mode_list_item, (ViewGroup) null);
                }
                CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.txt_fluency_content);
                TextView textView = (TextView) view.findViewById(R.id.txt_fluency_summary);
                checkedTextView.setText(FluencyModeListPreference.this.mContentArray[i]);
                checkedTextView.setChecked(i == FluencyModeListPreference.this.mStatus);
                textView.setText(FluencyModeListPreference.this.mSummaryArray[i]);
            }
            return view;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.settings.display.FluencyModeListPreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int value;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.value = parcel.readInt();
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.value);
        }
    }

    public FluencyModeListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLocalAdapter = new LocalAdapter(context);
        Resources resources = context.getResources();
        this.mContentArray = resources.getStringArray(R.array.fluency_mode_entries);
        this.mSummaryArray = resources.getStringArray(R.array.fluency_mode_summaries);
        this.mConfirmArray = resources.getStringArray(R.array.fluency_mode_confirms);
        setEntryValues(this.mContentArray);
        int status = getStatus();
        this.mStatus = status;
        this.mStoredPosition = status;
        setSummary(this.mContentArray[status]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeMiuiLiteMode(int i) {
        if (i == 0) {
            SystemProperties.set("persist.sys.miui_feature_config", "/system/etc/miui_feature/lite.conf");
        } else if (i == 1) {
            SystemProperties.set("persist.sys.miui_feature_config", "/system/etc/miui_feature/default.conf");
        }
        ((PowerManager) getContext().getSystemService("power")).reboot(null);
    }

    private int getStatus() {
        return MiuiFeatureUtils.isLiteMode() ? 0 : 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDialog(final int i) {
        new AlertDialog.Builder(getContext()).setTitle(this.mContentArray[i]).setIconAttribute(16843605).setMessage(this.mConfirmArray[i]).setPositiveButton(R.string.fluency_mode_confirm_and_reboot, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.FluencyModeListPreference.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                FluencyModeListPreference.this.changeMiuiLiteMode(i);
            }
        }).setNegativeButton(17039369, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.FluencyModeListPreference.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                FluencyModeListPreference fluencyModeListPreference = FluencyModeListPreference.this;
                fluencyModeListPreference.mStoredPosition = fluencyModeListPreference.mStatus;
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.display.FluencyModeListPreference.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                FluencyModeListPreference fluencyModeListPreference = FluencyModeListPreference.this;
                fluencyModeListPreference.mStoredPosition = fluencyModeListPreference.mStatus;
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setSingleChoiceItems(this.mLocalAdapter, this.mStatus, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.FluencyModeListPreference.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i < 0 || i >= FluencyModeListPreference.this.mContentArray.length) {
                    return;
                }
                FluencyModeListPreference.this.setValueIndex(i);
                FluencyModeListPreference.this.mStoredPosition = i;
                if (i != FluencyModeListPreference.this.mStatus) {
                    FluencyModeListPreference.this.showDialog(i);
                }
                FluencyModeListPreference.this.getDialog().dismiss();
            }
        });
        builder.setPositiveButton((CharSequence) null, (DialogInterface.OnClickListener) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.ListPreference, androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        int i = savedState.value;
        this.mStoredPosition = i;
        if (this.mStatus != i) {
            showDialog(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.ListPreference, androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.value = this.mStoredPosition;
        return savedState;
    }
}
