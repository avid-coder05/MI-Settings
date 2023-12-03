package com.android.settings.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;

/* loaded from: classes2.dex */
public class PopupCameraLedListAdapter extends BaseAdapter {
    private int mChooseIndex;
    private Context mContext;
    private int[] mPictures;
    private PopupCameraLedChoosePreference mPreference;

    /* loaded from: classes2.dex */
    class Holder {
        ImageView iv;

        Holder() {
        }
    }

    public PopupCameraLedListAdapter(Context context, PopupCameraLedChoosePreference popupCameraLedChoosePreference, int[] iArr) {
        this.mContext = context;
        this.mPreference = popupCameraLedChoosePreference;
        this.mPictures = iArr;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mPictures.length;
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return Integer.valueOf(this.mPictures[i]);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        Holder holder;
        boolean isEnabled = this.mPreference.getGridView().isEnabled();
        if (view == null) {
            holder = new Holder();
            view2 = LayoutInflater.from(this.mContext).inflate(R.layout.popup_led_gridview_item, viewGroup, false);
            holder.iv = (ImageView) view2.findViewById(R.id.iv_item);
            view2.setTag(holder);
        } else {
            view2 = view;
            holder = (Holder) view.getTag();
        }
        holder.iv.setImageDrawable(this.mContext.getResources().getDrawable(this.mPictures[i]));
        holder.iv.setAlpha(isEnabled ? 1.0f : 0.4f);
        view2.setBackgroundResource((i == this.mChooseIndex && isEnabled) ? R.drawable.popup_led_background_view : 0);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.useAt(view2).touch().handleTouchOf(view2, new AnimConfig[0]);
        }
        return view2;
    }

    public void setChooseItem(int i) {
        this.mChooseIndex = i;
        notifyDataSetChanged();
    }
}
