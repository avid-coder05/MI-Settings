package com.android.settings.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import miuix.animation.Folme;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes2.dex */
public class MiuiNFCImagePreference extends Preference implements FolmeAnimationController {
    private Context mContext;

    /* loaded from: classes2.dex */
    private static class NfcClickText extends ClickableSpan {
        private Context mContext;

        public NfcClickText(Context context) {
            this.mContext = context;
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View view) {
            Uri parse = Uri.parse("https://sf.pay.xiaomi.com/views/cmsModelPages/CSStandardKnowledge.html?id=620cd7d01cc9dd8866247232");
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(268435456);
            intent.setData(parse);
            try {
                this.mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setColor(this.mContext.getColor(R.color.headset_find_device_blue));
            textPaint.setUnderlineText(false);
        }
    }

    public MiuiNFCImagePreference(Context context) {
        super(context);
        init(context);
    }

    public MiuiNFCImagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public MiuiNFCImagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public MiuiNFCImagePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context.getApplicationContext();
        setLayoutResource(R.layout.miui_nfc_image_layout);
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // androidx.preference.Preference
    @SuppressLint({"ResourceType"})
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        if (view == null || this.mContext == null) {
            return;
        }
        Folme.clean(view);
        view.setBackgroundResource(0);
        TextView textView = (TextView) view.findViewById(R.id.text);
        String string = this.mContext.getResources().getString(R.string.nfc_image_preference_text_noclick);
        String string2 = this.mContext.getResources().getString(R.string.nfc_image_preference_text_click);
        int length = string.length();
        SpannableString spannableString = new SpannableString(string.concat(string2));
        spannableString.setSpan(new NfcClickText(this.mContext), length, spannableString.length(), 33);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString);
        textView.setHighlightColor(0);
    }
}
