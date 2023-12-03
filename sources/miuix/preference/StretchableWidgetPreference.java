package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.property.ViewProperty;
import miuix.stretchablewidget.StretchableWidget;
import miuix.stretchablewidget.WidgetContainer;

/* loaded from: classes5.dex */
public class StretchableWidgetPreference extends Preference {
    private View mButtonLine;
    private WidgetContainer mContainer;
    private String mDetailMsgResId;
    private TextView mDetailMsgView;
    private int mHeight;
    private boolean mIsExpand;
    private ImageView mStateImage;
    private StretchableWidget.StretchableWidgetStateChangedListener mStretchableWidgetStateChangedListener;
    private TextView mTitle;
    private View mTopLine;
    private RelativeLayout mTopView;

    public StretchableWidgetPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.stretchableWidgetPreferenceStyle);
    }

    public StretchableWidgetPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHeight = 0;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.StretchableWidgetPreference, i, 0);
        this.mDetailMsgResId = obtainStyledAttributes.getString(R$styleable.StretchableWidgetPreference_detail_message);
        this.mIsExpand = obtainStyledAttributes.getBoolean(R$styleable.StretchableWidgetPreference_expand_state, false);
        obtainStyledAttributes.recycle();
    }

    private void setContainerAmin(boolean z) {
        IStateStyle add = Folme.useValue(this.mContainer).setup("start").add("widgetHeight", this.mHeight);
        ViewProperty viewProperty = ViewProperty.ALPHA;
        add.add(viewProperty, 1.0f).setup("end").add("widgetHeight", 0).add(viewProperty, 0.0f);
        Folme.useValue(this.mContainer).setTo(z ? "start" : "end");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stateChangeAnim() {
        boolean z = !this.mIsExpand;
        this.mIsExpand = z;
        if (z) {
            Folme.useValue(this.mContainer).to("start", new AnimConfig().setFromSpeed(0.0f).setSpecial(ViewProperty.ALPHA, (AnimSpecialConfig) new AnimSpecialConfig().setEase(-2, 1.0f, 0.2f)));
            this.mStateImage.setBackgroundResource(miuix.stretchablewidget.R$drawable.miuix_stretchable_widget_state_expand);
            this.mButtonLine.setVisibility(0);
            this.mTopLine.setVisibility(0);
        } else {
            Folme.useValue(this.mContainer).to("end", new AnimConfig().setFromSpeed(0.0f).setSpecial(ViewProperty.ALPHA, (AnimSpecialConfig) new AnimSpecialConfig().setEase(-2, 1.0f, 0.2f)));
            this.mStateImage.setBackgroundResource(miuix.stretchablewidget.R$drawable.miuix_stretchable_widget_state_collapse);
            this.mButtonLine.setVisibility(8);
            this.mTopLine.setVisibility(8);
        }
        StretchableWidget.StretchableWidgetStateChangedListener stretchableWidgetStateChangedListener = this.mStretchableWidgetStateChangedListener;
        if (stretchableWidgetStateChangedListener != null) {
            stretchableWidgetStateChangedListener.stateChanged(this.mIsExpand);
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mTopView = (RelativeLayout) view.findViewById(R$id.top_view);
        WidgetContainer widgetContainer = (WidgetContainer) view.findViewById(16908312);
        this.mContainer = widgetContainer;
        widgetContainer.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        this.mHeight = this.mContainer.getMeasuredHeight();
        this.mTitle = (TextView) view.findViewById(R$id.title);
        this.mDetailMsgView = (TextView) view.findViewById(R$id.detail_msg_text);
        ImageView imageView = (ImageView) view.findViewById(R$id.state_image);
        this.mStateImage = imageView;
        imageView.setBackgroundResource(R$drawable.miuix_stretchable_widget_state_collapse);
        this.mButtonLine = view.findViewById(R$id.button_line);
        this.mTopLine = view.findViewById(R$id.top_line);
        setDetailMsgText(this.mDetailMsgResId);
        setState(this.mIsExpand);
        this.mTopView.setOnClickListener(new View.OnClickListener() { // from class: miuix.preference.StretchableWidgetPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                StretchableWidgetPreference.this.stateChangeAnim();
            }
        });
    }

    public void setDetailMsgText(String str) {
        this.mDetailMsgView.setText(str);
    }

    public void setState(boolean z) {
        if (z) {
            this.mStateImage.setBackgroundResource(R$drawable.miuix_stretchable_widget_state_expand);
            this.mButtonLine.setVisibility(0);
            this.mTopLine.setVisibility(0);
        } else {
            this.mStateImage.setBackgroundResource(R$drawable.miuix_stretchable_widget_state_collapse);
            this.mButtonLine.setVisibility(8);
            this.mTopLine.setVisibility(8);
        }
        setContainerAmin(z);
    }
}
