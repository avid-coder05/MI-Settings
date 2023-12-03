package com.android.settingslib.fuelgauge;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import java.time.Duration;
import java.time.Instant;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: Estimate.kt */
/* loaded from: classes2.dex */
public final class Estimate {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final long averageDischargeTime;
    private final long estimateMillis;
    private final boolean isBasedOnUsage;

    /* compiled from: Estimate.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @Nullable
        public final Estimate getCachedEstimateIfAvailable(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            ContentResolver contentResolver = context.getContentResolver();
            if (Duration.between(getLastCacheUpdateTime(context), Instant.now()).compareTo(Duration.ofMinutes(1L)) > 0) {
                return null;
            }
            return new Estimate(Settings.Global.getLong(contentResolver, "time_remaining_estimate_millis", -1L), Settings.Global.getInt(contentResolver, "time_remaining_estimate_based_on_usage", 0) == 1, Settings.Global.getLong(contentResolver, "average_time_to_discharge", -1L));
        }

        @NotNull
        public final Instant getLastCacheUpdateTime(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            Instant ofEpochMilli = Instant.ofEpochMilli(Settings.Global.getLong(context.getContentResolver(), "battery_estimates_last_update_time", -1L));
            Intrinsics.checkNotNullExpressionValue(ofEpochMilli, "ofEpochMilli(\n                    Settings.Global.getLong(\n                            context.contentResolver,\n                            Settings.Global.BATTERY_ESTIMATES_LAST_UPDATE_TIME,\n                            -1))");
            return ofEpochMilli;
        }

        public final void storeCachedEstimate(@NotNull Context context, @NotNull Estimate estimate) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(estimate, "estimate");
            ContentResolver contentResolver = context.getContentResolver();
            Settings.Global.putLong(contentResolver, "time_remaining_estimate_millis", estimate.getEstimateMillis());
            Settings.Global.putInt(contentResolver, "time_remaining_estimate_based_on_usage", estimate.isBasedOnUsage() ? 1 : 0);
            Settings.Global.putLong(contentResolver, "average_time_to_discharge", estimate.getAverageDischargeTime());
            Settings.Global.putLong(contentResolver, "battery_estimates_last_update_time", System.currentTimeMillis());
        }
    }

    public Estimate(long j, boolean z, long j2) {
        this.estimateMillis = j;
        this.isBasedOnUsage = z;
        this.averageDischargeTime = j2;
    }

    @Nullable
    public static final Estimate getCachedEstimateIfAvailable(@NotNull Context context) {
        return Companion.getCachedEstimateIfAvailable(context);
    }

    @NotNull
    public static final Instant getLastCacheUpdateTime(@NotNull Context context) {
        return Companion.getLastCacheUpdateTime(context);
    }

    public static final void storeCachedEstimate(@NotNull Context context, @NotNull Estimate estimate) {
        Companion.storeCachedEstimate(context, estimate);
    }

    public final long getAverageDischargeTime() {
        return this.averageDischargeTime;
    }

    public final long getEstimateMillis() {
        return this.estimateMillis;
    }

    public final boolean isBasedOnUsage() {
        return this.isBasedOnUsage;
    }
}
