package miuix.os;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.HashMap;
import miuix.appcompat.app.ProgressDialog;

/* loaded from: classes5.dex */
public abstract class AsyncTaskWithProgress<Params, Result> extends AsyncTask<Params, Integer, Result> {
    private static final HashMap<String, AsyncTaskWithProgress<?, ?>> sAllTasks = new HashMap<>();
    private final FragmentManager mFragmentManager;
    private int mTheme = 0;
    private int mTitleId = 0;
    private CharSequence mTitle = null;
    private int mMessageId = 0;
    private CharSequence mMessage = null;
    private boolean mCancelable = false;
    private boolean mIndeterminate = false;
    private int mMaxProgress = 0;
    private int mProgressStyle = 0;
    private int mCurrentProgress = 0;
    private volatile ProgressDialogFragment mFragment = null;
    private final AsyncTaskWithProgress<Params, Result>.Listeners mListeners = new Listeners();

    /* loaded from: classes5.dex */
    private class Listeners implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        private Listeners() {
        }

        @Override // android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            onClick(dialogInterface, -2);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            Dialog dialog;
            if (AsyncTaskWithProgress.this.mFragment == null || (dialog = AsyncTaskWithProgress.this.mFragment.getDialog()) == null || dialogInterface != dialog || i != -2) {
                return;
            }
            AsyncTaskWithProgress.this.cancel(true);
        }
    }

    /* loaded from: classes5.dex */
    public static class ProgressDialogFragment extends DialogFragment {
        private AsyncTaskWithProgress<?, ?> mTask;

        static ProgressDialogFragment newInstance(String str) {
            ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("task", str);
            progressDialogFragment.setArguments(bundle);
            return progressDialogFragment;
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            AsyncTaskWithProgress<?, ?> asyncTaskWithProgress = this.mTask;
            if (asyncTaskWithProgress != null && ((AsyncTaskWithProgress) asyncTaskWithProgress).mCancelable) {
                ((AsyncTaskWithProgress) this.mTask).mListeners.onCancel(dialogInterface);
            }
            super.onCancel(dialogInterface);
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            AsyncTaskWithProgress<?, ?> asyncTaskWithProgress = (AsyncTaskWithProgress) AsyncTaskWithProgress.sAllTasks.get(getArguments().getString("task"));
            this.mTask = asyncTaskWithProgress;
            if (asyncTaskWithProgress == null) {
                FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
                beginTransaction.remove(this);
                beginTransaction.commit();
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            if (this.mTask == null) {
                return super.onCreateDialog(bundle);
            }
            ProgressDialog progressDialog = new ProgressDialog(getActivity(), ((AsyncTaskWithProgress) this.mTask).mTheme);
            if (((AsyncTaskWithProgress) this.mTask).mTitleId != 0) {
                progressDialog.setTitle(((AsyncTaskWithProgress) this.mTask).mTitleId);
            } else {
                progressDialog.setTitle(((AsyncTaskWithProgress) this.mTask).mTitle);
            }
            if (((AsyncTaskWithProgress) this.mTask).mMessageId != 0) {
                progressDialog.setMessage(getActivity().getText(((AsyncTaskWithProgress) this.mTask).mMessageId));
            } else {
                progressDialog.setMessage(((AsyncTaskWithProgress) this.mTask).mMessage);
            }
            progressDialog.setProgressStyle(((AsyncTaskWithProgress) this.mTask).mProgressStyle);
            progressDialog.setIndeterminate(((AsyncTaskWithProgress) this.mTask).mIndeterminate);
            if (!((AsyncTaskWithProgress) this.mTask).mIndeterminate) {
                progressDialog.setMax(((AsyncTaskWithProgress) this.mTask).mMaxProgress);
                progressDialog.setProgress(((AsyncTaskWithProgress) this.mTask).mCurrentProgress);
            }
            if (((AsyncTaskWithProgress) this.mTask).mCancelable) {
                progressDialog.setButton(-2, progressDialog.getContext().getText(17039360), ((AsyncTaskWithProgress) this.mTask).mListeners);
                progressDialog.setCancelable(true);
            } else {
                progressDialog.setButton(-2, null, null);
                progressDialog.setCancelable(false);
            }
            return progressDialog;
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            AsyncTaskWithProgress<?, ?> asyncTaskWithProgress = this.mTask;
            if (asyncTaskWithProgress != null) {
                ((AsyncTaskWithProgress) asyncTaskWithProgress).mFragment = this;
            }
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onStop() {
            AsyncTaskWithProgress<?, ?> asyncTaskWithProgress = this.mTask;
            if (asyncTaskWithProgress != null) {
                ((AsyncTaskWithProgress) asyncTaskWithProgress).mFragment = null;
            }
            super.onStop();
        }

        void setProgress(int i) {
            Dialog dialog = getDialog();
            if (dialog instanceof ProgressDialog) {
                ((ProgressDialog) dialog).setProgress(i);
            }
        }
    }

    public AsyncTaskWithProgress(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    private void dismissDialog() {
        ProgressDialogFragment progressDialogFragment = (ProgressDialogFragment) this.mFragmentManager.findFragmentByTag("AsyncTaskWithProgress@" + hashCode());
        if (progressDialogFragment != null) {
            progressDialogFragment.dismissAllowingStateLoss();
        }
    }

    @Override // android.os.AsyncTask
    public void onCancelled() {
        sAllTasks.remove("AsyncTaskWithProgress@" + hashCode());
        dismissDialog();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Result result) {
        sAllTasks.remove("AsyncTaskWithProgress@" + hashCode());
        dismissDialog();
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        String str = "AsyncTaskWithProgress@" + hashCode();
        sAllTasks.put(str, this);
        if (this.mFragmentManager != null) {
            this.mFragment = ProgressDialogFragment.newInstance(str);
            this.mFragment.setCancelable(this.mCancelable);
            this.mFragment.show(this.mFragmentManager, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onProgressUpdate(Integer... numArr) {
        super.onProgressUpdate((Object[]) numArr);
        this.mCurrentProgress = numArr[0].intValue();
        if (this.mFragment != null) {
            this.mFragment.setProgress(this.mCurrentProgress);
        }
    }

    public AsyncTaskWithProgress<Params, Result> setCancelable(boolean z) {
        this.mCancelable = z;
        return this;
    }

    public AsyncTaskWithProgress<Params, Result> setMessage(int i) {
        this.mMessageId = i;
        this.mMessage = null;
        return this;
    }
}
