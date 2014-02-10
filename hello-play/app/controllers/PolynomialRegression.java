package controllers;
import Jama.Matrix;
import Jama.QRDecomposition;
import models.Earthquake;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

public class PolynomialRegression {
    private final int N;
    private final int degree;
    private Matrix beta = null;
    private double SSE = 0d;
    private double SST = 0d;
    
    public static Date getLastMonth(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, - 30);
        return cal.getTime();
    }

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
    
    public PolynomialRegression(List<Earthquake> eqList, int degree) {
        this.degree = degree;
        if (eqList.size() == 0){
            N = 0;
            return;
        }
        if (eqList.size() == 1){
            N = 1;
            return;
        }
        List<Double> y2 = new ArrayList<Double>();
        Date lastEq = eqList.get(0).date;
        double maxMag = 0;//eqList.get(0).magnitude;

        //System.out.println(lastEq);
        List<Double> x2 = new ArrayList<Double>();
        double max = 0d;
        for (int i = 1; i < eqList.size(); i++){
            if (eqList.get(i).date.before(getLastMonth(lastEq))){
                if (max < maxMag){
                    max = maxMag;
                }
                x2.add(new Double(y2.size()));
                y2.add(maxMag);
                maxMag = 0;
                lastEq = eqList.get(i).date;
                //System.out.println(lastEq);
            } else {
                maxMag ++;
                /*if (eqList.get(i).magnitude > maxMag){
                    maxMag = eqList.get(i).magnitude;
                }*/
            }
        }
        x2.add(new Double(y2.size()));
        y2.add(maxMag);
        double[] x = new double[x2.size()];
        double[] y = new double[x2.size()];
        
        for (int i =0 ;i<y2.size();i++){
            x[i] = x2.get(i);
            y[i] = y2.get(i);
        }

        
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
        if (N == 0)
            return 0;
        if (N == 1)
            return 1;
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
