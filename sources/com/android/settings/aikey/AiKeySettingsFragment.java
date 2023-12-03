package com.android.settings.aikey;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.aikey.PreferenceHelper;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class AiKeySettingsFragment extends Fragment implements View.OnClickListener {
    private View aiDoubleView;
    private View aiLongPress;
    private View aiSingleView;
    private AlertDialog doubleClickDialog;
    private AlertDialog longPressDialog;
    private Context mContext;
    private AlertDialog singleClickDialog;
    private TextView tvDoubleSummary;
    private TextView tvLongPressSummary;
    private TextView tvSingleSummary;

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.aiSingleView == view) {
            this.singleClickDialog.show();
        } else if (this.aiLongPress == view) {
            this.longPressDialog.show();
        } else if (this.aiDoubleView == view) {
            this.doubleClickDialog.show();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_ai_key_settings, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.aiDoubleView = view.findViewById(R.id.ai_double_click);
        this.aiSingleView = view.findViewById(R.id.ai_single_click);
        this.aiLongPress = view.findViewById(R.id.ai_long_press);
        this.tvSingleSummary = (TextView) view.findViewById(R.id.single_click_summary);
        this.tvLongPressSummary = (TextView) view.findViewById(R.id.long_press_summary);
        this.tvDoubleSummary = (TextView) view.findViewById(R.id.double_click_summary);
        int pressAiButtonSettings = PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(this.mContext, "key_single_click_ai_button_settings");
        int pressAiButtonSettings2 = PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(this.mContext, "key_long_press_ai_button_settings");
        int pressAiButtonSettings3 = PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(this.mContext, "key_double_click_ai_button_settings");
        final String[] stringArray = getResources().getStringArray(R.array.ai_item_action_array);
        this.tvSingleSummary.setText(stringArray[pressAiButtonSettings < 1 ? (char) 0 : (char) 1]);
        this.tvLongPressSummary.setText(stringArray[pressAiButtonSettings2 < 1 ? (char) 0 : (char) 1]);
        this.tvDoubleSummary.setText(stringArray[pressAiButtonSettings3 >= 1 ? (char) 1 : (char) 0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        int i = R.string.ai_key;
        this.singleClickDialog = builder.setTitle(i).setSingleChoiceItems(stringArray, pressAiButtonSettings, new DialogInterface.OnClickListener() { // from class: com.android.settings.aikey.AiKeySettingsFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.dismiss();
                PreferenceHelper.AiSettingsPreferenceHelper.setPressAiButtonSettings(AiKeySettingsFragment.this.mContext, "key_single_click_ai_button_settings", i2);
                AiKeySettingsFragment.this.tvSingleSummary.setText(stringArray[i2]);
            }
        }).create();
        this.longPressDialog = new AlertDialog.Builder(this.mContext).setTitle(i).setSingleChoiceItems(stringArray, pressAiButtonSettings2, new DialogInterface.OnClickListener() { // from class: com.android.settings.aikey.AiKeySettingsFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.dismiss();
                PreferenceHelper.AiSettingsPreferenceHelper.setPressAiButtonSettings(AiKeySettingsFragment.this.mContext, "key_long_press_ai_button_settings", i2);
                AiKeySettingsFragment.this.tvLongPressSummary.setText(stringArray[i2]);
            }
        }).create();
        this.doubleClickDialog = new AlertDialog.Builder(this.mContext).setTitle(i).setSingleChoiceItems(stringArray, pressAiButtonSettings3, new DialogInterface.OnClickListener() { // from class: com.android.settings.aikey.AiKeySettingsFragment.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.dismiss();
                PreferenceHelper.AiSettingsPreferenceHelper.setPressAiButtonSettings(AiKeySettingsFragment.this.mContext, "key_double_click_ai_button_settings", i2);
                AiKeySettingsFragment.this.tvDoubleSummary.setText(stringArray[i2]);
            }
        }).create();
        this.aiSingleView.setOnClickListener(this);
        this.aiLongPress.setOnClickListener(this);
        this.aiDoubleView.setOnClickListener(this);
        Context context = this.mContext;
        if (context == null || !(context instanceof SettingsActivity)) {
            return;
        }
        ((SettingsActivity) context).setTitle(i);
    }
}
