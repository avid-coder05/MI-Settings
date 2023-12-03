package com.xiaomi.micloudsdk;

import com.xiaomi.micloudsdk.cloudinfo.utils.CloudInfoUtils;
import com.xiaomi.opensdk.exception.AuthenticationException;
import com.xiaomi.opensdk.exception.RetriableException;
import com.xiaomi.opensdk.exception.UnretriableException;
import miui.cloud.sync.VipInfo;

@Deprecated
/* loaded from: classes2.dex */
public class CommonSdk {
    public static VipInfo getMiCloudMemberStatusInfo(String str, String str2) throws UnretriableException, RetriableException, AuthenticationException {
        return CloudInfoUtils.getMiCloudMemberStatusInfo(str, str2);
    }
}
