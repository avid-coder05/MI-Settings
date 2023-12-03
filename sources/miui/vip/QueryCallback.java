package miui.vip;

import java.util.List;

/* loaded from: classes4.dex */
public class QueryCallback {
    long mMonitorTypes;

    public QueryCallback(int... iArr) {
        long j = this.mMonitorTypes | 8;
        this.mMonitorTypes = j;
        this.mMonitorTypes = j | 1;
        addMonitorTypes(iArr);
    }

    public void addMonitorTypes(int... iArr) {
        if (iArr == null || iArr.length <= 0) {
            return;
        }
        for (int i : iArr) {
            this.mMonitorTypes |= i;
        }
    }

    public boolean isMonitorType(int i) {
        return (((long) i) & this.mMonitorTypes) != 0;
    }

    public void onAchievements(int i, List<VipAchievement> list, String str) {
    }

    public void onBanners(int i, List<VipBanner> list, String str) {
    }

    public void onConnected(boolean z, VipUserInfo vipUserInfo, List<VipAchievement> list) {
    }

    public void onLevelByPhoneNumber(int i, List<VipPhoneLevel> list, String str) {
    }

    public void onUserInfo(int i, VipUserInfo vipUserInfo, String str) {
    }

    public void removeMonitorTypes(int... iArr) {
        if (iArr == null || iArr.length <= 0) {
            return;
        }
        for (int i : iArr) {
            this.mMonitorTypes &= ~i;
        }
    }
}
