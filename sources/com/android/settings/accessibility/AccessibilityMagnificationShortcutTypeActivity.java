package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.StringJoiner;
import miui.provider.ExtraNetwork;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AccessibilityMagnificationShortcutTypeActivity extends AppCompatActivity {
    private static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
    private CheckBox mHardwareTypeCheckBox;
    protected CharSequence mPackageName;
    private CheckBox mSoftwareTypeCheckBox;
    private CheckBox mTripleTapTypeCheckBox;
    private int mUserShortcutType = 0;

    /* loaded from: classes.dex */
    public static final class AccessibilityUserShortcutType {
        private static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DialogType {
    }

    private static View createEditDialogContentView(Context context, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        if (i == 1) {
            View inflate = layoutInflater.inflate(R.layout.accessibility_edit_shortcut_magnification_pad, (ViewGroup) null);
            initSoftwareShortcut(context, inflate);
            initHardwareShortcut(context, inflate);
            initMagnifyShortcut(context, inflate);
            initAdvancedWidget(inflate);
            return inflate;
        }
        throw new IllegalArgumentException();
    }

    private static SpannableString getSummaryStringWithIcon(Context context, int i) {
        String string = context.getString(R.string.accessibility_shortcut_edit_dialog_summary_software);
        SpannableString valueOf = SpannableString.valueOf(string);
        int indexOf = string.indexOf("%s");
        Drawable drawable = context.getDrawable(R.drawable.ic_accessibility_new);
        ImageSpan imageSpan = new ImageSpan(drawable);
        imageSpan.setContentDescription("");
        drawable.setBounds(0, 0, i, i);
        valueOf.setSpan(imageSpan, indexOf, indexOf + 2, 33);
        return valueOf;
    }

    private static boolean hasMagnificationValueInSettings(Context context, int i) {
        TextUtils.SimpleStringSplitter simpleStringSplitter;
        if (i == 4) {
            return Settings.Secure.getInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 0) == 1;
        }
        String string = Settings.Secure.getString(context.getContentResolver(), AccessibilityUtil.convertKeyFromSettings(i));
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        sStringColonSplitter.setString(string);
        do {
            simpleStringSplitter = sStringColonSplitter;
            if (!simpleStringSplitter.hasNext()) {
                return false;
            }
        } while (!"com.android.server.accessibility.MagnificationController".equals(simpleStringSplitter.next()));
        return true;
    }

    private static void initAdvancedWidget(View view) {
        view.findViewById(R.id.triple_tap_shortcut).setVisibility(0);
    }

    private static void initHardwareShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(R.id.hardware_shortcut), context.getText(R.string.accessibility_shortcut_edit_dialog_title_hardware), context.getText(R.string.accessibility_shortcut_edit_dialog_summary_hardware), context.getResources().getConfiguration().orientation == 1 ? R.drawable.accessibility_shortcut_type_hardware_pad_vertical : R.drawable.accessibility_shortcut_type_hardware_pad_horizontal);
    }

    private static void initMagnifyShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(R.id.triple_tap_shortcut), context.getText(R.string.accessibility_shortcut_edit_dialog_title_triple_tap), context.getText(R.string.accessibility_shortcut_edit_dialog_summary_triple_tap), context.getResources().getConfiguration().orientation == 1 ? R.drawable.accessibility_shortcut_type_triple_tap_pad_vertical : R.drawable.accessibility_shortcut_type_triple_tap_pad_horizontal);
    }

    private void initShortcutPreference(Bundle bundle) {
        setTitle(getString(R.string.accessibility_shortcut_title, new Object[]{this.mPackageName}));
    }

    private static void initSoftwareShortcut(Context context, View view) {
        View findViewById = view.findViewById(R.id.software_shortcut);
        setupShortcutWidget(findViewById, retrieveTitle(context), retrieveSummary(context, ((TextView) findViewById.findViewById(R.id.summary)).getLineHeight()), retrieveImageResId(context));
    }

    private void initializeDialogCheckBox() {
        View findViewById = findViewById(R.id.software_shortcut);
        int i = R.id.checkbox;
        CheckBox checkBox = (CheckBox) findViewById.findViewById(i);
        this.mSoftwareTypeCheckBox = checkBox;
        setDialogTextAreaClickListener(findViewById, checkBox);
        View findViewById2 = findViewById(R.id.hardware_shortcut);
        CheckBox checkBox2 = (CheckBox) findViewById2.findViewById(i);
        this.mHardwareTypeCheckBox = checkBox2;
        setDialogTextAreaClickListener(findViewById2, checkBox2);
        View findViewById3 = findViewById(R.id.triple_tap_shortcut);
        CheckBox checkBox3 = (CheckBox) findViewById3.findViewById(i);
        this.mTripleTapTypeCheckBox = checkBox3;
        setDialogTextAreaClickListener(findViewById3, checkBox3);
        updateAlertDialogCheckState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDialogTextAreaClickListener$0(CheckBox checkBox, View view) {
        checkBox.toggle();
        optInAllMagnificationValuesToSettings(this, this.mUserShortcutType);
        optOutAllMagnificationValuesFromSettings(this, ~this.mUserShortcutType);
    }

    static void optInAllMagnificationValuesToSettings(Context context, int i) {
        if ((i & 1) == 1) {
            optInMagnificationValueToSettings(context, 1);
        }
        if ((i & 2) == 2) {
            optInMagnificationValueToSettings(context, 2);
        }
        if ((i & 4) == 4) {
            optInMagnificationValueToSettings(context, 4);
        }
    }

    private static void optInMagnificationValueToSettings(Context context, int i) {
        if (i == 4) {
            Settings.Secure.putInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 1);
        } else if (hasMagnificationValueInSettings(context, i)) {
        } else {
            String convertKeyFromSettings = AccessibilityUtil.convertKeyFromSettings(i);
            String string = Settings.Secure.getString(context.getContentResolver(), convertKeyFromSettings);
            StringJoiner stringJoiner = new StringJoiner(String.valueOf(':'));
            if (!TextUtils.isEmpty(string)) {
                stringJoiner.add(string);
            }
            stringJoiner.add("com.android.server.accessibility.MagnificationController");
            Settings.Secure.putString(context.getContentResolver(), convertKeyFromSettings, stringJoiner.toString());
        }
    }

    static void optOutAllMagnificationValuesFromSettings(Context context, int i) {
        if ((i & 1) == 1) {
            optOutMagnificationValueFromSettings(context, 1);
        }
        if ((i & 2) == 2) {
            optOutMagnificationValueFromSettings(context, 2);
        }
        if ((i & 4) == 4) {
            optOutMagnificationValueFromSettings(context, 4);
        }
    }

    private static void optOutMagnificationValueFromSettings(Context context, int i) {
        if (i == 4) {
            Settings.Secure.putInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 0);
            return;
        }
        String convertKeyFromSettings = AccessibilityUtil.convertKeyFromSettings(i);
        String string = Settings.Secure.getString(context.getContentResolver(), convertKeyFromSettings);
        if (TextUtils.isEmpty(string)) {
            return;
        }
        StringJoiner stringJoiner = new StringJoiner(String.valueOf(':'));
        sStringColonSplitter.setString(string);
        while (true) {
            TextUtils.SimpleStringSplitter simpleStringSplitter = sStringColonSplitter;
            if (!simpleStringSplitter.hasNext()) {
                Settings.Secure.putString(context.getContentResolver(), convertKeyFromSettings, stringJoiner.toString());
                return;
            }
            String next = simpleStringSplitter.next();
            if (!TextUtils.isEmpty(next) && !"com.android.server.accessibility.MagnificationController".equals(next)) {
                stringJoiner.add(next);
            }
        }
    }

    private static int retrieveImageResId(Context context) {
        return context.getResources().getConfiguration().orientation == 1 ? R.drawable.accessibility_shortcut_type_software_gesture_pad_vertical : R.drawable.accessibility_shortcut_type_software_gesture_pad_horizontal;
    }

    private static CharSequence retrieveSummary(Context context, int i) {
        return getSummaryStringWithIcon(context, i);
    }

    private static CharSequence retrieveTitle(Context context) {
        return context.getText(R.string.accessibility_shortcut_edit_dialog_title_software);
    }

    private void setDialogTextAreaClickListener(View view, final CheckBox checkBox) {
        view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityMagnificationShortcutTypeActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                AccessibilityMagnificationShortcutTypeActivity.this.lambda$setDialogTextAreaClickListener$0(checkBox, view2);
            }
        });
    }

    private static void setupShortcutWidget(View view, CharSequence charSequence, CharSequence charSequence2, int i) {
        ((TextView) view.findViewById(R.id.title)).setText(charSequence);
        TextView textView = (TextView) view.findViewById(R.id.summary);
        if (TextUtils.isEmpty(charSequence2)) {
            textView.setVisibility(8);
        } else {
            textView.setText(charSequence2);
        }
        ((ImageView) view.findViewById(R.id.image)).setImageResource(i);
    }

    private void updateAlertDialogCheckState() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int i = 0;
        if (getIntent() != null) {
            i = getIntent().getIntExtra("dialog_type", 0);
            this.mPackageName = getIntent().getStringExtra(ExtraNetwork.FIREWALL_PACKAGE_NAME);
        }
        setContentView(createEditDialogContentView(this, i));
        initShortcutPreference(bundle);
        initializeDialogCheckBox();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        updateShortcutPreferenceData();
    }

    protected void updateShortcutPreferenceData() {
    }
}
