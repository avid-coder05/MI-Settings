package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.miuixbasewidget.R$color;
import miuix.miuixbasewidget.R$dimen;
import miuix.miuixbasewidget.R$drawable;
import miuix.miuixbasewidget.R$id;
import miuix.miuixbasewidget.R$string;
import miuix.miuixbasewidget.R$style;
import miuix.miuixbasewidget.R$styleable;

/* loaded from: classes5.dex */
public class MessageView extends LinearLayout {
    private Drawable mCloseBackground;
    private OnMessageViewCloseListener mOnMessageViewCloseListener;
    private TextView mTextView;

    /* loaded from: classes5.dex */
    public interface OnMessageViewCloseListener {
        void onClosed();
    }

    public MessageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MessageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context, attributeSet, i);
    }

    private void addCloseIcon() {
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMarginStart(getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_message_view_text_margin_right));
        imageView.setId(R$id.close);
        imageView.setBackground(this.mCloseBackground);
        imageView.setContentDescription(getContext().getResources().getString(R$string.close));
        imageView.setOnClickListener(new View.OnClickListener() { // from class: miuix.miuixbasewidget.widget.MessageView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Folme.useAt(view).visible().setFlags(1L).hide(new AnimConfig[0]);
                MessageView.this.setVisibility(8);
                if (MessageView.this.mOnMessageViewCloseListener != null) {
                    MessageView.this.mOnMessageViewCloseListener.onClosed();
                }
            }
        });
        addView(imageView, layoutParams);
        Folme.useAt(imageView).touch().handleTouchOf(imageView, new AnimConfig[0]);
    }

    private void initView(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.MessageView, i, R$style.Widget_MessageView);
        String string = obtainStyledAttributes.getString(R$styleable.MessageView_android_text);
        ColorStateList colorStateList = AppCompatResources.getColorStateList(context, obtainStyledAttributes.getResourceId(R$styleable.MessageView_android_textColor, R$color.miuix_appcompat_message_view_text_color_light));
        this.mCloseBackground = AppCompatResources.getDrawable(context, obtainStyledAttributes.getResourceId(R$styleable.MessageView_closeBackground, R$drawable.miuix_appcompat_ic_message_view_close_guide_light));
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.MessageView_closable, true);
        obtainStyledAttributes.recycle();
        this.mTextView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.weight = 1.0f;
        this.mTextView.setId(16908308);
        this.mTextView.setPaddingRelative(getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_message_view_text_padding_start), 0, 0, 0);
        this.mTextView.setText(string);
        this.mTextView.setTextColor(colorStateList);
        this.mTextView.setTextSize(0, getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_message_view_text_size));
        this.mTextView.setTextDirection(5);
        addView(this.mTextView, layoutParams);
        setClosable(z);
        setGravity(16);
        Folme.useAt(this).touch().setTintMode(0).setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(this, new AnimConfig[0]);
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.FLOATED).handleHoverOf(this, new AnimConfig[0]);
    }

    public void setClosable(boolean z) {
        View findViewById = findViewById(R$id.close);
        if (z) {
            if (findViewById == null) {
                addCloseIcon();
            }
        } else if (findViewById != null) {
            removeView(findViewById);
        }
    }

    public void setMessage(CharSequence charSequence) {
        this.mTextView.setText(charSequence);
    }

    public void setOnMessageViewCloseListener(OnMessageViewCloseListener onMessageViewCloseListener) {
        this.mOnMessageViewCloseListener = onMessageViewCloseListener;
    }
}
