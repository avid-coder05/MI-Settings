package com.android.settings.nfc;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class MiuiAndroidBeam extends BaseFragment {
    private SlidingButton mAndroidBeamSwitch;
    private NfcAdapter mNfcAdapter;

    private void initView(View view) {
        if (this.mNfcAdapter == null) {
            return;
        }
        SlidingButton slidingButton = (SlidingButton) view.findViewById(R.id.android_beam_switch);
        this.mAndroidBeamSwitch = slidingButton;
        slidingButton.setChecked(this.mNfcAdapter.isNdefPushEnabled());
        this.mAndroidBeamSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.nfc.MiuiAndroidBeam.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                MiuiAndroidBeam.this.mAndroidBeamSwitch.setEnabled(false);
                if (z) {
                    MiuiAndroidBeam.this.mNfcAdapter.enableNdefPush();
                } else {
                    MiuiAndroidBeam.this.mNfcAdapter.disableNdefPush();
                }
                MiuiAndroidBeam.this.mAndroidBeamSwitch.setEnabled(true);
            }
        });
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.android_beam, viewGroup, false);
        initView(inflate);
        return inflate;
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        this.mNfcAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            getActivity().finish();
        }
    }
}
