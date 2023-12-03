package com.android.settings.homepage;

import android.app.ActivityManager;
import android.os.Bundle;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.R;
import com.android.settings.accounts.AvatarViewMixin;
import com.android.settings.core.CategoryMixin;
import com.android.settings.homepage.contextualcards.ContextualCardsFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;

/* loaded from: classes.dex */
public class SettingsHomepageActivity extends FragmentActivity implements CategoryMixin.CategoryHandler {
    private CategoryMixin mCategoryMixin;
    private View mHomepageView;
    private View mSuggestionView;

    private int getSearchBoxHeight() {
        return getResources().getDimensionPixelSize(R.dimen.search_bar_height) + (getResources().getDimensionPixelSize(R.dimen.search_bar_margin) * 2);
    }

    private void initHomepageContainer() {
        View findViewById = findViewById(R.id.homepage_container);
        findViewById.setFocusableInTouchMode(true);
        findViewById.requestFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuggestionFragment$0() {
        showHomepageWithSuggestion(false);
    }

    private void showFragment(Fragment fragment, int i) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        Fragment findFragmentById = supportFragmentManager.findFragmentById(i);
        if (findFragmentById == null) {
            beginTransaction.add(i, fragment);
        } else {
            beginTransaction.show(findFragmentById);
        }
        beginTransaction.commit();
    }

    private void showSuggestionFragment() {
        Class<? extends Fragment> contextualSuggestionFragment = FeatureFactory.getFactory(this).getSuggestionFeatureProvider(this).getContextualSuggestionFragment();
        if (contextualSuggestionFragment == null) {
            return;
        }
        int i = R.id.suggestion_content;
        this.mSuggestionView = findViewById(i);
        View findViewById = findViewById(R.id.settings_homepage_container);
        this.mHomepageView = findViewById;
        findViewById.setVisibility(8);
        this.mHomepageView.postDelayed(new Runnable() { // from class: com.android.settings.homepage.SettingsHomepageActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SettingsHomepageActivity.this.lambda$showSuggestionFragment$0();
            }
        }, 300L);
        try {
            showFragment(contextualSuggestionFragment.getConstructor(new Class[0]).newInstance(new Object[0]), i);
        } catch (Exception e) {
            Log.w("SettingsHomepageActivity", "Cannot show fragment", e);
        }
    }

    @Override // com.android.settings.core.CategoryMixin.CategoryHandler
    public CategoryMixin getCategoryMixin() {
        return this.mCategoryMixin;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.settings_homepage_container);
        findViewById(R.id.app_bar_container).setMinimumHeight(getSearchBoxHeight());
        initHomepageContainer();
        FeatureFactory.getFactory(this).getSearchFeatureProvider().initSearchToolbar(this, (Toolbar) findViewById(R.id.search_action_bar), 1502);
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        this.mCategoryMixin = new CategoryMixin(this);
        getLifecycle().addObserver(this.mCategoryMixin);
        if (!((ActivityManager) getSystemService(ActivityManager.class)).isLowRamDevice()) {
            ImageView imageView = (ImageView) findViewById(R.id.account_avatar);
            if (AvatarViewMixin.isAvatarSupported(this)) {
                imageView.setVisibility(0);
                getLifecycle().addObserver(new AvatarViewMixin(this, imageView));
            }
            showSuggestionFragment();
            if (FeatureFlagUtils.isEnabled(this, "settings_contextual_home")) {
                showFragment(new ContextualCardsFragment(), R.id.contextual_cards_content);
            }
        }
        TopLevelSettings topLevelSettings = new TopLevelSettings();
        int i = R.id.main_content;
        showFragment(topLevelSettings, i);
        ((FrameLayout) findViewById(i)).getLayoutTransition().enableTransitionType(4);
    }

    public void showHomepageWithSuggestion(boolean z) {
        if (this.mHomepageView == null) {
            return;
        }
        Log.i("SettingsHomepageActivity", "showHomepageWithSuggestion: " + z);
        this.mSuggestionView.setVisibility(z ? 0 : 8);
        this.mHomepageView.setVisibility(0);
        this.mHomepageView = null;
    }
}
