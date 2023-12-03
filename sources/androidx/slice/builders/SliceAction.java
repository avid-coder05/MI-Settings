package androidx.slice.builders;

import android.app.PendingIntent;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.core.SliceActionImpl;

/* loaded from: classes.dex */
public class SliceAction implements androidx.slice.core.SliceAction {
    private final SliceActionImpl mSliceAction;

    public SliceAction(PendingIntent action, IconCompat actionIcon, int imageMode, CharSequence actionTitle) {
        this.mSliceAction = new SliceActionImpl(action, actionIcon, imageMode, actionTitle);
    }

    public SliceAction(PendingIntent action, IconCompat actionIcon, CharSequence actionTitle, boolean isChecked) {
        this.mSliceAction = new SliceActionImpl(action, actionIcon, actionTitle, isChecked);
    }

    public SliceAction(PendingIntent action, CharSequence actionTitle, boolean isChecked) {
        this.mSliceAction = new SliceActionImpl(action, actionTitle, isChecked);
    }

    public static SliceAction create(PendingIntent action, IconCompat actionIcon, int imageMode, CharSequence actionTitle) {
        return new SliceAction(action, actionIcon, imageMode, actionTitle);
    }

    public static SliceAction createDeeplink(PendingIntent action, IconCompat actionIcon, int imageMode, CharSequence actionTitle) {
        SliceAction sliceAction = new SliceAction(action, actionIcon, imageMode, actionTitle);
        sliceAction.mSliceAction.setActivity(true);
        return sliceAction;
    }

    public static SliceAction createToggle(PendingIntent action, IconCompat actionIcon, CharSequence actionTitle, boolean isChecked) {
        return new SliceAction(action, actionIcon, actionTitle, isChecked);
    }

    public static SliceAction createToggle(PendingIntent action, CharSequence actionTitle, boolean isChecked) {
        return new SliceAction(action, actionTitle, isChecked);
    }

    public Slice buildSlice(Slice.Builder builder) {
        return this.mSliceAction.buildSlice(builder);
    }

    @Override // androidx.slice.core.SliceAction
    public PendingIntent getAction() {
        return this.mSliceAction.getAction();
    }

    @Override // androidx.slice.core.SliceAction
    public IconCompat getIcon() {
        return this.mSliceAction.getIcon();
    }

    @Override // androidx.slice.core.SliceAction
    public int getImageMode() {
        return this.mSliceAction.getImageMode();
    }

    public SliceActionImpl getImpl() {
        return this.mSliceAction;
    }

    @Override // androidx.slice.core.SliceAction
    public int getPriority() {
        return this.mSliceAction.getPriority();
    }

    @Override // androidx.slice.core.SliceAction
    public CharSequence getTitle() {
        return this.mSliceAction.getTitle();
    }

    @Override // androidx.slice.core.SliceAction
    public boolean isToggle() {
        return this.mSliceAction.isToggle();
    }

    public void setPrimaryAction(Slice.Builder builder) {
        builder.addAction(this.mSliceAction.getAction(), this.mSliceAction.buildPrimaryActionSlice(builder), this.mSliceAction.getSubtype());
    }
}
