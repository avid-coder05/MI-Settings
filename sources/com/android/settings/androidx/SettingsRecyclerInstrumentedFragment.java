package com.android.settings.androidx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.core.InstrumentedFragment;

/* loaded from: classes.dex */
public class SettingsRecyclerInstrumentedFragment extends InstrumentedFragment {
    RecyclerView mList;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.print_service_settings, viewGroup, false);
        this.mList = (RecyclerView) inflate.findViewById(16908298);
        return inflate;
    }
}
