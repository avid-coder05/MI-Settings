package com.android.settings.usagestats.widget.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import com.android.settings.usagestats.utils.AppInfoUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes2.dex */
public class AppUsageViewRender extends BaseViewRender {
    private List<DayAppUsageStats> mAppUsageListData;
    private Paint mExplainPointPaint;
    private Paint mExplainTextPaint;
    private String mExplainWeekDay;
    private String mExplainWeekEnd;
    private Paint mLinePaint;
    private Paint mLineTextPaint;
    private int mWeekDayColor;
    private int mWeekEndColor;
    private float pointDiameter;

    public AppUsageViewRender(Context context) {
        super(context);
    }

    private void drawColorExplain(Canvas canvas) {
    }

    private String getMouthText(DayInfo dayInfo) {
        this.dateFormat.applyPattern(this.mContext.getResources().getString(R.string.usage_state_which_mouth));
        return this.dateFormat.format(Long.valueOf(dayInfo.dayBeginningTime));
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public void calculateOthers() {
        super.calculateOthers();
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawColorExplain(canvas);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void drawExplain(Canvas canvas) {
        this.mExplainPointPaint.setColor(this.mWeekDayColor);
        canvas.drawPoint(isRtl() ? this.mWidth - (this.pointDiameter / 2.0f) : this.pointDiameter / 2.0f, (this.mTipRectHeight / 2.0f) - (this.pointDiameter / 2.0f), this.mExplainPointPaint);
        float textBaseLine = AppInfoUtils.getTextBaseLine(this.mExplainPointPaint, this.mTipRectHeight / 2.0f);
        float dp2px = this.pointDiameter + MiuiUtils.dp2px(this.mContext, 4.73f);
        canvas.drawText(this.mExplainWeekDay, isRtl() ? this.mWidth - dp2px : dp2px, textBaseLine, this.mExplainTextPaint);
        float dp2px2 = dp2px + MiuiUtils.dp2px(this.mContext, 10.0f) + getTextWidth(this.mExplainWeekDay, this.mExplainTextPaint) + (this.pointDiameter / 2.0f);
        this.mExplainPointPaint.setColor(this.mWeekEndColor);
        canvas.drawPoint(isRtl() ? this.mWidth - dp2px2 : dp2px2, (this.mTipRectHeight / 2.0f) - (this.pointDiameter / 2.0f), this.mExplainPointPaint);
        float dp2px3 = dp2px2 + (this.pointDiameter / 2.0f) + MiuiUtils.dp2px(this.mContext, 4.73f);
        canvas.drawText(this.mExplainWeekEnd, isRtl() ? this.mWidth - dp2px3 : dp2px3 + (this.pointDiameter / 2.0f), textBaseLine, this.mExplainTextPaint);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void drawOthers(Canvas canvas, int i, float f) {
        DayInfo dayInfo = this.mAppUsageListData.get(i).getDayInfo();
        if (this.isWeekData || dayInfo.dayInMonth != 1 || i == 0) {
            return;
        }
        int i2 = this.mHeight;
        canvas.drawLine(f, i2, f, i2 - this.mBarMaxHeight, this.mLinePaint);
        canvas.drawText(getMouthText(dayInfo), f, (this.mHeight - this.mBarMaxHeight) - MiuiUtils.dp2px(this.mContext, 3.27f), this.mLineTextPaint);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected int getBarColor(int i) {
        DayInfo dayInfo = this.mAppUsageListData.get(i).getDayInfo();
        if (i == (isRtl() ? 0 : this.mDataSize - 1)) {
            return getColor(R.color.usage_stats_app_usage_bar_today);
        }
        int i2 = dayInfo.dayInWeek;
        return (i2 == 1 || i2 == 7) ? this.mWeekEndColor : this.mWeekDayColor;
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected String getBarCoordText(int i) {
        return i == (isRtl() ? 0 : this.mDataSize + (-1)) ? getString(R.string.usage_state_today) : (this.isWeekData || !(i % 5 == 0 || (isRtl() && i == this.mDataSize + (-1)))) ? this.isWeekData ? getString(BaseViewRender.WEEKS.get(this.mAppUsageListData.get(i).getDayInfo().dayInWeek)) : "" : String.valueOf(this.mAppUsageListData.get(i).getDayInfo().dayInMonth);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getBarTop(int i) {
        DayAppUsageStats dayAppUsageStats = this.mAppUsageListData.get(i);
        if (dayAppUsageStats.getTotalUsageTime() == 0) {
            return this.mHeight + 100;
        }
        float f = this.mHeight;
        float f2 = this.mBarMaxHeight;
        return (f - f2) + (f2 * (1.0f - (((float) dayAppUsageStats.getTotalUsageTime()) / this.mMaxYValue)));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public Paint.Align getCoordTextAlign(int i) {
        return i == this.mDataSize + (-1) ? Paint.Align.RIGHT : super.getCoordTextAlign(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public int getCoordTextColor(int i) {
        return i == (isRtl() ? 0 : this.mDataSize + (-1)) ? getColor(R.color.usage_stats_app_usage_bar_today) : super.getCoordTextColor(i);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected int getDataSize() {
        List<DayAppUsageStats> list = this.mAppUsageListData;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected long getMaxYValue() {
        long j = 0;
        for (DayAppUsageStats dayAppUsageStats : this.mAppUsageListData) {
            if (j < dayAppUsageStats.getTotalUsageTime()) {
                j = dayAppUsageStats.getTotalUsageTime();
            }
        }
        return j;
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getMyBarMaxHeight() {
        return getDimenValue(R.dimen.usage_state_app_usage_max_bar_height);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getTipRectWidth() {
        return getDimenValue(R.dimen.usage_state_show_tip_width);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void getTipTitle(int i) {
        DayInfo dayInfo = this.mAppUsageListData.get(i).getDayInfo();
        this.dateFormat.applyPattern(getString(R.string.usage_state_date));
        this.mTipTile = getString(R.string.usage_state_mourth_day, this.dateFormat.format(Long.valueOf(dayInfo.dayBeginningTime)));
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void getTipValue(int i) {
        this.mTipValue = AppInfoUtils.formatTime(this.mContext, this.mAppUsageListData.get(i).getTotalUsageTime());
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public void init() {
        super.init();
        Paint paint = new Paint();
        this.mExplainPointPaint = paint;
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mExplainTextPaint = paint2;
        paint2.setTextSize(getDimenValue(R.dimen.usage_state_app_usage_explain_text_size));
        this.mExplainTextPaint.setColor(getColor(R.color.usage_stats_black60));
        this.mExplainTextPaint.setTextAlign(isRtl() ? Paint.Align.RIGHT : Paint.Align.LEFT);
        this.mExplainTextPaint.setAntiAlias(true);
        this.mLinePaint = new Paint(1);
        float dp2px = MiuiUtils.dp2px(this.mContext, 0.36f);
        this.mLinePaint.setPathEffect(new DashPathEffect(new float[]{dp2px, dp2px}, 0.0f));
        this.mLinePaint.setColor(getColor(R.color.usage_stats_dash_line_color));
        this.mLinePaint.setStrokeWidth(dp2px);
        Paint paint3 = new Paint(1);
        this.mLineTextPaint = paint3;
        paint3.setTextAlign(Paint.Align.CENTER);
        this.mLineTextPaint.setTextSize(getDimenValue(R.dimen.usage_state_line_text_size));
        this.mLineTextPaint.setColor(getColor(R.color.usage_stats_dash_line_text_color));
        float dp2px2 = MiuiUtils.dp2px(this.mContext, 5.09f);
        this.pointDiameter = dp2px2;
        this.mExplainPointPaint.setStrokeWidth(dp2px2);
        this.mExplainPointPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mExplainWeekDay = getString(R.string.usage_state_work_day);
        this.mExplainWeekEnd = getString(R.string.usage_state_rest_day);
        this.mWeekDayColor = getColor(R.color.usage_stats_app_usage_bar_normal_day);
        this.mWeekEndColor = getColor(R.color.usage_stats_app_usage_bar_week_day);
    }

    public void setAppUsageList(List<DayAppUsageStats> list) {
        if (this.mAppUsageListData == null) {
            this.mAppUsageListData = new ArrayList();
        }
        this.mAppUsageListData.clear();
        this.mAppUsageListData.addAll(list);
        if (isRtl()) {
            Collections.reverse(this.mAppUsageListData);
        }
    }
}
