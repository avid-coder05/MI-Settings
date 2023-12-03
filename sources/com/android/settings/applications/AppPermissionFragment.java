package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.BaseListFragment;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import com.miui.maml.util.AppIconsHelper;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

/* loaded from: classes.dex */
public class AppPermissionFragment extends BaseListFragment {
    private HeaderAdapter mAdapter;
    private ArrayMap<PreferenceActivity.Header, ApplicationInfo> mHeaderToApplicationInfo = new ArrayMap<>();
    private PackageManager mPm;

    /* loaded from: classes.dex */
    private static class HeaderAdapter extends ArrayAdapter<PreferenceActivity.Header> {
        private Context mContext;
        private ArrayMap<PreferenceActivity.Header, ApplicationInfo> mHeaderToApplication;
        private LayoutInflater mInflater;
        private PackageManager mPm;

        public HeaderAdapter(Context context, List<PreferenceActivity.Header> list, ArrayMap<PreferenceActivity.Header, ApplicationInfo> arrayMap) {
            super(context, 0, list);
            this.mContext = context;
            this.mPm = context.getPackageManager();
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mHeaderToApplication = arrayMap;
        }

        private Drawable getAppIcon(PreferenceActivity.Header header) {
            return AppIconsHelper.getIconDrawable(this.mContext, this.mHeaderToApplication.get(header), this.mPm, 600000L);
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
                if (!Build.IS_TABLET) {
                    view.findViewById(R.id.arrow_right).setVisibility(0);
                }
                view.setTag(viewHolder);
            }
            viewHolder.title.setText(item.getTitle(this.mContext.getResources()));
            viewHolder.icon.setImageDrawable(getAppIcon(item));
            return view;
        }
    }

    /* loaded from: classes.dex */
    private static class ViewHolder {
        public ImageView icon;
        public TextView title;

        private ViewHolder() {
        }
    }

    private void initAppHeader(List<PreferenceActivity.Header> list, String[] strArr) {
        List<ApplicationInfo> installedApplications;
        if (strArr == null || strArr.length == 0) {
            installedApplications = this.mPm.getInstalledApplications(0);
        } else {
            installedApplications = new ArrayList<>();
            for (String str : strArr) {
                try {
                    ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(str, 0);
                    if (applicationInfo != null) {
                        installedApplications.add(applicationInfo);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("AppPermissionFragment", "Pacake name: " + str + " not found");
                    e.printStackTrace();
                }
            }
        }
        for (ApplicationInfo applicationInfo2 : installedApplications) {
            PreferenceActivity.Header header = new PreferenceActivity.Header();
            header.title = applicationInfo2.loadLabel(this.mPm);
            list.add(header);
            this.mHeaderToApplicationInfo.put(header, applicationInfo2);
        }
    }

    @Override // com.android.settings.BaseListFragment, miuix.appcompat.app.ListFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        activity.setTitle(getResources().getString(R.string.application_permissions));
        this.mPm = activity.getPackageManager();
        int identifier = getResources().getIdentifier("app_permission", "array", getActivity().getPackageName());
        String[] stringArray = identifier != 0 ? getResources().getStringArray(identifier) : null;
        List<PreferenceActivity.Header> arrayList = new ArrayList<>();
        initAppHeader(arrayList, stringArray);
        HeaderAdapter headerAdapter = new HeaderAdapter(activity, arrayList, this.mHeaderToApplicationInfo);
        this.mAdapter = headerAdapter;
        setListAdapter(headerAdapter);
    }

    @Override // androidx.fragment.app.ListFragment
    public void onListItemClick(ListView listView, View view, int i, long j) {
        ApplicationInfo applicationInfo = this.mHeaderToApplicationInfo.get(this.mAdapter.getItem(i));
        if (applicationInfo != null) {
            String str = applicationInfo.packageName;
            Intent intent = new Intent(getActivity(), PermissionInfoActivity.class);
            try {
                intent.putExtra("extra_package_application", this.mPm.getApplicationInfo(str, 0));
                startActivity(intent);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("AppPermissionFragment", "Package name not found", e);
            }
        }
    }
}
