package com.android.settings.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.CustomEditTextPreference;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class ValidatedEditTextPreference extends CustomEditTextPreference {
    private boolean mIsPassword;
    private boolean mIsSummaryPassword;
    private final EditTextWatcher mTextWatcher;
    private Validator mValidator;

    /* loaded from: classes2.dex */
    private class EditTextWatcher implements TextWatcher {
        private EditTextWatcher() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            EditText editText = ValidatedEditTextPreference.this.getEditText();
            if (ValidatedEditTextPreference.this.mValidator == null || editText == null) {
                return;
            }
            AlertDialog alertDialog = (AlertDialog) ValidatedEditTextPreference.this.getDialog();
            alertDialog.getButton(-1).setEnabled(ValidatedEditTextPreference.this.mValidator.isTextValid(editText.getText().toString()));
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* loaded from: classes2.dex */
    public interface Validator {
        boolean isTextValid(String str);
    }

    public ValidatedEditTextPreference(Context context) {
        super(context);
        this.mTextWatcher = new EditTextWatcher();
    }

    public ValidatedEditTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTextWatcher = new EditTextWatcher();
    }

    public ValidatedEditTextPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextWatcher = new EditTextWatcher();
    }

    public ValidatedEditTextPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTextWatcher = new EditTextWatcher();
    }

    public boolean isPassword() {
        return this.mIsPassword;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreference
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null && !TextUtils.isEmpty(editText.getText())) {
            editText.setSelection(editText.getText().length());
        }
        if (this.mValidator == null || editText == null) {
            return;
        }
        editText.removeTextChangedListener(this.mTextWatcher);
        if (this.mIsPassword) {
            editText.setInputType(145);
            editText.setMaxLines(1);
        }
        editText.addTextChangedListener(this.mTextWatcher);
    }

    @Override // com.android.settingslib.miuisettings.preference.EditTextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908304);
        if (textView == null) {
            return;
        }
        if (this.mIsSummaryPassword) {
            textView.setInputType(129);
        } else {
            textView.setInputType(524289);
        }
    }

    public void setIsPassword(boolean z) {
        this.mIsPassword = z;
    }

    public void setIsSummaryPassword(boolean z) {
        this.mIsSummaryPassword = z;
    }

    public void setValidator(Validator validator) {
        this.mValidator = validator;
    }
}
