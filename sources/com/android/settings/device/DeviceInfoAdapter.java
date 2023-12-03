package com.android.settings.device;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.device.MiuiVersionCard;
import com.android.settings.special.ExternalRamController;
import com.android.settings.stat.commonswitch.TalkbackSwitch;
import java.util.ArrayList;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.IFolme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceCardViewHolder> {
    private DeviceCardInfo[] cardInfos;
    private Context mContext;
    private int mType;
    private List<BorderedBaseDeviceCardItem> mCards = new ArrayList();
    private boolean closeValueTextLineLimit = false;

    /* loaded from: classes.dex */
    public class DeviceCardViewHolder extends RecyclerView.ViewHolder {
        BaseDeviceCardItem card;

        public DeviceCardViewHolder(View view) {
            super(view);
            this.card = (BaseDeviceCardItem) view.findViewById(R.id.base_card_item);
        }
    }

    public DeviceInfoAdapter(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Dialog buildAlertDialog(Context context) {
        return new AlertDialog.Builder(context, R.style.AlertDialog_Theme_DayNight).setTitle(R.string.external_ram_dialog_icon_title).setMessage(ExternalRamController.getDialogInfo(context)).setPositiveButton(R.string.external_ram_dialog_icon_confirm, (DialogInterface.OnClickListener) null).create();
    }

    private void initExternalRamIcon(final Context context, DeviceCardViewHolder deviceCardViewHolder, String str) {
        if (context == null) {
            return;
        }
        Drawable drawable = context.getResources().getDrawable(R.drawable.external_ram_notification);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        SpannableString spannableString = new SpannableString(str + "   ");
        int length = spannableString.length();
        ClickableSpan clickableSpan = new ClickableSpan() { // from class: com.android.settings.device.DeviceInfoAdapter.3
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                DeviceInfoAdapter.this.buildAlertDialog(context).show();
            }
        };
        MiuiVersionCard.CustomImageSpan customImageSpan = new MiuiVersionCard.CustomImageSpan(drawable, 2);
        int i = length + (-1);
        spannableString.setSpan(customImageSpan, i, length, 17);
        spannableString.setSpan(clickableSpan, i, length, 17);
        deviceCardViewHolder.card.setTitle(spannableString);
        deviceCardViewHolder.card.mTitleView.setHighlightColor(0);
        deviceCardViewHolder.card.mTitleView.setMovementMethod(LinkMovementMethod.getInstance());
        deviceCardViewHolder.card.mTitleView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.device.DeviceInfoAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DeviceInfoAdapter.this.lambda$initExternalRamIcon$0(context, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initExternalRamIcon$0(Context context, View view) {
        if (context == null || !TalkbackSwitch.isTalkbackEnable(context)) {
            return;
        }
        buildAlertDialog(context).show();
    }

    private void updateCardHeight(final List<BorderedBaseDeviceCardItem> list) {
        if (list == null || list.size() != 2) {
            return;
        }
        for (BorderedBaseDeviceCardItem borderedBaseDeviceCardItem : list) {
            if (!"miui_version".equals(borderedBaseDeviceCardItem.getKey())) {
                final RelativeLayout relativeLayout = borderedBaseDeviceCardItem.getmBoardLayout();
                if (relativeLayout == null) {
                    return;
                }
                relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.device.DeviceInfoAdapter.4
                    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                    public void onGlobalLayout() {
                        int height = relativeLayout.getHeight();
                        int height2 = relativeLayout.findViewById(R.id.card_value_layout).getHeight();
                        int height3 = relativeLayout.findViewById(R.id.card_value).getHeight();
                        int i = height2 - height3;
                        if (i < 80) {
                            int i2 = (height + 80) - i;
                            if (i == 0) {
                                i2 = relativeLayout.findViewById(R.id.card_title).getHeight() + height3 + relativeLayout.getPaddingBottom() + relativeLayout.getPaddingTop() + 80;
                            }
                            for (BorderedBaseDeviceCardItem borderedBaseDeviceCardItem2 : list) {
                                ViewGroup.LayoutParams layoutParams = borderedBaseDeviceCardItem2.getLayoutParams();
                                layoutParams.height = borderedBaseDeviceCardItem2.getPaddingTop() + i2 + borderedBaseDeviceCardItem2.getPaddingBottom();
                                borderedBaseDeviceCardItem2.setLayoutParams(layoutParams);
                            }
                        }
                        relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    public void closeValueTextLineLimit() {
        this.closeValueTextLineLimit = true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        DeviceCardInfo[] deviceCardInfoArr = this.cardInfos;
        if (deviceCardInfoArr == null) {
            return 0;
        }
        return deviceCardInfoArr.length;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(DeviceCardViewHolder deviceCardViewHolder, int i) {
        if (this.cardInfos[i].getIndex() != 5) {
            deviceCardViewHolder.card.setTitle(this.cardInfos[i].getTitle());
        } else {
            initExternalRamIcon(this.mContext, deviceCardViewHolder, this.cardInfos[i].getTitle());
        }
        if (this.closeValueTextLineLimit) {
            deviceCardViewHolder.card.setValueMaxLine(Integer.MAX_VALUE);
        }
        deviceCardViewHolder.card.setValue(this.cardInfos[i].getValue());
        if (this.mType != 1) {
            deviceCardViewHolder.card.setIcon(this.cardInfos[i].getIconResId());
        }
        String key = this.cardInfos[i].getKey();
        deviceCardViewHolder.card.setKey(null);
        if (!TextUtils.isEmpty(key)) {
            deviceCardViewHolder.card.setKey(key);
            if (key.equals("Android security patch") || key.equals("miui_version")) {
                if (key.equals("Android security patch") && this.mContext.getResources().getConfiguration().locale.getLanguage().equals("bo")) {
                    deviceCardViewHolder.card.mTitleView.setLineSpacing(0.0f, 0.6f);
                }
                this.mCards.add((BorderedBaseDeviceCardItem) deviceCardViewHolder.card);
            }
        }
        if (this.mCards.size() == 2) {
            updateCardHeight(this.mCards);
        }
        View.OnClickListener listener = this.cardInfos[i].getListener();
        if (listener == null) {
            deviceCardViewHolder.card.setClickable(false);
            return;
        }
        deviceCardViewHolder.card.setOnClickListener(listener);
        BaseDeviceCardItem baseDeviceCardItem = deviceCardViewHolder.card;
        int type = this.cardInfos[i].getType();
        if (type == 0 || (type == 1 && this.mType != 1)) {
            baseDeviceCardItem.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.device.DeviceInfoAdapter.2
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == 0) {
                        view.setAlpha(0.6f);
                        return false;
                    } else if (action == 1 || action == 3) {
                        view.setAlpha(1.0f);
                        return false;
                    } else {
                        return false;
                    }
                }
            });
            return;
        }
        IFolme useAt = Folme.useAt(baseDeviceCardItem);
        if (type == 1 || type == 2) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) baseDeviceCardItem.getLayoutParams();
            if (type == 2) {
                layoutParams.height = this.mContext.getResources().getDimensionPixelSize(R.dimen.params_card_height);
            }
            int paddingStart = baseDeviceCardItem.getPaddingStart();
            Resources resources = this.mContext.getResources();
            int i2 = R.dimen.board_layout_padding_top_bottom;
            baseDeviceCardItem.setPadding(paddingStart, resources.getDimensionPixelSize(i2), baseDeviceCardItem.getPaddingStart(), this.mContext.getResources().getDimensionPixelSize(i2));
            baseDeviceCardItem.setLayoutParams(layoutParams);
            useAt.touch().handleTouchOf(baseDeviceCardItem, new AnimConfig[0]);
        }
        if (type == 3) {
            baseDeviceCardItem.findViewById(R.id.board_layout).setBackgroundResource(R.drawable.card_list_background);
            useAt.touch().setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(baseDeviceCardItem, new AnimConfig[0]);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public DeviceCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = this.mType == 1 ? LayoutInflater.from(this.mContext).inflate(R.layout.bordered_base_card_item_wrap, viewGroup, false) : LayoutInflater.from(this.mContext).inflate(R.layout.base_card_item_wrap, viewGroup, false);
        inflate.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.device.DeviceInfoAdapter.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.setEnabled(true);
            }
        });
        return new DeviceCardViewHolder(inflate);
    }

    public void setDataList(DeviceCardInfo[] deviceCardInfoArr) {
        this.cardInfos = deviceCardInfoArr;
        notifyDataSetChanged();
    }

    public void setType(int i) {
        this.mType = i;
    }
}
