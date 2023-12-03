package com.android.settings.display;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R;
import java.util.HashMap;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ScreenZoomFragment extends PageLayoutBaseFragment {
    private static final HashMap<Integer, Float> CALL_TITLE_SIZE;
    private static final int[] ICONS = {R.drawable.font_setting_icon_weather2, R.drawable.font_setting_icon_gallery2, R.drawable.font_setting_icon_phone, R.drawable.font_setting_icon_note};
    private static final int[] ICON_NAMES = {R.string.page_layout_11, R.string.page_layout_3, R.string.page_layout_34, R.string.page_layout_35};
    private static final HashMap<Integer, Float> PAGE_LAYOUT_SIZE;
    private static final HashMap<Integer, Integer> SCREEN_ZOOM_HINT;
    private static final HashMap<Integer, Float> SUB_TITLE_SIZE;
    private final HashMap<Integer, Integer> ICON_SIZES = new HashMap<>();
    private LinearLayout mIconGrandParentView;

    /* loaded from: classes.dex */
    public static class ConfirmDialog extends DialogFragment {
        private static final String FRAG_TAG = ConfirmDialog.class.getName();
        private int mType;
        private int mUiMode;

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            if (bundle != null) {
                this.mUiMode = bundle.getInt("com.android.settings.display.ScreenZoomFragment:STATE_SCREEN_ZOOM");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(this.mType == 1 ? R.string.page_layout_confirm_title_enlarge : R.string.page_layout_confirm_title_narrow);
            builder.setMessage(this.mType == 1 ? R.string.page_layout_confirm_message_enlarge : R.string.page_layout_confirm_message_narrow);
            builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.ScreenZoomFragment.ConfirmDialog.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ScreenZoomUtils.setZoomLevel(ConfirmDialog.this.getContext(), ConfirmDialog.this.mUiMode);
                    ConfirmDialog.this.getActivity().onBackPressed();
                }
            });
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder.create();
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("com.android.settings.display.ScreenZoomFragment:STATE_SCREEN_ZOOM", this.mUiMode);
        }

        public void setCurrentUiMode(int i) {
            this.mUiMode = i;
        }

        public void setType(int i) {
            this.mType = i;
        }
    }

    static {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        SCREEN_ZOOM_HINT = hashMap;
        CALL_TITLE_SIZE = new HashMap<>();
        SUB_TITLE_SIZE = new HashMap<>();
        PAGE_LAYOUT_SIZE = new HashMap<>();
        hashMap.put(0, Integer.valueOf(R.string.screen_zoom_small));
        hashMap.put(1, Integer.valueOf(R.string.screen_zoom_normal));
        hashMap.put(2, Integer.valueOf(R.string.screen_zoom_big));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int confirmType() {
        if (ScreenZoomUtils.getLastZoomLevel(getContext()) == this.mCurrentLevel) {
            return 0;
        }
        if (ScreenZoomUtils.isBiggerMode(getContext(), this.mCurrentLevel)) {
            return 1;
        }
        return ScreenZoomUtils.isSmallerMode(getContext(), this.mCurrentLevel) ? 2 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void effectZoomLevel() {
        getActivity().onBackPressed();
        ScreenZoomUtils.setZoomLevel(getContext(), this.mCurrentLevel);
    }

    private void initTextSize() {
        float pageLayoutTextSize = getPageLayoutTextSize();
        float pageLayoutTitleTextSize = getPageLayoutTitleTextSize();
        float pageLayoutSummaryTextSize = getPageLayoutSummaryTextSize();
        HashMap<Integer, Float> hashMap = PAGE_LAYOUT_SIZE;
        hashMap.put(0, Float.valueOf(pageLayoutTextSize * 0.8f));
        hashMap.put(1, Float.valueOf(pageLayoutTextSize * 1.0f));
        hashMap.put(2, Float.valueOf(pageLayoutTextSize * 1.05f));
        HashMap<Integer, Float> hashMap2 = CALL_TITLE_SIZE;
        hashMap2.put(0, Float.valueOf(pageLayoutTitleTextSize * 0.8f));
        hashMap2.put(1, Float.valueOf(pageLayoutTitleTextSize * 1.0f));
        hashMap2.put(2, Float.valueOf(pageLayoutTitleTextSize * 1.05f));
        HashMap<Integer, Float> hashMap3 = SUB_TITLE_SIZE;
        hashMap3.put(0, Float.valueOf(0.8f * pageLayoutSummaryTextSize));
        hashMap3.put(1, Float.valueOf(1.0f * pageLayoutSummaryTextSize));
        hashMap3.put(2, Float.valueOf(pageLayoutSummaryTextSize * 1.05f));
    }

    private void relayoutItems() {
        int intValue = this.ICON_SIZES.get(Integer.valueOf(this.mCurrentLevel)).intValue();
        HashMap<Integer, Float> hashMap = SUB_TITLE_SIZE;
        updateIconAndTexts(intValue, hashMap.get(Integer.valueOf(this.mCurrentLevel)).floatValue());
        View view = (TextView) this.mRootView.findViewById(R.id.actionbar_title);
        HashMap<Integer, Float> hashMap2 = PAGE_LAYOUT_SIZE;
        setAllTextSize(view, hashMap2.get(Integer.valueOf(this.mCurrentLevel)).floatValue());
        TextView textView = (TextView) this.mRootView.findViewById(R.id.font_hint_view);
        textView.setText(SCREEN_ZOOM_HINT.get(Integer.valueOf(this.mCurrentLevel)).intValue());
        setAllTextSize(textView, hashMap2.get(Integer.valueOf(this.mCurrentLevel)).floatValue());
        float floatValue = hashMap.get(Integer.valueOf(this.mCurrentLevel)).floatValue();
        ColorStateList colorStateList = getActivity().getResources().getColorStateList(R.color.transparent_40_balck);
        TextView textView2 = (TextView) this.mRootView.findViewById(R.id.icon_title);
        setAllTextSize(textView2, floatValue);
        textView2.setTextColor(colorStateList);
        TextView textView3 = (TextView) this.mRootView.findViewById(R.id.words_title);
        setAllTextSize(textView3, floatValue);
        textView3.setTextColor(colorStateList);
        for (int i : PageLayoutBaseFragment.PAGE_LAYOUT_CONTACT_PAGE_IDS) {
            View findViewById = this.mRootView.findViewById(i);
            View view2 = (TextView) findViewById.findViewById(R.id.call_title);
            if (view2 != null) {
                setAllTextSize(view2, CALL_TITLE_SIZE.get(Integer.valueOf(this.mCurrentLevel)).floatValue());
            }
            View view3 = (TextView) findViewById.findViewById(R.id.call_detail);
            if (view3 != null) {
                setAllTextSize(view3, SUB_TITLE_SIZE.get(Integer.valueOf(this.mCurrentLevel)).floatValue());
            }
        }
    }

    private void setAllTextSize(View view, float f) {
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(0, f);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setAllTextSize(viewGroup.getChildAt(i), f);
            }
        }
    }

    private void updateIconAndTexts(int i, float f) {
        for (int i2 = 0; i2 < 1; i2++) {
            LinearLayout linearLayout = (LinearLayout) this.mIconGrandParentView.getChildAt(i2);
            for (int i3 = 0; i3 < 4; i3++) {
                LinearLayout linearLayout2 = (LinearLayout) linearLayout.getChildAt(i3);
                ImageView imageView = (ImageView) linearLayout2.findViewById(R.id.icon);
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = i;
                layoutParams.height = i;
                int i4 = (i2 * 4) + i3;
                imageView.setImageDrawable(getResources().getDrawable(ICONS[i4]));
                TextView textView = (TextView) linearLayout2.findViewById(R.id.text);
                textView.setText(getString(ICON_NAMES[i4]));
                textView.setTextSize(0, f);
            }
        }
    }

    @Override // com.android.settings.display.PageLayoutBaseFragment
    protected void initActionBarMenu() {
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        if (appCompatActionBar == null) {
            return;
        }
        appCompatActionBar.setDisplayOptions(16, 16);
        appCompatActionBar.setCustomView(R.layout.miuix_appcompat_edit_mode_title);
        View customView = appCompatActionBar.getCustomView();
        ((TextView) customView.findViewById(16908310)).setText(getString(R.string.screen_zoom_title));
        TextView textView = (TextView) customView.findViewById(16908313);
        textView.setBackgroundResource(R.drawable.action_mode_title_button_cancel);
        textView.setText((CharSequence) null);
        textView.setContentDescription(getText(17039360));
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.ScreenZoomFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ScreenZoomFragment.this.getActivity().onBackPressed();
            }
        });
        TextView textView2 = (TextView) customView.findViewById(16908314);
        textView2.setBackgroundResource(R.drawable.action_mode_title_button_confirm);
        textView2.setText((CharSequence) null);
        textView2.setContentDescription(getText(17039370));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.ScreenZoomFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int confirmType = ScreenZoomFragment.this.confirmType();
                if (confirmType == 0) {
                    ScreenZoomFragment.this.effectZoomLevel();
                    return;
                }
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setCurrentUiMode(ScreenZoomFragment.this.mCurrentLevel);
                confirmDialog.setType(confirmType);
                confirmDialog.show(ScreenZoomFragment.this.getFragmentManager(), ConfirmDialog.FRAG_TAG);
            }
        });
    }

    @Override // com.android.settings.display.PageLayoutBaseFragment
    protected void initUI(View view) {
        View view2 = this.mRootView;
        if (view2 == null) {
            return;
        }
        int i = 0;
        view2.setSystemUiVisibility(0);
        this.mIconGrandParentView = (LinearLayout) this.mRootView.findViewById(R.id.icon_grand_parent);
        FontSizeAdjustView fontSizeAdjustView = (FontSizeAdjustView) this.mRootView.findViewById(R.id.font_view);
        this.mAdjustView = fontSizeAdjustView;
        fontSizeAdjustView.setPointCount(3);
        this.mAdjustView.setFontSizeChangeListener(this);
        this.mAdjustView.setCurrentPointIndex(this.mCurrentLevel);
        while (true) {
            int[] iArr = PageLayoutBaseFragment.PAGE_LAYOUT_CONTACT_PAGE_IDS;
            if (i >= iArr.length) {
                relayoutItems();
                return;
            }
            View findViewById = this.mRootView.findViewById(iArr[i]);
            TextView textView = (TextView) findViewById.findViewById(R.id.call_title);
            textView.setText(PageLayoutBaseFragment.CALL_RECORDS_NAME_IDS[i]);
            textView.setTextColor(getActivity().getResources().getColorStateList(R.color.transparent_80_balck));
            TextView textView2 = (TextView) findViewById.findViewById(R.id.call_detail);
            textView2.setText(PageLayoutBaseFragment.CALL_RECORDS_SUMMARY_IDS[i]);
            textView2.setTextColor(getActivity().getResources().getColorStateList(R.color.transparent_60_balck));
            i++;
        }
    }

    @Override // com.android.settings.display.PageLayoutBaseFragment, com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mCurrentLevel = bundle.getInt("key_current_zoom_level");
        } else {
            this.mCurrentLevel = ScreenZoomUtils.getLastZoomLevel(getContext());
        }
        initTextSize();
        if (this.ICON_SIZES.size() == 0) {
            float dimensionPixelSize = getContext().getResources().getConfiguration().orientation == 1 ? getResources().getDimensionPixelSize(R.dimen.font_settings_zoom_icon_size_port) : getResources().getDimensionPixelSize(R.dimen.font_settings_zoom_icon_size_land);
            this.ICON_SIZES.put(0, Integer.valueOf((int) (0.8f * dimensionPixelSize)));
            this.ICON_SIZES.put(1, Integer.valueOf((int) (1.0f * dimensionPixelSize)));
            this.ICON_SIZES.put(2, Integer.valueOf((int) (dimensionPixelSize * 1.05f)));
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setCustomView((View) null);
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onInflateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.screen_zoom_fragment, (ViewGroup) null);
        PreferenceFrameLayout.LayoutParams layoutParams = new PreferenceFrameLayout.LayoutParams(-1, -1);
        layoutParams.removeBorders = true;
        this.mRootView = inflate;
        inflate.setLayoutParams(layoutParams);
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            bundle.putInt("key_current_zoom_level", this.mCurrentLevel);
        }
    }

    @Override // com.android.settings.display.FontSizeAdjustView.FontSizeChangeListener
    public void onSizeChange(int i) {
        this.mCurrentLevel = i;
        relayoutItems();
    }
}
