package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.icu.text.MessageFormat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.accessibility.AccessibilityDialogUtils;
import com.android.settings.accessibility.ItemInfoArrayAdapter;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.utils.AnnotationSpan;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class AccessibilityDialogUtils {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CustomButton {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface CustomButtonsClickListener {
        void onClick(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DialogEnums {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DialogType {
    }

    public static Dialog createCustomDialog(Context context, CharSequence charSequence, View view, DialogInterface.OnClickListener onClickListener) {
        AlertDialog create = new AlertDialog.Builder(context).setView(view).setTitle(charSequence).setCancelable(true).setPositiveButton(R.string.save, onClickListener).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
        if ((view instanceof ScrollView) || (view instanceof AbsListView)) {
            setScrollIndicators(view);
        }
        return create;
    }

    private static AlertDialog createDialog(Context context, int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setView(createEditDialogContentView(context, i)).setTitle(charSequence).setPositiveButton(R.string.save, onClickListener).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityDialogUtils$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.dismiss();
            }
        }).create();
    }

    private static View createEditDialogContentView(Context context, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        if (i == 0) {
            View inflate = layoutInflater.inflate(R.layout.accessibility_edit_shortcut, (ViewGroup) null);
            initSoftwareShortcut(context, inflate);
            initHardwareShortcut(context, inflate);
            return inflate;
        } else if (i == 1) {
            View inflate2 = layoutInflater.inflate(R.layout.accessibility_edit_shortcut, (ViewGroup) null);
            initSoftwareShortcutForSUW(context, inflate2);
            initHardwareShortcut(context, inflate2);
            return inflate2;
        } else if (i == 2) {
            View inflate3 = layoutInflater.inflate(R.layout.accessibility_edit_shortcut_magnification, (ViewGroup) null);
            initSoftwareShortcut(context, inflate3);
            initHardwareShortcut(context, inflate3);
            initMagnifyShortcut(context, inflate3);
            initAdvancedWidget(inflate3);
            return inflate3;
        } else if (i != 3) {
            if (i == 4) {
                View inflate4 = layoutInflater.inflate(R.layout.accessibility_edit_magnification_shortcut, (ViewGroup) null);
                ((ImageView) inflate4.findViewById(R.id.image)).setImageResource(retrieveSoftwareShortcutImageResId(context));
                return inflate4;
            }
            throw new IllegalArgumentException();
        } else {
            View inflate5 = layoutInflater.inflate(R.layout.accessibility_edit_shortcut_magnification, (ViewGroup) null);
            initSoftwareShortcutForSUW(context, inflate5);
            initHardwareShortcut(context, inflate5);
            initMagnifyShortcut(context, inflate5);
            initAdvancedWidget(inflate5);
            return inflate5;
        }
    }

    public static Dialog createMagnificationSwitchShortcutDialog(Context context, CustomButtonsClickListener customButtonsClickListener) {
        View createSwitchShortcutDialogContentView = createSwitchShortcutDialogContentView(context);
        AlertDialog create = new AlertDialog.Builder(context).setView(createSwitchShortcutDialogContentView).setTitle(context.getString(R.string.accessibility_magnification_switch_shortcut_title)).create();
        setCustomButtonsClickListener(create, createSwitchShortcutDialogContentView, customButtonsClickListener, null);
        setScrollIndicators(createSwitchShortcutDialogContentView);
        return create;
    }

    public static ListView createSingleChoiceListView(Context context, List<? extends ItemInfoArrayAdapter.ItemInfo> list, AdapterView.OnItemClickListener onItemClickListener) {
        ListView listView = new ListView(context);
        listView.setId(16908298);
        listView.setDivider(null);
        listView.setChoiceMode(1);
        listView.setAdapter((ListAdapter) new ItemInfoArrayAdapter(context, list));
        listView.setOnItemClickListener(onItemClickListener);
        return listView;
    }

    private static View createSwitchShortcutDialogContentView(Context context) {
        return createEditDialogContentView(context, 4);
    }

    private static CharSequence getCustomizeAccessibilityButtonLink(final Context context) {
        return AnnotationSpan.linkify(context.getText(R.string.accessibility_shortcut_edit_dialog_summary_software_floating), new AnnotationSpan.LinkInfo("link", new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityDialogUtils$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AccessibilityDialogUtils.lambda$getCustomizeAccessibilityButtonLink$4(context, view);
            }
        }));
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

    private static void initAdvancedWidget(View view) {
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.advanced_shortcut);
        final View findViewById = view.findViewById(R.id.triple_tap_shortcut);
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityDialogUtils$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                AccessibilityDialogUtils.lambda$initAdvancedWidget$3(linearLayout, findViewById, view2);
            }
        });
    }

    private static void initHardwareShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(R.id.hardware_shortcut), context.getText(R.string.accessibility_shortcut_edit_dialog_title_hardware), context.getText(R.string.accessibility_shortcut_edit_dialog_summary_hardware), R.drawable.accessibility_shortcut_type_hardware);
    }

    private static void initMagnifyShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(R.id.triple_tap_shortcut), context.getText(R.string.accessibility_shortcut_edit_dialog_title_triple_tap), MessageFormat.format(context.getString(R.string.accessibility_shortcut_edit_dialog_summary_triple_tap), 3), R.drawable.accessibility_shortcut_type_triple_tap);
    }

    private static void initSoftwareShortcut(Context context, View view) {
        View findViewById = view.findViewById(R.id.software_shortcut);
        setupShortcutWidget(findViewById, context.getText(R.string.accessibility_shortcut_edit_dialog_title_software), retrieveSoftwareShortcutSummary(context, ((TextView) findViewById.findViewById(R.id.summary)).getLineHeight()), retrieveSoftwareShortcutImageResId(context));
    }

    private static void initSoftwareShortcutForSUW(Context context, View view) {
        View findViewById = view.findViewById(R.id.software_shortcut);
        setupShortcutWidget(findViewById, context.getText(R.string.accessibility_shortcut_edit_dialog_title_software), retrieveSoftwareShortcutSummaryForSUW(context, ((TextView) findViewById.findViewById(R.id.summary)).getLineHeight()), retrieveSoftwareShortcutImageResId(context));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getCustomizeAccessibilityButtonLink$4(Context context, View view) {
        new SubSettingLauncher(context).setDestination(AccessibilityButtonFragment.class.getName()).setSourceMetricsCategory(1873).launch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$initAdvancedWidget$3(LinearLayout linearLayout, View view, View view2) {
        linearLayout.setVisibility(8);
        view.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setCustomButtonsClickListener$1(CustomButtonsClickListener customButtonsClickListener, Dialog dialog, View view) {
        if (customButtonsClickListener != null) {
            customButtonsClickListener.onClick(1);
        }
        dialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setCustomButtonsClickListener$2(CustomButtonsClickListener customButtonsClickListener, Dialog dialog, View view) {
        if (customButtonsClickListener != null) {
            customButtonsClickListener.onClick(2);
        }
        dialog.dismiss();
    }

    private static int retrieveSoftwareShortcutImageResId(Context context) {
        return AccessibilityUtil.isFloatingMenuEnabled(context) ? R.drawable.accessibility_shortcut_type_software_floating : R.drawable.accessibility_shortcut_type_software;
    }

    private static CharSequence retrieveSoftwareShortcutSummary(Context context, int i) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (!AccessibilityUtil.isFloatingMenuEnabled(context)) {
            spannableStringBuilder.append((CharSequence) getSummaryStringWithIcon(context, i));
            spannableStringBuilder.append((CharSequence) "\n\n");
        }
        spannableStringBuilder.append(getCustomizeAccessibilityButtonLink(context));
        return spannableStringBuilder;
    }

    private static CharSequence retrieveSoftwareShortcutSummaryForSUW(Context context, int i) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (!AccessibilityUtil.isFloatingMenuEnabled(context)) {
            spannableStringBuilder.append((CharSequence) getSummaryStringWithIcon(context, i));
        }
        return spannableStringBuilder;
    }

    private static void setCustomButtonsClickListener(final Dialog dialog, View view, final CustomButtonsClickListener customButtonsClickListener, final CustomButtonsClickListener customButtonsClickListener2) {
        Button button = (Button) view.findViewById(R.id.custom_positive_button);
        Button button2 = (Button) view.findViewById(R.id.custom_negative_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityDialogUtils$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    AccessibilityDialogUtils.lambda$setCustomButtonsClickListener$1(AccessibilityDialogUtils.CustomButtonsClickListener.this, dialog, view2);
                }
            });
        }
        if (button2 != null) {
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityDialogUtils$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    AccessibilityDialogUtils.lambda$setCustomButtonsClickListener$2(AccessibilityDialogUtils.CustomButtonsClickListener.this, dialog, view2);
                }
            });
        }
    }

    private static void setScrollIndicators(View view) {
        view.setScrollIndicators(3, 3);
    }

    private static void setScrollIndicators(AlertDialog alertDialog) {
        setScrollIndicators((ScrollView) alertDialog.findViewById(R.id.container_layout));
    }

    private static void setupShortcutWidget(View view, CharSequence charSequence, CharSequence charSequence2, int i) {
        ((CheckBox) view.findViewById(R.id.checkbox)).setText(charSequence);
        TextView textView = (TextView) view.findViewById(R.id.summary);
        if (TextUtils.isEmpty(charSequence2)) {
            textView.setVisibility(8);
        } else {
            textView.setText(charSequence2);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setFocusable(false);
        }
        ((ImageView) view.findViewById(R.id.image)).setImageResource(i);
    }

    public static AlertDialog showEditShortcutDialog(Context context, int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        AlertDialog createDialog = createDialog(context, i, charSequence, onClickListener);
        createDialog.show();
        setScrollIndicators(createDialog);
        return createDialog;
    }
}
