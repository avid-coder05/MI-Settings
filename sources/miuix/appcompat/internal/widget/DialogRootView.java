package miuix.appcompat.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/* loaded from: classes5.dex */
public class DialogRootView extends FrameLayout {
    private ConfigurationChangedCallback mCallback;

    /* loaded from: classes5.dex */
    public interface ConfigurationChangedCallback {
        void onConfigurationChanged(Configuration configuration);
    }

    public DialogRootView(Context context) {
        super(context);
    }

    public DialogRootView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DialogRootView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        ConfigurationChangedCallback configurationChangedCallback = this.mCallback;
        if (configurationChangedCallback != null) {
            configurationChangedCallback.onConfigurationChanged(configuration);
        }
    }

    public void setConfigurationChangedCallback(ConfigurationChangedCallback configurationChangedCallback) {
        this.mCallback = configurationChangedCallback;
    }
}
