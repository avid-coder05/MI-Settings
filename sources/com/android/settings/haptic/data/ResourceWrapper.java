package com.android.settings.haptic.data;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ResourceWrapper {
    private static List<ShowResource> loadContentDescriptionResource(Context context, int i) {
        if (i != 3) {
            if (i != 4) {
                if (i != 5) {
                    if (i != 6) {
                        return null;
                    }
                    return ResourceOverlay.interestingContentDescription;
                }
                return ResourceOverlay.muffledContentDescription;
            }
            return ResourceOverlay.crispContentDescription;
        }
        return ResourceOverlay.elasticContentDescription;
    }

    public static List<HapticResource> loadResource(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        List<ShowResource> loadShowResource = loadShowResource(context, i);
        List<ShowResource> loadSubtitleResource = loadSubtitleResource(context, i);
        List<ShowResource> loadVideoBgResource = loadVideoBgResource(context, i);
        List<ShowResource> loadContentDescriptionResource = loadContentDescriptionResource(context, i);
        if (loadShowResource == null) {
            Log.e("ResourceWrapper", "Couldn't load show resources for type=" + i);
            return arrayList;
        }
        for (int i2 = 0; i2 < loadShowResource.size(); i2++) {
            ShowResource showResource = loadShowResource.get(i2);
            arrayList.add(new HapticResource(showResource.resID, loadSubtitleResource.get(i2).resID, loadVideoBgResource.get(i2).resID, loadContentDescriptionResource.get(i2).resID, showResource.type));
        }
        return arrayList;
    }

    private static List<ShowResource> loadShowResource(Context context, int i) {
        if (i != 3) {
            if (i != 4) {
                if (i != 5) {
                    if (i != 6) {
                        return null;
                    }
                    return ResourceOverlay.interestingVideo;
                }
                return ResourceOverlay.muffledVideo;
            }
            return ResourceOverlay.crispVideo;
        }
        return ResourceOverlay.elasticVideo;
    }

    private static List<ShowResource> loadSubtitleResource(Context context, int i) {
        if (i != 3) {
            if (i != 4) {
                if (i != 5) {
                    if (i != 6) {
                        return null;
                    }
                    return ResourceOverlay.interestingSubtitle;
                }
                return ResourceOverlay.muffledSubtitle;
            }
            return ResourceOverlay.crispSubtitle;
        }
        return ResourceOverlay.elasticSubtitle;
    }

    private static List<ShowResource> loadVideoBgResource(Context context, int i) {
        if (i != 3) {
            if (i != 4) {
                if (i != 5) {
                    if (i != 6) {
                        return null;
                    }
                    return ResourceOverlay.interestingVideoBg;
                }
                return ResourceOverlay.muffledVideoBg;
            }
            return ResourceOverlay.crispVideoBg;
        }
        return ResourceOverlay.elasticVideoBg;
    }
}
