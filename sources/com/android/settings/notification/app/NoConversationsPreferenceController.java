package com.android.settings.notification.app;

import android.app.people.IPeopleManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.service.notification.ConversationChannelWrapper;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

/* loaded from: classes2.dex */
public class NoConversationsPreferenceController extends ConversationListPreferenceController {
    private static String TAG = "NoConversationsPC";
    private int mConversationCount;
    private IPeopleManager mPs;

    public NoConversationsPreferenceController(Context context, NotificationBackend notificationBackend, IPeopleManager iPeopleManager) {
        super(context, notificationBackend);
        this.mConversationCount = 0;
        this.mPs = iPeopleManager;
    }

    static /* synthetic */ int access$012(NoConversationsPreferenceController noConversationsPreferenceController, int i) {
        int i2 = noConversationsPreferenceController.mConversationCount + i;
        noConversationsPreferenceController.mConversationCount = i2;
        return i2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "no_conversations";
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    Preference getSummaryPreference() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        final LayoutPreference layoutPreference = (LayoutPreference) preference;
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.app.NoConversationsPreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                NoConversationsPreferenceController noConversationsPreferenceController = NoConversationsPreferenceController.this;
                noConversationsPreferenceController.mConversationCount = noConversationsPreferenceController.mBackend.getConversations(false).getList().size();
                try {
                    NoConversationsPreferenceController noConversationsPreferenceController2 = NoConversationsPreferenceController.this;
                    NoConversationsPreferenceController.access$012(noConversationsPreferenceController2, noConversationsPreferenceController2.mPs.getRecentConversations().getList().size());
                    return null;
                } catch (RemoteException e) {
                    Log.w(NoConversationsPreferenceController.TAG, "Error calling PS", e);
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r3) {
                if (((AbstractPreferenceController) NoConversationsPreferenceController.this).mContext == null) {
                    return;
                }
                layoutPreference.findViewById(R.id.onboarding).setVisibility(NoConversationsPreferenceController.this.mConversationCount == 0 ? 0 : 8);
                preference.setVisible(NoConversationsPreferenceController.this.mConversationCount == 0);
            }
        }.execute(new Void[0]);
    }
}
