package decisiontree;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Robert
 * Date: 11/27/11
 * Time: 4:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class Parameter {
    String name;
    ArrayList<String> values;
    HashMap<String,Double> p;
    Parameter(String newName){
        name=newName;
        values=new ArrayList<String>();
        p=new HashMap<String, Double>();
    }

    @Override
    public boolean equals(Object o){
        if (o.getClass().getName()!=this.getClass().getName()){
            return false;
        }
        Parameter other = (Parameter) o;
        if (!other.name.equals(this.name)){
            return false;
        }
        return true;
    }
}
