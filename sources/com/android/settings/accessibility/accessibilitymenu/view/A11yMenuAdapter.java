package com.android.settings.accessibility.accessibilitymenu.view;

import android.content.Intent;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService;
import com.android.settings.accessibility.accessibilitymenu.model.A11yMenuShortcut;
import java.util.List;
import java.util.Objects;
import miui.os.Build;
import miui.settings.splitlib.SplitUtils;

/* loaded from: classes.dex */
public final class A11yMenuAdapter extends BaseAdapter implements View.OnClickListener {
    private final LayoutInflater inflater;
    public final AccessibilityMenuService service;
    private final List<A11yMenuShortcut> shortcutDataList;
    private final Intent quickSettingsIntent = new Intent("action_panels_operation").putExtra("operation", "reverse_quick_settings_panel");
    private final Intent notificationsIntent = new Intent("action_panels_operation").putExtra("operation", "reverse_notifications_panel");

    /* loaded from: classes.dex */
    final class ViewHolder {
        ImageButton imageButton;
        TextView textView;

        ViewHolder() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public A11yMenuAdapter(AccessibilityMenuService accessibilityMenuService, List<A11yMenuShortcut> list) {
        this.service = accessibilityMenuService;
        this.shortcutDataList = list;
        this.inflater = LayoutInflater.from(accessibilityMenuService);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$0() {
        this.service.a11yMenuLayout.hideMenu();
    }

    @Override // android.widget.Adapter
    public final int getCount() {
        return this.shortcutDataList.size();
    }

    @Override // android.widget.Adapter
    public final Object getItem(int i) {
        return this.shortcutDataList.get(i);
    }

    @Override // android.widget.Adapter
    public final long getItemId(int i) {
        return this.shortcutDataList.get(i).shortcutId;
    }

    @Override // android.widget.Adapter
    public final View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.inflater.inflate(R.layout.grid_item, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.imageButton = (ImageButton) view.findViewById(R.id.shortcutIconBtn);
            viewHolder.textView = (TextView) view.findViewById(R.id.shortcutLabel);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        A11yMenuShortcut a11yMenuShortcut = (A11yMenuShortcut) getItem(i);
        viewHolder.imageButton.setTag(Integer.valueOf(a11yMenuShortcut.shortcutId));
        viewHolder.imageButton.setContentDescription(this.service.getString(a11yMenuShortcut.imgContentDescription));
        viewHolder.textView.setText(a11yMenuShortcut.labelText);
        viewHolder.imageButton.setBackgroundResource(a11yMenuShortcut.imageSrc);
        viewHolder.imageButton.setOnClickListener(this);
        return view;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (((Integer) view.getTag()).intValue()) {
            case 0:
                this.service.startActivityIfIntentIsSafe(new Intent("android.intent.action.VOICE_COMMAND"), 268435456);
                break;
            case 1:
                this.service.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
                if (!Build.IS_TABLET && (!android.os.Build.DEVICE.equals("cetus") || ((this.service.getResources().getConfiguration().screenLayout & 15) < 3 && (this.service.getResources().getConfiguration().uiMode & 8192) == 0))) {
                    this.service.startActivityIfIntentIsSafe(new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 268468224);
                    break;
                } else {
                    SplitUtils.startSettingsSplitActivity(this.service, intent, null, true);
                    break;
                }
                break;
            case 2:
                this.service.performGlobalAction(6);
                break;
            case 3:
                this.service.sendBroadcast(this.quickSettingsIntent);
                break;
            case 4:
                this.service.sendBroadcast(this.notificationsIntent);
                break;
            case 5:
                final AccessibilityMenuService accessibilityMenuService = this.service;
                Objects.requireNonNull(accessibilityMenuService);
                view.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.accessibilitymenu.view.A11yMenuAdapter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessibilityMenuService.this.screenShot();
                    }
                }, 100L);
                break;
            case 6:
                this.service.performGlobalAction(8);
                view.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.accessibilitymenu.view.A11yMenuAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        A11yMenuAdapter.this.lambda$onClick$0();
                    }
                }, 200L);
                return;
            case 7:
                this.service.performGlobalAction(3);
                break;
            case 8:
                AccessibilityMenuService accessibilityMenuService2 = this.service;
                if (accessibilityMenuService2.audioManager == null) {
                    accessibilityMenuService2.audioManager = (AudioManager) accessibilityMenuService2.getSystemService("audio");
                }
                this.service.audioManager.adjustStreamVolume(3, 0, 1);
                break;
        }
        this.service.a11yMenuLayout.hideMenu();
    }
}
