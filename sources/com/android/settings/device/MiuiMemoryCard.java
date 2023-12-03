package com.android.settings.device;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.device.MemoryInfoHelper;
import com.android.settings.report.InternationalCompat;
import com.android.settings.widget.WaterBox;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miuix.animation.Folme;
import miuix.animation.IVisibleStyle;
import miuix.animation.base.AnimConfig;

/* loaded from: classes.dex */
public class MiuiMemoryCard extends FrameLayout implements View.OnClickListener {
    private View calculatingView;
    private MemoryInfoHelper.Callback mCallback;
    private DashboardFragment mFragment;
    private WaterBox progressCardView;
    private View storageView;
    private TextView totalText;
    private TextView usedText;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MemoryInfoCallback implements MemoryInfoHelper.Callback {
        private WeakReference<MiuiMemoryCard> mOuterRef;

        public MemoryInfoCallback(MiuiMemoryCard miuiMemoryCard) {
            this.mOuterRef = new WeakReference<>(miuiMemoryCard);
        }

        @Override // com.android.settings.device.MemoryInfoHelper.Callback
        public void handleTaskResult(long j) {
            Context context;
            MiuiMemoryCard miuiMemoryCard = this.mOuterRef.get();
            if (miuiMemoryCard == null || (context = miuiMemoryCard.getContext()) == null) {
                return;
            }
            long totalMemoryBytes = MiuiAboutPhoneUtils.getInstance(context).getTotalMemoryBytes();
            Locale locale = Locale.ENGLISH;
            String formatShortSize = MiuiUtils.formatShortSize(context, totalMemoryBytes, "%.0f", locale);
            if (!TextUtils.isEmpty(formatShortSize)) {
                miuiMemoryCard.totalText.setText(formatShortSize);
            }
            long j2 = totalMemoryBytes - j;
            String formatShortSize2 = MiuiUtils.formatShortSize(context, j2, "%.1f", locale);
            if (!TextUtils.isEmpty(formatShortSize2)) {
                miuiMemoryCard.usedText.setText(formatShortSize2);
            }
            miuiMemoryCard.setPercent(((float) j2) / ((float) totalMemoryBytes));
            if (miuiMemoryCard.usedText.getText() != "") {
                AnimConfig ease = new AnimConfig().setDelay(120L).setEase(0, 500.0f, 0.9f, 0.3f);
                AnimConfig ease2 = new AnimConfig().setEase(0, 500.0f, 0.9f, 0.3f);
                IVisibleStyle visible = Folme.useAt(miuiMemoryCard.storageView).visible();
                IVisibleStyle.VisibleType visibleType = IVisibleStyle.VisibleType.HIDE;
                IVisibleStyle alpha = visible.setAlpha(0.0f, visibleType);
                IVisibleStyle.VisibleType visibleType2 = IVisibleStyle.VisibleType.SHOW;
                alpha.setAlpha(1.0f, visibleType2).setHide().show(ease);
                Folme.useAt(miuiMemoryCard.calculatingView).visible().setAlpha(1.0f, visibleType2).setAlpha(0.0f, visibleType).setShow().hide(ease2);
            }
        }
    }

    public MiuiMemoryCard(Context context) {
        super(context);
        initView();
    }

    public MiuiMemoryCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    private void initView() {
        LayoutInflater.from(((FrameLayout) this).mContext).inflate(R.layout.miui_memory_card_layout, (ViewGroup) this, true);
        setOnClickListener(this);
        WaterBox waterBox = (WaterBox) findViewById(R.id.water_box_view);
        this.progressCardView = waterBox;
        waterBox.setColor(getResources().getColor(R.color.progress_paint_color));
        this.progressCardView.setCornerRadius(48.0f);
        this.totalText = (TextView) findViewById(R.id.total_storage);
        this.usedText = (TextView) findViewById(R.id.used_storage);
        this.calculatingView = findViewById(R.id.calculating_view);
        this.storageView = findViewById(R.id.storage_view);
        MemoryInfoCallback memoryInfoCallback = new MemoryInfoCallback(this);
        this.mCallback = memoryInfoCallback;
        MemoryInfoHelper.getAvailableMemorySize(memoryInfoCallback);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Intent intent = new Intent("com.miui.cleanmaster.action.STORAGE_MANAGE");
        intent.putExtra("key_channel", "miui_settings");
        this.mFragment.getActivity().startActivity(intent);
        InternationalCompat.trackReportEvent("setting_About_phone_storage");
    }

    public void setFragment(DashboardFragment dashboardFragment) {
        this.mFragment = dashboardFragment;
    }

    public void setPercent(float f) {
        this.progressCardView.setValue(f);
    }
}
