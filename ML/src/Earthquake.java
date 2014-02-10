import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 2/9/14
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Earthquake {
    public double magnitude;
    public Date date;
    public Earthquake(Date date, double magnitude){
        this.date = date;
        this.magnitude = magnitude;
    }
}
