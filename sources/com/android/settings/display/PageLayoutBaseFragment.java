package com.android.settings.display;

import android.os.Bundle;
import android.view.View;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import com.android.settings.display.FontSizeAdjustView;
import com.android.settings.display.FontWeightAdjustView;
import java.util.HashMap;

/* loaded from: classes.dex */
public abstract class PageLayoutBaseFragment extends BaseFragment implements FontSizeAdjustView.FontSizeChangeListener, FontWeightAdjustView.FontWeightChangeListener {
    protected static final int[] CALL_RECORDS_NAME_IDS;
    protected static final int[] CALL_RECORDS_SUMMARY_IDS;
    protected static final int[] PAGE_LAYOUT_CONTACT_PAGE_IDS;
    protected static final HashMap<Integer, Integer> PAGE_LAYOUT_SIZE;
    protected static final HashMap<Integer, Integer> PAGE_LAYOUT_SUMMARY_SIZE;
    protected static final HashMap<Integer, Integer> PAGE_LAYOUT_TITLE_SIZE;
    protected FontSizeAdjustView mAdjustView;
    protected int mCurrentLevel;
    protected View mRootView;

    static {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        PAGE_LAYOUT_TITLE_SIZE = hashMap;
        HashMap<Integer, Integer> hashMap2 = new HashMap<>();
        PAGE_LAYOUT_SUMMARY_SIZE = hashMap2;
        HashMap<Integer, Integer> hashMap3 = new HashMap<>();
        PAGE_LAYOUT_SIZE = hashMap3;
        PAGE_LAYOUT_CONTACT_PAGE_IDS = new int[]{R.id.item_page_2_1, R.id.item_page_2_2, R.id.item_page_2_3};
        CALL_RECORDS_NAME_IDS = new int[]{R.string.call_records_name_1, R.string.call_records_name_2, R.string.call_records_name_3};
        CALL_RECORDS_SUMMARY_IDS = new int[]{R.string.call_records_summary_1, R.string.call_records_summary_2, R.string.call_records_summary_3};
        hashMap3.put(10, Integer.valueOf(R.dimen.page_layout_extral_small_size));
        hashMap3.put(12, Integer.valueOf(R.dimen.page_layout_small_size));
        hashMap3.put(1, Integer.valueOf(R.dimen.page_layout_normal_size));
        hashMap3.put(13, Integer.valueOf(R.dimen.page_layout_medium_size));
        hashMap3.put(14, Integer.valueOf(R.dimen.page_layout_large_size));
        hashMap3.put(15, Integer.valueOf(R.dimen.page_layout_huge_size));
        hashMap3.put(11, Integer.valueOf(R.dimen.page_layout_godzilla_size));
        hashMap.put(10, Integer.valueOf(R.dimen.page_layout_extral_small_title_size));
        hashMap.put(12, Integer.valueOf(R.dimen.page_layout_small_title_size));
        hashMap.put(1, Integer.valueOf(R.dimen.page_layout_normal_title_size));
        hashMap.put(13, Integer.valueOf(R.dimen.page_layout_medium_title_size));
        hashMap.put(14, Integer.valueOf(R.dimen.page_layout_large_title_size));
        hashMap.put(15, Integer.valueOf(R.dimen.page_layout_huge_title_size));
        hashMap.put(11, Integer.valueOf(R.dimen.page_layout_godzilla_title_size));
        hashMap2.put(10, Integer.valueOf(R.dimen.page_layout_extral_small_summary_size));
        hashMap2.put(12, Integer.valueOf(R.dimen.page_layout_small_summary_size));
        hashMap2.put(1, Integer.valueOf(R.dimen.page_layout_normal_summary_size));
        hashMap2.put(13, Integer.valueOf(R.dimen.page_layout_medium_summary_size));
        hashMap2.put(14, Integer.valueOf(R.dimen.page_layout_large_summary_size));
        hashMap2.put(15, Integer.valueOf(R.dimen.page_layout_huge_summary_size));
        hashMap2.put(11, Integer.valueOf(R.dimen.page_layout_godzilla_summary_size));
    }

    protected int getCurrentUIModeType() {
        return LargeFontUtils.getCurrentUIModeType();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getPageLayoutSummaryTextSize() {
        return getResources().getDimension(PAGE_LAYOUT_SUMMARY_SIZE.get(Integer.valueOf(getCurrentUIModeType())).intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getPageLayoutTextSize() {
        return getResources().getDimension(PAGE_LAYOUT_SIZE.get(Integer.valueOf(getCurrentUIModeType())).intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getPageLayoutTitleTextSize() {
        return getResources().getDimension(PAGE_LAYOUT_TITLE_SIZE.get(Integer.valueOf(getCurrentUIModeType())).intValue());
    }

    protected abstract void initActionBarMenu();

    protected abstract void initUI(View view);

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        initUI(getView());
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initActionBarMenu();
    }

    @Override // com.android.settings.display.FontWeightAdjustView.FontWeightChangeListener
    public void onWeightChange(int i) {
    }
}
