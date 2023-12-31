package com.android.settings.homepage.contextualcards.logging;

import android.util.Log;
import com.android.settings.homepage.contextualcards.ContextualCard;
import java.util.List;

/* loaded from: classes.dex */
public class ContextualCardLogUtils {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class TapTarget {
        static int TARGET_DEFAULT = 0;
        static int TARGET_SLIDER = 3;
        static int TARGET_TITLE = 1;
        static int TARGET_TOGGLE = 2;
    }

    public static int actionTypeToTapTarget(int i) {
        if (i != 0) {
            if (i != 2) {
                if (i != 3) {
                    Log.w("ContextualCardLogUtils", "unknown type " + i);
                    return TapTarget.TARGET_DEFAULT;
                }
                return TapTarget.TARGET_TITLE;
            }
            return TapTarget.TARGET_SLIDER;
        }
        return TapTarget.TARGET_TOGGLE;
    }

    public static String buildCardClickLog(ContextualCard contextualCard, int i, int i2, int i3) {
        return contextualCard.getTextSliceUri() + "|" + contextualCard.getRankingScore() + "|" + i + "|" + actionTypeToTapTarget(i2) + "|" + i3;
    }

    public static String buildCardDismissLog(ContextualCard contextualCard) {
        return contextualCard.getTextSliceUri() + "|" + contextualCard.getRankingScore();
    }

    public static String buildCardListLog(List<ContextualCard> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(list.size());
        for (ContextualCard contextualCard : list) {
            sb.append("|");
            sb.append(contextualCard.getTextSliceUri());
            sb.append("|");
            sb.append(contextualCard.getRankingScore());
        }
        return sb.toString();
    }
}
