package com.android.settings.emergency.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R;
import com.android.settings.emergency.service.LocationService;
import com.android.settings.emergency.ui.view.FullScreenDialog;
import com.android.settings.emergency.util.Config;
import com.android.settings.search.tree.SecuritySettingsTree;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.os.Build;
import miui.provider.ExtraContacts;
import miui.telephony.SubscriptionManager;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.ViewProperty;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class SosExitAlertActivity extends AppCompatActivity {
    private List<String> listNames;
    private List<String> listPhoneNunbers;
    private Button mBtnHideSos;
    private FullScreenDialog mFingerVerifyDialog;
    private RelativeLayout mGpContactSecond;
    private RelativeLayout mGpContactThird;
    private LinearLayout mGroupCn110Call;
    private LinearLayout mGroupCn119Call;
    private LinearLayout mGroupCn120Call;
    private LinearLayout mGroupCnSosCommonCall;
    private RelativeLayout mGroupCnSosEl;
    private ImageView mIvSosContactCallFirst;
    private ImageView mIvSosContactCallSecond;
    private ImageView mIvSosContactCallThird;
    private RelativeLayout mSosDialogLayout;
    private TextView mTvSosContactNameFirst;
    private TextView mTvSosContactNameSecond;
    private TextView mTvSosContactNameThird;
    private TextView mTvSosContactPhoneFirst;
    private TextView mTvSosContactPhoneSecond;
    private TextView mTvSosContactPhoneThird;
    private View mView;

    /* loaded from: classes.dex */
    private static class MyTransitionListener extends TransitionListener {
        private WeakReference<SosExitAlertActivity> reference;

        private MyTransitionListener(SosExitAlertActivity sosExitAlertActivity) {
            this.reference = new WeakReference<>(sosExitAlertActivity);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            SosExitAlertActivity sosExitAlertActivity = this.reference.get();
            if (sosExitAlertActivity != null) {
                sosExitAlertActivity.mFingerVerifyDialog.dismissWithoutAnimation();
                sosExitAlertActivity.finish();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void call(String str) {
        try {
            Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.fromParts("tel", str, null));
            SubscriptionManager.putSlotIdExtra(intent, 1);
            intent.putExtra("com.android.phone.extra.slot", 1);
            intent.setPackage("com.android.server.telecom");
            intent.setFlags(335544320);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("SOS-ExitAlertActivity", e.toString());
        }
    }

    private static String encryptPhone(String str) {
        String replace = str.replace(" ", "");
        if (replace.length() <= 6) {
            return replace;
        }
        StringBuilder sb = new StringBuilder(replace);
        sb.replace((replace.length() / 2) - 2, (replace.length() / 2) + 2, " **** ");
        return sb.toString();
    }

    private void initDatas() {
        getWindow().addFlags(524288);
        String sosEmergencyContacts = Config.getSosEmergencyContacts(this);
        String sosEmergencyContactNames = Config.getSosEmergencyContactNames(this);
        if (TextUtils.isEmpty(sosEmergencyContacts)) {
            return;
        }
        this.listPhoneNunbers = new ArrayList(Arrays.asList(sosEmergencyContacts.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)));
        this.listNames = new ArrayList();
        if (TextUtils.isEmpty(sosEmergencyContactNames)) {
            return;
        }
        this.listNames = new ArrayList(Arrays.asList(sosEmergencyContactNames.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)));
    }

    private void initListeners() {
        this.mBtnHideSos.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    SosExitAlertActivity.this.stopService(new Intent(SosExitAlertActivity.this, LocationService.class));
                } catch (Exception e) {
                    Log.e("SOS-ExitAlertActivity", e.toString());
                }
                AnimState animState = new AnimState("from");
                ViewProperty viewProperty = ViewProperty.SCALE_X;
                AnimState add = animState.add(viewProperty, 1.0d);
                ViewProperty viewProperty2 = ViewProperty.SCALE_Y;
                AnimState add2 = add.add(viewProperty2, 1.0d);
                ViewProperty viewProperty3 = ViewProperty.ALPHA;
                AnimState add3 = add2.add(viewProperty3, 1.0d);
                AnimState add4 = new AnimState("to").add(viewProperty, 0.949999988079071d).add(viewProperty2, 0.949999988079071d).add(viewProperty3, 0.0d);
                AnimConfig minDuration = new AnimConfig().setEase(-2, 0.9f, 0.3f).setMinDuration(500L);
                minDuration.addListeners(new MyTransitionListener());
                Folme.useAt(SosExitAlertActivity.this.mView).state().setTo(add3).to(add4, minDuration);
            }
        });
        this.mSosDialogLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AnimState animState = new AnimState("from");
                ViewProperty viewProperty = ViewProperty.SCALE_X;
                AnimState add = animState.add(viewProperty, 1.0d);
                ViewProperty viewProperty2 = ViewProperty.SCALE_Y;
                AnimState add2 = add.add(viewProperty2, 1.0d);
                ViewProperty viewProperty3 = ViewProperty.ALPHA;
                AnimState add3 = add2.add(viewProperty3, 1.0d);
                AnimState add4 = new AnimState("to").add(viewProperty, 0.949999988079071d).add(viewProperty2, 0.949999988079071d).add(viewProperty3, 0.0d);
                AnimConfig minDuration = new AnimConfig().setEase(-2, 0.9f, 0.3f).setMinDuration(500L);
                minDuration.addListeners(new MyTransitionListener());
                Folme.useAt(SosExitAlertActivity.this.mView).state().setTo(add3).to(add4, minDuration);
            }
        });
        this.mTvSosContactNameFirst.setText(this.listNames.get(0));
        this.mTvSosContactPhoneFirst.setText(encryptPhone(this.listPhoneNunbers.get(0)));
        this.mIvSosContactCallFirst.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SosExitAlertActivity sosExitAlertActivity = SosExitAlertActivity.this;
                sosExitAlertActivity.call((String) sosExitAlertActivity.listPhoneNunbers.get(0));
            }
        });
        if (this.listPhoneNunbers.size() > 1) {
            if (this.listNames.size() > 1) {
                this.mTvSosContactNameSecond.setText(this.listNames.get(1));
            }
            this.mTvSosContactPhoneSecond.setText(encryptPhone(this.listPhoneNunbers.get(1)));
            this.mIvSosContactCallSecond.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SosExitAlertActivity sosExitAlertActivity = SosExitAlertActivity.this;
                    sosExitAlertActivity.call((String) sosExitAlertActivity.listPhoneNunbers.get(1));
                }
            });
        } else {
            this.mGpContactSecond.setVisibility(8);
        }
        if (this.listPhoneNunbers.size() > 2) {
            if (this.listNames.size() > 2) {
                this.mTvSosContactNameThird.setText(this.listNames.get(2));
            }
            this.mTvSosContactPhoneThird.setText(encryptPhone(this.listPhoneNunbers.get(2)));
            this.mIvSosContactCallThird.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SosExitAlertActivity sosExitAlertActivity = SosExitAlertActivity.this;
                    sosExitAlertActivity.call((String) sosExitAlertActivity.listPhoneNunbers.get(2));
                }
            });
        } else {
            this.mGpContactThird.setVisibility(8);
        }
        this.mGroupCn110Call.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SosExitAlertActivity.this.call("110");
            }
        });
        this.mGroupCn120Call.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SosExitAlertActivity.this.call("120");
            }
        });
        this.mGroupCn119Call.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SosExitAlertActivity.this.call("119");
            }
        });
        this.mGroupCnSosEl.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyActivity");
                intent.setFlags(268468224);
                SosExitAlertActivity.this.startActivity(intent);
            }
        });
    }

    private void initViews() {
        this.mFingerVerifyDialog = new FullScreenDialog(this, R.style.Fod_Dialog_Fullscreen);
        this.mView = getLayoutInflater().inflate(R.layout.sos_dialog_view, (ViewGroup) null);
        this.mFingerVerifyDialog.show();
        this.mFingerVerifyDialog.setContentView(this.mView);
        AnimState animState = new AnimState("from");
        ViewProperty viewProperty = ViewProperty.SCALE_X;
        AnimState add = animState.add(viewProperty, 0.949999988079071d);
        ViewProperty viewProperty2 = ViewProperty.SCALE_Y;
        AnimState add2 = add.add(viewProperty2, 0.949999988079071d);
        ViewProperty viewProperty3 = ViewProperty.ALPHA;
        AnimState add3 = add2.add(viewProperty3, 0.0d);
        Folme.useAt(this.mView).state().setTo(add3).to(new AnimState("to").add(viewProperty, 1.0d).add(viewProperty2, 1.0d).add(viewProperty3, 1.0d), new AnimConfig().setEase(-2, 0.9f, 0.3f).setMinDuration(500L));
        this.mSosDialogLayout = (RelativeLayout) this.mView.findViewById(R.id.sos_dialog_layout);
        this.mGroupCnSosEl = (RelativeLayout) this.mView.findViewById(R.id.group_cn_sos_el);
        this.mGroupCnSosCommonCall = (LinearLayout) this.mView.findViewById(R.id.group_cn_sos_common_call);
        this.mIvSosContactCallFirst = (ImageView) this.mView.findViewById(R.id.iv_sos_contact_call_first);
        this.mIvSosContactCallSecond = (ImageView) this.mView.findViewById(R.id.iv_sos_contact_call_second);
        this.mIvSosContactCallThird = (ImageView) this.mView.findViewById(R.id.iv_sos_contact_call_third);
        this.mTvSosContactNameFirst = (TextView) this.mView.findViewById(R.id.tv_sos_contact_name_first);
        this.mTvSosContactNameSecond = (TextView) this.mView.findViewById(R.id.tv_sos_contact_name_second);
        this.mTvSosContactNameThird = (TextView) this.mView.findViewById(R.id.tv_sos_contact_name_third);
        this.mTvSosContactPhoneFirst = (TextView) this.mView.findViewById(R.id.tv_sos_contact_phone_first);
        this.mTvSosContactPhoneSecond = (TextView) this.mView.findViewById(R.id.tv_sos_contact_phone_second);
        this.mTvSosContactPhoneThird = (TextView) this.mView.findViewById(R.id.tv_sos_contact_phone_third);
        this.mGroupCn119Call = (LinearLayout) this.mView.findViewById(R.id.group_cn_119_call);
        this.mGroupCn120Call = (LinearLayout) this.mView.findViewById(R.id.group_cn_120_call);
        this.mGroupCn110Call = (LinearLayout) this.mView.findViewById(R.id.group_cn_110_call);
        this.mGpContactSecond = (RelativeLayout) this.mView.findViewById(R.id.gp_contact_second);
        this.mGpContactThird = (RelativeLayout) this.mView.findViewById(R.id.gp_contact_third);
        this.mBtnHideSos = (Button) this.mView.findViewById(R.id.btn_hide_sos);
        if ("zh".equals(getResources().getConfiguration().locale.getLanguage())) {
            this.mBtnHideSos.setTextSize(13.0f);
        } else {
            this.mBtnHideSos.setTextSize(12.0f);
        }
        final TextView textView = (TextView) this.mView.findViewById(R.id.sos_dec);
        final LottieAnimationView lottieAnimationView = (LottieAnimationView) this.mView.findViewById(R.id.sos_json_anim);
        lottieAnimationView.setAnimation(R.raw.sos);
        lottieAnimationView.setImageAssetsFolder("images");
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.emergency.ui.SosExitAlertActivity.10
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                float lineRight = textView.getLayout().getLineRight(textView.getLineCount() - 1);
                int height = textView.getHeight() / textView.getLineCount();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lottieAnimationView.getLayoutParams();
                layoutParams.leftMargin = ((int) lineRight) + SosExitAlertActivity.this.getResources().getDimensionPixelSize(R.dimen.sos_lottie_marginLeft);
                layoutParams.topMargin = (height * (textView.getLineCount() - 1)) + textView.getPaddingTop() + SosExitAlertActivity.this.getResources().getDimensionPixelSize(R.dimen.sos_lottie_marginTop);
                lottieAnimationView.setLayoutParams(layoutParams);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initDatas();
        initViews();
        initListeners();
        if (Build.IS_INTERNATIONAL_BUILD) {
            this.mGroupCnSosEl.setVisibility(8);
            this.mGroupCnSosCommonCall.setVisibility(8);
        }
    }
}
