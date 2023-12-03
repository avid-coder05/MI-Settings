package com.android.settings.usagestats.widget.render;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import com.android.settings.R;
import com.android.settings.usagestats.model.DayDeviceUsageStats;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public abstract class DeviceUsageViewRender extends BaseViewRender implements ISetDeviceDataInterface {
    protected List<Integer> mOneDayList;
    private TextPaint mTextPaint;
    private int mTodayCount;
    private int mWeekCount;
    protected List<DayDeviceUsageStats> mWeekDeviceList;

    public DeviceUsageViewRender(Context context) {
        super(context);
    }

    private void getWeekSum() {
        if (this.mWeekDeviceList.isEmpty()) {
            return;
        }
        this.mWeekCount = 0;
        Iterator<DayDeviceUsageStats> it = this.mWeekDeviceList.iterator();
        while (it.hasNext()) {
            this.mWeekCount += getDeviceTypeCount(it.next());
        }
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void drawExplain(Canvas canvas) {
        String quantityString;
        if (this.isWeekData) {
            Resources resources = this.mContext.getResources();
            int weekExplainId = getWeekExplainId();
            int i = this.mWeekCount;
            quantityString = resources.getQuantityString(weekExplainId, i, Integer.valueOf(i));
        } else {
            Resources resources2 = this.mContext.getResources();
            int todayExplainId = getTodayExplainId();
            int i2 = this.mTodayCount;
            quantityString = resources2.getQuantityString(todayExplainId, i2, Integer.valueOf(i2));
        }
        setTalkBackDescription(quantityString);
        canvas.save();
        if (isRtl()) {
            canvas.translate(canvas.getWidth() / 4, 0.0f);
        }
        StaticLayout.Builder.obtain(quantityString, 0, quantityString.length(), this.mTextPaint, (canvas.getWidth() * 3) / 4).setAlignment(isRtl() ? Layout.Alignment.ALIGN_RIGHT : Layout.Alignment.ALIGN_LEFT).setIncludePad(false).setLineSpacing(0.0f, 1.0f).setMaxLines(2).setEllipsize(TextUtils.TruncateAt.END).build().draw(canvas);
        canvas.restore();
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected String getBarCoordText(int i) {
        if (this.isWeekData) {
            return i == (isRtl() ? 0 : this.mDataSize + (-1)) ? getString(R.string.usage_state_today) : getString(BaseViewRender.WEEKS.get(this.mWeekDeviceList.get(i).getDayInfo().dayInWeek));
        }
        if (i != (isRtl() ? 0 : this.mDataSize - 1)) {
            return (i % 4 == 0 || i == this.mDataSize - 1) ? isRtl() ? String.valueOf(this.mDataSize - i) : String.valueOf(i + 1) : "";
        }
        int i2 = isRtl() ? this.mDataSize - i : i + 1;
        return this.mContext.getResources().getQuantityString(R.plurals.usage_state_hour24, i2, Integer.valueOf(i2));
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getBarTop(int i) {
        int deviceTypeCount = this.isWeekData ? getDeviceTypeCount(this.mWeekDeviceList.get(i)) : this.mOneDayList.get(i).intValue();
        if (deviceTypeCount == 0) {
            return this.mHeight + 100;
        }
        float f = this.mHeight;
        float f2 = this.mBarMaxHeight;
        return (f - f2) + (f2 * (1.0f - (deviceTypeCount / this.mMaxYValue)));
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
        return (this.isWeekData ? this.mWeekDeviceList : this.mOneDayList).size();
    }

    protected abstract int getDeviceTypeCount(DayDeviceUsageStats dayDeviceUsageStats);

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected long getMaxYValue() {
        int i = 0;
        if (this.isWeekData) {
            for (DayDeviceUsageStats dayDeviceUsageStats : this.mWeekDeviceList) {
                if (getDeviceTypeCount(dayDeviceUsageStats) > i) {
                    i = getDeviceTypeCount(dayDeviceUsageStats);
                }
            }
        } else {
            for (Integer num : this.mOneDayList) {
                if (i < num.intValue()) {
                    i = num.intValue();
                }
            }
        }
        return i;
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getMyBarMaxHeight() {
        return getDimenValue(R.dimen.usage_state_device_rect_bar_height);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected float getTipRectWidth() {
        return getDimenValue(R.dimen.usage_state_show_tip_width2);
    }

    protected abstract int getTodayExplainId();

    protected abstract int getWeekExplainId();

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    public void init() {
        super.init();
        TextPaint textPaint = new TextPaint();
        this.mTextPaint = textPaint;
        textPaint.setColor(getColor(R.color.usage_stats_black90));
        this.mTextPaint.setTextSize(getDimenValue(R.dimen.usage_state_device_usage_explain_text_size));
        this.mTextPaint.setStyle(Paint.Style.FILL);
        this.mTextPaint.setAntiAlias(true);
    }

    @Override // com.android.settings.usagestats.widget.render.ISetDeviceDataInterface
    public void setDeviceDataList(List<DayDeviceUsageStats> list) {
        if (this.mWeekDeviceList == null) {
            this.mWeekDeviceList = new ArrayList();
        }
        this.mWeekDeviceList.clear();
        this.mWeekDeviceList.addAll(list);
        if (isRtl()) {
            Collections.reverse(this.mWeekDeviceList);
        }
        if (this.mWeekDeviceList.isEmpty()) {
            return;
        }
        getWeekSum();
    }

    @Override // com.android.settings.usagestats.widget.render.ISetDeviceDataInterface
    public void setOneDayList(List<Integer> list) {
        if (this.mOneDayList == null) {
            this.mOneDayList = new ArrayList();
        }
        this.mOneDayList.clear();
        this.mOneDayList.addAll(list);
        if (isRtl()) {
            Collections.reverse(this.mOneDayList);
        }
        if (this.mOneDayList.isEmpty()) {
            return;
        }
        this.mTodayCount = 0;
        Iterator<Integer> it = this.mOneDayList.iterator();
        while (it.hasNext()) {
            this.mTodayCount += it.next().intValue();
        }
    }
}
