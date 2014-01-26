/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package decisiontree;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author student
 */
public class DecisionTree {
    ArrayList<Parameter> param;
    ArrayList<Boolean> results;
    Boolean defaultAnswer;

    private void addEntry(ArrayList<Parameter> param,ArrayList<Boolean> results,Boolean result,String... values) throws Exception{
        if (param.size()!=values.length)
            throw new Exception("Invalid number of values @ addEntry");
        for (int i=0;i<param.size();i++)
            param.get(i).values.add(values[i]);
        results.add(result);
    }

    public void createPredef() throws Exception {
        param=new ArrayList<Parameter>();
        results=new ArrayList<Boolean>();
        param.add(new Parameter("prognoza"));
        param.add(new Parameter("temperatura"));
        param.add(new Parameter("umiditatea"));
        param.add(new Parameter("vantul"));
        addEntry(param,results,false,"optimista","cald","mare","slab");
        addEntry(param,results,false,"optimista","cald","mare","puternic");
        addEntry(param,results,true,"neutra","cald","mare","slab");
        addEntry(param,results,true,"proasta","optim","mare","slab");
        addEntry(param,results,true,"proasta","frig","normala","slab");
        addEntry(param,results,false,"proasta","frig","normala","puternic");
        addEntry(param,results,true,"neutra","frig","normala","puternic");
        addEntry(param,results,false,"optimista","optim","mare","slab");
        addEntry(param,results,true,"optimista","frig","normala","slab");
        addEntry(param,results,true,"proasta","optim","normala","slab");
        addEntry(param,results,true,"optimista","optim","normala","puternic");
        addEntry(param,results,true,"neutra","optim","mare","puternic");
        addEntry(param,results,true,"neutra","cald","normala","slab");
        addEntry(param,results,false,"proasta","optim","mare","puternic");
  /*    param=new Parameter[1];
        param[0]=new Parameter("A");
        addEntry(param,true,"v2");
        addEntry(param,false,"v2");
        addEntry(param,false,"v3");
        addEntry(param,false,"v1");*/
        getProbabilities(param);
        int poz=0,neg=0;
        for (Boolean b:results){
            if (b==true)
                poz++;
            else
                neg++;
        }
        if (poz>=neg)
            defaultAnswer = true;
        else
            defaultAnswer = false;

    }

    private void getProbabilities( ArrayList<Parameter> param){
        for (Parameter aParam : param) {
            for (String v: aParam.values){
                //aParam -> calculam probabilitatea valorii j al parametrului aParam
                if (aParam.p.containsKey(v)){
                    aParam.p.put(v,aParam.p.get(v)+1);
                }
                else
                    aParam.p.put(v,(double) 1);
            }
        }
    }

    private double computeEntropy(Parameter param){
        double entropy=0;
        Set<String> keys=param.p.keySet();
        for (String k:keys){
            double prob=1/param.p.get(k);
            entropy-=prob*(Math.log(prob)/Math.log(2));
        }
        return entropy;
    }

    private double computeBinaryEntropy(double poz,double neg){
        double p=0,n=0;
        if (poz!=0)
            p=poz *(Math.log(poz)/Math.log(2));
        if (neg!=0)
            n=neg *(Math.log(neg)/Math.log(2));
        return -p - n;
    }



    private double computeGain(Parameter parameter, ArrayList<Boolean> results){
        int poz=0,neg=0;
        for (Boolean b:results){
            if (b==true)
                poz++;
            else
                neg++;
        }
        double gain=computeBinaryEntropy((double)poz/(poz+neg),(double)neg/(poz+neg));

        double temp;
        Set<String> keys=parameter.p.keySet();
        for (String k:keys){//v in possible values of parameter
            poz=neg=0;
            int i=0;
            for (String s:parameter.values){
                if (k.equals(s)){
                    if (results.get(i)==true)
                        poz++;
                    else
                        neg++;
                }
                i++;
            }
            temp=(parameter.p.get(k)/results.size())*computeBinaryEntropy((double)poz/(poz+neg),(double)neg/(poz+neg));
            gain-=temp;
        }
        return gain;
    }


    public void computeNode(Node node,ArrayList<Parameter> param,ArrayList<Boolean> results) throws Exception {
    //compute new param and results
        if (param.size()==0){
            node.answer=defaultAnswer;
        }
        Set<String> key= param.get(node.pNum).p.keySet();
        for (String val:key){
            //for each possible value of current parameter

            //add all parameters except the chosen one in the parent
            ArrayList<Parameter> newParam=new ArrayList<Parameter>();
            ArrayList<Boolean> newResults=new ArrayList<Boolean>();
            for (Parameter p:param){
                if (!p.name.equals(node.name))
                    newParam.add(new Parameter(p.name));
            }

            //compute new param and results
            for (int i=0;i<results.size();i++){
                //if the current line is val, then add a new entry
                if (param.get(node.pNum).values.get(i).equals(val)){
                    ArrayList<String> values=new ArrayList<String>();
                    for (Parameter p:param){
                        if (p.name!=node.name)
                            values.add(p.values.get(i));
                    }

                    String[] list=values.toArray(new String[values.size()]);
                    addEntry(newParam, newResults, results.get(i), list);
                }
            }
            //get probabilities of new parameters
            getProbabilities(newParam);

            double maxGain=0,currentGain;
            int iMax=0;
            for (int i=0;i<newParam.size();i++){
                currentGain=computeGain(newParam.get(i),newResults);
                    if (maxGain<currentGain){
                        maxGain=currentGain;
                    iMax=i;
                }
            }


            if (maxGain>0){
                Node newNode=new Node();
                newNode.name=newParam.get(iMax).name;
                newNode.pNum=iMax;
                node.addDescendant(newNode,val);
                computeNode(newNode,newParam,newResults);
            }
            else
            {
                Node newNode=new Node();
                newNode.answer=newResults.get(0);
                node.addDescendant(newNode,val);
            }
        }
    }

    public Node computeTree() throws Exception {
        double maxGain=0,currentGain;
        int iMax=0;
        for (int i=0;i<param.size();i++){
            currentGain=computeGain(param.get(i),results);
                if (maxGain<currentGain){
                    maxGain=currentGain;
                iMax=i;
            }
        }
        Node root=new Node();
        root.name=param.get(iMax).name;
        root.pNum=iMax;
        computeNode(root,param,results);
        return root;
    }

    public void printResults(Node root, Node parent){
        if (root.name == null)
            return;
        System.out.println("-------------------------");
        System.out.println("Node: " + root.name);
        if (parent==null){
            System.out.println("Parent: none");
        } else {
            System.out.println("Parent: " + parent.name);
        }

        for (int i=0;i<root.getE().size();i++){
            String edge = root.getE().get(i);
            Node vertice = root.getV().get(i);
            System.out.println("value: " + edge + ". Is it a leaf (has a definite answer)? " + vertice.isLeaf());
            if (vertice.isLeaf()){
                System.out.println("value: " + edge + " has outcome: " + vertice.answer);
            }
        }
        for (Node node : root.getV()){
            printResults(node, root);
        }
    }

    public Boolean computeResult(Node root, ArrayList<Parameter> parameters,  ArrayList<String> values) throws Exception {
        int j = parameters.indexOf(new Parameter(root.name));
        for (int i=0;i<root.getE().size();i++){
            if (values.get(j).equals(root.getE().get(i))){
                if (root.getV().get(i).isLeaf()){
                    return root.getV().get(i).getAnswer();
                } else {
                    return computeResult(root.getV().get(i), parameters, values);
                }
            }
        }
        throw new Exception("Decision tree has no value to return");
    }

    public static void main(String args[]) throws Exception {
        DecisionTree tree=new DecisionTree();
        tree.createPredef();
        Node root=tree.computeTree();
        tree.printResults(root,null);
        ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("prognoza"));
        parameters.add(new Parameter("temperatura"));
        parameters.add(new Parameter("umiditatea"));
        parameters.add(new Parameter("vantul"));
        ArrayList<String> values = new ArrayList<String>();
        values.add("proasta");
        values.add("");
        values.add("");
        values.add("slab");
        values.add(null);
        values.add(null);
        values.add(null);
        System.out.println(tree.computeResult(root, parameters, values));
    }
}
