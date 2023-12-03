package com.android.settings.network.telephony;

import android.content.Context;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import com.android.internal.telephony.OperatorInfo;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.List;
import java.util.Objects;

/* loaded from: classes2.dex */
public class NetworkOperatorPreference extends Preference {
    private CellIdentity mCellId;
    private CellInfo mCellInfo;
    private List<String> mForbiddenPlmns;
    private int mLevel;
    private boolean mShow4GForLTE;
    private boolean mUseNewApi;

    public NetworkOperatorPreference(Context context, CellIdentity cellIdentity, List<String> list, boolean z) {
        this(context, list, z);
        updateCell(null, cellIdentity);
    }

    public NetworkOperatorPreference(Context context, CellInfo cellInfo, List<String> list, boolean z) {
        this(context, list, z);
        updateCell(cellInfo);
    }

    private NetworkOperatorPreference(Context context, List<String> list, boolean z) {
        super(context);
        this.mLevel = -1;
        this.mForbiddenPlmns = list;
        this.mShow4GForLTE = z;
        this.mUseNewApi = context.getResources().getBoolean(17891545);
    }

    private int getAccessNetworkTypeFromCellInfo(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoGsm) {
            return 1;
        }
        if (cellInfo instanceof CellInfoCdma) {
            return 4;
        }
        if ((cellInfo instanceof CellInfoWcdma) || (cellInfo instanceof CellInfoTdscdma)) {
            return 2;
        }
        if (cellInfo instanceof CellInfoLte) {
            return 3;
        }
        return cellInfo instanceof CellInfoNr ? 6 : 0;
    }

    private CellSignalStrength getCellSignalStrength(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoCdma) {
            return ((CellInfoCdma) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoWcdma) {
            return ((CellInfoWcdma) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoTdscdma) {
            return ((CellInfoTdscdma) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoLte) {
            return ((CellInfoLte) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoNr) {
            return ((CellInfoNr) cellInfo).getCellSignalStrength();
        }
        return null;
    }

    private int getIconIdForCell(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoGsm) {
            return R.drawable.signal_strength_g;
        }
        if (cellInfo instanceof CellInfoCdma) {
            return R.drawable.signal_strength_1x;
        }
        if ((cellInfo instanceof CellInfoWcdma) || (cellInfo instanceof CellInfoTdscdma)) {
            return R.drawable.signal_strength_3g;
        }
        if (cellInfo instanceof CellInfoLte) {
            return this.mShow4GForLTE ? R.drawable.ic_signal_strength_4g : R.drawable.signal_strength_lte;
        } else if (cellInfo instanceof CellInfoNr) {
            return R.drawable.signal_strength_5g;
        } else {
            return 0;
        }
    }

    private void updateCell(CellInfo cellInfo, CellIdentity cellIdentity) {
        this.mCellInfo = cellInfo;
        this.mCellId = cellIdentity;
        refresh();
    }

    private void updateIcon(int i) {
        if (!this.mUseNewApi || i < 0 || i >= 5) {
            return;
        }
        setIcon(MobileNetworkUtils.getSignalStrengthIcon(getContext(), i, 5, getIconIdForCell(this.mCellInfo), false));
    }

    public OperatorInfo getOperatorInfo() {
        return new OperatorInfo(Objects.toString(this.mCellId.getOperatorAlphaLong(), ""), Objects.toString(this.mCellId.getOperatorAlphaShort(), ""), getOperatorNumeric(), getAccessNetworkTypeFromCellInfo(this.mCellInfo));
    }

    public String getOperatorName() {
        return CellInfoUtil.getNetworkTitle(this.mCellId, getOperatorNumeric());
    }

    public String getOperatorNumeric() {
        CellIdentityNr cellIdentityNr;
        String mccString;
        CellIdentity cellIdentity = this.mCellId;
        if (cellIdentity == null) {
            return null;
        }
        if (cellIdentity instanceof CellIdentityGsm) {
            return ((CellIdentityGsm) cellIdentity).getMobileNetworkOperator();
        }
        if (cellIdentity instanceof CellIdentityWcdma) {
            return ((CellIdentityWcdma) cellIdentity).getMobileNetworkOperator();
        }
        if (cellIdentity instanceof CellIdentityTdscdma) {
            return ((CellIdentityTdscdma) cellIdentity).getMobileNetworkOperator();
        }
        if (cellIdentity instanceof CellIdentityLte) {
            return ((CellIdentityLte) cellIdentity).getMobileNetworkOperator();
        }
        if (!(cellIdentity instanceof CellIdentityNr) || (mccString = (cellIdentityNr = (CellIdentityNr) cellIdentity).getMccString()) == null) {
            return null;
        }
        return mccString.concat(cellIdentityNr.getMncString());
    }

    public boolean isForbiddenNetwork() {
        List<String> list = this.mForbiddenPlmns;
        return list != null && list.contains(getOperatorNumeric());
    }

    public boolean isSameCell(CellInfo cellInfo) {
        if (cellInfo == null) {
            return false;
        }
        return this.mCellId.equals(CellInfoUtil.getCellIdentity(cellInfo));
    }

    public void refresh() {
        String operatorName = getOperatorName();
        if (isForbiddenNetwork()) {
            operatorName = operatorName + " " + getContext().getResources().getString(R.string.forbidden_network);
        }
        setTitle(Objects.toString(operatorName, ""));
        CellInfo cellInfo = this.mCellInfo;
        if (cellInfo == null) {
            return;
        }
        CellSignalStrength cellSignalStrength = getCellSignalStrength(cellInfo);
        int level = cellSignalStrength != null ? cellSignalStrength.getLevel() : -1;
        this.mLevel = level;
        updateIcon(level);
    }

    @Override // androidx.preference.Preference
    public void setIcon(int i) {
        updateIcon(i);
    }

    public void updateCell(CellInfo cellInfo) {
        updateCell(cellInfo, CellInfoUtil.getCellIdentity(cellInfo));
    }
}
