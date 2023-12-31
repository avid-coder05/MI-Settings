package com.android.settings.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;

/* loaded from: classes2.dex */
public class EntityHeaderController {
    private int mAction1;
    private int mAction2;
    private final Activity mActivity;
    private final Context mAppContext;
    private Intent mAppNotifPrefIntent;
    private View.OnClickListener mEditOnClickListener;
    private final Fragment mFragment;
    private boolean mHasAppInfoLink;
    private final View mHeader;
    private Drawable mIcon;
    private String mIconContentDescription;
    private boolean mIsInstantApp;
    private CharSequence mLabel;
    private Lifecycle mLifecycle;
    private final int mMetricsCategory;
    private String mPackageName;
    private RecyclerView mRecyclerView;
    private CharSequence mSecondSummary;
    private CharSequence mSummary;
    private int mUid = -10000;

    private EntityHeaderController(Activity activity, Fragment fragment, View view) {
        this.mActivity = activity;
        Context applicationContext = activity.getApplicationContext();
        this.mAppContext = applicationContext;
        this.mFragment = fragment;
        this.mMetricsCategory = FeatureFactory.getFactory(applicationContext).getMetricsFeatureProvider().getMetricsCategory(fragment);
        if (view != null) {
            this.mHeader = view;
        } else {
            this.mHeader = LayoutInflater.from(fragment.getContext()).inflate(R.layout.settings_entity_header, (ViewGroup) null);
        }
    }

    private void bindAppInfoLink(View view) {
        String str;
        if (this.mHasAppInfoLink) {
            if (view == null || (str = this.mPackageName) == null || str.equals("os") || this.mUid == -10000) {
                Log.w("AppDetailFeature", "Missing ingredients to build app info link, skip");
            } else {
                view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.EntityHeaderController.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        AppInfoBase.startAppInfoFragment(AppInfoDashboardFragment.class, R.string.application_info_label, EntityHeaderController.this.mPackageName, EntityHeaderController.this.mUid, EntityHeaderController.this.mFragment, 0, EntityHeaderController.this.mMetricsCategory);
                    }
                });
            }
        }
    }

    private void bindButton(ImageButton imageButton, int i) {
        if (imageButton == null) {
            return;
        }
        if (i == 0) {
            imageButton.setVisibility(8);
        } else if (i == 1) {
            if (this.mAppNotifPrefIntent == null) {
                imageButton.setVisibility(8);
                return;
            }
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.EntityHeaderController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EntityHeaderController.this.lambda$bindButton$0(view);
                }
            });
            imageButton.setVisibility(0);
        } else if (i != 2) {
        } else {
            if (this.mEditOnClickListener == null) {
                imageButton.setVisibility(8);
                return;
            }
            imageButton.setImageResource(17302782);
            imageButton.setVisibility(0);
            imageButton.setOnClickListener(this.mEditOnClickListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindButton$0(View view) {
        FeatureFactory.getFactory(this.mAppContext).getMetricsFeatureProvider().action(0, 1016, this.mMetricsCategory, null, 0);
        this.mFragment.startActivity(this.mAppNotifPrefIntent);
    }

    public static EntityHeaderController newInstance(Activity activity, Fragment fragment, View view) {
        return new EntityHeaderController(activity, fragment, view);
    }

    private void setText(int i, CharSequence charSequence) {
        TextView textView = (TextView) this.mHeader.findViewById(i);
        if (textView != null) {
            textView.setText(charSequence);
            textView.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        }
    }

    public EntityHeaderController bindHeaderButtons() {
        View findViewById = this.mHeader.findViewById(R.id.entity_header_content);
        ImageButton imageButton = (ImageButton) this.mHeader.findViewById(16908313);
        ImageButton imageButton2 = (ImageButton) this.mHeader.findViewById(16908314);
        bindAppInfoLink(findViewById);
        bindButton(imageButton, this.mAction1);
        bindButton(imageButton2, this.mAction2);
        return this;
    }

    View done(Activity activity) {
        return done(activity, true);
    }

    public View done(Activity activity, boolean z) {
        ImageView imageView = (ImageView) this.mHeader.findViewById(R.id.entity_header_icon);
        if (imageView != null) {
            imageView.setImageDrawable(this.mIcon);
            imageView.setContentDescription(this.mIconContentDescription);
        }
        setText(R.id.entity_header_title, this.mLabel);
        setText(R.id.entity_header_summary, this.mSummary);
        setText(R.id.entity_header_second_summary, this.mSecondSummary);
        if (this.mIsInstantApp) {
            setText(R.id.install_type, this.mHeader.getResources().getString(R.string.install_type_instant));
        }
        if (z) {
            bindHeaderButtons();
        }
        return this.mHeader;
    }

    public LayoutPreference done(Activity activity, Context context) {
        LayoutPreference layoutPreference = new LayoutPreference(context, done(activity));
        layoutPreference.setOrder(-1000);
        layoutPreference.setSelectable(false);
        layoutPreference.setKey("pref_app_header");
        layoutPreference.setAllowDividerBelow(true);
        return layoutPreference;
    }

    public EntityHeaderController setButtonActions(int i, int i2) {
        this.mAction1 = i;
        this.mAction2 = i2;
        return this;
    }

    public EntityHeaderController setHasAppInfoLink(boolean z) {
        this.mHasAppInfoLink = z;
        return this;
    }

    public EntityHeaderController setIcon(Drawable drawable) {
        if (drawable != null) {
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                drawable = constantState.newDrawable(this.mAppContext.getResources());
            }
            this.mIcon = drawable;
        }
        return this;
    }

    public EntityHeaderController setIcon(ApplicationsState.AppEntry appEntry) {
        this.mIcon = Utils.getBadgedIcon(this.mAppContext, appEntry.info);
        return this;
    }

    public EntityHeaderController setIconContentDescription(String str) {
        this.mIconContentDescription = str;
        return this;
    }

    public EntityHeaderController setIsInstantApp(boolean z) {
        this.mIsInstantApp = z;
        return this;
    }

    public EntityHeaderController setLabel(ApplicationsState.AppEntry appEntry) {
        this.mLabel = appEntry.label;
        return this;
    }

    public EntityHeaderController setLabel(CharSequence charSequence) {
        this.mLabel = charSequence;
        return this;
    }

    public EntityHeaderController setPackageName(String str) {
        this.mPackageName = str;
        return this;
    }

    public EntityHeaderController setRecyclerView(RecyclerView recyclerView, Lifecycle lifecycle) {
        this.mRecyclerView = recyclerView;
        this.mLifecycle = lifecycle;
        return this;
    }

    public EntityHeaderController setSecondSummary(CharSequence charSequence) {
        this.mSecondSummary = charSequence;
        return this;
    }

    public EntityHeaderController setSummary(PackageInfo packageInfo) {
        if (packageInfo != null) {
            this.mSummary = packageInfo.versionName;
        }
        return this;
    }

    public EntityHeaderController setSummary(CharSequence charSequence) {
        this.mSummary = charSequence;
        return this;
    }

    public EntityHeaderController setUid(int i) {
        this.mUid = i;
        return this;
    }
}
