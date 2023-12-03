package com.android.settings.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.SoundPool;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import miuix.animation.Folme;

/* loaded from: classes2.dex */
public class PopupCameraSoundChoosePreference extends Preference {
    private PopupCameraSoundListAdapter mAdapter;
    private Context mContext;
    private GridView mGridView;
    private Handler mHandler;
    private int[] mImages;
    private int mLastIndex;
    private SoundPool mSoundPool;
    private String[] mSounds;
    private Runnable mTask;
    private int[] mTexts;

    public PopupCameraSoundChoosePreference(Context context) {
        this(context, null);
    }

    public PopupCameraSoundChoosePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PopupCameraSoundChoosePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int[] iArr = {R.drawable.popup_muqin_up, R.drawable.popup_yingyan_up, R.drawable.popup_mofa_up, R.drawable.popup_jijia_up, R.drawable.popup_chilun_up, R.drawable.popup_cangmen_up};
        this.mImages = iArr;
        this.mTexts = new int[]{R.string.popup_title_muqin, R.string.popup_title_yingyan, R.string.popup_title_mofa, R.string.popup_title_jijia, R.string.popup_title_chilun, R.string.popup_title_cangmen};
        this.mSounds = new String[iArr.length * 2];
        this.mContext = context;
        setLayoutResource(R.layout.popup_gridview_list);
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.key_popup_voice_choice);
        int i2 = 0;
        while (true) {
            String[] strArr = this.mSounds;
            if (i2 >= strArr.length) {
                break;
            }
            strArr[i2] = "system/media/audio/ui/" + stringArray[i2];
            i2++;
        }
        this.mHandler = new Handler();
        if (this.mSoundPool == null) {
            this.mSoundPool = new SoundPool(10, 1, 0);
        }
        for (String str : this.mSounds) {
            this.mSoundPool.load(str, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSoundSelected$0(int i) {
        realPlaySound((i * 2) + 1);
    }

    private void realPlaySound(int i) {
        this.mSoundPool.play(i + 1, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    private void updateSelectedItem(int i) {
        Settings.System.putIntForUser(getContext().getContentResolver(), "miui_popup_sound_index", i, -2);
    }

    public GridView getGridView() {
        return this.mGridView;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundColor(0);
        this.mGridView = (GridView) view.findViewById(R.id.popup_gridview);
        PopupCameraSoundListAdapter popupCameraSoundListAdapter = new PopupCameraSoundListAdapter(this.mContext, this, this.mImages, this.mTexts);
        this.mAdapter = popupCameraSoundListAdapter;
        this.mGridView.setAdapter((ListAdapter) popupCameraSoundListAdapter);
        int intForUser = Settings.System.getIntForUser(getContext().getContentResolver(), "miui_popup_sound_index", 0, -2);
        this.mLastIndex = intForUser;
        this.mAdapter.setChooseItem(intForUser);
        final boolean isEnabled = getGridView().isEnabled();
        this.mGridView.setSelector(new ColorDrawable(0));
        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.popup.PopupCameraSoundChoosePreference.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                if (isEnabled) {
                    PopupCameraSoundChoosePreference.this.mAdapter.setChooseItem(i);
                    PopupCameraSoundChoosePreference.this.onSoundSelected(i);
                }
            }
        });
    }

    public void onDestroy() {
        SoundPool soundPool = this.mSoundPool;
        if (soundPool != null) {
            soundPool.release();
        }
    }

    public void onSoundSelected(final int i) {
        if (this.mLastIndex != i) {
            updateSelectedItem(i);
        }
        this.mLastIndex = i;
        this.mHandler.removeCallbacks(this.mTask);
        this.mTask = new Runnable() { // from class: com.android.settings.popup.PopupCameraSoundChoosePreference$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PopupCameraSoundChoosePreference.this.lambda$onSoundSelected$0(i);
            }
        };
        realPlaySound(i * 2);
        this.mHandler.postDelayed(this.mTask, 1000L);
    }
}
