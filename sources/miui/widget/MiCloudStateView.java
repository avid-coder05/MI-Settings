package miui.widget;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import java.lang.ref.WeakReference;
import miui.accounts.ExtraAccountManager;
import miui.animation.Folme;
import miui.animation.ITouchStyle;
import miui.animation.base.AnimConfig;

/* loaded from: classes4.dex */
public class MiCloudStateView extends LinearLayout {
    private static final int SYNC_OBSERVER_MASK = 13;
    private static final int SYNC_OBSERVER_TYPE_STATUS = 8;
    private Drawable mArrowRight;
    private int mCloudCountNormalTextAppearance;
    private int mCloudStatusDisabledTextAppearance;
    private int mCloudStatusHighlightTextAppearance;
    private int mCloudStatusNormalTextAppearance;
    private Context mContext;
    private UpdateStateTask mCurrentUpdateTask;
    private FrameLayout mCustomView;
    private String mDisabledStatusText;
    private Handler mHandler;
    private int mLastVisible;
    private ILayoutUpdateListener mLayoutUpdateListener;
    private TextView mMiCloudCountText;
    private TextView mMiCloudStatusText;
    private boolean mPendingUpdate;
    private Object mSyncChangeHandle;
    private ISyncInfoProvider mSyncInfoProvider;
    private boolean mSyncing;

    /* loaded from: classes4.dex */
    public interface ILayoutUpdateListener {
        void onLayoutUpdate(boolean z, boolean z2, int[] iArr);
    }

    /* loaded from: classes4.dex */
    public interface ISyncInfoProvider {
        String getAuthority();

        int[] getUnsyncedCount(Context context);

        String getUnsyncedCountText(Context context, int[] iArr);
    }

    /* loaded from: classes4.dex */
    private static class SyncObserver implements SyncStatusObserver {
        WeakReference<MiCloudStateView> mView;

        SyncObserver(MiCloudStateView miCloudStateView) {
            this.mView = new WeakReference<>(miCloudStateView);
        }

        @Override // android.content.SyncStatusObserver
        public void onStatusChanged(int i) {
            MiCloudStateView miCloudStateView = this.mView.get();
            if (miCloudStateView != null) {
                miCloudStateView.updateState(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class UpdateStateTask extends AsyncTask<Void, Void, Void> {
        boolean enabled;
        boolean syncing;
        int[] unsyncedCounts;

        private UpdateStateTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(MiCloudStateView.this.getContext());
            if (xiaomiAccount == null) {
                this.enabled = false;
                this.syncing = false;
            } else {
                String authority = MiCloudStateView.this.mSyncInfoProvider.getAuthority();
                if (TextUtils.isEmpty(authority)) {
                    this.enabled = false;
                    this.syncing = false;
                } else {
                    this.enabled = ContentResolver.getSyncAutomatically(xiaomiAccount, authority);
                    this.syncing = ContentResolver.isSyncActive(xiaomiAccount, authority);
                }
            }
            MiCloudStateView.this.mSyncing = this.syncing;
            if (!this.enabled || this.syncing) {
                return null;
            }
            this.unsyncedCounts = MiCloudStateView.this.mSyncInfoProvider.getUnsyncedCount(MiCloudStateView.this.getContext());
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r4) {
            super.onPostExecute((UpdateStateTask) r4);
            MiCloudStateView.this.mCurrentUpdateTask = null;
            if (MiCloudStateView.this.isAttachedToWindow()) {
                MiCloudStateView.this.updateLayout(this.enabled, this.syncing, this.unsyncedCounts);
                if (MiCloudStateView.this.mPendingUpdate) {
                    MiCloudStateView.this.mPendingUpdate = false;
                    MiCloudStateView.this.updateState(true);
                }
            }
        }
    }

    public MiCloudStateView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MiCloudStateView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSyncing = false;
        initialize(context, attributeSet, i);
    }

    private int getTotalCount(int[] iArr) {
        if (iArr == null || iArr.length <= 0) {
            return 0;
        }
        int i = 0;
        for (int i2 : iArr) {
            i += i2;
        }
        return i;
    }

    private boolean hasCustomView() {
        return this.mCustomView.getVisibility() == 0;
    }

    private void initialize(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MiCloudStateView, R.attr.cloudStateViewStyle, miui.system.R.style.Widget_MiCloudStateView_Light);
        this.mCloudCountNormalTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.MiCloudStateView_cloudCountNormalTextAppearance, 0);
        this.mCloudStatusNormalTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.MiCloudStateView_cloudStatusNormalTextAppearance, 0);
        this.mCloudStatusHighlightTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.MiCloudStateView_cloudStatusHighlightTextAppearance, 0);
        this.mCloudStatusDisabledTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.MiCloudStateView_cloudStatusDisabledTextAppearance, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.MiCloudStateView_cloudStatusBackground);
        this.mArrowRight = obtainStyledAttributes.getDrawable(R.styleable.MiCloudStateView_cloudArrowRight);
        obtainStyledAttributes.recycle();
        this.mDisabledStatusText = getResources().getString(R.string.cloud_state_disabled);
        this.mContext = context;
        this.mHandler = new Handler();
        setBackground(drawable);
        Folme.useAt(new View[]{this}).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(this, new AnimConfig[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLayout(boolean z, boolean z2, int[] iArr) {
        int totalCount = getTotalCount(iArr);
        if (!z) {
            this.mMiCloudStatusText.setVisibility(8);
            this.mMiCloudCountText.setText(this.mDisabledStatusText);
            this.mMiCloudCountText.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.cloud_btn_padding));
            this.mMiCloudCountText.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, this.mArrowRight, (Drawable) null);
        } else if (z2) {
            this.mMiCloudCountText.setCompoundDrawablePadding(0);
            this.mMiCloudCountText.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            if (!hasCustomView()) {
                this.mMiCloudStatusText.setVisibility(0);
                this.mMiCloudStatusText.setText(R.string.cloud_state_syncing);
            }
        } else {
            this.mMiCloudCountText.setCompoundDrawablePadding(0);
            this.mMiCloudCountText.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            if (!hasCustomView()) {
                this.mMiCloudStatusText.setVisibility(0);
                if (totalCount > 0) {
                    this.mMiCloudStatusText.setText(this.mSyncInfoProvider.getUnsyncedCountText(this.mContext, iArr));
                } else {
                    this.mMiCloudStatusText.setText(R.string.cloud_state_finished);
                }
            }
        }
        Context context = getContext();
        if (z2 || totalCount <= 0) {
            this.mMiCloudStatusText.setTextAppearance(context, this.mCloudStatusNormalTextAppearance);
        } else {
            this.mMiCloudStatusText.setTextAppearance(context, this.mCloudStatusHighlightTextAppearance);
        }
        ILayoutUpdateListener iLayoutUpdateListener = this.mLayoutUpdateListener;
        if (iLayoutUpdateListener != null) {
            iLayoutUpdateListener.onLayoutUpdate(z, z2, iArr);
        }
        requestLayout();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mPendingUpdate) {
            this.mPendingUpdate = false;
            updateState(true);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mMiCloudCountText = (TextView) findViewById(R.id.cloud_count);
        this.mMiCloudStatusText = (TextView) findViewById(R.id.cloud_status);
        this.mCustomView = (FrameLayout) findViewById(R.id.custom_view);
        Context context = getContext();
        this.mMiCloudCountText.setTextAppearance(context, this.mCloudCountNormalTextAppearance);
        this.mMiCloudStatusText.setTextAppearance(context, this.mCloudStatusNormalTextAppearance);
        this.mMiCloudCountText.setSelected(true);
        this.mMiCloudStatusText.setSelected(true);
    }

    public void registerObserver() {
        if (this.mSyncChangeHandle == null) {
            this.mSyncChangeHandle = ContentResolver.addStatusChangeListener(13, new SyncObserver(this));
        }
    }

    public void setCustomView(View view) {
        if (view == null) {
            this.mCustomView.setVisibility(8);
            this.mCustomView.removeAllViews();
            this.mMiCloudStatusText.setVisibility(this.mLastVisible);
            return;
        }
        this.mCustomView.setVisibility(0);
        this.mCustomView.removeAllViews();
        this.mCustomView.addView(view);
        this.mLastVisible = this.mMiCloudStatusText.getVisibility();
        this.mMiCloudStatusText.setVisibility(8);
    }

    public void setDisabledStatusText(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mDisabledStatusText = str;
    }

    public void setLayoutUpdateListener(ILayoutUpdateListener iLayoutUpdateListener) {
        this.mLayoutUpdateListener = iLayoutUpdateListener;
    }

    public void setSyncInfoProvider(ISyncInfoProvider iSyncInfoProvider) {
        this.mSyncInfoProvider = iSyncInfoProvider;
    }

    public void setTotalCountText(String str) {
        this.mMiCloudCountText.setText(str);
    }

    public void unregisterObserver() {
        Object obj = this.mSyncChangeHandle;
        if (obj != null) {
            ContentResolver.removeStatusChangeListener(obj);
            this.mSyncChangeHandle = null;
        }
    }

    public void updateState() {
        updateState(false);
    }

    public void updateState(final boolean z) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            this.mHandler.post(new Runnable() { // from class: miui.widget.MiCloudStateView.1
                @Override // java.lang.Runnable
                public void run() {
                    MiCloudStateView.this.updateState(z);
                }
            });
        } else if (!isAttachedToWindow()) {
            if (z) {
                this.mPendingUpdate = true;
            }
        } else if (z || !this.mSyncing) {
            if (this.mSyncInfoProvider == null) {
                throw new IllegalStateException("mSyncInfoProvider can't be null");
            }
            if (this.mCurrentUpdateTask != null) {
                this.mPendingUpdate = true;
                return;
            }
            UpdateStateTask updateStateTask = new UpdateStateTask();
            this.mCurrentUpdateTask = updateStateTask;
            updateStateTask.execute(new Void[0]);
        }
    }
}
