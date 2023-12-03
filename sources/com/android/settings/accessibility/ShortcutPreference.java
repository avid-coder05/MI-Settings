package com.android.settings.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes.dex */
public class ShortcutPreference extends Preference implements FolmeAnimationController {
    private boolean mChecked;
    private OnClickCallback mClickCallback;
    private boolean mSettingsEditable;

    /* loaded from: classes.dex */
    public interface OnClickCallback {
        void onSettingsClicked(ShortcutPreference shortcutPreference);

        void onToggleClicked(ShortcutPreference shortcutPreference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ShortcutPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mClickCallback = null;
        this.mChecked = false;
        this.mSettingsEditable = true;
        setLayoutResource(R.layout.accessibility_shortcut_secondary_action);
        setWidgetLayoutResource(R.layout.preference_widget_primary_switch);
        setIconSpaceReserved(false);
    }

    private void callOnSettingsClicked() {
        OnClickCallback onClickCallback = this.mClickCallback;
        if (onClickCallback != null) {
            onClickCallback.onSettingsClicked(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callOnToggleClicked() {
        setChecked(!this.mChecked);
        OnClickCallback onClickCallback = this.mClickCallback;
        if (onClickCallback != null) {
            onClickCallback.onToggleClicked(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        callOnSettingsClicked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onBindViewHolder$1(View view, MotionEvent motionEvent) {
        return motionEvent.getActionMasked() == 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$2(View view) {
        callOnToggleClicked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$3(View view) {
        callOnToggleClicked();
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public boolean isSettingsEditable() {
        return this.mSettingsEditable;
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(16843534, typedValue, true);
        LinearLayout linearLayout = (LinearLayout) preferenceViewHolder.itemView.findViewById(R.id.main_frame);
        if (linearLayout != null) {
            linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ShortcutPreference$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ShortcutPreference.this.lambda$onBindViewHolder$0(view);
                }
            });
            linearLayout.setClickable(this.mSettingsEditable);
            linearLayout.setFocusable(this.mSettingsEditable);
            linearLayout.setBackgroundResource(this.mSettingsEditable ? typedValue.resourceId : 0);
        }
        Switch r0 = (Switch) preferenceViewHolder.itemView.findViewById(R.id.switchWidget);
        if (r0 != null) {
            r0.setOnHoverListener(null);
            r0.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.accessibility.ShortcutPreference$$ExternalSyntheticLambda3
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$onBindViewHolder$1;
                    lambda$onBindViewHolder$1 = ShortcutPreference.lambda$onBindViewHolder$1(view, motionEvent);
                    return lambda$onBindViewHolder$1;
                }
            });
            r0.setContentDescription(getContext().getText(R.string.accessibility_shortcut_settings));
            r0.setChecked(this.mChecked);
            r0.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ShortcutPreference$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ShortcutPreference.this.lambda$onBindViewHolder$2(view);
                }
            });
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.accessibility.ShortcutPreference.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, final boolean z) {
                    compoundButton.post(new Runnable() { // from class: com.android.settings.accessibility.ShortcutPreference.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (z != ShortcutPreference.this.mChecked) {
                                ShortcutPreference.this.callOnToggleClicked();
                            }
                        }
                    });
                }
            });
            r0.setClickable(this.mSettingsEditable);
            r0.setFocusable(this.mSettingsEditable);
        }
        View findViewById = preferenceViewHolder.itemView.findViewById(R.id.divider);
        if (findViewById != null) {
            findViewById.setVisibility(this.mSettingsEditable ? 0 : 8);
        }
        preferenceViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ShortcutPreference$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ShortcutPreference.this.lambda$onBindViewHolder$3(view);
            }
        });
        preferenceViewHolder.itemView.setClickable(!this.mSettingsEditable);
        preferenceViewHolder.itemView.setFocusable(!this.mSettingsEditable);
        View findViewById2 = preferenceViewHolder.itemView.findViewById(R.id.icon_frame);
        if (findViewById2 != null) {
            findViewById2.setVisibility(8);
        }
    }

    public void setChecked(boolean z) {
        if (this.mChecked != z) {
            this.mChecked = z;
            notifyChanged();
        }
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.mClickCallback = onClickCallback;
    }

    public void setSettingsEditable(boolean z) {
        if (this.mSettingsEditable != z) {
            this.mSettingsEditable = z;
            notifyChanged();
        }
    }
}
