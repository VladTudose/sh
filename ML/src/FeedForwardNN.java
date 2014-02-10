import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.data.norm.MaxNormalizer;
import org.neuroph.core.data.norm.Normalizer;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 2/9/14
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeedForwardNN {
    public static Date getLastMonth(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, - 30);
        return cal.getTime();
    }

    public static List<Double> discretizeEqList(List<Earthquake> eqList){
        List<Double> dList = new ArrayList<Double>();
        Date lastEq = eqList.get(0).date;
        double maxMag = 0;//eqList.get(0).magnitude;

        //System.out.println(lastEq);

        double max = 0;
        for (int i = 1; i < eqList.size(); i++){
            if (eqList.get(i).date.before(getLastMonth(lastEq))){
                if (max < maxMag){
                    max = maxMag;
                }
                dList.add(maxMag);
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
        dList.add(maxMag);
        System.out.println(max);
        return dList;
    }

    public static NeuralNetwork createNN(List<Double> data, Integer n, DataSet trainingSet){
        List<Integer> neuronsInLayers = new ArrayList<Integer>();
        neuronsInLayers.add(n);
        neuronsInLayers.add(2*n+1);
        neuronsInLayers.add(1);

        NeuralNetwork nn = new MultiLayerPerceptron(neuronsInLayers);
        BackPropagation backPropagation = new BackPropagation();
        backPropagation.setMaxError(0.001);
        backPropagation.setMaxIterations(5000);
        nn.setLearningRule(backPropagation);

        nn.learn(trainingSet);

        System.out.println("error: " + backPropagation.getTotalNetworkError());
        System.out.println("iteration: " + backPropagation.getCurrentIteration());
        return nn;
    }

    public static void main(String[] args){
        List<Earthquake> eqList = CSVParser.getEarthquakes("simplified.csv", true);
        List<Double> dList = discretizeEqList(eqList);
        Integer n = new Double(Math.sqrt(dList.size())).intValue();
        DataSet trainingSet = new DataSet(n, 1);

        for (int i = n-1; i < dList.size() - 1; i++){
            //0..i input, i+1 output
            ArrayList<Double> input = new ArrayList<Double>();
            ArrayList<Double> output = new ArrayList<Double>();
            for (int j = i-n+1; j <= i; j++){
                input.add(dList.get(j));
                System.out.print(dList.get(j)+ " ");
            }
            output.add(dList.get(i+1));
            System.out.print(dList.get(i + 1));
            System.out.println();
            trainingSet.addRow(new DataSetRow(input, output));
        }

        Normalizer nm = new MaxNormalizer();
        nm.normalize(trainingSet);


        NeuralNetwork nn = createNN(dList, n, trainingSet);

        for (int i = 0; i < trainingSet.getRowAt(trainingSet.getRows().size() - 1).getInput().length; i++){
            System.out.print( trainingSet.getRowAt(trainingSet.getRows().size() - 1).getInput()[i]+ " ");
        }
        nn.setInput(trainingSet.getRowAt(trainingSet.getRows().size() - 1).getInput());
        nn.calculate();
        double rez = nn.getOutput()[0];
        System.out.println("prediction: " + rez);
    }
}
