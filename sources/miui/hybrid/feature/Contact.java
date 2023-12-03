package miui.hybrid.feature;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.text.TextUtils;
import java.util.Map;
import miui.hybrid.HybridFeature;
import miui.hybrid.LifecycleListener;
import miui.hybrid.NativeInterface;
import miui.hybrid.Request;
import miui.hybrid.Response;
import miui.provider.Notes;
import miui.telephony.PhoneNumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class Contact implements HybridFeature {
    private static final String ACTION_LOOKUP = "lookup";
    private static final String ACTION_PICK_PHONE_NUMBER = "pickPhoneNumber";
    private static final String KEY_CONTACT_LIST = "contactList";
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_PHONE_LABEL = "label";
    private static final String KEY_PHONE_NUMBER = "number";
    private static final String KEY_PHONE_TYPE = "type";
    private static final String PACKAGE_NAME_CONTACTS = "com.android.contacts";
    private static final String[] PROJECTION_PHONE = {"display_name", Notes.Data.DATA4, "data2", "data3"};
    private static final int REQUEST_CODE_PICK = 1;

    /* JADX INFO: Access modifiers changed from: private */
    public static JSONObject getContactPhoneNumber(ContentResolver contentResolver, Uri uri) {
        Cursor query = contentResolver.query(uri, PROJECTION_PHONE, null, null, null);
        JSONObject jSONObject = new JSONObject();
        try {
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        jSONObject.put(KEY_DISPLAY_NAME, query.getString(query.getColumnIndex("display_name")));
                        jSONObject.put("number", query.getString(query.getColumnIndex(Notes.Data.DATA4)));
                        jSONObject.put("type", query.getString(query.getColumnIndex("data2")));
                        jSONObject.put("label", query.getString(query.getColumnIndex("data3")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jSONObject;
        } finally {
            query.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static JSONObject getContactsByPhoneNumber(ContentResolver contentResolver, String str) {
        JSONArray jSONArray = new JSONArray();
        Cursor query = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI.buildUpon().appendPath(str).build(), PROJECTION_PHONE, null, null, null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    try {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put(KEY_DISPLAY_NAME, query.getString(query.getColumnIndex("display_name")));
                        jSONObject.put("number", query.getString(query.getColumnIndex(Notes.Data.DATA4)));
                        jSONObject.put("type", query.getString(query.getColumnIndex("data2")));
                        jSONObject.put("label", query.getString(query.getColumnIndex("data3")));
                        jSONArray.put(jSONObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } finally {
                    query.close();
                }
            }
        }
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put(KEY_CONTACT_LIST, jSONArray);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return jSONObject2;
    }

    private Response lookupContact(final Request request) {
        try {
            String normalizePhoneNumber = normalizePhoneNumber(new JSONObject(request.getRawParams()).getString("phoneNumber"));
            final ContentResolver contentResolver = request.getNativeInterface().getActivity().getContentResolver();
            new AsyncTask<String, Void, JSONObject>() { // from class: miui.hybrid.feature.Contact.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public JSONObject doInBackground(String... strArr) {
                    return Contact.getContactsByPhoneNumber(contentResolver, strArr[0]);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(JSONObject jSONObject) {
                    request.getCallback().callback(new Response(0, jSONObject));
                }
            }.execute(normalizePhoneNumber);
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return new Response(204, "param not valid");
        }
    }

    private static String normalizePhoneNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        PhoneNumberUtils.PhoneNumber parse = PhoneNumberUtils.PhoneNumber.parse(str);
        String normalizedNumber = parse.getNormalizedNumber(false, true);
        parse.recycle();
        return normalizedNumber;
    }

    private Response pickPhoneNumber(final Request request) {
        final NativeInterface nativeInterface = request.getNativeInterface();
        Activity activity = nativeInterface.getActivity();
        nativeInterface.addLifecycleListener(new LifecycleListener() { // from class: miui.hybrid.feature.Contact.2
            public void onActivityResult(int i, int i2, Intent intent) {
                nativeInterface.removeLifecycleListener(this);
                if (i == 1) {
                    if (i2 == -1) {
                        final ContentResolver contentResolver = request.getNativeInterface().getActivity().getContentResolver();
                        new AsyncTask<Uri, Void, JSONObject>() { // from class: miui.hybrid.feature.Contact.2.1
                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            public JSONObject doInBackground(Uri... uriArr) {
                                return Contact.getContactPhoneNumber(contentResolver, uriArr[0]);
                            }

                            /* JADX INFO: Access modifiers changed from: protected */
                            @Override // android.os.AsyncTask
                            public void onPostExecute(JSONObject jSONObject) {
                                request.getCallback().callback(new Response(0, jSONObject));
                            }
                        }.execute(intent.getData());
                    } else if (i2 == 0) {
                        request.getCallback().callback(new Response(100, "cancel"));
                    } else {
                        request.getCallback().callback(new Response(200));
                    }
                }
            }
        });
        Intent intent = new Intent("android.intent.action.PICK", ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        intent.setType("vnd.android.cursor.dir/phone_v2");
        intent.setPackage("com.android.contacts");
        activity.startActivityForResult(intent, 1);
        return null;
    }

    public HybridFeature.Mode getInvocationMode(Request request) {
        if (TextUtils.equals(request.getAction(), ACTION_LOOKUP) || TextUtils.equals(request.getAction(), ACTION_PICK_PHONE_NUMBER)) {
            return HybridFeature.Mode.CALLBACK;
        }
        return null;
    }

    public Response invoke(Request request) {
        return TextUtils.equals(request.getAction(), ACTION_PICK_PHONE_NUMBER) ? pickPhoneNumber(request) : TextUtils.equals(request.getAction(), ACTION_LOOKUP) ? lookupContact(request) : new Response(204, "no such action");
    }

    public void setParams(Map<String, String> map) {
    }
}
