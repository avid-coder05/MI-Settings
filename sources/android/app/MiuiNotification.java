package android.app;

/* loaded from: classes.dex */
public class MiuiNotification {
    public boolean customizedIcon;
    private boolean enableFloat = true;
    private boolean enableKeyguard = true;
    private int floatTime = 5000;
    private int messageCount = 1;
    private CharSequence targetPkg;

    @Deprecated
    public MiuiNotification setEnableFloat(boolean z) {
        this.enableFloat = z;
        return this;
    }

    @Deprecated
    public void setMessageCount(int i) {
        this.messageCount = i;
    }

    @Deprecated
    public void setTargetPkg(CharSequence charSequence) {
        this.targetPkg = charSequence;
    }
}
