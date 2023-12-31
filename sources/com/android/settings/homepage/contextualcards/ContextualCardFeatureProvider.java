package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.database.Cursor;

/* loaded from: classes.dex */
public interface ContextualCardFeatureProvider {
    Cursor getContextualCards();

    int markCardAsDismissed(Context context, String str);
}
