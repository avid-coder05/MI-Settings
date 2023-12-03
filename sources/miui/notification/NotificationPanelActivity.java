package miui.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.miui.system.internal.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes3.dex */
public abstract class NotificationPanelActivity extends Activity {
    private static final int STATUS_BAR_TRANSIENT = 67108864;
    public static final String TAG = "NotificationPanelActivity";
    TextView mAppInfo;
    String mAppTitle;
    ImageView mClearButton;
    boolean mClosing;
    List<NotificationItem> mData;
    protected Handler mHandler;
    protected LayoutInflater mInflater;
    TextView mNoNotificationTips;
    int mNotificationHeight;
    NotificationRowLayout mNotificationList;
    ScrollView mScrollView;
    private View.OnClickListener mClearButtonListener = new View.OnClickListener() { // from class: miui.notification.NotificationPanelActivity.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            NotificationPanelActivity notificationPanelActivity = NotificationPanelActivity.this;
            notificationPanelActivity.clearAllNotification(notificationPanelActivity.mScrollView, notificationPanelActivity.mNotificationList);
        }
    };
    Runnable mOpenAnimation = new Runnable() { // from class: miui.notification.NotificationPanelActivity.3
        @Override // java.lang.Runnable
        public void run() {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(NotificationPanelActivity.this.getResources().getInteger(17694722));
            animatorSet.playTogether(ObjectAnimator.ofFloat(NotificationPanelActivity.this.mScrollView, "scaleY", 0.0f, 1.0f), ObjectAnimator.ofFloat(NotificationPanelActivity.this.mAppInfo, "translationY", (r2.mNotificationHeight * r2.mData.size()) / 2, 0.0f), ObjectAnimator.ofFloat(this, "alpha", 0.0f, 1.0f));
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: miui.notification.NotificationPanelActivity.3.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    NotificationPanelActivity notificationPanelActivity = NotificationPanelActivity.this;
                    notificationPanelActivity.startClearButtonAnimation(notificationPanelActivity.mNotificationList.getChildCount() > 0);
                }
            });
            animatorSet.start();
        }
    };
    Runnable mPostCollapseCleanup = null;

    /* loaded from: classes3.dex */
    class LoadDataTask extends AsyncTask<Void, Void, Void> {
        LoadDataTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            NotificationPanelActivity notificationPanelActivity = NotificationPanelActivity.this;
            notificationPanelActivity.mData = notificationPanelActivity.getData();
            NotificationPanelActivity notificationPanelActivity2 = NotificationPanelActivity.this;
            notificationPanelActivity2.mAppTitle = notificationPanelActivity2.getAppTitle();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r4) {
            NotificationPanelActivity notificationPanelActivity = NotificationPanelActivity.this;
            notificationPanelActivity.mAppInfo.setText(notificationPanelActivity.mAppTitle);
            List<NotificationItem> list = NotificationPanelActivity.this.mData;
            if (list == null || list.size() <= 0) {
                NotificationPanelActivity.this.mNoNotificationTips.setVisibility(0);
                NotificationPanelActivity.this.mNotificationList.setVisibility(8);
            } else {
                for (NotificationItem notificationItem : NotificationPanelActivity.this.mData) {
                    NotificationPanelActivity notificationPanelActivity2 = NotificationPanelActivity.this;
                    notificationPanelActivity2.mNotificationList.addView(notificationPanelActivity2.inflateNotificationView(notificationItem));
                }
            }
            NotificationPanelActivity notificationPanelActivity3 = NotificationPanelActivity.this;
            notificationPanelActivity3.mHandler.post(notificationPanelActivity3.mOpenAnimation);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class NotificationActionClicker implements View.OnClickListener {
        private PendingIntent mIntent;

        public NotificationActionClicker(PendingIntent pendingIntent) {
            this.mIntent = pendingIntent;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.mIntent != null) {
                try {
                    Log.d(NotificationPanelActivity.TAG, "NotificationClicker ActionClick ");
                    this.mIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    Log.w(NotificationPanelActivity.TAG, "Sending contentIntent failed: " + e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class NotificationClicker implements View.OnClickListener {
        private PendingIntent mIntent;

        public NotificationClicker(PendingIntent pendingIntent) {
            this.mIntent = pendingIntent;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.mIntent != null) {
                try {
                    Log.d(NotificationPanelActivity.TAG, "NotificationClicker onClick ");
                    this.mIntent.send();
                    NotificationPanelActivity.this.removeNotificationView(view);
                } catch (PendingIntent.CanceledException e) {
                    Log.w(NotificationPanelActivity.TAG, "Sending contentIntent failed: " + e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeAnimation() {
        if (this.mClosing) {
            return;
        }
        this.mClosing = true;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(getResources().getInteger(17694720));
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.mScrollView, "scaleY", 1.0f, 0.0f), ObjectAnimator.ofFloat(this.mAppInfo, "translationY", this.mScrollView.getHeight() / 2), ObjectAnimator.ofFloat(this.mClearButton, "alpha", 0.0f));
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: miui.notification.NotificationPanelActivity.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                NotificationPanelActivity.this.startClearButtonAnimation(false);
                NotificationPanelActivity.this.mAppInfo.setTranslationY(0.0f);
                NotificationPanelActivity.this.mAppInfo.setText((CharSequence) null);
                NotificationPanelActivity.this.mNotificationList.removeAllViews();
                Runnable runnable = NotificationPanelActivity.this.mPostCollapseCleanup;
                if (runnable != null) {
                    runnable.run();
                    NotificationPanelActivity.this.mPostCollapseCleanup = null;
                }
                NotificationPanelActivity notificationPanelActivity = NotificationPanelActivity.this;
                notificationPanelActivity.mClosing = false;
                notificationPanelActivity.finish();
            }
        });
        animatorSet.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View inflateNotificationView(NotificationItem notificationItem) {
        View inflate = this.mInflater.inflate(R.layout.status_bar_notification, (ViewGroup) null);
        setRowValue(inflate, notificationItem);
        return inflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeNotificationView(View view) {
        this.mNotificationList.removeView(view);
        if (this.mNotificationList.getChildCount() == 0) {
            closeAnimation();
        }
    }

    private void setRowValue(View view, NotificationItem notificationItem) {
        ImageView imageView = (ImageView) view.findViewById(16908294);
        TextView textView = (TextView) view.findViewById(R.id.title);
        TextView textView2 = (TextView) view.findViewById(R.id.content);
        TextView textView3 = (TextView) view.findViewById(R.id.action);
        if (notificationItem.getIcon() == null) {
            imageView.setVisibility(8);
        } else {
            imageView.setImageDrawable(notificationItem.getIcon());
        }
        textView.setText(notificationItem.getTitle());
        textView2.setText(notificationItem.getContent());
        if (notificationItem.getAction() == null && notificationItem.getActionIcon() == null) {
            textView3.setVisibility(8);
        } else {
            textView3.setOnClickListener(new NotificationActionClicker(notificationItem.getClickActionIntent()));
            textView3.setText(notificationItem.getAction());
            if (notificationItem.getActionIcon() != null) {
                textView3.setBackground(notificationItem.getActionIcon());
            }
        }
        updateNotificationVetoButton(view, notificationItem.getClearIntent());
        view.setTag(notificationItem);
        view.setId(notificationItem.getId());
        view.setOnClickListener(new NotificationClicker(notificationItem.getClickIntent()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startClearButtonAnimation(boolean z) {
        if (this.mClearButton.isEnabled() != z) {
            ImageView imageView = this.mClearButton;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            ObjectAnimator.ofFloat(imageView, "alpha", fArr).setDuration(getResources().getInteger(17694720)).start();
            this.mClearButton.setEnabled(z);
        }
    }

    private void updateNotificationVetoButton(View view, final PendingIntent pendingIntent) {
        view.findViewById(R.id.veto).setOnClickListener(new View.OnClickListener() { // from class: miui.notification.NotificationPanelActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (pendingIntent != null) {
                    try {
                        Log.d(NotificationPanelActivity.TAG, "NotificationClicker clear ");
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
                NotificationPanelActivity.this.removeNotificationView((View) view2.getParent());
            }
        });
    }

    protected void addNotification(NotificationItem notificationItem) {
        if (notificationItem == null) {
            return;
        }
        if (this.mNotificationList.getChildCount() == 0) {
            this.mNoNotificationTips.setVisibility(8);
            this.mNotificationList.setVisibility(0);
            startClearButtonAnimation(true);
        }
        this.mNotificationList.addView(inflateNotificationView(notificationItem), 0);
    }

    public void clearAllNotification(ScrollView scrollView, final NotificationRowLayout notificationRowLayout) {
        int childCount = notificationRowLayout.getChildCount();
        int scrollY = scrollView.getScrollY();
        int height = scrollView.getHeight() + scrollY;
        final ArrayList arrayList = new ArrayList(childCount);
        final ArrayList arrayList2 = new ArrayList(childCount);
        for (int i = 0; i < childCount; i++) {
            View childAt = notificationRowLayout.getChildAt(i);
            if (notificationRowLayout.canChildBeDismissed(childAt) && childAt.getBottom() > scrollY && childAt.getTop() < height) {
                arrayList.add(childAt);
            }
            if (notificationRowLayout.canChildBeDismissed(childAt)) {
                arrayList2.add(childAt);
            }
        }
        new Thread(new Runnable() { // from class: miui.notification.NotificationPanelActivity.5
            @Override // java.lang.Runnable
            public void run() {
                int i2 = 0;
                notificationRowLayout.setViewRemoval(false);
                NotificationPanelActivity.this.mPostCollapseCleanup = new Runnable() { // from class: miui.notification.NotificationPanelActivity.5.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            notificationRowLayout.setViewRemoval(true);
                            Iterator it = arrayList2.iterator();
                            while (it.hasNext()) {
                                ((View) it.next()).findViewById(R.id.veto).performClick();
                            }
                        } catch (Exception unused) {
                        }
                    }
                };
                final int width = ((View) arrayList.get(0)).getWidth() * 8;
                Iterator it = arrayList.iterator();
                int i3 = 140;
                while (it.hasNext()) {
                    final View view = (View) it.next();
                    NotificationPanelActivity.this.mHandler.postDelayed(new Runnable() { // from class: miui.notification.NotificationPanelActivity.5.2
                        @Override // java.lang.Runnable
                        public void run() {
                            notificationRowLayout.dismissRowAnimated(view, width);
                        }
                    }, i2);
                    i3 = Math.max(50, i3 - 10);
                    i2 += i3;
                }
                NotificationPanelActivity.this.mHandler.postDelayed(new Runnable() { // from class: miui.notification.NotificationPanelActivity.5.3
                    @Override // java.lang.Runnable
                    public void run() {
                        NotificationPanelActivity.this.closeAnimation();
                    }
                }, i2 + 225);
            }
        }).start();
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    protected abstract String getAppTitle();

    protected abstract List<NotificationItem> getData();

    @Override // android.app.Activity
    public void onBackPressed() {
        closeAnimation();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.notification_panel);
        getWindow().addFlags(67108864);
        getWindow().addFlags(4);
        getWindow().setBackgroundDrawableResource(miui.system.R.color.blur_background_mask);
        overridePendingTransition(0, 0);
        this.mInflater = LayoutInflater.from(this);
        this.mAppInfo = (TextView) findViewById(R.id.app_info);
        this.mNotificationList = (NotificationRowLayout) findViewById(R.id.list);
        this.mNoNotificationTips = (TextView) findViewById(R.id.no_notification_tips);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        this.mScrollView = scrollView;
        scrollView.setVerticalScrollBarEnabled(false);
        ImageView imageView = (ImageView) findViewById(R.id.clear_button);
        this.mClearButton = imageView;
        imageView.setEnabled(false);
        this.mClearButton.setOnClickListener(this.mClearButtonListener);
        this.mNotificationHeight = getResources().getDimensionPixelSize(R.dimen.notification_row_height);
        this.mHandler = new Handler();
        new LoadDataTask().execute(new Void[0]);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        closeAnimation();
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            closeAnimation();
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    protected void removeNotification(int i) {
        for (int i2 = 0; i2 < this.mNotificationList.getChildCount(); i2++) {
            if (this.mNotificationList.getChildAt(i2).getId() == i) {
                removeNotificationView(this.mNotificationList.getChildAt(i2));
            }
        }
    }

    protected void updateNotification(int i, NotificationItem notificationItem) {
        if (notificationItem == null) {
            return;
        }
        for (int i2 = 0; i2 < this.mNotificationList.getChildCount(); i2++) {
            View childAt = this.mNotificationList.getChildAt(i2);
            if (childAt.getId() == i) {
                setRowValue(childAt, notificationItem);
            }
        }
    }
}
