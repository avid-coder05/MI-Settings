package androidx.core.internal.view;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.MenuItem;
import android.view.View;
import androidx.core.view.ActionProvider;

/* loaded from: classes.dex */
public interface SupportMenuItem extends MenuItem {
    @Override // android.view.MenuItem
    boolean collapseActionView();

    @Override // android.view.MenuItem
    boolean expandActionView();

    @Override // android.view.MenuItem
    View getActionView();

    @Override // android.view.MenuItem
    int getAlphabeticModifiers();

    @Override // android.view.MenuItem
    CharSequence getContentDescription();

    @Override // android.view.MenuItem
    ColorStateList getIconTintList();

    @Override // android.view.MenuItem
    PorterDuff.Mode getIconTintMode();

    @Override // android.view.MenuItem
    int getNumericModifiers();

    ActionProvider getSupportActionProvider();

    @Override // android.view.MenuItem
    CharSequence getTooltipText();

    @Override // android.view.MenuItem
    boolean isActionViewExpanded();

    @Override // android.view.MenuItem
    MenuItem setActionView(int resId);

    @Override // android.view.MenuItem
    MenuItem setActionView(View view);

    @Override // android.view.MenuItem
    MenuItem setAlphabeticShortcut(char alphaChar, int alphaModifiers);

    @Override // android.view.MenuItem
    SupportMenuItem setContentDescription(CharSequence contentDescription);

    @Override // android.view.MenuItem
    MenuItem setIconTintList(ColorStateList tint);

    @Override // android.view.MenuItem
    MenuItem setIconTintMode(PorterDuff.Mode tintMode);

    @Override // android.view.MenuItem
    MenuItem setNumericShortcut(char numericChar, int numericModifiers);

    @Override // android.view.MenuItem
    MenuItem setShortcut(char numericChar, char alphaChar, int numericModifiers, int alphaModifiers);

    @Override // android.view.MenuItem
    void setShowAsAction(int actionEnum);

    @Override // android.view.MenuItem
    MenuItem setShowAsActionFlags(int actionEnum);

    SupportMenuItem setSupportActionProvider(ActionProvider actionProvider);

    @Override // android.view.MenuItem
    SupportMenuItem setTooltipText(CharSequence tooltipText);
}
