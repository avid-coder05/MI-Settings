package com.miui.maml.data;

import com.miui.maml.data.Expression;
import java.util.HashSet;

/* loaded from: classes2.dex */
public class RootExpression extends Expression {
    private boolean mAlwaysEvaluate;
    private double mDoubleValue;
    private Expression mExp;
    private String mStringValue;
    private Variables mVars;
    private VarVersion[] mVersions;
    private HashSet<VarVersion> mVersionSet = new HashSet<>();
    private boolean mIsNumInit = false;
    private boolean mIsStrInit = false;
    private VarVersionVisitor mVarVersionVisitor = null;

    /* loaded from: classes2.dex */
    public static class VarVersion {
        int mIndex;
        private boolean mIsNumber;
        int mVersion;

        public VarVersion(int i, int i2, boolean z) {
            this.mIndex = i;
            this.mVersion = i2;
            this.mIsNumber = z;
        }

        public boolean equals(Object obj) {
            if (obj instanceof VarVersion) {
                VarVersion varVersion = (VarVersion) obj;
                return varVersion.mIsNumber == this.mIsNumber && varVersion.mIndex == this.mIndex;
            }
            return false;
        }

        public int getVer(Variables variables) {
            return variables.getVer(this.mIndex, this.mIsNumber);
        }

        public int hashCode() {
            return this.mIsNumber ? this.mIndex : (-r1) - 1;
        }
    }

    /* loaded from: classes2.dex */
    private static class VarVersionVisitor extends ExpressionVisitor {
        private RootExpression mRoot;

        public VarVersionVisitor(RootExpression rootExpression) {
            this.mRoot = rootExpression;
        }

        @Override // com.miui.maml.data.ExpressionVisitor
        public void visit(Expression expression) {
            if (expression instanceof Expression.VariableExpression) {
                Expression.VariableExpression variableExpression = (Expression.VariableExpression) expression;
                variableExpression.evaluate();
                this.mRoot.addVarVersion(new VarVersion(variableExpression.getIndex(), variableExpression.getVersion(), expression instanceof Expression.NumberVariableExpression));
            } else if (expression instanceof Expression.FunctionExpression) {
                String funName = ((Expression.FunctionExpression) expression).getFunName();
                if ("rand".equals(funName) || "eval".equals(funName) || "preciseeval".equals(funName)) {
                    this.mRoot.mAlwaysEvaluate = true;
                }
            }
        }
    }

    public RootExpression(Variables variables, Expression expression) {
        this.mVars = variables;
        this.mExp = expression;
    }

    @Override // com.miui.maml.data.Expression
    public void accept(ExpressionVisitor expressionVisitor) {
    }

    public void addVarVersion(VarVersion varVersion) {
        this.mVersionSet.add(varVersion);
    }

    @Override // com.miui.maml.data.Expression
    public double evaluate() {
        int ver;
        boolean z = true;
        if (this.mIsNumInit) {
            int i = 0;
            if (!this.mAlwaysEvaluate) {
                if (this.mVersions != null) {
                    boolean z2 = false;
                    while (true) {
                        VarVersion[] varVersionArr = this.mVersions;
                        if (i >= varVersionArr.length) {
                            break;
                        }
                        VarVersion varVersion = varVersionArr[i];
                        if (varVersion != null && varVersion.mVersion != (ver = varVersion.getVer(this.mVars))) {
                            varVersion.mVersion = ver;
                            z2 = true;
                        }
                        i++;
                    }
                    z = z2;
                } else {
                    z = false;
                }
            }
            if (z) {
                this.mDoubleValue = this.mExp.evaluate();
            }
        } else {
            this.mDoubleValue = this.mExp.evaluate();
            if (this.mVarVersionVisitor == null) {
                VarVersionVisitor varVersionVisitor = new VarVersionVisitor(this);
                this.mVarVersionVisitor = varVersionVisitor;
                this.mExp.accept(varVersionVisitor);
                if (this.mVersionSet.size() <= 0) {
                    this.mVersions = null;
                } else {
                    VarVersion[] varVersionArr2 = new VarVersion[this.mVersionSet.size()];
                    this.mVersions = varVersionArr2;
                    this.mVersionSet.toArray(varVersionArr2);
                }
            }
            this.mIsNumInit = true;
        }
        return this.mDoubleValue;
    }

    @Override // com.miui.maml.data.Expression
    public String evaluateStr() {
        int ver;
        boolean z = true;
        if (this.mIsStrInit) {
            int i = 0;
            if (!this.mAlwaysEvaluate) {
                if (this.mVersions != null) {
                    boolean z2 = false;
                    while (true) {
                        VarVersion[] varVersionArr = this.mVersions;
                        if (i >= varVersionArr.length) {
                            break;
                        }
                        VarVersion varVersion = varVersionArr[i];
                        if (varVersion != null && varVersion.mVersion != (ver = varVersion.getVer(this.mVars))) {
                            varVersion.mVersion = ver;
                            z2 = true;
                        }
                        i++;
                    }
                    z = z2;
                } else {
                    z = false;
                }
            }
            if (z) {
                this.mStringValue = this.mExp.evaluateStr();
            }
        } else {
            this.mStringValue = this.mExp.evaluateStr();
            if (this.mVarVersionVisitor == null) {
                VarVersionVisitor varVersionVisitor = new VarVersionVisitor(this);
                this.mVarVersionVisitor = varVersionVisitor;
                this.mExp.accept(varVersionVisitor);
                VarVersion[] varVersionArr2 = new VarVersion[this.mVersionSet.size()];
                this.mVersions = varVersionArr2;
                this.mVersionSet.toArray(varVersionArr2);
            }
            this.mIsStrInit = true;
        }
        return this.mStringValue;
    }

    @Override // com.miui.maml.data.Expression
    public boolean isNull() {
        return this.mExp.isNull();
    }
}
