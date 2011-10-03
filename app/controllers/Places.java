package controllers;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.repackaged.org.json.JSONException;

import lib.CommonFunctions;
import models.Place;
import models.PlaceComment;
import models.PlaceVote;
import models.Tag;
import models.User;
import play.data.Upload;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Router;
import siena.Json;

public class Places extends Controller {
	 @Before
	    static void checkAuthenticated() {
	        String expires = session.get("expires");
	        System.out.println(System.currentTimeMillis()/1000); 
	        System.out.println(expires);
	    }
	 public static void index(String address) {
	    	
	    	User user=CommonFunctions.getLoggedUser(session);
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
	    	List<Place> places = Place.getAll(lat,lng);
	    	for (Place place : places) {
				place.user=User.findById(place.user.id);
			}
	    	List<Tag> tagcloud = Tag.getTagCloud();
	    	String actionVicino = Router.getFullUrl("Places.index");
	    	render(user,places,tagcloud,address,actionVicino);
	    }
	 
	 public static void create(){
		 User user = CommonFunctions.getLoggedUser(session);
		 List<Tag> tagcloud = Tag.getTagCloud();
		 render(user,tagcloud);
	 }
	 
	 public static void show(Long id){
		 Place place = Place.findById(id);
		 place.user=User.findById(place.user.id);
		 int votes=place.points;
		 List<PlaceComment> comments = place.comments.fetch();
		 for (PlaceComment comment : comments) {
			comment.user=User.findById(comment.user.id);
		 }
		 User user = CommonFunctions.getLoggedUser(session);
		 List<Tag> tagcloud = Tag.getTagCloud();
		 render(place,votes,comments,user,tagcloud);
	 }
	 
	 public static void save(String nome, String descrizione, String indirizzo, Upload immagine){
		 User user = CommonFunctions.getLoggedUser(session);
		 Json imageLinks=null;
		try {
			imageLinks = CommonFunctions.uploadImage(immagine);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, Double> location;
		try {
			location = CommonFunctions.geocode(indirizzo);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			location = new HashMap<String, Double>();
			e1.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			location = new HashMap<String, Double>();
			e1.printStackTrace();
		}
		 try {
			new Place(user,nome,descrizione,imageLinks,indirizzo,location.get("latitude"),location.get("longitude")).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash.error("Utente non trovato");
		}
		 Application.index(null);
	 }
	 
	 public static void addComment(Long Id, String comment){
		 Place place= Place.findById(Id);
		 User user = CommonFunctions.getLoggedUser(session);
		 
		try {
			new PlaceComment(user, place, comment, new Date()).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash.error("Utente o luogo non trovato");
		}
		 
		 show(Id);
	 }
	 
	 public static void vote(Long id, Boolean isPositive,Boolean redirectToPlace){
		 Place place = Place.findById(id);
		 User user = CommonFunctions.getLoggedUser(session);
		 try {
			new PlaceVote(user, place, isPositive).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash.error("Utente o luogo non trovato");
		}
		 place.update();
		 if(redirectToPlace)
			 show(id);
		 else
			 index(null);
	 }
	 public static void getPlaces(String search){
		 List<Place> l = Place.filterByName(search);
		 renderJSON(l);
	 }
	 
  }
