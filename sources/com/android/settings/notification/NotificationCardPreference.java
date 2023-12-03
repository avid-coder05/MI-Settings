package com.android.settings.notification;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;

/* loaded from: classes2.dex */
public class NotificationCardPreference extends Preference {
    public NotificationCardPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.layout_notification_top_card);
    }

    private void setupAnim(View view) {
        Folme.useAt(view).touch().handleTouchOf(view, new AnimConfig[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startBadge() {
        Intent showBadgeNotificationIntent = NotificationSettingsHelper.getShowBadgeNotificationIntent(getContext());
        if (showBadgeNotificationIntent != null) {
            try {
                getContext().startActivity(showBadgeNotificationIntent);
            } catch (ActivityNotFoundException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFloat() {
        Intent floatNotificationIntent = NotificationSettingsHelper.getFloatNotificationIntent(getContext());
        if (floatNotificationIntent != null) {
            try {
                getContext().startActivity(floatNotificationIntent);
            } catch (ActivityNotFoundException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startLockScreen() {
        Intent lockScreenNotificationIntent = NotificationSettingsHelper.getLockScreenNotificationIntent(getContext());
        if (lockScreenNotificationIntent != null) {
            try {
                getContext().startActivity(lockScreenNotificationIntent);
            } catch (ActivityNotFoundException unused) {
            }
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.settings.notification.NotificationCardPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.lock_screen_notification_card) {
                    NotificationCardPreference.this.startLockScreen();
                } else if (id == R.id.float_notification_card) {
                    NotificationCardPreference.this.startFloat();
                } else if (id == R.id.show_app_badge_card) {
                    NotificationCardPreference.this.startBadge();
                }
            }
        };
        final View view = preferenceViewHolder.itemView;
        view.setEnabled(false);
        final View requireViewById = view.requireViewById(R.id.lock_screen_notification_card);
        setupAnim(requireViewById);
        requireViewById.setOnClickListener(onClickListener);
        final View requireViewById2 = view.requireViewById(R.id.float_notification_card);
        setupAnim(requireViewById2);
        requireViewById2.setOnClickListener(onClickListener);
        final View requireViewById3 = view.requireViewById(R.id.show_app_badge_card);
        setupAnim(requireViewById3);
        requireViewById3.setOnClickListener(onClickListener);
        view.post(new Runnable() { // from class: com.android.settings.notification.NotificationCardPreference.2
            @Override // java.lang.Runnable
            public void run() {
                int measuredHeight = requireViewById.getMeasuredHeight();
                int measuredHeight2 = requireViewById2.getMeasuredHeight();
                int measuredHeight3 = requireViewById3.getMeasuredHeight();
                if (measuredHeight <= measuredHeight2) {
                    measuredHeight = measuredHeight2;
                }
                if (measuredHeight > measuredHeight3) {
                    measuredHeight3 = measuredHeight;
                }
                requireViewById.getLayoutParams().height = measuredHeight3;
                requireViewById2.getLayoutParams().height = measuredHeight3;
                requireViewById3.getLayoutParams().height = measuredHeight3;
                view.requestLayout();
            }
        });
    }
}
