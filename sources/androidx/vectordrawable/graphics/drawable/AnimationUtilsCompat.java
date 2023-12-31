package androidx.vectordrawable.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class AnimationUtilsCompat {
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00cc, code lost:
    
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.view.animation.Interpolator createInterpolatorFromXml(android.content.Context r4, org.xmlpull.v1.XmlPullParser r5) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            int r0 = r5.getDepth()
            r1 = 0
        L5:
            int r2 = r5.next()
            r3 = 3
            if (r2 != r3) goto L12
            int r3 = r5.getDepth()
            if (r3 <= r0) goto Lcc
        L12:
            r3 = 1
            if (r2 == r3) goto Lcc
            r3 = 2
            if (r2 == r3) goto L19
            goto L5
        L19:
            android.util.AttributeSet r1 = android.util.Xml.asAttributeSet(r5)
            java.lang.String r2 = r5.getName()
            java.lang.String r3 = "linearInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L2f
            android.view.animation.LinearInterpolator r1 = new android.view.animation.LinearInterpolator
            r1.<init>()
            goto L5
        L2f:
            java.lang.String r3 = "accelerateInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L3e
            android.view.animation.AccelerateInterpolator r2 = new android.view.animation.AccelerateInterpolator
            r2.<init>(r4, r1)
        L3c:
            r1 = r2
            goto L5
        L3e:
            java.lang.String r3 = "decelerateInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L4c
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>(r4, r1)
            goto L3c
        L4c:
            java.lang.String r3 = "accelerateDecelerateInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L5a
            android.view.animation.AccelerateDecelerateInterpolator r1 = new android.view.animation.AccelerateDecelerateInterpolator
            r1.<init>()
            goto L5
        L5a:
            java.lang.String r3 = "cycleInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L68
            android.view.animation.CycleInterpolator r2 = new android.view.animation.CycleInterpolator
            r2.<init>(r4, r1)
            goto L3c
        L68:
            java.lang.String r3 = "anticipateInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L76
            android.view.animation.AnticipateInterpolator r2 = new android.view.animation.AnticipateInterpolator
            r2.<init>(r4, r1)
            goto L3c
        L76:
            java.lang.String r3 = "overshootInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L85
            android.view.animation.OvershootInterpolator r2 = new android.view.animation.OvershootInterpolator
            r2.<init>(r4, r1)
            goto L3c
        L85:
            java.lang.String r3 = "anticipateOvershootInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L93
            android.view.animation.AnticipateOvershootInterpolator r2 = new android.view.animation.AnticipateOvershootInterpolator
            r2.<init>(r4, r1)
            goto L3c
        L93:
            java.lang.String r3 = "bounceInterpolator"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto La2
            android.view.animation.BounceInterpolator r1 = new android.view.animation.BounceInterpolator
            r1.<init>()
            goto L5
        La2:
            java.lang.String r3 = "pathInterpolator"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto Lb1
            androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat r2 = new androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat
            r2.<init>(r4, r1, r5)
            goto L3c
        Lb1:
            java.lang.RuntimeException r4 = new java.lang.RuntimeException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Unknown interpolator name: "
            r0.append(r1)
            java.lang.String r5 = r5.getName()
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            r4.<init>(r5)
            throw r4
        Lcc:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.vectordrawable.graphics.drawable.AnimationUtilsCompat.createInterpolatorFromXml(android.content.Context, org.xmlpull.v1.XmlPullParser):android.view.animation.Interpolator");
    }

    public static Interpolator loadInterpolator(Context context, int i) throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= 21) {
            return AnimationUtils.loadInterpolator(context, i);
        }
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                if (i == 17563663) {
                    return new FastOutLinearInInterpolator();
                }
                if (i == 17563661) {
                    return new FastOutSlowInInterpolator();
                }
                if (i == 17563662) {
                    return new LinearOutSlowInInterpolator();
                }
                XmlResourceParser animation = context.getResources().getAnimation(i);
                Interpolator createInterpolatorFromXml = createInterpolatorFromXml(context, animation);
                if (animation != null) {
                    animation.close();
                }
                return createInterpolatorFromXml;
            } catch (IOException e) {
                Resources.NotFoundException notFoundException = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                notFoundException.initCause(e);
                throw notFoundException;
            } catch (XmlPullParserException e2) {
                Resources.NotFoundException notFoundException2 = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                notFoundException2.initCause(e2);
                throw notFoundException2;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }
}
