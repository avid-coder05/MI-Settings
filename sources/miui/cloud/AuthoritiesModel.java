package miui.cloud;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncAdapterType;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.cloud.os.MultiuserUtils;
import miui.cloud.sync.providers.BrowserSyncInfoProvider;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;
import miui.cloud.sync.providers.CalllogSyncInfoProvider;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.cloud.sync.providers.GlobalBrowserSyncInfoProvider;
import miui.cloud.sync.providers.MusicSyncInfoProvider;
import miui.cloud.util.BuildUtil;
import miui.cloud.util.DeviceFeatureUtils;
import miui.cloud.util.PkgInfoUtil;
import miui.cloud.util.SysHelper;

/* loaded from: classes3.dex */
public class AuthoritiesModel {
    private static final HashMap<String, String> AUTHORITY_TO_MIUI_APP_PKG_MAP;
    private static final HashMap<String, String[]> AUTHORITY_TO_PKG_MAP;
    private static final String DEFAULT_TYPE_XIAOMI_ACCOUNT = "com.xiaomi";
    private static final String FEATURE_SUPPORT_GOOGLE_CSP_SYNC = "support_google_csp_sync";
    public static final IFilter UNAVAILABLE_AUTHORITIES_FILTER;
    private final String mAccountType;
    private List<String> mAuthorities;
    private final Context mContext;

    /* loaded from: classes3.dex */
    public interface IFilter {
        void filter(Context context, List<String> list);
    }

    /* loaded from: classes3.dex */
    public interface ISorter {
        void sort(Context context, List<String> list);
    }

    /* loaded from: classes3.dex */
    private static class UnAvailableAuthoritiesFilter implements IFilter {
        private UnAvailableAuthoritiesFilter() {
        }

        private List<String> getPkgInfoNotExistAuthorities(Context context) {
            ArrayList arrayList = new ArrayList();
            for (Map.Entry entry : AuthoritiesModel.AUTHORITY_TO_PKG_MAP.entrySet()) {
                String str = (String) entry.getKey();
                String[] strArr = (String[]) entry.getValue();
                int length = strArr.length;
                boolean z = false;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    } else if (PkgInfoUtil.isPkgExist(context, strArr[i])) {
                        z = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (!z) {
                    arrayList.add(str);
                }
            }
            return arrayList;
        }

        @Override // miui.cloud.AuthoritiesModel.IFilter
        public void filter(Context context, List<String> list) {
            list.remove(CloudSyncUtils.MMSLITE_PROVIDER_AUTHORITY);
            list.remove(MusicSyncInfoProvider.AUTHORITY);
            list.remove("com.miui.player");
            if (!DeviceFeatureUtils.hasDeviceFeature("support_google_csp_sync")) {
                for (String str : AuthoritiesModel.AUTHORITY_TO_MIUI_APP_PKG_MAP.keySet()) {
                    if (!AuthoritiesModel.isMiuiAppAuthority(context, str)) {
                        list.remove(str);
                    }
                }
            }
            list.removeAll(getPkgInfoNotExistAuthorities(context));
            if (!SysHelper.hasSmsCapability(context)) {
                list.remove("sms");
            }
            if (!SysHelper.hasVoiceCapability(context)) {
                list.remove(CalllogSyncInfoProvider.AUTHORITY);
            }
            if (!SysHelper.hasModemCapability()) {
                list.remove("antispam");
            }
            if (BuildUtil.isInternationalBuild()) {
                list.remove("antispam");
            }
            if (MultiuserUtils.myUserId() != MultiuserUtils.get_USER_OWNER()) {
                list.remove("antispam");
                list.remove("sms");
            }
            if (list.contains(BrowserSyncInfoProvider.AUTHORITY) && list.contains(GlobalBrowserSyncInfoProvider.AUTHORITY)) {
                if (BuildUtil.isInternationalBuild()) {
                    list.remove(BrowserSyncInfoProvider.AUTHORITY);
                } else {
                    list.remove(GlobalBrowserSyncInfoProvider.AUTHORITY);
                }
            }
        }
    }

    static {
        HashMap<String, String> hashMap = new HashMap<>();
        AUTHORITY_TO_MIUI_APP_PKG_MAP = hashMap;
        HashMap<String, String[]> hashMap2 = new HashMap<>();
        AUTHORITY_TO_PKG_MAP = hashMap2;
        hashMap.put("sms", "com.android.mms");
        hashMap.put(ContactsSyncInfoProvider.AUTHORITY, ContactsSyncInfoProvider.AUTHORITY);
        hashMap.put(CalllogSyncInfoProvider.AUTHORITY, ContactsSyncInfoProvider.AUTHORITY);
        hashMap2.put(CalendarSyncInfoProvider.AUTHORITY, new String[]{CalendarSyncInfoProvider.AUTHORITY, "com.xiaomi.calendar"});
        UNAVAILABLE_AUTHORITIES_FILTER = new UnAvailableAuthoritiesFilter();
    }

    public AuthoritiesModel(Context context) {
        this.mAuthorities = new ArrayList();
        this.mContext = context;
        this.mAccountType = "com.xiaomi";
    }

    public AuthoritiesModel(Context context, Account account) {
        this.mAuthorities = new ArrayList();
        this.mContext = context;
        this.mAccountType = account.type;
    }

    public AuthoritiesModel(Context context, Account account, List<String> list) {
        ArrayList arrayList = new ArrayList();
        this.mAuthorities = arrayList;
        this.mContext = context;
        this.mAccountType = account.type;
        arrayList.addAll(list);
    }

    public static boolean isMiuiAppAuthority(Context context, String str) {
        String str2 = AUTHORITY_TO_MIUI_APP_PKG_MAP.get(str);
        return str2 == null || PkgInfoUtil.isPkgExist(context, str2);
    }

    public AuthoritiesModel filter(List<String> list) {
        this.mAuthorities.removeAll(list);
        return this;
    }

    public AuthoritiesModel filter(String[] strArr) {
        return filter(Arrays.asList(strArr));
    }

    public AuthoritiesModel filterBy(IFilter iFilter) {
        iFilter.filter(this.mContext, this.mAuthorities);
        return this;
    }

    public AuthoritiesModel getAllAuthorities() {
        for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypes()) {
            if (syncAdapterType.isUserVisible() && TextUtils.equals(this.mAccountType, syncAdapterType.accountType)) {
                String str = syncAdapterType.authority;
                if (this.mContext.getPackageManager().resolveContentProvider(str, 0) != null) {
                    this.mAuthorities.add(str);
                }
            }
        }
        return this;
    }

    public AuthoritiesModel sortBy(ISorter iSorter) {
        iSorter.sort(this.mContext, this.mAuthorities);
        return this;
    }

    public List<String> toList() {
        return this.mAuthorities;
    }
}
