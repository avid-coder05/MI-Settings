package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class DialogListPreference extends MiuiListPreference {
    private int mCheckedItem;
    private ListAdapter mListAdapter;
    private CharSequence[] mSummaries;

    /* loaded from: classes.dex */
    private class ListAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public ListAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        private CharSequence getSummaryText(int i) {
            return (DialogListPreference.this.mSummaries == null || i < 0 || i >= DialogListPreference.this.mSummaries.length) ? "" : DialogListPreference.this.mSummaries[i];
        }

        private CharSequence getTitleText(int i) {
            return (DialogListPreference.this.getEntries() == null || i < 0 || i >= DialogListPreference.this.getEntries().length) ? "" : DialogListPreference.this.getEntries()[i];
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return DialogListPreference.this.getEntries().length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return DialogListPreference.this.getEntries()[i];
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(R.layout.dialog_list_item, (ViewGroup) null);
            }
            CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.content);
            TextView textView = (TextView) view.findViewById(R.id.summary);
            Drawable checkMarkDrawable = checkedTextView.getCheckMarkDrawable();
            if (checkMarkDrawable != null) {
                int left = checkedTextView.getLeft() + checkedTextView.getPaddingStart() + checkMarkDrawable.getCurrent().getIntrinsicWidth();
                ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    if (marginLayoutParams.getMarginStart() != left) {
                        marginLayoutParams.setMarginStart(left);
                    }
                }
            }
            checkedTextView.setText(getTitleText(i));
            checkedTextView.setChecked(i == DialogListPreference.this.mCheckedItem);
            textView.setText(getSummaryText(i));
            return view;
        }
    }

    public DialogListPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DialogListPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.attr.textPreferenceStyle);
    }

    public DialogListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.DialogListPreference, i, i2);
        this.mSummaries = obtainStyledAttributes.getTextArray(R$styleable.DialogListPreference_summaries);
        obtainStyledAttributes.recycle();
        this.mListAdapter = new ListAdapter(context);
    }

    private String getString(int i) {
        return getContext().getString(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setCustomTitle(null);
        builder.setTitle(getDialogTitle());
        builder.setSingleChoiceItems(this.mListAdapter, this.mCheckedItem, new DialogInterface.OnClickListener() { // from class: com.android.settings.DialogListPreference.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i < 0 || i >= DialogListPreference.this.getEntries().length) {
                    return;
                }
                DialogListPreference.this.setValueIndex(i);
                DialogListPreference.this.getDialog().dismiss();
                DialogListPreference.this.callChangeListener(Integer.valueOf(i));
            }
        });
        builder.setPositiveButton((CharSequence) null, (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() { // from class: com.android.settings.DialogListPreference.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    @Override // androidx.preference.ListPreference
    public void setValue(String str) {
        super.setValue(str);
        setSummary(getEntry());
        this.mCheckedItem = findIndexOfValue(str);
    }

    @Override // androidx.preference.ListPreference
    public void setValueIndex(int i) {
        if (i >= 0) {
            super.setValueIndex(i);
        }
        this.mCheckedItem = i;
    }
}
