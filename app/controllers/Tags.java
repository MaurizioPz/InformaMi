package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lib.CommonFunctions;
import models.Event;
import models.Place;
import models.Tag;
import models.User;
import play.mvc.Controller;
import play.mvc.Router;
public class Tags extends Controller {
	 public static void save(String nome){
		 User user = CommonFunctions.getLoggedUser(session);
		 try {
			new Tag(user,nome).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash.error("Utente non trovato");
		}
		 Application.index(null);
	 }
	 

	 public static void getTags(String search){
		 if(search.lastIndexOf(',')>0)
			 search = search.substring(search.lastIndexOf(',')+1);
		 search = search.replaceAll(" ", "");
		 List<Tag> l = Tag.filterByName(search);
		 renderJSON(l);
	 }
	 
	 public static void getByTag(String tag,String address){
		 List<Event> events=null;
    	 Double lat=null,lng=null;
		 if(address!=null){
		    	try{
		    		Map<String, Double> location = CommonFunctions.geocode(address);
		    		lat=location.get("latitude");
		    		lng=location.get("longitude");
		    	}catch (Exception e) {
		    		e.printStackTrace();
		    	}
	    	}
		 try {
			Tag t = Tag.findByName(tag, null);
			events = t.getEvents();
			if((lat!=null)&&(lng!=null)){
				List<Event> tempList = new ArrayList<Event>();
				for (Event event : events) {
					event.place=Place.findById(event.place.id); //TODO Better performance
					if(event.place.isNear(lat,lng))
						tempList.add(event);
				}
				events=tempList;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	User user=CommonFunctions.getLoggedUser(session);
    	List<Tag> tagcloud = Tag.getTagCloud();
    	String actionVicino = "/tag/"+tag;
    	renderTemplate("Events/index.html", user,events,tagcloud,address,actionVicino);
	 }

}
