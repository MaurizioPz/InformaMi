package lib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import models.User;
import play.data.Upload;
import play.mvc.Scope.Session;
import siena.Json;
import static siena.Json.*;
import siena.sdb.ws.Base64;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

public class CommonFunctions {
	public static User getLoggedUser(Session session){
		Long userID=null;
    	if (session.get("userID")!=null)
    		userID=Long.parseLong(session.get("userID"));
    	User user=null;
    	if(userID!=null){
    		user = User.findById(userID);
		}
		return user;
	}
	
	private static final String imgurKey="081f3bb95260fbe572c2354724af2bac";
	
	public static Json uploadImage(Upload immagine) throws Exception {
	    
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(immagine.asBytes());
		
		String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encodeBytes(baos.toByteArray()), "UTF-8");
		data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(imgurKey, "UTF-8");

		URL url = new URL("http://api.imgur.com/2/upload.json");
		
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
		
		BufferedReader br =new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		String a="";
        while ((inputLine = br.readLine()) != null) 
            a=a+inputLine;
        br.close();
        
        Json json=null;
		try {
			JSONObject j = new JSONObject(a);
			j = j.getJSONObject("upload").getJSONObject("links");
			json = map().put("original", j.getString("original"))
							.put("small_square", j.getString("small_square"))
							.put("large_thumbnail", j.getString("large_thumbnail"))
							.put("delete_page", j.getString("delete_page"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return json;
	}
	
	public static Map<String,Double> geocode(String address) throws IOException, JSONException{
		URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address,"UTF-8")+"&sensor=false");

		URLConnection conn = url.openConnection();
	
		BufferedReader br =new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String inputLine;
		String a="";
        while ((inputLine = br.readLine()) != null) 
            a=a+inputLine;
        br.close();
		JSONObject j =new JSONObject(a);
		Map<String,Double> map = new HashMap<String, Double>();
		
		j=j.getJSONArray("results").getJSONObject(0);
		j=j.getJSONObject("geometry");
		j=j.getJSONObject("location");
		map.put("latitude", j.getDouble("lat"));
		map.put("longitude", j.getDouble("lng"));

		return map;
	}
	

}
