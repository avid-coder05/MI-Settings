package miui.vip;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes4.dex */
public class VipUserInfo implements Parcelable {
    public static final Parcelable.Creator<VipUserInfo> CREATOR = new Parcelable.Creator<VipUserInfo>() { // from class: miui.vip.VipUserInfo.1
        @Override // android.os.Parcelable.Creator
        public VipUserInfo createFromParcel(Parcel parcel) {
            return VipUserInfo.readFromParcel(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public VipUserInfo[] newArray(int i) {
            return new VipUserInfo[i];
        }
    };
    public int dailyTaskLimit;
    public int hasNewAwards;
    public int level;
    public long registerTime;
    public int score;
    public int scoreToNextLevel;
    public String timezone;
    public int todayCompleteTaskCount;
    public int todayScore;
    public int totalScore;
    public int userId;
    public String levelTxt = "";
    public String taskTxt = "";
    public String badgeTxt = "";

    public static VipUserInfo readFromParcel(Parcel parcel) {
        VipUserInfo vipUserInfo = new VipUserInfo();
        vipUserInfo.userId = parcel.readInt();
        vipUserInfo.level = parcel.readInt();
        vipUserInfo.score = parcel.readInt();
        vipUserInfo.scoreToNextLevel = parcel.readInt();
        vipUserInfo.totalScore = parcel.readInt();
        vipUserInfo.todayScore = parcel.readInt();
        vipUserInfo.dailyTaskLimit = parcel.readInt();
        vipUserInfo.todayCompleteTaskCount = parcel.readInt();
        vipUserInfo.timezone = parcel.readString();
        vipUserInfo.levelTxt = parcel.readString();
        vipUserInfo.taskTxt = parcel.readString();
        vipUserInfo.badgeTxt = parcel.readString();
        vipUserInfo.registerTime = parcel.readLong();
        vipUserInfo.hasNewAwards = parcel.readInt();
        return vipUserInfo;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "VipUserInfo{userId=" + this.userId + ", level=" + this.level + ", score=" + this.score + ", scoreToNextLevel=" + this.scoreToNextLevel + ", totalScore=" + this.totalScore + ", todayScore=" + this.todayScore + ", dailyTaskLimit=" + this.dailyTaskLimit + ", todayCompleteTaskCount=" + this.todayCompleteTaskCount + ", timezone='" + this.timezone + "', levelTxt='" + this.levelTxt + "', taskTxt='" + this.taskTxt + "', badgeTxt='" + this.badgeTxt + "', registerTime=" + this.registerTime + ", hasNewAwards=" + this.hasNewAwards + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.userId);
        parcel.writeInt(this.level);
        parcel.writeInt(this.score);
        parcel.writeInt(this.scoreToNextLevel);
        parcel.writeInt(this.totalScore);
        parcel.writeInt(this.todayScore);
        parcel.writeInt(this.dailyTaskLimit);
        parcel.writeInt(this.todayCompleteTaskCount);
        parcel.writeString(this.timezone);
        parcel.writeString(this.levelTxt);
        parcel.writeString(this.taskTxt);
        parcel.writeString(this.badgeTxt);
        parcel.writeLong(this.registerTime);
        parcel.writeInt(this.hasNewAwards);
    }
}
