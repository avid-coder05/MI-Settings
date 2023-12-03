package com.android.settings.display;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.MiuiConfiguration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.PreferenceFrameLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.BaseFragment;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.display.FontAdapter;
import com.android.settings.display.FontSizeAdjustView;
import com.android.settings.display.FontWeightAdjustView;
import com.android.settings.display.PageLayoutFragment;
import com.android.settings.display.font.FontModel2JsonUtils;
import com.android.settings.display.font.FontWeightUtils;
import com.android.settings.display.util.FileUtils;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.recommend.RecommendFilter;
import com.android.settings.recommend.RecommendManager;
import com.android.settings.recommend.bean.RecommendItem;
import com.android.settings.report.InternationalCompat;
import com.android.settings.stat.commonswitch.TalkbackSwitch;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.util.ToastUtil;
import com.android.settingslib.utils.ThreadUtils;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import miui.app.constants.ThemeManagerConstants;
import miui.provider.ExtraCalendarContracts;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.ActionBar;
import org.json.JSONArray;

/* loaded from: classes.dex */
public class PageLayoutFragment extends BaseFragment implements FontSizeAdjustView.FontSizeChangeListener, FontWeightAdjustView.FontWeightChangeListener, FontSizeAdjustView.RecommendListener, FontAdapter.FontSelectListener {
    private static String LOCAL_FONT_SP;
    private static int MAX_FONT_COUNT;
    private static final int MIUI_VERSION_CODE;
    public static final int[] MIUI_WGHT;
    protected static final HashMap<Integer, Integer> PAGE_LAYOUT_SIZE;
    public static final LinkedHashMap<Integer, Integer> PAGE_LAYOUT_TITLE;
    private static int RECOMMEND_HIDE;
    private static int RECOMMEND_SHOW;
    public static final String SYSTEM_FONTS_MIUI_EX_REGULAR_TTF;
    private FontAdapter fontAdapter;
    private View fontWeightLinearLayout;
    final boolean isPrimaryUser;
    private List<LocalFontModel> localFontModelList;
    private FontSizeAdjustView mAdjustView;
    private Context mContext;
    private LocalFontModel mCurrentFont;
    private String mCurrentFontId;
    protected int mCurrentLevel;
    private TextView mFontBubbleLeftTv;
    private TextView mFontBubbleRightTv;
    private TextView mFontHintTv;
    protected FontWeightAdjustView mFontWeightAdjustView;
    private FontUpdateHandler mHander;
    private boolean mLanProMiui13FontIsExists;
    private String mLastFontId;
    private int mLastFontWeight;
    private View mRecommendLayout;
    private View mRootView;
    final int myUserId;
    private List<LocalFontModel> originFontModelList;
    private RecyclerView recyclerView;
    private FontSettingsScrollView scrollViewCard;
    private HashMap<String, String> mCacheResTitle = new HashMap<>();
    private boolean isTalkbackMode = false;
    private int mLastProgress = -1;
    private SparseArray<Typeface> mTypefaceCache = new SparseArray<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.display.PageLayoutFragment$5  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass5 implements Animator.AnimatorListener {
        AnonymousClass5() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationEnd$0() {
            PageLayoutFragment.this.mRecommendLayout.setVisibility(PageLayoutFragment.RECOMMEND_HIDE);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$5$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PageLayoutFragment.AnonymousClass5.this.lambda$onAnimationEnd$0();
                }
            });
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }
    }

    /* loaded from: classes.dex */
    public static class DownloadRunnable implements Runnable {
        private WeakReference<PageLayoutFragment> mRef;

        public DownloadRunnable(PageLayoutFragment pageLayoutFragment) {
            this.mRef = new WeakReference<>(pageLayoutFragment);
        }

        @Override // java.lang.Runnable
        public void run() {
            PageLayoutFragment pageLayoutFragment;
            WeakReference<PageLayoutFragment> weakReference = this.mRef;
            if (weakReference == null || (pageLayoutFragment = weakReference.get()) == null || new File("/data/user_de/0/com.android.settings/files/fonts/Roboto-Regular.ttf").exists() || pageLayoutFragment.mContext == null) {
                return;
            }
            pageLayoutFragment.mLanProMiui13FontIsExists = FileUtils.unZip("/system/media/theme/MILanProVF.mtz", pageLayoutFragment.mContext.getFilesDir().getAbsolutePath(), "", "fonts/");
            pageLayoutFragment.getFonts(pageLayoutFragment.mContext);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FontUpdateHandler extends Handler {
        private WeakReference<PageLayoutFragment> fragmentWeakReference;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.display.PageLayoutFragment$FontUpdateHandler$1  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass1 implements ValueAnimator.AnimatorUpdateListener {
            final /* synthetic */ PageLayoutFragment val$fragment;

            AnonymousClass1(PageLayoutFragment pageLayoutFragment) {
                this.val$fragment = pageLayoutFragment;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public static /* synthetic */ void lambda$onAnimationUpdate$0(PageLayoutFragment pageLayoutFragment, ValueAnimator valueAnimator) {
                pageLayoutFragment.recyclerView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final PageLayoutFragment pageLayoutFragment = this.val$fragment;
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$FontUpdateHandler$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PageLayoutFragment.FontUpdateHandler.AnonymousClass1.lambda$onAnimationUpdate$0(PageLayoutFragment.this, valueAnimator);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.display.PageLayoutFragment$FontUpdateHandler$2  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass2 implements Animator.AnimatorListener {
            final /* synthetic */ PageLayoutFragment val$fragment;

            /* JADX INFO: Access modifiers changed from: package-private */
            /* renamed from: com.android.settings.display.PageLayoutFragment$FontUpdateHandler$2$1  reason: invalid class name */
            /* loaded from: classes.dex */
            public class AnonymousClass1 implements ValueAnimator.AnimatorUpdateListener {
                final /* synthetic */ PageLayoutFragment val$fragment;

                AnonymousClass1(PageLayoutFragment pageLayoutFragment) {
                    this.val$fragment = pageLayoutFragment;
                }

                /* JADX INFO: Access modifiers changed from: private */
                public static /* synthetic */ void lambda$onAnimationUpdate$0(PageLayoutFragment pageLayoutFragment, ValueAnimator valueAnimator) {
                    pageLayoutFragment.recyclerView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }

                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    final PageLayoutFragment pageLayoutFragment = this.val$fragment;
                    ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$FontUpdateHandler$2$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PageLayoutFragment.FontUpdateHandler.AnonymousClass2.AnonymousClass1.lambda$onAnimationUpdate$0(PageLayoutFragment.this, valueAnimator);
                        }
                    });
                }
            }

            AnonymousClass2(PageLayoutFragment pageLayoutFragment) {
                this.val$fragment = pageLayoutFragment;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public static /* synthetic */ void lambda$onAnimationCancel$1(PageLayoutFragment pageLayoutFragment) {
                pageLayoutFragment.recyclerView.setAdapter(pageLayoutFragment.fontAdapter);
                pageLayoutFragment.recyclerView.setAlpha(1.0f);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onAnimationEnd$0(PageLayoutFragment pageLayoutFragment) {
                ValueAnimator duration = ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(150L);
                duration.addUpdateListener(new AnonymousClass1(pageLayoutFragment));
                pageLayoutFragment.recyclerView.setAdapter(pageLayoutFragment.fontAdapter);
                duration.start();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                final PageLayoutFragment pageLayoutFragment = this.val$fragment;
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$FontUpdateHandler$2$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PageLayoutFragment.FontUpdateHandler.AnonymousClass2.lambda$onAnimationCancel$1(PageLayoutFragment.this);
                    }
                });
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                final PageLayoutFragment pageLayoutFragment = this.val$fragment;
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$FontUpdateHandler$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PageLayoutFragment.FontUpdateHandler.AnonymousClass2.this.lambda$onAnimationEnd$0(pageLayoutFragment);
                    }
                });
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }
        }

        public FontUpdateHandler(WeakReference<PageLayoutFragment> weakReference) {
            this.fragmentWeakReference = weakReference;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            final PageLayoutFragment pageLayoutFragment = this.fragmentWeakReference.get();
            if (pageLayoutFragment == null || message.what != 1) {
                return;
            }
            List list = (List) ((HashMap) message.obj).get("fontList");
            pageLayoutFragment.originFontModelList = list;
            if (list == null || list.size() == 0) {
                pageLayoutFragment.recyclerView.setVisibility(8);
                return;
            }
            List arrayList = new ArrayList(list.size());
            for (int i = 0; i < list.size(); i++) {
                LocalFontModel localFontModel = (LocalFontModel) list.get(i);
                if (i == 0 && localFontModel.getId().equals("10")) {
                    String string = pageLayoutFragment.mContext.getResources().getString(PageLayoutFragment.access$700());
                    Log.i("PageLayoutFragment", "handleMessage: set defaultFont" + string);
                    localFontModel.setTitle(string);
                }
                if (!TextUtils.equals(localFontModel.getId(), "b004d74e-5c49-430c-bb6a-18ed5d2d33e4") || pageLayoutFragment.mLanProMiui13FontIsExists) {
                    arrayList.add(localFontModel);
                } else {
                    Log.i("PageLayoutFragment", "handleMessage: " + localFontModel.getId() + "; " + pageLayoutFragment.mLanProMiui13FontIsExists);
                }
            }
            LocalFontModel localFontModel2 = new LocalFontModel("-1000", null, null, false);
            int i2 = 0;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                LocalFontModel localFontModel3 = (LocalFontModel) arrayList.get(i3);
                if (localFontModel3.isUsing()) {
                    pageLayoutFragment.mCurrentFontId = localFontModel3.getId();
                    pageLayoutFragment.mCurrentFont = localFontModel3;
                    pageLayoutFragment.mLastFontId = pageLayoutFragment.mCurrentFontId;
                    i2 = i3;
                }
            }
            if (pageLayoutFragment.mCurrentFont == null) {
                arrayList.clear();
                arrayList.add(new LocalFontModel("10", pageLayoutFragment.mContext.getResources().getString(PageLayoutFragment.access$700()), null, true));
                pageLayoutFragment.mCurrentFont = (LocalFontModel) arrayList.get(0);
                pageLayoutFragment.mLastFontId = "10";
            }
            if (i2 >= 2) {
                LocalFontModel localFontModel4 = (LocalFontModel) arrayList.get(i2);
                arrayList.remove(i2);
                arrayList.add(1, localFontModel4);
            }
            if (arrayList.size() > PageLayoutFragment.MAX_FONT_COUNT) {
                arrayList = arrayList.subList(0, PageLayoutFragment.MAX_FONT_COUNT);
            }
            arrayList.add(localFontModel2);
            if (pageLayoutFragment.mCurrentFont.isVariable()) {
                pageLayoutFragment.fontWeightLinearLayout.setAlpha(1.0f);
                pageLayoutFragment.mFontWeightAdjustView.setEnabled(true);
                if (pageLayoutFragment.mFontWeightAdjustView.getFontWeightChangeListener() == null) {
                    pageLayoutFragment.mFontWeightAdjustView.setFontWeightChangeListener(new FontWeightAdjustView.FontWeightChangeListener() { // from class: com.android.settings.display.PageLayoutFragment$FontUpdateHandler$$ExternalSyntheticLambda0
                        @Override // com.android.settings.display.FontWeightAdjustView.FontWeightChangeListener
                        public final void onWeightChange(int i4) {
                            PageLayoutFragment.this.onWeightChange(i4);
                        }
                    });
                }
                pageLayoutFragment.mFontWeightAdjustView.setProgress(pageLayoutFragment.mLastFontWeight);
                pageLayoutFragment.onWeightChange(pageLayoutFragment.mLastFontWeight);
            } else if (!pageLayoutFragment.mCurrentFont.getId().equals("10")) {
                if (pageLayoutFragment.isPrimaryUser) {
                    pageLayoutFragment.fontWeightLinearLayout.setAlpha(0.3f);
                }
                pageLayoutFragment.mFontWeightAdjustView.setEnabled(false);
            }
            boolean z = !pageLayoutFragment.compareOldAndNewFontList(arrayList, pageLayoutFragment.localFontModelList);
            Log.i("PageLayoutFragment", "handleMessage: needShowNewFontList " + z);
            if (z) {
                pageLayoutFragment.localFontModelList = arrayList;
                pageLayoutFragment.fontAdapter = new FontAdapter();
                pageLayoutFragment.fontAdapter.setContext(pageLayoutFragment.mContext);
                pageLayoutFragment.fontAdapter.setDataList(pageLayoutFragment.localFontModelList);
                pageLayoutFragment.fontAdapter.setCurrentFontId(pageLayoutFragment.mCurrentFontId);
                pageLayoutFragment.fontAdapter.setFontSelectListener(pageLayoutFragment);
                pageLayoutFragment.setLocalFontModelListCacahe(pageLayoutFragment.originFontModelList, pageLayoutFragment.mCurrentFontId);
                showNewFontList();
            }
            FontWeightUtils.updateVarFont();
            pageLayoutFragment.updateBubbleAndHintText();
        }

        void showNewFontList() {
            PageLayoutFragment pageLayoutFragment = this.fragmentWeakReference.get();
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.5f).setDuration(150L);
            duration.addUpdateListener(new AnonymousClass1(pageLayoutFragment));
            duration.addListener(new AnonymousClass2(pageLayoutFragment));
            duration.start();
        }
    }

    static {
        LinkedHashMap<Integer, Integer> linkedHashMap = new LinkedHashMap<>();
        PAGE_LAYOUT_TITLE = linkedHashMap;
        if (RegionUtils.IS_JP_KDDI) {
            linkedHashMap.put(10, Integer.valueOf(R.string.layout_size_small));
            linkedHashMap.put(12, Integer.valueOf(R.string.layout_size_normal));
            linkedHashMap.put(1, Integer.valueOf(R.string.layout_size_medium));
            linkedHashMap.put(14, Integer.valueOf(R.string.layout_size_large));
            linkedHashMap.put(15, Integer.valueOf(R.string.layout_size_huge));
            linkedHashMap.put(11, Integer.valueOf(R.string.layout_size_exhuge));
        } else {
            linkedHashMap.put(10, Integer.valueOf(R.string.layout_size_extral_small));
            linkedHashMap.put(12, Integer.valueOf(R.string.layout_size_small));
            linkedHashMap.put(1, Integer.valueOf(R.string.layout_size_normal));
            linkedHashMap.put(13, Integer.valueOf(R.string.layout_size_medium));
            linkedHashMap.put(14, Integer.valueOf(R.string.layout_size_large));
            linkedHashMap.put(15, Integer.valueOf(R.string.layout_size_huge));
            linkedHashMap.put(11, Integer.valueOf(R.string.layout_size_exhuge));
        }
        RECOMMEND_SHOW = 0;
        RECOMMEND_HIDE = 4;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        PAGE_LAYOUT_SIZE = hashMap;
        SYSTEM_FONTS_MIUI_EX_REGULAR_TTF = SystemProperties.get("ro.miui.ui.font.mi_font_path", "/system/fonts/MiLanProVF.ttf");
        MIUI_WGHT = new int[]{150, 200, 250, 305, 340, 400, 480, 540, 630, ExtraCalendarContracts.CALENDAR_ACCESS_LEVEL_LOCAL};
        hashMap.put(10, Integer.valueOf(R.dimen.page_layout_extral_small_size));
        hashMap.put(12, Integer.valueOf(R.dimen.page_layout_small_size));
        hashMap.put(1, Integer.valueOf(R.dimen.page_layout_normal_size));
        hashMap.put(13, Integer.valueOf(R.dimen.page_layout_medium_size));
        hashMap.put(14, Integer.valueOf(R.dimen.page_layout_large_size));
        hashMap.put(15, Integer.valueOf(R.dimen.page_layout_huge_size));
        hashMap.put(11, Integer.valueOf(R.dimen.page_layout_godzilla_size));
        MAX_FONT_COUNT = 9;
        MIUI_VERSION_CODE = SystemProperties.getInt("ro.miui.ui.version.code", 0);
        LOCAL_FONT_SP = "LOCAL_FONT_SP";
    }

    public PageLayoutFragment() {
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        this.mLanProMiui13FontIsExists = true;
        this.mHander = new FontUpdateHandler(new WeakReference(this));
    }

    static /* synthetic */ int access$700() {
        return getFontTitle();
    }

    private void addChildViewForRecommendLayout(List<RecommendItem> list) {
        LinearLayout linearLayout = (LinearLayout) this.mRootView.findViewById(R.id.line_layout);
        linearLayout.removeViews(1, linearLayout.getChildCount() - 1);
        for (int i = 0; i < list.size(); i++) {
            RecommendItem recommendItem = list.get(i);
            if (!TextUtils.isEmpty(recommendItem.getTargetPageTitle())) {
                String targetPageTitle = recommendItem.getTargetPageTitle();
                if (this.mCacheResTitle.containsKey(targetPageTitle)) {
                    try {
                        linearLayout.addView(addRecommendView(this.mCacheResTitle.get(targetPageTitle), Intent.parseUri(recommendItem.getIntent(), 0)));
                    } catch (URISyntaxException unused) {
                        Log.d("PageLayoutFragment", "tryBuildRecommendLayout: Uri parse fail or recommendLayout addVew fail");
                    }
                } else {
                    String string = this.mContext.getResources().getString(this.mContext.getResources().getIdentifier(recommendItem.getTargetPageTitle(), "string", this.mContext.getPackageName()));
                    try {
                        linearLayout.addView(addRecommendView(string, Intent.parseUri(recommendItem.getIntent(), 0)));
                        this.mCacheResTitle.put(recommendItem.getTargetPageTitle(), string);
                    } catch (URISyntaxException unused2) {
                        Log.d("PageLayoutFragment", "tryBuildRecommendLayout: Uri parse fail or recommendLayout addVew fail");
                    }
                }
            }
        }
    }

    private boolean adjustCurrentLevelIfNeed() {
        Iterator<Integer> it = PAGE_LAYOUT_SIZE.keySet().iterator();
        while (it.hasNext()) {
            if (this.mCurrentLevel == it.next().intValue()) {
                return false;
            }
        }
        return true;
    }

    private boolean compareOldAndNewFont(LocalFontModel localFontModel, LocalFontModel localFontModel2) {
        if (localFontModel == null || localFontModel.getId() == null || localFontModel.getContentUri() == null || localFontModel.getTitle() == null) {
            return true;
        }
        if (localFontModel.getId().equals(localFontModel2.getId()) && localFontModel.getTitle().equals(localFontModel2.getTitle()) && localFontModel.getContentUri().equals(localFontModel2.getContentUri()) && localFontModel.isUsing() == localFontModel2.isUsing() && localFontModel.isVariable() == localFontModel2.isVariable()) {
            return localFontModel.getFontWeight() == null && localFontModel2.getFontWeight() == null;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean compareOldAndNewFontList(List<LocalFontModel> list, List<LocalFontModel> list2) {
        if (list == null || list2 == null || list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!compareOldAndNewFont(list.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private void completeHintText(TextView textView, int i, int i2) {
        if (textView == null || getContext() == null) {
            return;
        }
        Resources resources = getContext().getResources();
        StringBuilder sb = new StringBuilder();
        sb.append(resources.getString(PAGE_LAYOUT_TITLE.get(Integer.valueOf(i)).intValue()));
        if (i2 != 50) {
            int i3 = i2 < 50 ? R.string.weight_light : R.string.weight_heavy;
            sb.append(" ");
            sb.append(resources.getString(i3));
        }
        textView.setText(sb.toString());
    }

    private void deleteRecommendFile() {
        File file = new File(this.mContext.getFilesDir().getAbsolutePath() + "/recommend.json");
        if (file.exists()) {
            file.delete();
        }
    }

    private String getCurrentFontId() {
        return this.mContext.getSharedPreferences(LOCAL_FONT_SP, 0).getString("current_font_id", "10");
    }

    public static List<LocalFontModel> getFontList(Context context) {
        ArrayList arrayList = new ArrayList();
        Bundle call = context.getContentResolver().call(Uri.parse("content://com.android.thememanager.theme_provider"), "getFonts", (String) null, (Bundle) null);
        if (call == null) {
            return arrayList;
        }
        String string = call.getString("result");
        Log.i("PageLayoutFragment", "getFonts json:" + string);
        return string == null ? arrayList : getFontsResult(string);
    }

    private static int getFontTitle() {
        return MIUI_VERSION_CODE >= 13 ? R.string.MiSans_title : R.string.xiaomi_lanting_title;
    }

    private static List<LocalFontModel> getFontsResult(String str) {
        if (str == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    arrayList.add(FontModel2JsonUtils.Json2LocalFont(jSONArray.getJSONObject(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return arrayList;
    }

    private List<LocalFontModel> getLocalFontModelListCacahe() {
        return getFontsResult(this.mContext.getSharedPreferences(LOCAL_FONT_SP, 0).getString("local_font_list", null));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyFont$3(boolean[] zArr, Context context) {
        if (!zArr[0]) {
            ToastUtil.show(context, R.string.toast_apply_font_fail, 1);
            return;
        }
        String str = this.mCurrentFontId;
        this.mLastFontId = str;
        setLocalFontModelListCacahe(this.originFontModelList, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$applyFont$4(Exception exc, Context context) {
        exc.printStackTrace();
        ToastUtil.show(context, R.string.toast_apply_font_fail, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyFont$5(String str, final Context context) {
        try {
            final boolean[] zArr = {false};
            Bundle bundle = new Bundle();
            bundle.putString("fontId", str);
            try {
                zArr[0] = context.getContentResolver().call(Uri.parse("content://com.android.thememanager.theme_provider"), "applyFont", (String) null, bundle).getBoolean("applyResult");
            } catch (Exception e) {
                zArr[0] = false;
                e.printStackTrace();
            }
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    PageLayoutFragment.this.lambda$applyFont$3(zArr, context);
                }
            });
        } catch (Exception e2) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    PageLayoutFragment.lambda$applyFont$4(e2, context);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getFonts$2(Context context) {
        try {
            List<LocalFontModel> fontList = getFontList(context);
            if (fontList.isEmpty()) {
                return;
            }
            Message message = new Message();
            message.what = 1;
            HashMap hashMap = new HashMap();
            hashMap.put("fontList", fontList);
            message.obj = hashMap;
            this.mHander.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tryBuildRecommendLayout$0(List list) {
        addChildViewForRecommendLayout(list);
        View view = this.mRecommendLayout;
        if (view != null) {
            setAllTextByCustomSize((ViewGroup) view, 1);
            LinearLayout linearLayout = (LinearLayout) this.mRecommendLayout.findViewById(R.id.line_layout);
            Folme.useAt(linearLayout).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setTint(0.08f, 0.0f, 0.0f, 0.0f).setTintMode(0).handleTouchOf(linearLayout, new AnimConfig[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tryBuildRecommendLayout$1() {
        RecommendManager recommendManager = RecommendManager.getInstance(this.mContext);
        recommendManager.loadRecommendList();
        if (!RecommendManager.isLoadComplete()) {
            Log.e("PageLayoutFragment", "recommend items not load complete.");
            return;
        }
        RecommendFilter recommendFilter = new RecommendFilter();
        int pageIndex = getPageIndex();
        final List<RecommendItem> listByPageIndex = recommendFilter.getListByPageIndex(this.mContext, pageIndex);
        if (listByPageIndex == null) {
            deleteRecommendFile();
            recommendManager.loadRecommendList();
            if (RecommendManager.isLoadComplete() && (listByPageIndex = recommendFilter.getListByPageIndex(this.mContext, pageIndex)) == null) {
                return;
            }
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PageLayoutFragment.this.lambda$tryBuildRecommendLayout$0(listByPageIndex);
            }
        });
    }

    private boolean notNeedCache(int i) {
        LocalFontModel localFontModel;
        return i == 50 && (localFontModel = this.mCurrentFont) != null && TextUtils.equals(localFontModel.getId(), "b004d74e-5c49-430c-bb6a-18ed5d2d33e4");
    }

    private void notifyFontWeightChanged() {
        Bundle bundle = new Bundle();
        bundle.putInt("key_var_font_scale", LargeFontUtils.getFontWeight(getContext()));
        MiuiConfiguration.sendThemeConfigurationChangeMsg(536870912L, bundle);
    }

    private void relayoutItems() {
        TextView textView = (TextView) this.mRootView.findViewById(R.id.font_hint_view);
        completeHintText(textView, this.mCurrentLevel, LargeFontUtils.getFontWeight(getContext()));
        Resources resources = getResources();
        HashMap<Integer, Integer> hashMap = PAGE_LAYOUT_SIZE;
        setAllTextSize(textView, resources.getDimension(hashMap.get(Integer.valueOf(this.mCurrentLevel)).intValue()));
        View view = (TextView) this.mRootView.findViewById(R.id.font_bubble_right);
        View view2 = (TextView) this.mRootView.findViewById(R.id.font_bubble_left);
        setAllTextSize(view, getResources().getDimension(hashMap.get(Integer.valueOf(this.mCurrentLevel)).intValue()));
        setAllTextSize(view2, getResources().getDimension(hashMap.get(Integer.valueOf(this.mCurrentLevel)).intValue()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAllTextByCustomSize(View view, int i) {
        if (view instanceof TextView) {
            try {
                ((TextView) view).setTextSize(0, getResources().getDimension(PAGE_LAYOUT_SIZE.get(Integer.valueOf(i)).intValue()) * getCurrentZoomRadio());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                setAllTextByCustomSize(viewGroup.getChildAt(i2), i);
            }
        }
    }

    private void setAllTextSize(View view, float f) {
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(0, f * getCurrentZoomRadio());
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setAllTextSize(viewGroup.getChildAt(i), getCurrentZoomRadio() * f);
            }
        }
    }

    private void setLocalFontModelListCacahe(String str, String str2) {
        SharedPreferences.Editor edit = this.mContext.getSharedPreferences(LOCAL_FONT_SP, 0).edit();
        edit.putString("local_font_list", str);
        edit.putString("current_font_id", str2);
        edit.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLocalFontModelListCacahe(List<LocalFontModel> list, String str) {
        JSONArray jSONArray = new JSONArray();
        int size = list.size();
        int i = MAX_FONT_COUNT;
        if (size > i) {
            list = list.subList(0, i);
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            jSONArray.put(FontModel2JsonUtils.LocalFont2Json(list.get(i2)));
        }
        setLocalFontModelListCacahe(jSONArray.toString(), str);
    }

    private void setTextViewFont(TextView textView, int i) {
        if (this.mContext == null) {
            return;
        }
        Typeface typeface = null;
        int scaleWght = FontWeightUtils.getScaleWght(this.mContext, 5, textView.getTextSize(), 0);
        LocalFontModel localFontModel = this.mCurrentFont;
        if (localFontModel == null || !localFontModel.isVariable() || this.mCurrentFont.getId().equals("10") || this.mCurrentFont.getFontWeight() == null || this.mCurrentFont.getFontWeight().size() < 1) {
            String str = this.mCurrentFontId;
            if (str != null && str.equals("10") && (typeface = this.mTypefaceCache.get(scaleWght)) == null) {
                typeface = FontWeightUtils.getVarTypeface(scaleWght, 0);
            }
        } else {
            int intValue = this.mCurrentFont.getFontWeight().get(this.mCurrentFont.getFontWeight().size() - 1).intValue();
            scaleWght = ((int) ((i / 100.0f) * (intValue - r1))) + this.mCurrentFont.getFontWeight().get(0).intValue();
            typeface = this.mTypefaceCache.get(scaleWght);
            if (typeface == null) {
                typeface = FontAdapter.getVarTypeface(this.mCurrentFont, scaleWght);
            }
        }
        if (!notNeedCache(i)) {
            this.mTypefaceCache.put(scaleWght, typeface);
        }
        textView.setTypeface(typeface);
    }

    private static void setUiMode(Activity activity, int i) {
        if (activity != null) {
            LargeFontUtils.sendUiModeChangeMessage(activity.getApplicationContext(), i);
            if (i != 11 || i == LargeFontUtils.getCurrentUIModeType()) {
                return;
            }
            ToastUtil.show(activity, activity.getResources().getString(R.string.layout_exhuge_font_take_effect_tips), 0);
        }
    }

    private void tryBuildRecommendLayout() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PageLayoutFragment.this.lambda$tryBuildRecommendLayout$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBubbleAndHintText() {
        LocalFontModel localFontModel = this.mCurrentFont;
        if (localFontModel == null || this.fontAdapter == null) {
            return;
        }
        if (localFontModel.isVariable() || this.mCurrentFont.getId().equals("10")) {
            setTextViewFont(this.mFontHintTv, this.mLastFontWeight);
            setTextViewFont(this.mFontBubbleLeftTv, this.mLastFontWeight);
            setTextViewFont(this.mFontBubbleRightTv, this.mLastFontWeight);
            return;
        }
        this.fontAdapter.setFontFamily(this.mFontHintTv, this.mCurrentFont);
        this.fontAdapter.setFontFamily(this.mFontBubbleLeftTv, this.mCurrentFont);
        this.fontAdapter.setFontFamily(this.mFontBubbleLeftTv, this.mCurrentFont);
    }

    private void updateLocalFontModelListCache(List<LocalFontModel> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (LocalFontModel localFontModel : list) {
            if (localFontModel.getId().equals("10")) {
                String string = this.mContext.getResources().getString(getFontTitle());
                Log.i("PageLayoutFragment", "updateLocalFontModelListCache: " + string);
                localFontModel.setTitle(string);
            }
        }
    }

    public RelativeLayout addRecommendView(CharSequence charSequence, final Intent intent) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(this.mContext).inflate(R.layout.font_recommend_item, (ViewGroup) null);
        TextView textView = (TextView) relativeLayout.findViewById(R.id.item_view);
        textView.setText(charSequence);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.PageLayoutFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PageLayoutFragment.this.mContext.startActivity(intent);
            }
        });
        Folme.useAt(textView).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setAlpha(0.6f, new ITouchStyle.TouchType[0]).handleTouchOf(textView, new AnimConfig[0]);
        return relativeLayout;
    }

    public void applyFont(final Context context, final String str) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                PageLayoutFragment.this.lambda$applyFont$5(str, context);
            }
        });
    }

    @Override // com.android.settings.display.FontAdapter.FontSelectListener
    public void fontSelected(int i, boolean z) {
        if (z) {
            Intent intent = new Intent();
            if (SettingsFeatures.isFoldDevice() || SettingsFeatures.isSplitTabletDevice()) {
                intent.setAction("com.setting.pad.font");
                intent.setPackage("com.android.thememanager");
            } else {
                intent.setClassName("com.android.thememanager", "com.android.thememanager.ThemeResourceTabActivity");
                intent.putExtra("EXTRA_TAB_ID", ThemeManagerConstants.COMPONENT_CODE_FONT);
            }
            if (MiuiUtils.canFindActivityStatic(getContext(), intent)) {
                startActivity(intent);
                return;
            }
            return;
        }
        LocalFontModel localFontModel = this.localFontModelList.get(i);
        this.mCurrentFontId = localFontModel.getId();
        this.mCurrentFont = localFontModel;
        this.fontAdapter.setFontFamily(this.mFontBubbleLeftTv, localFontModel);
        this.fontAdapter.setFontFamily(this.mFontBubbleRightTv, localFontModel);
        if (!this.isPrimaryUser || (!this.mCurrentFontId.equals("10") && !this.mCurrentFont.isVariable())) {
            if (this.isPrimaryUser) {
                this.fontWeightLinearLayout.setAlpha(0.3f);
            }
            this.mFontWeightAdjustView.setProgress(50);
            this.mFontWeightAdjustView.setEnabled(false);
            return;
        }
        if (this.mCurrentFont.isVariable()) {
            FontWeightUtils.updateVarFont();
        }
        this.fontWeightLinearLayout.setAlpha(1.0f);
        this.mFontWeightAdjustView.setEnabled(true);
        this.mFontWeightAdjustView.setFontWeightChangeListener(this);
        this.mFontWeightAdjustView.setProgress(50);
        LargeFontUtils.setFontWeight(this.mContext, 50);
        this.mTypefaceCache.clear();
    }

    protected float getCurrentZoomRadio() {
        return getZoomRadioByLevel(ScreenZoomUtils.getLastZoomLevel(getContext()));
    }

    public void getFonts(final Context context) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.display.PageLayoutFragment$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PageLayoutFragment.this.lambda$getFonts$2(context);
            }
        });
    }

    public int getPageIndex() {
        return PageIndexManager.PAGE_FONT_SIZE_WEIGHT_SETTINGS;
    }

    protected float getZoomRadioByLevel(int i) {
        if (i != 0) {
            if (i != 1) {
                return i != 2 ? 0.0f : 1.05f;
            }
            return 1.0f;
        }
        return 0.8f;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.recyclerView.setHasFixedSize(false);
        FontAdapter fontAdapter = new FontAdapter();
        this.fontAdapter = fontAdapter;
        fontAdapter.setContext(this.mContext);
        this.fontAdapter.setFontSelectListener(new FontAdapter.FontSelectListener() { // from class: com.android.settings.display.PageLayoutFragment$$ExternalSyntheticLambda0
            @Override // com.android.settings.display.FontAdapter.FontSelectListener
            public final void fontSelected(int i, boolean z) {
                PageLayoutFragment.this.fontSelected(i, z);
            }
        });
        this.fontAdapter.setDataList(this.localFontModelList);
        this.fontAdapter.setCurrentFontId(this.mCurrentFontId);
        this.recyclerView.setAdapter(this.fontAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext, 0, false));
        this.mRecommendLayout = this.mRootView.findViewById(R.id.recommend_layout);
        tryBuildRecommendLayout();
        if (this.isPrimaryUser) {
            getFonts(this.mContext);
        } else {
            this.mFontWeightAdjustView.setEnabled(false);
        }
        LinearLayout linearLayout = (LinearLayout) this.mRootView.findViewById(R.id.font_bubble_right_layout);
        LinearLayout linearLayout2 = (LinearLayout) this.mRootView.findViewById(R.id.font_bubble_left_layout);
        boolean isTalkbackEnable = TalkbackSwitch.isTalkbackEnable(this.mContext);
        this.isTalkbackMode = isTalkbackEnable;
        if (!isTalkbackEnable) {
            Folme.useAt(linearLayout).touch().setScale(0.9f, new ITouchStyle.TouchType[0]).handleTouchOf(linearLayout, new AnimConfig[0]);
            Folme.useAt(linearLayout2).touch().setScale(0.9f, new ITouchStyle.TouchType[0]).setTint(0.08f, 0.0f, 0.0f, 0.0f).setTintMode(0).handleTouchOf(linearLayout2, new AnimConfig[0]);
        }
        this.mFontHintTv = (TextView) this.mRootView.findViewById(R.id.font_hint_view);
        this.mFontBubbleRightTv = (TextView) this.mRootView.findViewById(R.id.font_bubble_right);
        this.mFontBubbleLeftTv = (TextView) this.mRootView.findViewById(R.id.font_bubble_left);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
        ThreadUtils.postOnBackgroundThread(new DownloadRunnable(this));
        LargeFontUtils.getVariableFontChange(this.mContext);
        this.mCurrentLevel = LargeFontUtils.getCurrentUIModeType();
        Log.w("PageLayoutFragment", "mCurrentLevel:" + this.mCurrentLevel);
        if (adjustCurrentLevelIfNeed()) {
            this.mCurrentLevel = 1;
        }
        this.mLastFontWeight = LargeFontUtils.getFontWeight(this.mContext);
        if (this.isPrimaryUser) {
            List<LocalFontModel> localFontModelListCacahe = getLocalFontModelListCacahe();
            updateLocalFontModelListCache(localFontModelListCacahe);
            String currentFontId = getCurrentFontId();
            this.mCurrentFontId = currentFontId;
            this.mLastFontId = currentFontId;
            if (localFontModelListCacahe == null) {
                localFontModelListCacahe = new ArrayList<>();
                Context context = this.mContext;
                if (context != null && context.getResources() != null) {
                    localFontModelListCacahe.add(new LocalFontModel("10", getResources().getString(getFontTitle()), null, false));
                }
            }
            this.localFontModelList = localFontModelListCacahe;
            int i = 0;
            for (int i2 = 0; i2 < this.localFontModelList.size(); i2++) {
                if (this.localFontModelList.get(i2).getId().equals(this.mCurrentFontId)) {
                    this.mCurrentFontId = this.localFontModelList.get(i2).getId();
                    this.mCurrentFont = this.localFontModelList.get(i2);
                    this.localFontModelList.get(i2).setUsing(true);
                    i = i2;
                } else {
                    this.localFontModelList.get(i2).setUsing(false);
                }
            }
            if (i >= 2) {
                LocalFontModel localFontModel = this.localFontModelList.get(i);
                this.localFontModelList.remove(i);
                this.localFontModelList.add(1, localFontModel);
            }
        } else {
            this.localFontModelList = new ArrayList();
            Context context2 = this.mContext;
            if (context2 != null && context2.getResources() != null) {
                LocalFontModel localFontModel2 = new LocalFontModel("10", this.mContext.getResources().getString(getFontTitle()), null, true);
                this.mCurrentFontId = "10";
                this.mLastFontId = "10";
                this.localFontModelList.add(localFontModel2);
                this.mCurrentFont = localFontModel2;
            }
        }
        if (this.localFontModelList.size() > MAX_FONT_COUNT) {
            Log.d("PageLayoutFragment", "the size of the current font list: " + this.localFontModelList.size());
            this.localFontModelList = this.localFontModelList.subList(0, MAX_FONT_COUNT);
        }
        this.localFontModelList.add(new LocalFontModel("-1000", null, null, false));
        InternationalCompat.trackReportEvent("setting_font_click_size");
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mHander.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R.layout.font_settings_fragment, (ViewGroup) null);
        ViewGroup.LayoutParams layoutParams = new PreferenceFrameLayout.LayoutParams(-1, -1);
        ((PreferenceFrameLayout.LayoutParams) layoutParams).removeBorders = true;
        this.mRootView.setLayoutParams(layoutParams);
        return this.mRootView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mContext == null) {
            this.mContext = getContext();
        }
        relayoutItems();
        if (this.mCurrentLevel == 11 || this.isTalkbackMode) {
            this.mRecommendLayout.setVisibility(RECOMMEND_SHOW);
            this.scrollViewCard.setCanScroll(true);
        } else {
            this.mRecommendLayout.setVisibility(RECOMMEND_HIDE);
            this.scrollViewCard.fullScroll(33);
            this.scrollViewCard.setCanScroll(false);
        }
        this.fontAdapter.setCurrentFontId(this.mCurrentFontId);
    }

    @Override // com.android.settings.display.FontSizeAdjustView.FontSizeChangeListener
    public void onSizeChange(int i) {
        HashMap hashMap = new HashMap();
        hashMap.put("fontSize", Integer.valueOf(i));
        InternationalCompat.trackReportObjectEvent("setting_Display_fontsize", hashMap);
        if (i == 0) {
            this.mCurrentLevel = 10;
        } else if (i == 1) {
            this.mCurrentLevel = 12;
        } else if (i == 2) {
            this.mCurrentLevel = 1;
        } else if (i == 3) {
            this.mCurrentLevel = 13;
        } else if (i == 4) {
            this.mCurrentLevel = 14;
        } else if (i == 5) {
            this.mCurrentLevel = 15;
        } else if (i != 6) {
            return;
        } else {
            this.mCurrentLevel = 11;
        }
        relayoutItems();
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.title_font_settings);
            try {
                appCompatActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.font_settings_bg_color)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        String str = this.mLastFontId;
        if (str != null && !str.equals(this.mCurrentFontId)) {
            applyFont(this.mContext, this.mCurrentFontId);
        }
        setUiMode(getActivity(), this.mCurrentLevel);
        if (this.mLastFontWeight != LargeFontUtils.getFontWeight(getContext())) {
            notifyFontWeightChanged();
        }
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        LocalFontModel localFontModel;
        super.onViewCreated(view, bundle);
        this.mFontWeightAdjustView = (FontWeightAdjustView) this.mRootView.findViewById(R.id.fontweight_view);
        this.fontWeightLinearLayout = this.mRootView.findViewById(R.id.ll_font_weight);
        int i = 0;
        if (LargeFontUtils.isSupportVarintFont() || (localFontModel = this.mCurrentFont) == null || localFontModel.isVariable()) {
            this.fontWeightLinearLayout.setAlpha(1.0f);
            this.mFontWeightAdjustView.setEnabled(true);
        } else {
            if (this.isPrimaryUser) {
                this.fontWeightLinearLayout.setAlpha(0.3f);
            }
            this.mFontWeightAdjustView.setEnabled(false);
        }
        this.mFontWeightAdjustView.setFontWeightChangeListener(this);
        this.recyclerView = (RecyclerView) this.mRootView.findViewById(R.id.font_recycler_view);
        ((TextView) this.mRootView.findViewById(R.id.font_bubble_left)).setText(getResources().getString(R.string.adjust_font_size_answer_cn));
        FontSizeAdjustView fontSizeAdjustView = (FontSizeAdjustView) this.mRootView.findViewById(R.id.font_view);
        this.mAdjustView = fontSizeAdjustView;
        fontSizeAdjustView.setFontSizeChangeListener(this);
        int i2 = this.mCurrentLevel;
        if (i2 == 1) {
            i = 2;
        } else if (i2 == 11) {
            i = 6;
        } else if (i2 == 12) {
            i = 1;
        } else if (i2 == 13) {
            i = 3;
        } else if (i2 == 14) {
            i = 4;
        } else if (i2 == 15) {
            i = 5;
        }
        this.mAdjustView.setCurrentPointIndex(i);
        this.mAdjustView.setLastCurrentPointIndex(i);
        this.mAdjustView.setRecommendListener(this);
        this.scrollViewCard = (FontSettingsScrollView) this.mRootView.findViewById(R.id.bottom_scroll_view);
    }

    @Override // com.android.settings.display.FontWeightAdjustView.FontWeightChangeListener
    public void onWeightChange(int i) {
        if (i % 2 != 0 && Math.abs(i - this.mLastProgress) < 5 && this.mLastFontWeight != -1) {
            Log.i("PageLayoutFragment", "ignore weight change, progress:" + i);
            return;
        }
        this.mLastProgress = i;
        if (this.mFontHintTv == null) {
            this.mFontHintTv = (TextView) this.mRootView.findViewById(R.id.font_hint_view);
        }
        if (this.mFontBubbleRightTv == null) {
            this.mFontBubbleRightTv = (TextView) this.mRootView.findViewById(R.id.font_bubble_right);
        }
        if (this.mFontBubbleLeftTv == null) {
            this.mFontBubbleLeftTv = (TextView) this.mRootView.findViewById(R.id.font_bubble_left);
        }
        completeHintText(this.mFontHintTv, this.mCurrentLevel, i);
        setTextViewFont(this.mFontHintTv, i);
        setTextViewFont(this.mFontBubbleLeftTv, i);
        setTextViewFont(this.mFontBubbleRightTv, i);
    }

    public void scrollToPosition(int i, int i2) {
        ObjectAnimator ofInt = ObjectAnimator.ofInt(this.scrollViewCard, "scrollX", i);
        ObjectAnimator ofInt2 = ObjectAnimator.ofInt(this.scrollViewCard, "scrollY", i2);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000L);
        animatorSet.playTogether(ofInt, ofInt2);
        animatorSet.start();
    }

    @Override // com.android.settings.display.FontSizeAdjustView.RecommendListener
    public void scrollViewToHideRecommend() {
        if (!this.isTalkbackMode && this.mRecommendLayout.getVisibility() != RECOMMEND_HIDE) {
            this.scrollViewCard.setCanScroll(false);
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(120L);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.display.PageLayoutFragment.4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PageLayoutFragment.this.mRecommendLayout.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            duration.addListener(new AnonymousClass5());
            duration.start();
        }
        scrollToPosition(0, 0);
    }

    @Override // com.android.settings.display.FontSizeAdjustView.RecommendListener
    public void showRecommendLayout() {
        if (this.isTalkbackMode) {
            return;
        }
        int visibility = this.mRecommendLayout.getVisibility();
        int i = RECOMMEND_SHOW;
        if (visibility != i) {
            this.mRecommendLayout.setVisibility(i);
            this.mRecommendLayout.setAlpha(0.0f);
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.display.PageLayoutFragment.2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PageLayoutFragment.this.mRecommendLayout.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            duration.addListener(new Animator.AnimatorListener() { // from class: com.android.settings.display.PageLayoutFragment.3
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    PageLayoutFragment.this.scrollViewCard.setCanScroll(true);
                    PageLayoutFragment pageLayoutFragment = PageLayoutFragment.this;
                    pageLayoutFragment.setAllTextByCustomSize((ViewGroup) pageLayoutFragment.mRecommendLayout, 1);
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                }
            });
            duration.start();
        }
    }
}
