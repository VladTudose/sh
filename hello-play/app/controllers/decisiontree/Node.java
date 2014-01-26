package decisiontree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Robert
 * Date: 11/27/11
 * Time: 6:34 AM
 * To change this template use File | Settings | File Templates.
 */

public class Node{
    public String name;
    public int pNum;
    public boolean answer;
    private Boolean leaf;
    private ArrayList<Node> v;
    private ArrayList<String> e;

    public Node(){
        v=new ArrayList<Node>();
        e=new ArrayList<String>();
        leaf=true;
    }

    public void addDescendant(Node newNode,String edge){
        v.add(newNode);
        e.add(edge);
        leaf=false;
    }

    public Boolean isLeaf(){
        return leaf;
    }

    public Boolean getAnswer() throws Exception {
        if (leaf){
            return answer;
        }
        else
            throw new Exception("Cannot get answer from non-leaf Node");
    }

    public List<Node> getV(){
        return v;
    }

    public List<String> getE(){
        return e;
    }
}
