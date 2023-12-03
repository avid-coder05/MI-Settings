package miui.content.res;

import java.io.InputStream;

/* loaded from: classes3.dex */
public class ThemeResourcesEmpty extends ThemeResources {
    public static ThemeResourcesEmpty sInstance = new ThemeResourcesEmpty();

    /* loaded from: classes3.dex */
    public interface LoadThemeValuesCallback {
    }

    protected ThemeResourcesEmpty() {
        super(null, null, "FakeForEmpty", ThemeResources.THEME_PATHS[0]);
    }

    @Override // miui.content.res.ThemeResources
    public long checkUpdate() {
        return 0L;
    }

    @Override // miui.content.res.ThemeResources
    public InputStream getThemeStream(String str, long[] jArr) {
        return null;
    }

    @Override // miui.content.res.ThemeResources
    public boolean hasThemeFile(String str) {
        return false;
    }

    @Override // miui.content.res.ThemeResources
    public void mergeThemeValues(String str, ThemeValues themeValues) {
    }
}
