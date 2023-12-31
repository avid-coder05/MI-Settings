package androidx.activity.result;

import android.annotation.SuppressLint;
import androidx.core.app.ActivityOptionsCompat;

/* loaded from: classes.dex */
public abstract class ActivityResultLauncher<I> {
    public void launch(@SuppressLint({"UnknownNullness"}) I i) {
        launch(i, null);
    }

    public abstract void launch(@SuppressLint({"UnknownNullness"}) I i, ActivityOptionsCompat activityOptionsCompat);

    public abstract void unregister();
}
