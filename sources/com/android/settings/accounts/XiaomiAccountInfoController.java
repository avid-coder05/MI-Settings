package com.android.settings.accounts;

import android.accounts.Account;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.R;
import com.android.settings.notify.SettingsNotifyHelper;
import miui.os.Build;

/* loaded from: classes.dex */
public class XiaomiAccountInfoController extends BaseSettingsController {
    private ImageView mIcon;
    private TextView mRightValue;
    private TextView mSummary;
    private TextView mTitle;

    public XiaomiAccountInfoController(Context context, TextView textView) {
        super(context, textView);
    }

    private void setIcon(boolean z) {
        ImageView imageView = this.mIcon;
        if (imageView != null) {
            if (!z) {
                imageView.setImageResource(R.drawable.ic_account_avatar);
                return;
            }
            Bitmap xiaomiAccountAvatar = XiaomiAccountUtils.getInstance(this.mContext).getXiaomiAccountAvatar();
            if (xiaomiAccountAvatar == null || xiaomiAccountAvatar.isRecycled()) {
                this.mIcon.setImageResource(R.drawable.ic_account_avatar);
            } else {
                this.mIcon.setImageBitmap(xiaomiAccountAvatar);
            }
        }
    }

    private void setRightValue(boolean z) {
        if (this.mRightValue != null) {
            Account account = XiaomiAccountUtils.getInstance(this.mContext).getAccount(this.mContext);
            this.mRightValue.setText((CharSequence) null);
            if (z || Build.IS_TABLET || account != null) {
                this.mRightValue.setVisibility(4);
                return;
            }
            this.mRightValue.setVisibility(0);
            Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.account_unlogin_tip);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            this.mRightValue.setCompoundDrawables(null, null, drawable, null);
        }
    }

    private void setSummary(boolean z) {
        TextView textView = this.mSummary;
        if (textView != null) {
            textView.setVisibility(0);
            this.mSummary.setText(z ? R.string.login_account_summary_temp : R.string.unlogin_account_summary);
            this.mSummary.setTextColor(this.mContext.getResources().getColor(R.color.account_info_summary));
        }
    }

    private void setTitle(String str) {
        TextView textView = this.mTitle;
        if (textView != null) {
            if (TextUtils.isEmpty(str)) {
                str = this.mContext.getResources().getString(R.string.unlogin_account_title);
            }
            textView.setText(str);
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
    }

    public void setUpTextView(ImageView imageView, TextView textView, TextView textView2, TextView textView3) {
        this.mIcon = imageView;
        this.mTitle = textView;
        this.mSummary = textView2;
        this.mRightValue = textView3;
        updateStatus();
    }

    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        if (this.mStatusView == null) {
            return;
        }
        if (!SettingsNotifyHelper.isPhoneRecycledToNotify(this.mContext)) {
            setTitle(XiaomiAccountUtils.getInstance(this.mContext).getXiaoAccountName());
            setSummary(!TextUtils.isEmpty(r0));
            setRightValue(!TextUtils.isEmpty(r0));
            setIcon(!TextUtils.isEmpty(r0));
        } else if (Build.IS_TABLET) {
            this.mStatusView.setText((CharSequence) null);
        } else {
            Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.device_update_signal1);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            this.mStatusView.setText(R.string.suggestion_for_phone_recycled);
            this.mStatusView.setCompoundDrawables(null, null, drawable, null);
        }
    }
}
