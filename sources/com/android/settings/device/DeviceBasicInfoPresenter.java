package com.android.settings.device;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.special.ExternalRamController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import miui.telephony.phonenumber.CountryCode;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class DeviceBasicInfoPresenter {
    private DeviceInfoAdapter mAdapter;
    private DeviceCardInfo[] mBasicInfoCards;
    private DeviceCardInfo[] mCards;
    private Context mContext;
    private DeviceCardInfo[] mDisplayCards;
    private Boolean mIsCardsInitCompleted;
    public static final Map<Integer, Integer> INDEX_MAP = new HashMap<Integer, Integer>() { // from class: com.android.settings.device.DeviceBasicInfoPresenter.1
        {
            put(0, 2);
            put(1, 1);
            put(2, 6);
            put(3, 4);
            put(4, 5);
            put(5, 3);
        }
    };
    public static final Map<Integer, Integer> ICON_MAP = new HashMap<Integer, Integer>() { // from class: com.android.settings.device.DeviceBasicInfoPresenter.2
        {
            put(0, Integer.valueOf(R.drawable.device_description_cpu));
            put(1, Integer.valueOf(R.drawable.device_description_battery));
            put(2, Integer.valueOf(R.drawable.device_description_camera));
            put(3, Integer.valueOf(R.drawable.device_description_screen));
            put(4, Integer.valueOf(R.drawable.device_description_resolution));
            put(5, Integer.valueOf(R.drawable.device_description_ram));
            put(6, Integer.valueOf(R.drawable.device_description_model));
        }
    };

    /* loaded from: classes.dex */
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        public SpaceItemDecoration() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            rect.bottom = DeviceBasicInfoPresenter.this.mContext.getResources().getDimensionPixelSize(R.dimen.card_item_bottom);
        }
    }

    public DeviceBasicInfoPresenter(Context context) {
        this.mIsCardsInitCompleted = Boolean.FALSE;
        this.mContext = context;
        this.mCards = new DeviceCardInfo[7];
        this.mBasicInfoCards = new DeviceCardInfo[2];
    }

    public DeviceBasicInfoPresenter(Context context, DeviceCardInfo[] deviceCardInfoArr) {
        this.mIsCardsInitCompleted = Boolean.FALSE;
        this.mContext = context;
        this.mCards = deviceCardInfoArr;
        this.mIsCardsInitCompleted = Boolean.TRUE;
    }

    private void buildGridView(View view) {
        final int i = MiuiUtils.isLandScape(this.mContext) ? 3 : 2;
        View findViewById = view.getRootView().findViewById(R.id.disclaimer);
        if (findViewById != null) {
            findViewById.setVisibility(0);
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.description_grid);
        this.mAdapter = new DeviceInfoAdapter(this.mContext);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.mContext, i);
        this.mDisplayCards = this.mIsCardsInitCompleted.booleanValue() ? this.mCards : this.mBasicInfoCards;
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: com.android.settings.device.DeviceBasicInfoPresenter.3
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i2) {
                if (DeviceBasicInfoPresenter.this.mDisplayCards[i2].getType() == 1) {
                    return i;
                }
                return 1;
            }
        });
        this.mAdapter.setDataList(this.mDisplayCards);
        recyclerView.setAdapter(this.mAdapter);
        if (recyclerView.getItemDecorationCount() <= 0 || recyclerView.getItemDecorationAt(0) == null) {
            recyclerView.addItemDecoration(new SpaceItemDecoration());
        }
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private DeviceCardInfo createDeviceModelCard(View.OnClickListener onClickListener) {
        DeviceCardInfo deviceCardInfo = new DeviceCardInfo();
        deviceCardInfo.setTitle(this.mContext.getResources().getString(R.string.model_number));
        deviceCardInfo.setValue(MiuiAboutPhoneUtils.getWrapModelNumber());
        deviceCardInfo.setIconResId(ICON_MAP.get(6).intValue());
        deviceCardInfo.setListener(onClickListener);
        return deviceCardInfo;
    }

    private DeviceCardInfo createMemoryCard(View.OnClickListener onClickListener) {
        DeviceCardInfo deviceCardInfo = new DeviceCardInfo();
        deviceCardInfo.setTitle(this.mContext.getResources().getString(R.string.device_memory));
        setMemoryValueAndIndex(deviceCardInfo);
        deviceCardInfo.setIconResId(ICON_MAP.get(5).intValue());
        deviceCardInfo.setListener(onClickListener);
        return deviceCardInfo;
    }

    private String getItemTitle(JSONObject jSONObject) {
        Resources resources = this.mContext.getResources();
        int identifier = resources.getIdentifier("device_description_" + ParseMiShopDataUtils.getItemTitle(jSONObject), "string", this.mContext.getPackageName());
        return identifier != 0 ? resources.getString(identifier) : "";
    }

    private void initBasicInfo(View.OnClickListener onClickListener) {
        if (this.mBasicInfoCards == null) {
            return;
        }
        this.mBasicInfoCards[0] = createDeviceModelCard(onClickListener);
        this.mBasicInfoCards[1] = createMemoryCard(onClickListener);
    }

    private void initData(String str, View.OnClickListener onClickListener) {
        if (this.mCards == null) {
            return;
        }
        JSONArray basicItemsArray = ParseMiShopDataUtils.getBasicItemsArray(str);
        this.mCards[0] = createDeviceModelCard(onClickListener);
        if (basicItemsArray == null || basicItemsArray.length() <= 0) {
            return;
        }
        for (int i = 0; i < basicItemsArray.length(); i++) {
            JSONObject jSONObject = JSONUtils.getJSONObject(basicItemsArray, i);
            int itemIndex = ParseMiShopDataUtils.getItemIndex(jSONObject);
            if (itemIndex >= 0 && itemIndex < 7) {
                String itemTitle = getItemTitle(jSONObject);
                DeviceCardInfo deviceCardInfo = new DeviceCardInfo();
                if (!TextUtils.isEmpty(itemTitle)) {
                    deviceCardInfo.setTitle(itemTitle);
                }
                String itemSummary = ParseMiShopDataUtils.getItemSummary(jSONObject);
                if (!TextUtils.isEmpty(itemSummary)) {
                    deviceCardInfo.setValue(itemSummary);
                }
                deviceCardInfo.setIconResId(ICON_MAP.get(Integer.valueOf(itemIndex)).intValue());
                if (itemIndex == 0) {
                    ParseMiShopDataUtils.setCpuInfo(itemSummary);
                    deviceCardInfo.setKey("cpu_item");
                } else if (itemIndex == 2) {
                    deviceCardInfo.setType(1);
                    deviceCardInfo.setTitle(this.mContext.getResources().getString(R.string.device_camera));
                    if (!TextUtils.isEmpty(itemSummary)) {
                        deviceCardInfo.setValue(String.format(this.mContext.getResources().getString(R.string.camera_rear), itemSummary.replace("\\n", "")));
                    }
                } else if (itemIndex == 5) {
                    setMemoryValueAndIndex(deviceCardInfo);
                }
                deviceCardInfo.setListener(onClickListener);
                this.mCards[INDEX_MAP.get(Integer.valueOf(itemIndex)).intValue()] = deviceCardInfo;
            }
        }
        this.mIsCardsInitCompleted = Boolean.TRUE;
    }

    public void addBasicInfoCard(DeviceCardInfo deviceCardInfo) {
        ArrayList arrayList = new ArrayList(Arrays.asList(this.mBasicInfoCards));
        arrayList.add(deviceCardInfo);
        this.mBasicInfoCards = (DeviceCardInfo[]) arrayList.toArray(new DeviceCardInfo[arrayList.size()]);
        if (!isCardsInitComplete()) {
            DeviceCardInfo[] deviceCardInfoArr = this.mBasicInfoCards;
            this.mDisplayCards = deviceCardInfoArr;
            this.mAdapter.setDataList(deviceCardInfoArr);
        }
        this.mAdapter.notifyDataSetChanged();
    }

    public DeviceCardInfo getCardByIndex(int i) {
        return this.mCards[INDEX_MAP.get(Integer.valueOf(i)).intValue()];
    }

    public DeviceCardInfo[] getCards() {
        return this.mCards;
    }

    public boolean isCardsInitComplete() {
        return this.mIsCardsInitCompleted.booleanValue();
    }

    public void setMemoryValueAndIndex(DeviceCardInfo deviceCardInfo) {
        String totaolRam = MiuiAboutPhoneUtils.getInstance(this.mContext).getTotaolRam();
        if (!ExternalRamController.isChecked(this.mContext)) {
            deviceCardInfo.setValue(totaolRam);
            return;
        }
        deviceCardInfo.setIndex(5);
        deviceCardInfo.setValue(totaolRam.substring(0, 4) + CountryCode.GSM_GENERAL_IDD_CODE + ExternalRamController.getBdSizeInfo());
    }

    public void showBasicInfoGridView(View view) {
        if (this.mContext == null || view == null) {
            return;
        }
        this.mIsCardsInitCompleted = Boolean.TRUE;
        view.setVisibility(0);
        buildGridView(view);
    }

    public void showBasicInfoGridView(View view, String str, boolean z, View.OnClickListener onClickListener) {
        if (this.mContext == null || view == null) {
            return;
        }
        if (ParseMiShopDataUtils.showBasicItems(str)) {
            initData(str, onClickListener);
        }
        if (!this.mIsCardsInitCompleted.booleanValue() && !z) {
            Log.w("DeviceBasicInfoPresenter", "card init not complete");
            view.setVisibility(8);
            return;
        }
        if (!this.mIsCardsInitCompleted.booleanValue()) {
            initBasicInfo(onClickListener);
        }
        view.setVisibility(0);
        buildGridView(view);
    }

    public void updateCameraInfo(String str) {
        JSONObject allParamData;
        String str2;
        if (this.mContext != null && this.mIsCardsInitCompleted.booleanValue() && !TextUtils.isEmpty(str) && ParseMiShopDataUtils.getDataSuccess(str) && (allParamData = ParseMiShopDataUtils.getAllParamData(str)) != null && ParseMiShopDataUtils.showBasicItems(allParamData.toString())) {
            String frontCameraPixel = ParseMiShopDataUtils.getFrontCameraPixel(str);
            if (ParseMiShopDataUtils.isCameraPixelEmpty(frontCameraPixel)) {
                return;
            }
            String rearCameraPixel = ParseMiShopDataUtils.getRearCameraPixel(str);
            Resources resources = this.mContext.getResources();
            if (ParseMiShopDataUtils.isCameraPixelEmpty(rearCameraPixel)) {
                str2 = String.format(resources.getString(R.string.camera_front), frontCameraPixel);
            } else {
                str2 = String.format(resources.getString(R.string.camera_front), frontCameraPixel) + "\n" + String.format(resources.getString(R.string.camera_rear), rearCameraPixel);
            }
            DeviceCardInfo deviceCardInfo = this.mCards[INDEX_MAP.get(2).intValue()];
            if (deviceCardInfo != null) {
                deviceCardInfo.setValue(str2);
                this.mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void updateCardByIndex(int i, DeviceCardInfo deviceCardInfo) {
        this.mCards[INDEX_MAP.get(Integer.valueOf(i)).intValue()] = deviceCardInfo;
        this.mAdapter.notifyDataSetChanged();
    }
}
