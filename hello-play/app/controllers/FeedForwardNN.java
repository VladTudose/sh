package controllers;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.data.norm.MaxNormalizer;
import org.neuroph.core.data.norm.Normalizer;
import org.neuroph.core.transfer.Tanh;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import models.Earthquake;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 2/9/14
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeedForwardNN {
	public static Double max;
    public static Date getLastMonth(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, - 30);
        return cal.getTime();
    }

    public List<Double> discretizeEqList(List<Earthquake> eqList){
        List<Double> dList = new ArrayList<Double>();
        Date lastEq = eqList.get(0).date;
        double maxMag = 0;//eqList.get(0).magnitude;

        //System.out.println(lastEq);

        max = 0d;
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

    public NeuralNetwork createNN(List<Double> data, Integer n, DataSet trainingSet){
        List<Integer> neuronsInLayers = new ArrayList<Integer>();
        neuronsInLayers.add(n);
        neuronsInLayers.add(2*n+1);
        neuronsInLayers.add(1);

        NeuralNetwork nn = new MultiLayerPerceptron(neuronsInLayers);
        Neuron[] neuronList = nn.getInputNeurons();
        TransferFunction tf = new Tanh();
        for (Neuron neuron: neuronList){
        	neuron.setTransferFunction(tf);
        }
        
        BackPropagation backPropagation = new BackPropagation();
        backPropagation.setMaxError(0.001);
        backPropagation.setMaxIterations(5000);
        nn.setLearningRule(backPropagation);

        nn.learn(trainingSet);

        System.out.println("error: " + backPropagation.getTotalNetworkError());
        System.out.println("iteration: " + backPropagation.getCurrentIteration());
        return nn;
    }

    public Double predictNextValue(List<Earthquake> eqList){
        if (eqList.size() == 0){
            return 0d;
        } 
        if (eqList.size() == 1){
            return 1d;
        }
        
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
        System.out.println();
        nn.setInput(trainingSet.getRowAt(trainingSet.getRows().size() - 1).getInput());
        nn.calculate();
        return nn.getOutput()[0] * max;
    }
    
    public static void main(String[] args){
    	FeedForwardNN fNN = new FeedForwardNN();
        List<Earthquake> eqList = CSVParser.getEarthquakes("simplified.csv", true);
    	Double rez = fNN.predictNextValue(eqList);
    	System.out.println("prediction: " + rez);
    }
}
