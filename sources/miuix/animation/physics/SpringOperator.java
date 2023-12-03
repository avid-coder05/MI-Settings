package miuix.animation.physics;

/* loaded from: classes5.dex */
public class SpringOperator implements PhysicsOperator {
    @Override // miuix.animation.physics.PhysicsOperator
    public void getParameters(float[] fArr, double[] dArr) {
        double d = fArr[0];
        double d2 = fArr[1];
        dArr[0] = Math.pow(6.283185307179586d / d2, 2.0d);
        dArr[1] = Math.min((d * 12.566370614359172d) / d2, 60.0d);
    }

    @Override // miuix.animation.physics.PhysicsOperator
    public double updateVelocity(double d, double d2, double d3, double d4, double... dArr) {
        return (d * (1.0d - (d3 * d4))) + ((float) (d2 * (dArr[0] - dArr[1]) * d4));
    }
}
