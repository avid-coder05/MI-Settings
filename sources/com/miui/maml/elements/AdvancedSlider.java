package com.miui.maml.elements;

import android.content.Intent;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import com.miui.maml.ActionCommand;
import com.miui.maml.CommandTrigger;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.interpolater.InterpolatorHelper;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.util.IntentInfo;
import com.miui.maml.util.Task;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class AdvancedSlider extends ElementGroup {
    private EndPoint mCurrentEndPoint;
    private ArrayList<EndPoint> mEndPoints;
    protected boolean mIsHaptic;
    private boolean mIsKeepStatusAfterLaunch;
    private IndexedVariable mMoveDistVar;
    private IndexedVariable mMoveXVar;
    private IndexedVariable mMoveYVar;
    private boolean mMoving;
    private OnLaunchListener mOnLaunchListener;
    private ReboundAnimationController mReboundAnimationController;
    private StartPoint mStartPoint;
    private boolean mStartPointPressed;
    private IndexedVariable mStateVar;
    private float mTouchOffsetX;
    private float mTouchOffsetY;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.elements.AdvancedSlider$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$AdvancedSlider$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$com$miui$maml$elements$AdvancedSlider$State = iArr;
            try {
                iArr[State.Normal.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$AdvancedSlider$State[State.Pressed.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$AdvancedSlider$State[State.Reached.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class CheckTouchResult {
        public EndPoint endPoint;
        public boolean reached;

        private CheckTouchResult() {
        }

        /* synthetic */ CheckTouchResult(AdvancedSlider advancedSlider, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class EndPoint extends SliderPoint {
        public LaunchAction mAction;
        private ArrayList<Position> mPath;
        private Expression mPathX;
        private Expression mPathY;
        private int mRawTolerance;
        private float mTolerance;

        public EndPoint(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot, "EndPoint");
            this.mRawTolerance = 150;
            load(element);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Utils.Point getNearestPoint(float f, float f2) {
            if (this.mPath == null) {
                return new Utils.Point(f - AdvancedSlider.this.mTouchOffsetX, f2 - AdvancedSlider.this.mTouchOffsetY);
            }
            Utils.Point point = null;
            double d = Double.MAX_VALUE;
            for (int i = 1; i < this.mPath.size(); i++) {
                float f3 = f - AdvancedSlider.this.mTouchOffsetX;
                float f4 = f2 - AdvancedSlider.this.mTouchOffsetY;
                Position position = this.mPath.get(i - 1);
                Position position2 = this.mPath.get(i);
                Utils.Point point2 = new Utils.Point(position.getX(), position.getY());
                Utils.Point point3 = new Utils.Point(position2.getX(), position2.getY());
                Utils.Point point4 = new Utils.Point(f3, f4);
                Utils.Point pointProjectionOnSegment = Utils.pointProjectionOnSegment(point2, point3, point4, true);
                double Dist = Utils.Dist(pointProjectionOnSegment, point4, false);
                if (Dist < d) {
                    point = pointProjectionOnSegment;
                    d = Dist;
                }
            }
            return point;
        }

        private void load(Element element) {
            loadTask(element);
            loadPath(element);
        }

        private void loadPath(Element element) {
            Element child = Utils.getChild(element, "Path");
            if (child == null) {
                this.mPath = null;
                return;
            }
            this.mRawTolerance = getAttrAsInt(child, "tolerance", 150);
            this.mPath = new ArrayList<>();
            Variables variables = getVariables();
            this.mPathX = Expression.build(variables, child.getAttribute("x"));
            this.mPathY = Expression.build(variables, child.getAttribute("y"));
            NodeList elementsByTagName = child.getElementsByTagName("Position");
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                this.mPath.add(new Position(variables, (Element) elementsByTagName.item(i), this.mPathX, this.mPathY));
            }
        }

        private void loadTask(Element element) {
            Element child = Utils.getChild(element, "Intent");
            Element child2 = Utils.getChild(element, "Command");
            Element child3 = Utils.getChild(element, "Trigger");
            if (child == null && child2 == null && child3 == null) {
                return;
            }
            LaunchAction launchAction = new LaunchAction(AdvancedSlider.this, null);
            this.mAction = launchAction;
            if (child != null) {
                launchAction.mIntentInfo = new IntentInfo(child, getVariables());
            } else if (child2 == null) {
                if (child3 != null) {
                    launchAction.mTrigger = new CommandTrigger(child3, this.mRoot);
                }
            } else {
                launchAction.mCommand = ActionCommand.create(child2, this.mRoot);
                if (this.mAction.mCommand == null) {
                    Log.w("LockScreen_AdvancedSlider", "invalid Command element: " + child2.toString());
                }
            }
        }

        @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
        public void finish() {
            super.finish();
            LaunchAction launchAction = this.mAction;
            if (launchAction != null) {
                launchAction.finish();
            }
        }

        public float getTransformedDist(Utils.Point point, float f, float f2) {
            if (this.mPath == null) {
                return 1.7014117E38f;
            }
            if (point == null) {
                return Float.MAX_VALUE;
            }
            float Dist = (float) Utils.Dist(point, new Utils.Point(f - AdvancedSlider.this.mTouchOffsetX, f2 - AdvancedSlider.this.mTouchOffsetY), true);
            if (Dist < this.mTolerance) {
                return Dist;
            }
            return Float.MAX_VALUE;
        }

        @Override // com.miui.maml.elements.AdvancedSlider.SliderPoint, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
        public void init() {
            super.init();
            LaunchAction launchAction = this.mAction;
            if (launchAction != null) {
                launchAction.init();
            }
            this.mTolerance = scale(this.mRawTolerance);
        }

        @Override // com.miui.maml.elements.AdvancedSlider.SliderPoint
        protected void onStateChange(State state, State state2) {
            if (state == State.Invalid) {
                return;
            }
            if (AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State[state2.ordinal()] == 3) {
                this.mRoot.playSound(this.mReachedSound);
            }
            super.onStateChange(state, state2);
        }

        @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
        public void pause() {
            super.pause();
            LaunchAction launchAction = this.mAction;
            if (launchAction != null) {
                launchAction.pause();
            }
        }

        @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.ScreenElement
        public void resume() {
            super.resume();
            LaunchAction launchAction = this.mAction;
            if (launchAction != null) {
                launchAction.resume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class InterpolatorController extends ReboundAnimationController {
        private InterpolatorHelper mInterpolator;
        private long mReboundTime;
        private Expression mReboundTimeExp;

        public InterpolatorController(InterpolatorHelper interpolatorHelper, Expression expression) {
            super(AdvancedSlider.this, null);
            this.mInterpolator = interpolatorHelper;
            this.mReboundTimeExp = expression;
        }

        @Override // com.miui.maml.elements.AdvancedSlider.ReboundAnimationController
        protected long getDistance(long j) {
            if (j >= this.mReboundTime) {
                onStop();
                return (long) this.mTotalDistance;
            }
            return (long) (this.mTotalDistance * this.mInterpolator.get(((float) j) / ((float) r0)));
        }

        @Override // com.miui.maml.elements.AdvancedSlider.ReboundAnimationController
        protected void onStart() {
            this.mReboundTime = (long) this.mReboundTimeExp.evaluate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class LaunchAction {
        public ActionCommand mCommand;
        public boolean mConfigTaskLoaded;
        public IntentInfo mIntentInfo;
        public CommandTrigger mTrigger;

        private LaunchAction() {
        }

        /* synthetic */ LaunchAction(AdvancedSlider advancedSlider, AnonymousClass1 anonymousClass1) {
            this();
        }

        private Intent performTask() {
            IntentInfo intentInfo = this.mIntentInfo;
            if (intentInfo == null) {
                return null;
            }
            if (!this.mConfigTaskLoaded) {
                Task findTask = AdvancedSlider.this.mRoot.findTask(intentInfo.getId());
                if (findTask != null && !TextUtils.isEmpty(findTask.action)) {
                    this.mIntentInfo.set(findTask);
                }
                this.mConfigTaskLoaded = true;
            }
            if (Utils.isProtectedIntent(this.mIntentInfo.getAction())) {
                return null;
            }
            Intent intent = new Intent();
            this.mIntentInfo.update(intent);
            intent.setFlags(872415232);
            return intent;
        }

        public void finish() {
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.finish();
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.finish();
            }
            this.mConfigTaskLoaded = false;
        }

        public void init() {
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.init();
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.init();
            }
        }

        public void pause() {
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.pause();
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.pause();
            }
        }

        public Intent perform() {
            if (this.mIntentInfo != null) {
                return performTask();
            }
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.perform();
                return null;
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.perform();
                return null;
            }
            return null;
        }

        public void resume() {
            ActionCommand actionCommand = this.mCommand;
            if (actionCommand != null) {
                actionCommand.resume();
            }
            CommandTrigger commandTrigger = this.mTrigger;
            if (commandTrigger != null) {
                commandTrigger.resume();
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface OnLaunchListener {
        boolean onLaunch(String str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Position {
        private Expression mBaseX;
        private Expression mBaseY;
        private Expression mX;
        private Expression mY;

        public Position(Variables variables, Element element, Expression expression, Expression expression2) {
            this.mBaseX = expression;
            this.mBaseY = expression2;
            this.mX = Expression.build(variables, AdvancedSlider.this.getAttr(element, "x"));
            this.mY = Expression.build(variables, AdvancedSlider.this.getAttr(element, "y"));
        }

        public float getX() {
            AdvancedSlider advancedSlider = AdvancedSlider.this;
            Expression expression = this.mX;
            double evaluate = expression == null ? 0.0d : expression.evaluate();
            Expression expression2 = this.mBaseX;
            return advancedSlider.scale(evaluate + (expression2 != null ? expression2.evaluate() : 0.0d));
        }

        public float getY() {
            AdvancedSlider advancedSlider = AdvancedSlider.this;
            Expression expression = this.mY;
            double evaluate = expression == null ? 0.0d : expression.evaluate();
            Expression expression2 = this.mBaseY;
            return advancedSlider.scale(evaluate + (expression2 != null ? expression2.evaluate() : 0.0d));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public abstract class ReboundAnimationController implements ITicker {
        private int mBounceStartPointIndex;
        private EndPoint mEndPoint;
        private long mPreDistance;
        protected long mStartTime;
        private float mStartX;
        private float mStartY;
        protected double mTotalDistance;

        private ReboundAnimationController() {
            this.mStartTime = -1L;
        }

        /* synthetic */ ReboundAnimationController(AdvancedSlider advancedSlider, AnonymousClass1 anonymousClass1) {
            this();
        }

        private Utils.Point getPoint(float f, float f2, float f3, float f4, long j) {
            Utils.Point point = new Utils.Point(f, f2);
            Utils.Point point2 = new Utils.Point(f3, f4);
            double Dist = Utils.Dist(point, point2, true);
            double d = j;
            if (d >= Dist) {
                return null;
            }
            double d2 = (Dist - d) / Dist;
            double d3 = point2.x;
            double d4 = point.x;
            double d5 = point2.y;
            double d6 = point.y;
            return new Utils.Point(d4 + ((d3 - d4) * d2), d6 + ((d5 - d6) * d2));
        }

        protected abstract long getDistance(long j);

        public void init() {
            this.mStartTime = -1L;
        }

        public boolean isRunning() {
            return this.mStartTime >= 0;
        }

        protected void onMove(float f, float f2) {
            AdvancedSlider.this.moveStartPoint(f, f2);
        }

        protected abstract void onStart();

        protected void onStop() {
            this.mStartTime = -1L;
            AdvancedSlider.this.cancelMoving();
        }

        public void start(EndPoint endPoint) {
            this.mStartTime = 0L;
            this.mEndPoint = endPoint;
            this.mStartX = AdvancedSlider.this.mStartPoint.getOffsetX() + AdvancedSlider.this.mStartPoint.getAnchorX();
            float offsetY = AdvancedSlider.this.mStartPoint.getOffsetY() + AdvancedSlider.this.mStartPoint.getAnchorY();
            this.mStartY = offsetY;
            this.mBounceStartPointIndex = -1;
            this.mTotalDistance = 0.0d;
            Utils.Point point = new Utils.Point(this.mStartX, offsetY);
            if (endPoint != null && endPoint.mPath != null) {
                int i = 1;
                while (true) {
                    if (i >= endPoint.mPath.size()) {
                        break;
                    }
                    int i2 = i - 1;
                    Position position = (Position) endPoint.mPath.get(i2);
                    Position position2 = (Position) endPoint.mPath.get(i);
                    Utils.Point point2 = new Utils.Point(position.getX(), position.getY());
                    Utils.Point point3 = new Utils.Point(position2.getX(), position2.getY());
                    Utils.Point pointProjectionOnSegment = Utils.pointProjectionOnSegment(point2, point3, point, false);
                    if (pointProjectionOnSegment != null) {
                        this.mBounceStartPointIndex = i2;
                        this.mTotalDistance += Utils.Dist(point2, pointProjectionOnSegment, true);
                        break;
                    }
                    this.mTotalDistance += Utils.Dist(point2, point3, true);
                    i++;
                }
            } else {
                this.mTotalDistance = Utils.Dist(new Utils.Point(AdvancedSlider.this.mStartPoint.getAnchorX(), AdvancedSlider.this.mStartPoint.getAnchorY()), point, true);
            }
            if (this.mTotalDistance < 3.0d) {
                onStop();
                return;
            }
            onStart();
            AdvancedSlider.this.requestUpdate();
        }

        public void stopRunning() {
            this.mStartTime = -1L;
        }

        @Override // com.miui.maml.elements.ITicker
        public void tick(long j) {
            long j2 = this.mStartTime;
            if (j2 < 0) {
                return;
            }
            if (j2 == 0) {
                this.mStartTime = j;
                this.mPreDistance = 0L;
            } else {
                long distance = getDistance(j - j2);
                if (this.mStartTime < 0) {
                    return;
                }
                EndPoint endPoint = this.mEndPoint;
                if (endPoint != null && endPoint.mPath != null) {
                    float offsetX = AdvancedSlider.this.mStartPoint.getOffsetX() + AdvancedSlider.this.mStartPoint.getAnchorX();
                    float offsetY = AdvancedSlider.this.mStartPoint.getOffsetY() + AdvancedSlider.this.mStartPoint.getAnchorY();
                    long j3 = distance - this.mPreDistance;
                    int i = this.mBounceStartPointIndex;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        Position position = (Position) this.mEndPoint.mPath.get(i);
                        Utils.Point point = getPoint(position.getX(), position.getY(), offsetX, offsetY, j3);
                        if (point != null) {
                            this.mBounceStartPointIndex = i;
                            onMove((float) point.x, (float) point.y);
                            break;
                        } else if (i == 0) {
                            onStop();
                            break;
                        } else {
                            j3 = (long) (j3 - Utils.Dist(new Utils.Point(position.getX(), position.getY()), new Utils.Point(offsetX, offsetY), true));
                            offsetX = position.getX();
                            offsetY = position.getY();
                            i--;
                        }
                    }
                } else {
                    Utils.Point point2 = getPoint(AdvancedSlider.this.mStartPoint.getAnchorX(), AdvancedSlider.this.mStartPoint.getAnchorY(), this.mStartX, this.mStartY, distance);
                    if (point2 == null) {
                        onStop();
                    } else {
                        onMove((float) point2.x, (float) point2.y);
                    }
                }
                this.mPreDistance = distance;
            }
            AdvancedSlider.this.requestUpdate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SliderPoint extends ElementGroup {
        private ScreenElement mCurrentStateElements;
        protected boolean mIsAlignChildren;
        protected String mName;
        protected String mNormalSound;
        protected ElementGroup mNormalStateElements;
        @Deprecated
        private CommandTrigger mNormalStateTrigger;
        private IndexedVariable mPointStateVar;
        protected String mPressedSound;
        protected ElementGroup mPressedStateElements;
        @Deprecated
        private CommandTrigger mPressedStateTrigger;
        protected String mReachedSound;
        private ElementGroup mReachedStateElements;
        @Deprecated
        private CommandTrigger mReachedStateTrigger;
        private State mState;

        public SliderPoint(Element element, ScreenElementRoot screenElementRoot, String str) {
            super(element, screenElementRoot);
            this.mState = State.Invalid;
            load(element, str);
        }

        private void load(Element element, String str) {
            this.mName = getAttr(element, "name");
            this.mNormalSound = getAttr(element, "normalSound");
            this.mPressedSound = getAttr(element, "pressedSound");
            this.mReachedSound = getAttr(element, "reachedSound");
            this.mNormalStateTrigger = loadTrigger(element, "NormalState");
            this.mPressedStateTrigger = loadTrigger(element, "PressedState");
            this.mReachedStateTrigger = loadTrigger(element, "ReachedState");
            if (!TextUtils.isEmpty(this.mName)) {
                this.mPointStateVar = new IndexedVariable(this.mName + ".state", getVariables(), true);
            }
            this.mIsAlignChildren = Boolean.parseBoolean(getAttr(element, "alignChildren"));
        }

        private CommandTrigger loadTrigger(Element element, String str) {
            Element child = Utils.getChild(element, str);
            if (child != null) {
                return CommandTrigger.fromParentElement(child, this.mRoot);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.ScreenElement
        public void doRender(Canvas canvas) {
            canvas.save();
            if (!this.mIsAlignChildren) {
                canvas.translate(-getLeft(), -getTop());
            }
            super.doRender(canvas);
            canvas.restore();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.miui.maml.elements.ElementGroup
        public float getParentLeft() {
            float left = this.mIsAlignChildren ? getLeft() : 0.0f;
            ElementGroup elementGroup = this.mParent;
            return left + (elementGroup != null ? elementGroup.getParentLeft() : 0.0f);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.miui.maml.elements.ElementGroup
        public float getParentTop() {
            float top = this.mIsAlignChildren ? getTop() : 0.0f;
            ElementGroup elementGroup = this.mParent;
            return top + (elementGroup != null ? elementGroup.getParentTop() : 0.0f);
        }

        public State getState() {
            return this.mState;
        }

        @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
        public void init() {
            super.init();
            ElementGroup elementGroup = this.mNormalStateElements;
            if (elementGroup != null) {
                elementGroup.show(true);
            }
            ElementGroup elementGroup2 = this.mPressedStateElements;
            if (elementGroup2 != null) {
                elementGroup2.show(false);
            }
            ElementGroup elementGroup3 = this.mReachedStateElements;
            if (elementGroup3 != null) {
                elementGroup3.show(false);
            }
            setState(State.Normal);
            CommandTrigger commandTrigger = this.mNormalStateTrigger;
            if (commandTrigger != null) {
                commandTrigger.init();
            }
            CommandTrigger commandTrigger2 = this.mPressedStateTrigger;
            if (commandTrigger2 != null) {
                commandTrigger2.init();
            }
            CommandTrigger commandTrigger3 = this.mReachedStateTrigger;
            if (commandTrigger3 != null) {
                commandTrigger3.init();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.miui.maml.elements.ElementGroup
        public ScreenElement onCreateChild(Element element) {
            String tagName = element.getTagName();
            if (tagName.equalsIgnoreCase("NormalState")) {
                ElementGroup elementGroup = new ElementGroup(element, this.mRoot);
                this.mNormalStateElements = elementGroup;
                return elementGroup;
            } else if (tagName.equalsIgnoreCase("PressedState")) {
                ElementGroup elementGroup2 = new ElementGroup(element, this.mRoot);
                this.mPressedStateElements = elementGroup2;
                return elementGroup2;
            } else if (tagName.equalsIgnoreCase("ReachedState")) {
                ElementGroup elementGroup3 = new ElementGroup(element, this.mRoot);
                this.mReachedStateElements = elementGroup3;
                return elementGroup3;
            } else {
                return super.onCreateChild(element);
            }
        }

        protected void onStateChange(State state, State state2) {
            int[] iArr = AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State;
            int i = iArr[state2.ordinal()];
            if (i == 1) {
                CommandTrigger commandTrigger = this.mNormalStateTrigger;
                if (commandTrigger != null) {
                    commandTrigger.perform();
                }
                performAction("normal");
            } else if (i != 2) {
                if (i != 3) {
                    return;
                }
                CommandTrigger commandTrigger2 = this.mReachedStateTrigger;
                if (commandTrigger2 != null) {
                    commandTrigger2.perform();
                }
                performAction("reached");
            } else {
                CommandTrigger commandTrigger3 = this.mPressedStateTrigger;
                if (commandTrigger3 != null) {
                    commandTrigger3.perform();
                }
                performAction("pressed");
                int i2 = iArr[state.ordinal()];
                if (i2 == 1) {
                    performAction("pressed_normal");
                } else if (i2 != 3) {
                } else {
                    performAction("pressed_reached");
                }
            }
        }

        public void setState(State state) {
            boolean z;
            State state2 = this.mState;
            if (state2 == state) {
                return;
            }
            this.mState = state;
            ElementGroup elementGroup = null;
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State[state.ordinal()];
            int i2 = 2;
            if (i == 1) {
                elementGroup = this.mNormalStateElements;
                z = this.mPressedStateElements != null;
                i2 = 0;
            } else if (i == 2) {
                ElementGroup elementGroup2 = this.mPressedStateElements;
                elementGroup = elementGroup2 != null ? elementGroup2 : this.mNormalStateElements;
                z = (elementGroup2 == null || AdvancedSlider.this.mStartPointPressed) ? false : true;
                i2 = 1;
            } else if (i != 3) {
                z = false;
                i2 = 0;
            } else {
                ElementGroup elementGroup3 = this.mReachedStateElements;
                if (elementGroup3 != null) {
                    elementGroup = elementGroup3;
                } else {
                    elementGroup = this.mPressedStateElements;
                    if (elementGroup == null) {
                        elementGroup = this.mNormalStateElements;
                    }
                }
                z = elementGroup3 != null;
            }
            ScreenElement screenElement = this.mCurrentStateElements;
            if (screenElement != elementGroup) {
                if (screenElement != null) {
                    screenElement.show(false);
                }
                if (elementGroup != null) {
                    elementGroup.show(true);
                }
                this.mCurrentStateElements = elementGroup;
            }
            if (elementGroup != null && z) {
                elementGroup.reset();
            }
            IndexedVariable indexedVariable = this.mPointStateVar;
            if (indexedVariable != null) {
                indexedVariable.set(i2);
            }
            onStateChange(state2, this.mState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SpeedAccController extends ReboundAnimationController {
        private int mBounceAccelation;
        private Expression mBounceAccelationExp;
        private int mBounceInitSpeed;
        private Expression mBounceInitSpeedExp;
        private IndexedVariable mBounceProgress;

        public SpeedAccController(Element element) {
            super(AdvancedSlider.this, null);
            this.mBounceInitSpeedExp = Expression.build(AdvancedSlider.this.getVariables(), AdvancedSlider.this.getAttr(element, "bounceInitSpeed"));
            this.mBounceAccelationExp = Expression.build(AdvancedSlider.this.getVariables(), AdvancedSlider.this.getAttr(element, "bounceAcceleration"));
            if (AdvancedSlider.this.mHasName) {
                this.mBounceProgress = new IndexedVariable(AdvancedSlider.this.mName + ".bounce_progress", AdvancedSlider.this.getVariables(), true);
            }
        }

        @Override // com.miui.maml.elements.AdvancedSlider.ReboundAnimationController
        protected long getDistance(long j) {
            int i = this.mBounceInitSpeed;
            int i2 = this.mBounceAccelation;
            long j2 = ((i * j) / 1000) + (((i2 * j) * j) / 2000000);
            if (i + ((i2 * j) / 1000) <= 0) {
                onStop();
                IndexedVariable indexedVariable = this.mBounceProgress;
                if (indexedVariable != null) {
                    indexedVariable.set(1.0d);
                }
            }
            double d = this.mTotalDistance;
            if (d > 0.0d) {
                double d2 = j2 / d;
                IndexedVariable indexedVariable2 = this.mBounceProgress;
                if (indexedVariable2 != null) {
                    indexedVariable2.set(d2 <= 1.0d ? d2 : 1.0d);
                }
            }
            return j2;
        }

        @Override // com.miui.maml.elements.AdvancedSlider.ReboundAnimationController
        public void init() {
            super.init();
            IndexedVariable indexedVariable = this.mBounceProgress;
            if (indexedVariable != null) {
                indexedVariable.set(1.0d);
            }
        }

        @Override // com.miui.maml.elements.AdvancedSlider.ReboundAnimationController
        protected void onStart() {
            Expression expression = this.mBounceInitSpeedExp;
            if (expression != null) {
                this.mBounceInitSpeed = (int) AdvancedSlider.this.evaluate(expression);
            }
            Expression expression2 = this.mBounceAccelationExp;
            if (expression2 != null) {
                this.mBounceAccelation = (int) AdvancedSlider.this.evaluate(expression2);
            }
            IndexedVariable indexedVariable = this.mBounceProgress;
            if (indexedVariable != null) {
                indexedVariable.set(0.0d);
            }
        }

        @Override // com.miui.maml.elements.AdvancedSlider.ReboundAnimationController
        public void start(EndPoint endPoint) {
            if (this.mBounceInitSpeedExp == null) {
                onStop();
            } else {
                super.start(endPoint);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class StartPoint extends SliderPoint {
        private float mAnchorX;
        private float mAnchorY;
        protected float mOffsetX;
        protected float mOffsetY;
        public InterpolatorController mReboundController;

        public StartPoint(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot, "StartPoint");
            this.mAnchorX = Utils.getAttrAsFloat(element, "anchorX", 0.0f);
            this.mAnchorY = Utils.getAttrAsFloat(element, "anchorY", 0.0f);
            InterpolatorHelper create = InterpolatorHelper.create(getVariables(), element);
            Expression build = Expression.build(getVariables(), element.getAttribute("easeTime"));
            if (create == null || build == null) {
                return;
            }
            this.mReboundController = new InterpolatorController(create, build);
        }

        @Override // com.miui.maml.elements.AdvancedSlider.SliderPoint, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.ScreenElement
        public void doRender(Canvas canvas) {
            int save = canvas.save();
            canvas.translate(this.mOffsetX, this.mOffsetY);
            super.doRender(canvas);
            canvas.restoreToCount(save);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
        public void doTick(long j) {
            super.doTick(j);
            InterpolatorController interpolatorController = this.mReboundController;
            if (interpolatorController != null) {
                interpolatorController.tick(j);
            }
        }

        public float getAnchorX() {
            return getLeft() + this.mAnchorX;
        }

        public float getAnchorY() {
            return getTop() + this.mAnchorY;
        }

        public float getOffsetX() {
            return this.mOffsetX;
        }

        public float getOffsetY() {
            return this.mOffsetY;
        }

        @Override // com.miui.maml.elements.AdvancedSlider.SliderPoint, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
        public void init() {
            super.init();
            InterpolatorController interpolatorController = this.mReboundController;
            if (interpolatorController != null) {
                interpolatorController.init();
            }
        }

        public void moveTo(float f, float f2) {
            this.mOffsetX = f;
            this.mOffsetY = f2;
        }

        @Override // com.miui.maml.elements.AdvancedSlider.SliderPoint
        protected void onStateChange(State state, State state2) {
            if (state == State.Invalid) {
                return;
            }
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$elements$AdvancedSlider$State[state2.ordinal()];
            if (i == 1) {
                this.mRoot.playSound(this.mNormalSound);
            } else if (i == 2 && !this.mPressed) {
                this.mRoot.playSound(this.mPressedSound);
            }
            super.onStateChange(state, state2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum State {
        Normal,
        Pressed,
        Reached,
        Invalid
    }

    public AdvancedSlider(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelMoving() {
        resetInner();
        onCancel();
    }

    private boolean checkEndPoint(Utils.Point point, EndPoint endPoint) {
        if (!endPoint.touched((float) point.x, (float) point.y, false)) {
            endPoint.setState(State.Pressed);
            return false;
        }
        State state = endPoint.getState();
        State state2 = State.Reached;
        if (state != state2) {
            endPoint.setState(state2);
            Iterator<EndPoint> it = this.mEndPoints.iterator();
            while (it.hasNext()) {
                EndPoint next = it.next();
                if (next != endPoint) {
                    next.setState(State.Pressed);
                }
            }
            onReach(((SliderPoint) endPoint).mName);
        }
        return true;
    }

    private CheckTouchResult checkTouch(float f, float f2) {
        CheckTouchResult checkTouchResult = new CheckTouchResult(this, null);
        Iterator<EndPoint> it = this.mEndPoints.iterator();
        Utils.Point point = null;
        float f3 = Float.MAX_VALUE;
        while (it.hasNext()) {
            EndPoint next = it.next();
            Utils.Point nearestPoint = next.getNearestPoint(f, f2);
            float transformedDist = next.getTransformedDist(nearestPoint, f, f2);
            if (transformedDist < f3) {
                checkTouchResult.endPoint = next;
                point = nearestPoint;
                f3 = transformedDist;
            }
        }
        boolean z = false;
        if (f3 >= Float.MAX_VALUE) {
            Log.i("LockScreen_AdvancedSlider", "unlock touch canceled due to exceeding tollerance");
            this.mStartPoint.performAction("cancel");
            return null;
        }
        moveStartPoint((float) point.x, (float) point.y);
        if (f3 >= 1.7014117E38f) {
            Iterator<EndPoint> it2 = this.mEndPoints.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                EndPoint next2 = it2.next();
                if (next2.mPath == null && (z = checkEndPoint(point, next2))) {
                    checkTouchResult.endPoint = next2;
                    break;
                }
            }
        } else {
            z = checkEndPoint(point, checkTouchResult.endPoint);
        }
        this.mStartPoint.setState(z ? State.Reached : State.Pressed);
        if (this.mHasName) {
            this.mStateVar.set(z ? 2.0d : 1.0d);
        }
        checkTouchResult.reached = z;
        return checkTouchResult;
    }

    private boolean doLaunch(EndPoint endPoint) {
        this.mStartPoint.performAction("launch");
        endPoint.performAction("launch");
        LaunchAction launchAction = endPoint.mAction;
        return onLaunch(((SliderPoint) endPoint).mName, launchAction != null ? launchAction.perform() : null);
    }

    private void load(Element element) {
        InterpolatorController interpolatorController;
        if (element == null) {
            return;
        }
        if (this.mHasName) {
            this.mStateVar = new IndexedVariable(this.mName + ".state", getVariables(), true);
            this.mMoveXVar = new IndexedVariable(this.mName + ".move_x", getVariables(), true);
            this.mMoveYVar = new IndexedVariable(this.mName + ".move_y", getVariables(), true);
            this.mMoveDistVar = new IndexedVariable(this.mName + ".move_dist", getVariables(), true);
        }
        StartPoint startPoint = this.mStartPoint;
        if (startPoint == null || (interpolatorController = startPoint.mReboundController) == null) {
            SpeedAccController speedAccController = new SpeedAccController(element);
            this.mReboundAnimationController = speedAccController;
            this.mRoot.addPreTicker(speedAccController);
        } else {
            this.mReboundAnimationController = interpolatorController;
        }
        this.mIsHaptic = Boolean.parseBoolean(getAttr(element, "haptic"));
        this.mIsKeepStatusAfterLaunch = Boolean.parseBoolean(getAttr(element, "keepStatusAfterLaunch"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveStartPoint(float f, float f2) {
        float anchorX = f - this.mStartPoint.getAnchorX();
        float anchorY = f2 - this.mStartPoint.getAnchorY();
        this.mStartPoint.moveTo(anchorX, anchorY);
        if (this.mHasName) {
            double descale = descale(anchorX);
            double descale2 = descale(anchorY);
            double sqrt = Math.sqrt((descale * descale) + (descale2 * descale2));
            this.mMoveXVar.set(descale);
            this.mMoveYVar.set(descale2);
            this.mMoveDistVar.set(sqrt);
        }
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        resetInner();
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        this.mReboundAnimationController.init();
        resetInner();
    }

    protected void onCancel() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ElementGroup
    public ScreenElement onCreateChild(Element element) {
        String tagName = element.getTagName();
        if (tagName.equalsIgnoreCase("StartPoint")) {
            StartPoint startPoint = new StartPoint(element, this.mRoot);
            this.mStartPoint = startPoint;
            return startPoint;
        } else if (tagName.equalsIgnoreCase("EndPoint")) {
            EndPoint endPoint = new EndPoint(element, this.mRoot);
            if (this.mEndPoints == null) {
                this.mEndPoints = new ArrayList<>();
            }
            this.mEndPoints.add(endPoint);
            return endPoint;
        } else {
            return super.onCreateChild(element);
        }
    }

    protected boolean onLaunch(String str, Intent intent) {
        OnLaunchListener onLaunchListener = this.mOnLaunchListener;
        return onLaunchListener != null ? onLaunchListener.onLaunch(str) : this.mIsKeepStatusAfterLaunch;
    }

    protected void onMove(float f, float f2) {
    }

    protected void onReach(String str) {
        if (this.mIsHaptic) {
            this.mRoot.haptic(0);
        }
    }

    protected void onRelease() {
        if (this.mIsHaptic) {
            this.mRoot.haptic(1);
        }
    }

    protected void onStart() {
        if (this.mIsHaptic) {
            this.mRoot.haptic(1);
        }
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public boolean onTouch(MotionEvent motionEvent) {
        boolean z;
        boolean z2;
        if (isVisible()) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            float absoluteLeft = x - getAbsoluteLeft();
            float absoluteTop = y - getAbsoluteTop();
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                if (this.mStartPoint.touched(absoluteLeft, absoluteTop, false)) {
                    this.mMoving = true;
                    this.mTouchOffsetX = absoluteLeft - this.mStartPoint.getAnchorX();
                    this.mTouchOffsetY = absoluteTop - this.mStartPoint.getAnchorY();
                    if (this.mReboundAnimationController.isRunning()) {
                        this.mReboundAnimationController.stopRunning();
                        this.mTouchOffsetX -= this.mStartPoint.getOffsetX();
                        this.mTouchOffsetY -= this.mStartPoint.getOffsetY();
                    }
                    this.mStartPoint.setState(State.Pressed);
                    Iterator<EndPoint> it = this.mEndPoints.iterator();
                    while (it.hasNext()) {
                        it.next().setState(State.Pressed);
                    }
                    this.mStartPointPressed = true;
                    if (this.mHasName) {
                        this.mStateVar.set(1.0d);
                    }
                    this.mReboundAnimationController.init();
                    onStart();
                    z = true;
                }
                z = false;
            } else if (actionMasked == 1) {
                if (this.mMoving) {
                    Log.i("LockScreen_AdvancedSlider", "unlock touch up");
                    CheckTouchResult checkTouch = checkTouch(absoluteLeft, absoluteTop);
                    if (checkTouch != null) {
                        if (checkTouch.reached) {
                            z2 = doLaunch(checkTouch.endPoint);
                        } else {
                            this.mStartPoint.performAction("release");
                            EndPoint endPoint = checkTouch.endPoint;
                            if (endPoint != null) {
                                endPoint.performAction("release");
                            }
                            z2 = false;
                        }
                        this.mCurrentEndPoint = checkTouch.endPoint;
                    } else {
                        z2 = false;
                    }
                    this.mMoving = false;
                    if (!z2) {
                        this.mReboundAnimationController.start(this.mCurrentEndPoint);
                    }
                    onRelease();
                    z = true;
                }
                z = false;
            } else if (actionMasked != 2) {
                if (actionMasked == 3 && this.mMoving) {
                    this.mReboundAnimationController.start(null);
                    this.mCurrentEndPoint = null;
                    this.mMoving = false;
                    onRelease();
                    this.mStartPoint.performAction("cancel");
                    z = true;
                }
                z = false;
            } else {
                if (this.mMoving) {
                    CheckTouchResult checkTouch2 = checkTouch(absoluteLeft, absoluteTop);
                    if (checkTouch2 != null) {
                        this.mCurrentEndPoint = checkTouch2.endPoint;
                        onMove(absoluteLeft, absoluteTop);
                    } else {
                        this.mReboundAnimationController.start(this.mCurrentEndPoint);
                        this.mMoving = false;
                        onRelease();
                    }
                    z = true;
                }
                z = false;
            }
            return super.onTouch(motionEvent) || (z && this.mInterceptTouch);
        }
        return false;
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void pause() {
        super.pause();
        resetInner();
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.ScreenElement
    public void reset(long j) {
        super.reset(j);
        resetInner();
    }

    protected void resetInner() {
        if (this.mStartPointPressed) {
            this.mStartPointPressed = false;
            this.mStartPoint.moveTo(0.0f, 0.0f);
            this.mStartPoint.setState(State.Normal);
            Iterator<EndPoint> it = this.mEndPoints.iterator();
            while (it.hasNext()) {
                it.next().setState(State.Normal);
            }
            if (this.mHasName) {
                this.mMoveXVar.set(0.0d);
                this.mMoveYVar.set(0.0d);
                this.mMoveDistVar.set(0.0d);
                this.mStateVar.set(0.0d);
            }
            this.mMoving = false;
            requestUpdate();
        }
    }
}
