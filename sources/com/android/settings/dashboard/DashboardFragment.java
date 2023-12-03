package com.android.settings.dashboard;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.CategoryMixin;
import com.android.settings.core.PreferenceControllerListHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.PrimarySwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.ProviderTile;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.miuisettings.preference.PreferenceUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import miui.yellowpage.YellowPageStatistic;

/* loaded from: classes.dex */
public abstract class DashboardFragment extends SettingsPreferenceFragment implements CategoryMixin.CategoryListener, PreferenceGroup.OnExpandButtonClickListener, BasePreferenceController.UiBlockListener {
    UiBlockerController mBlockerController;
    private DashboardFeatureProvider mDashboardFeatureProvider;
    private boolean mListeningToCategoryChange;
    private DashboardTilePlaceholderPreferenceController mPlaceholderPreferenceController;
    private List<String> mSuppressInjectedTileKeys;
    final ArrayMap<String, List<DynamicDataObserver>> mDashboardTilePrefKeys = new ArrayMap<>();
    private final Map<Class, List<AbstractPreferenceController>> mPreferenceControllers = new ArrayMap();
    private final List<DynamicDataObserver> mRegisteredObservers = new ArrayList();
    private final List<AbstractPreferenceController> mControllers = new ArrayList();

    private void displayResourceTiles() {
        if (getPreferenceScreenResId() <= 0) {
            return;
        }
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mPreferenceControllers.values().stream().flatMap(DashboardFragment$$ExternalSyntheticLambda10.INSTANCE).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractPreferenceController) obj).displayPreference(PreferenceScreen.this);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUiBlocker$2(List list, AbstractPreferenceController abstractPreferenceController) {
        if ((abstractPreferenceController instanceof BasePreferenceController.UiBlocker) && abstractPreferenceController.isAvailable()) {
            ((BasePreferenceController) abstractPreferenceController).setUiBlockListener(this);
            list.add(abstractPreferenceController.getPreferenceKey());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUiBlocker$3() {
        updatePreferenceVisibility(this.mPreferenceControllers);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onAttach$0(Lifecycle lifecycle, BasePreferenceController basePreferenceController) {
        if (basePreferenceController instanceof LifecycleObserver) {
            lifecycle.addObserver((LifecycleObserver) basePreferenceController);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onAttach$1(int i, AbstractPreferenceController abstractPreferenceController) {
        if (abstractPreferenceController instanceof BasePreferenceController) {
            ((BasePreferenceController) abstractPreferenceController).setMetricsCategory(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Preference lambda$onCreatePreferences$4(AbstractPreferenceController abstractPreferenceController) {
        return findPreference(abstractPreferenceController.getPreferenceKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreatePreferences$5(Preference preference) {
        preference.getExtras().putInt(YellowPageStatistic.Display.CATEGORY, getMetricsCategory());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$6(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        if (this.mRegisteredObservers.contains(dynamicDataObserver)) {
            return;
        }
        lambda$registerDynamicDataObservers$8(contentResolver, dynamicDataObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterDynamicDataObservers$9(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        Log.d("DashboardFragment", "unregister observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        this.mRegisteredObservers.remove(dynamicDataObserver);
        contentResolver.unregisterContentObserver(dynamicDataObserver);
    }

    private void refreshAllPreferences(String str) {
        displayResourceTiles();
        refreshDashboardTiles(str);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Log.d(str, "All preferences added, reporting fully drawn");
            activity.reportFullyDrawn();
        }
        updatePreferenceVisibility(this.mPreferenceControllers);
    }

    private void refreshDashboardTiles(String str) {
        String str2 = str;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        DashboardCategory tilesForCategory = this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey());
        if (tilesForCategory == null) {
            Log.d(str2, "NO dashboard tiles for " + str2);
            return;
        }
        List<Tile> tiles = tilesForCategory.getTiles();
        if (tiles == null) {
            Log.d(str2, "tile list is empty, skipping category " + tilesForCategory.key);
            return;
        }
        ArrayMap arrayMap = new ArrayMap(this.mDashboardTilePrefKeys);
        boolean shouldForceRoundedIcon = shouldForceRoundedIcon();
        for (Tile tile : tiles) {
            String dashboardKeyForTile = this.mDashboardFeatureProvider.getDashboardKeyForTile(tile);
            if (TextUtils.isEmpty(dashboardKeyForTile)) {
                Log.d(str2, "tile does not contain a key, skipping " + tile);
            } else if (displayTile(tile)) {
                tile.getMetaData().remove("com.android.settings.icon_uri");
                if (this.mDashboardTilePrefKeys.containsKey(dashboardKeyForTile)) {
                    this.mDashboardFeatureProvider.bindPreferenceToTileAndGetObservers(getActivity(), shouldForceRoundedIcon, getMetricsCategory(), preferenceScreen.findPreference(dashboardKeyForTile), tile, dashboardKeyForTile, this.mPlaceholderPreferenceController.getOrder());
                } else {
                    Preference createPreference = createPreference(tile);
                    List<DynamicDataObserver> bindPreferenceToTileAndGetObservers = this.mDashboardFeatureProvider.bindPreferenceToTileAndGetObservers(getActivity(), shouldForceRoundedIcon, getMetricsCategory(), createPreference, tile, dashboardKeyForTile, this.mPlaceholderPreferenceController.getOrder());
                    preferenceScreen.addPreference(createPreference);
                    registerDynamicDataObservers(bindPreferenceToTileAndGetObservers);
                    this.mDashboardTilePrefKeys.put(dashboardKeyForTile, bindPreferenceToTileAndGetObservers);
                }
                DashBoardTileInjector.setTileSummary(getContext(), tile);
                arrayMap.remove(dashboardKeyForTile);
                str2 = str;
            }
        }
        for (Map.Entry entry : arrayMap.entrySet()) {
            String str3 = (String) entry.getKey();
            this.mDashboardTilePrefKeys.remove(str3);
            Preference findPreference = preferenceScreen.findPreference(str3);
            if (findPreference != null) {
                preferenceScreen.removePreference(findPreference);
            }
            unregisterDynamicDataObservers((List) entry.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: registerDynamicDataObserver  reason: merged with bridge method [inline-methods] */
    public void lambda$registerDynamicDataObservers$8(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        Log.d("DashboardFragment", "register observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        contentResolver.registerContentObserver(dynamicDataObserver.getUri(), false, dynamicDataObserver);
        this.mRegisteredObservers.add(dynamicDataObserver);
    }

    private void unregisterDynamicDataObservers(List<DynamicDataObserver> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        final ContentResolver contentResolver = getContentResolver();
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$unregisterDynamicDataObservers$9(contentResolver, (DynamicDataObserver) obj);
            }
        });
    }

    protected void addPreferenceController(AbstractPreferenceController abstractPreferenceController) {
        if (this.mPreferenceControllers.get(abstractPreferenceController.getClass()) == null) {
            this.mPreferenceControllers.put(abstractPreferenceController.getClass(), new ArrayList());
        }
        this.mPreferenceControllers.get(abstractPreferenceController.getClass()).add(abstractPreferenceController);
    }

    void checkUiBlocker(List<AbstractPreferenceController> list) {
        final ArrayList arrayList = new ArrayList();
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$checkUiBlocker$2(arrayList, (AbstractPreferenceController) obj);
            }
        });
        if (arrayList.isEmpty()) {
            return;
        }
        UiBlockerController uiBlockerController = new UiBlockerController(arrayList);
        this.mBlockerController = uiBlockerController;
        uiBlockerController.start(new Runnable() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DashboardFragment.this.lambda$checkUiBlocker$3();
            }
        });
    }

    Preference createPreference(Tile tile) {
        return tile instanceof ProviderTile ? new SwitchPreference(getPrefContext()) : tile.hasSwitch() ? new PrimarySwitchPreference(getPrefContext()) : new Preference(getPrefContext());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean displayTile(Tile tile) {
        if (this.mSuppressInjectedTileKeys == null || !tile.hasKey()) {
            return true;
        }
        return !this.mSuppressInjectedTileKeys.contains(tile.getKey(getContext()));
    }

    public String getCategoryKey() {
        return DashboardFragmentRegistry.PARENT_TO_CATEGORY_KEY_MAP.get(getClass().getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getLogTag();

    /* JADX INFO: Access modifiers changed from: protected */
    public Collection<List<AbstractPreferenceController>> getPreferenceControllers() {
        return this.mPreferenceControllers.values();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public abstract int getPreferenceScreenResId();

    protected boolean isParalleledControllers() {
        return false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSuppressInjectedTileKeys = Arrays.asList(context.getResources().getStringArray(R.array.config_suppress_injected_tile_keys));
        this.mDashboardFeatureProvider = FeatureFactory.getFactory(context).getDashboardFeatureProvider(context);
        List<AbstractPreferenceController> createPreferenceControllers = createPreferenceControllers(context);
        List<BasePreferenceController> filterControllers = PreferenceControllerListHelper.filterControllers(PreferenceControllerListHelper.getPreferenceControllersFromXml(context, getPreferenceScreenResId()), createPreferenceControllers);
        if (createPreferenceControllers != null) {
            this.mControllers.addAll(createPreferenceControllers);
        }
        this.mControllers.addAll(filterControllers);
        final Lifecycle settingsLifecycle = getSettingsLifecycle();
        filterControllers.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.lambda$onAttach$0(Lifecycle.this, (BasePreferenceController) obj);
            }
        });
        final int metricsCategory = getMetricsCategory();
        this.mControllers.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.lambda$onAttach$1(metricsCategory, (AbstractPreferenceController) obj);
            }
        });
        DashboardTilePlaceholderPreferenceController dashboardTilePlaceholderPreferenceController = new DashboardTilePlaceholderPreferenceController(context);
        this.mPlaceholderPreferenceController = dashboardTilePlaceholderPreferenceController;
        this.mControllers.add(dashboardTilePlaceholderPreferenceController);
        Iterator<AbstractPreferenceController> it = this.mControllers.iterator();
        while (it.hasNext()) {
            addPreferenceController(it.next());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onBindPreferences() {
        super.onBindPreferences();
        refreshAllPreferences(getLogTag());
    }

    @Override // com.android.settings.core.BasePreferenceController.UiBlockListener
    public void onBlockerWorkFinished(BasePreferenceController basePreferenceController) {
        this.mBlockerController.countDown(basePreferenceController.getPreferenceKey());
    }

    @Override // com.android.settings.core.CategoryMixin.CategoryListener
    public void onCategoriesChanged(Set<String> set) {
        String categoryKey = getCategoryKey();
        if (this.mDashboardFeatureProvider.getTilesForCategory(categoryKey) == null) {
            return;
        }
        if (set == null) {
            refreshDashboardTiles(getLogTag());
        } else if (set.contains(categoryKey)) {
            Log.i("DashboardFragment", "refresh tiles for " + categoryKey);
            refreshDashboardTiles(getLogTag());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        checkUiBlocker(this.mControllers);
        this.mControllers.stream().map(new Function() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Preference lambda$onCreatePreferences$4;
                lambda$onCreatePreferences$4 = DashboardFragment.this.lambda$onCreatePreferences$4((AbstractPreferenceController) obj);
                return lambda$onCreatePreferences$4;
            }
        }).filter(new Predicate() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda11
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((Preference) obj);
            }
        }).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$onCreatePreferences$5((Preference) obj);
            }
        });
        super.onCreatePreferences(bundle, str);
    }

    public void onExpandButtonClick() {
        this.mMetricsFeatureProvider.action(0, 834, getMetricsCategory(), null, 0);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        Iterator<List<AbstractPreferenceController>> it = this.mPreferenceControllers.values().iterator();
        while (it.hasNext()) {
            Iterator<AbstractPreferenceController> it2 = it.next().iterator();
            while (it2.hasNext()) {
                if (it2.next().handlePreferenceTreeClick(preference)) {
                    writePreferenceClickMetric(preference);
                    return true;
                }
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updatePreferenceStates();
        writeElapsedTimeMetric(1729, "isParalleledControllers:" + isParalleledControllers());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey()) == null) {
            return;
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof CategoryMixin.CategoryHandler) {
            this.mListeningToCategoryChange = true;
            ((CategoryMixin.CategoryHandler) activity).getCategoryMixin().addCategoryListener(this);
        }
        final ContentResolver contentResolver = getContentResolver();
        this.mDashboardTilePrefKeys.values().stream().filter(new Predicate() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda12
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((List) obj);
            }
        }).flatMap(DashboardFragment$$ExternalSyntheticLambda10.INSTANCE).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$onStart$6(contentResolver, (DynamicDataObserver) obj);
            }
        });
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        unregisterDynamicDataObservers(new ArrayList(this.mRegisteredObservers));
        if (this.mListeningToCategoryChange) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CategoryMixin.CategoryHandler) {
                ((CategoryMixin.CategoryHandler) activity).getCategoryMixin().removeCategoryListener(this);
            }
            this.mListeningToCategoryChange = false;
        }
    }

    void registerDynamicDataObservers(List<DynamicDataObserver> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        final ContentResolver contentResolver = getContentResolver();
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$registerDynamicDataObservers$8(contentResolver, (DynamicDataObserver) obj);
            }
        });
    }

    protected boolean shouldForceRoundedIcon() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateActionBarTitleView(int i) {
        TextView textView;
        TextView textView2 = (TextView) getActivity().findViewById(R.id.action_bar_title);
        if (textView2 != null) {
            textView2.setText(i);
        }
        View findViewById = getActivity().findViewById(R.id.action_bar_movable_container);
        if (findViewById == null || (textView = (TextView) findViewById.findViewById(R.id.action_bar_title_expand)) == null) {
            return;
        }
        textView.setText(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePreferenceStates() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Iterator<List<AbstractPreferenceController>> it = this.mPreferenceControllers.values().iterator();
        while (it.hasNext()) {
            for (AbstractPreferenceController abstractPreferenceController : it.next()) {
                if (abstractPreferenceController.isAvailable()) {
                    String preferenceKey = abstractPreferenceController.getPreferenceKey();
                    if (TextUtils.isEmpty(preferenceKey)) {
                        Log.d("DashboardFragment", String.format("Preference key is %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                    } else {
                        Preference findPreference = preferenceScreen.findPreference(preferenceKey);
                        if (findPreference == null) {
                            Log.d("DashboardFragment", String.format("Cannot find preference with key %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                        } else {
                            abstractPreferenceController.updateState(findPreference);
                        }
                    }
                }
            }
        }
    }

    void updatePreferenceStatesInParallel() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Collection<List<AbstractPreferenceController>> values = this.mPreferenceControllers.values();
        ArrayList<ControllerFutureTask> arrayList = new ArrayList();
        Iterator<List<AbstractPreferenceController>> it = values.iterator();
        while (it.hasNext()) {
            Iterator<AbstractPreferenceController> it2 = it.next().iterator();
            while (it2.hasNext()) {
                ControllerFutureTask controllerFutureTask = new ControllerFutureTask(new ControllerTask(it2.next(), preferenceScreen, this.mMetricsFeatureProvider, getMetricsCategory()), null);
                arrayList.add(controllerFutureTask);
                ThreadUtils.postOnBackgroundThread(controllerFutureTask);
            }
        }
        for (ControllerFutureTask controllerFutureTask2 : arrayList) {
            try {
                controllerFutureTask2.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.w("DashboardFragment", controllerFutureTask2.getController().getPreferenceKey() + " " + e.getMessage());
            }
        }
    }

    void updatePreferenceVisibility(Map<Class, List<AbstractPreferenceController>> map) {
        UiBlockerController uiBlockerController;
        if (getPreferenceScreen() == null || map == null || (uiBlockerController = this.mBlockerController) == null) {
            return;
        }
        boolean isBlockerFinished = uiBlockerController.isBlockerFinished();
        Iterator<List<AbstractPreferenceController>> it = map.values().iterator();
        while (it.hasNext()) {
            for (AbstractPreferenceController abstractPreferenceController : it.next()) {
                Preference findPreference = findPreference(abstractPreferenceController.getPreferenceKey());
                if (findPreference != null) {
                    PreferenceUtils.setVisible(findPreference, isBlockerFinished && abstractPreferenceController.isAvailable());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T extends AbstractPreferenceController> T use(Class<T> cls) {
        List<AbstractPreferenceController> list = this.mPreferenceControllers.get(cls);
        if (list != null) {
            if (list.size() > 1) {
                Log.w("DashboardFragment", "Multiple controllers of Class " + cls.getSimpleName() + " found, returning first one.");
            }
            return (T) list.get(0);
        }
        return null;
    }
}
