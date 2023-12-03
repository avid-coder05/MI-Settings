package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import miui.provider.ExtraNetwork;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AccessibilityShortcutTypeActivity extends AppCompatActivity {
    private static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
    protected ComponentName mComponentName;
    private CheckBox mHardwareTypeCheckBox;
    protected CharSequence mPackageName;
    private CheckBox mSoftwareTypeCheckBox;
    private int mUserShortcutTypes = 0;

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
        if (i == 0) {
            View inflate = layoutInflater.inflate(R.layout.accessibility_edit_shortcut_pad, (ViewGroup) null);
            initSoftwareShortcut(context, inflate);
            initHardwareShortcut(context, inflate);
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

    private static void initHardwareShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(R.id.hardware_shortcut), context.getText(R.string.accessibility_shortcut_edit_dialog_title_hardware), context.getText(R.string.accessibility_shortcut_edit_dialog_summary_hardware), context.getResources().getConfiguration().orientation == 1 ? R.drawable.accessibility_shortcut_type_hardware_pad_vertical : R.drawable.accessibility_shortcut_type_hardware_pad_horizontal);
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
        updateAlertDialogCheckState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDialogTextAreaClickListener$0(CheckBox checkBox, View view) {
        checkBox.toggle();
        updateUserShortcutType(true);
        AccessibilityUtil.optInAllValuesToSettings(this, this.mUserShortcutTypes, this.mComponentName);
        AccessibilityUtil.optOutAllValuesFromSettings(this, ~this.mUserShortcutTypes, this.mComponentName);
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
        view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityShortcutTypeActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                AccessibilityShortcutTypeActivity.this.lambda$setDialogTextAreaClickListener$0(checkBox, view2);
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

    private void updateUserShortcutType(boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int i = 0;
        if (getIntent() != null) {
            i = getIntent().getIntExtra("dialog_type", 0);
            this.mPackageName = getIntent().getStringExtra(ExtraNetwork.FIREWALL_PACKAGE_NAME);
            this.mComponentName = (ComponentName) getIntent().getBundleExtra(":settings:show_fragment_args").getParcelable("component_name");
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
