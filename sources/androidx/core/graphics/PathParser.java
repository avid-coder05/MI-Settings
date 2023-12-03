package androidx.core.graphics;

import android.graphics.Path;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PathParser {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    /* loaded from: classes.dex */
    public static class PathDataNode {
        public float[] mParams;
        public char mType;

        PathDataNode(char type, float[] params) {
            this.mType = type;
            this.mParams = params;
        }

        PathDataNode(PathDataNode n) {
            this.mType = n.mType;
            float[] fArr = n.mParams;
            this.mParams = PathParser.copyOfRange(fArr, 0, fArr.length);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private static void addCommand(Path path, float[] current, char previousCmd, char cmd, float[] val) {
            int i;
            int i2;
            int i3;
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            float f8;
            char c = cmd;
            char c2 = 0;
            float f9 = current[0];
            float f10 = current[1];
            float f11 = current[2];
            float f12 = current[3];
            float f13 = current[4];
            float f14 = current[5];
            switch (c) {
                case 'A':
                case 'a':
                    i = 7;
                    i2 = i;
                    break;
                case 'C':
                case 'c':
                    i = 6;
                    i2 = i;
                    break;
                case 'H':
                case 'V':
                case 'h':
                case 'v':
                    i2 = 1;
                    break;
                case 'L':
                case 'M':
                case 'T':
                case 'l':
                case 'm':
                case 't':
                default:
                    i2 = 2;
                    break;
                case 'Q':
                case 'S':
                case 'q':
                case 's':
                    i2 = 4;
                    break;
                case 'Z':
                case 'z':
                    path.close();
                    path.moveTo(f13, f14);
                    f9 = f13;
                    f11 = f9;
                    f10 = f14;
                    f12 = f10;
                    i2 = 2;
                    break;
            }
            float f15 = f9;
            float f16 = f10;
            float f17 = f13;
            float f18 = f14;
            int i4 = 0;
            char c3 = previousCmd;
            while (i4 < val.length) {
                if (c != 'A') {
                    if (c == 'C') {
                        i3 = i4;
                        int i5 = i3 + 2;
                        int i6 = i3 + 3;
                        int i7 = i3 + 4;
                        int i8 = i3 + 5;
                        path.cubicTo(val[i3 + 0], val[i3 + 1], val[i5], val[i6], val[i7], val[i8]);
                        f15 = val[i7];
                        float f19 = val[i8];
                        float f20 = val[i5];
                        float f21 = val[i6];
                        f16 = f19;
                        f12 = f21;
                        f11 = f20;
                    } else if (c == 'H') {
                        i3 = i4;
                        int i9 = i3 + 0;
                        path.lineTo(val[i9], f16);
                        f15 = val[i9];
                    } else if (c == 'Q') {
                        i3 = i4;
                        int i10 = i3 + 0;
                        int i11 = i3 + 1;
                        int i12 = i3 + 2;
                        int i13 = i3 + 3;
                        path.quadTo(val[i10], val[i11], val[i12], val[i13]);
                        float f22 = val[i10];
                        float f23 = val[i11];
                        f15 = val[i12];
                        f16 = val[i13];
                        f11 = f22;
                        f12 = f23;
                    } else if (c == 'V') {
                        i3 = i4;
                        int i14 = i3 + 0;
                        path.lineTo(f15, val[i14]);
                        f16 = val[i14];
                    } else if (c != 'a') {
                        if (c != 'c') {
                            if (c == 'h') {
                                int i15 = i4 + 0;
                                path.rLineTo(val[i15], 0.0f);
                                f15 += val[i15];
                            } else if (c != 'q') {
                                if (c == 'v') {
                                    int i16 = i4 + 0;
                                    path.rLineTo(0.0f, val[i16]);
                                    f4 = val[i16];
                                } else if (c == 'L') {
                                    int i17 = i4 + 0;
                                    int i18 = i4 + 1;
                                    path.lineTo(val[i17], val[i18]);
                                    f15 = val[i17];
                                    f16 = val[i18];
                                } else if (c == 'M') {
                                    int i19 = i4 + 0;
                                    f15 = val[i19];
                                    int i20 = i4 + 1;
                                    f16 = val[i20];
                                    if (i4 > 0) {
                                        path.lineTo(val[i19], val[i20]);
                                    } else {
                                        path.moveTo(val[i19], val[i20]);
                                        i3 = i4;
                                        f18 = f16;
                                        f17 = f15;
                                    }
                                } else if (c == 'S') {
                                    if (c3 == 'c' || c3 == 's' || c3 == 'C' || c3 == 'S') {
                                        f15 = (f15 * 2.0f) - f11;
                                        f16 = (f16 * 2.0f) - f12;
                                    }
                                    float f24 = f16;
                                    int i21 = i4 + 0;
                                    int i22 = i4 + 1;
                                    int i23 = i4 + 2;
                                    int i24 = i4 + 3;
                                    path.cubicTo(f15, f24, val[i21], val[i22], val[i23], val[i24]);
                                    f = val[i21];
                                    f2 = val[i22];
                                    f15 = val[i23];
                                    f16 = val[i24];
                                    f11 = f;
                                    f12 = f2;
                                } else if (c == 'T') {
                                    if (c3 == 'q' || c3 == 't' || c3 == 'Q' || c3 == 'T') {
                                        f15 = (f15 * 2.0f) - f11;
                                        f16 = (f16 * 2.0f) - f12;
                                    }
                                    int i25 = i4 + 0;
                                    int i26 = i4 + 1;
                                    path.quadTo(f15, f16, val[i25], val[i26]);
                                    float f25 = val[i25];
                                    float f26 = val[i26];
                                    i3 = i4;
                                    f12 = f16;
                                    f11 = f15;
                                    f15 = f25;
                                    f16 = f26;
                                } else if (c == 'l') {
                                    int i27 = i4 + 0;
                                    int i28 = i4 + 1;
                                    path.rLineTo(val[i27], val[i28]);
                                    f15 += val[i27];
                                    f4 = val[i28];
                                } else if (c == 'm') {
                                    int i29 = i4 + 0;
                                    f15 += val[i29];
                                    int i30 = i4 + 1;
                                    f16 += val[i30];
                                    if (i4 > 0) {
                                        path.rLineTo(val[i29], val[i30]);
                                    } else {
                                        path.rMoveTo(val[i29], val[i30]);
                                        i3 = i4;
                                        f18 = f16;
                                        f17 = f15;
                                    }
                                } else if (c == 's') {
                                    if (c3 == 'c' || c3 == 's' || c3 == 'C' || c3 == 'S') {
                                        float f27 = f15 - f11;
                                        f5 = f16 - f12;
                                        f6 = f27;
                                    } else {
                                        f6 = 0.0f;
                                        f5 = 0.0f;
                                    }
                                    int i31 = i4 + 0;
                                    int i32 = i4 + 1;
                                    int i33 = i4 + 2;
                                    int i34 = i4 + 3;
                                    path.rCubicTo(f6, f5, val[i31], val[i32], val[i33], val[i34]);
                                    f = val[i31] + f15;
                                    f2 = val[i32] + f16;
                                    f15 += val[i33];
                                    f3 = val[i34];
                                } else if (c == 't') {
                                    if (c3 == 'q' || c3 == 't' || c3 == 'Q' || c3 == 'T') {
                                        f7 = f15 - f11;
                                        f8 = f16 - f12;
                                    } else {
                                        f8 = 0.0f;
                                        f7 = 0.0f;
                                    }
                                    int i35 = i4 + 0;
                                    int i36 = i4 + 1;
                                    path.rQuadTo(f7, f8, val[i35], val[i36]);
                                    float f28 = f7 + f15;
                                    float f29 = f8 + f16;
                                    f15 += val[i35];
                                    f16 += val[i36];
                                    f12 = f29;
                                    f11 = f28;
                                }
                                f16 += f4;
                            } else {
                                int i37 = i4 + 0;
                                int i38 = i4 + 1;
                                int i39 = i4 + 2;
                                int i40 = i4 + 3;
                                path.rQuadTo(val[i37], val[i38], val[i39], val[i40]);
                                f = val[i37] + f15;
                                f2 = val[i38] + f16;
                                f15 += val[i39];
                                f3 = val[i40];
                            }
                            i3 = i4;
                        } else {
                            int i41 = i4 + 2;
                            int i42 = i4 + 3;
                            int i43 = i4 + 4;
                            int i44 = i4 + 5;
                            path.rCubicTo(val[i4 + 0], val[i4 + 1], val[i41], val[i42], val[i43], val[i44]);
                            f = val[i41] + f15;
                            f2 = val[i42] + f16;
                            f15 += val[i43];
                            f3 = val[i44];
                        }
                        f16 += f3;
                        f11 = f;
                        f12 = f2;
                        i3 = i4;
                    } else {
                        int i45 = i4 + 5;
                        int i46 = i4 + 6;
                        i3 = i4;
                        drawArc(path, f15, f16, val[i45] + f15, val[i46] + f16, val[i4 + 0], val[i4 + 1], val[i4 + 2], val[i4 + 3] != 0.0f, val[i4 + 4] != 0.0f);
                        f15 += val[i45];
                        f16 += val[i46];
                    }
                    i4 = i3 + i2;
                    c3 = cmd;
                    c = c3;
                    c2 = 0;
                } else {
                    i3 = i4;
                    int i47 = i3 + 5;
                    int i48 = i3 + 6;
                    drawArc(path, f15, f16, val[i47], val[i48], val[i3 + 0], val[i3 + 1], val[i3 + 2], val[i3 + 3] != 0.0f, val[i3 + 4] != 0.0f);
                    f15 = val[i47];
                    f16 = val[i48];
                }
                f12 = f16;
                f11 = f15;
                i4 = i3 + i2;
                c3 = cmd;
                c = c3;
                c2 = 0;
            }
            current[c2] = f15;
            current[1] = f16;
            current[2] = f11;
            current[3] = f12;
            current[4] = f17;
            current[5] = f18;
        }

        private static void arcToBezier(Path p, double cx, double cy, double a, double b, double e1x, double e1y, double theta, double start, double sweep) {
            double d = a;
            int ceil = (int) Math.ceil(Math.abs((sweep * 4.0d) / 3.141592653589793d));
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            double cos2 = Math.cos(start);
            double sin2 = Math.sin(start);
            double d2 = -d;
            double d3 = d2 * cos;
            double d4 = b * sin;
            double d5 = (d3 * sin2) - (d4 * cos2);
            double d6 = d2 * sin;
            double d7 = b * cos;
            double d8 = (sin2 * d6) + (cos2 * d7);
            double d9 = sweep / ceil;
            double d10 = d8;
            double d11 = d5;
            int i = 0;
            double d12 = e1x;
            double d13 = e1y;
            double d14 = start;
            while (i < ceil) {
                double d15 = d14 + d9;
                double sin3 = Math.sin(d15);
                double cos3 = Math.cos(d15);
                double d16 = (cx + ((d * cos) * cos3)) - (d4 * sin3);
                double d17 = cy + (d * sin * cos3) + (d7 * sin3);
                double d18 = (d3 * sin3) - (d4 * cos3);
                double d19 = (sin3 * d6) + (cos3 * d7);
                double d20 = d15 - d14;
                double tan = Math.tan(d20 / 2.0d);
                double sin4 = (Math.sin(d20) * (Math.sqrt(((tan * 3.0d) * tan) + 4.0d) - 1.0d)) / 3.0d;
                double d21 = d12 + (d11 * sin4);
                p.rLineTo(0.0f, 0.0f);
                p.cubicTo((float) d21, (float) (d13 + (d10 * sin4)), (float) (d16 - (sin4 * d18)), (float) (d17 - (sin4 * d19)), (float) d16, (float) d17);
                i++;
                d9 = d9;
                sin = sin;
                d12 = d16;
                d6 = d6;
                cos = cos;
                d14 = d15;
                d10 = d19;
                d11 = d18;
                ceil = ceil;
                d13 = d17;
                d = a;
            }
        }

        private static void drawArc(Path p, float x0, float y0, float x1, float y1, float a, float b, float theta, boolean isMoreThanHalf, boolean isPositiveArc) {
            double d;
            double d2;
            double radians = Math.toRadians(theta);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double d3 = x0;
            double d4 = d3 * cos;
            double d5 = y0;
            double d6 = a;
            double d7 = (d4 + (d5 * sin)) / d6;
            double d8 = ((-x0) * sin) + (d5 * cos);
            double d9 = b;
            double d10 = d8 / d9;
            double d11 = y1;
            double d12 = ((x1 * cos) + (d11 * sin)) / d6;
            double d13 = (((-x1) * sin) + (d11 * cos)) / d9;
            double d14 = d7 - d12;
            double d15 = d10 - d13;
            double d16 = (d7 + d12) / 2.0d;
            double d17 = (d10 + d13) / 2.0d;
            double d18 = (d14 * d14) + (d15 * d15);
            if (d18 == 0.0d) {
                Log.w("PathParser", " Points are coincident");
                return;
            }
            double d19 = (1.0d / d18) - 0.25d;
            if (d19 < 0.0d) {
                Log.w("PathParser", "Points are too far apart " + d18);
                float sqrt = (float) (Math.sqrt(d18) / 1.99999d);
                drawArc(p, x0, y0, x1, y1, a * sqrt, b * sqrt, theta, isMoreThanHalf, isPositiveArc);
                return;
            }
            double sqrt2 = Math.sqrt(d19);
            double d20 = d14 * sqrt2;
            double d21 = sqrt2 * d15;
            if (isMoreThanHalf == isPositiveArc) {
                d = d16 - d21;
                d2 = d17 + d20;
            } else {
                d = d16 + d21;
                d2 = d17 - d20;
            }
            double atan2 = Math.atan2(d10 - d2, d7 - d);
            double atan22 = Math.atan2(d13 - d2, d12 - d) - atan2;
            if (isPositiveArc != (atan22 >= 0.0d)) {
                atan22 = atan22 > 0.0d ? atan22 - 6.283185307179586d : atan22 + 6.283185307179586d;
            }
            double d22 = d * d6;
            double d23 = d2 * d9;
            arcToBezier(p, (d22 * cos) - (d23 * sin), (d22 * sin) + (d23 * cos), d6, d9, d3, d5, radians, atan2, atan22);
        }

        public static void nodesToPath(PathDataNode[] node, Path path) {
            float[] fArr = new float[6];
            char c = 'm';
            for (int i = 0; i < node.length; i++) {
                addCommand(path, fArr, c, node[i].mType, node[i].mParams);
                c = node[i].mType;
            }
        }

        public void interpolatePathDataNode(PathDataNode nodeFrom, PathDataNode nodeTo, float fraction) {
            this.mType = nodeFrom.mType;
            int i = 0;
            while (true) {
                float[] fArr = nodeFrom.mParams;
                if (i >= fArr.length) {
                    return;
                }
                this.mParams[i] = (fArr[i] * (1.0f - fraction)) + (nodeTo.mParams[i] * fraction);
                i++;
            }
        }
    }

    private static void addNode(ArrayList<PathDataNode> list, char cmd, float[] val) {
        list.add(new PathDataNode(cmd, val));
    }

    public static boolean canMorph(PathDataNode[] nodesFrom, PathDataNode[] nodesTo) {
        if (nodesFrom == null || nodesTo == null || nodesFrom.length != nodesTo.length) {
            return false;
        }
        for (int i = 0; i < nodesFrom.length; i++) {
            if (nodesFrom[i].mType != nodesTo[i].mType || nodesFrom[i].mParams.length != nodesTo[i].mParams.length) {
                return false;
            }
        }
        return true;
    }

    static float[] copyOfRange(float[] original, int start, int end) {
        if (start <= end) {
            int length = original.length;
            if (start < 0 || start > length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int i = end - start;
            int min = Math.min(i, length - start);
            float[] fArr = new float[i];
            System.arraycopy(original, start, fArr, 0, min);
            return fArr;
        }
        throw new IllegalArgumentException();
    }

    public static PathDataNode[] createNodesFromPathData(String pathData) {
        if (pathData == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int i = 1;
        int i2 = 0;
        while (i < pathData.length()) {
            int nextStart = nextStart(pathData, i);
            String trim = pathData.substring(i2, nextStart).trim();
            if (trim.length() > 0) {
                addNode(arrayList, trim.charAt(0), getFloats(trim));
            }
            i2 = nextStart;
            i = nextStart + 1;
        }
        if (i - i2 == 1 && i2 < pathData.length()) {
            addNode(arrayList, pathData.charAt(i2), new float[0]);
        }
        return (PathDataNode[]) arrayList.toArray(new PathDataNode[arrayList.size()]);
    }

    public static Path createPathFromPathData(String pathData) {
        Path path = new Path();
        PathDataNode[] createNodesFromPathData = createNodesFromPathData(pathData);
        if (createNodesFromPathData != null) {
            try {
                PathDataNode.nodesToPath(createNodesFromPathData, path);
                return path;
            } catch (RuntimeException e) {
                throw new RuntimeException("Error in parsing " + pathData, e);
            }
        }
        return null;
    }

    public static PathDataNode[] deepCopyNodes(PathDataNode[] source) {
        if (source == null) {
            return null;
        }
        PathDataNode[] pathDataNodeArr = new PathDataNode[source.length];
        for (int i = 0; i < source.length; i++) {
            pathDataNodeArr[i] = new PathDataNode(source[i]);
        }
        return pathDataNodeArr;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:24:0x003a A[LOOP:0: B:3:0x0007->B:24:0x003a, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x003d A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void extract(java.lang.String r8, int r9, androidx.core.graphics.PathParser.ExtractFloatResult r10) {
        /*
            r0 = 0
            r10.mEndWithNegOrDot = r0
            r1 = r9
            r2 = r0
            r3 = r2
            r4 = r3
        L7:
            int r5 = r8.length()
            if (r1 >= r5) goto L3d
            char r5 = r8.charAt(r1)
            r6 = 32
            r7 = 1
            if (r5 == r6) goto L35
            r6 = 69
            if (r5 == r6) goto L33
            r6 = 101(0x65, float:1.42E-43)
            if (r5 == r6) goto L33
            switch(r5) {
                case 44: goto L35;
                case 45: goto L2a;
                case 46: goto L22;
                default: goto L21;
            }
        L21:
            goto L31
        L22:
            if (r3 != 0) goto L27
            r2 = r0
            r3 = r7
            goto L37
        L27:
            r10.mEndWithNegOrDot = r7
            goto L35
        L2a:
            if (r1 == r9) goto L31
            if (r2 != 0) goto L31
            r10.mEndWithNegOrDot = r7
            goto L35
        L31:
            r2 = r0
            goto L37
        L33:
            r2 = r7
            goto L37
        L35:
            r2 = r0
            r4 = r7
        L37:
            if (r4 == 0) goto L3a
            goto L3d
        L3a:
            int r1 = r1 + 1
            goto L7
        L3d:
            r10.mEndPosition = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.graphics.PathParser.extract(java.lang.String, int, androidx.core.graphics.PathParser$ExtractFloatResult):void");
    }

    private static float[] getFloats(String s) {
        if (s.charAt(0) == 'z' || s.charAt(0) == 'Z') {
            return new float[0];
        }
        try {
            float[] fArr = new float[s.length()];
            ExtractFloatResult extractFloatResult = new ExtractFloatResult();
            int length = s.length();
            int i = 1;
            int i2 = 0;
            while (i < length) {
                extract(s, i, extractFloatResult);
                int i3 = extractFloatResult.mEndPosition;
                if (i < i3) {
                    fArr[i2] = Float.parseFloat(s.substring(i, i3));
                    i2++;
                }
                i = extractFloatResult.mEndWithNegOrDot ? i3 : i3 + 1;
            }
            return copyOfRange(fArr, 0, i2);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error in parsing \"" + s + "\"", e);
        }
    }

    private static int nextStart(String s, int end) {
        while (end < s.length()) {
            char charAt = s.charAt(end);
            if (((charAt - 'A') * (charAt - 'Z') <= 0 || (charAt - 'a') * (charAt - 'z') <= 0) && charAt != 'e' && charAt != 'E') {
                return end;
            }
            end++;
        }
        return end;
    }

    public static void updateNodes(PathDataNode[] target, PathDataNode[] source) {
        for (int i = 0; i < source.length; i++) {
            target[i].mType = source[i].mType;
            for (int i2 = 0; i2 < source[i].mParams.length; i2++) {
                target[i].mParams[i2] = source[i].mParams[i2];
            }
        }
    }
}
