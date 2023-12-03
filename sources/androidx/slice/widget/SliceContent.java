package androidx.slice.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceUtils;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class SliceContent {
    protected SliceItem mColorItem;
    protected SliceItem mContentDescr;
    protected SliceItem mLayoutDirItem;
    protected int mRowIndex;
    protected SliceItem mSliceItem;

    public SliceContent(Slice slice) {
        if (slice == null) {
            return;
        }
        init(new SliceItem(slice, "slice", (String) null, slice.getHints()));
        this.mRowIndex = -1;
    }

    public SliceContent(SliceItem item, int rowIndex) {
        if (item == null) {
            return;
        }
        init(item);
        this.mRowIndex = rowIndex;
    }

    private SliceAction fallBackToAppData(Context context, SliceItem textItem, SliceItem iconItem, int iconMode, SliceItem actionItem) {
        Intent launchIntentForPackage;
        SliceItem find = SliceQuery.find(this.mSliceItem, "slice", (String) null, (String) null);
        if (find == null) {
            return null;
        }
        Uri uri = find.getSlice().getUri();
        IconCompat icon = iconItem != null ? iconItem.getIcon() : null;
        CharSequence text = textItem != null ? textItem.getText() : null;
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(uri.getAuthority(), 0);
            ApplicationInfo applicationInfo = resolveContentProvider != null ? resolveContentProvider.applicationInfo : null;
            if (applicationInfo != null) {
                if (icon == null) {
                    icon = SliceViewUtil.createIconFromDrawable(packageManager.getApplicationIcon(applicationInfo));
                    iconMode = 2;
                }
                if (text == null) {
                    text = packageManager.getApplicationLabel(applicationInfo);
                }
                if (actionItem == null && (launchIntentForPackage = packageManager.getLaunchIntentForPackage(applicationInfo.packageName)) != null) {
                    actionItem = new SliceItem(PendingIntent.getActivity(context, 0, launchIntentForPackage, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE), new Slice.Builder(uri).build(), "action", null, new String[0]);
                }
            }
        }
        if (actionItem == null) {
            actionItem = new SliceItem(PendingIntent.getActivity(context, 0, new Intent(), MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE), null, "action", null, null);
        }
        if (text == null || icon == null) {
            return null;
        }
        return new SliceActionImpl(actionItem.getAction(), icon, iconMode, text);
    }

    private void init(SliceItem item) {
        this.mSliceItem = item;
        if ("slice".equals(item.getFormat()) || "action".equals(item.getFormat())) {
            this.mColorItem = SliceQuery.findTopLevelItem(item.getSlice(), "int", "color", null, null);
            this.mLayoutDirItem = SliceQuery.findTopLevelItem(item.getSlice(), "int", "layout_direction", null, null);
        }
        this.mContentDescr = SliceQuery.findSubtype(item, "text", "content_description");
    }

    public int getAccentColor() {
        SliceItem sliceItem = this.mColorItem;
        if (sliceItem != null) {
            return sliceItem.getInt();
        }
        return -1;
    }

    public CharSequence getContentDescription() {
        SliceItem sliceItem = this.mContentDescr;
        if (sliceItem != null) {
            return sliceItem.getText();
        }
        return null;
    }

    public int getHeight(SliceStyle style, SliceViewPolicy policy) {
        return 0;
    }

    public int getLayoutDir() {
        SliceItem sliceItem = this.mLayoutDirItem;
        if (sliceItem != null) {
            return SliceViewUtil.resolveLayoutDirection(sliceItem.getInt());
        }
        return -1;
    }

    public int getRowIndex() {
        return this.mRowIndex;
    }

    public SliceAction getShortcut(Context context) {
        SliceItem sliceItem;
        SliceItem sliceItem2;
        SliceItem sliceItem3 = this.mSliceItem;
        if (sliceItem3 == null) {
            return null;
        }
        SliceItem find = SliceQuery.find(sliceItem3, "action", new String[]{"title", "shortcut"}, (String[]) null);
        if (find != null) {
            sliceItem = SliceQuery.find(find, YellowPageContract.ImageLookup.DIRECTORY_IMAGE, "title", (String) null);
            sliceItem2 = SliceQuery.find(find, "text", (String) null, (String) null);
        } else {
            sliceItem = null;
            sliceItem2 = null;
        }
        if (find == null) {
            find = SliceQuery.find(this.mSliceItem, "action", (String) null, (String) null);
        }
        SliceItem sliceItem4 = find;
        if (sliceItem == null) {
            sliceItem = SliceQuery.find(this.mSliceItem, YellowPageContract.ImageLookup.DIRECTORY_IMAGE, "title", (String) null);
        }
        if (sliceItem2 == null) {
            sliceItem2 = SliceQuery.find(this.mSliceItem, "text", "title", (String) null);
        }
        if (sliceItem == null) {
            sliceItem = SliceQuery.find(this.mSliceItem, YellowPageContract.ImageLookup.DIRECTORY_IMAGE, (String) null, (String) null);
        }
        if (sliceItem2 == null) {
            sliceItem2 = SliceQuery.find(this.mSliceItem, "text", (String) null, (String) null);
        }
        int parseImageMode = sliceItem != null ? SliceUtils.parseImageMode(sliceItem) : 5;
        if (context != null) {
            return fallBackToAppData(context, sliceItem2, sliceItem, parseImageMode, sliceItem4);
        }
        if (sliceItem == null || sliceItem4 == null || sliceItem2 == null) {
            return null;
        }
        return new SliceActionImpl(sliceItem4.getAction(), sliceItem.getIcon(), parseImageMode, sliceItem2.getText());
    }

    public SliceItem getSliceItem() {
        return this.mSliceItem;
    }

    public boolean isValid() {
        return this.mSliceItem != null;
    }
}
