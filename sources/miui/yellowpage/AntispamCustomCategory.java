package miui.yellowpage;

/* loaded from: classes4.dex */
public class AntispamCustomCategory extends AntispamCategory {
    private boolean mIsUserCustom;
    private int mMarkedCount;
    private String mNumber;
    private int mNumberType;

    public AntispamCustomCategory(int i, String str, int i2, String str2, int i3, String str3, int i4, boolean z) {
        super(i, str, i2, str2, i3);
        this.mNumber = str3;
        this.mMarkedCount = i4;
        this.mIsUserCustom = z;
    }

    public int getMarkedCount() {
        return this.mMarkedCount;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getNumberType() {
        return this.mNumberType;
    }

    public boolean isNumberCategoryCustom() {
        return this.mIsUserCustom;
    }

    public void setNumberType(int i) {
        this.mNumberType = i;
    }
}
