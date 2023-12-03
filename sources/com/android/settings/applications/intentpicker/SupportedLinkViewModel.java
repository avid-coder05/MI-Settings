package com.android.settings.applications.intentpicker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import java.util.List;

/* loaded from: classes.dex */
public class SupportedLinkViewModel extends AndroidViewModel {
    private List<SupportedLinkWrapper> mSupportedLinkWrapperList;

    public SupportedLinkViewModel(Application application) {
        super(application);
    }

    public List<SupportedLinkWrapper> getSupportedLinkWrapperList() {
        return this.mSupportedLinkWrapperList;
    }

    public void setSupportedLinkWrapperList(List<SupportedLinkWrapper> list) {
        this.mSupportedLinkWrapperList = list;
    }
}
