package vendor.hardware.vibratorfeature;

import android.content.Context;
import android.hardware.vibrator.IVibrator;
import android.os.ServiceManager;
import android.util.Log;

/* loaded from: classes5.dex */
public class Vibrator {
    private Context mContext;
    private final IVibrator mService = IVibrator.Stub.asInterface(ServiceManager.getService("android.hardware.vibrator.IVibrator/default"));

    public Vibrator(Context context) {
        this.mContext = context;
    }

    public void setAmplitude(float f) {
        IVibrator iVibrator = this.mService;
        if (iVibrator == null) {
            Log.w("VibratorFeature", "mService is null");
            return;
        }
        try {
            iVibrator.setAmplitude(f);
        } catch (Exception e) {
            Log.e("VibratorFeature", "fail to setAmplitude", e);
        }
    }
}
