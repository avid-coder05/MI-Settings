package com.android.settings.haptic;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.haptic.utils.ViewUtils;
import com.android.settings.haptic.widget.HapticGridView;
import com.android.settingslib.util.MiStatInterfaceUtils;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.Fragment;

/* loaded from: classes.dex */
public class HapticDetailActivity extends AppCompatActivity implements ActionBar.FragmentViewPagerChangeListener {
    int[] colors = new int[2];
    private ActionBar mBar;
    private int mCurrentTab;
    private View mDecor;
    private GradientDrawable mGradientDrawable;

    /* loaded from: classes.dex */
    public static abstract class BaseHapticFragment extends Fragment {
        private HapticGridView mGridView;
        private TextView mTv;

        public abstract int getLayoutId();

        public abstract int getResType();

        public abstract int getText();

        @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            this.mGridView.onDestroy();
        }

        @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
        public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(getLayoutId(), viewGroup, false);
        }

        public void onPageChange(int i) {
            this.mGridView.onPageChange();
        }

        @Override // androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
        }

        @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onStop() {
            this.mGridView.onStop();
            super.onStop();
        }

        @Override // androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mGridView = (HapticGridView) view.findViewById(R.id.ringtone_grid);
            TextView textView = (TextView) view.findViewById(R.id.haptic_text);
            this.mTv = textView;
            textView.setText(getText());
            HapticGridView hapticGridView = this.mGridView;
            if (hapticGridView != null) {
                hapticGridView.setType(getResType());
            }
        }
    }

    /* loaded from: classes.dex */
    public static class CrispFragment extends BaseHapticFragment {
        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getLayoutId() {
            return R.layout.fragment_haptic_detail_base;
        }

        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getResType() {
            return 4;
        }

        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getText() {
            return R.string.crisp_text;
        }
    }

    /* loaded from: classes.dex */
    public static class ElasticFragment extends BaseHapticFragment {
        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getLayoutId() {
            return R.layout.fragment_haptic_detail_base;
        }

        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getResType() {
            return 3;
        }

        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getText() {
            return R.string.elastic_text;
        }
    }

    /* loaded from: classes.dex */
    public static class MuffledFragment extends BaseHapticFragment {
        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getLayoutId() {
            return R.layout.fragment_haptic_detail_base;
        }

        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getResType() {
            return 5;
        }

        @Override // com.android.settings.haptic.HapticDetailActivity.BaseHapticFragment
        public int getText() {
            return R.string.muffled_text;
        }
    }

    private int[] getGradientDrawable(float f, int i, int i2, int i3, int i4) {
        int transitionColor = ViewUtils.getTransitionColor(f, i, i3);
        int transitionColor2 = ViewUtils.getTransitionColor(f, i2, i4);
        int[] iArr = this.colors;
        iArr[0] = transitionColor;
        iArr[1] = transitionColor2;
        return iArr;
    }

    private void initActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        this.mBar = appCompatActionBar;
        if (appCompatActionBar != null) {
            appCompatActionBar.setBackgroundDrawable(new ColorDrawable(17170445));
            this.mBar.setFragmentViewPagerMode(this, false);
            String[] stringArray = getResources().getStringArray(R.array.tabs_taptic_detail);
            ImageView imageView = new ImageView(this);
            imageView.setContentDescription(getResources().getString(R.string.back));
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.haptic.HapticDetailActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    HapticDetailActivity.this.lambda$initActionBar$1(view);
                }
            });
            imageView.setImageResource(R.drawable.miuix_appcompat_action_bar_back_dark);
            this.mBar.setStartView(imageView);
            ActionBar actionBar = this.mBar;
            actionBar.addFragmentTab(stringArray[0], actionBar.newTab().setText(stringArray[0]), CrispFragment.class, null, false);
            ActionBar actionBar2 = this.mBar;
            actionBar2.addFragmentTab(stringArray[1], actionBar2.newTab().setText(stringArray[1]), MuffledFragment.class, null, false);
            ActionBar actionBar3 = this.mBar;
            actionBar3.addFragmentTab(stringArray[2], actionBar3.newTab().setText(stringArray[2]), ElasticFragment.class, null, false);
            this.mBar.addOnFragmentViewPagerChangeListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initActionBar$1(View view) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$0() {
        this.mDecor.setSystemUiVisibility(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{getColor(R.color.haptic_crisps), getColor(R.color.haptic_crisps_bottom)});
        View decorView = getWindow().getDecorView();
        this.mDecor = decorView;
        decorView.setBackground(this.mGradientDrawable);
        initActionBar();
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        int i2 = this.mCurrentTab;
        if (i2 == 0 && i == 0 && f > 0.0f) {
            this.mGradientDrawable.setColors(getGradientDrawable(f, getColor(R.color.haptic_crisps), getColor(R.color.haptic_crisps_bottom), getColor(R.color.haptic_low), getColor(R.color.haptic_low_bottom)));
            this.mDecor.setBackground(this.mGradientDrawable);
        } else if (i2 == 1 && i == 1 && f > 0.0f) {
            this.mGradientDrawable.setColors(getGradientDrawable(f, getColor(R.color.haptic_low), getColor(R.color.haptic_low_bottom), getColor(R.color.haptic_soft), getColor(R.color.haptic_soft_bottom)));
            this.mDecor.setBackground(this.mGradientDrawable);
        } else if (i2 == 1 && i == 0 && f > 0.0f) {
            this.mGradientDrawable.setColors(getGradientDrawable(1.0f - f, getColor(R.color.haptic_low), getColor(R.color.haptic_low_bottom), getColor(R.color.haptic_crisps), getColor(R.color.haptic_crisps_bottom)));
            this.mDecor.setBackground(this.mGradientDrawable);
        } else if (i2 == 2 && i == 1 && f > 0.0f) {
            this.mGradientDrawable.setColors(getGradientDrawable(1.0f - f, getColor(R.color.haptic_soft), getColor(R.color.haptic_soft_bottom), getColor(R.color.haptic_low), getColor(R.color.haptic_low_bottom)));
            this.mDecor.setBackground(this.mGradientDrawable);
        }
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageSelected(int i) {
        if (this.mCurrentTab != i) {
            String[] stringArray = getResources().getStringArray(R.array.tabs_taptic_detail);
            ((BaseHapticFragment) getSupportFragmentManager().findFragmentByTag(stringArray[this.mCurrentTab])).onPageChange(this.mCurrentTab);
            this.mCurrentTab = i;
            MiStatInterfaceUtils.trackPreferenceClick(HapticDetailActivity.class.getName(), stringArray[this.mCurrentTab]);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        getMainThreadHandler().postDelayed(new Runnable() { // from class: com.android.settings.haptic.HapticDetailActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                HapticDetailActivity.this.lambda$onResume$0();
            }
        }, 100L);
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public void setTheme(int i) {
        super.setTheme(R.style.HapticDetailTheme);
    }
}
