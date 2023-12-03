package com.android.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import com.android.internal.R;
import com.android.internal.util.XmlUtils;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import java.io.IOException;
import java.util.List;
import miui.payment.PaymentManager;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes2.dex */
public class HeaderUtils {
    public static void loadHeadersFromResource(Context context, int i, List<PreferenceActivity.Header> list) {
        XmlResourceParser xml;
        int next;
        List<PreferenceActivity.Header> list2;
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                xml = context.getResources().getXml(i);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        } catch (XmlPullParserException e2) {
            e = e2;
        }
        try {
            AttributeSet asAttributeSet = Xml.asAttributeSet(xml);
            do {
                next = xml.next();
                if (next == 1) {
                    break;
                }
            } while (next != 2);
            String name = xml.getName();
            if (!"preference-headers".equals(name)) {
                throw new RuntimeException("XML document must start with <preference-headers> tag; found" + name + " at " + xml.getPositionDescription());
            }
            int depth = xml.getDepth();
            Bundle bundle = null;
            while (true) {
                int next2 = xml.next();
                if (next2 == 1 || (next2 == 3 && xml.getDepth() <= depth)) {
                    break;
                } else if (next2 != 3 && next2 != 4) {
                    if ("header".equals(xml.getName())) {
                        PreferenceActivity.Header header = new PreferenceActivity.Header();
                        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(asAttributeSet, R.styleable.PreferenceHeader);
                        header.id = obtainStyledAttributes.getResourceId(1, -1);
                        TypedValue peekValue = obtainStyledAttributes.peekValue(2);
                        if (peekValue != null && peekValue.type == 3) {
                            int i2 = peekValue.resourceId;
                            if (i2 != 0) {
                                header.titleRes = i2;
                            } else {
                                header.title = peekValue.string;
                            }
                        }
                        TypedValue peekValue2 = obtainStyledAttributes.peekValue(3);
                        if (peekValue2 != null && peekValue2.type == 3) {
                            int i3 = peekValue2.resourceId;
                            if (i3 != 0) {
                                header.summaryRes = i3;
                            } else {
                                header.summary = peekValue2.string;
                            }
                        }
                        TypedValue peekValue3 = obtainStyledAttributes.peekValue(5);
                        if (peekValue3 != null && peekValue3.type == 3) {
                            int i4 = peekValue3.resourceId;
                            if (i4 != 0) {
                                header.breadCrumbTitleRes = i4;
                            } else {
                                header.breadCrumbTitle = peekValue3.string;
                            }
                        }
                        TypedValue peekValue4 = obtainStyledAttributes.peekValue(6);
                        if (peekValue4 != null && peekValue4.type == 3) {
                            int i5 = peekValue4.resourceId;
                            if (i5 != 0) {
                                header.breadCrumbShortTitleRes = i5;
                            } else {
                                header.breadCrumbShortTitle = peekValue4.string;
                            }
                        }
                        header.iconRes = obtainStyledAttributes.getResourceId(0, 0);
                        header.fragment = obtainStyledAttributes.getString(4);
                        obtainStyledAttributes.recycle();
                        if (bundle == null) {
                            bundle = new Bundle();
                        }
                        int depth2 = xml.getDepth();
                        while (true) {
                            int next3 = xml.next();
                            if (next3 == 1 || (next3 == 3 && xml.getDepth() <= depth2)) {
                                break;
                            } else if (next3 != 3 && next3 != 4) {
                                String name2 = xml.getName();
                                if (name2.equals("extra")) {
                                    context.getResources().parseBundleExtra("extra", asAttributeSet, bundle);
                                    XmlUtils.skipCurrentTag(xml);
                                } else if (name2.equals(PaymentManager.KEY_INTENT)) {
                                    header.intent = Intent.parseIntent(context.getResources(), xml, asAttributeSet);
                                } else {
                                    XmlUtils.skipCurrentTag(xml);
                                }
                            }
                        }
                        if (bundle.size() > 0) {
                            header.fragmentArguments = bundle;
                            list2 = list;
                            bundle = null;
                        } else {
                            list2 = list;
                        }
                        list2.add(header);
                    } else {
                        XmlUtils.skipCurrentTag(xml);
                    }
                }
            }
            xml.close();
        } catch (IOException e3) {
            e = e3;
            throw new RuntimeException("Error parsing headers", e);
        } catch (XmlPullParserException e4) {
            e = e4;
            throw new RuntimeException("Error parsing headers", e);
        } catch (Throwable th2) {
            th = th2;
            xmlResourceParser = xml;
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }
}
