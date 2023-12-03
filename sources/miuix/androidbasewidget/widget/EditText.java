package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;
import miuix.androidbasewidget.R$color;
import miuix.androidbasewidget.R$style;
import miuix.androidbasewidget.R$styleable;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;

/* loaded from: classes5.dex */
public class EditText extends AppCompatEditText {
    private final int DEFAULT_TEXT_HANDLE_AND_CURSOR_COLOR;
    private boolean isAddListener;
    private TextWatcher mErrorWatcher;
    private int mTextHandleAndCursorColor;

    /* loaded from: classes5.dex */
    private class ErrorWatcher implements TextWatcher {
        private ErrorWatcher() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            EditText.this.setMiuiStyleError(null);
            if (EditText.this.isAddListener) {
                EditText.this.isAddListener = false;
                EditText editText = EditText.this;
                editText.removeTextChangedListener(editText.mErrorWatcher);
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842862);
    }

    public EditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mErrorWatcher = new ErrorWatcher();
        int color = getResources().getColor(R$color.miuix_appcompat_handle_and_cursor_color);
        this.DEFAULT_TEXT_HANDLE_AND_CURSOR_COLOR = color;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.EditText, i, R$style.Widget_EditText_DayNight);
        this.mTextHandleAndCursorColor = obtainStyledAttributes.getColor(R$styleable.EditText_textHandleAndCursorColor, color);
        obtainStyledAttributes.recycle();
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
    }

    private int obtainHighlightColor() {
        return Color.argb(38, Color.red(this.mTextHandleAndCursorColor), Color.green(this.mTextHandleAndCursorColor), Color.blue(this.mTextHandleAndCursorColor));
    }

    @Override // android.widget.TextView, android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        boolean onPreDraw = super.onPreDraw();
        if (Build.VERSION.SDK_INT >= 29) {
            setHighlightColor(obtainHighlightColor());
            Drawable textSelectHandleLeft = getTextSelectHandleLeft();
            Drawable textSelectHandleRight = getTextSelectHandleRight();
            Drawable textSelectHandle = getTextSelectHandle();
            Drawable textCursorDrawable = getTextCursorDrawable();
            if (this.mTextHandleAndCursorColor != this.DEFAULT_TEXT_HANDLE_AND_CURSOR_COLOR) {
                Drawable[] drawableArr = {textSelectHandleLeft, textSelectHandleRight, textSelectHandle, textCursorDrawable};
                for (int i = 0; i < 4; i++) {
                    drawableArr[i].setColorFilter(this.mTextHandleAndCursorColor, PorterDuff.Mode.SRC_IN);
                }
            }
            setTextSelectHandleLeft(textSelectHandleLeft);
            setTextSelectHandleRight(textSelectHandleRight);
            setTextSelectHandle(textSelectHandle);
            setTextCursorDrawable(textCursorDrawable);
        }
        return onPreDraw;
    }

    public void setMiuiStyleError(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            getBackground().setLevel(0);
            return;
        }
        getBackground().setLevel(404);
        if (this.isAddListener) {
            return;
        }
        this.isAddListener = true;
        addTextChangedListener(this.mErrorWatcher);
    }
}
