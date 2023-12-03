package com.android.settings.wifi.operatorutils;

import android.content.Context;
import android.os.SystemProperties;
import com.android.internal.util.ArrayUtils;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.EuropeOperator;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.JapanOperator;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.MexicoOperator;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.SingaporeOperator;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.SouthKoreaOperator;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.TaiwanOperator;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.TelefonicaOperator;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.ThailandOperator;
import com.android.settings.wifi.operatorutils.operatorutilsimpl.VodafoneOperator;
import miui.os.Build;

/* loaded from: classes2.dex */
public final class OperatorFactory {
    private static final String[] REGION = {"TH", "DE", "PL", "GR", "CZ", "SK", "HU", "RO", "AT", "HR", "NL", "SG", "TW", "JP", "KR", "ES"};
    private static Context mContext;

    /* loaded from: classes2.dex */
    private static class EuropeOp {
        private static final EuropeOperator INSTANCE = new EuropeOperator(OperatorFactory.mContext);
    }

    /* loaded from: classes2.dex */
    private static class JapanOp {
        private static final JapanOperator INSTANCE = new JapanOperator(OperatorFactory.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MexicoOp {
        private static final String REGION = SystemProperties.get("ro.miui.customized.region");
        private static final MexicoOperator INSTANCE = new MexicoOperator(OperatorFactory.mContext);
    }

    /* loaded from: classes2.dex */
    private static class SingaporeOp {
        private static final SingaporeOperator INSTANCE = new SingaporeOperator(OperatorFactory.mContext);
    }

    /* loaded from: classes2.dex */
    private static class SouthKoreaOp {
        private static final SouthKoreaOperator INSTANCE = new SouthKoreaOperator(OperatorFactory.mContext);
    }

    /* loaded from: classes2.dex */
    private static class TaiwanOp {
        private static final TaiwanOperator INSTANCE = new TaiwanOperator(OperatorFactory.mContext);
    }

    /* loaded from: classes2.dex */
    private static class TelefonicaOp {
        private static final TelefonicaOperator INSTANCE = new TelefonicaOperator(OperatorFactory.mContext);
    }

    /* loaded from: classes2.dex */
    private static class ThailandOp {
        private static final ThailandOperator INSTANCE = new ThailandOperator(OperatorFactory.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class VodafoneOp {
        private static final String REGION = SystemProperties.get("ro.miui.customized.region");
        private static final VodafoneOperator INSTANCE = new VodafoneOperator(OperatorFactory.mContext);
    }

    public static Operator getInstance(Context context) {
        if (mContext == null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                context = applicationContext;
            }
            mContext = context;
        }
        switch (getWhichInstance()) {
            case 0:
                return ThailandOp.INSTANCE;
            case 1:
                return EuropeOp.INSTANCE;
            case 2:
                return SingaporeOp.INSTANCE;
            case 3:
                return VodafoneOp.INSTANCE;
            case 4:
                return TaiwanOp.INSTANCE;
            case 5:
                return JapanOp.INSTANCE;
            case 6:
                return SouthKoreaOp.INSTANCE;
            case 7:
                return TelefonicaOp.INSTANCE;
            case 8:
            default:
                return null;
            case 9:
                return MexicoOp.INSTANCE;
        }
    }

    private static int getWhichInstance() {
        int indexOf = ArrayUtils.indexOf(REGION, Build.getRegion());
        if (indexOf == 0) {
            return 0;
        }
        if (indexOf >= 1 && indexOf <= 10) {
            return "es_vodafone".equals(VodafoneOp.REGION) ? 3 : 1;
        } else if (11 == indexOf) {
            return 2;
        } else {
            if (12 == indexOf) {
                return 4;
            }
            if (13 == indexOf) {
                return 5;
            }
            if (14 == indexOf) {
                return 6;
            }
            if (15 == indexOf) {
                return 7;
            }
            return "mx_at".equals(MexicoOp.REGION) ? 9 : -1;
        }
    }
}
