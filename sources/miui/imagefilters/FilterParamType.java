package miui.imagefilters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes3.dex */
public @interface FilterParamType {

    /* loaded from: classes3.dex */
    public enum ParamType {
        DEFAULT,
        ICON_SIZE
    }

    ParamType value() default ParamType.DEFAULT;
}
