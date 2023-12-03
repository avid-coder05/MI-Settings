package miui.cta;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.miui.system.internal.R;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes3.dex */
public class CTAConfig {
    private static final String ABBREVIATE_PREFIX = ".";
    public static final CTAConfig EMPTY = new CTAConfig();
    private static final String TAG = "CTAConfig";
    private static final String TAG_ACTIVITIES = "activities";
    private static final String TAG_ACTIVITY = "activity";
    private ActivitiesNode mActivitiesNode;
    private HashMap<String, ActivityNode> mActivityNodes;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ActivitiesNode {
        boolean enabled;
        int messageId;
        boolean optional;
        int permission;

        private ActivitiesNode() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ActivityNode extends ActivitiesNode {
        String name;

        private ActivityNode() {
            super();
        }
    }

    /* loaded from: classes3.dex */
    public static class MatchResult {
        int messageId;
        boolean optional;
        int permission;

        MatchResult(ActivitiesNode activitiesNode) {
            this.optional = activitiesNode.optional;
            this.messageId = activitiesNode.messageId;
            this.permission = activitiesNode.permission;
        }
    }

    private CTAConfig() {
        this.mActivitiesNode = new ActivitiesNode();
        this.mActivityNodes = new HashMap<>();
    }

    public CTAConfig(Context context, XmlResourceParser xmlResourceParser) {
        this();
        parseConfig(context, xmlResourceParser);
    }

    private void parseActivities(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CTAActivitiesConfig);
        this.mActivitiesNode.enabled = obtainStyledAttributes.getBoolean(R.styleable.CTAActivitiesConfig_enabled, true);
        this.mActivitiesNode.optional = obtainStyledAttributes.getBoolean(R.styleable.CTAActivitiesConfig_optional, false);
        this.mActivitiesNode.messageId = obtainStyledAttributes.getResourceId(R.styleable.CTAActivitiesConfig_message, 0);
        this.mActivitiesNode.permission = obtainStyledAttributes.getInt(R.styleable.CTAActivitiesConfig_permission, 0);
        obtainStyledAttributes.recycle();
    }

    private void parseActivity(Context context, AttributeSet attributeSet) {
        ActivityNode activityNode = new ActivityNode();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CTAActivityConfig);
        activityNode.name = obtainStyledAttributes.getString(R.styleable.CTAActivityConfig_name);
        if (obtainStyledAttributes.hasValue(R.styleable.CTAActivityConfig_enabled)) {
            activityNode.enabled = obtainStyledAttributes.getBoolean(R.styleable.CTAActivityConfig_enabled, true);
        } else {
            activityNode.enabled = this.mActivitiesNode.enabled;
        }
        if (obtainStyledAttributes.hasValue(R.styleable.CTAActivityConfig_optional)) {
            activityNode.optional = obtainStyledAttributes.getBoolean(R.styleable.CTAActivityConfig_optional, false);
        } else {
            activityNode.optional = this.mActivitiesNode.optional;
        }
        activityNode.messageId = obtainStyledAttributes.getResourceId(R.styleable.CTAActivityConfig_message, 0);
        int i = obtainStyledAttributes.getInt(R.styleable.CTAActivityConfig_permission, 0);
        activityNode.permission = i;
        if (activityNode.messageId == 0 && i == 0) {
            ActivitiesNode activitiesNode = this.mActivitiesNode;
            activityNode.messageId = activitiesNode.messageId;
            activityNode.permission = activitiesNode.permission;
        }
        obtainStyledAttributes.recycle();
        String str = activityNode.name;
        if (!TextUtils.isEmpty(str) && activityNode.name.startsWith(ABBREVIATE_PREFIX)) {
            str = context.getPackageName() + str;
        }
        this.mActivityNodes.put(str, activityNode);
    }

    private void parseConfig(Context context, XmlResourceParser xmlResourceParser) {
        AttributeSet asAttributeSet = Xml.asAttributeSet(xmlResourceParser);
        try {
            int next = xmlResourceParser.next();
            boolean z = false;
            while (next != 1) {
                if (next == 2) {
                    String name = xmlResourceParser.getName();
                    if (TAG_ACTIVITIES.equals(name)) {
                        parseActivities(context, asAttributeSet);
                        z = true;
                    } else if (z && TAG_ACTIVITY.equals(name)) {
                        parseActivity(context, asAttributeSet);
                    }
                } else if (next == 3 && TAG_ACTIVITIES.equals(xmlResourceParser.getName())) {
                    z = false;
                }
                next = xmlResourceParser.next();
            }
        } catch (IOException e) {
            Log.e(TAG, "Fail to parse CTA config", e);
        } catch (XmlPullParserException e2) {
            Log.e(TAG, "Fail to parse CTA config", e2);
        }
    }

    public boolean canMatch() {
        if (this.mActivitiesNode.enabled) {
            return true;
        }
        for (ActivityNode activityNode : this.mActivityNodes.values()) {
            if (activityNode.enabled && !TextUtils.isEmpty(activityNode.name)) {
                return true;
            }
        }
        return false;
    }

    public MatchResult match(Class<? extends Activity> cls) {
        ActivityNode activityNode = this.mActivityNodes.get(cls.getName());
        if (activityNode != null) {
            if (activityNode.enabled) {
                return new MatchResult(activityNode);
            }
            return null;
        }
        ActivitiesNode activitiesNode = this.mActivitiesNode;
        if (activitiesNode.enabled) {
            return new MatchResult(activitiesNode);
        }
        return null;
    }
}
