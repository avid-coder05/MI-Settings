package miui.yellowpage;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.payment.PaymentManager;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes4.dex */
public class Service {
    private static final String TAG = "Service";
    private String mActions;
    private Map<String, Integer> mExtraData;
    private String mIcon;
    private boolean mIsMiFamily;
    private int mMid;
    private String mName;
    private String mRawData;

    public static Service fromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return fromJson(new JSONObject(str));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to get json object! ", e);
            return null;
        }
    }

    public static Service fromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        int optInt = jSONObject.optInt("mid");
        String optString = jSONObject.optString("name");
        String optString2 = jSONObject.optString("icon");
        JSONObject optJSONObject = jSONObject.optJSONObject("extraData");
        HashMap hashMap = new HashMap();
        if (optJSONObject != null) {
            hashMap.put(Tag.TagServicesData.SERVICE_IS_PROMOTE, Integer.valueOf(optJSONObject.optInt(Tag.TagServicesData.SERVICE_IS_PROMOTE)));
            hashMap.put(Tag.TagServicesData.SERVICE_IS_HOT, Integer.valueOf(optJSONObject.optInt(Tag.TagServicesData.SERVICE_IS_HOT)));
            hashMap.put(Tag.TagServicesData.SERVICE_IS_NEW, Integer.valueOf(optJSONObject.optInt(Tag.TagServicesData.SERVICE_IS_NEW)));
        }
        String optString3 = jSONObject.optString(Tag.TagServicesData.SERVICE_ACTIONS);
        boolean optBoolean = jSONObject.optBoolean(Tag.TagServicesData.IS_MIFAMILY_ENTRANCE);
        if (TextUtils.isEmpty(optString)) {
            return null;
        }
        return new Service().setMid(optInt).setName(optString).setIcon(optString2).setExtraData(hashMap).setActions(optString3).setIsMiFamily(optBoolean);
    }

    public static void serviceOnClick(Context context, String str) {
        serviceOnClick(context, str, null, null);
    }

    public static void serviceOnClick(Context context, String str, String str2, String str3) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            Intent intent = (Intent) InvocationHandler.invoke(context, YellowPageContract.Method.MODULE_TO_INTENT, str).getParcelable(PaymentManager.KEY_INTENT);
            if (intent != null) {
                int optInt = jSONObject.optInt("mid", 0);
                intent.putExtra(Tag.Intent.EXTRA_WEB_TITLE, jSONObject.optString("name"));
                intent.putExtra("mid", optInt);
                String stringExtra = intent.getStringExtra(Tag.Intent.EXTRA_WEB_URL);
                context.startActivity(intent);
                YellowPageStatistic.clickModuleItem(context, String.valueOf(optInt), stringExtra, str2, str3, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get json object! ", e);
        }
    }

    public String getActions() {
        return this.mActions;
    }

    public Map<String, Integer> getExtraData() {
        return this.mExtraData;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public boolean getIsMiFamily() {
        return this.mIsMiFamily;
    }

    public List<String> getMiStatKeyArgs(String str) {
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str)) {
            arrayList.add(str);
        }
        arrayList.add(String.valueOf(this.mMid));
        if (!TextUtils.isEmpty(this.mName)) {
            arrayList.add(this.mName);
        }
        return arrayList;
    }

    public int getMid() {
        return this.mMid;
    }

    public String getName() {
        return this.mName;
    }

    public String getRawData() {
        return this.mRawData;
    }

    public boolean hasExtraData() {
        Map<String, Integer> map = this.mExtraData;
        return (map == null || map.size() == 0) ? false : true;
    }

    public Service setActions(String str) {
        this.mActions = str;
        return this;
    }

    public Service setExtraData(Map<String, Integer> map) {
        this.mExtraData = map;
        return this;
    }

    public Service setIcon(String str) {
        this.mIcon = str;
        return this;
    }

    public Service setIsMiFamily(boolean z) {
        this.mIsMiFamily = z;
        return this;
    }

    public Service setMid(int i) {
        this.mMid = i;
        return this;
    }

    public Service setName(String str) {
        this.mName = str;
        return this;
    }

    public void setRawData(String str) {
        this.mRawData = str;
    }
}
