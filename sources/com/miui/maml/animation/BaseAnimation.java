package com.miui.maml.animation;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.CommandTriggers;
import com.miui.maml.animation.interpolater.InterpolatorHelper;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class BaseAnimation {
    private long mAnimEndTime;
    private long mAnimStartTime;
    protected String[] mAttrs;
    private double[] mCurValues;
    private IndexedVariable mCurrentFrame;
    private Expression mDelay;
    private boolean mDisable;
    private long mEndTime;
    private boolean mHasName;
    private boolean mInitPaused;
    private boolean mIsDelay;
    private boolean mIsFirstFrame;
    private boolean mIsFirstReset;
    private boolean mIsLastFrame;
    private boolean mIsLoop;
    private boolean mIsPaused;
    private boolean mIsReverse;
    private boolean mIsTimeInfinite;
    protected ArrayList<AnimationItem> mItems;
    private boolean mLoop;
    private String mName;
    private long mPauseTime;
    private long mPlayTimeRange;
    private long mRealTimeRange;
    private long mResetTime;
    protected ScreenElement mScreenElement;
    private long mStartTime;
    private String mTag;
    private CommandTriggers mTriggers;

    /* loaded from: classes2.dex */
    public static class AnimationItem {
        private BaseAnimation mAni;
        private double[] mAttrsValue;
        public Expression mDeltaTimeExp;
        public Expression[] mExps;
        public long mInitTime;
        public InterpolatorHelper mInterpolator;
        private String mName;
        private boolean mNeedEvaluate = true;
        public long mTime;

        public AnimationItem(BaseAnimation baseAnimation, Element element) {
            this.mAni = baseAnimation;
            load(element);
        }

        private void load(Element element) {
            Variables variables = this.mAni.getVariables();
            String attribute = element.getAttribute("name");
            this.mName = attribute;
            if (!TextUtils.isEmpty(attribute)) {
                this.mAni.mScreenElement.getRoot().addAnimationItem(this.mName, this);
            }
            this.mInterpolator = InterpolatorHelper.create(variables, element);
            String attribute2 = element.getAttribute("time");
            if (!TextUtils.isEmpty(attribute2)) {
                try {
                    this.mTime = Long.parseLong(attribute2);
                } catch (NumberFormatException unused) {
                }
            }
            this.mDeltaTimeExp = Expression.build(variables, element.getAttribute("dtime"));
            String[] attrs = this.mAni.getAttrs();
            if (attrs != null) {
                this.mAttrsValue = new double[attrs.length];
                this.mExps = new Expression[attrs.length];
                int length = attrs.length;
                int i = 0;
                int i2 = 0;
                while (i < length) {
                    String str = attrs[i];
                    Expression build = Expression.build(variables, element.getAttribute(str));
                    if (build == null && i2 == 0 && !"value".equals(str)) {
                        build = Expression.build(variables, element.getAttribute("value"));
                    }
                    this.mExps[i2] = build;
                    i++;
                    i2++;
                }
            }
            this.mInitTime = this.mTime;
        }

        private void reevaluate() {
            Expression[] expressionArr = this.mExps;
            if (expressionArr == null) {
                return;
            }
            int length = expressionArr.length;
            int i = 0;
            int i2 = 0;
            while (i < length) {
                Expression expression = expressionArr[i];
                int i3 = i2 + 1;
                this.mAttrsValue[i2] = expression == null ? 0.0d : expression.evaluate();
                i++;
                i2 = i3;
            }
        }

        public boolean attrExists(int i) {
            Expression[] expressionArr = this.mExps;
            return expressionArr != null && i >= 0 && i < expressionArr.length && expressionArr[i] != null;
        }

        public void changeInterpolator(String str, String str2, String str3) {
            this.mInterpolator = new InterpolatorHelper(this.mAni.getVariables(), str, str3, str2);
        }

        public double get(int i) {
            double[] dArr = this.mAttrsValue;
            if (dArr != null && i >= 0 && i < dArr.length) {
                if (this.mNeedEvaluate) {
                    reevaluate();
                    this.mNeedEvaluate = false;
                }
                return this.mAttrsValue[i];
            }
            Log.e("BaseAnimation", "fail to get number in AnimationItem:" + i);
            return 0.0d;
        }

        public void reset() {
            this.mNeedEvaluate = true;
            this.mTime = this.mInitTime;
        }
    }

    public BaseAnimation(Element element, String str, ScreenElement screenElement) {
        this(element, str, "value", screenElement);
    }

    public BaseAnimation(Element element, String str, String str2, ScreenElement screenElement) {
        this(element, str, new String[]{str2}, screenElement);
    }

    public BaseAnimation(Element element, String str, String[] strArr, ScreenElement screenElement) {
        this.mItems = new ArrayList<>();
        this.mLoop = true;
        this.mScreenElement = screenElement;
        this.mAttrs = strArr;
        this.mCurValues = new double[strArr.length];
        load(element, str);
    }

    private float getRatio(AnimationItem animationItem, long j, long j2, long j3) {
        InterpolatorHelper interpolatorHelper;
        float f = j3 == 0 ? 1.0f : ((float) (j - j2)) / ((float) j3);
        return (animationItem == null || (interpolatorHelper = animationItem.mInterpolator) == null) ? f : interpolatorHelper.get(f);
    }

    private void load(Element element, String str) {
        this.mName = element.getAttribute("name");
        this.mHasName = !TextUtils.isEmpty(r0);
        Variables variables = getVariables();
        if (this.mHasName) {
            this.mCurrentFrame = new IndexedVariable(this.mName + ".current_frame", variables, true);
        }
        this.mDelay = Expression.build(variables, element.getAttribute("delay"));
        this.mInitPaused = Boolean.parseBoolean(element.getAttribute("initPause"));
        this.mLoop = !"false".equalsIgnoreCase(element.getAttribute("loop"));
        this.mTag = element.getAttribute("tag");
        Utils.traverseXmlElementChildrenTags(element, new String[]{str, "Item"}, new Utils.XmlTraverseListener() { // from class: com.miui.maml.animation.BaseAnimation.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                BaseAnimation baseAnimation = BaseAnimation.this;
                baseAnimation.mItems.add(baseAnimation.onCreateItem(baseAnimation, element2));
            }
        });
        if (this.mItems.size() <= 0) {
            Log.e("BaseAnimation", "empty items");
            return;
        }
        ArrayList<AnimationItem> arrayList = this.mItems;
        this.mIsTimeInfinite = arrayList.get(arrayList.size() - 1).mTime >= 1000000000000L;
        if (this.mItems.size() <= 1 || !this.mIsTimeInfinite) {
            ArrayList<AnimationItem> arrayList2 = this.mItems;
            this.mRealTimeRange = arrayList2.get(arrayList2.size() - 1).mTime;
        } else {
            ArrayList<AnimationItem> arrayList3 = this.mItems;
            this.mRealTimeRange = arrayList3.get(arrayList3.size() - 2).mTime;
        }
        Element child = Utils.getChild(element, "Triggers");
        if (child != null) {
            this.mTriggers = new CommandTriggers(child, this.mScreenElement);
        }
    }

    private void reevaluate() {
        int size = this.mItems.size();
        long j = 0;
        for (int i = 0; i < size; i++) {
            AnimationItem animationItem = this.mItems.get(i);
            Expression expression = animationItem.mDeltaTimeExp;
            if (expression != null) {
                long evaluate = (long) expression.evaluate();
                if (evaluate < 0) {
                    evaluate = 0;
                }
                j += evaluate;
                animationItem.mTime = j;
            } else {
                long j2 = animationItem.mTime;
                if (j2 >= j) {
                    j = j2;
                }
            }
        }
        boolean z = j >= 1000000000000L;
        this.mIsTimeInfinite = z;
        if (size <= 1 || !z) {
            this.mRealTimeRange = j;
        } else {
            this.mRealTimeRange = this.mItems.get(size - 2).mTime;
        }
    }

    private void resetTime() {
        if (this.mIsFirstReset) {
            this.mIsFirstReset = false;
        }
        this.mIsLastFrame = false;
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            this.mItems.get(i).reset();
        }
        reevaluate();
        this.mAnimStartTime = transToAnimTime(this.mStartTime);
        long transToAnimTime = transToAnimTime(this.mEndTime);
        this.mAnimEndTime = transToAnimTime;
        this.mPlayTimeRange = Math.abs(transToAnimTime - this.mAnimStartTime);
    }

    private long transToAnimTime(long j) {
        return (j == -1 || j > this.mRealTimeRange) ? this.mRealTimeRange : j;
    }

    public void finish() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.finish();
        }
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            this.mItems.get(i).reset();
        }
        int length = this.mCurValues.length;
        for (int i2 = 0; i2 < length; i2++) {
            this.mCurValues[i2] = 0.0d;
        }
    }

    public String[] getAttrs() {
        return this.mAttrs;
    }

    public double getCurValue(int i) {
        return this.mCurValues[i];
    }

    protected double getDefaultValue() {
        return 0.0d;
    }

    protected double getDelayValue(int i) {
        AnimationItem item = getItem(0);
        if (item != null) {
            return item.get(i);
        }
        return 0.0d;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AnimationItem getItem(int i) {
        if (i < 0 || i >= this.mItems.size()) {
            return null;
        }
        return this.mItems.get(i);
    }

    public String getTag() {
        return this.mTag;
    }

    protected final Variables getVariables() {
        return this.mScreenElement.getVariables();
    }

    public void init() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.init();
        }
    }

    public void onAction(String str) {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.onAction(str);
        }
    }

    protected AnimationItem onCreateItem(BaseAnimation baseAnimation, Element element) {
        return new AnimationItem(baseAnimation, element);
    }

    protected void onTick(AnimationItem animationItem, AnimationItem animationItem2, float f) {
        if (animationItem == null && animationItem2 == null) {
            return;
        }
        double defaultValue = getDefaultValue();
        int length = this.mAttrs.length;
        for (int i = 0; i < length; i++) {
            double d = animationItem == null ? defaultValue : animationItem.get(i);
            this.mCurValues[i] = d + ((animationItem2.get(i) - d) * f);
        }
    }

    public void pause() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.pause();
        }
    }

    public void pauseAnim(long j) {
        if (this.mDisable || this.mIsPaused) {
            return;
        }
        this.mIsPaused = true;
        this.mPauseTime = j;
    }

    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        Expression expression;
        if (this.mDisable) {
            return;
        }
        this.mResetTime = j;
        if (j2 < 0 && j2 != -1) {
            j2 = 0;
        }
        this.mStartTime = j2;
        this.mAnimStartTime = j2;
        if (j3 < 0 && j3 != -1) {
            j3 = 0;
        }
        this.mEndTime = j3;
        this.mAnimEndTime = j3;
        this.mIsLoop = z;
        this.mIsDelay = z2;
        this.mIsReverse = j2 == -1 || (j2 >= j3 && j3 >= 0);
        if (j2 == j3) {
            this.mIsLoop = false;
        }
        if (z2 && (expression = this.mDelay) != null) {
            this.mResetTime = (long) (j + expression.evaluate());
        }
        this.mIsFirstFrame = true;
        this.mIsLastFrame = false;
        this.mIsPaused = false;
        this.mIsFirstReset = true;
        this.mPlayTimeRange = 0L;
    }

    public void reset(long j) {
        if (this.mDisable) {
            return;
        }
        int length = this.mAttrs.length;
        for (int i = 0; i < length; i++) {
            this.mCurValues[i] = getDelayValue(i);
        }
        if (this.mInitPaused) {
            playAnim(j, 0L, 0L, false, false);
        } else {
            playAnim(j, 0L, -1L, true, true);
        }
        if (this.mHasName) {
            this.mCurrentFrame.set(0.0d);
        }
        onAction("init");
    }

    public void resume() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.resume();
        }
    }

    public void resumeAnim(long j) {
        if (!this.mDisable && this.mIsPaused) {
            this.mIsPaused = false;
            this.mResetTime += j - this.mPauseTime;
        }
    }

    public void setCurValue(int i, double d) {
        this.mCurValues[i] = d;
    }

    public void setDisable(boolean z) {
        this.mDisable = z;
    }

    public final void tick(long j) {
        long j2;
        AnimationItem animationItem;
        long j3;
        if (this.mIsPaused || this.mDisable) {
            return;
        }
        long j4 = j - this.mResetTime;
        int i = 0;
        if (j4 < 0) {
            if (!this.mIsFirstFrame) {
                onTick(null, null, 0.0f);
                return;
            } else {
                this.mIsFirstFrame = false;
                j4 = 0;
            }
        }
        if (this.mIsFirstReset || (this.mIsLastFrame && !this.mIsTimeInfinite && this.mLoop && this.mIsLoop)) {
            resetTime();
        }
        if (!(!this.mIsTimeInfinite && this.mLoop && this.mIsLoop) && this.mIsLastFrame) {
            this.mIsPaused = true;
            this.mPauseTime = this.mResetTime + this.mPlayTimeRange;
            if (this.mHasName) {
                this.mCurrentFrame.set(this.mEndTime);
            }
            onAction("end");
            return;
        }
        long j5 = this.mPlayTimeRange;
        if (j4 >= j5) {
            this.mResetTime = j - (j4 % (j5 + 1));
            this.mIsLastFrame = true;
            j4 = j5;
        }
        long j6 = (this.mIsReverse ? this.mAnimStartTime - j4 : this.mAnimStartTime + j4) % (this.mRealTimeRange + 1);
        int size = this.mItems.size();
        AnimationItem animationItem2 = null;
        while (i < size) {
            AnimationItem animationItem3 = this.mItems.get(i);
            long j7 = animationItem3.mTime;
            if (j6 < j7) {
                if (i == 0) {
                    j2 = 0;
                    animationItem = null;
                    j3 = j7;
                } else {
                    AnimationItem animationItem4 = this.mItems.get(i - 1);
                    long j8 = animationItem3.mTime;
                    long j9 = animationItem4.mTime;
                    j2 = j9;
                    animationItem = animationItem4;
                    j3 = j8 - j9;
                }
                onTick(animationItem, animationItem3, getRatio(animationItem, j6, j2, j3));
                return;
            }
            i++;
            animationItem2 = animationItem3;
        }
        onTick(null, animationItem2, 1.0f);
    }
}
