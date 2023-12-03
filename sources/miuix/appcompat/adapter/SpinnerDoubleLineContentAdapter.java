package miuix.appcompat.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;

/* loaded from: classes5.dex */
public class SpinnerDoubleLineContentAdapter extends ArrayAdapter {
    private static final int TAG_VIEW = R$id.tag_spinner_dropdown_view_double_line;
    protected CharSequence[] mEntries;
    protected Drawable[] mIcons;
    private LayoutInflater mInflater;
    protected CharSequence[] mSummaries;

    /* loaded from: classes5.dex */
    private static class ViewHolder {
        ImageView icon;
        TextView summary;
        TextView title;

        private ViewHolder() {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SpinnerDoubleLineContentAdapter(Context context, int i) {
        super(context, i);
        this.mInflater = LayoutInflater.from(context);
    }

    private CharSequence getEntry(int i) {
        CharSequence[] charSequenceArr = this.mEntries;
        if (charSequenceArr == null || i >= charSequenceArr.length) {
            return null;
        }
        return charSequenceArr[i];
    }

    private Drawable getIcon(int i) {
        Drawable[] drawableArr = this.mIcons;
        if (drawableArr == null || i >= drawableArr.length) {
            return null;
        }
        return drawableArr[i];
    }

    private CharSequence getSummary(int i) {
        CharSequence[] charSequenceArr = this.mSummaries;
        if (charSequenceArr == null || i >= charSequenceArr.length) {
            return null;
        }
        return charSequenceArr[i];
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        CharSequence[] charSequenceArr = this.mEntries;
        if (charSequenceArr == null) {
            return 0;
        }
        return charSequenceArr.length;
    }

    @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (view == null || view.getTag(TAG_VIEW) == null) {
            view = this.mInflater.inflate(R$layout.miuix_appcompat_spiner_dropdown_view_double_line, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(16908294);
            viewHolder.title = (TextView) view.findViewById(16908310);
            viewHolder.summary = (TextView) view.findViewById(16908304);
            view.setTag(TAG_VIEW, viewHolder);
        }
        CharSequence entry = getEntry(i);
        CharSequence summary = getSummary(i);
        Drawable icon = getIcon(i);
        Object tag = view.getTag(TAG_VIEW);
        if (tag != null) {
            ViewHolder viewHolder2 = (ViewHolder) tag;
            if (TextUtils.isEmpty(entry)) {
                viewHolder2.title.setVisibility(8);
            } else {
                viewHolder2.title.setText(entry);
                viewHolder2.title.setVisibility(0);
            }
            if (TextUtils.isEmpty(summary)) {
                viewHolder2.summary.setVisibility(8);
            } else {
                viewHolder2.summary.setText(summary);
                viewHolder2.summary.setVisibility(0);
            }
            if (icon != null) {
                viewHolder2.icon.setImageDrawable(icon);
                viewHolder2.icon.setVisibility(0);
            } else {
                viewHolder2.icon.setVisibility(8);
            }
        }
        return view;
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public Drawable[] getEntryIcons() {
        return this.mIcons;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public Object getItem(int i) {
        CharSequence[] charSequenceArr = this.mEntries;
        if (charSequenceArr == null) {
            return null;
        }
        return charSequenceArr[i];
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    public void setEntries(CharSequence[] charSequenceArr) {
        this.mEntries = charSequenceArr;
    }

    public void setEntryIcons(int[] iArr) {
        if (iArr == null) {
            setEntryIcons((Drawable[]) null);
            return;
        }
        Drawable[] drawableArr = new Drawable[iArr.length];
        Resources resources = getContext().getResources();
        for (int i = 0; i < iArr.length; i++) {
            if (iArr[i] > 0) {
                drawableArr[i] = resources.getDrawable(iArr[i]);
            } else {
                drawableArr[i] = null;
            }
        }
        setEntryIcons(drawableArr);
    }

    public void setEntryIcons(Drawable[] drawableArr) {
        this.mIcons = drawableArr;
    }

    public void setSummaries(CharSequence[] charSequenceArr) {
        this.mSummaries = charSequenceArr;
    }
}
