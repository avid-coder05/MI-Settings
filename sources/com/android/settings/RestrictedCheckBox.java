package com.android.settings;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.CheckBox;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;

/* loaded from: classes.dex */
public class RestrictedCheckBox extends CheckBox {
    private Context mContext;
    private boolean mDisabledByAdmin;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;

    public RestrictedCheckBox(Context context) {
        this(context, null);
    }

    public RestrictedCheckBox(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    @Override // android.widget.CompoundButton, android.view.View
    public boolean performClick() {
        if (this.mDisabledByAdmin) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
            return true;
        }
        return super.performClick();
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        boolean z = enforcedAdmin != null;
        this.mEnforcedAdmin = enforcedAdmin;
        if (this.mDisabledByAdmin != z) {
            this.mDisabledByAdmin = z;
            RestrictedLockUtilsInternal.setTextViewAsDisabledByAdmin(this.mContext, this, z);
            if (this.mDisabledByAdmin) {
                getButtonDrawable().setColorFilter(this.mContext.getColor(R.color.disabled_text_color), PorterDuff.Mode.MULTIPLY);
            } else {
                getButtonDrawable().clearColorFilter();
            }
        }
    }
}
