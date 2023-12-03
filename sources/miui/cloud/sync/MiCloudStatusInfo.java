package miui.cloud.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.micloudsdk.cloudinfo.utils.CloudInfoUtils;
import java.util.ArrayList;
import java.util.Map;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class MiCloudStatusInfo {
    private static final String TAG = "MiCloudStatusInfo";
    private QuotaInfo mQuotaInfo;
    private String mUserId;
    private boolean mVipEnable;

    /* loaded from: classes3.dex */
    public class ItemInfo {
        private String mLocalizedName;
        private String mName;
        private long mUsed;

        public ItemInfo(String str, String str2, long j) {
            this.mName = str;
            this.mLocalizedName = str2;
            this.mUsed = j;
        }

        public String getLocalizedName() {
            return this.mLocalizedName;
        }

        public String getName() {
            return this.mName;
        }

        public long getUsed() {
            return this.mUsed;
        }

        public String toString() {
            return "ItemInfo{mName=" + this.mName + ", mLocalizedName=" + this.mLocalizedName + ", mUsed='" + this.mUsed + '}';
        }
    }

    /* loaded from: classes3.dex */
    public class QuotaInfo {
        public static final String WARN_FULL = "full";
        public static final String WARN_LOW_PERCENT = "low_percent";
        public static final String WARN_NONE = "none";
        private ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
        private long mTotal;
        private long mUsed;
        private String mWarn;
        private long mYearlyPackageCreateTime;
        private long mYearlyPackageExpireTime;
        private long mYearlyPackageSize;
        private String mYearlyPackageType;

        public QuotaInfo(long j, long j2, String str, String str2, long j3, long j4, long j5) {
            this.mTotal = j;
            this.mUsed = j2;
            this.mWarn = str;
            this.mYearlyPackageType = str2;
            this.mYearlyPackageSize = j3;
            this.mYearlyPackageCreateTime = j4;
            this.mYearlyPackageExpireTime = j5;
        }

        public void addItemInfo(ItemInfo itemInfo) {
            this.mItemInfoList.add(itemInfo);
        }

        public ArrayList<ItemInfo> getItemInfoList() {
            return this.mItemInfoList;
        }

        public long getTotal() {
            return this.mTotal;
        }

        public long getUsed() {
            return this.mUsed;
        }

        public String getWarn() {
            return this.mWarn;
        }

        public long getYearlyPackageCreateTime() {
            return this.mYearlyPackageCreateTime;
        }

        public long getYearlyPackageExpireTime() {
            return this.mYearlyPackageExpireTime;
        }

        public long getYearlyPackageSize() {
            return this.mYearlyPackageSize;
        }

        public String getYearlyPackageType() {
            return this.mYearlyPackageType;
        }

        public boolean isSpaceFull() {
            return WARN_FULL.equals(getWarn());
        }

        public boolean isSpaceLowPercent() {
            return WARN_LOW_PERCENT.equals(getWarn());
        }

        public String toString() {
            return "QuotaInfo{mTotal=" + this.mTotal + ", mUsed=" + this.mUsed + ", mWarn='" + this.mWarn + "', mYearlyPackageType='" + this.mYearlyPackageType + "', mYearlyPackageSize=" + this.mYearlyPackageSize + ", mYearlyPackageCreateTime=" + this.mYearlyPackageCreateTime + ", mYearlyPackageExpireTime=" + this.mYearlyPackageExpireTime + ", mItemInfoList=" + this.mItemInfoList + '}';
        }
    }

    public MiCloudStatusInfo(String str) {
        this.mUserId = str;
    }

    public static MiCloudStatusInfo fromProviderOrNull(Context context, Account account) {
        Cursor query = context.getContentResolver().query(Uri.parse("content://com.miui.micloud/status_info"), null, null, null, null);
        if (query != null) {
            try {
                query.moveToFirst();
                String string = query.getString(query.getColumnIndex("column_status_info"));
                String string2 = query.getString(query.getColumnIndex("column_status_info_user_id"));
                if (!TextUtils.isEmpty(string) && TextUtils.equals(string2, account.name)) {
                    MiCloudStatusInfo miCloudStatusInfo = new MiCloudStatusInfo(account.name);
                    miCloudStatusInfo.parseQuotaString(string);
                    return miCloudStatusInfo;
                }
            } finally {
                query.close();
            }
        }
        if (query != null) {
        }
        return null;
    }

    public static MiCloudStatusInfo fromUserData(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(context);
        if (xiaomiAccount == null) {
            return null;
        }
        String userData = accountManager.getUserData(xiaomiAccount, Constants.UserData.EXTRA_MICLOUD_STATUS_INFO_QUOTA);
        MiCloudStatusInfo miCloudStatusInfo = new MiCloudStatusInfo(xiaomiAccount.name);
        miCloudStatusInfo.parseQuotaString(userData);
        QuotaInfo quotaInfo = miCloudStatusInfo.getQuotaInfo();
        if (quotaInfo == null || quotaInfo.getWarn() == null) {
            Log.w(TAG, "deserialize failed");
            accountManager.setUserData(xiaomiAccount, Constants.UserData.EXTRA_MICLOUD_STATUS_INFO_QUOTA, "");
        }
        return miCloudStatusInfo;
    }

    private ItemInfo mapToItemInfo(String str, Map map) {
        Object obj = map.get("localized_name");
        String str2 = obj instanceof String ? (String) obj : "";
        Object obj2 = map.get("used");
        return new ItemInfo(str, str2, obj2 instanceof Integer ? ((Integer) obj2).intValue() : obj2 instanceof Long ? ((Long) obj2).longValue() : 0L);
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0045  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0049  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0055  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0079  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x008d  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00a7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private miui.cloud.sync.MiCloudStatusInfo.QuotaInfo mapToQuotaInfo(java.util.Map r20) {
        /*
            r19 = this;
            r0 = r20
            java.lang.String r1 = "total"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Long
            r3 = 0
            if (r2 == 0) goto L17
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r7 = r1
            goto L18
        L17:
            r7 = r3
        L18:
            java.lang.String r1 = "used"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Integer
            if (r2 == 0) goto L2c
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            long r1 = (long) r1
        L2a:
            r9 = r1
            goto L38
        L2c:
            boolean r2 = r1 instanceof java.lang.Long
            if (r2 == 0) goto L37
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            goto L2a
        L37:
            r9 = r3
        L38:
            java.lang.String r1 = "warn"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.String
            java.lang.String r5 = ""
            if (r2 == 0) goto L49
            java.lang.String r1 = (java.lang.String) r1
            r11 = r1
            goto L4a
        L49:
            r11 = r5
        L4a:
            java.lang.String r1 = "yearlyPackageType"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.String
            if (r2 == 0) goto L59
            java.lang.String r1 = (java.lang.String) r1
            r12 = r1
            goto L5a
        L59:
            r12 = r5
        L5a:
            java.lang.String r1 = "yearlyPackageSize"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Long
            if (r2 == 0) goto L6d
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r13 = r1
            goto L6e
        L6d:
            r13 = r3
        L6e:
            java.lang.String r1 = "yearlyPackageCreateTime"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Long
            if (r2 == 0) goto L81
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r15 = r1
            goto L82
        L81:
            r15 = r3
        L82:
            java.lang.String r1 = "yearlyPackageExpireTime"
            java.lang.Object r1 = r0.get(r1)
            boolean r2 = r1 instanceof java.lang.Long
            if (r2 == 0) goto L93
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
        L93:
            r17 = r3
            miui.cloud.sync.MiCloudStatusInfo$QuotaInfo r1 = new miui.cloud.sync.MiCloudStatusInfo$QuotaInfo
            r5 = r1
            r6 = r19
            r5.<init>(r7, r9, r11, r12, r13, r15, r17)
            java.lang.String r2 = "items"
            java.lang.Object r0 = r0.get(r2)
            boolean r2 = r0 instanceof java.util.Map
            if (r2 == 0) goto Lcd
            java.util.Map r0 = (java.util.Map) r0
            java.util.Set r2 = r0.keySet()
            java.util.Iterator r2 = r2.iterator()
        Lb1:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto Lcd
            java.lang.Object r3 = r2.next()
            java.lang.String r3 = (java.lang.String) r3
            java.lang.Object r4 = r0.get(r3)
            java.util.Map r4 = (java.util.Map) r4
            r5 = r19
            miui.cloud.sync.MiCloudStatusInfo$ItemInfo r3 = r5.mapToItemInfo(r3, r4)
            r1.addItemInfo(r3)
            goto Lb1
        Lcd:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.cloud.sync.MiCloudStatusInfo.mapToQuotaInfo(java.util.Map):miui.cloud.sync.MiCloudStatusInfo$QuotaInfo");
    }

    public QuotaInfo getQuotaInfo() {
        return this.mQuotaInfo;
    }

    public String getUserId() {
        return this.mUserId;
    }

    public boolean isVIPAvailable() {
        return this.mVipEnable;
    }

    public void parseMap(Map map) {
        Object obj = map.get("quota");
        if (obj instanceof Map) {
            this.mQuotaInfo = mapToQuotaInfo((Map) obj);
        }
        Object obj2 = map.get("VIPAvailable");
        if (obj2 instanceof Boolean) {
            this.mVipEnable = ((Boolean) obj2).booleanValue();
        }
    }

    public void parseQuotaString(String str) {
        if (TextUtils.isEmpty(str)) {
            Log.e(TAG, "parseQuotaString() quota is empty.");
            this.mQuotaInfo = null;
            return;
        }
        try {
            this.mQuotaInfo = CloudInfoUtils.getQuotaInfo(this, new JSONObject(str));
        } catch (JSONException unused) {
            Log.e(TAG, "catch JSONException in parseQuotaString()");
            this.mQuotaInfo = null;
        }
    }

    public String parseToQuotaInfo() throws JSONException {
        QuotaInfo quotaInfo = this.mQuotaInfo;
        if (quotaInfo == null) {
            Log.e(TAG, "parseToQuotaInfo() mQuotaInfo is null.");
            return "";
        }
        return CloudInfoUtils.toJSONObject(quotaInfo).toString();
    }
}
