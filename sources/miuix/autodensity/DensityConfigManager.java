package miuix.autodensity;

/* loaded from: classes5.dex */
class DensityConfigManager {
    private static DensityConfigManager sInstance;
    private DensityConfig mCurrentConfig;
    private DensityConfig mDefaultConfig;

    private DensityConfigManager() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DensityConfigManager getInstance() {
        if (sInstance == null) {
            sInstance = new DensityConfigManager();
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkUpdateCurrent(DensityConfig densityConfig) {
        if ((!equalsWithCurrent(densityConfig)) == true) {
            setCurrentConfig(densityConfig);
        }
    }

    boolean equalsWithCurrent(DensityConfig densityConfig) {
        DebugUtil.printDensityLog("currentConfig: " + this.mCurrentConfig);
        DebugUtil.printDensityLog("newConfig: " + densityConfig);
        DensityConfig densityConfig2 = this.mCurrentConfig;
        if (densityConfig2 == null) {
            return false;
        }
        return densityConfig2.equals(densityConfig);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DensityConfig getCurrentConfig() {
        return this.mCurrentConfig;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DensityConfig getDefaultConfig() {
        return this.mDefaultConfig;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initDefaultConfig(DensityConfig densityConfig) {
        this.mDefaultConfig = densityConfig;
    }

    void setCurrentConfig(DensityConfig densityConfig) {
        this.mCurrentConfig = densityConfig;
    }
}
