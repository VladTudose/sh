import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 2/9/14
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CSVParser {
    public static List<Earthquake> getEarthquakes(String csvFile, Boolean hasHeader){
        List<Earthquake> eqList = new ArrayList<Earthquake>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        try {
            br = new BufferedReader(new FileReader(csvFile));
            if (hasHeader){
                br.readLine();
            }
            Earthquake eq;
            while ((line = br.readLine()) != null) {
                String[] results = line.split(cvsSplitBy);
                eq = new Earthquake(dt.parse(results[0]), Double.parseDouble(results[1]));
                eqList.add(eq);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return eqList;
    }
}
