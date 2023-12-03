package miui.vip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.miui.internal.vip.VipResInputStream;
import com.miui.internal.vip.utils.ImageDownloader;
import com.miui.internal.vip.utils.RunnableHelper;
import com.miui.internal.vip.utils.Utils;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import miui.accounts.ExtraAccountManager;
import miui.util.AppConstants;

/* loaded from: classes4.dex */
public class VipWebClient extends WebViewClient {
    static final String ACCOUNT_AVATAR = "account_avatar/";
    static final String ACCOUNT_CALLBACK = "accountCallback";
    static final String ACCOUNT_FIELD = "account";
    static final String ACHIEVEMENT_CALLBACK = "achievementCallback";
    static final String ARRAY_BEGIN = "[";
    static final String ARRAY_END = "]";
    static final String COMMA = ",";
    static final String CONNECT_SERVICE = "connect_service";
    static final String DEFAULT_AVATAR = "http://request_vip_icon/default_photo";
    static final int DISPLAY_ACHIEVEMENTS_COUNT = 4;
    static final String JS_ACCOUNT = "{id:%s, name:'%s', avatarUrl:'%s'}";
    static final String JS_ACHIEVEMENT = "{id:%d, name:'%s', url:'%s', isOwned:%s}";
    static final String JS_INIT = "if (!window.XiaomiVipClient) {   window.XiaomiVipClient = {ICON_ACHIEVEMENT_LOCK: 'http://request_vip_icon/achievement_lock',vipUser: %s,achievements: %s,account: %s,setVipInfoCallback: function(callback) {   this.vipCallback = callback;},setAchievementCallback: function(callback) {   this.achievementCallback = callback;},setAccountCallback: function(callback) {   this.accountCallback = callback;},openVipTaskView: function() {   this.loadUrl('http://vip_view/vip_view_task');},openVipLevelView: function() {   this.loadUrl('http://vip_view/vip_view_level');},openVipAchievementView: function() {   this.loadUrl('http://vip_view/vip_view_achievements');},openUserDetailView: function() {   this.loadUrl('http://vip_view/user_detail');},loadUrl: function(url) {    var xhr = new XMLHttpRequest();    xhr.open('GET', url, true);    xhr.send();}};} else {   console.log('XiaomiVipCient is already initialized');}XiaomiVipClient.loadUrl('http://vip_view/connect_service?refresh=' + (!XiaomiVipClient.vipUser));console.log('initialization of XiaomiVipCient is completed');";
    static final String JS_UPDATE = "(function(){   var funcName = '%s';   var vName = '%s';   var args = %s;   if (window.XiaomiVipClient) {       XiaomiVipClient[vName] = args;       if (typeof XiaomiVipClient[funcName] == 'function') {           console.log('VipWebClient invokes ' + funcName);           XiaomiVipClient[funcName](args);       }   }})();";
    static final String JS_USER = "{id:%d, level:%d, badgeUrl:'%s'}";
    static final String JS_VIP_CLIENT = "window.XiaomiVipClient = {ICON_ACHIEVEMENT_LOCK: 'http://request_vip_icon/achievement_lock',vipUser: %s,achievements: %s,account: %s,setVipInfoCallback: function(callback) {   this.vipCallback = callback;},setAchievementCallback: function(callback) {   this.achievementCallback = callback;},setAccountCallback: function(callback) {   this.accountCallback = callback;},openVipTaskView: function() {   this.loadUrl('http://vip_view/vip_view_task');},openVipLevelView: function() {   this.loadUrl('http://vip_view/vip_view_level');},openVipAchievementView: function() {   this.loadUrl('http://vip_view/vip_view_achievements');},openUserDetailView: function() {   this.loadUrl('http://vip_view/user_detail');},loadUrl: function(url) {    var xhr = new XMLHttpRequest();    xhr.open('GET', url, true);    xhr.send();}};";
    static final String NULL_STR = "null";
    static final String PARAM_REFRESH = "refresh";
    static final String Q_MARK = "?";
    static final String SCHEMA_VIP_ICON = "http://request_vip_icon/";
    static final String SCHEMA_VIP_VIEW = "http://vip_view/";
    static final String VALUE_TRUE = "true";
    static final String VIEW_USER_DETAIL = "user_detail";
    static final String VIP_ACHIEVEMENTS_FIELD = "achievements";
    static final String VIP_CALLBACK = "vipCallback";
    static final String VIP_USER_FIELD = "vipUser";
    static final String VIP_VIEW_ACHIEVEMENTS = "vip_view_achievements";
    static final String VIP_VIEW_LEVEL = "vip_view_level";
    static final String VIP_VIEW_TASK = "vip_view_task";
    AccountInfo mAccount;
    volatile List<VipAchievement> mCachedAchievements;
    volatile String mStrAccount;
    volatile String mStrUser;
    volatile VipUserInfo mUser;
    final CopyOnWriteArraySet<String> mLoadingJsSet = new CopyOnWriteArraySet<>();
    AtomicReference<WebView> mWebView = new AtomicReference<>();
    ImageDownloader.FileDownloadListener mAvatarListener = new ImageDownloader.FileDownloadListener(new ImageDownloader.OnFileDownload() { // from class: miui.vip.VipWebClient.1
        public void onDownload(String str) {
            String str2;
            String str3;
            String str4;
            if (TextUtils.isEmpty(str)) {
                return;
            }
            synchronized (VipWebClient.this) {
                AccountInfo accountInfo = VipWebClient.this.mAccount;
                str2 = null;
                if (accountInfo != null) {
                    str2 = accountInfo.id;
                    str4 = accountInfo.userName;
                    str3 = Utils.getName(accountInfo.avatarUrl);
                } else {
                    str3 = null;
                    str4 = null;
                }
            }
            if (str2 != null) {
                VipWebClient vipWebClient = VipWebClient.this;
                vipWebClient.invokeJsAccountUpdate(str2, str4, vipWebClient.getAccountAvatarWebUrl(str3));
            }
        }
    });
    final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: miui.vip.VipWebClient.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Utils.log("VipWebClient.mReceiver.onReceive, action = %s", new Object[]{action});
            if (!TextUtils.equals(action, ExtraAccountManager.LOGIN_ACCOUNTS_POST_CHANGED_ACTION)) {
                VipWebClient.this.notifyAccountUpdate();
                return;
            }
            boolean hasAccount = Utils.hasAccount();
            Object[] objArr = new Object[1];
            objArr[0] = hasAccount ? "remove" : "add";
            Utils.log("VipWebClient.mReceiver.onReceive, login accounts changed, %s", objArr);
            if (hasAccount) {
                VipService.instance().connect(VipWebClient.this.mVipCallback);
                return;
            }
            VipWebClient.this.batchNotify(null, null);
            VipService.instance().disconnect(VipWebClient.this.mVipCallback);
        }
    };
    QueryCallback mVipCallback = new QueryCallback(16) { // from class: miui.vip.VipWebClient.3
        @Override // miui.vip.QueryCallback
        public void onAchievements(int i, List<VipAchievement> list, String str) {
            if (i == 0) {
                VipWebClient.this.notifyAchievementsUpdate(list);
            }
        }

        @Override // miui.vip.QueryCallback
        public void onConnected(boolean z, VipUserInfo vipUserInfo, List<VipAchievement> list) {
            Utils.log("VipWebClient.interceptRequest, onConnected, notify", new Object[0]);
            VipWebClient.this.batchNotify(vipUserInfo, list);
        }

        @Override // miui.vip.QueryCallback
        public void onUserInfo(int i, VipUserInfo vipUserInfo, String str) {
            if (i == 0) {
                VipWebClient.this.notifyVipUserUpdate(vipUserInfo);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class AccountInfo {
        public String avatarFileName;
        public String avatarUrl;
        public String id;
        public String userName;

        AccountInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class UrlParameters {
        HashMap<String, String> params = new HashMap<>();
        String path;

        UrlParameters() {
        }

        private StringBuilder mapToString(HashMap<String, String> hashMap) {
            StringBuilder sb = new StringBuilder("{");
            int length = sb.length();
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                if (sb.length() > length) {
                    sb.append(", ");
                }
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(entry.getValue());
            }
            sb.append("}");
            return sb;
        }

        public String toString() {
            return "UrlParameters{path='" + this.path + "', params=" + ((Object) mapToString(this.params)) + '}';
        }
    }

    private String achievementToJs(List<VipAchievement> list) {
        StringBuilder sb = new StringBuilder(ARRAY_BEGIN);
        int length = sb.length();
        if (list != null) {
            int min = Math.min(4, list.size());
            for (int i = 0; i < min; i++) {
                VipAchievement vipAchievement = list.get(i);
                if (sb.length() > length) {
                    sb.append(COMMA);
                }
                sb.append(format(JS_ACHIEVEMENT, Long.valueOf(vipAchievement.id), vipAchievement.name, vipAchievement.url, String.valueOf(vipAchievement.isOwned)));
            }
        }
        sb.append(ARRAY_END);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void batchNotify(VipUserInfo vipUserInfo, List<VipAchievement> list) {
        notifyVipUserUpdate(vipUserInfo);
        notifyAchievementsUpdate(list);
        notifyAccountUpdate();
    }

    private boolean connectService() {
        Utils.log("VipWebClient.connectService begin, mStrUser = %s", new Object[]{this.mStrUser});
        if (VipService.instance().isConnected() && !TextUtils.isEmpty(this.mStrUser)) {
            Utils.log("VipWebClient.connectService, no need to connect", new Object[0]);
            return false;
        }
        Utils.log("VipWebClient.connectService, do connection", new Object[0]);
        VipService.instance().connect(this.mVipCallback);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doLoadJs(String str) {
        WebView webView = this.mWebView.get();
        if (webView != null) {
            webView.loadUrl("javascript: " + str);
        }
        this.mLoadingJsSet.remove(str);
    }

    private String format(String str, Object... objArr) {
        return String.format(Locale.ENGLISH, str, objArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getAccountAvatarWebUrl(String str) {
        return "http://request_vip_icon/account_avatar/" + str;
    }

    private String getActionFromPath(String str) {
        if (TextUtils.equals(str, VIP_VIEW_LEVEL)) {
            return VipService.ACTION_VIP_LEVEL_LIST;
        }
        if (TextUtils.equals(str, VIP_VIEW_TASK)) {
            return VipService.ACTION_VIP_TASK;
        }
        if (TextUtils.equals(str, VIP_VIEW_ACHIEVEMENTS)) {
            return VipService.ACTION_VIP_ACHIEVEMENTS;
        }
        if (TextUtils.equals(str, VIEW_USER_DETAIL)) {
            return "com.xiaomi.account.action.USER_INFO_DETAIL";
        }
        return null;
    }

    private Context getContext() {
        return AppConstants.getCurrentApplication().getApplicationContext();
    }

    private void initWithData(WebView webView) {
        WebView webView2 = this.mWebView.get();
        if (webView != webView2) {
            this.mWebView.compareAndSet(webView2, webView);
        }
        Utils.log("VipWebClient.initWithData, init XiaomiVipClient", new Object[0]);
        Object[] objArr = new Object[3];
        boolean isEmpty = TextUtils.isEmpty(this.mStrUser);
        String str = NULL_STR;
        objArr[0] = isEmpty ? NULL_STR : this.mStrUser;
        objArr[1] = this.mCachedAchievements == null ? NULL_STR : achievementToJs(this.mCachedAchievements);
        if (!TextUtils.isEmpty(this.mStrAccount)) {
            str = this.mStrAccount;
        }
        objArr[2] = str;
        loadJs(format(JS_INIT, objArr));
    }

    private WebResourceResponse interceptRequest(WebView webView, String str) {
        InputStream vipResInputStream;
        Utils.log("interceptRequest, url = %s", new Object[]{str});
        if (str.startsWith(SCHEMA_VIP_ICON)) {
            String substring = str.substring(24);
            if (substring.startsWith(ACCOUNT_AVATAR)) {
                vipResInputStream = loadAccountAvatar();
            } else {
                vipResInputStream = new VipResInputStream(substring, this.mUser != null ? this.mUser.level : 0);
            }
            Utils.log("VipWebClient.shouldInterceptRequest, vip_icon, is = %s", new Object[]{vipResInputStream});
            if (vipResInputStream != null) {
                return new WebResourceResponse("image/*", "base64", vipResInputStream);
            }
        } else if (str.startsWith(SCHEMA_VIP_VIEW)) {
            UrlParameters parseUrl = parseUrl(str);
            Utils.log("VipWebClient.interceptRequest, cmd = %s", new Object[]{parseUrl});
            if (TextUtils.equals(CONNECT_SERVICE, parseUrl.path)) {
                boolean isValueTrue = isValueTrue(parseUrl, PARAM_REFRESH);
                Utils.log("VipWebClient.interceptRequest, needRefresh is %s", new Object[]{Boolean.valueOf(isValueTrue)});
                if (isValueTrue) {
                    Object[] objArr = new Object[2];
                    objArr[0] = this.mUser;
                    objArr[1] = this.mCachedAchievements != null ? Integer.valueOf(this.mCachedAchievements.size()) : "-1";
                    Utils.log("VipWebClient.interceptRequest, do batchNotify, mUser = %s, mCachedAchievements.size = %d", objArr);
                    batchNotify(this.mUser, this.mCachedAchievements);
                }
            } else {
                startAccountActivity(webView.getContext(), parseUrl);
            }
        }
        if (shouldIntercept(str)) {
            return new WebResourceResponse("", "", null);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeJsAccountUpdate(String str, String str2, String str3) {
        String format = format(JS_ACCOUNT, str, str2, str3);
        Utils.log("invokeJsAccountUpdate, mStrAccount = %s, js = %s", new Object[]{this.mStrAccount, format});
        if (TextUtils.equals(format, this.mStrAccount)) {
            return;
        }
        this.mStrAccount = format;
        loadJs(format(JS_UPDATE, ACCOUNT_CALLBACK, "account", format));
    }

    private boolean isSameAchievementList(List<VipAchievement> list, List<VipAchievement> list2) {
        if (list == list2) {
            return true;
        }
        if (list == null || list2 == null || list.size() != list2.size()) {
            return false;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            VipAchievement vipAchievement = list.get(i);
            VipAchievement vipAchievement2 = list2.get(i);
            if (vipAchievement.id != vipAchievement2.id || vipAchievement.isOwned != vipAchievement2.isOwned || !TextUtils.equals(vipAchievement.name, vipAchievement2.name) || !TextUtils.equals(Utils.getName(vipAchievement.url), Utils.getName(vipAchievement2.url))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValueTrue(UrlParameters urlParameters, String str) {
        return VALUE_TRUE.equalsIgnoreCase(urlParameters.params.get(str));
    }

    private InputStream loadAccountAvatar() {
        String str = "";
        String str2 = "";
        synchronized (this) {
            AccountInfo accountInfo = this.mAccount;
            if (accountInfo != null) {
                str = accountInfo.avatarUrl;
                str2 = accountInfo.avatarFileName;
            }
        }
        Bitmap loadImageFile = ImageDownloader.loadImageFile(getContext(), str, str2, this.mAvatarListener);
        if (loadImageFile != null) {
            return Utils.bitmapToStream(Utils.createPhoto(loadImageFile));
        }
        return null;
    }

    private void loadJs(final String str) {
        if (this.mWebView == null || !this.mLoadingJsSet.add(str)) {
            return;
        }
        if (Utils.isInMainThread()) {
            doLoadJs(str);
        } else {
            RunnableHelper.runInUIThread(new Runnable() { // from class: miui.vip.VipWebClient.4
                @Override // java.lang.Runnable
                public void run() {
                    VipWebClient.this.doLoadJs(str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAccountUpdate() {
        String userData;
        String userData2;
        String userData3;
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(getContext());
        AccountManager accountManager = AccountManager.get(getContext());
        if (xiaomiAccount == null) {
            synchronized (this) {
                this.mAccount = null;
            }
            invokeJsAccountUpdate("0", "", DEFAULT_AVATAR);
            return;
        }
        synchronized (this) {
            if (this.mAccount == null) {
                this.mAccount = new AccountInfo();
            }
            AccountInfo accountInfo = this.mAccount;
            accountInfo.id = xiaomiAccount.name;
            userData = accountManager.getUserData(xiaomiAccount, "acc_user_name");
            accountInfo.userName = userData;
            AccountInfo accountInfo2 = this.mAccount;
            userData2 = accountManager.getUserData(xiaomiAccount, "acc_avatar_url");
            accountInfo2.avatarUrl = userData2;
            AccountInfo accountInfo3 = this.mAccount;
            userData3 = accountManager.getUserData(xiaomiAccount, "acc_avatar_file_name");
            accountInfo3.avatarFileName = userData3;
        }
        String str = "";
        if (!TextUtils.isEmpty(userData2)) {
            String name = Utils.getName(userData2);
            if (!TextUtils.isEmpty(name)) {
                str = getAccountAvatarWebUrl(name);
            }
        }
        boolean isEmpty = TextUtils.isEmpty(str);
        if (!isEmpty) {
            ImageDownloader.loadImage(getContext(), userData2, userData3, this.mAvatarListener);
        }
        if (TextUtils.isEmpty(userData)) {
            return;
        }
        String str2 = xiaomiAccount.name;
        if (isEmpty) {
            str = DEFAULT_AVATAR;
        }
        invokeJsAccountUpdate(str2, userData, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAchievementsUpdate(List<VipAchievement> list) {
        if (isSameAchievementList(this.mCachedAchievements, list)) {
            return;
        }
        this.mCachedAchievements = list;
        String achievementToJs = (list == null || list.isEmpty()) ? "[]" : achievementToJs(list);
        Utils.log("notifyAchievementsUpdate, js = %s", new Object[]{achievementToJs});
        loadJs(format(JS_UPDATE, ACHIEVEMENT_CALLBACK, VIP_ACHIEVEMENTS_FIELD, achievementToJs));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyVipUserUpdate(VipUserInfo vipUserInfo) {
        String format;
        if (vipUserInfo != null) {
            this.mUser = vipUserInfo;
            format = vipUserToJs(vipUserInfo);
        } else {
            format = format(JS_USER, 0, 0, "");
        }
        Utils.log("notifyVipUserUpdate, mStrUser = %s, js = %s", new Object[]{this.mStrUser, format});
        if (TextUtils.equals(this.mStrUser, format)) {
            return;
        }
        this.mStrUser = format;
        loadJs(format(JS_UPDATE, VIP_CALLBACK, VIP_USER_FIELD, format));
    }

    private UrlParameters parseUrl(String str) {
        UrlParameters urlParameters = new UrlParameters();
        int indexOf = str.indexOf(Q_MARK);
        urlParameters.path = str.substring(16, indexOf > 0 ? indexOf : str.length());
        if (indexOf > 0) {
            for (String str2 : str.substring(indexOf + 1).split("&")) {
                String[] split = str2.split("=");
                if (split.length == 2) {
                    urlParameters.params.put(split[0], split[1]);
                }
            }
        }
        return urlParameters;
    }

    private void startAccountActivity(Context context, UrlParameters urlParameters) {
        String actionFromPath = getActionFromPath(urlParameters.path);
        boolean z = false;
        Utils.log("VipWebClient.interceptRequest, handleVipAction, path = %s, action = %s", new Object[]{urlParameters.path, actionFromPath});
        if (TextUtils.isEmpty(actionFromPath)) {
            return;
        }
        boolean equals = TextUtils.equals(actionFromPath, "com.xiaomi.account.action.USER_INFO_DETAIL");
        String str = ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME;
        if (equals) {
            if (!Utils.hasAccount()) {
                actionFromPath = "com.xiaomi.account.action.XIAOMI_ACCOUNT_WELCOME";
            }
            z = true;
        } else {
            str = "com.xiaomi.vip";
        }
        Utils.startActivity(context, actionFromPath, str, z);
    }

    private String vipUserToJs(VipUserInfo vipUserInfo) {
        return format(JS_USER, Integer.valueOf(vipUserInfo.userId), Integer.valueOf(vipUserInfo.level), "http://request_vip_icon/level_icon");
    }

    public void clear() {
        this.mWebView.set(null);
        this.mUser = null;
        this.mCachedAchievements = null;
        this.mStrUser = null;
        this.mStrAccount = null;
        VipService.instance().disconnect(this.mVipCallback);
        try {
            getContext().unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
            Utils.log("exception happened on unregisterReceiver, %s", new Object[]{e});
        }
    }

    public void init(WebView webView) {
        getContext().registerReceiver(this.mReceiver, Utils.ACCOUNT_CHANGE_FILTER);
        connectService();
        initWithData(webView);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == Math.abs(-192471457)) {
            Utils.log("onActivityResult for user detail activity", new Object[0]);
            notifyAccountUpdate();
        }
    }

    @Override // android.webkit.WebViewClient
    public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
        Utils.log("onPageStarted, url = %s", new Object[]{str});
        initWithData(webView);
    }

    public boolean shouldIntercept(String str) {
        return str.startsWith(SCHEMA_VIP_ICON) || str.startsWith(SCHEMA_VIP_VIEW);
    }

    @Override // android.webkit.WebViewClient
    public WebResourceResponse shouldInterceptRequest(WebView webView, String str) {
        try {
            WebResourceResponse interceptRequest = interceptRequest(webView, str);
            if (interceptRequest != null) {
                if (TextUtils.isEmpty(interceptRequest.getMimeType())) {
                    return null;
                }
                return interceptRequest;
            }
        } catch (Exception e) {
            Utils.logW("VipWebClient.shouldInterceptRequest, url = %s, exception: %s", new Object[]{str, e});
        }
        return null;
    }
}
