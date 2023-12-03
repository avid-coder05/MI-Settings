package miui.cta;

import android.content.Context;
import android.content.DialogInterface;
import com.miui.system.internal.R;
import miui.app.AlertDialog;

/* loaded from: classes3.dex */
public class CTADialogBuilder extends AlertDialog.Builder {
    private Context mContext;

    public CTADialogBuilder(Context context) {
        super(context);
        initialize(context);
    }

    public CTADialogBuilder(Context context, int i) {
        super(context, i);
        initialize(context);
    }

    private void initialize(Context context) {
        this.mContext = context;
        setTitle(R.string.cta_title);
        setCancelable(false);
    }

    public CTADialogBuilder setNegativeButton(DialogInterface.OnClickListener onClickListener) {
        setNegativeButton(R.string.cta_button_quit, onClickListener);
        return this;
    }

    public CTADialogBuilder setPositiveButton(DialogInterface.OnClickListener onClickListener) {
        setPositiveButton(R.string.cta_button_continue, onClickListener);
        return this;
    }
}
