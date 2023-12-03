package com.xiaomi.mirror;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.InputEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import com.xiaomi.mirror.IMirrorManager;
import com.xiaomi.mirror.opensdk.R;
import com.xiaomi.mirror.widget.ImmersionAdapter;
import com.xiaomi.mirror.widget.ImmersionListPopupWindow;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class MirrorManagerImpl {
    private static volatile MirrorManagerImpl INSTANCE = null;
    private static final String PACKAGE_NAME_MIRROR = "com.xiaomi.mirror";
    private static final String TAG = "MirrorManagerImpl";
    private ImmersionListPopupWindow mImmersionListPopupWindow;
    private IMirrorManager mMirrorManager;

    private MirrorManagerImpl(Context context) {
        if (context == null) {
            return;
        }
        try {
            IMirrorManager iMirrorManager = (IMirrorManager) MirrorManager.get(context.getApplicationContext());
            this.mMirrorManager = iMirrorManager;
            if (iMirrorManager != null) {
                iMirrorManager.setOnMirrorMenuClickListener(new IMirrorManager.OnMirrorMenuClickListener() { // from class: com.xiaomi.mirror.MirrorManagerImpl.1
                    @Override // com.xiaomi.mirror.IMirrorManager.OnMirrorMenuClickListener
                    public boolean onMirrorMenuClick(View view) {
                        ArrayList<MirrorMenu> onMirrorMenuQuery;
                        OnMirrorMenuQueryListener onMirrorMenuQueryListener = MirrorManagerImpl.getOnMirrorMenuQueryListener(view);
                        if (onMirrorMenuQueryListener == null || (onMirrorMenuQuery = onMirrorMenuQueryListener.onMirrorMenuQuery(view)) == null || onMirrorMenuQuery.size() == 0) {
                            return false;
                        }
                        MirrorManagerImpl.this.showPopupWindow(view, onMirrorMenuQuery);
                        return true;
                    }
                });
            }
        } catch (Throwable th) {
            Log.v(TAG, "MirrorManagerImpl " + th.toString());
        }
    }

    public static MirrorManagerImpl get(Context context) {
        if (INSTANCE == null) {
            synchronized (MirrorManagerImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MirrorManagerImpl(context);
                }
            }
        }
        return INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static OnMirrorMenuQueryListener getOnMirrorMenuQueryListener(View view) {
        if (view == null) {
            return null;
        }
        Object tag = view.getTag(R.id.tag_mirror_menu_query_listener);
        if ((tag == null || (tag instanceof OnMirrorMenuQueryListener)) && tag != null) {
            return (OnMirrorMenuQueryListener) tag;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPopupWindow(final View view, final ArrayList<MirrorMenu> arrayList) {
        CharSequence label;
        Context context;
        int i;
        if (view == null || arrayList == null || arrayList.size() <= 0) {
            return;
        }
        ImmersionListPopupWindow immersionListPopupWindow = this.mImmersionListPopupWindow;
        if (immersionListPopupWindow == null || !immersionListPopupWindow.isShowing()) {
            ArrayList arrayList2 = new ArrayList();
            Iterator<MirrorMenu> it = arrayList.iterator();
            while (it.hasNext()) {
                MirrorMenu next = it.next();
                if (next.getType() == 1) {
                    context = view.getContext();
                    i = R.string.menu_new_display_open;
                } else if (next.getType() == 2) {
                    context = view.getContext();
                    i = R.string.menu_pc_open;
                } else {
                    label = next.getLabel();
                    arrayList2.add(ImmersionAdapter.buildSettingViewEntry(label));
                }
                label = context.getString(i);
                arrayList2.add(ImmersionAdapter.buildSettingViewEntry(label));
            }
            ImmersionAdapter immersionAdapter = new ImmersionAdapter(view.getContext(), arrayList2);
            ImmersionListPopupWindow immersionListPopupWindow2 = new ImmersionListPopupWindow(view.getContext());
            this.mImmersionListPopupWindow = immersionListPopupWindow2;
            immersionListPopupWindow2.setAdapter(immersionAdapter);
            this.mImmersionListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.xiaomi.mirror.MirrorManagerImpl.2
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view2, int i2, long j) {
                    MirrorManagerImpl.this.mImmersionListPopupWindow.dismiss();
                    MirrorMenu mirrorMenu = (MirrorMenu) arrayList.get(i2);
                    if (!mirrorMenu.needCallRemote()) {
                        if (mirrorMenu.getListener() != null) {
                            mirrorMenu.getListener().onClick(view, mirrorMenu);
                        }
                        if (mirrorMenu.getPendingIntent() != null) {
                            try {
                                mirrorMenu.getPendingIntent().send();
                                return;
                            } catch (PendingIntent.CanceledException e) {
                                Log.e(MirrorManagerImpl.TAG, "", e);
                                return;
                            }
                        }
                        return;
                    }
                    if (mirrorMenu.getUri() != null) {
                        view.getContext().grantUriPermission(MirrorManagerImpl.PACKAGE_NAME_MIRROR, mirrorMenu.getUri(), 3);
                    }
                    int i3 = 0;
                    try {
                        View view3 = view;
                        if (view3 != null && view3.getDisplay() != null) {
                            i3 = view.getDisplay().getDisplayId();
                        }
                        MirrorManagerImpl.this.mMirrorManager.onRemoteMenuActionCall(mirrorMenu, i3);
                    } catch (Throwable th) {
                        Log.e(MirrorManagerImpl.TAG, "onRemoteMenuActionCall", th);
                    }
                }
            });
            this.mImmersionListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.xiaomi.mirror.MirrorManagerImpl.3
                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    OnMirrorMenuQueryListener onMirrorMenuQueryListener = MirrorManagerImpl.getOnMirrorMenuQueryListener(view);
                    if (onMirrorMenuQueryListener != null) {
                        onMirrorMenuQueryListener.onMirrorMenuShow(view, false);
                    }
                }
            });
            this.mImmersionListPopupWindow.show(view, null);
            OnMirrorMenuQueryListener onMirrorMenuQueryListener = getOnMirrorMenuQueryListener(view);
            if (onMirrorMenuQueryListener != null) {
                onMirrorMenuQueryListener.onMirrorMenuShow(view, true);
            }
        }
    }

    public boolean isCurrentClickFromMirror() {
        IMirrorManager iMirrorManager = this.mMirrorManager;
        if (iMirrorManager == null) {
            return false;
        }
        try {
            return iMirrorManager.isCurrentClickFromMirror();
        } catch (Throwable th) {
            Log.e(TAG, "isCurrentClickFromMirror", th);
            return false;
        }
    }

    public boolean isEventFromMirror(InputEvent inputEvent) {
        IMirrorManager iMirrorManager = this.mMirrorManager;
        if (iMirrorManager == null) {
            return false;
        }
        try {
            return iMirrorManager.isEventFromMirror(inputEvent);
        } catch (Throwable th) {
            Log.e(TAG, "isEventFromMirror", th);
            return false;
        }
    }

    public boolean isSupport() {
        IMirrorManager iMirrorManager = this.mMirrorManager;
        if (iMirrorManager == null) {
            return false;
        }
        try {
            return iMirrorManager.isModelSupport();
        } catch (Throwable th) {
            Log.e(TAG, "isSupport", th);
            return false;
        }
    }

    public boolean isWorking() {
        IMirrorManager iMirrorManager = this.mMirrorManager;
        if (iMirrorManager == null) {
            return false;
        }
        try {
            return iMirrorManager.isWorking();
        } catch (Throwable th) {
            Log.e(TAG, "isWorking", th);
            return false;
        }
    }

    public void notifyStartActivity(Intent intent) {
        IMirrorManager iMirrorManager = this.mMirrorManager;
        if (iMirrorManager == null) {
            return;
        }
        try {
            iMirrorManager.notifyStartActivity(intent);
        } catch (Throwable th) {
            Log.e(TAG, "notifyStartActivity", th);
        }
    }

    public void notifyStartActivityFromRecents(int i, Bundle bundle) {
        IMirrorManager iMirrorManager = this.mMirrorManager;
        if (iMirrorManager == null) {
            return;
        }
        try {
            iMirrorManager.notifyStartActivityFromRecents(i, bundle);
        } catch (Throwable th) {
            Log.e(TAG, "notifyStartActivityFromRecents", th);
        }
    }

    public void setOnMirrorMenuQueryListener(View view, OnMirrorMenuQueryListener onMirrorMenuQueryListener) {
        if (view == null) {
            Log.e(TAG, "setOnMirrorMenuQueryListener view is null");
        } else if (onMirrorMenuQueryListener != null) {
            int i = R.id.tag_mirror_menu_query_listener;
            Object tag = view.getTag(i);
            if (tag == null || (tag instanceof OnMirrorMenuQueryListener)) {
                view.setTag(i, onMirrorMenuQueryListener);
            } else {
                Log.e(TAG, "the tag is busy.".concat(String.valueOf(tag)));
            }
        } else {
            int i2 = R.id.tag_mirror_menu_query_listener;
            Object tag2 = view.getTag(i2);
            if (tag2 == null || (tag2 instanceof OnMirrorMenuQueryListener)) {
                view.setTag(i2, null);
            } else {
                Log.e(TAG, "[remove]the tag is busy.".concat(String.valueOf(tag2)));
            }
        }
    }
}
