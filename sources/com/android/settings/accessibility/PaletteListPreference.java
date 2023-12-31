package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes.dex */
public final class PaletteListPreference extends Preference implements FolmeAnimationController {
    private final List<Integer> mGradientColors;
    private final List<Float> mGradientOffsets;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface Position {
    }

    public PaletteListPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PaletteListPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mGradientColors = new ArrayList();
        this.mGradientOffsets = new ArrayList();
        setLayoutResource(R.layout.daltonizer_preview);
    }

    private GradientDrawable createGradientDrawable(ViewGroup viewGroup, int i) {
        this.mGradientColors.set(2, Integer.valueOf(i));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setOrientation(viewGroup.getLayoutDirection() == 1 ? GradientDrawable.Orientation.RIGHT_LEFT : GradientDrawable.Orientation.LEFT_RIGHT);
        gradientDrawable.setColors(Ints.toArray(this.mGradientColors), Floats.toArray(this.mGradientOffsets));
        return gradientDrawable;
    }

    private List<Integer> getPaletteColors(Context context) {
        return (List) Arrays.stream(context.getResources().getIntArray(R.array.setting_palette_colors)).boxed().collect(Collectors.toList());
    }

    private List<String> getPaletteData(Context context) {
        return Arrays.asList(context.getResources().getStringArray(R.array.setting_palette_data));
    }

    private int getTextLineHeight(Context context) {
        Paint.FontMetrics fontMetrics = new TextView(context).getPaint().getFontMetrics();
        return Math.round(fontMetrics.bottom - fontMetrics.top);
    }

    private int getTextWidth(Context context, String str) {
        return Math.round(new TextView(context).getPaint().measureText(str));
    }

    private void initPaletteAttributes(Context context) {
        int color = context.getColor(R.color.list_card_background);
        this.mGradientColors.add(0, Integer.valueOf(color));
        this.mGradientColors.add(1, Integer.valueOf(color));
        this.mGradientColors.add(2, Integer.valueOf(color));
        this.mGradientOffsets.add(0, Float.valueOf(0.0f));
        this.mGradientOffsets.add(1, Float.valueOf(0.5f));
        this.mGradientOffsets.add(2, Float.valueOf(1.0f));
    }

    private void initPaletteView(Context context, ViewGroup viewGroup) {
        if (viewGroup.getChildCount() > 0) {
            viewGroup.removeAllViews();
        }
        List<Integer> paletteColors = getPaletteColors(context);
        List<String> paletteData = getPaletteData(context);
        float dimension = context.getResources().getDimension(R.dimen.accessibility_layout_margin_start_end);
        this.mGradientOffsets.set(1, Float.valueOf((getTextWidth(context, (String) Collections.max(paletteData, Comparator.comparing(new Function() { // from class: com.android.settings.accessibility.PaletteListPreference$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((String) obj).length());
            }
        }))) + dimension) / AccessibilityUtil.getScreenWidthPixels(context)));
        int max = Ints.max((AccessibilityUtil.getScreenHeightPixels(context) / 2) / paletteData.size(), getTextLineHeight(context));
        for (int i = 0; i < paletteData.size(); i++) {
            TextView textView = new TextView(context);
            textView.setText(paletteData.get(i));
            textView.setHeight(max);
            textView.setPaddingRelative(Math.round(dimension), 0, 0, 0);
            textView.setGravity(16);
            textView.setBackground(createGradientDrawable(viewGroup, paletteColors.get(i).intValue()));
            viewGroup.addView(textView);
        }
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ViewGroup viewGroup = (ViewGroup) preferenceViewHolder.itemView.findViewById(R.id.palette_view);
        initPaletteAttributes(getContext());
        initPaletteView(getContext(), viewGroup);
    }
}
