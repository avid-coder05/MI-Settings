package miui.yellowpage;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import java.io.Serializable;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes4.dex */
public class YellowPageStatistic {
    private static final String TAG = "YellowPageStatistic";

    /* loaded from: classes4.dex */
    public interface Display {
        public static final String CALL = "call";
        public static final String CATEGORY = "category";
        public static final String EXPRESS_INQUIRY = "expressInquery";
        public static final String EXPRESS_SEND = "expressSend";
        public static final String FLOW_OF_PACKAGES = "flowOfPackages";
        public static final String HOME = "home";
        public static final String NAVIGATION = "navigation";
        public static final String NEARBY_HOTCAT = "nearby_hotcat";
        public static final String NEARBY_YP = "nearby_yp";
        public static final String RECHARGE = "recharge";
        public static final String SEARCH_NAVIGATION = "search_navigation";
        public static final String SMS = "sms";
        public static final String USER_CENTER = "ucenter";
        public static final String WEB = "web";
    }

    /* loaded from: classes4.dex */
    public static class StatsContext implements Serializable {
        public static final StatsContext EMPTY = new StatsContext("", 0);
        private static final long serialVersionUID = 998449268880523939L;
        private String mSource;
        private int mSourceModuleId;

        private StatsContext(String str, int i) {
            this.mSource = str;
            this.mSourceModuleId = i;
        }

        public static StatsContext parse(Intent intent) {
            String str;
            int i = 0;
            if (intent != null) {
                i = intent.getIntExtra("mid", 0);
                str = intent.getStringExtra("source");
                if ((i == 0 || TextUtils.isEmpty(str)) && YellowPageStatistic.isUriIntent(intent)) {
                    Uri data = intent.getData();
                    if (i == 0) {
                        String queryParameter = data.getQueryParameter("mid");
                        if (!TextUtils.isEmpty(queryParameter) && TextUtils.isDigitsOnly(queryParameter)) {
                            i = Integer.parseInt(queryParameter);
                        }
                    }
                    if (TextUtils.isEmpty(str)) {
                        str = data.getQueryParameter("source");
                    }
                }
            } else {
                str = "";
            }
            Log.d(YellowPageStatistic.TAG, "mid: " + i + ", source: " + str);
            return new StatsContext(str, i);
        }

        public static StatsContext parse(Bundle bundle) {
            String str;
            int i = 0;
            if (bundle != null) {
                i = bundle.getInt("mid", 0);
                str = bundle.getString("source");
            } else {
                str = "";
            }
            return new StatsContext(str, i);
        }

        public void attach(Intent intent) {
            if (intent == null) {
                return;
            }
            intent.putExtra("source", this.mSource);
            intent.putExtra("mid", this.mSourceModuleId);
        }

        public void attach(Bundle bundle) {
            if (bundle == null) {
                return;
            }
            bundle.putString("source", this.mSource);
            bundle.putInt("mid", this.mSourceModuleId);
        }

        public String getSource() {
            return this.mSource;
        }

        public int getSourceModuleId() {
            return this.mSourceModuleId;
        }
    }

    private YellowPageStatistic() {
    }

    public static void clickModuleItem(final Context context, final String str, final String str2, final String str3, final String str4, final int i) {
        ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.YellowPageStatistic.4
            @Override // java.lang.Runnable
            public void run() {
                Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_CLICK_MODULE_ITEM);
                if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("moduleId", str);
                    contentValues.put("hotLinkUrl", str2);
                    contentValues.put("source", str3);
                    contentValues.put("display", str4);
                    contentValues.put("sourceModuleId", Integer.valueOf(i));
                    context.getContentResolver().insert(withAppendedPath, contentValues);
                }
            }
        });
    }

    public static void clickNavigationItem(final Context context, final String str) {
        ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.YellowPageStatistic.3
            @Override // java.lang.Runnable
            public void run() {
                Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_CLICK_NAVIGATION_ITEM);
                if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("moduleId", str);
                    context.getContentResolver().insert(withAppendedPath, contentValues);
                }
            }
        });
    }

    public static void clickSearchItem(final Context context, final String str, final String str2, final String str3, final String str4) {
        ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.YellowPageStatistic.6
            @Override // java.lang.Runnable
            public void run() {
                Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_CLICK_SEARCH_ITEM);
                if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("type", str);
                    contentValues.put("id", str2);
                    contentValues.put("keyword", str3);
                    contentValues.put("index", str4);
                    context.getContentResolver().insert(withAppendedPath, contentValues);
                }
            }
        });
    }

    public static void clickYellowPage(final Context context, final String str, final String str2, final String str3, final int i) {
        ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.YellowPageStatistic.5
            @Override // java.lang.Runnable
            public void run() {
                Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_CLICK_YELLOW_PAGE);
                if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("yid", str);
                    contentValues.put("display", str2);
                    contentValues.put("source", str3);
                    contentValues.put("srcModuleId", Integer.valueOf(i));
                    context.getContentResolver().insert(withAppendedPath, contentValues);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isUriIntent(Intent intent) {
        return (intent == null || !"android.intent.action.VIEW".equals(intent.getAction()) || intent.getData() == null) ? false : true;
    }

    public static void logEvent(final Context context, final String str, final String str2, final String str3, final int i, final String str4) {
        ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.YellowPageStatistic.7
            @Override // java.lang.Runnable
            public void run() {
                Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_LOG_EVENT);
                if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Tag.TagWebService.CommonResult.RESULT_TYPE_EVENT, str);
                    contentValues.put("display", str2);
                    contentValues.put("source", str3);
                    contentValues.put("srcModuleId", Integer.valueOf(i));
                    contentValues.put("values", str4);
                    context.getContentResolver().insert(withAppendedPath, contentValues);
                }
            }
        });
    }

    public static boolean uploadData(Context context) {
        Uri insert;
        Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_UPLOAD_DATA);
        if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath) && (insert = context.getContentResolver().insert(withAppendedPath, new ContentValues())) != null) {
            return "1".equals(insert.getLastPathSegment());
        }
        return false;
    }

    public static void viewNormalDisplay(final Context context, final String str, final String str2, final int i) {
        ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.YellowPageStatistic.2
            @Override // java.lang.Runnable
            public void run() {
                Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_VIEW_NORMAL_DISPLAY);
                if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("display", str);
                    contentValues.put("source", str2);
                    contentValues.put("srcModuleId", Integer.valueOf(i));
                    context.getContentResolver().insert(withAppendedPath, contentValues);
                }
            }
        });
    }

    public static void viewYellowPageInPhoneCall(final Context context, final String str, final int i, final boolean z, final String str2, final String str3, final boolean z2) {
        ThreadPool.execute(new Runnable() { // from class: miui.yellowpage.YellowPageStatistic.1
            @Override // java.lang.Runnable
            public void run() {
                Uri withAppendedPath = Uri.withAppendedPath(YellowPageContract.Statistic.CONTENT_URI, YellowPageContract.Statistic.DIRECTORY_VIEW_YELLOWPAGE_IN_PHONE_CALL);
                if (YellowPageUtils.isContentProviderInstalled(context, withAppendedPath)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("number", str);
                    contentValues.put("type", Integer.valueOf(i));
                    contentValues.put("hit", Boolean.valueOf(z));
                    contentValues.put("yid", str2);
                    contentValues.put("displayAdName", str3);
                    contentValues.put("show", z2 ? "1" : "0");
                    ContentProviderClient contentProviderClient = null;
                    try {
                        try {
                            contentProviderClient = context.getContentResolver().acquireUnstableContentProviderClient(withAppendedPath);
                            contentProviderClient.insert(withAppendedPath, contentValues);
                        } catch (RemoteException e) {
                            Log.e(YellowPageStatistic.TAG, "", e);
                            if (contentProviderClient == null) {
                                return;
                            }
                        }
                        contentProviderClient.release();
                    } catch (Throwable th) {
                        if (contentProviderClient != null) {
                            contentProviderClient.release();
                        }
                        throw th;
                    }
                }
            }
        });
    }
}
