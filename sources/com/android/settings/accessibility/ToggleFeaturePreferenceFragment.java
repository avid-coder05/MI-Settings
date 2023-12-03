package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.accessibility.ShortcutPreference;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.LocaleUtils;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.widget.IllustrationPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import miui.provider.ExtraNetwork;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public abstract class ToggleFeaturePreferenceFragment extends SettingsPreferenceFragment implements ShortcutPreference.OnClickCallback, OnMainSwitchChangeListener {
    private LottieAnimationPreference mAnimatedImagePreference;
    protected ComponentName mComponentName;
    private CharSequence mDescription;
    private CheckBox mHardwareTypeCheckBox;
    protected CharSequence mHtmlDescription;
    private ImageView mImageGetterCacheView;
    protected Uri mImageUri;
    protected CharSequence mPackageName;
    protected String mPreferenceKey;
    private SettingsContentObserver mSettingsContentObserver;
    protected Intent mSettingsIntent;
    protected Preference mSettingsPreference;
    protected CharSequence mSettingsTitle;
    protected ShortcutPreference mShortcutPreference;
    private CheckBox mSoftwareTypeCheckBox;
    protected SettingsMainSwitchPreference mToggleServiceSwitchPreference;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;
    protected int mSavedCheckBoxValue = -1;
    private final Html.ImageGetter mImageGetter = new Html.ImageGetter() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda1
        @Override // android.text.Html.ImageGetter
        public final Drawable getDrawable(String str) {
            Drawable lambda$new$0;
            lambda$new$0 = ToggleFeaturePreferenceFragment.this.lambda$new$0(str);
            return lambda$new$0;
        }
    };

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x004d, code lost:
    
        if (r7.equals("accessibility_display_magnification_enabled") == false) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean customAccessibilityImage() {
        /*
            Method dump skipped, instructions count: 352
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.accessibility.ToggleFeaturePreferenceFragment.customAccessibilityImage():boolean");
    }

    private CharSequence generateFooterContentDescription(CharSequence charSequence) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getPrefContext().getString(R.string.accessibility_introduction_title, this.mPackageName));
        stringBuffer.append("\n\n");
        stringBuffer.append(charSequence);
        return stringBuffer;
    }

    private Drawable getDrawableFromUri(Uri uri) {
        if (this.mImageGetterCacheView == null) {
            this.mImageGetterCacheView = new ImageView(getPrefContext());
        }
        this.mImageGetterCacheView.setAdjustViewBounds(true);
        this.mImageGetterCacheView.setImageURI(uri);
        if (this.mImageGetterCacheView.getDrawable() == null) {
            return null;
        }
        Drawable newDrawable = this.mImageGetterCacheView.getDrawable().mutate().getConstantState().newDrawable();
        this.mImageGetterCacheView.setImageURI(null);
        int intrinsicWidth = newDrawable.getIntrinsicWidth();
        int intrinsicHeight = newDrawable.getIntrinsicHeight();
        int screenHeightPixels = AccessibilityUtil.getScreenHeightPixels(getPrefContext()) / 2;
        if (intrinsicWidth > AccessibilityUtil.getScreenWidthPixels(getPrefContext()) || intrinsicHeight > screenHeightPixels) {
            return null;
        }
        newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
        return newDrawable;
    }

    private boolean hasShortcutType(int i, int i2) {
        return (i & i2) == i2;
    }

    private void initAnimatedImagePreference() {
        if (customAccessibilityImage() || this.mImageUri == null) {
            return;
        }
        IllustrationPreference illustrationPreference = new IllustrationPreference(getPrefContext());
        illustrationPreference.setImageUri(this.mImageUri);
        illustrationPreference.setSelectable(false);
        illustrationPreference.setKey("animated_image");
        getPreferenceScreen().addPreference(illustrationPreference);
    }

    private void initFooterCategory() {
        if (TextUtils.equals(this.mPackageName, getString(R.string.accessibility_menu_service_name))) {
            return;
        }
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
        preferenceCategory.setKey("footer_categories");
        preferenceCategory.setTitle(getString(R.string.accessibility_introduction_title, this.mPackageName));
        getPreferenceScreen().addPreference(preferenceCategory);
    }

    private void initFooterPreference() {
        if (!TextUtils.isEmpty(this.mDescription)) {
            createFooterPreference(getPreferenceScreen(), this.mDescription, getString(R.string.accessibility_introduction_title, this.mPackageName));
        }
        if (TextUtils.isEmpty(this.mHtmlDescription) && TextUtils.isEmpty(this.mDescription)) {
            createFooterPreference(getPreferenceScreen(), getText(R.string.accessibility_service_default_description), getString(R.string.accessibility_introduction_title, this.mPackageName));
        }
    }

    private void initGeneralCategory() {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
        preferenceCategory.setKey("general_categories");
        preferenceCategory.setTitle(R.string.accessibility_screen_option);
        getPreferenceScreen().addPreference(preferenceCategory);
    }

    private void initHtmlTextPreference() {
        if (TextUtils.isEmpty(this.mHtmlDescription)) {
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Spanned fromHtml = Html.fromHtml(this.mHtmlDescription.toString(), 63, this.mImageGetter, null);
        getString(R.string.accessibility_introduction_title, this.mPackageName);
        AccessibilityFooterPreference accessibilityFooterPreference = new AccessibilityFooterPreference(preferenceScreen.getContext());
        accessibilityFooterPreference.setKey("html_description");
        accessibilityFooterPreference.setSummary(fromHtml);
        accessibilityFooterPreference.setContentDescription(generateFooterContentDescription(fromHtml));
        if (getHelpResource() != 0) {
            accessibilityFooterPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ToggleFeaturePreferenceFragment.this.lambda$initHtmlTextPreference$2(view);
                }
            });
            accessibilityFooterPreference.setLearnMoreContentDescription(getPrefContext().getString(R.string.footer_learn_more_content_description, this.mPackageName));
            accessibilityFooterPreference.setLinkEnabled(true);
        } else {
            accessibilityFooterPreference.setLinkEnabled(false);
        }
        preferenceScreen.addPreference(accessibilityFooterPreference);
    }

    private void initToggleServiceSwitchPreference() {
        SettingsMainSwitchPreference settingsMainSwitchPreference = new SettingsMainSwitchPreference(getPrefContext());
        this.mToggleServiceSwitchPreference = settingsMainSwitchPreference;
        settingsMainSwitchPreference.setKey("use_service");
        if (getArguments().containsKey("checked")) {
            this.mToggleServiceSwitchPreference.setChecked(getArguments().getBoolean("checked"));
        }
        getPreferenceScreen().addPreference(this.mToggleServiceSwitchPreference);
    }

    private void installActionBarToggleSwitch() {
        onInstallSwitchPreferenceToggleSwitch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createFooterPreference$3(View view) {
        view.startActivityForResult(HelpUtils.getHelpIntent(getContext(), getContext().getString(getHelpResource()), getContext().getClass().getName()), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initHtmlTextPreference$2(View view) {
        view.startActivityForResult(HelpUtils.getHelpIntent(getContext(), getContext().getString(getHelpResource()), getContext().getClass().getName()), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Drawable lambda$new$0(String str) {
        if (str == null || !str.startsWith("R.drawable.")) {
            return null;
        }
        return getDrawableFromUri(Uri.parse("android.resource://" + this.mComponentName.getPackageName() + "/drawable/" + str.substring(11)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$1(boolean z) {
        removeDialog(1);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    private void removeActionBarToggleSwitch() {
        this.mToggleServiceSwitchPreference.setOnPreferenceChangeListener(null);
        onRemoveSwitchPreferenceToggleSwitch();
    }

    private int restoreOnConfigChangedValue() {
        int i = this.mSavedCheckBoxValue;
        this.mSavedCheckBoxValue = -1;
        return i;
    }

    private void setDialogTextAreaClickListener(View view, final CheckBox checkBox) {
        view.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                checkBox.toggle();
            }
        });
    }

    private static void setupDefaultShortcutIfNecessary(Context context) {
        ComponentName unflattenFromString;
        if (TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), "accessibility_shortcut_target_service"))) {
            String shortcutTargetServiceComponentNameString = AccessibilityUtils.getShortcutTargetServiceComponentNameString(context, UserHandle.myUserId());
            if (TextUtils.isEmpty(shortcutTargetServiceComponentNameString) || (unflattenFromString = ComponentName.unflattenFromString(shortcutTargetServiceComponentNameString)) == null) {
                return;
            }
            Settings.Secure.putString(context.getContentResolver(), "accessibility_shortcut_target_service", unflattenFromString.flattenToString());
        }
    }

    private void updateEditShortcutDialogCheckBox() {
        int restoreOnConfigChangedValue = restoreOnConfigChangedValue();
        if (restoreOnConfigChangedValue == -1) {
            restoreOnConfigChangedValue = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1);
            if (!this.mShortcutPreference.isChecked()) {
                restoreOnConfigChangedValue = 0;
            }
        }
        this.mSoftwareTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 1));
        this.mHardwareTypeCheckBox.setChecked(hasShortcutType(restoreOnConfigChangedValue, 2));
    }

    private void updatePreferenceOrder() {
        List<String> preferenceOrderList = getPreferenceOrderList();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.setOrderingAsAdded(false);
        int size = preferenceOrderList.size();
        for (int i = 0; i < size; i++) {
            Preference findPreference = preferenceScreen.findPreference(preferenceOrderList.get(i));
            if (findPreference != null) {
                findPreference.setOrder(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        if (this.mComponentName == null) {
            return;
        }
        int shortcutTypeCheckBoxValue = getShortcutTypeCheckBoxValue();
        saveNonEmptyUserShortcutType(shortcutTypeCheckBoxValue);
        AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), shortcutTypeCheckBoxValue, this.mComponentName);
        AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), ~shortcutTypeCheckBoxValue, this.mComponentName);
        this.mShortcutPreference.setChecked(shortcutTypeCheckBoxValue != 0);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    void createFooterPreference(PreferenceScreen preferenceScreen, CharSequence charSequence, String str) {
        AccessibilityFooterPreference accessibilityFooterPreference = new AccessibilityFooterPreference(preferenceScreen.getContext());
        accessibilityFooterPreference.setSummary(charSequence);
        accessibilityFooterPreference.setContentDescription(generateFooterContentDescription(charSequence));
        if (getHelpResource() != 0) {
            accessibilityFooterPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ToggleFeaturePreferenceFragment.this.lambda$createFooterPreference$3(view);
                }
            });
            accessibilityFooterPreference.setLearnMoreContentDescription(getPrefContext().getString(R.string.footer_learn_more_content_description, this.mPackageName));
        }
        preferenceScreen.addPreference(accessibilityFooterPreference);
    }

    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 4;
    }

    protected List<String> getPreferenceOrderList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("animated_image");
        arrayList.add("use_service");
        arrayList.add("general_categories");
        arrayList.add("footer_categories");
        arrayList.add("html_description");
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getShortcutPreferenceKey() {
        return "shortcut_preference";
    }

    protected int getShortcutTypeCheckBoxValue() {
        CheckBox checkBox = this.mSoftwareTypeCheckBox;
        if (checkBox == null || this.mHardwareTypeCheckBox == null) {
            return -1;
        }
        boolean isChecked = checkBox.isChecked();
        return this.mHardwareTypeCheckBox.isChecked() ? (isChecked ? 1 : 0) | 2 : isChecked ? 1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CharSequence getShortcutTypeSummary(Context context) {
        if (this.mShortcutPreference.isSettingsEditable()) {
            if (this.mShortcutPreference.isChecked()) {
                int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(context, this.mComponentName.flattenToString(), 1);
                ArrayList arrayList = new ArrayList();
                CharSequence text = context.getText(R.string.accessibility_shortcut_edit_summary_software);
                if (hasShortcutType(retrieveUserShortcutType, 1)) {
                    arrayList.add(text);
                }
                if (hasShortcutType(retrieveUserShortcutType, 2)) {
                    arrayList.add(context.getText(R.string.accessibility_shortcut_hardware_keyword));
                }
                if (arrayList.isEmpty()) {
                    arrayList.add(text);
                }
                return CaseMap.toTitle().wholeString().noLowercase().apply(Locale.getDefault(), null, LocaleUtils.getConcatenatedString(arrayList));
            }
            return context.getText(R.string.switch_off_text);
        }
        return context.getText(R.string.accessibility_shortcut_edit_dialog_title_hardware);
    }

    abstract int getUserShortcutTypes();

    protected void initSettingsPreference() {
        if (this.mSettingsTitle == null || this.mSettingsIntent == null) {
            return;
        }
        Preference preference = new Preference(getPrefContext());
        this.mSettingsPreference = preference;
        preference.setTitle(this.mSettingsTitle);
        this.mSettingsPreference.setIconSpaceReserved(false);
        this.mSettingsPreference.setIntent(this.mSettingsIntent);
        ((PreferenceCategory) findPreference("general_categories")).addPreference(this.mSettingsPreference);
    }

    protected void initShortcutPreference() {
        ShortcutPreference shortcutPreference = new ShortcutPreference(getPrefContext(), null);
        this.mShortcutPreference = shortcutPreference;
        shortcutPreference.setPersistent(false);
        this.mShortcutPreference.setKey(getShortcutPreferenceKey());
        this.mShortcutPreference.setOnClickCallback(this);
        this.mShortcutPreference.setTitle(getString(R.string.accessibility_shortcut_title, this.mPackageName));
        ((PreferenceCategory) findPreference("general_categories")).addPreference(this.mShortcutPreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null && bundle.containsKey("shortcut_type")) {
            this.mSavedCheckBoxValue = bundle.getInt("shortcut_type", -1);
        }
        setupDefaultShortcutIfNecessary(getPrefContext());
        if (getPreferenceScreenResId() <= 0) {
            setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getPrefContext()));
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        this.mSettingsContentObserver = new SettingsContentObserver(new Handler(), arrayList) { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                ToggleFeaturePreferenceFragment.this.updateShortcutPreferenceData();
                ToggleFeaturePreferenceFragment.this.updateShortcutPreference();
            }
        };
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            AlertDialog showEditShortcutDialog = AccessibilityDialogUtils.showEditShortcutDialog(getPrefContext(), WizardManagerHelper.isAnySetupWizard(getIntent()) ? 1 : 0, getPrefContext().getString(R.string.accessibility_shortcut_title, this.mPackageName), new DialogInterface.OnClickListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    ToggleFeaturePreferenceFragment.this.callOnAlertDialogCheckboxClicked(dialogInterface, i2);
                }
            });
            setupEditShortcutDialog(showEditShortcutDialog);
            return showEditShortcutDialog;
        } else if (i == 1008) {
            AlertDialog createAccessibilityTutorialDialog = AccessibilityGestureNavigationTutorial.createAccessibilityTutorialDialog(getPrefContext(), getUserShortcutTypes());
            createAccessibilityTutorialDialog.setCanceledOnTouchOutside(false);
            return createAccessibilityTutorialDialog;
        } else {
            throw new IllegalArgumentException("Unsupported dialogId " + i);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        onProcessArguments(getArguments());
        initAnimatedImagePreference();
        initToggleServiceSwitchPreference();
        initGeneralCategory();
        initShortcutPreference();
        initSettingsPreference();
        initFooterCategory();
        initHtmlTextPreference();
        initFooterPreference();
        installActionBarToggleSwitch();
        updateToggleServiceTitle(this.mToggleServiceSwitchPreference);
        this.mTouchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() { // from class: com.android.settings.accessibility.ToggleFeaturePreferenceFragment$$ExternalSyntheticLambda5
            @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
            public final void onTouchExplorationStateChanged(boolean z) {
                ToggleFeaturePreferenceFragment.this.lambda$onCreateView$1(z);
            }
        };
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mAnimatedImagePreference.cancelAnimation();
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        removeActionBarToggleSwitch();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onInstallSwitchPreferenceToggleSwitch() {
        updateSwitchBarToggleSwitch();
        this.mToggleServiceSwitchPreference.addOnSwitchChangeListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.unregister(getContentResolver());
        super.onPause();
    }

    protected abstract void onPreferenceToggled(String str, boolean z);

    /* JADX INFO: Access modifiers changed from: protected */
    public void onProcessArguments(Bundle bundle) {
        this.mPreferenceKey = bundle.getString("preference_key");
        if (bundle.containsKey("resolve_info")) {
            getActivity().setTitle(((ResolveInfo) bundle.getParcelable("resolve_info")).loadLabel(getPackageManager()).toString());
        } else if (bundle.containsKey("title")) {
            setTitle(bundle.getString("title"));
        }
        if (bundle.containsKey(FunctionColumns.SUMMARY)) {
            this.mDescription = bundle.getCharSequence(FunctionColumns.SUMMARY);
        }
        if (bundle.containsKey("html_description")) {
            this.mHtmlDescription = bundle.getCharSequence("html_description");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRemoveSwitchPreferenceToggleSwitch() {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.register(getContentResolver());
        updateShortcutPreferenceData();
        updateShortcutPreference();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        int shortcutTypeCheckBoxValue = getShortcutTypeCheckBoxValue();
        if (shortcutTypeCheckBoxValue != -1) {
            bundle.putInt("shortcut_type", shortcutTypeCheckBoxValue);
        }
        super.onSaveInstanceState(bundle);
    }

    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        showDialog(1);
    }

    public void onSwitchChanged(Switch r1, boolean z) {
        onPreferenceToggled(this.mPreferenceKey, z);
    }

    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        if (this.mComponentName == null) {
            return;
        }
        int retrieveUserShortcutType = PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1);
        if (shortcutPreference.isChecked()) {
            AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), retrieveUserShortcutType, this.mComponentName);
            showDialog(PageIndexManager.PAGE_FACTORY_RESET);
        } else {
            AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), retrieveUserShortcutType, this.mComponentName);
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getActivity() instanceof SettingsActivity) {
            ((SettingsActivity) getActivity()).getSwitchBar().hide();
        }
        updatePreferenceOrder();
    }

    void saveNonEmptyUserShortcutType(int i) {
        if (i == 0) {
            return;
        }
        PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(this.mComponentName.flattenToString(), i));
    }

    public void setTitle(String str) {
        getActivity().setTitle(str);
    }

    void setupEditShortcutDialog(Dialog dialog) {
        View findViewById = dialog.findViewById(R.id.software_shortcut);
        int i = R.id.checkbox;
        CheckBox checkBox = (CheckBox) findViewById.findViewById(i);
        this.mSoftwareTypeCheckBox = checkBox;
        setDialogTextAreaClickListener(findViewById, checkBox);
        View findViewById2 = dialog.findViewById(R.id.hardware_shortcut);
        CheckBox checkBox2 = (CheckBox) findViewById2.findViewById(i);
        this.mHardwareTypeCheckBox = checkBox2;
        setDialogTextAreaClickListener(findViewById2, checkBox2);
        updateEditShortcutDialogCheckBox();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startAccessibilityShortcutTypeActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("component_name", this.mComponentName);
        intent.putExtra("dialog_type", 0);
        intent.putExtra(ExtraNetwork.FIREWALL_PACKAGE_NAME, this.mPackageName);
        intent.setClass(getPrefContext(), AccessibilityShortcutTypeActivity.class);
        intent.putExtra(":settings:show_fragment_args", bundle);
        getPrefContext().startActivity(intent);
    }

    protected void updateShortcutPreference() {
        if (this.mComponentName == null) {
            return;
        }
        this.mShortcutPreference.setChecked(AccessibilityUtil.hasValuesInSettings(getPrefContext(), PreferredShortcuts.retrieveUserShortcutType(getPrefContext(), this.mComponentName.flattenToString(), 1), this.mComponentName));
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    protected void updateShortcutPreferenceData() {
        int userShortcutTypesFromSettings;
        if (this.mComponentName == null || (userShortcutTypesFromSettings = AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName)) == 0) {
            return;
        }
        PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(this.mComponentName.flattenToString(), userShortcutTypesFromSettings));
    }

    protected void updateSwitchBarToggleSwitch() {
    }

    protected void updateToggleServiceTitle(SettingsMainSwitchPreference settingsMainSwitchPreference) {
        settingsMainSwitchPreference.setTitle(R.string.accessibility_service_primary_switch_title);
    }
}
