package com.android.settingslib.core.instrumentation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import androidx.preference.Preference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class MetricsFeatureProvider {
    protected List<LogWriter> mLoggerWriters = new ArrayList();

    public MetricsFeatureProvider() {
        installLogWriters();
    }

    public void action(int i, int i2, int i3, String str, int i4) {
        Iterator<LogWriter> it = this.mLoggerWriters.iterator();
        while (it.hasNext()) {
            it.next().action(i, i2, i3, str, i4);
        }
    }

    public void action(Context context, int i, int i2) {
        Iterator<LogWriter> it = this.mLoggerWriters.iterator();
        while (it.hasNext()) {
            it.next().action(context, i, i2);
        }
    }

    public void action(Context context, int i, String str) {
        Iterator<LogWriter> it = this.mLoggerWriters.iterator();
        while (it.hasNext()) {
            it.next().action(context, i, str);
        }
    }

    public void action(Context context, int i, boolean z) {
        Iterator<LogWriter> it = this.mLoggerWriters.iterator();
        while (it.hasNext()) {
            it.next().action(context, i, z);
        }
    }

    public void action(Context context, int i, Pair<Integer, Object>... pairArr) {
        Iterator<LogWriter> it = this.mLoggerWriters.iterator();
        while (it.hasNext()) {
            it.next().action(context, i, pairArr);
        }
    }

    public int getAttribution(Activity activity) {
        Intent intent;
        if (activity == null || (intent = activity.getIntent()) == null) {
            return 0;
        }
        return intent.getIntExtra(":settings:source_metrics", 0);
    }

    public int getMetricsCategory(Object obj) {
        if (obj == null || !(obj instanceof Instrumentable)) {
            return 0;
        }
        return ((Instrumentable) obj).getMetricsCategory();
    }

    public void hidden(Context context, int i, int i2) {
        Iterator<LogWriter> it = this.mLoggerWriters.iterator();
        while (it.hasNext()) {
            it.next().hidden(context, i, i2);
        }
    }

    protected void installLogWriters() {
        this.mLoggerWriters.add(new EventLogWriter());
    }

    public boolean logClickedPreference(Preference preference, int i) {
        if (preference == null) {
            return false;
        }
        return logSettingsTileClick(preference.getKey(), i) || logStartedIntent(preference.getIntent(), i) || logSettingsTileClick(preference.getFragment(), i);
    }

    public boolean logSettingsTileClick(String str, int i) {
        if (TextUtils.isEmpty(str) || str.contains("StandardWifiEntry:")) {
            return false;
        }
        action(i, 830, 0, str, 0);
        return true;
    }

    public boolean logStartedIntent(Intent intent, int i) {
        if (intent == null) {
            return false;
        }
        ComponentName component = intent.getComponent();
        return logSettingsTileClick(component != null ? component.flattenToString() : intent.getAction(), i);
    }

    public boolean logStartedIntentWithProfile(Intent intent, int i, boolean z) {
        if (intent == null) {
            return false;
        }
        ComponentName component = intent.getComponent();
        String flattenToString = component != null ? component.flattenToString() : intent.getAction();
        StringBuilder sb = new StringBuilder();
        sb.append(flattenToString);
        sb.append(z ? "/work" : "/personal");
        return logSettingsTileClick(sb.toString(), i);
    }

    public void visible(Context context, int i, int i2, int i3) {
        Iterator<LogWriter> it = this.mLoggerWriters.iterator();
        while (it.hasNext()) {
            it.next().visible(context, i, i2, i3);
        }
    }
}
