package androidx.core.text;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

@SuppressLint({"InlinedApi"})
/* loaded from: classes.dex */
public final class HtmlCompat {
    public static Spanned fromHtml(String source, int flags) {
        return Build.VERSION.SDK_INT >= 24 ? Html.fromHtml(source, flags) : Html.fromHtml(source);
    }

    public static String toHtml(Spanned text, int options) {
        return Build.VERSION.SDK_INT >= 24 ? Html.toHtml(text, options) : Html.toHtml(text);
    }
}
