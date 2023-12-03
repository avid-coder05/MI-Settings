package com.android.settings.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.android.internal.widget.ViewPager;
import java.util.Locale;

/* loaded from: classes2.dex */
public final class RtlCompatibleViewPager extends ViewPager {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class RtlSavedState extends View.BaseSavedState {
        public static final Parcelable.ClassLoaderCreator<RtlSavedState> CREATOR = new Parcelable.ClassLoaderCreator<RtlSavedState>() { // from class: com.android.settings.widget.RtlCompatibleViewPager.RtlSavedState.1
            /* JADX WARN: Type inference failed for: r0v0, types: [com.android.settings.widget.RtlCompatibleViewPager$1, java.lang.ClassLoader] */
            @Override // android.os.Parcelable.Creator
            public RtlSavedState createFromParcel(Parcel parcel) {
                ?? r0 = 0;
                return new RtlSavedState(parcel, r0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public RtlSavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new RtlSavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public RtlSavedState[] newArray(int i) {
                return new RtlSavedState[i];
            }
        };
        int position;

        private RtlSavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.position = parcel.readInt();
        }

        public RtlSavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.position);
        }
    }

    public RtlCompatibleViewPager(Context context) {
        this(context, null);
    }

    public RtlCompatibleViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public int getCurrentItem() {
        return getRtlAwareIndex(super.getCurrentItem());
    }

    public int getRtlAwareIndex(int i) {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1 ? (getAdapter().getCount() - i) - 1 : i;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        RtlSavedState rtlSavedState = (RtlSavedState) parcelable;
        super.onRestoreInstanceState(rtlSavedState.getSuperState());
        setCurrentItem(rtlSavedState.position);
    }

    public Parcelable onSaveInstanceState() {
        RtlSavedState rtlSavedState = new RtlSavedState(super.onSaveInstanceState());
        rtlSavedState.position = getCurrentItem();
        return rtlSavedState;
    }

    public void setCurrentItem(int i) {
        super.setCurrentItem(getRtlAwareIndex(i));
    }
}
