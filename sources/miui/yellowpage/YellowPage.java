package miui.yellowpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes4.dex */
public class YellowPage {
    private static final String TAG = "YellowPage";
    private String mAddress;
    private String mAlias;
    private String mAuthIconName;
    private String mBrief;
    private String mCatId;
    private String mContent;
    private String mCreditImg;
    private String mExtraData;
    private String mFirmUrl;
    private List<String> mGallery;
    private String mHotCatId;
    private int mHotSort;
    private boolean mIsHot;
    private boolean mIsMasterPage;
    private boolean mIsPreset;
    private String mLatitude;
    private String mLocId;
    private String mLongitude;
    private String mMiId;
    private String mName;
    private List<YellowPagePhone> mPhones = new ArrayList();
    private String mPhotoUrl;
    private String mPinyin;
    private int mProviderId;
    private List<Provider> mProviders;
    private String mSlogan;
    private List<Social> mSocials;
    private String mSourceId;
    private String mSourceUrl;
    private String mThumbnailUrl;
    private String mUrl;
    private long mYid;

    /* loaded from: classes4.dex */
    public static class Provider {
        private int mId;
        private String mSourceUrl;

        public static Provider fromJson(JSONObject jSONObject) {
            try {
                int i = jSONObject.getInt("provider");
                return new Provider().setId(i).setSourceUrl(jSONObject.getString(Tag.TagYellowPage.SOURCE_URL));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        public int getId() {
            return this.mId;
        }

        public String getSourceUrl() {
            return this.mSourceUrl;
        }

        public Provider setId(int i) {
            this.mId = i;
            return this;
        }

        public Provider setSourceUrl(String str) {
            this.mSourceUrl = str;
            return this;
        }
    }

    /* loaded from: classes4.dex */
    public static class Social {
        private String mName;
        private int mProviderId;
        private String mUrl;

        public Social(String str, String str2, int i) {
            this.mUrl = str;
            this.mName = str2;
            this.mProviderId = i;
        }

        public String getName() {
            return this.mName;
        }

        public int getProviderId() {
            return this.mProviderId;
        }

        public String getUrl() {
            return this.mUrl;
        }
    }

    /* loaded from: classes4.dex */
    private interface TagCallMenuNIvr {
        public static final String CALL_MENU = "callMenu";
        public static final String ICON = "icon";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String TYPE = "type";
    }

    /* loaded from: classes4.dex */
    private interface TagSocial {
        public static final String NAME = "name";
        public static final String PROVIDER = "provider";
        public static final String PROVIDER_NAME = "providerName";
        public static final String URL = "url";
    }

    public static YellowPage fromJson(String str) {
        try {
            return fromJson(new JSONObject(str));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static YellowPage fromJson(JSONObject jSONObject) {
        boolean z;
        boolean z2;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        boolean z3;
        long j;
        String str6;
        String str7;
        int i;
        String str8;
        String str9;
        String str10;
        String str11;
        String str12;
        String str13;
        String str14;
        String str15;
        String str16;
        String str17;
        String str18;
        int i2;
        ArrayList arrayList;
        ArrayList arrayList2;
        String str19 = "phone";
        try {
            long j2 = jSONObject.getLong(Tag.TagYellowPage.YID);
            String string = jSONObject.getString(Tag.TagYellowPage.NAME);
            int optInt = jSONObject.optInt("provider");
            String optString = jSONObject.optString(Tag.TagYellowPage.PINYIN);
            String optString2 = jSONObject.optString(Tag.TagYellowPage.ALIAS);
            String optString3 = jSONObject.optString("address");
            String optString4 = jSONObject.optString(Tag.TagYellowPage.URL);
            String optString5 = jSONObject.optString(Tag.TagYellowPage.FIRMURL);
            String optString6 = jSONObject.optString(Tag.TagYellowPage.CREDITIMG);
            String optString7 = jSONObject.optString(Tag.TagYellowPage.SOURCE_URL);
            String optString8 = jSONObject.optString(Tag.TagYellowPage.SOURCE_ID);
            String optString9 = jSONObject.optString(Tag.TagYellowPage.BRIEF_INFO);
            String str20 = optString8;
            String optString10 = jSONObject.optString("hotCatId");
            int optInt2 = jSONObject.optInt("hotSort");
            String optString11 = jSONObject.optString("catId");
            String optString12 = jSONObject.optString("locId");
            String optString13 = jSONObject.optString("longitude");
            String optString14 = jSONObject.optString("latitude");
            String optString15 = jSONObject.optString("miid");
            String optString16 = jSONObject.optString(Tag.TagYellowPage.MI_SUB_ID);
            String optString17 = jSONObject.optString("slogan");
            String optString18 = jSONObject.optString(Tag.TagYellowPage.AUTH_ICON);
            String str21 = optString6;
            String str22 = optString;
            boolean z4 = jSONObject.optInt(Tag.TagYellowPage.TYPE) == 2;
            boolean z5 = jSONObject.optInt(Tag.TagYellowPage.HOT) == 1;
            int i3 = optInt;
            boolean z6 = jSONObject.optInt("buildIn") == 1;
            boolean has = jSONObject.has(TagCallMenuNIvr.CALL_MENU);
            JSONArray optJSONArray = jSONObject.optJSONArray("phone");
            if (optJSONArray != null) {
                boolean z7 = z5;
                boolean z8 = z6;
                int i4 = 0;
                ArrayList arrayList3 = null;
                while (i4 < optJSONArray.length()) {
                    JSONObject jSONObject2 = optJSONArray.getJSONObject(i4);
                    String string2 = jSONObject2.getString(str19);
                    String str23 = str19;
                    String optString19 = jSONObject2.optString(Tag.TagPhone.NORMALIZED_NUMBER);
                    boolean z9 = z4;
                    String string3 = jSONObject2.getString(Tag.TagPhone.Tag);
                    String string4 = jSONObject2.getString(Tag.TagPhone.PINYIN);
                    int i5 = i4;
                    JSONArray jSONArray = optJSONArray;
                    long optLong = jSONObject2.optLong(Tag.TagPhone.T9_RANK);
                    int optInt3 = jSONObject2.optInt(Tag.TagPhone.ATD_CAT_ID);
                    int optInt4 = jSONObject2.optInt(Tag.TagPhone.MARKED_COUNT);
                    int optInt5 = jSONObject2.optInt("provider");
                    int optInt6 = jSONObject2.optInt("flag");
                    boolean z10 = jSONObject2.getInt("hide") == 0;
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                    }
                    ArrayList arrayList4 = arrayList3;
                    String str24 = optString17;
                    String str25 = str21;
                    String str26 = str22;
                    int i6 = i3;
                    YellowPagePhone yellowPagePhone = new YellowPagePhone(j2, string, string3, string2, optString19, 1, i6, optInt4, z10, str26, string4, has);
                    yellowPagePhone.setT9Rank(optLong);
                    yellowPagePhone.setRawSlogan(str24);
                    yellowPagePhone.setCid(optInt3);
                    yellowPagePhone.setFlag(optInt6);
                    yellowPagePhone.setAntispamProviderId(optInt5);
                    yellowPagePhone.setCreditImg(str25);
                    arrayList4.add(yellowPagePhone);
                    i4 = i5 + 1;
                    str22 = str26;
                    str21 = str25;
                    arrayList3 = arrayList4;
                    string = string;
                    i3 = i6;
                    optJSONArray = jSONArray;
                    str19 = str23;
                    optString15 = optString15;
                    str20 = str20;
                    optString10 = optString10;
                    optInt2 = optInt2;
                    optString11 = optString11;
                    optString12 = optString12;
                    optString13 = optString13;
                    optString18 = optString18;
                    z7 = z7;
                    optString7 = optString7;
                    z4 = z9;
                    optString5 = optString5;
                    optString4 = optString4;
                    optString3 = optString3;
                    optString2 = optString2;
                    z8 = z8;
                    j2 = j2;
                    optString17 = str24;
                    optString9 = optString9;
                    optString14 = optString14;
                }
                z2 = z4;
                str = optString7;
                str2 = optString5;
                str3 = optString4;
                str4 = optString3;
                str5 = optString2;
                j = j2;
                str6 = str20;
                str7 = optString10;
                i = optInt2;
                str8 = optString11;
                str9 = optString12;
                str10 = optString13;
                str11 = optString14;
                str12 = optString15;
                str13 = optString18;
                str14 = str22;
                z3 = z8;
                z = z7;
                str15 = optString9;
                str16 = string;
                str17 = optString17;
                str18 = str21;
                i2 = i3;
                arrayList = arrayList3;
            } else {
                z = z5;
                z2 = z4;
                str = optString7;
                str2 = optString5;
                str3 = optString4;
                str4 = optString3;
                str5 = optString2;
                z3 = z6;
                j = j2;
                str6 = str20;
                str7 = optString10;
                i = optInt2;
                str8 = optString11;
                str9 = optString12;
                str10 = optString13;
                str11 = optString14;
                str12 = optString15;
                str13 = optString18;
                str14 = str22;
                str15 = optString9;
                str16 = string;
                str17 = optString17;
                str18 = str21;
                i2 = i3;
                arrayList = null;
            }
            JSONArray optJSONArray2 = jSONObject.optJSONArray(Tag.TagYellowPage.SOCIAL);
            if (optJSONArray2 == null || optJSONArray2.length() <= 0) {
                arrayList2 = null;
            } else {
                arrayList2 = new ArrayList();
                for (int i7 = 0; i7 < optJSONArray2.length(); i7++) {
                    JSONObject jSONObject3 = optJSONArray2.getJSONObject(i7);
                    arrayList2.add(new Social(jSONObject3.getString("url"), jSONObject3.getString("name"), jSONObject3.optInt("provider")));
                }
            }
            ArrayList arrayList5 = new ArrayList();
            JSONArray optJSONArray3 = jSONObject.optJSONArray(Tag.TagYellowPage.PROVIDER_LIST);
            if (optJSONArray3 != null) {
                for (int i8 = 0; i8 < optJSONArray3.length(); i8++) {
                    Provider fromJson = Provider.fromJson(optJSONArray3.getJSONObject(i8));
                    if (fromJson != null) {
                        arrayList5.add(fromJson);
                    }
                }
            }
            String optString20 = jSONObject.optString(Tag.TagYellowPage.PHOTO);
            String optString21 = jSONObject.optString(Tag.TagYellowPage.THUMBNAIL);
            return new YellowPage().setId(j).setName(str16).setPinyin(str14).setBrief(str15).setAlias(str5).setAddress(str4).setPhones(arrayList).setSocials(arrayList2).setThumbnailName(optString21).setPhotoName(optString20).setProviderId(i2).setUrl(str3).setFirmUrl(str2).setCreditImg(str18).setSourceUrl(str).setSourceId(str6).setIsMasterPage(z2).setIsPreset(z3).setIsHot(z).setHotCatId(str7).setHotSort(i).setCatId(str8).setLocId(str9).setLongitude(str10).setLatitude(str11).setContent(jSONObject.toString()).setSlogan(str17).setProviderList(arrayList5).setMiId(TextUtils.isEmpty(optString16) ? str12 : str12 + "/" + optString16).setAuthIconName(str13).setExtraData(jSONObject.optString("extraData"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private YellowPage setContent(String str) {
        this.mContent = str;
        return this;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getAlias() {
        return this.mAlias;
    }

    public String getAuthIconName() {
        return this.mAuthIconName;
    }

    public String getBrief() {
        return this.mBrief;
    }

    public String getCatId() {
        return this.mCatId;
    }

    public String getContent() {
        return this.mContent;
    }

    public String getCreditImg() {
        return this.mCreditImg;
    }

    public String getExtraData() {
        return this.mExtraData;
    }

    public String getFirmUrl() {
        return this.mFirmUrl;
    }

    public List<String> getGallery() {
        return this.mGallery;
    }

    public String getHotCatId() {
        String str = this.mHotCatId;
        return str == null ? "" : str;
    }

    public int getHotSort() {
        return this.mHotSort;
    }

    public long getId() {
        return this.mYid;
    }

    public String getLatitude() {
        return this.mLatitude;
    }

    public String getLocId() {
        return this.mLocId;
    }

    public String getLongitude() {
        return this.mLongitude;
    }

    public String getMiId() {
        return this.mMiId;
    }

    public String getName() {
        return this.mName;
    }

    public YellowPagePhone getPhoneInfo(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String normalizedNumber = YellowPageUtils.getNormalizedNumber(context, str);
        List<YellowPagePhone> list = this.mPhones;
        if (list != null) {
            for (YellowPagePhone yellowPagePhone : list) {
                if (TextUtils.equals(normalizedNumber, yellowPagePhone.getNormalizedNumber())) {
                    return yellowPagePhone;
                }
            }
        }
        return null;
    }

    public List<YellowPagePhone> getPhones() {
        return this.mPhones;
    }

    public byte[] getPhoto() {
        return null;
    }

    public String getPhotoName() {
        return this.mPhotoUrl;
    }

    public String getPinyin() {
        return this.mPinyin;
    }

    public Bitmap getProviderIcon(Context context) {
        return YellowPageUtils.getProvider(context, this.mProviderId).getIcon();
    }

    public int getProviderId() {
        return this.mProviderId;
    }

    public List<Provider> getProviderList() {
        return this.mProviders;
    }

    public String getProviderName(Context context) {
        return YellowPageUtils.getProvider(context, this.mProviderId).getName();
    }

    public String getSlogan() {
        return this.mSlogan;
    }

    public List<Social> getSocials() {
        return this.mSocials;
    }

    public String getSourceId() {
        return this.mSourceId;
    }

    public String getSourceUrl() {
        return this.mSourceUrl;
    }

    public byte[] getThumbnail() {
        return null;
    }

    public String getThumbnailName() {
        return this.mThumbnailUrl;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public boolean isHot() {
        return this.mIsHot;
    }

    public boolean isMasterPage() {
        return this.mIsMasterPage;
    }

    public boolean isPreset() {
        return this.mIsPreset;
    }

    public boolean isProviderMiui() {
        return this.mProviderId == 0;
    }

    public YellowPage setAddress(String str) {
        this.mAddress = str;
        return this;
    }

    public YellowPage setAlias(String str) {
        this.mAlias = str;
        return this;
    }

    public YellowPage setAuthIconName(String str) {
        this.mAuthIconName = str;
        return this;
    }

    public YellowPage setBrief(String str) {
        this.mBrief = str;
        return this;
    }

    public YellowPage setCatId(String str) {
        this.mCatId = str;
        return this;
    }

    public YellowPage setCreditImg(String str) {
        this.mCreditImg = str;
        return this;
    }

    public YellowPage setExtraData(String str) {
        this.mExtraData = str;
        return this;
    }

    public YellowPage setFirmUrl(String str) {
        this.mFirmUrl = str;
        return this;
    }

    public YellowPage setGallery(List<String> list) {
        this.mGallery = list;
        return this;
    }

    public YellowPage setHotCatId(String str) {
        this.mHotCatId = str;
        return this;
    }

    public YellowPage setHotSort(int i) {
        this.mHotSort = i;
        return this;
    }

    public YellowPage setId(long j) {
        this.mYid = j;
        return this;
    }

    public YellowPage setIsHot(boolean z) {
        this.mIsHot = z;
        return this;
    }

    public YellowPage setIsMasterPage(boolean z) {
        this.mIsMasterPage = z;
        return this;
    }

    public YellowPage setIsPreset(boolean z) {
        this.mIsPreset = z;
        return this;
    }

    public YellowPage setLatitude(String str) {
        this.mLatitude = str;
        return this;
    }

    public YellowPage setLocId(String str) {
        this.mLocId = str;
        return this;
    }

    public YellowPage setLongitude(String str) {
        this.mLongitude = str;
        return this;
    }

    public YellowPage setMiId(String str) {
        this.mMiId = str;
        return this;
    }

    public YellowPage setName(String str) {
        this.mName = str;
        return this;
    }

    public YellowPage setPhones(List<YellowPagePhone> list) {
        this.mPhones = list;
        return this;
    }

    public YellowPage setPhotoName(String str) {
        this.mPhotoUrl = str;
        return this;
    }

    public YellowPage setPinyin(String str) {
        this.mPinyin = str;
        return this;
    }

    public YellowPage setProviderId(int i) {
        this.mProviderId = i;
        return this;
    }

    public YellowPage setProviderList(List<Provider> list) {
        this.mProviders = list;
        return this;
    }

    public YellowPage setSlogan(String str) {
        this.mSlogan = str;
        return this;
    }

    public YellowPage setSocials(List<Social> list) {
        this.mSocials = list;
        return this;
    }

    public YellowPage setSourceId(String str) {
        this.mSourceId = str;
        return this;
    }

    public YellowPage setSourceUrl(String str) {
        this.mSourceUrl = str;
        return this;
    }

    public YellowPage setThumbnailName(String str) {
        this.mThumbnailUrl = str;
        return this;
    }

    public YellowPage setUrl(String str) {
        this.mUrl = str;
        return this;
    }
}
