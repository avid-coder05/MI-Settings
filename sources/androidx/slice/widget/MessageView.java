package androidx.slice.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceQuery;
import androidx.slice.widget.SliceView;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class MessageView extends SliceChildView {
    private TextView mDetails;
    private ImageView mIcon;

    public MessageView(Context context) {
        super(context);
    }

    @Override // androidx.slice.widget.SliceChildView
    public int getMode() {
        return 2;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDetails = (TextView) findViewById(16908304);
        this.mIcon = (ImageView) findViewById(16908294);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void resetView() {
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setSliceItem(SliceContent content, boolean isHeader, int index, int rowCount, SliceView.OnSliceActionListener observer) {
        Drawable loadDrawable;
        SliceItem sliceItem = content.getSliceItem();
        setSliceActionListener(observer);
        SliceItem findSubtype = SliceQuery.findSubtype(sliceItem, YellowPageContract.ImageLookup.DIRECTORY_IMAGE, "source");
        if (findSubtype != null && findSubtype.getIcon() != null && (loadDrawable = findSubtype.getIcon().loadDrawable(getContext())) != null) {
            int applyDimension = (int) TypedValue.applyDimension(1, 24.0f, getContext().getResources().getDisplayMetrics());
            Bitmap createBitmap = Bitmap.createBitmap(applyDimension, applyDimension, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            loadDrawable.setBounds(0, 0, applyDimension, applyDimension);
            loadDrawable.draw(canvas);
            this.mIcon.setImageBitmap(SliceViewUtil.getCircularBitmap(createBitmap));
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (SliceItem sliceItem2 : SliceQuery.findAll(sliceItem, "text")) {
            if (spannableStringBuilder.length() != 0) {
                spannableStringBuilder.append('\n');
            }
            spannableStringBuilder.append(sliceItem2.getSanitizedText());
        }
        this.mDetails.setText(spannableStringBuilder.toString());
    }
}
