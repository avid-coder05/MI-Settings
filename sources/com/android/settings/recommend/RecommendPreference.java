package com.android.settings.recommend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.MiuiSettings;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.utils.TabletUtils;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.ArrayList;
import java.util.List;
import miuix.animation.Folme;

/* loaded from: classes2.dex */
public class RecommendPreference extends Preference {
    private static final String TAG = "RecommendPreference";
    private Context mContext;
    private List<RelativeLayout> mItemList;
    private LinearLayout mLinearLayout;
    private boolean mNeedClear;
    private String mRecommendTips;
    private TextView mRecommendTipsTv;
    private int mTopMargin;

    public RecommendPreference(Context context) {
        this(context, 0, false);
    }

    public RecommendPreference(Context context, int i, boolean z) {
        this(context, null);
        this.mContext = context;
        this.mTopMargin = i;
        this.mNeedClear = z;
    }

    public RecommendPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mItemList = new ArrayList();
    }

    private void tryClearRecommendView() {
        LinearLayout linearLayout = this.mLinearLayout;
        if (linearLayout == null || !this.mNeedClear) {
            return;
        }
        for (int childCount = linearLayout.getChildCount() - 1; childCount > 0; childCount--) {
            this.mLinearLayout.removeViewAt(childCount);
        }
    }

    public void addRecommendView(SpannableString spannableString, final Intent intent) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(this.mContext).inflate(R.layout.recommend_item, (ViewGroup) null);
        TextView textView = (TextView) relativeLayout.findViewById(R.id.item_view);
        textView.setText(spannableString);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.recommend.RecommendPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String stringExtra = intent.getStringExtra(":settings:show_fragment");
                if (!TabletUtils.IS_TABLET || TextUtils.isEmpty(stringExtra) || !(RecommendPreference.this.mContext instanceof MiuiSettings)) {
                    RecommendPreference.this.mContext.startActivity(intent);
                    return;
                }
                Bundle bundleExtra = intent.getBundleExtra(":android:show_fragment_args");
                if (bundleExtra == null) {
                    bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
                }
                RecommendPreference.this.startWithFragment(stringExtra, bundleExtra);
            }
        });
        this.mItemList.add(relativeLayout);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setPadding(0, 0, 0, 0);
        view.setBackgroundColor(0);
        this.mLinearLayout = (LinearLayout) view.findViewById(R.id.line_layout);
        TextView textView = (TextView) view.findViewById(R.id.recommend_tip);
        this.mRecommendTipsTv = textView;
        if (textView != null && !TextUtils.isEmpty(this.mRecommendTips)) {
            this.mRecommendTipsTv.setText(this.mRecommendTips);
        }
        if (this.mTopMargin > 0) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mLinearLayout.getLayoutParams();
            layoutParams.topMargin = this.mTopMargin;
            this.mLinearLayout.setLayoutParams(layoutParams);
        }
        tryClearRecommendView();
        for (RelativeLayout relativeLayout : this.mItemList) {
            if (relativeLayout.getParent() == null) {
                this.mLinearLayout.addView(relativeLayout);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.recommend_layout);
        return null;
    }

    public void setRecommendTips(String str) {
        this.mRecommendTips = str;
        notifyChanged();
    }

    public void startWithFragment(String str, Bundle bundle) {
        FragmentManager supportFragmentManager = ((AppCompatActivity) this.mContext).getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag(str);
        if (findFragmentByTag == null) {
            findFragmentByTag = Fragment.instantiate(this.mContext, str, bundle);
        }
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        beginTransaction.addToBackStack(str);
        beginTransaction.replace(R.id.content, findFragmentByTag, str);
        beginTransaction.commitAllowingStateLoss();
    }
}
