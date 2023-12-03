package miuix.internal.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import miuix.appcompat.R$dimen;
import miuix.internal.graphics.drawable.TaggingDrawable;

/* loaded from: classes5.dex */
public class TaggingDrawableUtil {
    private static int mPaddingLarge = -1;
    private static int mPaddingSingle = -1;
    private static int mPaddingSmall = -1;
    private static final int[] STATES_TAGS = {16842915, 16842916, 16842917, 16842918};
    private static final int[] STATE_SET_SINGLE = {16842915};
    private static final int[] STATE_SET_FIRST = {16842916};
    private static final int[] STATE_SET_MIDDLE = {16842917};
    private static final int[] STATE_SET_LAST = {16842918};

    private static int getDimen(Context context, int i) {
        return context.getResources().getDimensionPixelSize(i);
    }

    public static void updateBackgroundState(View view, int i, int i2) {
        if (view == null || i2 == 0) {
            return;
        }
        Drawable background = view.getBackground();
        if ((background instanceof StateListDrawable) && TaggingDrawable.containsTagState((StateListDrawable) background, STATES_TAGS)) {
            TaggingDrawable taggingDrawable = new TaggingDrawable(background);
            view.setBackground(taggingDrawable);
            background = taggingDrawable;
        }
        if (background instanceof TaggingDrawable) {
            ((TaggingDrawable) background).setTaggingState(i2 == 1 ? STATE_SET_SINGLE : i == 0 ? STATE_SET_FIRST : i == i2 - 1 ? STATE_SET_LAST : STATE_SET_MIDDLE);
        }
    }

    public static void updateItemBackground(View view, int i, int i2) {
        updateBackgroundState(view, i, i2);
        updateItemPadding(view, i, i2);
    }

    public static void updateItemPadding(View view, int i, int i2) {
        int i3;
        int i4;
        if (view == null || i2 == 0) {
            return;
        }
        Context context = view.getContext();
        int paddingStart = view.getPaddingStart();
        view.getPaddingTop();
        int paddingEnd = view.getPaddingEnd();
        view.getPaddingBottom();
        if (i2 != 1) {
            if (mPaddingSmall == -1) {
                mPaddingSmall = getDimen(context, R$dimen.miuix_appcompat_drop_down_menu_padding_small);
            }
            if (mPaddingLarge == -1) {
                mPaddingLarge = getDimen(context, R$dimen.miuix_appcompat_drop_down_menu_padding_large);
            }
            if (i == 0) {
                i3 = mPaddingLarge;
                i4 = mPaddingSmall;
            } else if (i == i2 - 1) {
                i3 = mPaddingSmall;
                i4 = mPaddingLarge;
            } else {
                i3 = mPaddingSmall;
            }
            view.setPaddingRelative(paddingStart, i3, paddingEnd, i4);
        }
        if (mPaddingSingle == -1) {
            mPaddingSingle = getDimen(context, R$dimen.miuix_appcompat_drop_down_menu_padding_single_item);
        }
        i3 = mPaddingSingle;
        i4 = i3;
        view.setPaddingRelative(paddingStart, i3, paddingEnd, i4);
    }
}
