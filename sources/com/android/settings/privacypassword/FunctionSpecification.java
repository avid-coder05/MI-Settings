package com.android.settings.privacypassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import miui.os.Build;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class FunctionSpecification extends AppCompatActivity implements View.OnClickListener {
    private ImageView mFullDown;
    private ImageView mFunctionSpecificationIcon;
    private TextView mFunctionSpecificationView;
    private PrivacyPasswordManager mPrivacyPasswordManger;
    private Button mUseImmediate;

    /*  JADX ERROR: NullPointerException in pass: RegionMakerVisitor
        java.lang.NullPointerException: Cannot read field "wordsInUse" because "set" is null
        	at java.base/java.util.BitSet.or(BitSet.java:943)
        	at jadx.core.utils.BlockUtils.getPathCross(BlockUtils.java:746)
        	at jadx.core.utils.BlockUtils.getPathCross(BlockUtils.java:825)
        	at jadx.core.dex.visitors.regions.IfMakerHelper.restructureIf(IfMakerHelper.java:91)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:715)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:156)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:95)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:739)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:156)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:95)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    private boolean isPkgExsisted(android.content.Context r2, java.lang.String r3) {
        /*
            r1 = this;
            r1 = 0
            if (r3 == 0) goto L17
            java.lang.String r0 = ""
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto Lc
            goto L17
        Lc:
            android.content.pm.PackageManager r2 = r2.getPackageManager()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L17
            android.content.pm.ApplicationInfo r2 = r2.getApplicationInfo(r3, r1)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L17
            if (r2 == 0) goto L17
            r1 = 1
        L17:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.privacypassword.FunctionSpecification.isPkgExsisted(android.content.Context, java.lang.String):boolean");
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        String stringExtra = getIntent().getStringExtra("privacy_password_function_specification");
        if (stringExtra != null) {
            BussinessSpecificationInfo bussinessSpecificationInfo = BussinessPackageInfoCache.getSpcificationInfos().get(stringExtra);
            if (bussinessSpecificationInfo == null) {
                finish();
                return;
            }
            Intent intent = new Intent(bussinessSpecificationInfo.intentAction);
            String str = bussinessSpecificationInfo.startPackage;
            if (str != null && str.contains("fileexplorer") && Build.IS_INTERNATIONAL_BUILD) {
                str = "com.mi.android.globalFileexplorer";
                if (!isPkgExsisted(this, "com.mi.android.globalFileexplorer")) {
                    str = "com.android.fileexplorer";
                }
            }
            intent.addFlags(268435456);
            intent.setPackage(str);
            startActivity(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.funcion_specification);
        this.mPrivacyPasswordManger = PrivacyPasswordManager.getInstance(this);
        this.mFunctionSpecificationView = (TextView) findViewById(R.id.function_specific);
        Button button = (Button) findViewById(R.id.use_privacy_password_immediate);
        this.mUseImmediate = button;
        button.setOnClickListener(this);
        this.mFunctionSpecificationIcon = (ImageView) findViewById(R.id.function_specific_icon);
        this.mFullDown = (ImageView) findViewById(R.id.full_down);
        String stringExtra = getIntent().getStringExtra("privacy_password_function_specification");
        if (stringExtra != null) {
            BussinessSpecificationInfo bussinessSpecificationInfo = BussinessPackageInfoCache.getSpcificationInfos().get(stringExtra);
            if (bussinessSpecificationInfo == null) {
                finish();
                return;
            }
            getAppCompatActionBar().setTitle(bussinessSpecificationInfo.actionBarTitle);
            this.mFunctionSpecificationView.setText(bussinessSpecificationInfo.specificText);
            this.mFunctionSpecificationIcon.setImageDrawable(getResources().getDrawable(bussinessSpecificationInfo.specificImage));
            if (bussinessSpecificationInfo.isGone) {
                this.mFullDown.setVisibility(8);
            }
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (this.mPrivacyPasswordManger.havePattern()) {
            return;
        }
        finish();
    }
}
