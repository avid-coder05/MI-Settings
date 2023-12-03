package miui.yellowpage;

import android.content.Context;
import android.text.TextUtils;

/* loaded from: classes4.dex */
public class YellowPagePhone {
    public static final int INVALIDE_YID = -1;
    private static final int MASK_SUSPECT = 240;
    private static final int MASK_T9_SEARCHABLE = 15;
    public static final int TYPE_ANTISPAM = 2;
    public static final int TYPE_MARKED = 3;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_YELLOW_PAGE = 1;
    private int mAntispamProviderId;
    private int mCid;
    private String mCreditImg;
    private int mFlag;
    private boolean mHasCallMenu;
    private int mMarkCount;
    private String mNormalizedNumber;
    private String mNumber;
    private int mNumberType;
    private int mProviderId;
    private String mRawSlogan;
    private String mSlogan;
    private long mT9Rank;
    private String mTag;
    private String mTagPinyin;
    private int mType;
    private boolean mVisible;
    private String mWordAd;
    private long mYpId;
    private String mYpName;
    private String mYpNamePinyin;

    public YellowPagePhone(long j, String str, String str2, String str3, String str4, int i, int i2, int i3, boolean z, String str5, String str6) {
        this(j, str, str2, str3, str4, i, i2, i3, z, str5, str6, false);
    }

    public YellowPagePhone(long j, String str, String str2, String str3, String str4, int i, int i2, int i3, boolean z, String str5, String str6, int i4) {
        this(j, str, str2, str3, str4, i, i2, i3, z, str5, str6, false);
        this.mCid = i4;
    }

    public YellowPagePhone(long j, String str, String str2, String str3, String str4, int i, int i2, int i3, boolean z, String str5, String str6, boolean z2) {
        this.mNumberType = 3;
        this.mYpName = str;
        this.mTag = str2;
        this.mNumber = str3;
        this.mType = i;
        this.mVisible = z;
        this.mMarkCount = i3;
        this.mYpId = j;
        this.mProviderId = i2;
        this.mYpNamePinyin = str5;
        this.mTagPinyin = str6;
        this.mNormalizedNumber = str4;
        this.mHasCallMenu = z2;
    }

    public YellowPagePhone(long j, String str, String str2, String str3, String str4, int i, int i2, int i3, boolean z, String str5, String str6, boolean z2, boolean z3) {
        this.mNumberType = 3;
        this.mYpName = str;
        this.mTag = str2;
        this.mNumber = str3;
        this.mType = i;
        this.mVisible = z;
        this.mMarkCount = i3;
        this.mYpId = j;
        this.mProviderId = i2;
        this.mYpNamePinyin = str5;
        this.mTagPinyin = str6;
        this.mNormalizedNumber = str4;
        this.mHasCallMenu = z3;
        if (z2) {
            this.mFlag |= MASK_SUSPECT;
        }
    }

    public int getAntispamProviderId() {
        return this.mAntispamProviderId;
    }

    public int getCid() {
        return this.mCid;
    }

    public String getCreditImg() {
        return this.mCreditImg;
    }

    public int getFlag() {
        return this.mFlag;
    }

    public int getMarkedCount() {
        return this.mMarkCount;
    }

    public String getNormalizedNumber() {
        return this.mNormalizedNumber;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getNumberType() {
        return this.mNumberType;
    }

    public int getPhoneType() {
        return this.mType;
    }

    public int getProviderId() {
        return this.mProviderId;
    }

    public String getProviderName(Context context) {
        return YellowPageUtils.getProvider(context, this.mProviderId).getName();
    }

    public String getRawSlogan() {
        return this.mRawSlogan;
    }

    public String getSlogan() {
        return this.mSlogan;
    }

    public long getT9Rank() {
        return this.mT9Rank;
    }

    public String getTag() {
        return this.mTag;
    }

    public String getTagPinyin() {
        return this.mTagPinyin;
    }

    public String getWordAd() {
        return this.mWordAd;
    }

    public long getYellowPageId() {
        return this.mYpId;
    }

    public String getYellowPageName() {
        return this.mYpName;
    }

    public String getYellowPagePinyin() {
        return this.mYpNamePinyin;
    }

    public boolean hasCallMenu() {
        return this.mHasCallMenu;
    }

    public boolean isAntispam() {
        return this.mCid > 0;
    }

    public boolean isMarkedSuspect() {
        return (this.mFlag & MASK_SUSPECT) > 0;
    }

    public boolean isProviderMiui() {
        return this.mProviderId == 0;
    }

    public boolean isSuspect(Context context) {
        return isMarkedSuspect();
    }

    public boolean isT9Searchable() {
        return (this.mFlag & 15) == 0;
    }

    public boolean isUnknown() {
        return this.mType == 0;
    }

    public boolean isUserMarked() {
        return this.mType == 3;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public boolean isYellowPage() {
        return this.mType == 1;
    }

    public YellowPagePhone setAntispamProviderId(int i) {
        this.mAntispamProviderId = i;
        return this;
    }

    public YellowPagePhone setCid(int i) {
        this.mCid = i;
        return this;
    }

    public void setCreditImg(String str) {
        this.mCreditImg = str;
    }

    public YellowPagePhone setFlag(int i) {
        this.mFlag = i;
        return this;
    }

    public void setNumberType(int i) {
        this.mNumberType = i;
    }

    public YellowPagePhone setRawSlogan(String str) {
        this.mRawSlogan = str;
        if (!TextUtils.isEmpty(str)) {
            int indexOf = this.mRawSlogan.indexOf("$");
            if (indexOf < 0 || indexOf >= this.mRawSlogan.length() - 1) {
                this.mSlogan = this.mRawSlogan;
            } else {
                this.mSlogan = this.mRawSlogan.substring(0, indexOf);
                this.mWordAd = this.mRawSlogan.substring(indexOf + 1);
            }
        }
        return this;
    }

    public YellowPagePhone setT9Rank(long j) {
        this.mT9Rank = j;
        return this;
    }
}
