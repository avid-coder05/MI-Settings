package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.util.Log;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public class ConditionManager {
    private final Context mAppContext;
    final List<ConditionalCardController> mCardControllers = new ArrayList();
    private boolean mIsListeningToStateChange;
    private final ConditionListener mListener;

    /* loaded from: classes.dex */
    public static class DisplayableChecker implements Callable<ContextualCard> {
        private final ConditionalCardController mController;

        private DisplayableChecker(ConditionalCardController conditionalCardController) {
            this.mController = conditionalCardController;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public ContextualCard call() throws Exception {
            if (this.mController.isDisplayable()) {
                return this.mController.buildContextualCard();
            }
            return null;
        }
    }

    public ConditionManager(Context context, ConditionListener conditionListener) {
        this.mAppContext = context.getApplicationContext();
        this.mListener = conditionListener;
        initCandidates();
    }

    private <T extends ConditionalCardController> T getController(long j) {
        Iterator<ConditionalCardController> it = this.mCardControllers.iterator();
        while (it.hasNext()) {
            T t = (T) it.next();
            if (t.getId() == j) {
                return t;
            }
        }
        throw new IllegalStateException("Cannot find controller for " + j);
    }

    private void initCandidates() {
        this.mCardControllers.add(new AirplaneModeConditionController(this.mAppContext, this));
        this.mCardControllers.add(new BackgroundDataConditionController(this.mAppContext, this));
        this.mCardControllers.add(new BatterySaverConditionController(this.mAppContext, this));
        this.mCardControllers.add(new CellularDataConditionController(this.mAppContext, this));
        this.mCardControllers.add(new DndConditionCardController(this.mAppContext, this));
        this.mCardControllers.add(new HotspotConditionController(this.mAppContext, this));
        this.mCardControllers.add(new NightDisplayConditionController(this.mAppContext, this));
        this.mCardControllers.add(new RingerVibrateConditionController(this.mAppContext, this));
        this.mCardControllers.add(new RingerMutedConditionController(this.mAppContext, this));
        this.mCardControllers.add(new WorkModeConditionController(this.mAppContext, this));
        this.mCardControllers.add(new GrayscaleConditionController(this.mAppContext, this));
    }

    public List<ContextualCard> getDisplayableCards() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Iterator<ConditionalCardController> it = this.mCardControllers.iterator();
        while (it.hasNext()) {
            arrayList2.add(ThreadUtils.postOnBackgroundThread(new DisplayableChecker(getController(it.next().getId()))));
        }
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            try {
                ContextualCard contextualCard = (ContextualCard) ((Future) it2.next()).get(20L, TimeUnit.MILLISECONDS);
                if (contextualCard != null) {
                    arrayList.add(contextualCard);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.w("ConditionManager", "Failed to get displayable state for card, likely timeout. Skipping", e);
            }
        }
        return arrayList;
    }

    public void onActionClick(long j) {
        getController(j).onActionClick();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onConditionChanged() {
        ConditionListener conditionListener = this.mListener;
        if (conditionListener != null) {
            conditionListener.onConditionsChanged();
        }
    }

    public void onPrimaryClick(Context context, long j) {
        getController(j).onPrimaryClick(context);
    }

    public void startMonitoringStateChange() {
        if (this.mIsListeningToStateChange) {
            Log.d("ConditionManager", "Already listening to condition state changes, skipping monitor setup");
        } else {
            this.mIsListeningToStateChange = true;
            Iterator<ConditionalCardController> it = this.mCardControllers.iterator();
            while (it.hasNext()) {
                it.next().startMonitoringStateChange();
            }
        }
        onConditionChanged();
    }

    public void stopMonitoringStateChange() {
        if (!this.mIsListeningToStateChange) {
            Log.d("ConditionManager", "Not listening to condition state changes, skipping");
            return;
        }
        Iterator<ConditionalCardController> it = this.mCardControllers.iterator();
        while (it.hasNext()) {
            it.next().stopMonitoringStateChange();
        }
        this.mIsListeningToStateChange = false;
    }
}
