package androidx.appcompat.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.DragEvent;
import android.widget.TextView;
import androidx.core.view.ContentInfoCompat;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
class AppCompatEditor {
    private final TextView mTextView;

    /* loaded from: classes.dex */
    private static final class Api24Impl {
        static boolean onDrop(DragEvent dragEvent, TextView textView, Activity activity) {
            int offsetForPosition = textView.getOffsetForPosition(dragEvent.getX(), dragEvent.getY());
            activity.requestDragAndDropPermissions(dragEvent);
            textView.beginBatchEdit();
            try {
                Selection.setSelection((Spannable) textView.getText(), offsetForPosition);
                ViewCompat.performReceiveContent(textView, new ContentInfoCompat.Builder(dragEvent.getClipData(), 3).build());
                textView.endBatchEdit();
                return true;
            } catch (Throwable th) {
                textView.endBatchEdit();
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatEditor(TextView textView) {
        this.mTextView = textView;
    }

    private Activity getActivity() {
        for (Context context = this.mTextView.getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
        }
        return null;
    }

    public boolean onDragEvent(DragEvent dragEvent) {
        if (Build.VERSION.SDK_INT < 24 || dragEvent.getAction() != 3 || dragEvent.getLocalState() != null || ViewCompat.getOnReceiveContentMimeTypes(this.mTextView) == null) {
            return false;
        }
        Activity activity = getActivity();
        if (activity == null) {
            Log.i("AppCompatEditor", "No activity so not calling performReceiveContent: " + this.mTextView);
            return false;
        }
        return Api24Impl.onDrop(dragEvent, this.mTextView, activity);
    }
}
