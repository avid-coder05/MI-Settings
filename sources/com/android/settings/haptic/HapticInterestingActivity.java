package com.android.settings.haptic;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.haptic.widget.HapticGridView;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class HapticInterestingActivity extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class HapticInterestingFragment extends Fragment {
        HapticGridView mGridView;
        TextView mTv;

        @Override // androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(R.layout.fragment_haptic_detail_base, viewGroup, false);
        }

        @Override // androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mGridView = (HapticGridView) view.findViewById(R.id.ringtone_grid);
            TextView textView = (TextView) view.findViewById(R.id.haptic_text);
            this.mTv = textView;
            textView.setText(R.string.interesting_text);
            HapticGridView hapticGridView = this.mGridView;
            if (hapticGridView != null) {
                hapticGridView.setType(6);
            }
        }
    }

    private void addFragment() {
        getSupportFragmentManager().beginTransaction().replace(16908290, new HapticInterestingFragment()).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$0() {
        getWindow().getDecorView().setSystemUiVisibility(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getAppCompatActionBar().setBackgroundDrawable(new ColorDrawable(17170445));
        getWindow().getDecorView().setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{getColor(R.color.haptic_interesting), getColor(R.color.haptic_interesting_bottom)}));
        addFragment();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        getMainThreadHandler().postDelayed(new Runnable() { // from class: com.android.settings.haptic.HapticInterestingActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HapticInterestingActivity.this.lambda$onResume$0();
            }
        }, 100L);
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public void setTheme(int i) {
        super.setTheme(R.style.InterestingTheme);
    }
}
