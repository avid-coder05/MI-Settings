package com.android.settings.wifi.calling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import com.android.settings.core.InstrumentedFragment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class WifiCallingDisclaimerFragment extends InstrumentedFragment implements View.OnClickListener {
    private Button mAgreeButton;
    private Button mDisagreeButton;
    private List<DisclaimerItem> mDisclaimerItemList = new ArrayList();
    private boolean mScrollToBottom;

    /* JADX INFO: Access modifiers changed from: private */
    public void updateButtonState() {
        this.mAgreeButton.setEnabled(this.mScrollToBottom);
    }

    @VisibleForTesting
    void finish(int i) {
        FragmentActivity activity = getActivity();
        activity.setResult(i, null);
        activity.finish();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 105;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view != this.mAgreeButton) {
            if (view == this.mDisagreeButton) {
                finish(0);
                return;
            }
            return;
        }
        Iterator<DisclaimerItem> it = this.mDisclaimerItemList.iterator();
        while (it.hasNext()) {
            it.next().onAgreed();
        }
        finish(-1);
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        List<DisclaimerItem> create = DisclaimerItemFactory.create(getActivity(), arguments != null ? arguments.getInt("EXTRA_SUB_ID") : Integer.MAX_VALUE);
        this.mDisclaimerItemList = create;
        if (create.isEmpty()) {
            finish(-1);
        } else if (bundle != null) {
            this.mScrollToBottom = bundle.getBoolean("state_is_scroll_to_bottom", this.mScrollToBottom);
        }
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.wfc_disclaimer_fragment, viewGroup, false);
        Button button = (Button) inflate.findViewById(R.id.agree_button);
        this.mAgreeButton = button;
        button.setOnClickListener(this);
        Button button2 = (Button) inflate.findViewById(R.id.disagree_button);
        this.mDisagreeButton = button2;
        button2.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.disclaimer_item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new DisclaimerItemListAdapter(this.mDisclaimerItemList));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.android.settings.wifi.calling.WifiCallingDisclaimerFragment.1
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView2, int i, int i2) {
                super.onScrolled(recyclerView2, i, i2);
                if (recyclerView2.canScrollVertically(1)) {
                    return;
                }
                WifiCallingDisclaimerFragment.this.mScrollToBottom = true;
                WifiCallingDisclaimerFragment.this.updateButtonState();
                recyclerView2.removeOnScrollListener(this);
            }
        });
        return inflate;
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateButtonState();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("state_is_scroll_to_bottom", this.mScrollToBottom);
    }
}
