package com.android.settings.dndmode;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.R;
import java.text.DateFormatSymbols;
import java.util.Calendar;

/* loaded from: classes.dex */
public final class Alarm implements Parcelable {
    public static final String ALARM_ALERT_SILENT = "alarm_killed";
    public static final int ALARM_TYPE = 0;
    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() { // from class: com.android.settings.dndmode.Alarm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Alarm createFromParcel(Parcel parcel) {
            return new Alarm(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Alarm[] newArray(int i) {
            return new Alarm[i];
        }
    };
    public static final int TIMER_TYPE = 1;
    public Uri alert;
    public DaysOfWeek daysOfWeek;
    public boolean deleteAfterUse;
    public boolean enabled;
    public int hour;
    public int id;
    public String label;
    public int minutes;
    public int seconds;
    public boolean silent;
    public long time;
    public boolean vibrate;

    /* loaded from: classes.dex */
    public static final class DaysOfWeek {
        private static int[] DAY_MAP = {2, 3, 4, 5, 6, 7, 1};
        private int mDays;

        public DaysOfWeek(int i) {
            this.mDays = i;
        }

        private boolean isSet(int i) {
            return (this.mDays & (1 << i)) > 0;
        }

        public int getAlarmType() {
            int i = this.mDays;
            if (i != 48) {
                if (i != 79) {
                    return i != 127 ? 4 : 1;
                }
                return 2;
            }
            return 3;
        }

        public boolean[] getBooleanArray() {
            boolean[] zArr = new boolean[7];
            for (int i = 0; i < 7; i++) {
                zArr[i] = isSet(i);
            }
            return zArr;
        }

        public int getCoded() {
            return this.mDays;
        }

        public boolean isAlarmDay() {
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                if ((this.mDays & (1 << i)) != 0) {
                    if (i == 6) {
                        if (calendar.get(7) == 1) {
                            return true;
                        }
                    } else if (calendar.get(7) == i + 2) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isRepeatSet() {
            return this.mDays != 0;
        }

        public void set(int i, boolean z) {
            if (z) {
                this.mDays = (1 << i) | this.mDays;
                return;
            }
            this.mDays = (~(1 << i)) & this.mDays;
        }

        public void set(DaysOfWeek daysOfWeek) {
            this.mDays = daysOfWeek.mDays;
        }

        public String toString(Context context, boolean z) {
            StringBuilder sb = new StringBuilder();
            int i = this.mDays;
            if (i == 0) {
                return z ? context.getText(R.string.repeat_never).toString() : "";
            } else if (i == 127) {
                return context.getText(R.string.every_day).toString();
            } else {
                if (i == 128) {
                    int i2 = R.string.legal_workday;
                    if (HolidayHelper.isHolidayDataInvalid(context)) {
                        i2 = R.string.legal_workday_invalidate;
                    }
                    return context.getText(i2).toString();
                }
                int i3 = 0;
                while (i > 0) {
                    if ((i & 1) == 1) {
                        i3++;
                    }
                    i >>= 1;
                }
                DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
                String[] shortWeekdays = i3 > 1 ? dateFormatSymbols.getShortWeekdays() : dateFormatSymbols.getWeekdays();
                for (int i4 = 0; i4 < 7; i4++) {
                    if ((this.mDays & (1 << i4)) != 0) {
                        sb.append(shortWeekdays[DAY_MAP[i4]]);
                        i3--;
                        if (i3 > 0) {
                            sb.append(" ");
                        }
                    }
                }
                return sb.toString();
            }
        }
    }

    public Alarm() {
        this.id = -1;
        this.enabled = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        this.hour = calendar.get(11);
        this.minutes = calendar.get(12);
        this.seconds = calendar.get(13);
        this.vibrate = true;
        this.daysOfWeek = new DaysOfWeek(0);
        this.alert = RingtoneManager.getDefaultUri(4);
        this.label = "";
        this.deleteAfterUse = false;
    }

    public Alarm(Parcel parcel) {
        this.id = parcel.readInt();
        this.enabled = parcel.readInt() == 1;
        this.hour = parcel.readInt();
        this.minutes = parcel.readInt();
        this.seconds = parcel.readInt();
        this.daysOfWeek = new DaysOfWeek(parcel.readInt());
        this.time = parcel.readLong();
        this.vibrate = parcel.readInt() == 1;
        this.label = parcel.readString();
        this.alert = (Uri) parcel.readParcelable(null);
        this.silent = parcel.readInt() == 1;
        this.deleteAfterUse = parcel.readInt() == 1;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Alarm) && this.id == ((Alarm) obj).id;
    }

    public int hashCode() {
        return this.id;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeInt(this.enabled ? 1 : 0);
        parcel.writeInt(this.hour);
        parcel.writeInt(this.minutes);
        parcel.writeInt(this.seconds);
        parcel.writeInt(this.daysOfWeek.getCoded());
        parcel.writeLong(this.time);
        parcel.writeInt(this.vibrate ? 1 : 0);
        parcel.writeString(this.label);
        parcel.writeParcelable(this.alert, i);
        parcel.writeInt(this.silent ? 1 : 0);
        parcel.writeInt(this.deleteAfterUse ? 1 : 0);
    }
}
