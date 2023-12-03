package com.android.settings.emergency.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;

/* loaded from: classes.dex */
public class ContactsItemView extends LinearLayout {
    private ImageView mDeleteBtn;
    private ImageView mDragBtn;
    private boolean mEditMode;
    private TextView mName;
    private TextView mNumber;

    public ContactsItemView(Context context) {
        super(context);
    }

    public ContactsItemView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void bindData(Pair<String, String> pair) {
        this.mDeleteBtn.setTag(pair);
        this.mName.setText((CharSequence) pair.first);
        this.mNumber.setText((CharSequence) pair.second);
        this.mName.setTextColor(getResources().getColor(R.color.emergency_contact_name_color));
        this.mNumber.setTextColor(getResources().getColor(R.color.emergency_contact_number_color));
        if (this.mEditMode) {
            this.mDragBtn.setVisibility(0);
            this.mDeleteBtn.setVisibility(0);
            return;
        }
        this.mDragBtn.setVisibility(8);
        this.mDeleteBtn.setVisibility(8);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mName = (TextView) findViewById(R.id.name);
        this.mNumber = (TextView) findViewById(R.id.number);
        this.mDragBtn = (ImageView) findViewById(R.id.drag_btn);
        this.mDeleteBtn = (ImageView) findViewById(R.id.delete_btn);
    }

    public void setEditMode(boolean z) {
        this.mEditMode = z;
    }

    public void setOnDeleteBtnClickListener(View.OnClickListener onClickListener) {
        this.mDeleteBtn.setOnClickListener(onClickListener);
    }

    public void setOnDragBtnClickListener(View.OnTouchListener onTouchListener) {
        this.mDragBtn.setOnTouchListener(onTouchListener);
    }
}
