package com.android.settings.usagestats.utils;

import java.util.Observable;
import java.util.Observer;

/* loaded from: classes2.dex */
public class ControllerObserverUtil {
    private static ControllerObserverUtil instance;
    private Observable observable = new Observable() { // from class: com.android.settings.usagestats.utils.ControllerObserverUtil.1
        @Override // java.util.Observable
        public void notifyObservers(Object obj) {
            setChanged();
            super.notifyObservers(obj);
        }
    };

    private ControllerObserverUtil() {
    }

    public static ControllerObserverUtil getInstance() {
        if (instance == null) {
            synchronized (ControllerObserverUtil.class) {
                if (instance == null) {
                    instance = new ControllerObserverUtil();
                }
            }
        }
        return instance;
    }

    public void addObserver(Observer observer) {
        this.observable.addObserver(observer);
    }

    public void notify(Object obj) {
        this.observable.notifyObservers(obj);
    }

    public void removeAllObserver() {
        this.observable.deleteObservers();
    }
}
