package androidx.constraintlayout.motion.utils;

import java.util.Arrays;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ArcCurveFit extends CurveFit {
    Arc[] mArcs;
    private final double[] mTime;

    /* loaded from: classes.dex */
    private static class Arc {
        private static double[] ourPercent = new double[91];
        boolean linear;
        double mArcDistance;
        double mArcVelocity;
        double mEllipseA;
        double mEllipseB;
        double mEllipseCenterX;
        double mEllipseCenterY;
        double[] mLut;
        double mOneOverDeltaTime;
        double mTime1;
        double mTime2;
        double mTmpCosAngle;
        double mTmpSinAngle;
        boolean mVertical;
        double mX1;
        double mX2;
        double mY1;
        double mY2;

        Arc(int i, double d, double d2, double d3, double d4, double d5, double d6) {
            this.linear = false;
            this.mVertical = i == 1;
            this.mTime1 = d;
            this.mTime2 = d2;
            this.mOneOverDeltaTime = 1.0d / (d2 - d);
            if (3 == i) {
                this.linear = true;
            }
            double d7 = d5 - d3;
            double d8 = d6 - d4;
            if (!this.linear && Math.abs(d7) >= 0.001d && Math.abs(d8) >= 0.001d) {
                this.mLut = new double[101];
                boolean z = this.mVertical;
                this.mEllipseA = d7 * (z ? -1 : 1);
                this.mEllipseB = d8 * (z ? 1 : -1);
                this.mEllipseCenterX = z ? d5 : d3;
                this.mEllipseCenterY = z ? d4 : d6;
                buildTable(d3, d4, d5, d6);
                this.mArcVelocity = this.mArcDistance * this.mOneOverDeltaTime;
                return;
            }
            this.linear = true;
            this.mX1 = d3;
            this.mX2 = d5;
            this.mY1 = d4;
            this.mY2 = d6;
            double hypot = Math.hypot(d8, d7);
            this.mArcDistance = hypot;
            this.mArcVelocity = hypot * this.mOneOverDeltaTime;
            double d9 = this.mTime2;
            double d10 = this.mTime1;
            this.mEllipseCenterX = d7 / (d9 - d10);
            this.mEllipseCenterY = d8 / (d9 - d10);
        }

        private void buildTable(double d, double d2, double d3, double d4) {
            double d5;
            double d6 = d3 - d;
            double d7 = d2 - d4;
            int i = 0;
            double d8 = 0.0d;
            double d9 = 0.0d;
            double d10 = 0.0d;
            while (true) {
                if (i >= ourPercent.length) {
                    break;
                }
                double d11 = d8;
                double radians = Math.toRadians((i * 90.0d) / ((double) (r15.length - 1)));
                double sin = Math.sin(radians) * d6;
                double cos = Math.cos(radians) * d7;
                if (i > 0) {
                    d5 = Math.hypot(sin - d9, cos - d10) + d11;
                    ourPercent[i] = d5;
                } else {
                    d5 = d11;
                }
                i++;
                d10 = cos;
                d8 = d5;
                d9 = sin;
            }
            double d12 = d8;
            this.mArcDistance = d12;
            int i2 = 0;
            while (true) {
                double[] dArr = ourPercent;
                if (i2 >= dArr.length) {
                    break;
                }
                dArr[i2] = dArr[i2] / d12;
                i2++;
            }
            int i3 = 0;
            while (true) {
                if (i3 >= this.mLut.length) {
                    return;
                }
                double length = i3 / ((double) (r1.length - 1));
                int binarySearch = Arrays.binarySearch(ourPercent, length);
                if (binarySearch >= 0) {
                    this.mLut[i3] = binarySearch / (ourPercent.length - 1);
                } else if (binarySearch == -1) {
                    this.mLut[i3] = 0.0d;
                } else {
                    int i4 = -binarySearch;
                    int i5 = i4 - 2;
                    double[] dArr2 = ourPercent;
                    this.mLut[i3] = (i5 + ((length - dArr2[i5]) / (dArr2[i4 - 1] - dArr2[i5]))) / ((double) (dArr2.length - 1));
                }
                i3++;
            }
        }

        double getDX() {
            double d = this.mEllipseA * this.mTmpCosAngle;
            double hypot = this.mArcVelocity / Math.hypot(d, (-this.mEllipseB) * this.mTmpSinAngle);
            if (this.mVertical) {
                d = -d;
            }
            return d * hypot;
        }

        double getDY() {
            double d = this.mEllipseA * this.mTmpCosAngle;
            double d2 = (-this.mEllipseB) * this.mTmpSinAngle;
            double hypot = this.mArcVelocity / Math.hypot(d, d2);
            return this.mVertical ? (-d2) * hypot : d2 * hypot;
        }

        public double getLinearDX(double d) {
            return this.mEllipseCenterX;
        }

        public double getLinearDY(double d) {
            return this.mEllipseCenterY;
        }

        public double getLinearX(double d) {
            double d2 = (d - this.mTime1) * this.mOneOverDeltaTime;
            double d3 = this.mX1;
            return d3 + (d2 * (this.mX2 - d3));
        }

        public double getLinearY(double d) {
            double d2 = (d - this.mTime1) * this.mOneOverDeltaTime;
            double d3 = this.mY1;
            return d3 + (d2 * (this.mY2 - d3));
        }

        double getX() {
            return this.mEllipseCenterX + (this.mEllipseA * this.mTmpSinAngle);
        }

        double getY() {
            return this.mEllipseCenterY + (this.mEllipseB * this.mTmpCosAngle);
        }

        double lookup(double d) {
            if (d <= 0.0d) {
                return 0.0d;
            }
            if (d >= 1.0d) {
                return 1.0d;
            }
            double[] dArr = this.mLut;
            double length = d * ((double) (dArr.length - 1));
            int i = (int) length;
            return dArr[i] + ((length - i) * (dArr[i + 1] - dArr[i]));
        }

        void setPoint(double d) {
            double lookup = lookup((this.mVertical ? this.mTime2 - d : d - this.mTime1) * this.mOneOverDeltaTime) * 1.5707963267948966d;
            this.mTmpSinAngle = Math.sin(lookup);
            this.mTmpCosAngle = Math.cos(lookup);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0026, code lost:
    
        if (r5 == 1) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ArcCurveFit(int[] r25, double[] r26, double[][] r27) {
        /*
            r24 = this;
            r0 = r24
            r1 = r26
            r24.<init>()
            r0.mTime = r1
            int r2 = r1.length
            r3 = 1
            int r2 = r2 - r3
            androidx.constraintlayout.motion.utils.ArcCurveFit$Arc[] r2 = new androidx.constraintlayout.motion.utils.ArcCurveFit.Arc[r2]
            r0.mArcs = r2
            r2 = 0
            r4 = r2
            r5 = r3
            r6 = r5
        L14:
            androidx.constraintlayout.motion.utils.ArcCurveFit$Arc[] r7 = r0.mArcs
            int r8 = r7.length
            if (r4 >= r8) goto L51
            r8 = r25[r4]
            r9 = 3
            r10 = 2
            if (r8 == 0) goto L2d
            if (r8 == r3) goto L2a
            if (r8 == r10) goto L28
            if (r8 == r9) goto L26
            goto L2e
        L26:
            if (r5 != r3) goto L2a
        L28:
            r5 = r10
            goto L2b
        L2a:
            r5 = r3
        L2b:
            r6 = r5
            goto L2e
        L2d:
            r6 = r9
        L2e:
            androidx.constraintlayout.motion.utils.ArcCurveFit$Arc r22 = new androidx.constraintlayout.motion.utils.ArcCurveFit$Arc
            r10 = r1[r4]
            int r23 = r4 + 1
            r12 = r1[r23]
            r8 = r27[r4]
            r14 = r8[r2]
            r8 = r27[r4]
            r16 = r8[r3]
            r8 = r27[r23]
            r18 = r8[r2]
            r8 = r27[r23]
            r20 = r8[r3]
            r8 = r22
            r9 = r6
            r8.<init>(r9, r10, r12, r14, r16, r18, r20)
            r7[r4] = r22
            r4 = r23
            goto L14
        L51:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.motion.utils.ArcCurveFit.<init>(int[], double[], double[][]):void");
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double getPos(double d, int i) {
        Arc[] arcArr = this.mArcs;
        int i2 = 0;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        } else if (d > arcArr[arcArr.length - 1].mTime2) {
            d = arcArr[arcArr.length - 1].mTime2;
        }
        while (true) {
            Arc[] arcArr2 = this.mArcs;
            if (i2 >= arcArr2.length) {
                return Double.NaN;
            }
            if (d <= arcArr2[i2].mTime2) {
                if (arcArr2[i2].linear) {
                    return i == 0 ? arcArr2[i2].getLinearX(d) : arcArr2[i2].getLinearY(d);
                }
                arcArr2[i2].setPoint(d);
                return i == 0 ? this.mArcs[i2].getX() : this.mArcs[i2].getY();
            }
            i2++;
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getPos(double d, double[] dArr) {
        Arc[] arcArr = this.mArcs;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        }
        if (d > arcArr[arcArr.length - 1].mTime2) {
            d = arcArr[arcArr.length - 1].mTime2;
        }
        int i = 0;
        while (true) {
            Arc[] arcArr2 = this.mArcs;
            if (i >= arcArr2.length) {
                return;
            }
            if (d <= arcArr2[i].mTime2) {
                if (arcArr2[i].linear) {
                    dArr[0] = arcArr2[i].getLinearX(d);
                    dArr[1] = this.mArcs[i].getLinearY(d);
                    return;
                }
                arcArr2[i].setPoint(d);
                dArr[0] = this.mArcs[i].getX();
                dArr[1] = this.mArcs[i].getY();
                return;
            }
            i++;
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getPos(double d, float[] fArr) {
        Arc[] arcArr = this.mArcs;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        } else if (d > arcArr[arcArr.length - 1].mTime2) {
            d = arcArr[arcArr.length - 1].mTime2;
        }
        int i = 0;
        while (true) {
            Arc[] arcArr2 = this.mArcs;
            if (i >= arcArr2.length) {
                return;
            }
            if (d <= arcArr2[i].mTime2) {
                if (arcArr2[i].linear) {
                    fArr[0] = (float) arcArr2[i].getLinearX(d);
                    fArr[1] = (float) this.mArcs[i].getLinearY(d);
                    return;
                }
                arcArr2[i].setPoint(d);
                fArr[0] = (float) this.mArcs[i].getX();
                fArr[1] = (float) this.mArcs[i].getY();
                return;
            }
            i++;
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double getSlope(double d, int i) {
        Arc[] arcArr = this.mArcs;
        int i2 = 0;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        }
        if (d > arcArr[arcArr.length - 1].mTime2) {
            d = arcArr[arcArr.length - 1].mTime2;
        }
        while (true) {
            Arc[] arcArr2 = this.mArcs;
            if (i2 >= arcArr2.length) {
                return Double.NaN;
            }
            if (d <= arcArr2[i2].mTime2) {
                if (arcArr2[i2].linear) {
                    return i == 0 ? arcArr2[i2].getLinearDX(d) : arcArr2[i2].getLinearDY(d);
                }
                arcArr2[i2].setPoint(d);
                return i == 0 ? this.mArcs[i2].getDX() : this.mArcs[i2].getDY();
            }
            i2++;
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getSlope(double d, double[] dArr) {
        Arc[] arcArr = this.mArcs;
        if (d < arcArr[0].mTime1) {
            d = arcArr[0].mTime1;
        } else if (d > arcArr[arcArr.length - 1].mTime2) {
            d = arcArr[arcArr.length - 1].mTime2;
        }
        int i = 0;
        while (true) {
            Arc[] arcArr2 = this.mArcs;
            if (i >= arcArr2.length) {
                return;
            }
            if (d <= arcArr2[i].mTime2) {
                if (arcArr2[i].linear) {
                    dArr[0] = arcArr2[i].getLinearDX(d);
                    dArr[1] = this.mArcs[i].getLinearDY(d);
                    return;
                }
                arcArr2[i].setPoint(d);
                dArr[0] = this.mArcs[i].getDX();
                dArr[1] = this.mArcs[i].getDY();
                return;
            }
            i++;
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double[] getTimePoints() {
        return this.mTime;
    }
}
