package com.android.settings.utils;

import android.os.Build;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class ActivationInfoUtil {
    private static final List<String> sBlockList;

    static {
        ArrayList arrayList = new ArrayList();
        sBlockList = arrayList;
        arrayList.add("ginkgo");
        arrayList.add("cepheus");
        arrayList.add("davinci");
        arrayList.add("raphael");
        arrayList.add("crux");
        arrayList.add("grus");
        arrayList.add("pyxis");
        arrayList.add("vela");
        arrayList.add("tucana");
        arrayList.add("lavender");
        arrayList.add("violet");
        arrayList.add("laurus");
        arrayList.add("begonia");
        arrayList.add("picasso");
        arrayList.add("picasso_48m");
        arrayList.add("phoenix");
        arrayList.add("cmi");
        arrayList.add("cezanne");
        arrayList.add("lmi");
        arrayList.add("cas");
        arrayList.add("umi");
        arrayList.add("apollo");
        arrayList.add("atom");
        arrayList.add("bomb");
        arrayList.add("vangogh");
        arrayList.add("enuma");
        arrayList.add("elish");
        arrayList.add("nabu");
        arrayList.add("whyred");
        arrayList.add("polaris");
        arrayList.add("wayne");
        arrayList.add("ysl");
        arrayList.add("dipper");
        arrayList.add("sirius");
        arrayList.add("ursa");
        arrayList.add("cereus");
        arrayList.add("cactus");
        arrayList.add("sakura");
        arrayList.add("nitrogen");
        arrayList.add("equuleus");
        arrayList.add("platina");
        arrayList.add("perseus");
        arrayList.add("lotus");
        arrayList.add("onclite");
        arrayList.add("pine");
        arrayList.add("raphaels");
        arrayList.add("olivelite");
        arrayList.add("olive");
        arrayList.add("merlin");
        arrayList.add("lancelot");
        arrayList.add("dandelion");
    }

    public static boolean isCurrentDeviceInBlockList() {
        return sBlockList.contains(Build.DEVICE.toLowerCase());
    }
}
