package androidx.cardview.widget;

import android.content.Context;
import android.content.res.ColorStateList;

/* loaded from: classes.dex */
interface CardViewImpl {
    ColorStateList getBackgroundColor(CardViewDelegate cardView);

    float getElevation(CardViewDelegate cardView);

    float getMaxElevation(CardViewDelegate cardView);

    float getMinHeight(CardViewDelegate cardView);

    float getMinWidth(CardViewDelegate cardView);

    float getRadius(CardViewDelegate cardView);

    void initStatic();

    void initialize(CardViewDelegate cardView, Context context, ColorStateList backgroundColor, float radius, float elevation, float maxElevation);

    void onCompatPaddingChanged(CardViewDelegate cardView);

    void onPreventCornerOverlapChanged(CardViewDelegate cardView);

    void setBackgroundColor(CardViewDelegate cardView, ColorStateList color);

    void setElevation(CardViewDelegate cardView, float elevation);

    void setMaxElevation(CardViewDelegate cardView, float maxElevation);

    void setRadius(CardViewDelegate cardView, float radius);

    void updatePadding(CardViewDelegate cardView);
}
