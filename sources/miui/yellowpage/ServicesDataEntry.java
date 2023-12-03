package miui.yellowpage;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes4.dex */
public class ServicesDataEntry {
    private String mGroupName;
    private Type mItemType;
    private List<Service> mServices;

    /* loaded from: classes4.dex */
    public enum Type {
        NONE,
        TOP_SERVICE,
        SINGLE_LINE_BANNER,
        CONVENIENT_SERVICE,
        DOUBLE_BANNER,
        USEFUL_NUMBERS,
        BOTTOM_BANNER
    }

    public ServicesDataEntry(Type type) {
        this.mItemType = type;
    }

    public static ServicesDataEntry fromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return fromJson(new JSONObject(str));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ServicesDataEntry fromJson(JSONObject jSONObject) {
        Service fromJson;
        if (jSONObject == null) {
            return null;
        }
        try {
            String optString = jSONObject.optString("name");
            int i = jSONObject.getInt(Tag.TagServicesData.GROUP_STYLE);
            JSONArray jSONArray = jSONObject.getJSONArray("data");
            ServicesDataEntry servicesDataEntry = new ServicesDataEntry(Type.values()[i]);
            servicesDataEntry.setName(optString);
            if (jSONArray != null && jSONArray.length() > 0) {
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                    if (jSONObject2 != null && (fromJson = Service.fromJson(jSONObject2)) != null) {
                        fromJson.setRawData(jSONObject2.toString());
                        arrayList.add(fromJson);
                    }
                }
                servicesDataEntry.setServices(arrayList);
            }
            return servicesDataEntry;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGroupName() {
        return this.mGroupName;
    }

    public Type getItemType() {
        return this.mItemType;
    }

    public List<Service> getServices() {
        return this.mServices;
    }

    public void setItemType(Type type) {
        this.mItemType = type;
    }

    public void setName(String str) {
        this.mGroupName = str;
    }

    public void setServices(List<Service> list) {
        this.mServices = list;
    }
}
