/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speaker;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pakleni
 */
public class Player {
    private boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean openWebpage(String urlString){
        try {
            URL url = new URL (urlString);
            openWebpage(url);
            return true;
        } catch (MalformedURLException ex) {
            Logger.getLogger(SpeakerController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
            
    private String search(String q) {
        URL url;
        try {
            String link = "http://api.deezer.com/search?q=" + URLEncoder.encode(q, "UTF-8");
        	
            url = new URL(link);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            
            Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(in);
            JSONArray data = (JSONArray)obj.get("data");
            JSONObject firstResult = (JSONObject) data.get(0);
            
            String res = (String)firstResult.get("link") + "?autoplay=true";
            
            return res;

        } catch (MalformedURLException ex) {
            Logger.getLogger(SpeakerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(SpeakerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            return "NO";
        } catch (ParseException ex) {
            Logger.getLogger(SpeakerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "NO";
    }
    
    void searchAndPlay(String query) {
        String res = search(query);
        
        if (res.equals("NO")) {
            System.out.println("We no find dat");
            return;
        }
        
        
        openWebpage(res);
    }

    private static Player singleton = null;
    
    public static Player getInstance() {
        if (singleton == null){
            singleton = new Player();
        }
        
        return singleton;
    }
}
