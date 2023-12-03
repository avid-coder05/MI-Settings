package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.settings.FramesSequenceAnimation;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class KeySettingsPreviewPreference extends Preference {
    private final ArrayList<FramesSequenceAnimation> animations;
    private String mAction;
    private RelativeLayout mActionRelativeLayout;
    private ImageView mBackGroundDrawable;
    private ImageView mBottomDrawable;
    private Context mContext;
    private ImageView mDoublePowerDrawable;
    private ImageView mLongPressBottomDrawable;
    private ImageView mPowerDrawable;
    private String mPreferenceKey;
    private ImageView mThreeGestureDrawable;
    private ImageView mThreeLongPressDrawable;

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Enum visitor error
    jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'DOUBLE_CLICK_POWER' uses external variables
    	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
    	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
    	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
    	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
     */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* loaded from: classes.dex */
    public static final class AnimationEnum {
        private static final /* synthetic */ AnimationEnum[] $VALUES;
        public static final AnimationEnum CLICK_BOTTOM;
        public static final AnimationEnum DOUBLE_CLICK_POWER;
        public static final AnimationEnum LONG_PRESS;
        public static final AnimationEnum LONG_PRESS_POWER;
        public static final AnimationEnum POWER_CLICK;
        public static final AnimationEnum THREE_DROP;
        public static final AnimationEnum THREE_LONG_PRESS;
        private int mAnimArrayId;
        private int mImgViewId;

        static {
            AnimationEnum animationEnum = new AnimationEnum("POWER_CLICK", 0, R.id.key_power_click, R.array.power_click);
            POWER_CLICK = animationEnum;
            AnimationEnum animationEnum2 = new AnimationEnum("LONG_PRESS", 1, R.id.key_long_press_bottom, R.array.long_press);
            LONG_PRESS = animationEnum2;
            AnimationEnum animationEnum3 = new AnimationEnum("CLICK_BOTTOM", 2, R.id.key_click_bottom, R.array.click_bottom);
            CLICK_BOTTOM = animationEnum3;
            int i = R.id.key_power_double_click;
            AnimationEnum animationEnum4 = new AnimationEnum("DOUBLE_CLICK_POWER", 3, i, R.array.power_double_click);
            DOUBLE_CLICK_POWER = animationEnum4;
            AnimationEnum animationEnum5 = new AnimationEnum("THREE_DROP", 4, R.id.key_three_gesture, R.array.three_drop);
            THREE_DROP = animationEnum5;
            AnimationEnum animationEnum6 = new AnimationEnum("THREE_LONG_PRESS", 5, R.id.key_three_long_press, R.array.three_long_press);
            THREE_LONG_PRESS = animationEnum6;
            AnimationEnum animationEnum7 = new AnimationEnum("LONG_PRESS_POWER", 6, i, R.array.power_long_press);
            LONG_PRESS_POWER = animationEnum7;
            $VALUES = new AnimationEnum[]{animationEnum, animationEnum2, animationEnum3, animationEnum4, animationEnum5, animationEnum6, animationEnum7};
        }

        private AnimationEnum(String str, int i, int i2, int i3) {
            this.mImgViewId = i2;
            this.mAnimArrayId = i3;
        }

        public static AnimationEnum valueOf(String str) {
            return (AnimationEnum) Enum.valueOf(AnimationEnum.class, str);
        }

        public static AnimationEnum[] values() {
            return (AnimationEnum[]) $VALUES.clone();
        }
    }

    public KeySettingsPreviewPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.animations = new ArrayList<>();
        this.mContext = context;
        setLayoutResource(R.xml.key_settings_preview);
    }

    private void addPreviewAnimationView(View view, AnimationEnum animationEnum) {
        addPreviewAnimationView(view, animationEnum, 0);
    }

    private void addPreviewAnimationView(View view, AnimationEnum animationEnum, int i) {
        ImageView imageView = (ImageView) view.findViewById(animationEnum.mImgViewId);
        FramesSequenceAnimation framesSequenceAnimation = new FramesSequenceAnimation(this.mContext, imageView, animationEnum.mAnimArrayId, 10);
        if (i != 0) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginLayoutParams.leftMargin = (int) this.mContext.getResources().getDimension(i);
            imageView.setLayoutParams(marginLayoutParams);
        }
        framesSequenceAnimation.setAnimationListener(new FramesSequenceAnimation.AnimationListener() { // from class: com.android.settings.KeySettingsPreviewPreference.1
            @Override // com.android.settings.FramesSequenceAnimation.AnimationListener
            public void onAnimationBackgroundChange() {
                KeySettingsPreviewPreference.this.changeBackground();
            }

            @Override // com.android.settings.FramesSequenceAnimation.AnimationListener
            public void onAnimationPlayed() {
                KeySettingsPreviewPreference.this.resetBackground();
            }

            @Override // com.android.settings.FramesSequenceAnimation.AnimationListener
            public void onAnimationStarted() {
            }

            @Override // com.android.settings.FramesSequenceAnimation.AnimationListener
            public void onAnimationStopped() {
            }
        });
        this.animations.add(framesSequenceAnimation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeBackground() {
        if ("launch_camera".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_camera));
        } else if ("screen_shot".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_screen_shot));
        } else if ("partial_screen_shot".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_partial_screen_shot));
        } else if ("launch_voice_assistant".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_voice_assistant));
        } else if ("launch_google_search".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_voice_assistant));
        } else if ("go_to_sleep".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(null);
        } else if ("turn_on_torch".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_turn_on_torch));
        } else if ("close_app".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_launcher));
        } else if ("split_screen".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_split_screen));
        } else if ("mi_pay".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_mipay));
        } else if ("show_menu".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_show_menu));
        } else if ("launch_recents".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_launch_recents));
        } else {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_launcher));
        }
    }

    private void processPreviewAnimation(View view) {
        resetAnimationEnv();
        if ("long_press_power_key".equals(this.mAction)) {
            setBackgroundDrawable(true);
            addPreviewAnimationView(view, AnimationEnum.LONG_PRESS_POWER);
        } else if ("double_click_power_key".equals(this.mAction)) {
            setBackgroundDrawable(true);
            addPreviewAnimationView(view, AnimationEnum.DOUBLE_CLICK_POWER);
        } else if ("long_press_menu_key".equals(this.mAction)) {
            setBackgroundDrawable(false);
            addPreviewAnimationView(view, AnimationEnum.LONG_PRESS, R.dimen.key_settings_click_menu_marginLeft);
        } else if ("long_press_menu_key_when_lock".equals(this.mAction)) {
            setBackgroundDrawable(false);
            addPreviewAnimationView(view, AnimationEnum.LONG_PRESS, R.dimen.key_settings_click_menu_marginLeft);
        } else if ("long_press_home_key".equals(this.mAction)) {
            setBackgroundDrawable(false);
            addPreviewAnimationView(view, AnimationEnum.LONG_PRESS, R.dimen.key_settings_click_home_marginLeft);
        } else if ("long_press_back_key".equals(this.mAction)) {
            setBackgroundDrawable(false);
            addPreviewAnimationView(view, AnimationEnum.LONG_PRESS, R.dimen.key_settings_click_back_marginLeft);
        } else if ("key_combination_power_back".equals(this.mAction)) {
            setBackgroundDrawable(true);
            addPreviewAnimationView(view, AnimationEnum.POWER_CLICK);
            addPreviewAnimationView(view, AnimationEnum.CLICK_BOTTOM, R.dimen.key_settings_click_back_marginLeft);
        } else if ("key_combination_power_home".equals(this.mAction)) {
            setBackgroundDrawable(true);
            addPreviewAnimationView(view, AnimationEnum.POWER_CLICK);
            addPreviewAnimationView(view, AnimationEnum.CLICK_BOTTOM, R.dimen.key_settings_click_home_marginLeft);
        } else if ("key_combination_power_menu".equals(this.mAction)) {
            setBackgroundDrawable(true);
            addPreviewAnimationView(view, AnimationEnum.POWER_CLICK);
            addPreviewAnimationView(view, AnimationEnum.CLICK_BOTTOM, R.dimen.key_settings_click_menu_marginLeft);
        } else if ("three_gesture_down".equals(this.mAction)) {
            setBackgroundDrawable(false);
            addPreviewAnimationView(view, AnimationEnum.THREE_DROP);
        } else if ("three_gesture_long_press".equals(this.mAction)) {
            setBackgroundDrawable(false);
            addPreviewAnimationView(view, AnimationEnum.THREE_LONG_PRESS);
        } else if ("key_none".equals(this.mAction)) {
            setBackgroundDrawable(false);
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_launcher));
        } else if (!"launch_recents".equals(this.mAction)) {
            return;
        } else {
            setBackgroundDrawable(false);
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_launcher));
        }
        Log.i("animationanimation", "size = " + this.animations.size());
        Iterator<FramesSequenceAnimation> it = this.animations.iterator();
        while (it.hasNext()) {
            it.next().start();
        }
    }

    private void resetAnimationEnv() {
        this.mBackGroundDrawable.setImageDrawable(null);
        this.mPowerDrawable.setImageDrawable(null);
        this.mDoublePowerDrawable.setImageDrawable(null);
        this.mBottomDrawable.setImageDrawable(null);
        this.mLongPressBottomDrawable.setImageDrawable(null);
        this.mThreeGestureDrawable.setImageDrawable(null);
        this.mThreeLongPressDrawable.setImageDrawable(null);
        if (!this.animations.isEmpty()) {
            Iterator<FramesSequenceAnimation> it = this.animations.iterator();
            while (it.hasNext()) {
                it.next().stop();
            }
            this.animations.clear();
        }
        resetBackground();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetBackground() {
        if ("close_app".equals(this.mPreferenceKey) || "show_menu".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_camera));
        } else if ("split_screen".equals(this.mPreferenceKey)) {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_show_menu));
        } else {
            this.mActionRelativeLayout.setBackground(this.mContext.getResources().getDrawable(R.drawable.keysettings_launcher));
        }
    }

    private void setBackgroundDrawable(boolean z) {
        this.mBackGroundDrawable.setImageDrawable(this.mContext.getResources().getDrawable(z ? R.drawable.keysettings_common_power : R.drawable.keysettings_common_no_power));
    }

    public void destroy() {
        if (this.mActionRelativeLayout != null) {
            resetAnimationEnv();
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        this.mActionRelativeLayout = (RelativeLayout) view.findViewById(R.id.key_settings_preview_action);
        this.mBackGroundDrawable = (ImageView) view.findViewById(R.id.key_settings_preview_background);
        this.mPowerDrawable = (ImageView) view.findViewById(R.id.key_power_click);
        this.mDoublePowerDrawable = (ImageView) view.findViewById(R.id.key_power_double_click);
        this.mBottomDrawable = (ImageView) view.findViewById(R.id.key_click_bottom);
        this.mLongPressBottomDrawable = (ImageView) view.findViewById(R.id.key_long_press_bottom);
        this.mThreeGestureDrawable = (ImageView) view.findViewById(R.id.key_three_gesture);
        this.mThreeLongPressDrawable = (ImageView) view.findViewById(R.id.key_three_long_press);
        processPreviewAnimation(view);
    }

    public void setCheckedAction(String str) {
        this.mAction = str;
        notifyChanged();
    }

    public void setPreferenceKey(String str) {
        this.mPreferenceKey = str;
    }
}
