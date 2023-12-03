package com.xiaomi.passport.servicetoken;

import android.accounts.Account;
import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.accountsdk.utils.Coder;
import com.xiaomi.passport.servicetoken.ServiceTokenResult;

/* loaded from: classes2.dex */
class ServiceTokenUtilAM extends ServiceTokenUtilImplBase {
    private final IAMUtil amUtil;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServiceTokenUtilAM(IAMUtil iAMUtil) {
        if (iAMUtil == null) {
            throw new IllegalArgumentException("amUtil == null");
        }
        this.amUtil = iAMUtil;
    }

    static String checkAndGet(String str, String str2) {
        if (str == null || str2 == null) {
            return null;
        }
        String[] split = str2.split(",");
        if (split.length == 2 && str.equalsIgnoreCase(split[0])) {
            return split[1];
        }
        return null;
    }

    private ServiceTokenResult noAccountErrorResult(String str) {
        return new ServiceTokenResult.Builder(str).errorCode(ServiceTokenResult.ErrorCode.ERROR_NO_ACCOUNT).build();
    }

    final ServiceTokenResult addAccountAdditionalInfo(Context context, Account account, ServiceTokenResult serviceTokenResult) {
        if (serviceTokenResult.errorCode != ServiceTokenResult.ErrorCode.ERROR_NONE || TextUtils.isEmpty(serviceTokenResult.sid) || TextUtils.isEmpty(serviceTokenResult.serviceToken)) {
            return serviceTokenResult;
        }
        String md5DigestUpperCase = Coder.getMd5DigestUpperCase(serviceTokenResult.serviceToken);
        String cUserId = this.amUtil.getCUserId(context, account);
        String checkAndGet = checkAndGet(md5DigestUpperCase, this.amUtil.getSlh(context, serviceTokenResult.sid, account));
        return new ServiceTokenResult.Builder(serviceTokenResult.sid).serviceToken(serviceTokenResult.serviceToken).security(serviceTokenResult.security).errorCode(serviceTokenResult.errorCode).errorMessage(serviceTokenResult.errorMessage).errorStackTrace(serviceTokenResult.errorStackTrace).peeked(serviceTokenResult.peeked).cUserId(cUserId).slh(checkAndGet).ph(checkAndGet(md5DigestUpperCase, this.amUtil.getPh(context, serviceTokenResult.sid, account))).userId(account.name).build();
    }

    @Override // com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase
    public final ServiceTokenResult getServiceTokenImpl(Context context, String str) {
        Account xiaomiAccount = this.amUtil.getXiaomiAccount(context);
        if (xiaomiAccount == null) {
            return noAccountErrorResult(str);
        }
        String peekAuthToken = this.amUtil.peekAuthToken(context, str, xiaomiAccount);
        if (TextUtils.isEmpty(peekAuthToken)) {
            try {
                return addAccountAdditionalInfo(context, xiaomiAccount, AMAuthTokenConverter.fromAMBundle(this.amUtil.getAuthToken(context, str, xiaomiAccount).getResult(), str));
            } catch (Exception e) {
                return AMAuthTokenConverter.fromAMException(str, e);
            }
        }
        return addAccountAdditionalInfo(context, xiaomiAccount, AMAuthTokenConverter.parseAMAuthToken(str, peekAuthToken, true));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase
    public final ServiceTokenResult invalidateServiceTokenImpl(Context context, ServiceTokenResult serviceTokenResult) {
        if (this.amUtil.getXiaomiAccount(context) == null) {
            return noAccountErrorResult(serviceTokenResult.sid);
        }
        this.amUtil.invalidateAuthToken(context, AMAuthTokenConverter.buildAMAuthToken(serviceTokenResult));
        return new ServiceTokenResult.Builder(serviceTokenResult.sid).build();
    }
}
