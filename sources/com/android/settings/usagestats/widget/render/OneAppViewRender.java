package com.android.settings.usagestats.widget.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.AppValueData;
import com.android.settings.usagestats.utils.AppInfoUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class OneAppViewRender extends BaseViewRender implements IRenderOneAppInterface {
    private float mExplainBaseLine;
    private Paint mExplainPaint;
    private float mExplainTextSize;
    private List<AppUsageStats> mOneDayList;
    private String mTodayUsageTime;
    private ArrayList<AppValueData> mWeekValueList;

    public OneAppViewRender(Context context) {
        super(context);
        this.mTodayUsageTime = "";
    }

    private boolean listNotEmpty(List list) {
        return (list == null || list.isEmpty()) ? false : true;
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void drawExplain(Canvas canvas) {
        setTalkBackDescription(this.mTodayUsageTime);
        this.mExplainPaint.setTextAlign(isRtl() ? Paint.Align.RIGHT : Paint.Align.LEFT);
        canvas.drawText(this.mTodayUsageTime, isRtl() ? this.mWidth : 0.0f, this.mExplainBaseLine, this.mExplainPaint);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected int getBarColor(int i) {
        if (this.isWeekData) {
            if (i == (isRtl() ? 0 : this.mDataSize - 1)) {
                return getColor(R.color.usage_stats_app_usage_bar_today);
            }
        }
        return getColor(R.color.usage_stats_app_usage_bar_normal_day);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected String getBarCoordText(int i) {
        if (this.isWeekData) {
            return i == (isRtl() ? 0 : this.mDataSize + (-1)) ? getString(R.string.usage_state_today) : getString(BaseViewRender.WEEKS.get(this.mWeekValueList.get(i).getDayInfo().dayInWeek));
        }
        if (i != (isRtl() ? 0 : this.mDataSize - 1)) {
            return (i % 4 == 0 || i == this.mDataSize - 1) ? isRtl() ? String.valueOf(this.mDataSize - i) : String.valueOf(i + 1) : "";
        }
        int i2 = isRtl() ? this.mDataSize - i : i + 1;
        return this.mContext.getResources().getQuantityString(R.plurals.usage_state_hour24, i2, Integer.valueOf(i2));
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getBarTop(int i) {
        float value = (float) (this.isWeekData ? this.mWeekValueList.get(i).getValue() : this.mOneDayList.get(i).getTotalForegroundTime());
        if (value == 0.0f) {
            return this.mHeight + 100;
        }
        int i2 = this.mHeight;
        float f = this.mBarMaxHeight;
        float f2 = (i2 - f) + (f * (1.0f - (value / this.mMaxYValue)));
        return ((float) i2) - f2 > 3.0f ? f2 : (float) (i2 - 3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public Paint.Align getCoordTextAlign(int i) {
        return i == this.mDataSize + (-1) ? Paint.Align.RIGHT : super.getCoordTextAlign(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public int getCoordTextColor(int i) {
        if (this.isWeekData) {
            if (i == (isRtl() ? 0 : this.mDataSize - 1)) {
                return getColor(R.color.usage_stats_app_usage_bar_today);
            }
        }
        return super.getCoordTextColor(i);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected int getDataSize() {
        return this.isWeekData ? this.mWeekValueList.size() : this.mOneDayList.size();
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected long getMaxYValue() {
        long j = 0;
        if (this.isWeekData) {
            Iterator<AppValueData> it = this.mWeekValueList.iterator();
            while (it.hasNext()) {
                AppValueData next = it.next();
                if (j < next.getValue()) {
                    j = next.getValue();
                }
            }
        } else {
            for (AppUsageStats appUsageStats : this.mOneDayList) {
                if (j < appUsageStats.getTotalForegroundTime()) {
                    j = appUsageStats.getTotalForegroundTime();
                }
            }
        }
        return j;
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getMyBarMaxHeight() {
        return getDimenValue(R.dimen.usage_stats_detail_bar_max_height);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getTipRectWidth() {
        return getDimenValue(R.dimen.usage_state_show_tip_width2);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void getTipTitle(int i) {
        if (this.isWeekData) {
            this.mTipTile = getString(R.string.usage_state_mourth_day, this.dateFormat.format(Long.valueOf(this.mWeekValueList.get(i).getDayInfo().dayBeginningTime)));
            return;
        }
        Context context = this.mContext;
        int i2 = R.string.usage_state_app_usage_tip_title;
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(isRtl() ? (this.mDataSize - i) - 1 : i);
        objArr[1] = Integer.valueOf(isRtl() ? this.mDataSize - i : i + 1);
        this.mTipTile = context.getString(i2, objArr);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void getTipValue(int i) {
        this.mTipValue = AppInfoUtils.formatTime(this.mContext, this.isWeekData ? this.mWeekValueList.get(i).getValue() : this.mOneDayList.get(i).getTotalForegroundTime());
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public void init() {
        super.init();
        this.mExplainTextSize = getDimenValue(R.dimen.usage_state_text_size70);
        Paint paint = new Paint(1);
        this.mExplainPaint = paint;
        paint.setColor(getColor(R.color.usage_state_black));
        this.mExplainPaint.setTextSize(this.mExplainTextSize);
        this.mExplainPaint.setTextAlign(Paint.Align.LEFT);
        this.mExplainBaseLine = AppInfoUtils.getTextBaseLine(this.mExplainPaint, MiuiUtils.dp2px(this.mContext.getApplicationContext(), 16.91f));
    }

    @Override // com.android.settings.usagestats.widget.render.IRenderOneAppInterface
    public void setOneDayList(List<AppUsageStats> list) {
        if (this.mOneDayList == null) {
            this.mOneDayList = new ArrayList();
        }
        this.mOneDayList.clear();
        this.mOneDayList.addAll(list);
        if (listNotEmpty(this.mOneDayList)) {
            if (isRtl()) {
                Collections.reverse(this.mOneDayList);
            }
            long j = 0;
            Iterator<AppUsageStats> it = this.mOneDayList.iterator();
            while (it.hasNext()) {
                j += it.next().getTotalForegroundTime();
            }
            this.mTodayUsageTime = AppInfoUtils.formatTime(this.mContext, j);
        }
    }

    @Override // com.android.settings.usagestats.widget.render.IRenderOneAppInterface
    public void setWeekList(ArrayList<AppValueData> arrayList) {
        if (this.mWeekValueList == null) {
            this.mWeekValueList = new ArrayList<>();
        }
        this.mWeekValueList.clear();
        this.mWeekValueList.addAll(arrayList);
        if (listNotEmpty(this.mWeekValueList)) {
            if (isRtl()) {
                Collections.reverse(this.mWeekValueList);
            }
            long j = 0;
            Iterator<AppValueData> it = this.mWeekValueList.iterator();
            while (it.hasNext()) {
                j += it.next().getValue();
            }
            this.mTodayUsageTime = AppInfoUtils.formatTime(this.mContext, j);
        }
    }
}
