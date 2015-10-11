package math;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

/**
 * A Kalman filter implemented using SimpleMatrix.  The code tends to be easier to
 * read and write, but the performance is degraded due to excessive creation/destruction of
 * memory and the use of more generic algorithms.  This also demonstrates how code can be
 * seamlessly implemented using both SimpleMatrix and DenseMatrix64F.  This allows code
 * to be quickly prototyped or to be written either by novices or experts.
 *
 * @author Peter Abeles
 */
public class KalmanFilter {

    // kinematics description
    private SimpleMatrix F;
    private SimpleMatrix Q;

    // system state estimate
    private SimpleMatrix x;
    private SimpleMatrix P;

    public KalmanFilter() {	
    }
    

    public void configureLinearModel(DenseMatrix64F F) {
        this.F = new SimpleMatrix(F);
    }
    
    public void configureProcessNoise(DenseMatrix64F Q) {
        this.Q = new SimpleMatrix(Q);
    }

    public void setState(DenseMatrix64F x, DenseMatrix64F P) {
        this.x = new SimpleMatrix(x);
        this.P = new SimpleMatrix(P);
    }

    public void predict(DenseMatrix64F control) {
        // x = F x + G u
    	
        x = F.mult(x);
        x = x.plus(SimpleMatrix.wrap(control));
        
        // P = F P F' + Q
        P = F.mult(P).mult(F.transpose()).plus(Q);
    }

    public void update(DenseMatrix64F _z, DenseMatrix64F _H, DenseMatrix64F _R) {
        // a fast way to make the matrices usable by SimpleMatrix
        SimpleMatrix z = SimpleMatrix.wrap(_z);
        SimpleMatrix R = SimpleMatrix.wrap(_R);
        SimpleMatrix H = SimpleMatrix.wrap(_H);

        // y = z - H x
        SimpleMatrix y = z.minus(H.mult(x));

        // S = H P H' + R
        SimpleMatrix S = H.mult(P).mult(H.transpose()).plus(R);

        // K = PH'S^(-1)
        SimpleMatrix K = P.mult(H.transpose().mult(S.invert()));

        // x = x + Ky
        x = x.plus(K.mult(y));

        // P = (I-KH)P = P - KHP
        P = P.minus(K.mult(H).mult(P));
    }

    public DenseMatrix64F getState() {
        return x.getMatrix();
    }

    public DenseMatrix64F getCovariance() {
        return P.getMatrix();
    }
}