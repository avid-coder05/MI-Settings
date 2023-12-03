package com.miui.maml;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.search.SearchUpdater;
import com.android.settings.search.provider.SettingsProvider;
import com.miui.maml.NotifierManager;
import com.miui.maml.ObjectFactory;
import com.miui.maml.SoundManager;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.SensorBinder;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.data.VariableType;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.AnimConfigElement;
import com.miui.maml.elements.AnimStateElement;
import com.miui.maml.elements.AnimatedScreenElement;
import com.miui.maml.elements.ElementGroup;
import com.miui.maml.elements.FunctionElement;
import com.miui.maml.elements.GraphicsElement;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.elements.filament.PhysicallyBasedRenderingElement;
import com.miui.maml.elements.video.VideoElement;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.IntentInfo;
import com.miui.maml.util.MobileDataUtils;
import com.miui.maml.util.ReflectionHelper;
import com.miui.maml.util.Task;
import com.miui.maml.util.Utils;
import com.miui.maml.util.Variable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.provider.ExtraTelephony;
import miui.telephony.MiuiHeDuoHaoUtil;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public abstract class ActionCommand {
    private static final Handler mHandler = new Handler(Looper.getMainLooper());
    protected ScreenElement mScreenElement;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.ActionCommand$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$AnimConfigCommand$Type;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$AnimStateCommand$Type;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$PbrCommand$CommandType;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$VariableBinderCommand$Command;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ActionCommand$VideoCommand$CommandType;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$SoundManager$Command;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$VariableType;

        static {
            int[] iArr = new int[VideoCommand.CommandType.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$VideoCommand$CommandType = iArr;
            try {
                iArr[VideoCommand.CommandType.PLAY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$VideoCommand$CommandType[VideoCommand.CommandType.PAUSE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$VideoCommand$CommandType[VideoCommand.CommandType.SEEK_TO.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$VideoCommand$CommandType[VideoCommand.CommandType.SET_VOLUME.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$VideoCommand$CommandType[VideoCommand.CommandType.CONFIG.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            int[] iArr2 = new int[FolmeCommand.Type.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type = iArr2;
            try {
                iArr2[FolmeCommand.Type.TO.ordinal()] = 1;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type[FolmeCommand.Type.SET_TO.ordinal()] = 2;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type[FolmeCommand.Type.FROM_TO.ordinal()] = 3;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type[FolmeCommand.Type.CANCEL.ordinal()] = 4;
            } catch (NoSuchFieldError unused9) {
            }
            int[] iArr3 = new int[AnimConfigCommand.Type.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$AnimConfigCommand$Type = iArr3;
            try {
                iArr3[AnimConfigCommand.Type.UPDATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimConfigCommand$Type[AnimConfigCommand.Type.REMOVE.ordinal()] = 2;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimConfigCommand$Type[AnimConfigCommand.Type.CLEAR.ordinal()] = 3;
            } catch (NoSuchFieldError unused12) {
            }
            int[] iArr4 = new int[AnimStateCommand.Type.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$AnimStateCommand$Type = iArr4;
            try {
                iArr4[AnimStateCommand.Type.UPDATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimStateCommand$Type[AnimStateCommand.Type.REMOVE.ordinal()] = 2;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimStateCommand$Type[AnimStateCommand.Type.CLEAR.ordinal()] = 3;
            } catch (NoSuchFieldError unused15) {
            }
            int[] iArr5 = new int[AnimationCommand.CommandType.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType = iArr5;
            try {
                iArr5[AnimationCommand.CommandType.PLAY.ordinal()] = 1;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType[AnimationCommand.CommandType.PAUSE.ordinal()] = 2;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType[AnimationCommand.CommandType.RESUME.ordinal()] = 3;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType[AnimationCommand.CommandType.PLAY_WITH_PARAMS.ordinal()] = 4;
            } catch (NoSuchFieldError unused19) {
            }
            int[] iArr6 = new int[TickListenerCommand.CommandType.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType = iArr6;
            try {
                iArr6[TickListenerCommand.CommandType.SET.ordinal()] = 1;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType[TickListenerCommand.CommandType.UNSET.ordinal()] = 2;
            } catch (NoSuchFieldError unused21) {
            }
            int[] iArr7 = new int[GraphicsCommand.CommandType.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType = iArr7;
            try {
                iArr7[GraphicsCommand.CommandType.LINE_TO.ordinal()] = 1;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.MOVE_TO.ordinal()] = 2;
            } catch (NoSuchFieldError unused23) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.CURVE_TO.ordinal()] = 3;
            } catch (NoSuchFieldError unused24) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.DRAW_RECT.ordinal()] = 4;
            } catch (NoSuchFieldError unused25) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.BEGIN_FILL.ordinal()] = 5;
            } catch (NoSuchFieldError unused26) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.LINE_STYLE.ordinal()] = 6;
            } catch (NoSuchFieldError unused27) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.DRAW_CIRCLE.ordinal()] = 7;
            } catch (NoSuchFieldError unused28) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.DRAW_ELLIPSE.ordinal()] = 8;
            } catch (NoSuchFieldError unused29) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.CUBIC_CURVE_TO.ordinal()] = 9;
            } catch (NoSuchFieldError unused30) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.DRAW_ROUND_RECT.ordinal()] = 10;
            } catch (NoSuchFieldError unused31) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.LINE_GRADIENT_STYLE.ordinal()] = 11;
            } catch (NoSuchFieldError unused32) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.BEGIN_GRADIENT_FILL.ordinal()] = 12;
            } catch (NoSuchFieldError unused33) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.CREATE_GRADIENT_BOX.ordinal()] = 13;
            } catch (NoSuchFieldError unused34) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[GraphicsCommand.CommandType.SET_RENDER_LISTENER.ordinal()] = 14;
            } catch (NoSuchFieldError unused35) {
            }
            int[] iArr8 = new int[PbrCommand.CommandType.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$PbrCommand$CommandType = iArr8;
            try {
                iArr8[PbrCommand.CommandType.UPDATE_UNIFORM.ordinal()] = 1;
            } catch (NoSuchFieldError unused36) {
            }
            int[] iArr9 = new int[SensorBinderCommand.CommandType.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType = iArr9;
            try {
                iArr9[SensorBinderCommand.CommandType.TURN_ON.ordinal()] = 1;
            } catch (NoSuchFieldError unused37) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType[SensorBinderCommand.CommandType.TURN_OFF.ordinal()] = 2;
            } catch (NoSuchFieldError unused38) {
            }
            int[] iArr10 = new int[TargetCommand.TargetType.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType = iArr10;
            try {
                iArr10[TargetCommand.TargetType.SCREEN_ELEMENT.ordinal()] = 1;
            } catch (NoSuchFieldError unused39) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[TargetCommand.TargetType.VARIABLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused40) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[TargetCommand.TargetType.ANIMATION_ITEM.ordinal()] = 3;
            } catch (NoSuchFieldError unused41) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[TargetCommand.TargetType.VARIABLE_BINDER.ordinal()] = 4;
            } catch (NoSuchFieldError unused42) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[TargetCommand.TargetType.CONSTRUCTOR.ordinal()] = 5;
            } catch (NoSuchFieldError unused43) {
            }
            int[] iArr11 = new int[AnimationProperty.Type.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type = iArr11;
            try {
                iArr11[AnimationProperty.Type.PLAY.ordinal()] = 1;
            } catch (NoSuchFieldError unused44) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type[AnimationProperty.Type.PAUSE.ordinal()] = 2;
            } catch (NoSuchFieldError unused45) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type[AnimationProperty.Type.RESUME.ordinal()] = 3;
            } catch (NoSuchFieldError unused46) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type[AnimationProperty.Type.PLAY_WITH_PARAMS.ordinal()] = 4;
            } catch (NoSuchFieldError unused47) {
            }
            int[] iArr12 = new int[SoundManager.Command.values().length];
            $SwitchMap$com$miui$maml$SoundManager$Command = iArr12;
            try {
                iArr12[SoundManager.Command.Play.ordinal()] = 1;
            } catch (NoSuchFieldError unused48) {
            }
            try {
                $SwitchMap$com$miui$maml$SoundManager$Command[SoundManager.Command.Pause.ordinal()] = 2;
            } catch (NoSuchFieldError unused49) {
            }
            try {
                $SwitchMap$com$miui$maml$SoundManager$Command[SoundManager.Command.Resume.ordinal()] = 3;
            } catch (NoSuchFieldError unused50) {
            }
            try {
                $SwitchMap$com$miui$maml$SoundManager$Command[SoundManager.Command.Stop.ordinal()] = 4;
            } catch (NoSuchFieldError unused51) {
            }
            int[] iArr13 = new int[IntentCommand.IntentType.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType = iArr13;
            try {
                iArr13[IntentCommand.IntentType.Activity.ordinal()] = 1;
            } catch (NoSuchFieldError unused52) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType[IntentCommand.IntentType.Broadcast.ordinal()] = 2;
            } catch (NoSuchFieldError unused53) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType[IntentCommand.IntentType.Service.ordinal()] = 3;
            } catch (NoSuchFieldError unused54) {
            }
            try {
                $SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType[IntentCommand.IntentType.Var.ordinal()] = 4;
            } catch (NoSuchFieldError unused55) {
            }
            int[] iArr14 = new int[VariableBinderCommand.Command.values().length];
            $SwitchMap$com$miui$maml$ActionCommand$VariableBinderCommand$Command = iArr14;
            try {
                iArr14[VariableBinderCommand.Command.Refresh.ordinal()] = 1;
            } catch (NoSuchFieldError unused56) {
            }
            int[] iArr15 = new int[VariableType.values().length];
            $SwitchMap$com$miui$maml$data$VariableType = iArr15;
            try {
                iArr15[VariableType.NUM.ordinal()] = 1;
            } catch (NoSuchFieldError unused57) {
            }
            try {
                $SwitchMap$com$miui$maml$data$VariableType[VariableType.STR.ordinal()] = 2;
            } catch (NoSuchFieldError unused58) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ActionPerformCommand extends TargetCommand {
        private String mAction;
        private Expression mActionExp;

        public ActionPerformCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            String attribute = element.getAttribute("action");
            this.mAction = attribute;
            if (TextUtils.isEmpty(attribute)) {
                this.mAction = null;
                this.mActionExp = Expression.build(getVariables(), element.getAttribute("actionExp"));
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void doPerform() {
            String evaluateStr;
            ScreenElement screenElement = (ScreenElement) getTarget();
            if (screenElement == null) {
                return;
            }
            String str = this.mAction;
            if (str != null) {
                screenElement.performAction(str);
                return;
            }
            Expression expression = this.mActionExp;
            if (expression == null || (evaluateStr = expression.evaluateStr()) == null) {
                return;
            }
            screenElement.performAction(evaluateStr);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class AnimConfigCommand extends TargetCommand {
        private Expression mAttr;
        private Type mCommand;
        private Expression mSubName;
        private Expression[] mValuesExp;

        /* loaded from: classes2.dex */
        enum Type {
            UPDATE,
            REMOVE,
            CLEAR,
            INVALID
        }

        public AnimConfigCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mSubName = Expression.build(getVariables(), element.getAttribute("subNameExp"));
            this.mAttr = Expression.build(getVariables(), element.getAttribute("attrExp"));
            this.mValuesExp = Expression.buildMultiple(getVariables(), element.getAttribute("valuesExp"));
            String attribute = element.getAttribute("command");
            attribute.hashCode();
            char c = 65535;
            switch (attribute.hashCode()) {
                case -934610812:
                    if (attribute.equals("remove")) {
                        c = 0;
                        break;
                    }
                    break;
                case -838846263:
                    if (attribute.equals("update")) {
                        c = 1;
                        break;
                    }
                    break;
                case 94746189:
                    if (attribute.equals("clear")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.mCommand = Type.REMOVE;
                    return;
                case 1:
                    this.mCommand = Type.UPDATE;
                    return;
                case 2:
                    this.mCommand = Type.CLEAR;
                    return;
                default:
                    this.mCommand = Type.INVALID;
                    return;
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            String str;
            Object target = getTarget();
            if (target == null || !(target instanceof AnimConfigElement)) {
                return;
            }
            AnimConfigElement animConfigElement = (AnimConfigElement) target;
            StringBuilder sb = new StringBuilder();
            sb.append(this.mTargetName);
            if (this.mSubName != null) {
                str = "." + this.mSubName.evaluateStr();
            } else {
                str = "";
            }
            sb.append(str);
            String sb2 = sb.toString();
            Expression expression = this.mAttr;
            String evaluateStr = expression != null ? expression.evaluateStr() : "";
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$AnimConfigCommand$Type[this.mCommand.ordinal()];
            if (i == 1) {
                Expression[] expressionArr = this.mValuesExp;
                if (expressionArr != null) {
                    animConfigElement.updateConfigData(sb2, evaluateStr, expressionArr);
                }
            } else if (i == 2) {
                animConfigElement.removeConfigData(sb2, evaluateStr);
            } else if (i != 3) {
            } else {
                animConfigElement.clearConfigData();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class AnimStateCommand extends TargetCommand {
        private Expression mAttrArrayName;
        private String[] mAttrs;
        private Expression[] mAttrsExp;
        private Type mCommand;
        private boolean mIsAttrsValid;
        private boolean mIsValuesValid;
        private Expression mValueArrayName;
        private double[] mValues;
        private Expression[] mValuesExp;

        /* loaded from: classes2.dex */
        enum Type {
            UPDATE,
            REMOVE,
            CLEAR,
            INVALID
        }

        public AnimStateCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            Variables variables = getVariables();
            this.mValueArrayName = Expression.build(variables, element.getAttribute("valueArrayNameExp"));
            this.mAttrArrayName = Expression.build(variables, element.getAttribute("attrArrayNameExp"));
            this.mValuesExp = Expression.buildMultiple(variables, element.getAttribute("valuesExp"));
            this.mAttrsExp = Expression.buildMultiple(variables, element.getAttribute("attrsExp"));
            this.mIsValuesValid = isExpressionsValid(this.mValuesExp);
            boolean isExpressionsValid = isExpressionsValid(this.mAttrsExp);
            this.mIsAttrsValid = isExpressionsValid;
            if (isExpressionsValid) {
                this.mAttrs = new String[this.mAttrsExp.length];
            }
            if (this.mIsValuesValid) {
                this.mValues = new double[this.mValuesExp.length];
            }
            String attribute = element.getAttribute("command");
            attribute.hashCode();
            char c = 65535;
            switch (attribute.hashCode()) {
                case -934610812:
                    if (attribute.equals("remove")) {
                        c = 0;
                        break;
                    }
                    break;
                case -838846263:
                    if (attribute.equals("update")) {
                        c = 1;
                        break;
                    }
                    break;
                case 94746189:
                    if (attribute.equals("clear")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.mCommand = Type.REMOVE;
                    return;
                case 1:
                    this.mCommand = Type.UPDATE;
                    return;
                case 2:
                    this.mCommand = Type.CLEAR;
                    return;
                default:
                    this.mCommand = Type.INVALID;
                    return;
            }
        }

        private void remove(AnimStateElement animStateElement) {
            Object obj;
            String[] strArr = this.mAttrs;
            if (strArr == null) {
                if (this.mAttrArrayName == null || (obj = getVariables().get(this.mAttrArrayName.evaluateStr())) == null || !(obj instanceof String[])) {
                    return;
                }
                animStateElement.removeAttr((String[]) obj);
                return;
            }
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                this.mAttrs[i] = this.mAttrsExp[i].evaluateStr();
            }
            animStateElement.removeAttr(this.mAttrs);
        }

        private void update(AnimStateElement animStateElement) {
            double[] dArr;
            String[] strArr = this.mAttrs;
            if (strArr != null && (dArr = this.mValues) != null && strArr.length == dArr.length) {
                int length = strArr.length;
                for (int i = 0; i < length; i++) {
                    this.mAttrs[i] = this.mAttrsExp[i].evaluateStr();
                    this.mValues[i] = this.mValuesExp[i].evaluate();
                }
                animStateElement.updateAttr(this.mAttrs, this.mValues);
            } else if (this.mAttrArrayName != null && this.mValueArrayName != null) {
                Object obj = getVariables().get(this.mAttrArrayName.evaluateStr());
                Object obj2 = getVariables().get(this.mValueArrayName.evaluateStr());
                if (obj == null || !(obj instanceof String[]) || obj2 == null || !(obj2 instanceof double[])) {
                    return;
                }
                animStateElement.updateAttr((String[]) obj, (double[]) obj2);
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            Object target = getTarget();
            if (target == null || !(target instanceof AnimStateElement)) {
                return;
            }
            AnimStateElement animStateElement = (AnimStateElement) target;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$AnimStateCommand$Type[this.mCommand.ordinal()];
            if (i == 1) {
                update(animStateElement);
            } else if (i == 2) {
                remove(animStateElement);
            } else if (i != 3) {
            } else {
                animStateElement.clear();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class AnimationCommand extends TargetCommand {
        private boolean mAllAni;
        private String[] mAniTags;
        private CommandType mCommand;
        private Expression[] mPlayParams;

        /* loaded from: classes2.dex */
        private enum CommandType {
            INVALID,
            PLAY,
            PAUSE,
            RESUME,
            PLAY_WITH_PARAMS
        }

        public AnimationCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            String attribute = element.getAttribute("command");
            if (attribute.equalsIgnoreCase("play")) {
                this.mCommand = CommandType.PLAY;
            } else if (attribute.equalsIgnoreCase("pause")) {
                this.mCommand = CommandType.PAUSE;
            } else if (attribute.equalsIgnoreCase("resume")) {
                this.mCommand = CommandType.RESUME;
            } else if (attribute.toLowerCase().startsWith("play(") && attribute.endsWith(")")) {
                this.mCommand = CommandType.PLAY_WITH_PARAMS;
                Expression[] buildMultiple = Expression.buildMultiple(getVariables(), attribute.substring(5, attribute.length() - 1));
                this.mPlayParams = buildMultiple;
                if (buildMultiple != null && buildMultiple.length != 2 && buildMultiple.length != 4) {
                    Log.e("ActionCommand", "bad expression format");
                }
            } else {
                this.mCommand = CommandType.INVALID;
            }
            String attribute2 = element.getAttribute("tags");
            if (".".equals(attribute2)) {
                this.mAllAni = true;
            } else if (TextUtils.isEmpty(attribute2)) {
            } else {
                this.mAniTags = attribute2.split(",");
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void doPerform() {
            ScreenElement screenElement = (ScreenElement) getTarget();
            if (screenElement == null) {
                return;
            }
            CommandType commandType = this.mCommand;
            if ((commandType == CommandType.PLAY || commandType == CommandType.PLAY_WITH_PARAMS) && (this.mAllAni || this.mAniTags != null)) {
                screenElement.setAnim(this.mAniTags);
            }
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$AnimationCommand$CommandType[this.mCommand.ordinal()];
            if (i == 1) {
                screenElement.playAnim();
            } else if (i == 2) {
                screenElement.pauseAnim();
            } else if (i == 3) {
                screenElement.resumeAnim();
            } else if (i != 4) {
            } else {
                long j = 0;
                long j2 = -1;
                Expression[] expressionArr = this.mPlayParams;
                boolean z = false;
                if (expressionArr.length > 0) {
                    j = (long) (expressionArr[0] == null ? 0.0d : expressionArr[0].evaluate());
                }
                Expression[] expressionArr2 = this.mPlayParams;
                if (expressionArr2.length > 1) {
                    j2 = (long) (expressionArr2[1] == null ? -1.0d : expressionArr2[1].evaluate());
                }
                Expression[] expressionArr3 = this.mPlayParams;
                boolean z2 = expressionArr3.length > 2 && expressionArr3[2] != null && expressionArr3[2].evaluate() > 0.0d;
                Expression[] expressionArr4 = this.mPlayParams;
                if (expressionArr4.length > 3) {
                    z = expressionArr4[3] != null && expressionArr4[3].evaluate() > 0.0d;
                }
                screenElement.playAnim(j, j2, z2, z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Deprecated
    /* loaded from: classes2.dex */
    public static class AnimationProperty extends PropertyCommand {
        private Expression[] mPlayParams;
        private Type mType;

        /* loaded from: classes2.dex */
        enum Type {
            INVALID,
            PLAY,
            PAUSE,
            RESUME,
            PLAY_WITH_PARAMS
        }

        protected AnimationProperty(ScreenElement screenElement, Variable variable, String str) {
            super(screenElement, variable, str);
            if (str.equalsIgnoreCase("play")) {
                this.mType = Type.PLAY;
            } else if (str.equalsIgnoreCase("pause")) {
                this.mType = Type.PAUSE;
            } else if (str.equalsIgnoreCase("resume")) {
                this.mType = Type.RESUME;
            } else if (!str.toLowerCase().startsWith("play(") || !str.endsWith(")")) {
                this.mType = Type.INVALID;
            } else {
                this.mType = Type.PLAY_WITH_PARAMS;
                Expression[] buildMultiple = Expression.buildMultiple(getVariables(), str.substring(5, str.length() - 1));
                this.mPlayParams = buildMultiple;
                if (buildMultiple == null || buildMultiple.length == 2 || buildMultiple.length == 4) {
                    return;
                }
                Log.e("ActionCommand", "bad expression format");
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void doPerform() {
            boolean z;
            boolean z2;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$AnimationProperty$Type[this.mType.ordinal()];
            if (i == 1) {
                this.mTargetElement.playAnim();
            } else if (i == 2) {
                this.mTargetElement.pauseAnim();
            } else if (i == 3) {
                this.mTargetElement.resumeAnim();
            } else if (i != 4) {
            } else {
                long j = 0;
                long j2 = -1;
                Expression[] expressionArr = this.mPlayParams;
                if (expressionArr.length > 0) {
                    j = (long) (expressionArr[0] == null ? 0.0d : expressionArr[0].evaluate());
                }
                long j3 = j;
                Expression[] expressionArr2 = this.mPlayParams;
                if (expressionArr2.length > 1) {
                    j2 = (long) (expressionArr2[1] == null ? -1.0d : expressionArr2[1].evaluate());
                }
                long j4 = j2;
                Expression[] expressionArr3 = this.mPlayParams;
                if (expressionArr3.length > 2) {
                    z = expressionArr3[2] != null && expressionArr3[2].evaluate() > 0.0d;
                } else {
                    z = false;
                }
                Expression[] expressionArr4 = this.mPlayParams;
                if (expressionArr4.length > 3) {
                    z2 = expressionArr4[3] != null && expressionArr4[3].evaluate() > 0.0d;
                } else {
                    z2 = false;
                }
                this.mTargetElement.playAnim(j3, j4, z, z2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class BaseMethodCommand extends TargetCommand {
        protected IndexedVariable mErrorCodeVar;
        private ObjVar[] mParamObjVars;
        protected Class<?>[] mParamTypes;
        protected Object[] mParamValues;
        private Expression[] mParams;
        protected IndexedVariable mReturnVar;
        protected Class<?> mTargetClass;
        protected String mTargetClassName;

        public BaseMethodCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            String attribute = element.getAttribute(ExtraTelephony.UnderstandInfo.CLASS);
            this.mTargetClassName = attribute;
            if (TextUtils.isEmpty(attribute)) {
                this.mTargetClassName = null;
            }
            this.mParams = Expression.buildMultiple(getVariables(), element.getAttribute(YellowPageContract.HttpRequest.PARAMS));
            String attribute2 = element.getAttribute("paramTypes");
            if (this.mParams != null && !TextUtils.isEmpty(attribute2)) {
                try {
                    Class<?>[] strTypesToClass = ReflectionHelper.strTypesToClass(TextUtils.split(attribute2, ","));
                    this.mParamTypes = strTypesToClass;
                    if (this.mParams.length != strTypesToClass.length) {
                        Log.e("ActionCommand", this.mLogStr + "different length of params and paramTypes");
                        this.mParams = null;
                        this.mParamTypes = null;
                    }
                } catch (ClassNotFoundException e) {
                    Log.e("ActionCommand", this.mLogStr + "invalid method paramTypes. " + e.toString());
                    this.mParams = null;
                    this.mParamTypes = null;
                }
            }
            String attribute3 = element.getAttribute(SettingsProvider.RETURN);
            if (!TextUtils.isEmpty(attribute3)) {
                this.mReturnVar = new IndexedVariable(attribute3, getVariables(), VariableType.parseType(element.getAttribute("returnType")).isNumber());
            }
            String attribute4 = element.getAttribute("errorVar");
            if (!TextUtils.isEmpty(attribute4)) {
                this.mErrorCodeVar = new IndexedVariable(attribute4, getVariables(), true);
            }
            this.mLogStr += ", class=" + this.mTargetClassName + " type=" + this.mTargetType.toString();
        }

        @Override // com.miui.maml.ActionCommand
        public void finish() {
            super.finish();
            this.mParamValues = null;
        }

        @Override // com.miui.maml.ActionCommand.TargetCommand, com.miui.maml.ActionCommand
        public void init() {
            Expression expression;
            super.init();
            Class<?>[] clsArr = this.mParamTypes;
            if (clsArr != null) {
                if (this.mParamObjVars == null) {
                    this.mParamObjVars = new ObjVar[clsArr.length];
                }
                int i = 0;
                while (true) {
                    Class<?>[] clsArr2 = this.mParamTypes;
                    if (i >= clsArr2.length) {
                        break;
                    }
                    this.mParamObjVars[i] = null;
                    Class<?> cls = clsArr2[i];
                    if ((!cls.isPrimitive() || cls.isArray()) && cls != String.class && (expression = this.mParams[i]) != null) {
                        String evaluateStr = expression.evaluateStr();
                        if (!TextUtils.isEmpty(evaluateStr)) {
                            this.mParamObjVars[i] = new ObjVar(evaluateStr, getVariables());
                        }
                    }
                    i++;
                }
            }
            String str = this.mTargetClassName;
            if (str != null) {
                try {
                    this.mTargetClass = Class.forName(str);
                } catch (Exception e) {
                    Log.w("ActionCommand", "target class not found, name: " + this.mTargetClassName + "\n" + e.toString());
                }
            }
        }

        protected void prepareParams() {
            Expression[] expressionArr = this.mParams;
            if (expressionArr == null) {
                return;
            }
            if (this.mParamValues == null) {
                this.mParamValues = new Object[expressionArr.length];
            }
            int i = 0;
            while (true) {
                Expression[] expressionArr2 = this.mParams;
                if (i >= expressionArr2.length) {
                    return;
                }
                Object[] objArr = this.mParamValues;
                objArr[i] = null;
                Class<?> cls = this.mParamTypes[i];
                Expression expression = expressionArr2[i];
                if (expression != null) {
                    if (cls == String.class) {
                        objArr[i] = expression.evaluateStr();
                    } else if (cls == Integer.TYPE) {
                        objArr[i] = Integer.valueOf((int) expression.evaluate());
                    } else if (cls == Long.TYPE) {
                        objArr[i] = Long.valueOf((long) expression.evaluate());
                    } else if (cls == Boolean.TYPE) {
                        objArr[i] = Boolean.valueOf(expression.evaluate() > 0.0d);
                    } else if (cls == Double.TYPE) {
                        objArr[i] = Double.valueOf(expression.evaluate());
                    } else if (cls == Float.TYPE) {
                        objArr[i] = Float.valueOf((float) expression.evaluate());
                    } else if (cls == Byte.TYPE) {
                        objArr[i] = Byte.valueOf((byte) expression.evaluate());
                    } else if (cls == Short.TYPE) {
                        objArr[i] = Short.valueOf((short) expression.evaluate());
                    } else if (cls == Character.TYPE) {
                        objArr[i] = Character.valueOf((char) expression.evaluate());
                    } else {
                        ObjVar objVar = this.mParamObjVars[i];
                        objArr[i] = objVar != null ? objVar.get() : null;
                    }
                }
                i++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ConditionCommand extends ActionCommand {
        private ActionCommand mCommand;
        private Expression mCondition;

        public ConditionCommand(ActionCommand actionCommand, Expression expression) {
            super(actionCommand.getRoot());
            this.mCommand = actionCommand;
            this.mCondition = expression;
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            if (this.mCondition.evaluate() > 0.0d) {
                this.mCommand.perform();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            this.mCommand.init();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class DataSwitchCommand extends NotificationReceiver {
        private boolean mApnEnable;
        private MobileDataUtils mMobileDataUtils;
        private OnOffCommandHelper mOnOffHelper;

        public DataSwitchCommand(ScreenElement screenElement, String str) {
            super(screenElement, "data_state", NotifierManager.TYPE_MOBILE_DATA);
            this.mOnOffHelper = new OnOffCommandHelper(str);
            this.mMobileDataUtils = MobileDataUtils.getInstance();
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            boolean z = this.mApnEnable;
            OnOffCommandHelper onOffCommandHelper = this.mOnOffHelper;
            boolean z2 = onOffCommandHelper.mIsToggle ? !z : onOffCommandHelper.mIsOn;
            if (z != z2) {
                this.mMobileDataUtils.enableMobileData(this.mScreenElement.getContext().mContext, z2);
            }
        }

        @Override // com.miui.maml.ActionCommand.NotificationReceiver
        protected void update() {
            boolean isMobileEnable = this.mMobileDataUtils.isMobileEnable(this.mScreenElement.getContext().mContext);
            this.mApnEnable = isMobileEnable;
            updateState(isMobileEnable ? 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class DelayCommand extends ActionCommand {
        private Runnable mCmd;
        private ActionCommand mCommand;
        private long mDelay;

        public DelayCommand(ActionCommand actionCommand, long j) {
            super(actionCommand.getRoot());
            this.mCommand = actionCommand;
            this.mDelay = j;
            this.mCmd = new Runnable() { // from class: com.miui.maml.ActionCommand.DelayCommand.1
                @Override // java.lang.Runnable
                public void run() {
                    DelayCommand.this.mCommand.perform();
                }
            };
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            getRoot().postDelayed(this.mCmd, this.mDelay);
        }

        @Override // com.miui.maml.ActionCommand
        public void finish() {
            getRoot().removeCallbacks(this.mCmd);
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            this.mCommand.init();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class EaseTypeCommand extends TargetCommand {
        private String mEaseFun;
        private String mEaseParams;
        private Expression mEaseTypeExp;

        public EaseTypeCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mTargetType = TargetCommand.TargetType.ANIMATION_ITEM;
            this.mEaseTypeExp = Expression.build(getVariables(), element.getAttribute("easeTypeExp"));
            this.mEaseFun = element.getAttribute("easeFunExp");
            this.mEaseParams = element.getAttribute("easeParamsExp");
        }

        @Override // com.miui.maml.ActionCommand
        public void doPerform() {
            ArrayList arrayList = (ArrayList) getTarget();
            if (arrayList == null) {
                return;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((BaseAnimation.AnimationItem) it.next()).changeInterpolator(this.mEaseTypeExp.evaluateStr(), this.mEaseParams, this.mEaseFun);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ExternCommand extends ActionCommand {
        private String mCommand;
        private Expression mNumParaExp;
        private Expression mStrParaExp;

        public ExternCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mCommand = element.getAttribute("command");
            this.mNumParaExp = Expression.build(getVariables(), element.getAttribute("numPara"));
            this.mStrParaExp = Expression.build(getVariables(), element.getAttribute("strPara"));
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            ScreenElementRoot root = getRoot();
            String str = this.mCommand;
            Expression expression = this.mNumParaExp;
            Double valueOf = expression == null ? null : Double.valueOf(expression.evaluate());
            Expression expression2 = this.mStrParaExp;
            root.issueExternCommand(str, valueOf, expression2 != null ? expression2.evaluateStr() : null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class FieldCommand extends BaseMethodCommand {
        private Field mField;
        private String mFieldName;
        private boolean mIsSet;

        public FieldCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mFieldName = element.getAttribute("field");
            this.mLogStr = "FieldCommand, " + this.mLogStr + ", field=" + this.mFieldName + "\n";
            String attribute = element.getAttribute(YellowPageContract.HttpRequest.PARAM_METHOD);
            if ("get".equals(attribute)) {
                this.mIsSet = false;
            } else if ("set".equals(attribute)) {
                this.mIsSet = true;
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            if (this.mField == null) {
                loadField();
            }
            if (this.mField != null) {
                try {
                    int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
                    if (i == 1 || i == 2) {
                        if (this.mIsSet) {
                            prepareParams();
                            Object[] objArr = this.mParamValues;
                            if (objArr != null && objArr.length == 1) {
                                this.mField.set(getTarget(), this.mParamValues[0]);
                            }
                        } else if (this.mReturnVar != null) {
                            this.mReturnVar.set(this.mField.get(getTarget()));
                        }
                    }
                } catch (IllegalAccessException e) {
                    Log.e("ActionCommand", e.toString());
                } catch (IllegalArgumentException e2) {
                    Log.e("ActionCommand", e2.toString());
                } catch (NullPointerException e3) {
                    Log.e("ActionCommand", this.mLogStr + "Field target is null. " + e3.toString());
                }
            }
        }

        @Override // com.miui.maml.ActionCommand.BaseMethodCommand, com.miui.maml.ActionCommand.TargetCommand, com.miui.maml.ActionCommand
        public void init() {
            super.init();
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
            if ((i == 1 || i == 2) && this.mField != null) {
                loadField();
            }
        }

        protected void loadField() {
            Object target;
            if (this.mTargetClass == null && (target = getTarget()) != null) {
                this.mTargetClass = target.getClass();
            }
            Class<?> cls = this.mTargetClass;
            if (cls == null) {
                Log.e("ActionCommand", this.mLogStr + "class is null.");
                return;
            }
            try {
                this.mField = cls.getField(this.mFieldName);
            } catch (NoSuchFieldException e) {
                Log.e("ActionCommand", this.mLogStr + e.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class FolmeCommand extends TargetCommand {
        private Type mCommand;
        private Expression mConfig;
        private boolean mIsParamsValid;
        private boolean mIsStatesValid;
        private Expression[] mParams;
        private Expression[] mStates;

        /* loaded from: classes2.dex */
        enum Type {
            TO,
            SET_TO,
            FROM_TO,
            CANCEL,
            ADD_RANGE_BOARD,
            INVALID
        }

        public FolmeCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mParams = Expression.buildMultiple(getVariables(), element.getAttribute(YellowPageContract.HttpRequest.PARAMS));
            this.mStates = Expression.buildMultiple(getVariables(), element.getAttribute("states"));
            this.mConfig = Expression.build(getVariables(), element.getAttribute("config"));
            this.mIsParamsValid = isExpressionsValid(this.mParams);
            this.mIsStatesValid = isExpressionsValid(this.mStates);
            String attribute = element.getAttribute("command");
            attribute.hashCode();
            char c = 65535;
            switch (attribute.hashCode()) {
                case -1367724422:
                    if (attribute.equals("cancel")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1266098235:
                    if (attribute.equals("fromTo")) {
                        c = 1;
                        break;
                    }
                    break;
                case 3707:
                    if (attribute.equals("to")) {
                        c = 2;
                        break;
                    }
                    break;
                case 109327997:
                    if (attribute.equals("setTo")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.mCommand = Type.CANCEL;
                    return;
                case 1:
                    this.mCommand = Type.FROM_TO;
                    return;
                case 2:
                    this.mCommand = Type.TO;
                    return;
                case 3:
                    this.mCommand = Type.SET_TO;
                    return;
                default:
                    this.mCommand = Type.INVALID;
                    return;
            }
        }

        private void folmeCancel(AnimatedScreenElement animatedScreenElement) {
            if (this.mIsParamsValid) {
                animatedScreenElement.folmeCancel(this.mParams);
            } else {
                animatedScreenElement.folmeCancel(null);
            }
        }

        private void folmeFromTo(AnimatedScreenElement animatedScreenElement) {
            if (this.mIsStatesValid) {
                Expression[] expressionArr = this.mStates;
                if (expressionArr.length > 1) {
                    String evaluateStr = expressionArr[0].evaluateStr();
                    String evaluateStr2 = this.mStates[1].evaluateStr();
                    Expression expression = this.mConfig;
                    animatedScreenElement.folmeFromTo(evaluateStr, evaluateStr2, expression != null ? expression.evaluateStr() : null);
                }
            }
        }

        private void folmeSetTo(AnimatedScreenElement animatedScreenElement) {
            if (this.mIsStatesValid) {
                Expression[] expressionArr = this.mStates;
                if (expressionArr.length > 0) {
                    animatedScreenElement.folmeSetTo(expressionArr[0].evaluateStr());
                }
            }
        }

        private void folmeTo(AnimatedScreenElement animatedScreenElement) {
            if (this.mIsStatesValid) {
                Expression[] expressionArr = this.mStates;
                if (expressionArr.length > 0) {
                    String evaluateStr = expressionArr[0].evaluateStr();
                    Expression expression = this.mConfig;
                    animatedScreenElement.folmeTo(evaluateStr, expression != null ? expression.evaluateStr() : null);
                }
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            Object target = getTarget();
            if (target == null || !(target instanceof AnimatedScreenElement)) {
                return;
            }
            AnimatedScreenElement animatedScreenElement = (AnimatedScreenElement) target;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$FolmeCommand$Type[this.mCommand.ordinal()];
            if (i == 1) {
                folmeTo(animatedScreenElement);
            } else if (i == 2) {
                folmeSetTo(animatedScreenElement);
            } else if (i == 3) {
                folmeFromTo(animatedScreenElement);
            } else if (i != 4) {
            } else {
                folmeCancel(animatedScreenElement);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class FrameRateCommand extends ActionCommand {
        private Expression mRate;

        public FrameRateCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mRate = Expression.build(screenElement.getVariables(), element.getAttribute("rate"));
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            if (this.mRate != null) {
                getRoot().requestFrameRateByCommand((float) this.mRate.evaluate());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class FunctionPerformCommand extends TargetCommand {
        public FunctionPerformCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mTargetType = TargetCommand.TargetType.SCREEN_ELEMENT;
        }

        @Override // com.miui.maml.ActionCommand
        public void doPerform() {
            Object target = getTarget();
            if (target == null || !(target instanceof FunctionElement)) {
                return;
            }
            ((FunctionElement) target).perform();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GraphicsCommand extends TargetCommand {
        private Expression mColorArrayNameExp;
        private Expression mColorExp;
        private ColorParser[] mColorParsers;
        private int[] mColors;
        private CommandType mCommand;
        private String mCurrentColorArrayName;
        private String mCurrentStopArrayName;
        private boolean mIsParamsValid;
        private boolean mIsStopsValid;
        private Expression[] mParamExps;
        private Expression mStopArrayNameExp;
        private Expression[] mStopExps;
        private float[] mStops;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public enum CommandType {
            INVALID,
            BEGIN_FILL,
            BEGIN_GRADIENT_FILL,
            CREATE_GRADIENT_BOX,
            CURVE_TO,
            CUBIC_CURVE_TO,
            DRAW_CIRCLE,
            DRAW_ELLIPSE,
            DRAW_RECT,
            DRAW_ROUND_RECT,
            LINE_GRADIENT_STYLE,
            LINE_STYLE,
            LINE_TO,
            MOVE_TO,
            SET_RENDER_LISTENER
        }

        public GraphicsCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            checkExps(element);
            this.mColorArrayNameExp = Expression.build(getVariables(), element.getAttribute("colorArrayNameExp"));
            this.mStopArrayNameExp = Expression.build(getVariables(), element.getAttribute("stopArrayNameExp"));
            this.mColorExp = Expression.build(getVariables(), element.getAttribute("colorExp"));
            parseCommand(element);
        }

        private void beginFill(GraphicsElement graphicsElement) {
            ColorParser[] colorParserArr = this.mColorParsers;
            int color = (colorParserArr == null || colorParserArr.length <= 0) ? -16777216 : colorParserArr[0].getColor();
            if (this.mColorExp != null) {
                color = (int) r2.evaluate();
            }
            graphicsElement.beginFill(color);
        }

        private void checkExps(Element element) {
            String[] split;
            Expression[] buildMultiple = Expression.buildMultiple(getVariables(), element.getAttribute("paramsExp"));
            this.mParamExps = buildMultiple;
            this.mIsParamsValid = isExpressionsValid(buildMultiple);
            Expression[] buildMultiple2 = Expression.buildMultiple(getVariables(), element.getAttribute("stopsExp"));
            this.mStopExps = buildMultiple2;
            this.mIsStopsValid = isExpressionsValid(buildMultiple2);
            String attribute = element.getAttribute("colors");
            if (TextUtils.isEmpty(attribute) || (split = attribute.split(",")) == null || split.length <= 0) {
                return;
            }
            this.mColorParsers = new ColorParser[split.length];
            for (int i = 0; i < split.length; i++) {
                this.mColorParsers[i] = new ColorParser(getVariables(), split[i]);
            }
        }

        private void createGradientBox(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 4) {
                    graphicsElement.createOrUpdateGradientBox(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()), this.mParamExps[4].evaluateStr());
                }
            }
        }

        private void cubicCurveTo(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 5) {
                    graphicsElement.cubicCurveTo(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()), scale((float) this.mParamExps[4].evaluate()), scale((float) this.mParamExps[5].evaluate()));
                }
            }
        }

        private void curveTo(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 3) {
                    graphicsElement.curveTo(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()));
                }
            }
        }

        private void drawCircle(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 2) {
                    graphicsElement.drawCircle(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()));
                }
            }
        }

        private void drawEllipse(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 3) {
                    graphicsElement.drawEllipse(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()));
                }
            }
        }

        private void drawRect(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 3) {
                    graphicsElement.drawRect(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()), scale((float) this.mParamExps[2].evaluate()), scale((float) this.mParamExps[3].evaluate()));
                }
            }
        }

        private void drawRoundRect(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 4) {
                    float scale = scale((float) expressionArr[0].evaluate());
                    float scale2 = scale((float) this.mParamExps[1].evaluate());
                    float scale3 = scale((float) this.mParamExps[2].evaluate());
                    float scale4 = scale((float) this.mParamExps[3].evaluate());
                    float scale5 = scale((float) this.mParamExps[4].evaluate());
                    Expression[] expressionArr2 = this.mParamExps;
                    graphicsElement.drawRoundRect(scale, scale2, scale3, scale4, scale5, expressionArr2.length > 5 ? scale((float) expressionArr2[5].evaluate()) : scale5);
                }
            }
        }

        private void lineStyle(GraphicsElement graphicsElement) {
            if (!this.mIsParamsValid || this.mParamExps.length <= 0) {
                return;
            }
            int i = -16777216;
            if (this.mColorExp != null) {
                i = (int) r1.evaluate();
            } else {
                ColorParser[] colorParserArr = this.mColorParsers;
                if (colorParserArr != null && colorParserArr.length > 0) {
                    i = colorParserArr[0].getColor();
                }
            }
            int i2 = i;
            float scale = scale((float) this.mParamExps[0].evaluate());
            Expression[] expressionArr = this.mParamExps;
            int evaluate = expressionArr.length > 1 ? (int) expressionArr[1].evaluate() : 0;
            Expression[] expressionArr2 = this.mParamExps;
            graphicsElement.lineStyle(scale, i2, evaluate, expressionArr2.length > 2 ? (int) expressionArr2[2].evaluate() : 0, this.mParamExps.length > 3 ? (int) r9[3].evaluate() : 0);
        }

        private void lineTo(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 1) {
                    graphicsElement.lineTo(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()));
                }
            }
        }

        private void moveTo(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 1) {
                    graphicsElement.moveTo(scale((float) expressionArr[0].evaluate()), scale((float) this.mParamExps[1].evaluate()));
                }
            }
        }

        private void parseColor() {
            if (this.mColorArrayNameExp != null) {
                parseColorByArrayName();
                return;
            }
            ColorParser[] colorParserArr = this.mColorParsers;
            if (colorParserArr == null || colorParserArr.length <= 1) {
                return;
            }
            parseColorByParsers();
        }

        private void parseColorByArrayName() {
            String evaluateStr = this.mColorArrayNameExp.evaluateStr();
            if (TextUtils.isEmpty(evaluateStr)) {
                this.mColors = null;
            } else if (evaluateStr.equals(this.mCurrentColorArrayName)) {
            } else {
                this.mCurrentColorArrayName = evaluateStr;
                Object obj = new IndexedVariable(evaluateStr, getVariables(), false).get();
                if (obj != null && (obj instanceof int[])) {
                    int[] iArr = (int[]) obj;
                    if (iArr.length > 1) {
                        this.mColors = iArr;
                        return;
                    }
                }
                this.mColors = null;
            }
        }

        private void parseColorByParsers() {
            if (this.mColors == null) {
                this.mColors = new int[this.mColorParsers.length];
            }
            int i = 0;
            while (true) {
                ColorParser[] colorParserArr = this.mColorParsers;
                if (i >= colorParserArr.length) {
                    return;
                }
                this.mColors[i] = colorParserArr[i].getColor();
                i++;
            }
        }

        private void parseCommand(Element element) {
            String attribute = element.getAttribute("command");
            attribute.hashCode();
            char c = 65535;
            switch (attribute.hashCode()) {
                case -1807133155:
                    if (attribute.equals("lineStyle")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1741117076:
                    if (attribute.equals("setRenderListener")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1102672497:
                    if (attribute.equals("lineTo")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1073257012:
                    if (attribute.equals("beginFill")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1068263892:
                    if (attribute.equals("moveTo")) {
                        c = 4;
                        break;
                    }
                    break;
                case -826951352:
                    if (attribute.equals("drawRect")) {
                        c = 5;
                        break;
                    }
                    break;
                case -556608716:
                    if (attribute.equals("drawCircle")) {
                        c = 6;
                        break;
                    }
                    break;
                case 27279565:
                    if (attribute.equals("lineGradientStyle")) {
                        c = 7;
                        break;
                    }
                    break;
                case 80105951:
                    if (attribute.equals("createGradientBox")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 137996206:
                    if (attribute.equals("drawRoundRect")) {
                        c = '\t';
                        break;
                    }
                    break;
                case 753006880:
                    if (attribute.equals("cubicCurveTo")) {
                        c = '\n';
                        break;
                    }
                    break;
                case 1127058378:
                    if (attribute.equals("curveTo")) {
                        c = 11;
                        break;
                    }
                    break;
                case 1312120860:
                    if (attribute.equals("beginGradientFill")) {
                        c = '\f';
                        break;
                    }
                    break;
                case 1780535802:
                    if (attribute.equals("drawEllipse")) {
                        c = '\r';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.mCommand = CommandType.LINE_STYLE;
                    return;
                case 1:
                    this.mCommand = CommandType.SET_RENDER_LISTENER;
                    return;
                case 2:
                    this.mCommand = CommandType.LINE_TO;
                    return;
                case 3:
                    this.mCommand = CommandType.BEGIN_FILL;
                    return;
                case 4:
                    this.mCommand = CommandType.MOVE_TO;
                    return;
                case 5:
                    this.mCommand = CommandType.DRAW_RECT;
                    return;
                case 6:
                    this.mCommand = CommandType.DRAW_CIRCLE;
                    return;
                case 7:
                    this.mCommand = CommandType.LINE_GRADIENT_STYLE;
                    return;
                case '\b':
                    this.mCommand = CommandType.CREATE_GRADIENT_BOX;
                    return;
                case '\t':
                    this.mCommand = CommandType.DRAW_ROUND_RECT;
                    return;
                case '\n':
                    this.mCommand = CommandType.CUBIC_CURVE_TO;
                    return;
                case 11:
                    this.mCommand = CommandType.CURVE_TO;
                    return;
                case '\f':
                    this.mCommand = CommandType.BEGIN_GRADIENT_FILL;
                    return;
                case '\r':
                    this.mCommand = CommandType.DRAW_ELLIPSE;
                    return;
                default:
                    this.mCommand = CommandType.INVALID;
                    return;
            }
        }

        private void parseStop() {
            if (this.mStopArrayNameExp != null) {
                parseStopByArrayName();
            } else if (this.mIsStopsValid) {
                parseStopByExp();
            }
        }

        private void parseStopByArrayName() {
            String evaluateStr = this.mStopArrayNameExp.evaluateStr();
            if (TextUtils.isEmpty(evaluateStr)) {
                this.mStops = null;
            } else if (evaluateStr.equals(this.mCurrentStopArrayName)) {
            } else {
                this.mCurrentStopArrayName = evaluateStr;
                Object obj = new IndexedVariable(evaluateStr, getVariables(), false).get();
                if (obj != null && (obj instanceof float[])) {
                    float[] fArr = (float[]) obj;
                    if (fArr.length > 1) {
                        this.mStops = fArr;
                        return;
                    }
                }
                this.mStops = null;
            }
        }

        private void parseStopByExp() {
            if (this.mStops == null) {
                this.mStops = new float[this.mStopExps.length];
            }
            int i = 0;
            while (true) {
                Expression[] expressionArr = this.mStopExps;
                if (i >= expressionArr.length) {
                    return;
                }
                this.mStops[i] = (float) expressionArr[i].evaluate();
                i++;
            }
        }

        private float scale(float f) {
            return f * getRoot().getScale();
        }

        private void setRenderListener(GraphicsElement graphicsElement) {
            if (this.mIsParamsValid) {
                Expression[] expressionArr = this.mParamExps;
                if (expressionArr.length > 0) {
                    ScreenElement findElement = getRoot().findElement(expressionArr[0].evaluateStr());
                    if (findElement == null || !(findElement instanceof FunctionElement)) {
                        return;
                    }
                    graphicsElement.setRenderListener((FunctionElement) findElement);
                }
            }
        }

        private void setShader(GraphicsElement graphicsElement) {
            if (!this.mIsParamsValid || this.mParamExps.length <= 2) {
                return;
            }
            parseColor();
            parseStop();
            int[] iArr = this.mColors;
            if (iArr == null || iArr.length < 2) {
                Log.e("GraphicsCommand", "needs >= 2 number of colors");
                return;
            }
            float[] fArr = this.mStops;
            if (fArr != null && fArr.length != iArr.length) {
                Log.e("GraphicsCommand", "color and position arrays must be of equal length");
                return;
            }
            int evaluate = (int) this.mParamExps[0].evaluate();
            String evaluateStr = this.mParamExps[1].evaluateStr();
            String evaluateStr2 = this.mParamExps[2].evaluateStr();
            Expression[] expressionArr = this.mParamExps;
            int evaluate2 = expressionArr.length > 3 ? (int) expressionArr[3].evaluate() : 0;
            CommandType commandType = this.mCommand;
            if (commandType == CommandType.LINE_GRADIENT_STYLE) {
                graphicsElement.lineGradientStyle(evaluate, this.mColors, this.mStops, evaluateStr, evaluateStr2, evaluate2);
            } else if (commandType == CommandType.BEGIN_GRADIENT_FILL) {
                graphicsElement.beginGradientFill(evaluate, this.mColors, this.mStops, evaluateStr, evaluateStr2, evaluate2);
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            Object target = getTarget();
            if (target == null || !(target instanceof GraphicsElement)) {
                return;
            }
            GraphicsElement graphicsElement = (GraphicsElement) target;
            switch (AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$GraphicsCommand$CommandType[this.mCommand.ordinal()]) {
                case 1:
                    lineTo(graphicsElement);
                    return;
                case 2:
                    moveTo(graphicsElement);
                    return;
                case 3:
                    curveTo(graphicsElement);
                    return;
                case 4:
                    drawRect(graphicsElement);
                    return;
                case 5:
                    beginFill(graphicsElement);
                    return;
                case 6:
                    lineStyle(graphicsElement);
                    return;
                case 7:
                    drawCircle(graphicsElement);
                    return;
                case 8:
                    drawEllipse(graphicsElement);
                    return;
                case 9:
                    cubicCurveTo(graphicsElement);
                    return;
                case 10:
                    drawRoundRect(graphicsElement);
                    return;
                case 11:
                case 12:
                    setShader(graphicsElement);
                    return;
                case 13:
                    createGradientBox(graphicsElement);
                    return;
                case 14:
                    setRenderListener(graphicsElement);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class IfCommand extends ActionCommand {
        private MultiCommand mAlternateCommand;
        private Expression mCondition;
        private MultiCommand mConsequentCommand;

        public IfCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mCondition = Expression.build(screenElement.getVariables(), element.getAttribute("ifCondition"));
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeType() == 1) {
                    Element element2 = (Element) childNodes.item(i);
                    String tagName = element2.getTagName();
                    if ("Consequent".equalsIgnoreCase(tagName) && this.mConsequentCommand == null) {
                        this.mConsequentCommand = new MultiCommand(screenElement, element2);
                    } else if ("Alternate".equalsIgnoreCase(tagName) && this.mAlternateCommand == null) {
                        this.mAlternateCommand = new MultiCommand(screenElement, element2);
                    }
                }
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void doPerform() {
            Expression expression = this.mCondition;
            if (expression != null) {
                if (expression.evaluate() <= 0.0d) {
                    MultiCommand multiCommand = this.mAlternateCommand;
                    if (multiCommand != null) {
                        multiCommand.perform();
                        return;
                    }
                    return;
                }
                MultiCommand multiCommand2 = this.mConsequentCommand;
                if (multiCommand2 != null) {
                    multiCommand2.perform();
                }
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void finish() {
            MultiCommand multiCommand = this.mAlternateCommand;
            if (multiCommand != null) {
                multiCommand.finish();
            }
            MultiCommand multiCommand2 = this.mConsequentCommand;
            if (multiCommand2 != null) {
                multiCommand2.finish();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            MultiCommand multiCommand = this.mAlternateCommand;
            if (multiCommand != null) {
                multiCommand.init();
            }
            MultiCommand multiCommand2 = this.mConsequentCommand;
            if (multiCommand2 != null) {
                multiCommand2.init();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void pause() {
            MultiCommand multiCommand = this.mAlternateCommand;
            if (multiCommand != null) {
                multiCommand.pause();
            }
            MultiCommand multiCommand2 = this.mConsequentCommand;
            if (multiCommand2 != null) {
                multiCommand2.pause();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void resume() {
            MultiCommand multiCommand = this.mAlternateCommand;
            if (multiCommand != null) {
                multiCommand.resume();
            }
            MultiCommand multiCommand2 = this.mConsequentCommand;
            if (multiCommand2 != null) {
                multiCommand2.resume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class IntentCommand extends ActionCommand {
        private ObjVar mActivityOptionsBundle;
        private CommandTrigger mFallbackTrigger;
        private int mFlags;
        private Intent mIntent;
        private IntentInfo mIntentInfo;
        private IntentType mIntentType;
        private IndexedVariable mIntentVar;

        /* loaded from: classes2.dex */
        private enum IntentType {
            Activity,
            Broadcast,
            Service,
            Var
        }

        public IntentCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            IntentType intentType = IntentType.Activity;
            this.mIntentType = intentType;
            this.mIntentInfo = new IntentInfo(element, getVariables());
            boolean parseBoolean = Boolean.parseBoolean(element.getAttribute("broadcast"));
            String attribute = element.getAttribute("type");
            if (parseBoolean || "broadcast".equals(attribute)) {
                this.mIntentType = IntentType.Broadcast;
            } else if ("service".equals(attribute)) {
                this.mIntentType = IntentType.Service;
            } else if ("activity".equals(attribute)) {
                this.mIntentType = intentType;
            } else if ("var".equals(attribute)) {
                this.mIntentType = IntentType.Var;
                String attribute2 = element.getAttribute("intentVar");
                if (!TextUtils.isEmpty(attribute2)) {
                    this.mIntentVar = new IndexedVariable(attribute2, getVariables(), false);
                }
            }
            this.mFlags = Utils.getAttrAsInt(element, "flags", -1);
            String attribute3 = element.getAttribute("activityOption");
            this.mActivityOptionsBundle = TextUtils.isEmpty(attribute3) ? null : new ObjVar(attribute3, getVariables());
            Element child = Utils.getChild(element, "Fallback");
            if (child != null) {
                this.mFallbackTrigger = new CommandTrigger(child, screenElement);
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            String packageName;
            IndexedVariable indexedVariable;
            Intent intent = this.mIntent;
            if (intent != null) {
                this.mIntentInfo.update(intent);
                try {
                    int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$IntentCommand$IntentType[this.mIntentType.ordinal()];
                    if (i != 1) {
                        if (i == 2) {
                            Utils.sendBroadcast(getContext(), this.mIntent);
                            return;
                        } else if (i == 3) {
                            Utils.startService(getContext(), this.mIntent);
                            return;
                        } else if (i == 4 && (indexedVariable = this.mIntentVar) != null) {
                            indexedVariable.set(this.mIntent);
                            return;
                        } else {
                            return;
                        }
                    }
                    ObjVar objVar = this.mActivityOptionsBundle;
                    Bundle bundle = objVar != null ? (Bundle) objVar.get() : null;
                    List<ResolveInfo> queryIntentActivities = getContext().getPackageManager().queryIntentActivities(this.mIntent, SearchUpdater.GOOGLE);
                    if (queryIntentActivities != null && queryIntentActivities.size() > 0) {
                        Utils.startActivity(getContext(), this.mIntent, bundle);
                        return;
                    }
                    if (!TextUtils.isEmpty(this.mIntent.getPackage())) {
                        packageName = this.mIntent.getPackage();
                    } else if (TextUtils.isEmpty(this.mIntent.getComponent().getPackageName())) {
                        return;
                    } else {
                        packageName = this.mIntent.getComponent().getPackageName();
                    }
                    HideSdkDependencyUtils.PreloadedAppPolicy_installPreloadedDataApp(getContext(), packageName, this.mIntent, bundle);
                } catch (Exception e) {
                    if (this.mFallbackTrigger == null) {
                        Log.e("ActionCommand", e.toString());
                        return;
                    }
                    Log.i("ActionCommand", "fail to send Intent, fallback...");
                    this.mFallbackTrigger.perform();
                }
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void finish() {
            CommandTrigger commandTrigger = this.mFallbackTrigger;
            if (commandTrigger != null) {
                commandTrigger.finish();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            Task findTask = getRoot().findTask(this.mIntentInfo.getId());
            if (findTask != null && !TextUtils.isEmpty(findTask.action)) {
                this.mIntentInfo.set(findTask);
            }
            if (Utils.isProtectedIntent(this.mIntentInfo.getAction())) {
                return;
            }
            Intent intent = new Intent();
            this.mIntent = intent;
            this.mIntentInfo.update(intent);
            int i = this.mFlags;
            if (i != -1) {
                this.mIntent.setFlags(i);
            } else if (this.mIntentType == IntentType.Activity) {
                this.mIntent.setFlags(872415232);
            }
            CommandTrigger commandTrigger = this.mFallbackTrigger;
            if (commandTrigger != null) {
                commandTrigger.init();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void pause() {
            CommandTrigger commandTrigger = this.mFallbackTrigger;
            if (commandTrigger != null) {
                commandTrigger.pause();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void resume() {
            CommandTrigger commandTrigger = this.mFallbackTrigger;
            if (commandTrigger != null) {
                commandTrigger.resume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class LoopCommand extends MultiCommand {
        private Expression mBeginExp;
        private Expression mConditionExp;
        private Expression mCountExp;
        private Expression mEndExp;
        private IndexedVariable mIndexVar;

        public LoopCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            String attribute = element.getAttribute("indexName");
            Variables variables = getVariables();
            if (!TextUtils.isEmpty(attribute)) {
                this.mIndexVar = new IndexedVariable(attribute, variables, true);
            }
            this.mBeginExp = Expression.build(variables, element.getAttribute("begin"));
            Expression build = Expression.build(variables, element.getAttribute(Tag.TagPhone.MARKED_COUNT));
            this.mCountExp = build;
            if (build == null) {
                this.mEndExp = Expression.build(variables, element.getAttribute("end"));
            }
            this.mConditionExp = Expression.build(variables, element.getAttribute("loopCondition"));
        }

        @Override // com.miui.maml.ActionCommand.MultiCommand, com.miui.maml.ActionCommand
        protected void doPerform() {
            int evaluate;
            Expression expression = this.mBeginExp;
            int evaluate2 = expression == null ? 0 : (int) expression.evaluate();
            Expression expression2 = this.mCountExp;
            if (expression2 != null) {
                evaluate = (((int) expression2.evaluate()) + evaluate2) - 1;
            } else {
                Expression expression3 = this.mEndExp;
                evaluate = expression3 == null ? 0 : (int) expression3.evaluate();
            }
            int i = evaluate - evaluate2;
            if (i > 10000) {
                Log.w("ActionCommand", "count is too large: " + i + ", exceeds WARNING 10000");
            }
            while (evaluate2 <= evaluate) {
                Expression expression4 = this.mConditionExp;
                if (expression4 != null && expression4.evaluate() <= 0.0d) {
                    return;
                }
                IndexedVariable indexedVariable = this.mIndexVar;
                if (indexedVariable != null) {
                    indexedVariable.set(evaluate2);
                }
                int size = this.mCommands.size();
                for (int i2 = 0; i2 < size; i2++) {
                    this.mCommands.get(i2).perform();
                }
                evaluate2++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MethodCommand extends BaseMethodCommand {
        private Constructor<?> mCtor;
        private Method mMethod;
        private String mMethodName;

        public MethodCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mMethodName = element.getAttribute(YellowPageContract.HttpRequest.PARAM_METHOD);
            this.mLogStr = "MethodCommand, " + this.mLogStr + ", method=" + this.mMethodName + "\n    ";
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            String str;
            IndexedVariable indexedVariable;
            double d;
            prepareParams();
            Object obj = null;
            int i = 0;
            try {
                try {
                    int i2 = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
                    if (i2 == 1 || i2 == 2) {
                        if (this.mMethod == null) {
                            loadMethod();
                        }
                        if (this.mMethod != null) {
                            obj = this.mMethod.invoke(getTarget(), this.mParamValues);
                            i = 1;
                        }
                        i = -1;
                    } else if (i2 == 5) {
                        Constructor<?> constructor = this.mCtor;
                        if (constructor != null) {
                            obj = constructor.newInstance(this.mParamValues);
                            i = 1;
                        }
                        i = -1;
                    }
                    IndexedVariable indexedVariable2 = this.mReturnVar;
                    if (indexedVariable2 != null) {
                        indexedVariable2.set(obj);
                    }
                    indexedVariable = this.mErrorCodeVar;
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.mLogStr);
                    sb.append(e.toString());
                    if (cause != null) {
                        str = "\n cause: " + cause.toString();
                    } else {
                        str = "";
                    }
                    sb.append(str);
                    Log.e("ActionCommand", sb.toString());
                    indexedVariable = this.mErrorCodeVar;
                    if (indexedVariable == null) {
                        return;
                    }
                    d = -2;
                }
                if (indexedVariable != null) {
                    d = i;
                    indexedVariable.set(d);
                }
            } catch (Throwable th) {
                IndexedVariable indexedVariable3 = this.mErrorCodeVar;
                if (indexedVariable3 != null) {
                    indexedVariable3.set(i);
                }
                throw th;
            }
        }

        @Override // com.miui.maml.ActionCommand.BaseMethodCommand, com.miui.maml.ActionCommand.TargetCommand, com.miui.maml.ActionCommand
        public void init() {
            super.init();
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
            if (i == 1 || i == 2) {
                if (this.mMethod == null) {
                    loadMethod();
                }
            } else if (i != 5) {
            } else {
                if (!getRoot().getCapability(4)) {
                    this.mCtor = null;
                } else if (this.mCtor == null) {
                    Class<?> cls = this.mTargetClass;
                    if (cls == null) {
                        Log.e("ActionCommand", this.mLogStr + "init, class is null.");
                        return;
                    }
                    try {
                        this.mCtor = cls.getConstructor(this.mParamTypes);
                    } catch (NoSuchMethodException e) {
                        Log.e("ActionCommand", this.mLogStr + "init, fail to find method. " + e.toString());
                    }
                }
            }
        }

        protected void loadMethod() {
            Object target;
            if (this.mTargetClass == null && (target = getTarget()) != null) {
                this.mTargetClass = target.getClass();
            }
            Class<?> cls = this.mTargetClass;
            if (cls == null) {
                Log.e("ActionCommand", this.mLogStr + "loadMethod(), class is null.");
                return;
            }
            try {
                this.mMethod = cls.getMethod(this.mMethodName, this.mParamTypes);
            } catch (NoSuchMethodException e) {
                Log.e("ActionCommand", this.mLogStr + "loadMethod(). " + e.toString());
            }
            Log.d("ActionCommand", this.mLogStr + "loadMethod(), successful.  " + this.mMethod.toString());
        }
    }

    /* loaded from: classes2.dex */
    private static class ModeToggleHelper {
        private int mCurModeIndex;
        private int mCurToggleIndex;
        private ArrayList<Integer> mModeIds;
        private ArrayList<String> mModeNames;
        private boolean mToggle;
        private boolean mToggleAll;
        private ArrayList<Integer> mToggleModes;

        private ModeToggleHelper() {
            this.mModeNames = new ArrayList<>();
            this.mModeIds = new ArrayList<>();
            this.mToggleModes = new ArrayList<>();
        }

        /* synthetic */ ModeToggleHelper(AnonymousClass1 anonymousClass1) {
            this();
        }

        private int findMode(String str) {
            for (int i = 0; i < this.mModeNames.size(); i++) {
                if (this.mModeNames.get(i).equals(str)) {
                    return i;
                }
            }
            return -1;
        }

        public void addMode(String str, int i) {
            this.mModeNames.add(str);
            this.mModeIds.add(Integer.valueOf(i));
        }

        public boolean build(String str) {
            int findMode = findMode(str);
            if (findMode >= 0) {
                this.mCurModeIndex = findMode;
                return true;
            } else if (MiuiHeDuoHaoUtil.TOGGLE.equals(str)) {
                this.mToggleAll = true;
                return true;
            } else {
                for (String str2 : str.split(",")) {
                    int findMode2 = findMode(str2);
                    if (findMode2 < 0) {
                        return false;
                    }
                    this.mToggleModes.add(Integer.valueOf(findMode2));
                }
                this.mToggle = true;
                return true;
            }
        }

        public void click() {
            if (this.mToggle) {
                int i = this.mCurToggleIndex + 1;
                this.mCurToggleIndex = i;
                int size = i % this.mToggleModes.size();
                this.mCurToggleIndex = size;
                this.mCurModeIndex = this.mToggleModes.get(size).intValue();
            } else if (this.mToggleAll) {
                int i2 = this.mCurModeIndex + 1;
                this.mCurModeIndex = i2;
                this.mCurModeIndex = i2 % this.mModeNames.size();
            }
        }

        public int getModeId() {
            return this.mModeIds.get(this.mCurModeIndex).intValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MultiCommand extends ActionCommand {
        protected ArrayList<ActionCommand> mCommands;

        public MultiCommand(final ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mCommands = new ArrayList<>();
            Utils.traverseXmlElementChildren(element, null, new Utils.XmlTraverseListener() { // from class: com.miui.maml.ActionCommand.MultiCommand.1
                @Override // com.miui.maml.util.Utils.XmlTraverseListener
                public void onChild(Element element2) {
                    ActionCommand create = ActionCommand.create(element2, screenElement);
                    if (create != null) {
                        MultiCommand.this.mCommands.add(create);
                    }
                }
            });
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().perform();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void finish() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().finish();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().init();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void pause() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().pause();
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void resume() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().resume();
            }
        }
    }

    /* loaded from: classes2.dex */
    private static abstract class NotificationReceiver extends StatefulActionCommand implements NotifierManager.OnNotifyListener {
        private NotifierManager mNotifierManager;
        private String mType;

        public NotificationReceiver(ScreenElement screenElement, String str, String str2) {
            super(screenElement, str);
            this.mType = str2;
            this.mNotifierManager = NotifierManager.getInstance(getContext());
        }

        protected void asyncUpdate() {
            ActionCommand.mHandler.post(new Runnable() { // from class: com.miui.maml.ActionCommand.NotificationReceiver.1
                @Override // java.lang.Runnable
                public void run() {
                    NotificationReceiver.this.update();
                }
            });
        }

        @Override // com.miui.maml.ActionCommand
        public void finish() {
            this.mNotifierManager.releaseNotifier(this.mType, this);
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            update();
            this.mNotifierManager.acquireNotifier(this.mType, this);
        }

        @Override // com.miui.maml.NotifierManager.OnNotifyListener
        public void onNotify(Context context, Intent intent, Object obj) {
            asyncUpdate();
        }

        @Override // com.miui.maml.ActionCommand
        public void pause() {
            this.mNotifierManager.pause(this.mType, this);
        }

        @Override // com.miui.maml.ActionCommand
        public void resume() {
            update();
            this.mNotifierManager.resume(this.mType, this);
        }

        protected abstract void update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ObjVar {
        private int mIndex;
        private Expression mIndexArr;
        private Variables mVars;

        public ObjVar(String str, Variables variables) {
            this.mVars = variables;
            int indexOf = str.indexOf(91);
            if (indexOf > 0) {
                try {
                    String substring = str.substring(0, indexOf);
                    try {
                        this.mIndexArr = Expression.build(variables, str.substring(indexOf + 1, str.length() - 1));
                    } catch (IndexOutOfBoundsException unused) {
                    }
                    str = substring;
                } catch (IndexOutOfBoundsException unused2) {
                }
            }
            this.mIndex = variables.registerVariable(str);
        }

        public Object get() {
            Expression expression;
            Object obj = this.mVars.get(this.mIndex);
            if (obj == null || (expression = this.mIndexArr) == null || !(obj instanceof Object[])) {
                return obj;
            }
            try {
                return ((Object[]) obj)[(int) expression.evaluate()];
            } catch (IndexOutOfBoundsException unused) {
                return null;
            }
        }
    }

    /* loaded from: classes2.dex */
    private static class OnOffCommandHelper {
        protected boolean mIsOn;
        protected boolean mIsToggle;

        public OnOffCommandHelper(String str) {
            if (str.equalsIgnoreCase(MiuiHeDuoHaoUtil.TOGGLE)) {
                this.mIsToggle = true;
            } else if (str.equalsIgnoreCase("on")) {
                this.mIsOn = true;
            } else if (str.equalsIgnoreCase("off")) {
                this.mIsOn = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class PbrCommand extends TargetCommand {
        private CommandType mCommand;
        private String mCustElementName;
        private boolean mIsParamsValid;
        private Expression[] mParams;
        private boolean mUniformAutoRefresh;
        private String mUniformName;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public enum CommandType {
            INVALID,
            UPDATE_UNIFORM
        }

        public PbrCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            String attribute = element.getAttribute(YellowPageContract.HttpRequest.PARAMS);
            Expression[] buildMultiple = Expression.buildMultiple(getVariables(), attribute);
            this.mParams = buildMultiple;
            boolean isExpressionsValid = isExpressionsValid(buildMultiple);
            this.mIsParamsValid = isExpressionsValid;
            if (!isExpressionsValid) {
                Log.e("PbrCommand", "Wrong params: " + attribute);
            }
            this.mUniformName = element.getAttribute("uniformName");
            this.mCustElementName = element.getAttribute("custElementName");
            this.mUniformAutoRefresh = Boolean.parseBoolean(element.getAttribute("uniformRefresh"));
            parseCommand(element);
        }

        private void parseCommand(Element element) {
            String attribute = element.getAttribute("command");
            attribute.hashCode();
            if (attribute.equals("updateUniform")) {
                this.mCommand = CommandType.UPDATE_UNIFORM;
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            Object target = getTarget();
            if (target == null || !(target instanceof PhysicallyBasedRenderingElement)) {
                return;
            }
            PhysicallyBasedRenderingElement physicallyBasedRenderingElement = (PhysicallyBasedRenderingElement) target;
            if (AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$PbrCommand$CommandType[this.mCommand.ordinal()] != 1) {
                return;
            }
            physicallyBasedRenderingElement.updateUniform(this.mCustElementName, this.mUniformName, this.mUniformAutoRefresh, this.mParams);
        }
    }

    @Deprecated
    /* loaded from: classes2.dex */
    public static abstract class PropertyCommand extends ActionCommand {
        protected ScreenElement mTargetElement;
        private Variable mTargetObj;

        protected PropertyCommand(ScreenElement screenElement, Variable variable, String str) {
            super(screenElement);
            this.mTargetObj = variable;
        }

        public static PropertyCommand create(ScreenElement screenElement, String str, String str2) {
            Variable variable = new Variable(str);
            if ("visibility".equals(variable.getPropertyName())) {
                return new VisibilityProperty(screenElement, variable, str2);
            }
            if ("animation".equals(variable.getPropertyName())) {
                return new AnimationProperty(screenElement, variable, str2);
            }
            return null;
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            super.init();
            if (this.mTargetObj != null && this.mTargetElement == null) {
                ScreenElement findElement = getRoot().findElement(this.mTargetObj.getObjName());
                this.mTargetElement = findElement;
                if (findElement == null) {
                    Log.w("ActionCommand", "could not find PropertyCommand target, name: " + this.mTargetObj.getObjName());
                    this.mTargetObj = null;
                }
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void perform() {
            if (this.mTargetElement == null) {
                return;
            }
            doPerform();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class RingModeCommand extends NotificationReceiver {
        private AudioManager mAudioManager;
        private ModeToggleHelper mToggleHelper;

        public RingModeCommand(ScreenElement screenElement, String str) {
            super(screenElement, "ring_mode", "android.media.RINGER_MODE_CHANGED");
            ModeToggleHelper modeToggleHelper = new ModeToggleHelper(null);
            this.mToggleHelper = modeToggleHelper;
            modeToggleHelper.addMode("normal", 2);
            this.mToggleHelper.addMode("silent", 0);
            this.mToggleHelper.addMode("vibrate", 1);
            if (this.mToggleHelper.build(str)) {
                return;
            }
            Log.e("ActionCommand", "invalid ring mode command value: " + str);
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            if (this.mAudioManager == null) {
                return;
            }
            this.mToggleHelper.click();
            int modeId = this.mToggleHelper.getModeId();
            this.mAudioManager.setRingerMode(modeId);
            updateState(modeId);
        }

        @Override // com.miui.maml.ActionCommand.NotificationReceiver
        protected void update() {
            if (this.mAudioManager == null) {
                this.mAudioManager = (AudioManager) this.mScreenElement.getContext().mContext.getSystemService("audio");
            }
            AudioManager audioManager = this.mAudioManager;
            if (audioManager == null) {
                return;
            }
            updateState(audioManager.getRingerMode());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SensorBinderCommand extends TargetCommand {
        private CommandType mCommand;

        /* loaded from: classes2.dex */
        private enum CommandType {
            INVALID,
            TURN_ON,
            TURN_OFF
        }

        public SensorBinderCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mTargetType = TargetCommand.TargetType.VARIABLE_BINDER;
            String attribute = element.getAttribute("command");
            attribute.hashCode();
            if (attribute.equals("turnOff")) {
                this.mCommand = CommandType.TURN_OFF;
            } else if (attribute.equals("turnOn")) {
                this.mCommand = CommandType.TURN_ON;
            } else {
                this.mCommand = CommandType.INVALID;
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            Object target = getTarget();
            if (target == null || !(target instanceof SensorBinder)) {
                return;
            }
            SensorBinder sensorBinder = (SensorBinder) target;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$SensorBinderCommand$CommandType[this.mCommand.ordinal()];
            if (i == 1) {
                sensorBinder.turnOnSensorBinder();
            } else if (i != 2) {
            } else {
                sensorBinder.turnOffSensorBinder();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class SoundCommand extends ActionCommand {
        private SoundManager.Command mCommand;
        private boolean mKeepCur;
        private boolean mLoop;
        private String mSound;
        private Expression mStreamIdExp;
        private IndexedVariable mStreamIdVar;
        private Expression mVolumeExp;

        public SoundCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mSound = element.getAttribute("sound");
            this.mKeepCur = Boolean.parseBoolean(element.getAttribute("keepCur"));
            this.mLoop = Boolean.parseBoolean(element.getAttribute("loop"));
            this.mCommand = SoundManager.Command.parse(element.getAttribute("command"));
            Expression build = Expression.build(getVariables(), element.getAttribute("volume"));
            this.mVolumeExp = build;
            if (build == null) {
                Log.e("ActionCommand", "invalid expression in SoundCommand");
            }
            this.mStreamIdExp = Expression.build(getVariables(), element.getAttribute("streamId"));
            String attribute = element.getAttribute("streamIdVar");
            if (TextUtils.isEmpty(attribute)) {
                return;
            }
            this.mStreamIdVar = new IndexedVariable(attribute, getVariables(), true);
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            Expression expression;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$SoundManager$Command[this.mCommand.ordinal()];
            if (i != 1) {
                if ((i == 2 || i == 3 || i == 4) && (expression = this.mStreamIdExp) != null) {
                    getRoot().playSound((int) expression.evaluate(), this.mCommand);
                    return;
                }
                return;
            }
            Expression expression2 = this.mVolumeExp;
            int playSound = getRoot().playSound(this.mSound, new SoundManager.SoundOptions(this.mKeepCur, this.mLoop, expression2 != null ? (float) expression2.evaluate() : 0.0f));
            IndexedVariable indexedVariable = this.mStreamIdVar;
            if (indexedVariable != null) {
                indexedVariable.set(playSound);
            }
        }
    }

    /* loaded from: classes2.dex */
    private static abstract class StatefulActionCommand extends ActionCommand {
        private IndexedVariable mVar;

        public StatefulActionCommand(ScreenElement screenElement, String str) {
            super(screenElement);
            this.mVar = new IndexedVariable(str, getVariables(), true);
        }

        protected final void updateState(int i) {
            IndexedVariable indexedVariable = this.mVar;
            if (indexedVariable == null) {
                return;
            }
            indexedVariable.set(i);
            getRoot().requestUpdate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class TargetCommand extends ActionCommand {
        protected String mLogStr;
        private Object mTarget;
        protected Expression mTargetIndex;
        protected String mTargetName;
        protected Expression mTargetNameExp;
        protected TargetType mTargetType;

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: classes2.dex */
        public enum TargetType {
            SCREEN_ELEMENT,
            VARIABLE,
            CONSTRUCTOR,
            ANIMATION_ITEM,
            VARIABLE_BINDER
        }

        public TargetCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            Expression build = Expression.build(getVariables(), element.getAttribute("targetExp"));
            this.mTargetNameExp = build;
            if (build != null) {
                this.mTargetName = build.evaluateStr();
            } else {
                this.mTargetName = element.getAttribute("target");
            }
            if (TextUtils.isEmpty(this.mTargetName)) {
                this.mTargetName = null;
            }
            this.mTargetIndex = Expression.build(getVariables(), element.getAttribute("targetIndex"));
            String attribute = element.getAttribute("targetType");
            TargetType targetType = TargetType.SCREEN_ELEMENT;
            this.mTargetType = targetType;
            if ("element".equals(attribute)) {
                this.mTargetType = targetType;
            } else if ("var".equals(attribute)) {
                this.mTargetType = TargetType.VARIABLE;
            } else if ("ctor".equals(attribute)) {
                this.mTargetType = TargetType.CONSTRUCTOR;
            }
            this.mLogStr = "target=" + this.mTargetName + " type=" + this.mTargetType.toString();
        }

        private void findTarget() {
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
            if (i != 1) {
                if (i == 2) {
                    if (this.mTargetName != null) {
                        this.mTarget = Integer.valueOf(getVariables().registerVariable(this.mTargetName));
                        return;
                    } else {
                        Log.e("ActionCommand", "MethodCommand, type=var, empty target name");
                        return;
                    }
                } else if (i == 3) {
                    this.mTarget = getRoot().getAnimationItems(this.mTargetName);
                    return;
                } else if (i != 4) {
                    return;
                } else {
                    this.mTarget = getRoot().findBinder(this.mTargetName);
                    return;
                }
            }
            ScreenElement findElement = getRoot().findElement(this.mTargetName);
            this.mTarget = findElement;
            if (findElement == null) {
                Log.e("ActionCommand", "could not find ScreenElement target, name: " + this.mTargetName);
            } else if (this.mTargetIndex == null || ElementGroup.isArrayGroup(findElement)) {
            } else {
                Log.e("ActionCommand", "target with index is not an ArrayGroup, name: " + this.mTargetName);
                this.mTargetIndex = null;
            }
        }

        protected Object getTarget() {
            Expression expression;
            Expression expression2 = this.mTargetNameExp;
            if (expression2 != null) {
                String evaluateStr = expression2.evaluateStr();
                if (evaluateStr == null) {
                    this.mTargetName = null;
                    this.mTarget = null;
                    return null;
                } else if (!evaluateStr.equals(this.mTargetName)) {
                    this.mTargetName = evaluateStr;
                    findTarget();
                }
            }
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TargetCommand$TargetType[this.mTargetType.ordinal()];
            if (i == 1) {
                Object obj = this.mTarget;
                return (obj == null || (expression = this.mTargetIndex) == null) ? obj : ((ElementGroup) obj).getChild((int) expression.evaluate());
            } else if (i != 2) {
                if (i == 3 || i == 4) {
                    return this.mTarget;
                }
                return null;
            } else {
                if (this.mTarget != null) {
                    Object obj2 = getVariables().get(((Integer) this.mTarget).intValue());
                    if (this.mTargetIndex == null) {
                        return obj2;
                    }
                    if (obj2.getClass().isArray()) {
                        return Array.get(obj2, (int) this.mTargetIndex.evaluate());
                    }
                    Log.e("ActionCommand", "target with index is not an Array variable, name: " + this.mTargetName);
                    this.mTargetIndex = null;
                }
                return null;
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            super.init();
            findTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class TickListenerCommand extends TargetCommand {
        private CommandType mCommand;
        private Expression mFunNameExp;

        /* loaded from: classes2.dex */
        private enum CommandType {
            INVALID,
            SET,
            UNSET
        }

        public TickListenerCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            this.mFunNameExp = Expression.build(getVariables(), element.getAttribute("function"));
            String attribute = element.getAttribute("command");
            attribute.hashCode();
            if (attribute.equals("set")) {
                this.mCommand = CommandType.SET;
            } else if (attribute.equals("unset")) {
                this.mCommand = CommandType.UNSET;
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            Object target = getTarget();
            if (target == null || !(target instanceof AnimatedScreenElement)) {
                return;
            }
            AnimatedScreenElement animatedScreenElement = (AnimatedScreenElement) target;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$TickListenerCommand$CommandType[this.mCommand.ordinal()];
            if (i != 1) {
                if (i != 2) {
                    return;
                }
                animatedScreenElement.unsetOnTickListener();
                return;
            }
            ScreenElement findElement = getRoot().findElement(this.mFunNameExp.evaluateStr());
            if (findElement == null || !(findElement instanceof FunctionElement)) {
                return;
            }
            animatedScreenElement.setOnTickListener((FunctionElement) findElement);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class UsbStorageSwitchCommand extends NotificationReceiver {
        private boolean mConnected;
        private OnOffCommandHelper mOnOffHelper;
        private StorageManager mStorageManager;

        public UsbStorageSwitchCommand(ScreenElement screenElement, String str) {
            super(screenElement, "usb_mode", "android.hardware.usb.action.USB_STATE");
            this.mOnOffHelper = new OnOffCommandHelper(str);
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            final boolean z;
            StorageManager storageManager = this.mStorageManager;
            if (storageManager == null) {
                return;
            }
            boolean StorageManager_isUsbMassStorageEnabled = HideSdkDependencyUtils.StorageManager_isUsbMassStorageEnabled(storageManager);
            OnOffCommandHelper onOffCommandHelper = this.mOnOffHelper;
            if (onOffCommandHelper.mIsToggle) {
                z = !StorageManager_isUsbMassStorageEnabled;
            } else {
                boolean z2 = onOffCommandHelper.mIsOn;
                if (z2 == StorageManager_isUsbMassStorageEnabled) {
                    return;
                }
                z = z2;
            }
            updateState(3);
            new Thread("StorageSwitchThread") { // from class: com.miui.maml.ActionCommand.UsbStorageSwitchCommand.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    if (z) {
                        HideSdkDependencyUtils.StorageManager_enableUsbMassStorage(UsbStorageSwitchCommand.this.mStorageManager);
                    } else {
                        HideSdkDependencyUtils.StorageManager_disableUsbMassStorage(UsbStorageSwitchCommand.this.mStorageManager);
                    }
                    UsbStorageSwitchCommand.this.updateState(z ? 2 : 1);
                }
            }.start();
        }

        @Override // com.miui.maml.ActionCommand.NotificationReceiver, com.miui.maml.NotifierManager.OnNotifyListener
        public void onNotify(Context context, Intent intent, Object obj) {
            if (intent != null) {
                this.mConnected = intent.getExtras().getBoolean(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED);
            }
            super.onNotify(context, intent, obj);
        }

        @Override // com.miui.maml.ActionCommand.NotificationReceiver
        protected void update() {
            if (this.mStorageManager == null) {
                StorageManager storageManager = (StorageManager) getContext().getSystemService("storage");
                this.mStorageManager = storageManager;
                if (storageManager == null) {
                    Log.w("ActionCommand", "Failed to get StorageManager");
                    return;
                }
            }
            updateState(this.mConnected ? HideSdkDependencyUtils.StorageManager_isUsbMassStorageEnabled(this.mStorageManager) ? 2 : 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class VariableAssignmentCommand extends ActionCommand {
        private Expression[] mArrayValues;
        private Expression mExpression;
        private Expression mIndexExpression;
        private IndexedVariable mLengthVar;
        private String mName;
        private Expression mNameExp;
        private boolean mPersist;
        private boolean mRequestUpdate;
        private VariableType mType;
        private IndexedVariable mVar;

        public VariableAssignmentCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            Variables variables = screenElement.getVariables();
            Expression build = Expression.build(variables, element.getAttribute("nameExp"));
            this.mNameExp = build;
            if (build != null) {
                this.mName = build.evaluateStr();
            } else {
                this.mName = element.getAttribute("name");
            }
            this.mPersist = Boolean.parseBoolean(element.getAttribute("persist"));
            this.mRequestUpdate = Boolean.parseBoolean(element.getAttribute("requestUpdate"));
            this.mType = VariableType.parseType(element.getAttribute("type"));
            if (TextUtils.isEmpty(this.mName)) {
                Log.e("ActionCommand", "empty name in VariableAssignmentCommand");
            } else {
                this.mVar = new IndexedVariable(this.mName, variables, this.mType.isNumber());
                if (this.mType.isArray()) {
                    this.mLengthVar = new IndexedVariable(this.mName + ".length", variables, true);
                }
            }
            this.mExpression = Expression.build(variables, element.getAttribute("expression"));
            if (this.mType.isArray()) {
                this.mIndexExpression = Expression.build(variables, element.getAttribute("index"));
                this.mArrayValues = Expression.buildMultiple(variables, element.getAttribute("values"));
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            ScreenElementRoot root = getRoot();
            Variables variables = getVariables();
            Expression expression = this.mNameExp;
            Object obj = null;
            if (expression != null) {
                String evaluateStr = expression.evaluateStr();
                if (TextUtils.isEmpty(evaluateStr)) {
                    this.mName = null;
                    return;
                } else if (!evaluateStr.equals(this.mName)) {
                    this.mName = evaluateStr;
                    this.mVar = new IndexedVariable(evaluateStr, variables, this.mType.isNumber());
                    if (this.mType.isArray()) {
                        this.mLengthVar = new IndexedVariable(this.mName + ".length", variables, true);
                    }
                }
            }
            if (this.mVar == null) {
                return;
            }
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$VariableType[this.mType.ordinal()];
            if (i == 1) {
                Expression expression2 = this.mExpression;
                if (expression2 != null) {
                    double evaluate = expression2.evaluate();
                    this.mVar.set(evaluate);
                    if (this.mPersist && root.getCapability(2)) {
                        root.saveVar(this.mName, Double.valueOf(evaluate));
                    }
                }
            } else if (i != 2) {
                int i2 = 0;
                if (this.mType.isNumberOrStringArray()) {
                    if (this.mIndexExpression == null || this.mExpression == null) {
                        if (this.mArrayValues != null) {
                            Object obj2 = this.mVar.get();
                            int length = this.mArrayValues.length;
                            if (obj2 == null || Array.getLength(obj2) != length || obj2.getClass().getComponentType() != this.mType.mTypeClass) {
                                variables.createArray(this.mName, length, this.mType.mTypeClass);
                                this.mLengthVar.set(length);
                                obj2 = this.mVar.get();
                            }
                            if (this.mType.isNumberArray()) {
                                while (i2 < length) {
                                    Expression expression3 = this.mArrayValues[i2];
                                    Variables.putValueToArr(obj2, i2, expression3 == null ? 0.0d : expression3.evaluate());
                                    i2++;
                                }
                            } else {
                                while (i2 < length) {
                                    Expression expression4 = this.mArrayValues[i2];
                                    ((String[]) obj2)[i2] = expression4 == null ? null : expression4.evaluateStr();
                                    i2++;
                                }
                            }
                            this.mVar.set(obj2);
                        }
                    } else if (this.mType.isNumberArray()) {
                        this.mVar.setArr((int) this.mIndexExpression.evaluate(), this.mExpression.evaluate());
                    } else {
                        this.mVar.setArr((int) this.mIndexExpression.evaluate(), this.mExpression.evaluateStr());
                    }
                }
                Expression expression5 = this.mExpression;
                String evaluateStr2 = expression5 != null ? expression5.evaluateStr() : null;
                Variables variables2 = getVariables();
                if (!TextUtils.isEmpty(evaluateStr2) && variables2.existsObj(evaluateStr2)) {
                    obj = variables2.get(evaluateStr2);
                }
                Expression expression6 = this.mIndexExpression;
                if (expression6 == null) {
                    Object obj3 = this.mVar.get();
                    this.mVar.set(obj);
                    if ((obj3 != null && (obj3 instanceof Array)) || (obj != null && (obj instanceof Array))) {
                        i2 = 1;
                    }
                    if (this.mLengthVar == null && i2 != 0) {
                        this.mLengthVar = new IndexedVariable(this.mName + ".length", variables, true);
                    }
                    if (obj == null || !(obj instanceof Array)) {
                        IndexedVariable indexedVariable = this.mLengthVar;
                        if (indexedVariable != null) {
                            indexedVariable.set(0.0d);
                        }
                    } else {
                        this.mLengthVar.set(Array.getLength(obj));
                    }
                } else {
                    this.mVar.setArr((int) expression6.evaluate(), obj);
                }
            } else {
                String evaluateStr3 = this.mExpression.evaluateStr();
                this.mVar.set(evaluateStr3);
                if (this.mPersist && root.getCapability(2)) {
                    root.saveVar(this.mName, evaluateStr3);
                }
            }
            if (this.mRequestUpdate) {
                root.requestUpdate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class VariableBinderCommand extends ActionCommand {
        private VariableBinder mBinder;
        private Command mCommand;
        private String mName;

        /* loaded from: classes2.dex */
        private enum Command {
            Refresh,
            Invalid
        }

        public VariableBinderCommand(ScreenElement screenElement, Element element) {
            super(screenElement);
            this.mCommand = Command.Invalid;
            this.mName = element.getAttribute("name");
            if (element.getAttribute("command").equals("refresh")) {
                this.mCommand = Command.Refresh;
            }
        }

        @Override // com.miui.maml.ActionCommand
        protected void doPerform() {
            if (this.mBinder == null || AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$VariableBinderCommand$Command[this.mCommand.ordinal()] != 1) {
                return;
            }
            this.mBinder.refresh();
        }

        @Override // com.miui.maml.ActionCommand
        public void init() {
            this.mBinder = getRoot().findBinder(this.mName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class VideoCommand extends TargetCommand {
        private CommandType mCommand;
        private Expression mLooping;
        private Expression mPath;
        private Expression mScaleMode;
        private Expression mTime;
        private Expression mVolume;

        /* loaded from: classes2.dex */
        private enum CommandType {
            PAUSE,
            PLAY,
            SEEK_TO,
            CONFIG,
            SET_VOLUME,
            INVALID
        }

        public VideoCommand(ScreenElement screenElement, Element element) {
            super(screenElement, element);
            Variables variables = getVariables();
            String attribute = element.getAttribute("command");
            this.mPath = Expression.build(variables, element.getAttribute("path"));
            this.mVolume = Expression.build(variables, element.getAttribute("volume"));
            this.mScaleMode = Expression.build(variables, element.getAttribute("scaleMode"));
            this.mLooping = Expression.build(variables, element.getAttribute("loop"));
            this.mTime = Expression.build(variables, element.getAttribute("time"));
            attribute.hashCode();
            char c = 65535;
            switch (attribute.hashCode()) {
                case -1354792126:
                    if (attribute.equals("config")) {
                        c = 0;
                        break;
                    }
                    break;
                case -906224877:
                    if (attribute.equals("seekTo")) {
                        c = 1;
                        break;
                    }
                    break;
                case 3443508:
                    if (attribute.equals("play")) {
                        c = 2;
                        break;
                    }
                    break;
                case 106440182:
                    if (attribute.equals("pause")) {
                        c = 3;
                        break;
                    }
                    break;
                case 670514716:
                    if (attribute.equals("setVolume")) {
                        c = 4;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.mCommand = CommandType.CONFIG;
                    return;
                case 1:
                    this.mCommand = CommandType.SEEK_TO;
                    return;
                case 2:
                    this.mCommand = CommandType.PLAY;
                    return;
                case 3:
                    this.mCommand = CommandType.PAUSE;
                    return;
                case 4:
                    this.mCommand = CommandType.SET_VOLUME;
                    return;
                default:
                    this.mCommand = CommandType.INVALID;
                    return;
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void doPerform() {
            Object target = getTarget();
            if (target == null || !(target instanceof VideoElement)) {
                return;
            }
            VideoElement videoElement = (VideoElement) target;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$ActionCommand$VideoCommand$CommandType[this.mCommand.ordinal()];
            if (i == 1) {
                videoElement.play();
            } else if (i == 2) {
                videoElement.pause();
            } else if (i == 3) {
                Expression expression = this.mTime;
                if (expression != null) {
                    videoElement.seekTo((int) expression.evaluate());
                }
            } else if (i == 4) {
                Expression expression2 = this.mVolume;
                if (expression2 != null) {
                    videoElement.setVolume((float) expression2.evaluate());
                }
            } else if (i != 5) {
            } else {
                Expression expression3 = this.mLooping;
                boolean z = false;
                if (expression3 != null && expression3.evaluate() > 0.0d) {
                    z = true;
                }
                Expression expression4 = this.mScaleMode;
                int evaluate = expression4 != null ? (int) expression4.evaluate() : 1;
                Expression expression5 = this.mPath;
                videoElement.config(z, evaluate, expression5 != null ? expression5.evaluateStr() : "");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Deprecated
    /* loaded from: classes2.dex */
    public static class VisibilityProperty extends PropertyCommand {
        private boolean mIsShow;
        private boolean mIsToggle;

        protected VisibilityProperty(ScreenElement screenElement, Variable variable, String str) {
            super(screenElement, variable, str);
            if (str.equalsIgnoreCase(MiuiHeDuoHaoUtil.TOGGLE)) {
                this.mIsToggle = true;
            } else if (str.equalsIgnoreCase("true")) {
                this.mIsShow = true;
            } else if (str.equalsIgnoreCase("false")) {
                this.mIsShow = false;
            }
        }

        @Override // com.miui.maml.ActionCommand
        public void doPerform() {
            if (!this.mIsToggle) {
                this.mTargetElement.show(this.mIsShow);
                return;
            }
            this.mTargetElement.show(!r1.isVisible());
        }
    }

    public ActionCommand(ScreenElement screenElement) {
        this.mScreenElement = screenElement;
    }

    protected static ActionCommand create(ScreenElement screenElement, String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            Variable variable = new Variable(str);
            if (variable.getObjName() != null) {
                return PropertyCommand.create(screenElement, str, str2);
            }
            String propertyName = variable.getPropertyName();
            if ("RingMode".equals(propertyName)) {
                return new RingModeCommand(screenElement, str2);
            }
            if ("Data".equals(propertyName)) {
                return new DataSwitchCommand(screenElement, str2);
            }
            if ("UsbStorage".equals(propertyName)) {
                return new UsbStorageSwitchCommand(screenElement, str2);
            }
        }
        return null;
    }

    public static ActionCommand create(Element element, ScreenElement screenElement) {
        ActionCommand graphicsCommand;
        ConditionCommand create;
        if (element == null) {
            return null;
        }
        Expression build = Expression.build(screenElement.getVariables(), element.getAttribute("condition"));
        Expression build2 = Expression.build(screenElement.getVariables(), element.getAttribute("delayCondition"));
        long attrAsLong = Utils.getAttrAsLong(element, "delay", 0L);
        String nodeName = element.getNodeName();
        nodeName.hashCode();
        char c = 65535;
        switch (nodeName.hashCode()) {
            case -1988058592:
                if (nodeName.equals("GraphicsCommand")) {
                    c = 0;
                    break;
                }
                break;
            case -1919219473:
                if (nodeName.equals("IntentCommand")) {
                    c = 1;
                    break;
                }
                break;
            case -1735490724:
                if (nodeName.equals("SoundCommand")) {
                    c = 2;
                    break;
                }
                break;
            case -1698045830:
                if (nodeName.equals("TickListenerCommand")) {
                    c = 3;
                    break;
                }
                break;
            case -1679919317:
                if (nodeName.equals("Command")) {
                    c = 4;
                    break;
                }
                break;
            case -1214351624:
                if (nodeName.equals("AnimConfigCommand")) {
                    c = 5;
                    break;
                }
                break;
            case -1157373931:
                if (nodeName.equals("ActionCommand")) {
                    c = 6;
                    break;
                }
                break;
            case -1031402045:
                if (nodeName.equals("EaseTypeCommand")) {
                    c = 7;
                    break;
                }
                break;
            case -768846862:
                if (nodeName.equals("MultiCommand")) {
                    c = '\b';
                    break;
                }
                break;
            case -447874370:
                if (nodeName.equals("FrameRateCommand")) {
                    c = '\t';
                    break;
                }
                break;
            case -176797942:
                if (nodeName.equals("MethodCommand")) {
                    c = '\n';
                    break;
                }
                break;
            case -146126197:
                if (nodeName.equals("PbrCommand")) {
                    c = 11;
                    break;
                }
                break;
            case 38409067:
                if (nodeName.equals("ExternCommand")) {
                    c = '\f';
                    break;
                }
                break;
            case 765286380:
                if (nodeName.equals("GroupCommand")) {
                    c = '\r';
                    break;
                }
                break;
            case 812540743:
                if (nodeName.equals("AnimationCommand")) {
                    c = 14;
                    break;
                }
                break;
            case 911071503:
                if (nodeName.equals("VariableCommand")) {
                    c = 15;
                    break;
                }
                break;
            case 967558768:
                if (nodeName.equals("VideoCommand")) {
                    c = 16;
                    break;
                }
                break;
            case 1017417233:
                if (nodeName.equals("SensorCommand")) {
                    c = 17;
                    break;
                }
                break;
            case 1192886094:
                if (nodeName.equals("IfCommand")) {
                    c = 18;
                    break;
                }
                break;
            case 1242365201:
                if (nodeName.equals("FieldCommand")) {
                    c = 19;
                    break;
                }
                break;
            case 1252238076:
                if (nodeName.equals("VibrateCommand")) {
                    c = 20;
                    break;
                }
                break;
            case 1624729163:
                if (nodeName.equals("AnimStateCommand")) {
                    c = 21;
                    break;
                }
                break;
            case 1774042657:
                if (nodeName.equals("BinderCommand")) {
                    c = 22;
                    break;
                }
                break;
            case 1905320272:
                if (nodeName.equals("FolmeCommand")) {
                    c = 23;
                    break;
                }
                break;
            case 1952987187:
                if (nodeName.equals("FunctionCommand")) {
                    c = 24;
                    break;
                }
                break;
            case 2009815015:
                if (nodeName.equals("LoopCommand")) {
                    c = 25;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                graphicsCommand = new GraphicsCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 1:
                graphicsCommand = new IntentCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 2:
                graphicsCommand = new SoundCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 3:
                graphicsCommand = new TickListenerCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 4:
                create = create(screenElement, element.getAttribute("target"), element.getAttribute("value"));
                break;
            case 5:
                graphicsCommand = new AnimConfigCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 6:
                graphicsCommand = new ActionPerformCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 7:
                graphicsCommand = new EaseTypeCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case '\b':
            case '\r':
                graphicsCommand = new MultiCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case '\t':
                graphicsCommand = new FrameRateCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case '\n':
                graphicsCommand = new MethodCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 11:
                graphicsCommand = new PbrCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case '\f':
                graphicsCommand = new ExternCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 14:
                graphicsCommand = new AnimationCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 15:
                graphicsCommand = new VariableAssignmentCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 16:
                graphicsCommand = new VideoCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 17:
                graphicsCommand = new SensorBinderCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 18:
                graphicsCommand = new IfCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 19:
                graphicsCommand = new FieldCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 20:
                graphicsCommand = new VibrateCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 21:
                graphicsCommand = new AnimStateCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 22:
                graphicsCommand = new VariableBinderCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 23:
                graphicsCommand = new FolmeCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 24:
                graphicsCommand = new FunctionPerformCommand(screenElement, element);
                create = graphicsCommand;
                break;
            case 25:
                graphicsCommand = new LoopCommand(screenElement, element);
                create = graphicsCommand;
                break;
            default:
                ObjectFactory.ActionCommandFactory actionCommandFactory = (ObjectFactory.ActionCommandFactory) screenElement.getContext().getObjectFactory("ActionCommand");
                if (actionCommandFactory == null) {
                    create = null;
                    break;
                } else {
                    create = actionCommandFactory.create(screenElement, element);
                    break;
                }
        }
        if (create == null) {
            return null;
        }
        if (build2 != null) {
            create = new ConditionCommand(create, build2);
        }
        if (attrAsLong > 0) {
            create = new DelayCommand(create, attrAsLong);
        }
        return build != null ? new ConditionCommand(create, build) : create;
    }

    protected abstract void doPerform();

    public void finish() {
    }

    protected final Context getContext() {
        return getScreenContext().mContext;
    }

    protected ScreenElementRoot getRoot() {
        return this.mScreenElement.getRoot();
    }

    protected final ScreenContext getScreenContext() {
        return this.mScreenElement.getContext();
    }

    protected final Variables getVariables() {
        return this.mScreenElement.getVariables();
    }

    public void init() {
    }

    protected boolean isExpressionsValid(Expression[] expressionArr) {
        if (expressionArr != null) {
            int i = 0;
            while (i < expressionArr.length && expressionArr[i] != null) {
                i++;
            }
            return i == expressionArr.length;
        }
        return false;
    }

    public void pause() {
    }

    public void perform() {
        doPerform();
    }

    public void resume() {
    }
}
