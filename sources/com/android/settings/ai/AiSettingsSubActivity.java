package com.android.settings.ai;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AiSettingsSubActivity extends AppCompatActivity {
    AiSettingsSubAdapter mAiSettingsSubAdapter;
    private ImageView mBack;
    private String mButtonType;
    LinearLayoutColorDivider mLinearLayoutColorDivider;
    private RecyclerView mRecyclerView;
    private TextView mTitle;

    private void initData() {
        String stringExtra = getIntent().getStringExtra("type");
        this.mButtonType = stringExtra;
        this.mAiSettingsSubAdapter = new AiSettingsSubAdapter(this, DataFactory.generateItems(this, stringExtra), this.mButtonType);
    }

    private void initUI() {
        this.mRecyclerView = (RecyclerView) findViewById(R.id.rcv_custom_op);
        this.mBack = (ImageView) findViewById(R.id.ai_settings_back);
        this.mTitle = (TextView) findViewById(R.id.ai_settings_sub_title);
        if ("key_double_click_ai_button_settings".equals(this.mButtonType)) {
            this.mTitle.setText(getString(R.string.ai_settings_double_click_category));
        } else if ("key_single_click_ai_button_settings".equals(this.mButtonType)) {
            this.mTitle.setText(getString(R.string.single_press_AI_button));
        } else {
            this.mTitle.setText(getString(R.string.ai_settings_long_click_category));
        }
        this.mLinearLayoutColorDivider = new LinearLayoutColorDivider(getResources(), R.color.white, R.dimen.ai_settings_divider_size, 1);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mRecyclerView.setAdapter(this.mAiSettingsSubAdapter);
        this.mRecyclerView.addItemDecoration(this.mLinearLayoutColorDivider);
        this.mBack.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ai.AiSettingsSubActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AiSettingsSubActivity.this.finish();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ai_settings_sub_activity);
        initData();
        initUI();
    }
}
