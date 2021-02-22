/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pakleni
 */
public class DistanceCalculator {
    static String KEY = "XWeOJGKcfZsTInP2rH9AWAPPd2wDNfrz";
    
    public static JSONObject getPath(String from, String to) {
        URL url;
        try {
            String link = "http://www.mapquestapi.com/directions/v2/route"
                    + "?key=" + KEY
                    + "&from=" + URLEncoder.encode(from, "UTF-8")
                    + "&to=" + URLEncoder.encode(to, "UTF-8")
                    + "&unit=" + "k";
           
            url = new URL(link);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            
            Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(in);
            
            JSONObject route = (JSONObject)obj.get("route");
            
            return route;

        } catch (MalformedURLException ex) {
            Logger.getLogger(PlanerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlanerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(PlanerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;  
    }
    
    public static double getDistance(String from, String to) {
        JSONObject ret = getPath(from, to);
        
        double distance = (double)ret.get("distance");
        return distance;
    }
    
    public static long getTime(String from, String to) {
        JSONObject ret = getPath(from, to);
        
        long time = (long)ret.get("time");
        return time;
    }
    
    public static void main(String[] args) {
        System.out.println(getTime("Dalmatinska 84, Beograd", "Patrijarha Dimitrija 151, Beograd"));
    }
}
