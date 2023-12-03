package com.android.settings.bluetooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public class HeadsetIDConstants {
    private HeadsetIDConstants() {
    }

    public static boolean checkSupport(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String[] split = str.split("\\,");
        if (split.length != 2) {
            Log.e("HeadsetIDConstants", "error length");
            return false;
        }
        String str2 = split[0];
        HashSet hashSet = new HashSet(Arrays.asList("01010402", "01010600", "01010602", "01010603", "01010601", "01010605", "01010606", "0201010000", "01010607", "01010703", "01010704", "01011004", "01010705", "01010707", "01011103", "01010901", "01010902", "01010903", "01010904", "0201010001", "01010403", "01010906", "01010907"));
        if (hashSet.size() <= 0 || TextUtils.isEmpty(str2)) {
            return false;
        }
        return hashSet.contains(str2);
    }

    public static Bitmap decodeImageResource(Context context, int i) {
        Bitmap bitmap;
        InputStream inputStream = null;
        Bitmap bitmap2 = null;
        inputStream = null;
        try {
            try {
                InputStream openRawResource = context.getResources().openRawResource(i);
                if (openRawResource == null) {
                    if (openRawResource != null) {
                        try {
                            openRawResource.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("HeadsetIDConstants", "is close exception");
                        }
                    }
                    return null;
                }
                try {
                    ArrayList arrayList = new ArrayList();
                    Log.d("HeadsetIDConstants", "before time " + System.currentTimeMillis());
                    int i2 = 0;
                    while (true) {
                        int read = openRawResource.read();
                        if (read <= -1) {
                            break;
                        }
                        if (i2 == 64) {
                            Log.d("HeadsetIDConstants", "" + read);
                            read ^= 153;
                        }
                        i2++;
                        arrayList.add(Byte.valueOf((byte) read));
                    }
                    Log.d("HeadsetIDConstants", "after1 time " + System.currentTimeMillis() + " " + arrayList.size());
                    byte[] bArr = new byte[arrayList.size()];
                    Iterator it = arrayList.iterator();
                    int i3 = 0;
                    while (it.hasNext()) {
                        bArr[i3] = ((Byte) it.next()).byteValue();
                        i3++;
                    }
                    Log.d("HeadsetIDConstants", "after2 time " + System.currentTimeMillis());
                    bitmap2 = BitmapFactory.decodeByteArray(bArr, 0, arrayList.size());
                    Log.d("HeadsetIDConstants", "after3 time " + System.currentTimeMillis());
                    try {
                        openRawResource.close();
                        return bitmap2;
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        Log.e("HeadsetIDConstants", "is close exception");
                        return bitmap2;
                    }
                } catch (Exception e3) {
                    e = e3;
                    Bitmap bitmap3 = bitmap2;
                    inputStream = openRawResource;
                    bitmap = bitmap3;
                    e.printStackTrace();
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                            Log.e("HeadsetIDConstants", "is close exception");
                        }
                    }
                    return bitmap;
                } catch (Throwable th) {
                    th = th;
                    inputStream = openRawResource;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                            Log.e("HeadsetIDConstants", "is close exception");
                        }
                    }
                    throw th;
                }
            } catch (Exception e6) {
                e = e6;
                bitmap = null;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean isK71Headset(String str) {
        return !TextUtils.isEmpty(str) && "0201010000".equals(str);
    }

    public static boolean isK71HeadsetGlobal(String str) {
        return "0201010001".equals(str);
    }

    public static boolean isK73ABlackHeadset(String str) {
        return "01010707".equals(str);
    }

    public static boolean isK73AGreenHeadset(String str) {
        return "01011103".equals(str);
    }

    public static boolean isK73AWhiteHeadset(String str) {
        return "01010705".equals(str);
    }

    public static boolean isK73BlackHeadset(String str) {
        return "01010703".equals(str);
    }

    public static boolean isK73DomesticHeadset(String str) {
        return "01010607".equals(str) || "01010703".equals(str) || "01010704".equals(str) || "01011004".equals(str);
    }

    public static boolean isK73GreenHeadset(String str) {
        return "01010704".equals(str);
    }

    public static boolean isK73Headset(String str) {
        return "01010607".equals(str) || "01010703".equals(str) || "01010704".equals(str) || "01011004".equals(str) || "01010705".equals(str) || "01010707".equals(str) || "01011103".equals(str);
    }

    public static boolean isK73LBlueHeadset(String str) {
        return "01011004".equals(str);
    }

    public static boolean isK73WhiteHeadset(String str) {
        return "01010607".equals(str);
    }

    public static boolean isK75ABlackHeadset(String str) {
        return "01010904".equals(str);
    }

    public static boolean isK75AWhiteHeadset(String str) {
        return "01010903".equals(str);
    }

    public static boolean isK75BlackHeadset(String str) {
        return "01010902".equals(str);
    }

    public static boolean isK75DomesticHeadset(String str) {
        return "01010901".equals(str) || "01010902".equals(str);
    }

    public static boolean isK75Headset(String str) {
        return "01010901".equals(str) || "01010902".equals(str) || "01010903".equals(str) || "01010904".equals(str);
    }

    public static boolean isK75WhiteHeadset(String str) {
        return "01010901".equals(str);
    }

    public static boolean isK76sHeadset(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return "01010906".equals(str) || "01010907".equals(str);
    }

    public static boolean isK77sDomesticHeadset(String str) {
        return "01010605".equals(str);
    }

    public static boolean isK77sHeadset(String str) {
        return "01010605".equals(str) || "01010606".equals(str);
    }

    public static boolean isSupportZimiAdapter(String str, String str2) {
        if (str == null) {
            return false;
        }
        if (str.equals("common")) {
            if (!"01010403".equals(str2)) {
                return false;
            }
        } else if (!str.equals("anc") || !"01010403".equals(str2)) {
            return false;
        }
        return true;
    }

    public static boolean isTWS01BlackHeadset(String str) {
        return "01010600".equals(str) || "01010603".equals(str);
    }

    public static boolean isTWS01DomesticHeadset(String str) {
        return "01010402".equals(str) || "01010600".equals(str) || "01010906".equals(str);
    }

    public static boolean isTWS01GlobalHeadset(String str) {
        return "01010602".equals(str) || "01010603".equals(str) || "01010907".equals(str);
    }

    public static boolean isTWS01GrayHeadset(String str) {
        return "01010402".equals(str) || "01010602".equals(str);
    }

    public static boolean isTWS01Headset(String str) {
        return "01010402".equals(str) || "01010600".equals(str) || "01010602".equals(str) || "01010603".equals(str) || "01010601".equals(str) || "01010906".equals(str) || "01010907".equals(str);
    }

    public static boolean isTWS01YellowHeadset(String str) {
        return "01010601".equals(str);
    }

    public static boolean isTWS200(String str) {
        return "01010403".equals(str);
    }

    public static boolean isUseInearBitForAutoAckHeadset(String str) {
        return isK77sHeadset(str) || isK73DomesticHeadset(str) || isK75DomesticHeadset(str);
    }
}
