package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.TimeZone;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class TimepanelScreenElement extends ImageScreenElement {
    private int mBmpHeight;
    private int mBmpWidth;
    protected Calendar mCalendar;
    private boolean mForceUpdate;
    private String mFormat;
    private Expression mFormatExp;
    private String mFormatRaw;
    private boolean mLoadResourceFailed;
    private char mLocalizedZero;
    private String mOldFormat;
    private String mOldSrc;
    private long mPreMinute;
    private CharSequence mPreTime;
    private int mSpace;
    private Expression mTimeZoneExp;
    private Runnable mUpdateTimeRunnable;

    public TimepanelScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mCalendar = Calendar.getInstance();
        this.mLocalizedZero = DecimalFormatSymbols.getInstance().getZeroDigit();
        this.mUpdateTimeRunnable = new Runnable() { // from class: com.miui.maml.elements.TimepanelScreenElement.1
            @Override // java.lang.Runnable
            public void run() {
                Bitmap bitmap;
                if (TimepanelScreenElement.this.mLoadResourceFailed || (bitmap = TimepanelScreenElement.this.mBitmap.getBitmap()) == null) {
                    return;
                }
                TimepanelScreenElement.this.mCalendar.setTimeInMillis(System.currentTimeMillis());
                if (TimepanelScreenElement.this.mTimeZoneExp != null) {
                    String evaluateStr = TimepanelScreenElement.this.mTimeZoneExp.evaluateStr();
                    if (!TextUtils.isEmpty(evaluateStr)) {
                        TimepanelScreenElement.this.mCalendar.setTimeZone(TimeZone.getTimeZone(evaluateStr));
                    }
                }
                CharSequence format = DateFormat.format(TimepanelScreenElement.this.getFormat(), TimepanelScreenElement.this.mCalendar);
                if (TimepanelScreenElement.this.mForceUpdate || !format.equals(TimepanelScreenElement.this.mPreTime)) {
                    TimepanelScreenElement.this.mPreTime = format;
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    int i = 0;
                    for (int i2 = 0; i2 < format.length(); i2++) {
                        Bitmap digitBmp = TimepanelScreenElement.this.getDigitBmp(format.charAt(i2));
                        if (digitBmp != null) {
                            canvas.drawBitmap(digitBmp, i, 0.0f, (Paint) null);
                            i = i + digitBmp.getWidth() + TimepanelScreenElement.this.mSpace;
                        }
                    }
                    TimepanelScreenElement.this.mBitmap.updateVersion();
                    TimepanelScreenElement timepanelScreenElement = TimepanelScreenElement.this;
                    timepanelScreenElement.mBmpWidth = i - timepanelScreenElement.mSpace;
                    TimepanelScreenElement timepanelScreenElement2 = TimepanelScreenElement.this;
                    timepanelScreenElement2.setActualWidth(timepanelScreenElement2.descale(timepanelScreenElement2.mBmpWidth));
                    TimepanelScreenElement.this.requestUpdate();
                }
            }
        };
        this.mFormatRaw = getAttr(element, "format");
        this.mFormatExp = Expression.build(getVariables(), getAttr(element, "formatExp"));
        this.mSpace = (int) scale(getAttrAsInt(element, "space", 0));
        this.mTimeZoneExp = Expression.build(getVariables(), getAttr(element, "timeZoneId"));
    }

    private void createBitmap() {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < 11; i3++) {
            Bitmap digitBmp = getDigitBmp("0123456789:".charAt(i3));
            if (digitBmp == null) {
                this.mLoadResourceFailed = true;
                Log.e("TimepanelScreenElement", "Failed to load digit bitmap: " + "0123456789:".charAt(i3));
                return;
            }
            if (i < digitBmp.getWidth()) {
                i = digitBmp.getWidth();
            }
            if (this.mBmpHeight < digitBmp.getHeight()) {
                this.mBmpHeight = digitBmp.getHeight();
            }
            if (i2 == 0) {
                i2 = digitBmp.getDensity();
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap((i * 5) + (this.mSpace * 4), this.mBmpHeight, Bitmap.Config.ARGB_8888);
        createBitmap.setDensity(i2);
        this.mBitmap.setBitmap(createBitmap);
        setActualHeight(descale(this.mBmpHeight));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap getDigitBmp(char c) {
        String valueOf;
        String src2 = getSrc();
        if (TextUtils.isEmpty(src2)) {
            src2 = "time.png";
        }
        if (c == ':') {
            valueOf = "dot";
        } else {
            char c2 = this.mLocalizedZero;
            if (c >= c2 && c <= c2 + '\t') {
                c = (char) ((c - c2) + 48);
            }
            valueOf = String.valueOf(c);
        }
        return getContext().mResourceManager.getBitmap(Utils.addFileNameSuffix(src2, valueOf));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getFormat() {
        Expression expression = this.mFormatExp;
        return expression != null ? expression.evaluateStr() : this.mFormat;
    }

    private void setDateFormat() {
        if (TextUtils.isEmpty(this.mFormatRaw) && this.mFormatExp == null) {
            this.mFormat = DateFormat.is24HourFormat(getContext().mContext) ? "kk:mm" : "hh:mm";
        } else {
            this.mFormat = this.mFormatRaw;
        }
    }

    private void updateTime(boolean z) {
        getContext().getHandler().removeCallbacks(this.mUpdateTimeRunnable);
        this.mForceUpdate = z;
        postInMainThread(this.mUpdateTimeRunnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        long currentTimeMillis = System.currentTimeMillis() / 60000;
        String src2 = getSrc();
        String format = getFormat();
        if (currentTimeMillis == this.mPreMinute && TextUtils.equals(src2, this.mOldSrc) && TextUtils.equals(format, this.mOldFormat)) {
            return;
        }
        updateTime(true);
        this.mPreMinute = currentTimeMillis;
        this.mOldSrc = src2;
        this.mOldFormat = format;
    }

    @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        this.mPreTime = null;
        this.mLoadResourceFailed = false;
        getContext().getHandler().removeCallbacks(this.mUpdateTimeRunnable);
        super.finish();
    }

    @Override // com.miui.maml.elements.ImageScreenElement
    protected int getBitmapWidth() {
        return this.mBmpWidth;
    }

    @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        setDateFormat();
        this.mPreTime = null;
        createBitmap();
        updateTime(true);
    }

    @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void pause() {
    }

    @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.ScreenElement
    public void resume() {
        this.mCalendar = Calendar.getInstance();
        this.mLocalizedZero = DecimalFormatSymbols.getInstance().getZeroDigit();
        setDateFormat();
        updateTime(true);
    }
}
