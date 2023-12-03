package com.android.settings.security;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.CardInfo;
import com.android.settings.MiuiCardGridView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.List;
import miuix.animation.Folme;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes2.dex */
public class UnlockModeCardPreference extends Preference implements FolmeAnimationController {
    private MiuiCardGridView mCardGridView;
    private List<CardInfo> mCardList;

    public UnlockModeCardPreference(Context context) {
        super(context);
    }

    public UnlockModeCardPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public List<CardInfo> getData() {
        return this.mCardList;
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        MiuiCardGridView miuiCardGridView = (MiuiCardGridView) view.findViewById(R.id.miui_card_view);
        this.mCardGridView = miuiCardGridView;
        List<CardInfo> list = this.mCardList;
        if (list != null) {
            miuiCardGridView.setData(list);
        }
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundResource(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.unlock_card_view_layout);
        return null;
    }

    public void refresh() {
        MiuiCardGridView miuiCardGridView = this.mCardGridView;
        if (miuiCardGridView != null) {
            miuiCardGridView.notifyDataChanged();
        }
    }

    public void setData(List<CardInfo> list) {
        this.mCardList = list;
    }
}
