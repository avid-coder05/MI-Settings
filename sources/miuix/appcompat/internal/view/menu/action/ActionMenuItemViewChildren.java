package miuix.appcompat.internal.view.menu.action;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;
import miuix.internal.util.DeviceHelper;

/* loaded from: classes5.dex */
public class ActionMenuItemViewChildren {
    private ImageView mImageView;
    private TextView mTextView;

    public ActionMenuItemViewChildren(LinearLayout linearLayout) {
        Context context = linearLayout.getContext();
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        LinearLayout.inflate(context, R$layout.miuix_appcompat_action_menu_item_child_layout, linearLayout);
        this.mImageView = (ImageView) linearLayout.findViewById(R$id.action_menu_item_child_icon);
        this.mTextView = (TextView) linearLayout.findViewById(R$id.action_menu_item_child_text);
        if (Build.VERSION.SDK_INT >= 29) {
            this.mImageView.setForceDarkAllowed(false);
        }
        initConfiguration(context);
        Folme.useAt(linearLayout).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setAlpha(0.6f, new ITouchStyle.TouchType[0]).handleTouchOf(linearLayout, new AnimConfig[0]);
        Folme.useAt(linearLayout).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(linearLayout, new AnimConfig[0]);
    }

    private void initConfiguration(Context context) {
        onConfigurationChanged(context.getResources().getConfiguration());
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (configuration.orientation == 1 || DeviceHelper.isTablet(this.mTextView.getContext())) {
            this.mTextView.setVisibility(0);
        } else {
            this.mTextView.setVisibility(8);
        }
        this.mTextView.setTextSize(0, this.mTextView.getContext().getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_small_text_size));
    }

    public void setEnabled(boolean z) {
        this.mImageView.setEnabled(z);
        this.mTextView.setEnabled(z);
    }

    public void setIcon(Drawable drawable) {
        if (this.mImageView.getDrawable() != drawable) {
            this.mImageView.setImageDrawable(drawable);
        }
    }

    public void setText(CharSequence charSequence) {
        this.mTextView.setText(charSequence);
    }
}
