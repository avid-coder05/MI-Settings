package com.android.settings.vpn2;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.R$styleable;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.widget.Spinner;

/* loaded from: classes2.dex */
public class VpnSpinner extends FrameLayout {
    CharSequence[] entries;
    LinearLayout mLayout;
    Spinner mSpinner;
    TextView mTextView;
    String mTitle;

    public VpnSpinner(Context context) {
        this(context, null);
    }

    public VpnSpinner(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VpnSpinner(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public VpnSpinner(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setWillNotDraw(false);
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.VpnSpinner, 0, 0);
        try {
            this.mTitle = obtainStyledAttributes.getString(R$styleable.VpnSpinner_spinner_title);
            this.entries = obtainStyledAttributes.getTextArray(R$styleable.VpnSpinner_spinner_entries);
            obtainStyledAttributes.recycle();
            initView(context);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    private void initView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.vpn_spinner, (ViewGroup) this, true);
        this.mTextView = (TextView) inflate.findViewById(R.id.spinner_title);
        this.mSpinner = (Spinner) inflate.findViewById(R.id.right_spinner);
        this.mLayout = (LinearLayout) inflate.findViewById(R.id.spinner_layout);
        this.mTextView.setText(this.mTitle);
        CharSequence[] charSequenceArr = this.entries;
        if (charSequenceArr != null && charSequenceArr.length != 0) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308, this.entries);
            arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
            this.mSpinner.setAdapter((SpinnerAdapter) arrayAdapter);
        }
        setSpinnerDisplayLocation(this.mLayout, this.mSpinner);
    }

    public CharSequence getPrompt() {
        return this.mSpinner.getPrompt();
    }

    public Object getSelectedItem() {
        return this.mSpinner.getSelectedItem();
    }

    public int getSelectedItemPosition() {
        return this.mSpinner.getSelectedItemPosition();
    }

    public Spinner getSpinner() {
        return this.mSpinner;
    }

    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        this.mSpinner.setAdapter(spinnerAdapter);
    }

    public void setPrompt(CharSequence charSequence) {
        this.mSpinner.setPrompt(charSequence);
    }

    public void setSelection(int i) {
        this.mSpinner.setSelection(i);
    }

    public void setSpinnerDisplayLocation(final ViewGroup viewGroup, final Spinner spinner) {
        if (viewGroup == null || spinner == null) {
            return;
        }
        spinner.setClickable(false);
        spinner.setLongClickable(false);
        spinner.setContextClickable(false);
        spinner.setOnSpinnerDismissListener(new Spinner.OnSpinnerDismissListener() { // from class: com.android.settings.vpn2.VpnSpinner.1
            @Override // miuix.appcompat.widget.Spinner.OnSpinnerDismissListener
            public void onSpinnerDismiss() {
                Folme.useAt(viewGroup).touch().touchUp(new AnimConfig[0]);
            }
        });
        viewGroup.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.vpn2.VpnSpinner.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (spinner.isEnabled()) {
                    int action = motionEvent.getAction();
                    if (action == 0) {
                        Folme.useAt(view).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).touchDown(new AnimConfig[0]);
                    } else if (action == 1) {
                        spinner.performClick(motionEvent.getX(), motionEvent.getY());
                    } else if (action == 3) {
                        Folme.useAt(view).touch().touchUp(new AnimConfig[0]);
                    }
                    return true;
                }
                return false;
            }
        });
    }
}
