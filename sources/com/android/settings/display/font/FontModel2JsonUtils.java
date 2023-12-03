package com.android.settings.display.font;

import com.android.settings.display.LocalFontModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FontModel2JsonUtils {
    public static LocalFontModel Json2LocalFont(JSONObject jSONObject) {
        LocalFontModel localFontModel = new LocalFontModel(null, null, null, false);
        for (Field field : LocalFontModel.class.getDeclaredFields()) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                if (field.getName().equals("fontWeight") && jSONObject.has("fontWeight")) {
                    ArrayList arrayList = new ArrayList();
                    JSONArray jSONArray = jSONObject.getJSONArray("fontWeight");
                    for (int i = 0; i < jSONArray.length(); i++) {
                        arrayList.add((Integer) jSONArray.get(i));
                    }
                    field.set(localFontModel, arrayList);
                } else if (jSONObject.has(field.getName())) {
                    field.set(localFontModel, jSONObject.get(field.getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return localFontModel;
    }

    public static JSONObject LocalFont2Json(LocalFontModel localFontModel) {
        JSONObject jSONObject = new JSONObject();
        for (Field field : localFontModel.getClass().getDeclaredFields()) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                if (field.getName().equals("fontWeight")) {
                    JSONArray jSONArray = new JSONArray();
                    List list = (List) field.get(localFontModel);
                    for (int i = 0; i < list.size(); i++) {
                        jSONArray.put(i, list.get(i));
                    }
                    jSONObject.put("fontWeight", jSONArray);
                } else {
                    jSONObject.put(field.getName(), field.get(localFontModel));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jSONObject;
    }
}
