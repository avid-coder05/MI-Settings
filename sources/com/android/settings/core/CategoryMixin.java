package com.android.settings.core;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.core.CategoryMixin;
import com.android.settings.dashboard.CategoryManager;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.drawer.Tile;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/* loaded from: classes.dex */
public class CategoryMixin implements LifecycleObserver {
    private static final ArraySet<ComponentName> sTileDenylist = new ArraySet<>();
    private int mCategoriesUpdateTaskCount;
    private final Context mContext;
    private final PackageReceiver mPackageReceiver = new PackageReceiver();
    private final List<CategoryListener> mCategoryListeners = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CategoriesUpdateTask extends AsyncTask<Boolean, Void, Set<String>> {
        private Context mApplicationContext;
        private final CategoryManager mCategoryManager;
        private WeakReference<CategoryMixin> mCategoryMixin;
        private Map<ComponentName, Tile> mPreviousTileMap;

        public CategoriesUpdateTask(CategoryMixin categoryMixin) {
            this.mCategoryMixin = new WeakReference<>(categoryMixin);
            CategoryMixin.access$108(categoryMixin);
            Context applicationContext = categoryMixin.mContext.getApplicationContext();
            this.mApplicationContext = applicationContext;
            this.mCategoryManager = CategoryManager.get(applicationContext);
        }

        private Set<String> getChangedCategories(boolean z) {
            if (z) {
                final ArraySet arraySet = new ArraySet();
                Map<ComponentName, Tile> tileByComponentMap = this.mCategoryManager.getTileByComponentMap();
                tileByComponentMap.forEach(new BiConsumer() { // from class: com.android.settings.core.CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda0
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        CategoryMixin.CategoriesUpdateTask.this.lambda$getChangedCategories$0(arraySet, (ComponentName) obj, (Tile) obj2);
                    }
                });
                ArraySet arraySet2 = new ArraySet(this.mPreviousTileMap.keySet());
                arraySet2.removeAll(tileByComponentMap.keySet());
                arraySet2.forEach(new Consumer() { // from class: com.android.settings.core.CategoryMixin$CategoriesUpdateTask$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        CategoryMixin.CategoriesUpdateTask.this.lambda$getChangedCategories$1(arraySet, (ComponentName) obj);
                    }
                });
                return arraySet;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getChangedCategories$0(Set set, ComponentName componentName, Tile tile) {
            Tile tile2 = this.mPreviousTileMap.get(componentName);
            if (tile2 == null) {
                Log.i("CategoryMixin", "Tile added: " + componentName.flattenToShortString());
                set.add(tile.getCategory());
            } else if (TextUtils.equals(tile.getTitle(this.mApplicationContext), tile2.getTitle(this.mApplicationContext)) && TextUtils.equals(tile.getSummary(this.mApplicationContext), tile2.getSummary(this.mApplicationContext))) {
            } else {
                Log.i("CategoryMixin", "Tile changed: " + componentName.flattenToShortString());
                set.add(tile.getCategory());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getChangedCategories$1(Set set, ComponentName componentName) {
            Log.i("CategoryMixin", "Tile removed: " + componentName.flattenToShortString());
            set.add(this.mPreviousTileMap.get(componentName).getCategory());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Set<String> doInBackground(Boolean... boolArr) {
            this.mPreviousTileMap = this.mCategoryManager.getTileByComponentMap();
            this.mCategoryManager.reloadAllCategories(this.mApplicationContext);
            this.mCategoryManager.updateCategoryFromDenylist(CategoryMixin.sTileDenylist);
            return getChangedCategories(boolArr[0].booleanValue());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Set<String> set) {
            WeakReference<CategoryMixin> weakReference = this.mCategoryMixin;
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            if (set == null || !set.isEmpty()) {
                this.mCategoryMixin.get().onCategoriesChanged(set);
            }
            CategoryMixin.access$110(this.mCategoryMixin.get());
        }
    }

    /* loaded from: classes.dex */
    public interface CategoryHandler {
        CategoryMixin getCategoryMixin();
    }

    /* loaded from: classes.dex */
    public interface CategoryListener {
        void onCategoriesChanged(Set<String> set);
    }

    /* loaded from: classes.dex */
    private class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            CategoryMixin.this.updateCategories(true);
        }
    }

    public CategoryMixin(Context context) {
        this.mContext = context;
    }

    static /* synthetic */ int access$108(CategoryMixin categoryMixin) {
        int i = categoryMixin.mCategoriesUpdateTaskCount;
        categoryMixin.mCategoriesUpdateTaskCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$110(CategoryMixin categoryMixin) {
        int i = categoryMixin.mCategoriesUpdateTaskCount;
        categoryMixin.mCategoriesUpdateTaskCount = i - 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCategories(boolean z) {
        if (this.mCategoriesUpdateTaskCount < 2) {
            new CategoriesUpdateTask(this).execute(Boolean.valueOf(z));
        }
    }

    public void addCategoryListener(CategoryListener categoryListener) {
        this.mCategoryListeners.add(categoryListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addToDenylist(ComponentName componentName) {
        sTileDenylist.add(componentName);
    }

    void onCategoriesChanged(final Set<String> set) {
        this.mCategoryListeners.forEach(new Consumer() { // from class: com.android.settings.core.CategoryMixin$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((CategoryMixin.CategoryListener) obj).onCategoriesChanged(set);
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mContext.unregisterReceiver(this.mPackageReceiver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme(FunctionColumns.PACKAGE);
        this.mContext.registerReceiver(this.mPackageReceiver, intentFilter);
        updateCategories();
    }

    public void removeCategoryListener(CategoryListener categoryListener) {
        this.mCategoryListeners.remove(categoryListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeFromDenylist(ComponentName componentName) {
        sTileDenylist.remove(componentName);
    }

    public void updateCategories() {
        updateCategories(false);
    }
}
