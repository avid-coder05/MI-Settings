package miuix.appcompat.internal.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$integer;
import miuix.appcompat.R$styleable;
import miuix.internal.util.AttributeResolver;

/* loaded from: classes5.dex */
public class ActionBarPolicy {
    private Context mContext;

    private ActionBarPolicy(Context context) {
        this.mContext = context;
    }

    public static ActionBarPolicy get(Context context) {
        return new ActionBarPolicy(context);
    }

    public boolean enableHomeButtonByDefault() {
        return this.mContext.getApplicationInfo().targetSdkVersion < 14;
    }

    public int getEmbeddedMenuWidthLimit() {
        return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
    }

    public int getMaxActionButtons() {
        return this.mContext.getResources().getInteger(R$integer.abc_max_action_buttons);
    }

    public int getStackedTabMaxWidth() {
        return this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_action_bar_stacked_tab_max_width);
    }

    public int getTabContainerHeight() {
        Context context = this.mContext;
        int[] iArr = R$styleable.ActionBar;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(null, iArr, 16843508, 0);
        int i = R$styleable.ActionBar_android_height;
        int layoutDimension = obtainStyledAttributes.getLayoutDimension(i, 0);
        obtainStyledAttributes.recycle();
        if (layoutDimension <= 0) {
            TypedArray obtainStyledAttributes2 = this.mContext.obtainStyledAttributes(null, iArr, 16843470, 0);
            int layoutDimension2 = obtainStyledAttributes2.getLayoutDimension(i, 0);
            obtainStyledAttributes2.recycle();
            return layoutDimension2;
        }
        return layoutDimension;
    }

    public boolean hasEmbeddedTabs() {
        return AttributeResolver.resolveBoolean(this.mContext, R$attr.actionBarEmbedTabs, false);
    }

    public boolean isTightTitle() {
        return AttributeResolver.resolveBoolean(this.mContext, R$attr.actionBarTightTitle, false);
    }

    public boolean showsOverflowMenuButton() {
        return Build.VERSION.SDK_INT >= 11;
    }
}
