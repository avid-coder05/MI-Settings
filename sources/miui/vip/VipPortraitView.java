package miui.vip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.internal.vip.VipInternalCallback;
import com.miui.internal.vip.utils.ImageDownloader;
import com.miui.internal.vip.utils.JsonParser;
import com.miui.internal.vip.utils.Utils;
import com.miui.internal.vip.utils.VipDataPref;
import com.miui.system.internal.R;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import miui.accounts.ExtraAccountManager;

/* loaded from: classes4.dex */
public class VipPortraitView extends RelativeLayout {
    static final int ACHIEVEMENT_COUNT = 4;
    static final String BANNER = "banner";
    static final int MAX_BANNER_COUNT = 2;
    static final int MODEL_COMPACT = 0;
    static final int MODEL_EXPAND = 1;
    static final String PREF_KEY_BANNER = "banner";
    static final String PREF_NAME = "portrait_view";
    static final String STATISTIC_AVATAR = "portrait_avatar";
    static final String STATISTIC_BACKGROUND = "portrait_background";
    static final String STATISTIC_BANNER = "portrait_banner_";
    static final String STATISTIC_CUSTOM_BUTTON = "portrait_custom_button";
    static final String STATISTIC_SIGN = "portrait_sign";
    static final int WRAP_CONTENT = -1;
    public int ARROW_STYLE_CARD;
    public int ARROW_STYLE_LIST;
    View.OnClickListener mAccountWelcomeClick;
    List<VipAchievement> mAchievementList;
    LinearLayout mAchievements;
    ImageView mAction;
    View.OnClickListener mActionClick;
    Drawable mActionIcon;
    View mArrow;
    int mArrowCardMargin;
    int mArrowListMargin;
    int mArrowStyle;
    ImageView mAvatar;
    View.OnClickListener mAvatarClick;
    View.OnClickListener mBackgroundClick;
    ImageView mBadge;
    LinearLayout mBanner;
    View mBannerGroup;
    List<VipBanner> mBannerList;
    Comparator<VipAchievement> mCmpVipAchievement;
    Comparator<VipBanner> mCmpVipBanner;
    Account mExtAccount;
    View mFrame;
    TextView mIdView;
    private final VipInternalCallback mListener;
    Drawable mLockIcon;
    TextView mName;
    VipDataPref mPref;
    private final BroadcastReceiver mReceiver;
    boolean mServiceAvailable;
    boolean mShowBanner;
    int mShowModel;
    TextView mSign;
    View.OnClickListener mSignClick;
    View mSignGroup;
    TextView mTitle;
    View.OnClickListener mUserDetailClick;
    long mUserId;
    VipUserInfo mUserInfo;
    String mUserSign;
    View.OnClickListener mVipLevelListClick;
    static final int[] LayoutId = {R.layout.vip_portrait_view, R.layout.vip_portrait_expand_view};
    static final int[] BadgeIconSize = {R.dimen.vip_achievement_icon_size, R.dimen.vip_achievement_icon_size_1};

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class ClickListenerWrapper implements View.OnClickListener {
        View.OnClickListener mClickListener;
        String mData;

        ClickListenerWrapper(String str, View.OnClickListener onClickListener) {
            this.mClickListener = onClickListener;
            this.mData = str;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            VipService.instance().sendStatistic(this.mData);
            View.OnClickListener onClickListener = this.mClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }
    }

    public VipPortraitView(Context context) {
        super(context);
        this.ARROW_STYLE_LIST = 0;
        this.ARROW_STYLE_CARD = 1;
        this.mArrowStyle = 0;
        this.mShowModel = 0;
        this.mShowBanner = true;
        this.mCmpVipAchievement = new Comparator<VipAchievement>() { // from class: miui.vip.VipPortraitView.1
            @Override // java.util.Comparator
            public int compare(VipAchievement vipAchievement, VipAchievement vipAchievement2) {
                return vipAchievement.id != vipAchievement2.id ? -1 : 0;
            }
        };
        this.mCmpVipBanner = new Comparator<VipBanner>() { // from class: miui.vip.VipPortraitView.2
            @Override // java.util.Comparator
            public int compare(VipBanner vipBanner, VipBanner vipBanner2) {
                return (TextUtils.equals(vipBanner.name, vipBanner2.name) && TextUtils.equals(vipBanner.icon, vipBanner2.icon)) ? 0 : -1;
            }
        };
        this.mAccountWelcomeClick = new View.OnClickListener() { // from class: miui.vip.VipPortraitView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Utils.startActivity(VipPortraitView.this.getContext(), "com.xiaomi.account.action.XIAOMI_ACCOUNT_WELCOME", ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
            }
        };
        ClickListenerWrapper clickListenerWrapper = new ClickListenerWrapper(STATISTIC_AVATAR, new View.OnClickListener() { // from class: miui.vip.VipPortraitView.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Utils.startActivity(VipPortraitView.this.getContext(), "com.xiaomi.account.action.USER_INFO_DETAIL", ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
            }
        });
        this.mUserDetailClick = clickListenerWrapper;
        this.mAvatarClick = clickListenerWrapper;
        ClickListenerWrapper clickListenerWrapper2 = new ClickListenerWrapper(STATISTIC_BACKGROUND, new View.OnClickListener() { // from class: miui.vip.VipPortraitView.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Utils.startActivity(VipPortraitView.this.getContext(), VipService.ACTION_VIP_LEVEL_LIST, "com.xiaomi.vip");
            }
        });
        this.mVipLevelListClick = clickListenerWrapper2;
        this.mBackgroundClick = clickListenerWrapper2;
        this.mListener = new VipInternalCallback(16, 64) { // from class: miui.vip.VipPortraitView.6
            public void onAchievements(int i, List<VipAchievement> list, String str) {
                if (i == 0) {
                    VipPortraitView vipPortraitView = VipPortraitView.this;
                    if (VipPortraitView.isSameList(vipPortraitView.mAchievementList, list, vipPortraitView.mCmpVipAchievement)) {
                        return;
                    }
                    VipPortraitView.this.setAchievements(list);
                }
            }

            public void onBanners(int i, List<VipBanner> list, String str) {
                if (i == 0) {
                    VipPortraitView vipPortraitView = VipPortraitView.this;
                    if (VipPortraitView.isSameList(vipPortraitView.mBannerList, list, vipPortraitView.mCmpVipBanner)) {
                        return;
                    }
                    VipPortraitView.this.saveBannerData(list);
                    VipPortraitView.this.setBanners(list);
                }
            }

            public void onConnected(boolean z, VipUserInfo vipUserInfo, List<VipAchievement> list) {
                Object[] objArr = new Object[3];
                objArr[0] = Boolean.valueOf(z);
                objArr[1] = vipUserInfo;
                objArr[2] = list != null ? Arrays.toString(list.toArray()) : "null";
                Utils.log("VipPortraitView.onConnected, serviceAvailable = %s, user = %s, achievements = %s", objArr);
                VipPortraitView vipPortraitView = VipPortraitView.this;
                vipPortraitView.mServiceAvailable = z;
                if (!z) {
                    vipPortraitView.clearVipInfo();
                    return;
                }
                Utils.log("VipPortraitView.onConnected, before setAchievements", new Object[0]);
                if (Utils.hasData(list)) {
                    VipPortraitView.this.setAchievements(list);
                } else {
                    VipService.instance().queryAchievements();
                }
                VipService.instance().queryBanners();
                Utils.log("VipPortraitView.onConnected, before setVipLevel", new Object[0]);
                if (vipUserInfo != null) {
                    VipPortraitView.this.setVipLevel(vipUserInfo);
                } else {
                    VipService.instance().queryUserVipInfo();
                }
            }

            public void onUserInfo(int i, VipUserInfo vipUserInfo, String str) {
                Utils.log("VipPortraitView.onUserInfo, code = %d, user = %s, errMsg = %s", new Object[]{Integer.valueOf(i), vipUserInfo, str});
                if (i == 0) {
                    VipPortraitView.this.setVipLevel(vipUserInfo);
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: miui.vip.VipPortraitView.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                Utils.log("VipPortraitView.mReceiver, action = %s", new Object[]{action});
                VipPortraitView.this.setAccountData();
                if (TextUtils.equals(action, ExtraAccountManager.LOGIN_ACCOUNTS_POST_CHANGED_ACTION)) {
                    if (intent.getIntExtra(ExtraAccountManager.EXTRA_UPDATE_TYPE, 0) == 2) {
                        Utils.log("mReciever, user is added, connect vip service", new Object[0]);
                        VipPortraitView.this.connect();
                        return;
                    }
                    Utils.log("mReciever, user is removed, disconnect vip service", new Object[0]);
                    VipPortraitView.this.disconnect();
                }
            }
        };
    }

    public VipPortraitView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VipPortraitView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.ARROW_STYLE_LIST = 0;
        this.ARROW_STYLE_CARD = 1;
        this.mArrowStyle = 0;
        this.mShowModel = 0;
        this.mShowBanner = true;
        this.mCmpVipAchievement = new Comparator<VipAchievement>() { // from class: miui.vip.VipPortraitView.1
            @Override // java.util.Comparator
            public int compare(VipAchievement vipAchievement, VipAchievement vipAchievement2) {
                return vipAchievement.id != vipAchievement2.id ? -1 : 0;
            }
        };
        this.mCmpVipBanner = new Comparator<VipBanner>() { // from class: miui.vip.VipPortraitView.2
            @Override // java.util.Comparator
            public int compare(VipBanner vipBanner, VipBanner vipBanner2) {
                return (TextUtils.equals(vipBanner.name, vipBanner2.name) && TextUtils.equals(vipBanner.icon, vipBanner2.icon)) ? 0 : -1;
            }
        };
        this.mAccountWelcomeClick = new View.OnClickListener() { // from class: miui.vip.VipPortraitView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Utils.startActivity(VipPortraitView.this.getContext(), "com.xiaomi.account.action.XIAOMI_ACCOUNT_WELCOME", ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
            }
        };
        ClickListenerWrapper clickListenerWrapper = new ClickListenerWrapper(STATISTIC_AVATAR, new View.OnClickListener() { // from class: miui.vip.VipPortraitView.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Utils.startActivity(VipPortraitView.this.getContext(), "com.xiaomi.account.action.USER_INFO_DETAIL", ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
            }
        });
        this.mUserDetailClick = clickListenerWrapper;
        this.mAvatarClick = clickListenerWrapper;
        ClickListenerWrapper clickListenerWrapper2 = new ClickListenerWrapper(STATISTIC_BACKGROUND, new View.OnClickListener() { // from class: miui.vip.VipPortraitView.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Utils.startActivity(VipPortraitView.this.getContext(), VipService.ACTION_VIP_LEVEL_LIST, "com.xiaomi.vip");
            }
        });
        this.mVipLevelListClick = clickListenerWrapper2;
        this.mBackgroundClick = clickListenerWrapper2;
        this.mListener = new VipInternalCallback(16, 64) { // from class: miui.vip.VipPortraitView.6
            public void onAchievements(int i2, List<VipAchievement> list, String str) {
                if (i2 == 0) {
                    VipPortraitView vipPortraitView = VipPortraitView.this;
                    if (VipPortraitView.isSameList(vipPortraitView.mAchievementList, list, vipPortraitView.mCmpVipAchievement)) {
                        return;
                    }
                    VipPortraitView.this.setAchievements(list);
                }
            }

            public void onBanners(int i2, List<VipBanner> list, String str) {
                if (i2 == 0) {
                    VipPortraitView vipPortraitView = VipPortraitView.this;
                    if (VipPortraitView.isSameList(vipPortraitView.mBannerList, list, vipPortraitView.mCmpVipBanner)) {
                        return;
                    }
                    VipPortraitView.this.saveBannerData(list);
                    VipPortraitView.this.setBanners(list);
                }
            }

            public void onConnected(boolean z, VipUserInfo vipUserInfo, List<VipAchievement> list) {
                Object[] objArr = new Object[3];
                objArr[0] = Boolean.valueOf(z);
                objArr[1] = vipUserInfo;
                objArr[2] = list != null ? Arrays.toString(list.toArray()) : "null";
                Utils.log("VipPortraitView.onConnected, serviceAvailable = %s, user = %s, achievements = %s", objArr);
                VipPortraitView vipPortraitView = VipPortraitView.this;
                vipPortraitView.mServiceAvailable = z;
                if (!z) {
                    vipPortraitView.clearVipInfo();
                    return;
                }
                Utils.log("VipPortraitView.onConnected, before setAchievements", new Object[0]);
                if (Utils.hasData(list)) {
                    VipPortraitView.this.setAchievements(list);
                } else {
                    VipService.instance().queryAchievements();
                }
                VipService.instance().queryBanners();
                Utils.log("VipPortraitView.onConnected, before setVipLevel", new Object[0]);
                if (vipUserInfo != null) {
                    VipPortraitView.this.setVipLevel(vipUserInfo);
                } else {
                    VipService.instance().queryUserVipInfo();
                }
            }

            public void onUserInfo(int i2, VipUserInfo vipUserInfo, String str) {
                Utils.log("VipPortraitView.onUserInfo, code = %d, user = %s, errMsg = %s", new Object[]{Integer.valueOf(i2), vipUserInfo, str});
                if (i2 == 0) {
                    VipPortraitView.this.setVipLevel(vipUserInfo);
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: miui.vip.VipPortraitView.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                Utils.log("VipPortraitView.mReceiver, action = %s", new Object[]{action});
                VipPortraitView.this.setAccountData();
                if (TextUtils.equals(action, ExtraAccountManager.LOGIN_ACCOUNTS_POST_CHANGED_ACTION)) {
                    if (intent.getIntExtra(ExtraAccountManager.EXTRA_UPDATE_TYPE, 0) == 2) {
                        Utils.log("mReciever, user is added, connect vip service", new Object[0]);
                        VipPortraitView.this.connect();
                        return;
                    }
                    Utils.log("mReciever, user is removed, disconnect vip service", new Object[0]);
                    VipPortraitView.this.disconnect();
                }
            }
        };
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.VipPortraitView, R.attr.vipShowModel, 0);
        this.mShowModel = obtainStyledAttributes.getInt(R.styleable.VipPortraitView_vipShowModel, 0);
        obtainStyledAttributes.recycle();
    }

    private static void addAchievementIcon(LinearLayout linearLayout, VipAchievement vipAchievement, int i, int i2, int i3) {
        Utils.log("addAchievementIcon, info.badgeId = %d, info.name = %s, info.url = %s", new Object[]{Long.valueOf(vipAchievement.id), vipAchievement.name, vipAchievement.url});
        Context context = linearLayout.getContext();
        View inflate = RelativeLayout.inflate(context, R.layout.vip_achievement_icon, null);
        inflate.setTag(vipAchievement);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.vip_id_achieve_icon);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = i2;
        layoutParams.height = i2;
        ImageDownloader.loadImage(context, vipAchievement.url, String.valueOf(vipAchievement.id), imageView);
        TextView textView = (TextView) inflate.findViewById(R.id.vip_id_achieve_name);
        textView.setVisibility(i == 0 ? 8 : 0);
        textView.setText(vipAchievement.name);
        addViewToAchievements(linearLayout, inflate, -1, i3);
    }

    private void addAchievementIconToLinearLayout(LinearLayout linearLayout, List<VipAchievement> list, int i) {
        addAchievementIconToLinearLayout(linearLayout, list, this.mLockIcon, this.mShowModel, i, getWidth());
    }

    private static void addAchievementIconToLinearLayout(LinearLayout linearLayout, List<VipAchievement> list, Drawable drawable, int i, int i2, int i3) {
        int achievementCount = getAchievementCount(list);
        int i4 = achievementCount - 1;
        Context context = linearLayout.getContext();
        int dimensionPixelOffset = i == 0 ? context.getResources().getDimensionPixelOffset(R.dimen.vip_margin_4) : ((i3 - (context.getResources().getDimensionPixelOffset(R.dimen.vip_margin_8) * 2)) - (i2 * achievementCount)) / i4;
        int i5 = 0;
        while (i5 < achievementCount) {
            addAchievementIcon(linearLayout, list.get(i5), i, i2, i5 == i4 ? 0 : dimensionPixelOffset);
            i5++;
        }
        drawAchievementLock(linearLayout, drawable, i2);
    }

    private static void addIconCover(View view, int i, Drawable drawable) {
        ImageView imageView = (ImageView) view.findViewById(R.id.vip_id_achieve_cover);
        if (imageView != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = i;
            layoutParams.height = i;
            imageView.setImageDrawable(drawable);
        }
    }

    private static void addViewToAchievements(LinearLayout linearLayout, View view, int i, int i2) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMarginEnd(i2);
        if (i != -1) {
            layoutParams.width = i;
            layoutParams.height = i;
        }
        linearLayout.addView(view, layoutParams);
    }

    private void changeModel(int i) {
        if (this.mShowModel != i) {
            this.mShowModel = i;
            removeAllViews();
            initView();
            loadData();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearVipInfo() {
        Utils.log("clearVipInfo", new Object[0]);
        setVipLevel(null);
        setAchievements(null);
        setBanners(null);
    }

    private static void drawAchievementLock(LinearLayout linearLayout, Drawable drawable, int i) {
        if (drawable != null) {
            int childCount = linearLayout.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = linearLayout.getChildAt(i2);
                Object tag = childAt.getTag();
                if ((tag instanceof VipAchievement) && !((VipAchievement) tag).isOwned) {
                    addIconCover(childAt, i, drawable);
                }
            }
        }
    }

    private static int getAchievementCount(List<VipAchievement> list) {
        if (list == null) {
            return 0;
        }
        return Math.min(list.size(), 4);
    }

    private int getAchievementIconSize() {
        return getAchievementIconSize(getContext(), this.mShowModel);
    }

    private static int getAchievementIconSize(Context context, int i) {
        return context.getResources().getDimensionPixelSize(BadgeIconSize[i]);
    }

    @Deprecated
    public static View getAchievementView(Context context, List<VipAchievement> list) {
        return null;
    }

    private String getBannerTypeName(long j) {
        return "banner" + String.valueOf(j);
    }

    private synchronized VipDataPref getPref() {
        if (this.mPref == null) {
            this.mPref = new VipDataPref(getContext(), PREF_NAME);
        }
        return this.mPref;
    }

    private void initBanner() {
        this.mBannerGroup = findViewById(R.id.vip_id_banner_group);
        this.mBanner = (LinearLayout) findViewById(R.id.vip_id_banner);
    }

    private void initView() {
        Utils.log("initView", new Object[0]);
        RelativeLayout.inflate(getContext(), LayoutId[this.mShowModel], this);
        View findViewById = findViewById(R.id.vip_id_frame);
        this.mFrame = findViewById;
        findViewById.setOnClickListener(this.mAvatarClick);
        this.mAvatar = (ImageView) findViewById(R.id.vip_id_avatar);
        TextView textView = (TextView) findViewById(R.id.vip_id_title);
        this.mTitle = textView;
        textView.setVisibility(8);
        this.mName = (TextView) findViewById(R.id.vip_id_name);
        this.mIdView = (TextView) findViewById(R.id.vip_id_user_id);
        this.mBadge = (ImageView) findViewById(R.id.vip_id_badge);
        View findViewById2 = findViewById(R.id.vip_id_sign_group);
        this.mSignGroup = findViewById2;
        findViewById2.setOnClickListener(this.mSignClick);
        this.mSign = (TextView) findViewById(R.id.vip_id_sign);
        this.mAchievements = (LinearLayout) findViewById(R.id.vip_id_achievements);
        this.mAction = (ImageView) findViewById(R.id.vip_id_custom_action);
        this.mArrow = findViewById(R.id.vip_id_arrow);
        setArrowStyle(this.mArrowStyle);
        this.mLockIcon = getContext().getResources().getDrawable(R.drawable.vip_icon_default_achievement);
        initBanner();
    }

    private void initViewAndSetData() {
        if (this.mFrame == null) {
            this.mArrowListMargin = getResources().getDimensionPixelSize(R.dimen.vip_margin_arrow_right);
            this.mArrowCardMargin = getResources().getDimensionPixelSize(R.dimen.vip_margin_frame_left);
            initView();
            setAccountData();
            loadBannerData();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> boolean isSameList(List<T> list, List<T> list2, Comparator<T> comparator) {
        if (list == null) {
            return list2 == null;
        } else if (list2 == null || list.size() != list2.size()) {
            return false;
        } else {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                if (comparator.compare(list.get(i), list2.get(i)) != 0) {
                    return false;
                }
            }
            return true;
        }
    }

    private void loadAvatarFile(Account account, AccountManager accountManager) {
        Utils.log("loadAvatarFile, account = %s", new Object[]{account});
        ImageView imageView = this.mAvatar;
        if (imageView != null) {
            if (account == null) {
                imageView.setImageResource(R.drawable.vip_default_avatar);
                return;
            }
            String userData = accountManager.getUserData(account, "acc_avatar_url");
            String userData2 = accountManager.getUserData(account, "acc_avatar_file_name");
            Utils.log("loadAvatarFile, avatarUrl = %s, fileName = %s", new Object[]{userData, userData2});
            if (TextUtils.isEmpty(userData2)) {
                this.mAvatar.setImageResource(R.drawable.vip_default_avatar);
                return;
            }
            String str = account.name;
            ImageDownloader.loadImage(getContext(), userData, userData2.replace(str, Utils.md5(str)), this.mAvatar, true);
        }
    }

    private void loadBannerData() {
        if (this.mUserId > 0) {
            List<VipBanner> parseJsonArrayAsList = JsonParser.parseJsonArrayAsList(getPref().getString("banner" + this.mUserId), VipBanner.class);
            Utils.log("VipPortraitView.loadBannerData, list = %s", new Object[]{Arrays.toString(parseJsonArrayAsList.toArray())});
            if (Utils.hasData(parseJsonArrayAsList)) {
                setBanners(parseJsonArrayAsList);
            }
        }
    }

    private void loadData() {
        Utils.log("loadData", new Object[0]);
        setAccountData();
        loadBannerData();
        setVipLevel(this.mUserInfo);
        setSignature(this.mUserSign);
        setCustomButton(this.mActionIcon, this.mActionClick);
        setAchievements(this.mAchievementList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveBannerData(List<VipBanner> list) {
        String json = JsonParser.toJson(list);
        Utils.log("VipPortraitView.saveBannerData, bannerList = %s, bannerJson = %s", new Object[]{list, json});
        getPref().setString("banner" + this.mUserId, json);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAccountData() {
        long currentTimeMillis = System.currentTimeMillis();
        Account account = this.mExtAccount;
        if (account == null) {
            account = ExtraAccountManager.getXiaomiAccount(getContext());
        }
        Utils.log("setAccountData, account = %s", new Object[]{account});
        if (this.mName != null && this.mIdView != null) {
            AccountManager accountManager = AccountManager.get(getContext());
            if (account != null) {
                this.mUserId = TextUtils.isDigitsOnly(account.name) ? Long.valueOf(account.name).longValue() : 0L;
                this.mFrame.setOnClickListener(this.mAvatarClick);
                super.setOnClickListener(this.mBackgroundClick);
                String charSequence = this.mIdView.getText().toString();
                if (TextUtils.isEmpty(charSequence) || !charSequence.equals(account.name)) {
                    Utils.log("setAccountData, data is changed", new Object[0]);
                    this.mIdView.setText(account.name);
                }
                String charSequence2 = this.mName.getText().toString();
                String userData = accountManager.getUserData(account, "acc_user_name");
                Utils.log("setAccountData, userName = %s", new Object[]{userData});
                if (TextUtils.isEmpty(charSequence2) || !charSequence2.equals(userData)) {
                    TextView textView = this.mName;
                    if (TextUtils.isEmpty(userData)) {
                        userData = account.name;
                    }
                    textView.setText(userData);
                }
            } else {
                Utils.log("setAccountData, user isn't signed in", new Object[0]);
                this.mName.setText(R.string.vip_not_login);
                this.mIdView.setText(R.string.vip_login);
                this.mFrame.setOnClickListener(this.mAccountWelcomeClick);
                super.setOnClickListener(this.mAccountWelcomeClick);
                clearVipInfo();
            }
            loadAvatarFile(account, accountManager);
        }
        Utils.log("setAccountData end, elapsed %d", new Object[]{Long.valueOf(System.currentTimeMillis() - currentTimeMillis)});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAchievements(List<VipAchievement> list) {
        Utils.log("setAchievements", new Object[0]);
        LinearLayout linearLayout = this.mAchievements;
        if (linearLayout != null) {
            int childCount = linearLayout.getChildCount();
            if (!Utils.hasData(list)) {
                if (childCount > 0) {
                    Utils.log("setAchievements, no achievement, remove all views", new Object[0]);
                    this.mAchievements.removeAllViews();
                }
                showAchievement(false);
            } else if ((!isSameList(this.mAchievementList, list, this.mCmpVipAchievement)) != false || childCount == 0) {
                Utils.log("setAchievements, set achievement list", new Object[0]);
                this.mAchievements.removeAllViews();
                showAchievement(true);
                addAchievementIconToLinearLayout(this.mAchievements, list, getAchievementIconSize());
                this.mAchievements.requestLayout();
            }
        }
        this.mAchievementList = list;
    }

    private void setBannerView(List<VipBanner> list, int i) {
        int min = Math.min(2, list.size());
        if (min > i) {
            for (int i2 = 0; i2 < min - i; i2++) {
                View inflate = RelativeLayout.inflate(getContext(), R.layout.vip_banner, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
                layoutParams.weight = 1.0f;
                this.mBanner.addView(inflate, layoutParams);
            }
        } else if (i > min) {
            for (int i3 = 0; i3 < i - min; i3++) {
                this.mBanner.removeViewAt(i3);
            }
        }
        for (int i4 = 0; i4 < min; i4++) {
            setBannerViewData(this.mBanner.getChildAt(i4), list.get(i4));
        }
    }

    private void setBannerViewData(View view, final VipBanner vipBanner) {
        ImageView imageView = (ImageView) view.findViewById(R.id.vip_id_banner_icon);
        if (TextUtils.isEmpty(vipBanner.icon) || !Utils.isStringUri(vipBanner.icon)) {
            imageView.setImageResource(R.drawable.vip_icon_chalice);
        } else {
            ImageDownloader.loadImage(getContext(), vipBanner.icon, getBannerTypeName(vipBanner.id), imageView);
        }
        ((TextView) view.findViewById(R.id.vip_id_banner_name)).setText(vipBanner.name);
        TextView textView = (TextView) view.findViewById(R.id.vip_id_banner_info);
        if (TextUtils.isEmpty(vipBanner.info)) {
            textView.setVisibility(8);
        } else {
            textView.setText(vipBanner.info);
        }
        Utils.log("setBannerViewData, banner = %s", new Object[]{vipBanner});
        if (TextUtils.isEmpty(vipBanner.action)) {
            return;
        }
        view.setOnClickListener(new ClickListenerWrapper(STATISTIC_BANNER + vipBanner.id, new View.OnClickListener() { // from class: miui.vip.VipPortraitView.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                Context context = VipPortraitView.this.getContext();
                VipBanner vipBanner2 = vipBanner;
                Utils.startActivity(context, vipBanner2.action, (String) null, vipBanner2.extraParams);
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBanners(List<VipBanner> list) {
        if (this.mBanner != null) {
            if (this.mShowBanner && Utils.hasData(list)) {
                this.mBannerGroup.setVisibility(0);
                int childCount = this.mBanner.getChildCount();
                if (childCount == 0 || !isSameList(this.mBannerList, list, this.mCmpVipBanner)) {
                    setBannerView(list, childCount);
                }
                this.mBanner.requestLayout();
            } else {
                this.mBannerGroup.setVisibility(8);
            }
        }
        this.mBannerList = list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVipLevel(VipUserInfo vipUserInfo) {
        int i;
        VipUserInfo vipUserInfo2;
        Utils.log("setVipLevel", new Object[0]);
        super.setOnClickListener(this.mBackgroundClick);
        if (this.mFrame != null) {
            if (vipUserInfo == null && this.mUserInfo != null) {
                Utils.log("setVipLevel, hide views of vip frame and level", new Object[0]);
                this.mBadge.setImageBitmap(null);
            } else if (vipUserInfo != null && (i = vipUserInfo.level) > 0 && ((vipUserInfo2 = this.mUserInfo) == null || vipUserInfo2.level != i || this.mBadge.getDrawable() == null)) {
                Utils.log("setVipLevel, level = %d", new Object[]{Integer.valueOf(vipUserInfo.level)});
                ImageDownloader.loadImage(getContext(), String.format("https://rs.vip.miui.com/h5/level_icons/icon_level_%d.webp", Integer.valueOf(vipUserInfo.level)), VipService.VIP_LEVEL_ICON, this.mBadge);
            }
        }
        this.mUserInfo = vipUserInfo;
    }

    private void showAchievement(boolean z) {
        this.mAchievements.setVisibility(z ? 0 : 8);
    }

    public void connect() {
        VipService.instance().connect(this.mListener);
    }

    public void disconnect() {
        VipService.instance().disconnect(this.mListener);
    }

    public boolean isShowCompactModel() {
        return this.mShowModel == 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Utils.log("VipPortraitView.onAttachedToWindow", new Object[0]);
        getContext().registerReceiver(this.mReceiver, Utils.ACCOUNT_CHANGE_FILTER);
        connect();
        initViewAndSetData();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Utils.log("VipPortraitView.onDetachedToWindow", new Object[0]);
        this.mExtAccount = null;
        ImageDownloader.stop();
        getContext().unregisterReceiver(this.mReceiver);
        disconnect();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViewAndSetData();
    }

    public void setArrowStyle(int i) {
        View view = this.mArrow;
        if (view != null && this.mShowModel == 1) {
            ((RelativeLayout.LayoutParams) view.getLayoutParams()).setMarginEnd(i == this.ARROW_STYLE_CARD ? this.mArrowCardMargin : this.mArrowListMargin);
        }
        this.mArrowStyle = i;
    }

    public void setAvatarViewClickListener(View.OnClickListener onClickListener) {
        View.OnClickListener clickListenerWrapper = onClickListener != null ? new ClickListenerWrapper(STATISTIC_AVATAR, onClickListener) : this.mUserDetailClick;
        this.mAvatarClick = clickListenerWrapper;
        View view = this.mFrame;
        if (view != null) {
            view.setOnClickListener(clickListenerWrapper);
        }
    }

    public void setCustomButton(Drawable drawable, View.OnClickListener onClickListener) {
        this.mActionIcon = drawable;
        this.mActionClick = new ClickListenerWrapper(STATISTIC_CUSTOM_BUTTON, onClickListener);
        ImageView imageView = this.mAction;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
            this.mAction.setOnClickListener(this.mActionClick);
        }
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        View.OnClickListener clickListenerWrapper = onClickListener != null ? new ClickListenerWrapper(STATISTIC_BACKGROUND, onClickListener) : this.mServiceAvailable ? this.mVipLevelListClick : null;
        this.mBackgroundClick = clickListenerWrapper;
        super.setOnClickListener(clickListenerWrapper);
    }

    public void setSignature(String str) {
        if (this.mSignGroup != null) {
            String charSequence = this.mSign.getText().toString();
            if (TextUtils.isEmpty(str)) {
                this.mSignGroup.setVisibility(8);
            } else {
                this.mSignGroup.setVisibility(0);
                if (!str.equals(charSequence)) {
                    this.mSign.setText(this.mUserSign);
                }
            }
        }
        this.mUserSign = str;
    }

    public void setSignatureViewClickListener(View.OnClickListener onClickListener) {
        ClickListenerWrapper clickListenerWrapper = new ClickListenerWrapper(STATISTIC_SIGN, onClickListener);
        this.mSignClick = clickListenerWrapper;
        View view = this.mSignGroup;
        if (view != null) {
            view.setOnClickListener(clickListenerWrapper);
        }
    }

    public void setXiaomiAccount(Account account) {
        this.mExtAccount = account;
        setAccountData();
    }

    public void showBanner(boolean z) {
        this.mShowBanner = z;
        setBanners(this.mBannerList);
    }

    public void showBottomDivider(boolean z) {
        findViewById(R.id.vip_bottom_divider).setVisibility(z ? 0 : 8);
    }
}
