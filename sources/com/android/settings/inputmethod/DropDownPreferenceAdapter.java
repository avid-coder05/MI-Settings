package com.android.settings.inputmethod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.settings.R;
import java.util.Arrays;

/* loaded from: classes.dex */
public class DropDownPreferenceAdapter extends ArrayAdapter {
    private CharSequence[] mDemoItems;
    private LayoutInflater mInflater;

    /* loaded from: classes.dex */
    private static class ViewHolder {
        TextView text;

        private ViewHolder() {
        }
    }

    public DropDownPreferenceAdapter(Context context, CharSequence[] charSequenceArr) {
        super(context, R.layout.miuix_appcompat_simple_spinner_layout, 16908308);
        this.mInflater = LayoutInflater.from(context);
        this.mDemoItems = charSequenceArr;
    }

    @Override // android.widget.ArrayAdapter
    public void addAll(Object[] objArr) {
        CharSequence[] charSequenceArr = this.mDemoItems;
        CharSequence[] charSequenceArr2 = (CharSequence[]) Arrays.copyOf(charSequenceArr, charSequenceArr.length + objArr.length);
        System.arraycopy(objArr, 0, charSequenceArr2, this.mDemoItems.length, objArr.length);
        this.mDemoItems = charSequenceArr2;
    }

    @Override // android.widget.ArrayAdapter
    public void clear() {
        this.mDemoItems = new CharSequence[0];
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        return this.mDemoItems.length;
    }

    @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mInflater.inflate(R.layout.dropdown_demo_adapter_layout, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view;
            view.setTag(viewHolder);
        }
        Object tag = view.getTag();
        if (tag != null) {
            ((ViewHolder) tag).text.setText((CharSequence) getItem(i));
        }
        return view;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public Object getItem(int i) {
        return this.mDemoItems[i];
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public void updateEnabledIME(CharSequence[] charSequenceArr) {
        this.mDemoItems = charSequenceArr;
        clear();
        addAll(charSequenceArr);
        notifyDataSetChanged();
    }
}
