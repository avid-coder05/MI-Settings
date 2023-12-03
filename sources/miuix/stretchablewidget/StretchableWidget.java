package miuix.stretchablewidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.property.ViewProperty;

/* loaded from: classes5.dex */
public class StretchableWidget extends LinearLayout {
    private View mButtonLine;
    private WidgetContainer mContainer;
    private Context mContext;
    private String mDetailMsgResId;
    protected TextView mDetailMsgText;
    protected int mHeight;
    private ImageView mIcon;
    private int mIconResId;
    private boolean mIsExpand;
    private View mLayout;
    private int mLayoutResId;
    private ImageView mStateImage;
    private StretchableWidgetStateChangedListener mStretchableWidgetStateChangedListener;
    private TextView mTitle;
    private String mTitleResId;
    private View mTopLine;
    private RelativeLayout mTopView;

    /* loaded from: classes5.dex */
    public interface StretchableWidgetStateChangedListener {
        void stateChanged(boolean z);
    }

    public StretchableWidget(Context context) {
        this(context, null);
    }

    public StretchableWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.stretchableWidgetStyle);
    }

    public StretchableWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHeight = 0;
        setOrientation(1);
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.StretchableWidget, i, 0);
        this.mTitleResId = obtainStyledAttributes.getString(R$styleable.StretchableWidget_title);
        this.mIconResId = obtainStyledAttributes.getResourceId(R$styleable.StretchableWidget_icon, 0);
        this.mLayoutResId = obtainStyledAttributes.getResourceId(R$styleable.StretchableWidget_layout, 0);
        this.mDetailMsgResId = obtainStyledAttributes.getString(R$styleable.StretchableWidget_detail_message);
        this.mIsExpand = obtainStyledAttributes.getBoolean(R$styleable.StretchableWidget_expand_state, false);
        init(context, attributeSet, i);
        obtainStyledAttributes.recycle();
    }

    private View inflaterView(int i) {
        if (i == 0) {
            return null;
        }
        return ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(i, (ViewGroup) null);
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.miuix_stretchable_widget_layout, (ViewGroup) this, true);
        this.mTopView = (RelativeLayout) inflate.findViewById(R$id.top_view);
        this.mIcon = (ImageView) inflate.findViewById(R$id.icon);
        this.mTitle = (TextView) inflate.findViewById(R$id.start_text);
        this.mStateImage = (ImageView) inflate.findViewById(R$id.state_image);
        this.mDetailMsgText = (TextView) inflate.findViewById(R$id.detail_msg_text);
        this.mContainer = (WidgetContainer) inflate.findViewById(R$id.customize_container);
        this.mButtonLine = inflate.findViewById(R$id.button_line);
        this.mTopLine = inflate.findViewById(R$id.top_line);
        setTitle(this.mTitleResId);
        preSetView(this.mContext, attributeSet, i);
        setLayout(this.mLayoutResId);
        setIcon(this.mIconResId);
        setDetailMessage(this.mDetailMsgResId);
        setState(this.mIsExpand);
        this.mTopView.setOnClickListener(new View.OnClickListener() { // from class: miuix.stretchablewidget.StretchableWidget.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                StretchableWidget.this.stateChangeAnim();
            }
        });
    }

    private void setContainerAmin(boolean z) {
        IStateStyle add = Folme.useValue(this.mContainer).setup("start").add("widgetHeight", this.mHeight);
        ViewProperty viewProperty = ViewProperty.ALPHA;
        add.add(viewProperty, 1.0f).setup("end").add("widgetHeight", 0).add(viewProperty, 0.0f);
        Folme.useValue(this.mContainer).setTo(z ? "start" : "end");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stateChangeAnim() {
        this.mIsExpand = !this.mIsExpand;
        AnimSpecialConfig animSpecialConfig = (AnimSpecialConfig) new AnimSpecialConfig().setEase(-2, 1.0f, 0.2f);
        if (this.mIsExpand) {
            Folme.useValue(this.mContainer).to("start", new AnimConfig().setFromSpeed(0.0f).setSpecial(ViewProperty.ALPHA, animSpecialConfig));
            this.mStateImage.setBackgroundResource(R$drawable.miuix_stretchable_widget_state_expand);
            this.mTopLine.setVisibility(0);
            this.mButtonLine.setVisibility(0);
        } else {
            Folme.useValue(this.mContainer).to("end", new AnimConfig().setFromSpeed(0.0f).setSpecial(ViewProperty.ALPHA, animSpecialConfig));
            this.mStateImage.setBackgroundResource(R$drawable.miuix_stretchable_widget_state_collapse);
            this.mTopLine.setVisibility(8);
            this.mButtonLine.setVisibility(8);
        }
        StretchableWidgetStateChangedListener stretchableWidgetStateChangedListener = this.mStretchableWidgetStateChangedListener;
        if (stretchableWidgetStateChangedListener != null) {
            stretchableWidgetStateChangedListener.stateChanged(this.mIsExpand);
        }
    }

    protected void afterSetView() {
    }

    public View getLayout() {
        return this.mLayout;
    }

    protected void preSetView(Context context, AttributeSet attributeSet, int i) {
    }

    public void setDetailMessage(CharSequence charSequence) {
        if (charSequence != null) {
            this.mDetailMsgText.setText(charSequence);
        }
    }

    public void setIcon(int i) {
        if (i == 0) {
            return;
        }
        this.mIcon.setBackgroundResource(i);
    }

    public View setLayout(int i) {
        if (i == 0) {
            return null;
        }
        View inflaterView = inflaterView(i);
        setView(inflaterView);
        return inflaterView;
    }

    public void setLayout(View view) {
        setView(view);
    }

    public void setState(boolean z) {
        if (z) {
            this.mStateImage.setBackgroundResource(R$drawable.miuix_stretchable_widget_state_expand);
            this.mTopLine.setVisibility(0);
            this.mButtonLine.setVisibility(0);
        } else {
            this.mStateImage.setBackgroundResource(R$drawable.miuix_stretchable_widget_state_collapse);
            this.mTopLine.setVisibility(8);
            this.mButtonLine.setVisibility(8);
        }
        setContainerAmin(z);
    }

    public void setStateChangedListener(StretchableWidgetStateChangedListener stretchableWidgetStateChangedListener) {
        this.mStretchableWidgetStateChangedListener = stretchableWidgetStateChangedListener;
    }

    public void setTitle(CharSequence charSequence) {
        if (charSequence != null) {
            this.mTitle.setText(charSequence);
        }
    }

    @SuppressLint({"WrongConstant"})
    public void setView(View view) {
        if (view == null) {
            return;
        }
        this.mLayout = view;
        if (view instanceof TextProvider) {
            ((TextProvider) view).setListener(new SyncDetailMessageListener() { // from class: miuix.stretchablewidget.StretchableWidget.2
            });
        }
        if (this.mContainer.getChildCount() == 0) {
            this.mContainer.addView(view);
        } else {
            this.mContainer.removeAllViews();
            this.mContainer.addView(view);
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        this.mHeight = view.getMeasuredHeight();
        afterSetView();
        setContainerAmin(this.mIsExpand);
    }
}
