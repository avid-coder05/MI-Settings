package com.android.settings.tts;

import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class TtsEnginePreference extends RadioButtonPreference {
    private final TextToSpeech.EngineInfo mEngineInfo;
    private volatile boolean mPreventRadioButtonCallbacks;
    private RadioButton mRadioButton;
    private final CompoundButton.OnCheckedChangeListener mRadioChangeListener;
    private final RadioButtonGroupState mSharedState;
    private View titleView;

    /* loaded from: classes2.dex */
    public interface RadioButtonGroupState {
        Checkable getCurrentChecked();

        String getCurrentKey();

        void setCurrentChecked(Checkable checkable);

        void setCurrentKey(String str);
    }

    private void displayDataAlert(DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickListener2) {
        Log.i("TtsEnginePreference", "Displaying data alert for :" + this.mEngineInfo.name);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(17039380).setMessage(getContext().getString(R.string.tts_engine_security_warning, this.mEngineInfo.label)).setCancelable(true).setPositiveButton(17039370, onClickListener).setNegativeButton(17039360, onClickListener2);
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeCurrentEngine(Checkable checkable) {
        if (this.mSharedState.getCurrentChecked() != null) {
            this.mSharedState.getCurrentChecked().setChecked(false);
        }
        this.mSharedState.setCurrentChecked(checkable);
        this.mSharedState.setCurrentKey(getKey());
        callChangeListener(this.mSharedState.getCurrentKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRadioButtonClicked(final CompoundButton compoundButton, boolean z) {
        if (this.mPreventRadioButtonCallbacks || this.mSharedState.getCurrentChecked() == compoundButton) {
            setTitleViewChecked(compoundButton.isChecked());
        } else if (z) {
            if (shouldDisplayDataAlert()) {
                displayDataAlert(new DialogInterface.OnClickListener() { // from class: com.android.settings.tts.TtsEnginePreference.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TtsEnginePreference.this.makeCurrentEngine(compoundButton);
                        compoundButton.setChecked(true);
                        TtsEnginePreference.this.setTitleViewChecked(true);
                    }
                }, new DialogInterface.OnClickListener() { // from class: com.android.settings.tts.TtsEnginePreference.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        compoundButton.setChecked(false);
                    }
                });
                return;
            }
            makeCurrentEngine(compoundButton);
            compoundButton.setChecked(true);
            setTitleViewChecked(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTitleViewChecked(boolean z) {
        View view = this.titleView;
        if (view == null || !(view instanceof CheckedTextView)) {
            return;
        }
        ((CheckedTextView) view).setCheckMarkDrawable(0);
        ((CheckedTextView) this.titleView).setChecked(z);
    }

    private boolean shouldDisplayDataAlert() {
        return !this.mEngineInfo.system;
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mSharedState == null) {
            throw new IllegalStateException("Call to getView() before a call tosetSharedState()");
        }
        final RadioButton radioButton = (RadioButton) preferenceViewHolder.findViewById(16908289);
        radioButton.setImportantForAccessibility(1);
        this.titleView = preferenceViewHolder.findViewById(16908310);
        radioButton.setOnCheckedChangeListener(this.mRadioChangeListener);
        boolean equals = getKey().equals(this.mSharedState.getCurrentKey());
        if (equals) {
            this.mSharedState.setCurrentChecked(radioButton);
        }
        this.mPreventRadioButtonCallbacks = true;
        radioButton.setChecked(equals);
        this.mPreventRadioButtonCallbacks = false;
        this.mRadioButton = radioButton;
        setTitleViewChecked(equals);
        preferenceViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.tts.TtsEnginePreference.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                TtsEnginePreference.this.onRadioButtonClicked(radioButton, !r1.isChecked());
            }
        });
    }
}
