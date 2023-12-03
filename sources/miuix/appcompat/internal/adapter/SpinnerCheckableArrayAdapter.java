package miuix.appcompat.internal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;

/* loaded from: classes5.dex */
public class SpinnerCheckableArrayAdapter extends ArrayAdapter {
    public static final int TAG_VIEW = R$id.tag_spinner_dropdown_view;
    private CheckedStateProvider mCheckProvider;
    private ArrayAdapter mContentAdapter;
    private LayoutInflater mInflater;

    /* loaded from: classes5.dex */
    public interface CheckedStateProvider {
        boolean isChecked(int i);
    }

    /* loaded from: classes5.dex */
    private static class ViewHolder {
        FrameLayout container;
        RadioButton radioButton;

        private ViewHolder() {
        }
    }

    public SpinnerCheckableArrayAdapter(Context context, int i, ArrayAdapter arrayAdapter, CheckedStateProvider checkedStateProvider) {
        super(context, i, 16908308);
        this.mInflater = LayoutInflater.from(context);
        this.mContentAdapter = arrayAdapter;
        this.mCheckProvider = checkedStateProvider;
    }

    public SpinnerCheckableArrayAdapter(Context context, ArrayAdapter arrayAdapter, CheckedStateProvider checkedStateProvider) {
        this(context, R$layout.miuix_appcompat_simple_spinner_layout_integrated, arrayAdapter, checkedStateProvider);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        return this.mContentAdapter.getCount();
    }

    @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        boolean z = false;
        if (view == null || view.getTag(TAG_VIEW) == null) {
            view = this.mInflater.inflate(R$layout.miuix_appcompat_spinner_dropdown_checkable_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.container = (FrameLayout) view.findViewById(R$id.spinner_dropdown_container);
            viewHolder.radioButton = (RadioButton) view.findViewById(16908289);
            view.setTag(TAG_VIEW, viewHolder);
        }
        Object tag = view.getTag(TAG_VIEW);
        if (tag != null) {
            ViewHolder viewHolder2 = (ViewHolder) tag;
            View dropDownView = this.mContentAdapter.getDropDownView(i, viewHolder2.container.getChildAt(0), viewGroup);
            viewHolder2.container.removeAllViews();
            viewHolder2.container.addView(dropDownView);
            CheckedStateProvider checkedStateProvider = this.mCheckProvider;
            if (checkedStateProvider != null && checkedStateProvider.isChecked(i)) {
                z = true;
            }
            viewHolder2.radioButton.setChecked(z);
            view.setActivated(z);
        }
        return view;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public Object getItem(int i) {
        return this.mContentAdapter.getItem(i);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public long getItemId(int i) {
        return this.mContentAdapter.getItemId(i);
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return this.mContentAdapter.hasStableIds();
    }
}
