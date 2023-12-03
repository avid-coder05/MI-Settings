package com.android.settings.applications;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import com.android.settingslib.applications.ApplicationsState;

/* loaded from: classes.dex */
public class IconLoader {
    private BackgroundHandler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private String mBgThreadName;
    Handler mMainHandler = new Handler() { // from class: com.android.settings.applications.IconLoader.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            IconItem iconItem;
            if (message.what != 1 || (iconItem = (IconItem) message.obj) == null) {
                return;
            }
            iconItem.refreshIcon();
        }
    };

    /* loaded from: classes.dex */
    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        public int getMessageType(int i) {
            int i2 = i % 15;
            if (hasMessages(i2)) {
                removeMessages(i2);
            }
            return i2;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            IconItem iconItem = (IconItem) message.obj;
            if (iconItem == null) {
                return;
            }
            ApplicationsState.AppEntry appEntry = iconItem.mEntry;
            appEntry.icon = appEntry.getIcon(iconItem.mIconView.getContext());
            Message obtainMessage = IconLoader.this.mMainHandler.obtainMessage();
            obtainMessage.what = 1;
            obtainMessage.obj = iconItem;
            IconLoader.this.mMainHandler.sendMessage(obtainMessage);
        }
    }

    /* loaded from: classes.dex */
    static class IconItem {
        ApplicationsState.AppEntry mEntry;
        ImageView mIconView;

        public IconItem(ImageView imageView, ApplicationsState.AppEntry appEntry) {
            this.mIconView = imageView;
            this.mEntry = appEntry;
        }

        public void refreshIcon() {
            if (this.mEntry.info.packageName.equals((String) this.mIconView.getTag())) {
                this.mIconView.setImageDrawable(this.mEntry.icon);
            }
        }
    }

    public IconLoader(String str) {
        this.mBgThreadName = str;
    }

    public void loadIcon(ImageView imageView, ApplicationsState.AppEntry appEntry, int i) {
        IconItem iconItem = new IconItem(imageView, appEntry);
        int messageType = this.mBackgroundHandler.getMessageType(i);
        Message obtainMessage = this.mBackgroundHandler.obtainMessage();
        obtainMessage.what = messageType;
        obtainMessage.obj = iconItem;
        this.mBackgroundHandler.sendMessage(obtainMessage);
    }

    public void start() {
        HandlerThread handlerThread = new HandlerThread(this.mBgThreadName, 10);
        this.mBackgroundThread = handlerThread;
        handlerThread.start();
        this.mBackgroundHandler = new BackgroundHandler(this.mBackgroundThread.getLooper());
    }

    public void stop() {
        this.mMainHandler.removeMessages(1);
        int i = 0;
        while (i < 15) {
            i++;
            this.mBackgroundHandler.removeMessages(i);
        }
        this.mBackgroundThread.quit();
    }
}
