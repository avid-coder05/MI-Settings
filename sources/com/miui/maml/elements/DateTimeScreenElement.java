package com.miui.maml.elements;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.util.TextFormatter;
import java.util.TimeZone;
import miui.date.Calendar;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class DateTimeScreenElement extends TextScreenElement {
    private DateFormatter mDateFormatter;
    private DateFormatter mDescriptionDateFormatter;

    /* loaded from: classes2.dex */
    class DateFormatter {
        private Calendar mCalendar = new Calendar();
        private int mCurDay = -1;
        private String mLunarDate;
        private String mOldFormat;
        private long mPreValue;
        private String mText;
        private TextFormatter mTextFormatter;
        private Expression mTimeZoneExp;
        private Expression mValueExp;

        public DateFormatter(TextFormatter textFormatter, Expression expression, Expression expression2) {
            this.mTextFormatter = textFormatter;
            this.mValueExp = expression;
            this.mTimeZoneExp = expression2;
        }

        public String getText() {
            String format;
            TextFormatter textFormatter = this.mTextFormatter;
            if (textFormatter == null || (format = textFormatter.getFormat()) == null) {
                return "";
            }
            Expression expression = this.mValueExp;
            long evaluate = expression != null ? (long) DateTimeScreenElement.this.evaluate(expression) : System.currentTimeMillis();
            if (!TextUtils.equals(this.mOldFormat, format) || Math.abs(evaluate - this.mPreValue) >= 200) {
                this.mOldFormat = format;
                this.mCalendar.setTimeInMillis(evaluate);
                Expression expression2 = this.mTimeZoneExp;
                if (expression2 != null) {
                    String evaluateStr = expression2.evaluateStr();
                    if (!TextUtils.isEmpty(evaluateStr)) {
                        this.mCalendar.setTimeZone(TimeZone.getTimeZone(evaluateStr));
                    }
                }
                if (format.contains("NNNN")) {
                    if (this.mCalendar.get(9) != this.mCurDay) {
                        this.mLunarDate = this.mCalendar.format("N月e");
                        String format2 = this.mCalendar.format("t");
                        if (format2 != null) {
                            this.mLunarDate += " " + format2;
                        }
                        this.mCurDay = this.mCalendar.get(9);
                        Log.i("DateTimeScreenElement", "get lunar date:" + this.mLunarDate);
                    }
                    format = format.replace("NNNN", this.mLunarDate);
                }
                String format3 = this.mCalendar.format(format);
                this.mText = format3;
                this.mPreValue = evaluate;
                return format3;
            }
            return this.mText;
        }

        public void resetCalendar() {
            this.mCalendar = new Calendar();
        }
    }

    public DateTimeScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        Expression build = Expression.build(getVariables(), element.getAttribute("value"));
        Expression build2 = Expression.build(getVariables(), element.getAttribute("timeZoneId"));
        this.mDateFormatter = new DateFormatter(this.mFormatter, build, build2);
        if (!TextUtils.isEmpty(element.getAttribute("contentDescriptionFormat"))) {
            this.mHasContentDescription = true;
            this.mDescriptionDateFormatter = new DateFormatter(TextFormatter.fromElement(getVariables(), element, null, "contentDescriptionFormat", null, null, null), build, build2);
        } else if (!TextUtils.isEmpty(element.getAttribute("contentDescriptionFormatExp"))) {
            this.mHasContentDescription = true;
            this.mDescriptionDateFormatter = new DateFormatter(TextFormatter.fromElement(getVariables(), element, null, null, null, null, "contentDescriptionFormatExp"), build, build2);
        }
        if (this.mHasContentDescription) {
            this.mRoot.addAccessibleElements(this);
        }
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public String getContentDescription() {
        DateFormatter dateFormatter = this.mDescriptionDateFormatter;
        return dateFormatter != null ? dateFormatter.getText() : super.getContentDescription();
    }

    @Override // com.miui.maml.elements.TextScreenElement
    protected String getText() {
        return this.mDateFormatter.getText();
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void resume() {
        super.resume();
        this.mDateFormatter.resetCalendar();
        DateFormatter dateFormatter = this.mDescriptionDateFormatter;
        if (dateFormatter != null) {
            dateFormatter.resetCalendar();
        }
    }
}
