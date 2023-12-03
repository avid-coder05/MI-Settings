package com.android.settings.notification;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.BaseListFragment;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class SilentModeNetWorkAlarmAppFragment extends BaseListFragment {
    private HeaderAdapter mAdapter;
    private ArrayMap<PreferenceActivity.Header, Drawable> mHeaderToDrawable = new ArrayMap<>();

    /* loaded from: classes2.dex */
    private static class HeaderAdapter extends ArrayAdapter<PreferenceActivity.Header> {
        private Context mContext;
        private ArrayMap<PreferenceActivity.Header, Drawable> mHeaderToDrawable;
        private LayoutInflater mInflater;

        public HeaderAdapter(Context context, List<PreferenceActivity.Header> list, ArrayMap<PreferenceActivity.Header, Drawable> arrayMap) {
            super(context, 0, list);
            this.mContext = context;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mHeaderToDrawable = arrayMap;
        }

        private Drawable getAppIcon(PreferenceActivity.Header header) {
            return this.mHeaderToDrawable.get(header);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            PreferenceActivity.Header item = getItem(i);
            if (view != null) {
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = this.mInflater.inflate(R.layout.miuix_preference_layout, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) view.findViewById(16908310);
                viewHolder.icon = (ImageView) view.findViewById(16908294);
                int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.system_app_text_view_padding_start);
                TextView textView = viewHolder.title;
                textView.setPaddingRelative(dimensionPixelSize, textView.getPaddingTop(), viewHolder.title.getPaddingEnd(), viewHolder.title.getPaddingBottom());
                int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(R.dimen.application_icon_size);
                viewHolder.icon.getLayoutParams().height = dimensionPixelSize2;
                viewHolder.icon.getLayoutParams().width = dimensionPixelSize2;
                viewHolder.icon.setPadding(0, 0, 0, 0);
                view.findViewById(16908304).setVisibility(8);
                view.setTag(viewHolder);
            }
            viewHolder.title.setText(item.getTitle(this.mContext.getResources()));
            viewHolder.icon.setImageDrawable(getAppIcon(item));
            return view;
        }
    }

    /* loaded from: classes2.dex */
    private static class ViewHolder {
        public ImageView icon;
        public TextView title;

        private ViewHolder() {
        }
    }

    private void initWeChatAppHeader(List<PreferenceActivity.Header> list) {
        PreferenceActivity.Header header = new PreferenceActivity.Header();
        header.title = getString(R.string.wechat);
        list.add(header);
        this.mHeaderToDrawable.put(header, getActivity().getResources().getDrawable(R.drawable.ic_wechat));
    }

    @Override // com.android.settings.BaseListFragment, miuix.appcompat.app.ListFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        activity.setTitle(getResources().getString(R.string.network_alarm_support_apps));
        List<PreferenceActivity.Header> arrayList = new ArrayList<>();
        initWeChatAppHeader(arrayList);
        HeaderAdapter headerAdapter = new HeaderAdapter(activity, arrayList, this.mHeaderToDrawable);
        this.mAdapter = headerAdapter;
        setListAdapter(headerAdapter);
    }
}
