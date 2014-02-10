package controllers;
import Jama.Matrix;
import Jama.QRDecomposition;

public class PolynomialRegression {
    private final int N;
    private final int degree;
    private final Matrix beta;
    private double SSE;
    private double SST;

    public PolynomialRegression(double[] x, double[] y, int degree) {
        this.degree = degree;
        N = x.length;

        // build Vandermonde matrix
        double[][] vandermonde = new double[N][degree+1];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <= degree; j++) {
                vandermonde[i][j] = Math.pow(x[i], j);
            }
        }
        Matrix X = new Matrix(vandermonde);

        // create matrix from vector
        Matrix Y = new Matrix(y, N);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        beta = qr.solve(Y);


        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum += y[i];
        double mean = sum / N;

        // total variation to be accounted for
        for (int i = 0; i < N; i++) {
            double dev = y[i] - mean;
            SST += dev*dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(beta).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();

    }

    public double beta(int j) {
        return beta.get(j, 0);
    }

    public int degree() {
        return degree;
    }

    public double R2() {
        return 1.0 - SSE/SST;
    }

    // predicted y value corresponding to x
    public double predict(double x) {
        // horner's method
        double y = 0.0;
        for (int j = degree; j >= 0; j--)
            y = beta(j) + (x * y);
        return y;
    }

    public String toString() {
        String s = "";
        int j = degree;

        // ignoring leading zero coefficients
        while (Math.abs(beta(j)) < 1E-5)
            j--;

        // create remaining terms
        for (j = j; j >= 0; j--) {
            if      (j == 0) s += String.format("%.2f ", beta(j));
            else if (j == 1) s += String.format("%.2f N + ", beta(j));
            else             s += String.format("%.2f N^%d + ", beta(j), j);
        }
        return s + "  (R^2 = " + String.format("%.3f", R2()) + ")";
    }

    public static double autoPredict(double[] x, double[] y, int value){
        int nbChanges = 0;
        double minTrend = Math.ceil(Math.sqrt(x.length));
        double maxNoise = Math.ceil(Math.sqrt(minTrend));

        Boolean increasing;
        int currentTrend = 2, currentNoise = 0;
        if (y[0] > y[1]){
            increasing = false;
        } else {
            increasing = true;
        }
        Boolean lastChange = null;

        for (int i = 2; i < x.length; i++){
            if (increasing == true && y[i] < y[i-1]){
                currentNoise ++;
            } else if (increasing == false && y[i] > y[i-1]){
                currentNoise ++;
            } else {
                currentTrend ++;
            }

            if (currentNoise > currentTrend){
                int aux = currentNoise;
                currentNoise = currentTrend;
                currentTrend = aux;
                increasing = ! increasing;
            }

            if (currentTrend >= minTrend && increasing != lastChange){
                nbChanges ++;
                lastChange = increasing;
            }

            if (currentNoise > maxNoise){
                currentTrend = 2;
                currentNoise = 0;
                if (y[i-1] > y[i]){
                    increasing = false;
                } else {
                    increasing = true;
                }
            }
        }
        if (nbChanges == 0){
            nbChanges ++;
        }
        PolynomialRegression regression = new PolynomialRegression(x, y, nbChanges);
        System.out.println("degree: " + nbChanges);
        return regression.predict(value);
    }

    public static void main(String[] args) {
        double[] x = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        double[] y = { 4, 3, 5, 6, 7, 8, 7, 6, 5, 9, 4, 5, 6, 7, -1, 8};
        double result = PolynomialRegression.autoPredict(x, y, 10);
        System.out.println(result);
        //PolynomialRegression regression = new PolynomialRegression(x, y, 5);
        //System.out.println(regression);
        //System.out.println(regression.predict(10));
    }
}
