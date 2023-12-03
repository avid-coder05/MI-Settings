package miui.sharesdk;

import android.text.TextUtils;

/* loaded from: classes4.dex */
public enum ShareChannel {
    CHANNEL_FAMILY("FamilyShare"),
    CHANNEL_SUGGESTION("Suggestion"),
    CHANNEL_CONTACT("Contact"),
    CHANNEL_WECHAT("WeChat"),
    CHANNEL_QR_CODE("QrCode"),
    CHANNEL_SEARCH("Mid");

    public final String serverTag;

    ShareChannel(String str) {
        this.serverTag = str;
    }

    public static ShareChannel getShareChannelByServerTag(String str) {
        ShareChannel shareChannel = CHANNEL_FAMILY;
        if (TextUtils.equals(str, shareChannel.serverTag)) {
            return shareChannel;
        }
        ShareChannel shareChannel2 = CHANNEL_SUGGESTION;
        if (TextUtils.equals(str, shareChannel2.serverTag)) {
            return shareChannel2;
        }
        ShareChannel shareChannel3 = CHANNEL_CONTACT;
        if (TextUtils.equals(str, shareChannel3.serverTag)) {
            return shareChannel3;
        }
        ShareChannel shareChannel4 = CHANNEL_WECHAT;
        if (TextUtils.equals(str, shareChannel4.serverTag)) {
            return shareChannel4;
        }
        ShareChannel shareChannel5 = CHANNEL_QR_CODE;
        if (TextUtils.equals(str, shareChannel5.serverTag)) {
            return shareChannel5;
        }
        ShareChannel shareChannel6 = CHANNEL_SEARCH;
        if (TextUtils.equals(str, shareChannel6.serverTag)) {
            return shareChannel6;
        }
        return null;
    }
}
