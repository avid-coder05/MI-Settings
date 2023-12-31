package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import com.android.settings.homepage.contextualcards.ContextualCard;

/* loaded from: classes.dex */
public interface ConditionalCardController {
    ContextualCard buildContextualCard();

    long getId();

    boolean isDisplayable();

    void onActionClick();

    void onPrimaryClick(Context context);

    void startMonitoringStateChange();

    void stopMonitoringStateChange();
}
