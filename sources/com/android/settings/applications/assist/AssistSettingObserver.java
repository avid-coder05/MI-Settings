package com.android.settings.applications.assist;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public abstract class AssistSettingObserver extends ContentObserver {
    private final Uri ASSIST_URI;

    public AssistSettingObserver() {
        super(null);
        this.ASSIST_URI = Settings.Secure.getUriFor("assistant");
    }

    protected abstract List<Uri> getSettingUris();

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        super.onChange(z, uri);
        List<Uri> settingUris = getSettingUris();
        if (this.ASSIST_URI.equals(uri) || (settingUris != null && settingUris.contains(uri))) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.assist.AssistSettingObserver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AssistSettingObserver.this.lambda$onChange$0();
                }
            });
        }
    }

    /* renamed from: onSettingChange  reason: merged with bridge method [inline-methods] */
    public abstract void lambda$onChange$0();

    public void register(ContentResolver contentResolver, boolean z) {
        if (!z) {
            contentResolver.unregisterContentObserver(this);
            return;
        }
        contentResolver.registerContentObserver(this.ASSIST_URI, false, this);
        List<Uri> settingUris = getSettingUris();
        if (settingUris != null) {
            Iterator<Uri> it = settingUris.iterator();
            while (it.hasNext()) {
                contentResolver.registerContentObserver(it.next(), false, this);
            }
        }
    }
}
