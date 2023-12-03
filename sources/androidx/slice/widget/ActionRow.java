package androidx.slice.widget;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import java.util.Iterator;
import java.util.List;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class ActionRow extends FrameLayout {
    private final LinearLayout mActionsGroup;
    private int mColor;
    private final int mIconPadding;
    private final int mSize;

    public ActionRow(Context context, boolean fullActions) {
        super(context);
        this.mColor = -16777216;
        this.mSize = (int) TypedValue.applyDimension(1, 48.0f, context.getResources().getDisplayMetrics());
        this.mIconPadding = (int) TypedValue.applyDimension(1, 12.0f, context.getResources().getDisplayMetrics());
        LinearLayout linearLayout = new LinearLayout(context);
        this.mActionsGroup = linearLayout;
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        addView(linearLayout);
    }

    private ImageView addAction(IconCompat icon, boolean allowTint) {
        ImageView imageView = new ImageView(getContext());
        int i = this.mIconPadding;
        imageView.setPadding(i, i, i, i);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(icon.loadDrawable(getContext()));
        if (allowTint) {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(this.mColor));
        }
        imageView.setBackground(SliceViewUtil.getDrawable(getContext(), 16843534));
        imageView.setTag(Boolean.valueOf(allowTint));
        addAction(imageView);
        return imageView;
    }

    private void addAction(View child) {
        LinearLayout linearLayout = this.mActionsGroup;
        int i = this.mSize;
        linearLayout.addView(child, new LinearLayout.LayoutParams(i, i, 1.0f));
    }

    private void createRemoteInputView(int color, Context context) {
        RemoteInputView inflate = RemoteInputView.inflate(context, this);
        inflate.setVisibility(4);
        addView(inflate, new FrameLayout.LayoutParams(-1, -1));
        inflate.setBackgroundColor(color);
    }

    private RemoteInputView findRemoteInputView(View v) {
        if (v == null) {
            return null;
        }
        return (RemoteInputView) v.findViewWithTag(RemoteInputView.VIEW_TAG);
    }

    private void handleSetRemoteInputActions(final SliceItem input, SliceItem image, final SliceItem action) {
        if (input.getRemoteInput().getAllowFreeFormInput()) {
            addAction(image.getIcon(), !image.hasHint("no_tint")).setOnClickListener(new View.OnClickListener() { // from class: androidx.slice.widget.ActionRow.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    ActionRow.this.handleRemoteInputClick(v, action, input.getRemoteInput());
                }
            });
            createRemoteInputView(this.mColor, getContext());
        }
    }

    private void setColor(int color) {
        this.mColor = color;
        for (int i = 0; i < this.mActionsGroup.getChildCount(); i++) {
            View childAt = this.mActionsGroup.getChildAt(i);
            if (((Integer) childAt.getTag()).intValue() == 0) {
                ImageViewCompat.setImageTintList((ImageView) childAt, ColorStateList.valueOf(this.mColor));
            }
        }
    }

    boolean handleRemoteInputClick(View view, SliceItem action, RemoteInput input) {
        if (input == null) {
            return false;
        }
        RemoteInputView remoteInputView = null;
        for (ViewParent parent = view.getParent().getParent(); parent != null && (!(parent instanceof View) || (remoteInputView = findRemoteInputView((View) parent)) == null); parent = parent.getParent()) {
        }
        if (remoteInputView == null) {
            return false;
        }
        int width = view.getWidth();
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (textView.getLayout() != null) {
                width = Math.min(width, ((int) textView.getLayout().getLineWidth(0)) + textView.getCompoundPaddingLeft() + textView.getCompoundPaddingRight());
            }
        }
        int left = view.getLeft() + (width / 2);
        int top = view.getTop() + (view.getHeight() / 2);
        int width2 = remoteInputView.getWidth();
        int height = remoteInputView.getHeight() - top;
        int i = width2 - left;
        remoteInputView.setRevealParameters(left, top, Math.max(Math.max(left + top, left + height), Math.max(i + top, i + height)));
        remoteInputView.setAction(action);
        remoteInputView.setRemoteInput(new RemoteInput[]{input}, input);
        remoteInputView.focusAnimated();
        return true;
    }

    public void setActions(List<SliceAction> actions, int color) {
        IconCompat icon;
        removeAllViews();
        this.mActionsGroup.removeAllViews();
        addView(this.mActionsGroup);
        if (color != -1) {
            setColor(color);
        }
        Iterator<SliceAction> it = actions.iterator();
        while (true) {
            if (!it.hasNext()) {
                setVisibility(getChildCount() == 0 ? 8 : 0);
                return;
            }
            SliceAction next = it.next();
            if (this.mActionsGroup.getChildCount() >= 5) {
                return;
            }
            SliceActionImpl sliceActionImpl = (SliceActionImpl) next;
            SliceItem sliceItem = sliceActionImpl.getSliceItem();
            final SliceItem actionItem = sliceActionImpl.getActionItem();
            SliceItem find = SliceQuery.find(sliceItem, "input");
            SliceItem find2 = SliceQuery.find(sliceItem, YellowPageContract.ImageLookup.DIRECTORY_IMAGE);
            if (find == null || find2 == null) {
                if (next.getIcon() != null && (icon = next.getIcon()) != null && actionItem != null) {
                    addAction(icon, next.getImageMode() == 0).setOnClickListener(new View.OnClickListener() { // from class: androidx.slice.widget.ActionRow.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            try {
                                actionItem.fireAction(null, null);
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else if (Build.VERSION.SDK_INT >= 21) {
                handleSetRemoteInputActions(find, find2, actionItem);
            } else {
                Log.w("ActionRow", "Received RemoteInput on API <20 " + find);
            }
        }
    }
}
