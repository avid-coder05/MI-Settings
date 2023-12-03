package androidx.slice.compat;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Preconditions;
import androidx.slice.Slice;
import androidx.slice.SliceItemHolder;
import androidx.slice.SliceProvider;
import androidx.slice.SliceSpec;
import androidx.versionedparcelable.ParcelUtils;
import androidx.versionedparcelable.VersionedParcelable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.payment.PaymentManager;

/* loaded from: classes.dex */
public class SliceProviderCompat {
    String mCallback;
    private final Context mContext;
    private CompatPermissionManager mPermissionManager;
    private CompatPinnedList mPinnedList;
    private final SliceProvider mProvider;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mAnr = new Runnable() { // from class: androidx.slice.compat.SliceProviderCompat.1
        @Override // java.lang.Runnable
        public void run() {
            Process.sendSignal(Process.myPid(), 3);
            Log.wtf("SliceProviderCompat", "Timed out while handling slice callback " + SliceProviderCompat.this.mCallback);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ProviderHolder implements AutoCloseable {
        final ContentProviderClient mProvider;

        ProviderHolder(ContentProviderClient provider) {
            this.mProvider = provider;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            ContentProviderClient contentProviderClient = this.mProvider;
            if (contentProviderClient == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 24) {
                contentProviderClient.close();
            } else {
                contentProviderClient.release();
            }
        }
    }

    public SliceProviderCompat(SliceProvider provider, CompatPermissionManager permissionManager, Context context) {
        this.mProvider = provider;
        this.mContext = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("slice_data_all_slice_files", 0);
        Set<String> stringSet = sharedPreferences.getStringSet("slice_data_all_slice_files", Collections.emptySet());
        if (!stringSet.contains("slice_data_androidx.slice.compat.SliceProviderCompat")) {
            ArraySet arraySet = new ArraySet(stringSet);
            arraySet.add("slice_data_androidx.slice.compat.SliceProviderCompat");
            sharedPreferences.edit().putStringSet("slice_data_all_slice_files", arraySet).commit();
        }
        this.mPinnedList = new CompatPinnedList(context, "slice_data_androidx.slice.compat.SliceProviderCompat");
        this.mPermissionManager = permissionManager;
    }

    private static ProviderHolder acquireClient(ContentResolver resolver, Uri uri) {
        ContentProviderClient acquireUnstableContentProviderClient = resolver.acquireUnstableContentProviderClient(uri);
        if (acquireUnstableContentProviderClient != null) {
            return new ProviderHolder(acquireUnstableContentProviderClient);
        }
        throw new IllegalArgumentException("No provider found for " + uri);
    }

    public static void addSpecs(Bundle extras, Set<SliceSpec> supportedSpecs) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        for (SliceSpec sliceSpec : supportedSpecs) {
            arrayList.add(sliceSpec.getType());
            arrayList2.add(Integer.valueOf(sliceSpec.getRevision()));
        }
        extras.putStringArrayList("specs", arrayList);
        extras.putIntegerArrayList("revs", arrayList2);
    }

    public static Slice bindSlice(Context context, Intent intent, Set<SliceSpec> supportedSpecs) {
        ActivityInfo activityInfo;
        Bundle bundle;
        Preconditions.checkNotNull(intent, PaymentManager.KEY_INTENT);
        Preconditions.checkArgument((intent.getComponent() == null && intent.getPackage() == null && intent.getData() == null) ? false : true, String.format("Slice intent must be explicit %s", intent));
        ContentResolver contentResolver = context.getContentResolver();
        Uri data = intent.getData();
        if (data == null || !"vnd.android.slice".equals(contentResolver.getType(data))) {
            Intent intent2 = new Intent(intent);
            if (!intent2.hasCategory("android.app.slice.category.SLICE")) {
                intent2.addCategory("android.app.slice.category.SLICE");
            }
            List<ResolveInfo> queryIntentContentProviders = context.getPackageManager().queryIntentContentProviders(intent2, 0);
            if (queryIntentContentProviders == null || queryIntentContentProviders.isEmpty()) {
                ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 128);
                if (resolveActivity == null || (activityInfo = resolveActivity.activityInfo) == null || (bundle = activityInfo.metaData) == null || !bundle.containsKey("android.metadata.SLICE_URI")) {
                    return null;
                }
                return bindSlice(context, Uri.parse(resolveActivity.activityInfo.metaData.getString("android.metadata.SLICE_URI")), supportedSpecs);
            }
            Uri build = new Uri.Builder().scheme("content").authority(queryIntentContentProviders.get(0).providerInfo.authority).build();
            ProviderHolder acquireClient = acquireClient(contentResolver, build);
            if (acquireClient.mProvider == null) {
                throw new IllegalArgumentException("Unknown URI " + build);
            }
            try {
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("slice_intent", intent);
                addSpecs(bundle2, supportedSpecs);
                return parseSlice(context, acquireClient.mProvider.call("map_slice", "supports_versioned_parcelable", bundle2));
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to bind slice", e);
                return null;
            } finally {
                acquireClient.close();
            }
        }
        return bindSlice(context, data, supportedSpecs);
    }

    public static Slice bindSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable("slice_uri", uri);
            addSpecs(bundle, supportedSpecs);
            return parseSlice(context, acquireClient.mProvider.call("bind_slice", "supports_versioned_parcelable", bundle));
        } catch (RemoteException e) {
            Log.e("SliceProviderCompat", "Unable to bind slice", e);
            return null;
        } finally {
            acquireClient.close();
        }
    }

    private Context getContext() {
        return this.mContext;
    }

    public static List<Uri> getPinnedSlices(Context context) {
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = context.getSharedPreferences("slice_data_all_slice_files", 0).getStringSet("slice_data_all_slice_files", Collections.emptySet()).iterator();
        while (it.hasNext()) {
            arrayList.addAll(new CompatPinnedList(context, it.next()).getPinnedSlices());
        }
        return arrayList;
    }

    public static Set<SliceSpec> getPinnedSpecs(Context context, Uri uri) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                Bundle call = acquireClient.mProvider.call("get_specs", "supports_versioned_parcelable", bundle);
                if (call != null) {
                    return getSpecs(call);
                }
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to get pinned specs", e);
            }
            acquireClient.close();
            return null;
        } finally {
            acquireClient.close();
        }
    }

    public static Collection<Uri> getSliceDescendants(Context context, Uri uri) {
        ProviderHolder acquireClient;
        Bundle call;
        try {
            acquireClient = acquireClient(context.getContentResolver(), uri);
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                call = acquireClient.mProvider.call("get_descendants", "supports_versioned_parcelable", bundle);
            } finally {
            }
        } catch (RemoteException e) {
            Log.e("SliceProviderCompat", "Unable to get slice descendants", e);
        }
        if (call == null) {
            acquireClient.close();
            return Collections.emptyList();
        }
        ArrayList parcelableArrayList = call.getParcelableArrayList("slice_descendants");
        acquireClient.close();
        return parcelableArrayList;
    }

    public static Set<SliceSpec> getSpecs(Bundle extras) {
        ArraySet arraySet = new ArraySet();
        ArrayList<String> stringArrayList = extras.getStringArrayList("specs");
        ArrayList<Integer> integerArrayList = extras.getIntegerArrayList("revs");
        if (stringArrayList != null && integerArrayList != null) {
            for (int i = 0; i < stringArrayList.size(); i++) {
                arraySet.add(new SliceSpec(stringArrayList.get(i), integerArrayList.get(i).intValue()));
            }
        }
        return arraySet;
    }

    public static void grantSlicePermission(Context context, String packageName, String toPackage, Uri uri) {
        try {
            ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                bundle.putString("provider_pkg", packageName);
                bundle.putString("pkg", toPackage);
                acquireClient.mProvider.call("grant_perms", "supports_versioned_parcelable", bundle);
                acquireClient.close();
            } finally {
            }
        } catch (RemoteException e) {
            Log.e("SliceProviderCompat", "Unable to get slice descendants", e);
        }
    }

    private Slice handleBindSlice(final Uri sliceUri, final Set<SliceSpec> specs, final String callingPkg) {
        if (callingPkg == null) {
            callingPkg = getContext().getPackageManager().getNameForUid(Binder.getCallingUid());
        }
        return this.mPermissionManager.checkSlicePermission(sliceUri, Binder.getCallingPid(), Binder.getCallingUid()) != 0 ? this.mProvider.createPermissionSlice(sliceUri, callingPkg) : onBindSliceStrict(sliceUri, specs);
    }

    private Collection<Uri> handleGetDescendants(Uri uri) {
        this.mCallback = "onGetSliceDescendants";
        return this.mProvider.onGetSliceDescendants(uri);
    }

    private void handleSlicePinned(final Uri sliceUri) {
        this.mCallback = "onSlicePinned";
        this.mHandler.postDelayed(this.mAnr, 2000L);
        try {
            this.mProvider.onSlicePinned(sliceUri);
            this.mProvider.handleSlicePinned(sliceUri);
        } finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }

    private void handleSliceUnpinned(final Uri sliceUri) {
        this.mCallback = "onSliceUnpinned";
        this.mHandler.postDelayed(this.mAnr, 2000L);
        try {
            this.mProvider.onSliceUnpinned(sliceUri);
            this.mProvider.handleSliceUnpinned(sliceUri);
        } finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }

    private Slice onBindSliceStrict(Uri sliceUri, Set<SliceSpec> specs) {
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        this.mCallback = "onBindSlice";
        this.mHandler.postDelayed(this.mAnr, 2000L);
        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDeath().build());
            SliceProvider.setSpecs(specs);
            try {
                try {
                    Slice onBindSlice = this.mProvider.onBindSlice(sliceUri);
                    StrictMode.setThreadPolicy(threadPolicy);
                    return onBindSlice;
                } catch (Exception e) {
                    Log.wtf("SliceProviderCompat", "Slice with URI " + sliceUri.toString() + " is invalid.", e);
                    StrictMode.setThreadPolicy(threadPolicy);
                    return null;
                }
            } finally {
                SliceProvider.setSpecs(null);
                this.mHandler.removeCallbacks(this.mAnr);
            }
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(threadPolicy);
            throw th;
        }
    }

    @SuppressLint({"WrongConstant"})
    private static Slice parseSlice(final Context context, Bundle res) {
        if (res == null) {
            return null;
        }
        synchronized (SliceItemHolder.sSerializeLock) {
            try {
                SliceItemHolder.sHandler = new SliceItemHolder.HolderHandler() { // from class: androidx.slice.compat.SliceProviderCompat.2
                    @Override // androidx.slice.SliceItemHolder.HolderHandler
                    public void handle(SliceItemHolder holder, String format) {
                        VersionedParcelable versionedParcelable = holder.mVersionedParcelable;
                        if (versionedParcelable instanceof IconCompat) {
                            IconCompat iconCompat = (IconCompat) versionedParcelable;
                            iconCompat.checkResource(context);
                            if (iconCompat.getType() == 2 && iconCompat.getResId() == 0) {
                                holder.mVersionedParcelable = null;
                            }
                        }
                    }
                };
                res.setClassLoader(SliceProviderCompat.class.getClassLoader());
                Parcelable parcelable = res.getParcelable("slice");
                if (parcelable == null) {
                    return null;
                }
                if (parcelable instanceof Bundle) {
                    return new Slice((Bundle) parcelable);
                }
                return (Slice) ParcelUtils.fromParcelable(parcelable);
            } finally {
                SliceItemHolder.sHandler = null;
            }
        }
    }

    public static void pinSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        try {
            if (acquireClient.mProvider == null) {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                bundle.putString("pkg", context.getPackageName());
                addSpecs(bundle, supportedSpecs);
                acquireClient.mProvider.call("pin_slice", "supports_versioned_parcelable", bundle);
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to pin slice", e);
            }
        } finally {
            acquireClient.close();
        }
    }

    public static void unpinSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        try {
            if (acquireClient.mProvider == null) {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable("slice_uri", uri);
                bundle.putString("pkg", context.getPackageName());
                addSpecs(bundle, supportedSpecs);
                acquireClient.mProvider.call("unpin_slice", "supports_versioned_parcelable", bundle);
            } catch (RemoteException e) {
                Log.e("SliceProviderCompat", "Unable to unpin slice", e);
            }
        } finally {
            acquireClient.close();
        }
    }

    public Bundle call(String method, String arg, Bundle extras) {
        if (method.equals("bind_slice")) {
            Uri uri = (Uri) extras.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri.getAuthority());
            Slice handleBindSlice = handleBindSlice(uri, getSpecs(extras), getCallingPackage());
            Bundle bundle = new Bundle();
            if ("supports_versioned_parcelable".equals(arg)) {
                synchronized (SliceItemHolder.sSerializeLock) {
                    bundle.putParcelable("slice", handleBindSlice != null ? ParcelUtils.toParcelable(handleBindSlice) : null);
                }
            } else {
                bundle.putParcelable("slice", handleBindSlice != null ? handleBindSlice.toBundle() : null);
            }
            return bundle;
        } else if (method.equals("map_slice")) {
            Uri onMapIntentToUri = this.mProvider.onMapIntentToUri((Intent) extras.getParcelable("slice_intent"));
            this.mProvider.validateIncomingAuthority(onMapIntentToUri.getAuthority());
            Bundle bundle2 = new Bundle();
            Slice handleBindSlice2 = handleBindSlice(onMapIntentToUri, getSpecs(extras), getCallingPackage());
            if ("supports_versioned_parcelable".equals(arg)) {
                synchronized (SliceItemHolder.sSerializeLock) {
                    bundle2.putParcelable("slice", handleBindSlice2 != null ? ParcelUtils.toParcelable(handleBindSlice2) : null);
                }
            } else {
                bundle2.putParcelable("slice", handleBindSlice2 != null ? handleBindSlice2.toBundle() : null);
            }
            return bundle2;
        } else if (method.equals("map_only")) {
            Uri onMapIntentToUri2 = this.mProvider.onMapIntentToUri((Intent) extras.getParcelable("slice_intent"));
            this.mProvider.validateIncomingAuthority(onMapIntentToUri2.getAuthority());
            Bundle bundle3 = new Bundle();
            bundle3.putParcelable("slice", onMapIntentToUri2);
            return bundle3;
        } else if (method.equals("pin_slice")) {
            Uri uri2 = (Uri) extras.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri2.getAuthority());
            if (this.mPinnedList.addPin(uri2, extras.getString("pkg"), getSpecs(extras))) {
                handleSlicePinned(uri2);
            }
            return null;
        } else if (method.equals("unpin_slice")) {
            Uri uri3 = (Uri) extras.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri3.getAuthority());
            if (this.mPinnedList.removePin(uri3, extras.getString("pkg"))) {
                handleSliceUnpinned(uri3);
            }
            return null;
        } else if (method.equals("get_specs")) {
            Uri uri4 = (Uri) extras.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri4.getAuthority());
            Bundle bundle4 = new Bundle();
            ArraySet<SliceSpec> specs = this.mPinnedList.getSpecs(uri4);
            if (specs.size() != 0) {
                addSpecs(bundle4, specs);
                return bundle4;
            }
            throw new IllegalStateException(uri4 + " is not pinned");
        } else if (method.equals("get_descendants")) {
            Uri uri5 = (Uri) extras.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri5.getAuthority());
            Bundle bundle5 = new Bundle();
            bundle5.putParcelableArrayList("slice_descendants", new ArrayList<>(handleGetDescendants(uri5)));
            return bundle5;
        } else if (method.equals("check_perms")) {
            Uri uri6 = (Uri) extras.getParcelable("slice_uri");
            this.mProvider.validateIncomingAuthority(uri6.getAuthority());
            int i = extras.getInt("pid");
            int i2 = extras.getInt("uid");
            Bundle bundle6 = new Bundle();
            bundle6.putInt("result", this.mPermissionManager.checkSlicePermission(uri6, i, i2));
            return bundle6;
        } else {
            if (method.equals("grant_perms")) {
                Uri uri7 = (Uri) extras.getParcelable("slice_uri");
                this.mProvider.validateIncomingAuthority(uri7.getAuthority());
                String string = extras.getString("pkg");
                if (Binder.getCallingUid() != Process.myUid()) {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
                this.mPermissionManager.grantSlicePermission(uri7, string);
            } else if (method.equals("revoke_perms")) {
                Uri uri8 = (Uri) extras.getParcelable("slice_uri");
                this.mProvider.validateIncomingAuthority(uri8.getAuthority());
                String string2 = extras.getString("pkg");
                if (Binder.getCallingUid() != Process.myUid()) {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
                this.mPermissionManager.revokeSlicePermission(uri8, string2);
            }
            return null;
        }
    }

    public String getCallingPackage() {
        return this.mProvider.getCallingPackage();
    }
}
