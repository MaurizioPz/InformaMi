package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lib.CommonFunctions;
import models.Event;
import models.EventComment;
import models.Place;
import models.Tag;
import models.User;
import play.data.Upload;
import play.mvc.Before;
import play.mvc.Controller;
import siena.Json;
public class Events extends Controller {


	@Before
	    static void checkAuthenticated() {
	        String expires = session.get("expires");
	        System.currentTimeMillis(); 
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
    	List<Event> events= Event.getAllByPoints(lat,lng);
    	for (Event event : events) {
			event.user = User.findById(event.user.id);
		}
    	List<Tag> tagcloud = Tag.getTagCloud();
    	render(user,events,tagcloud,address);
    }
	
	 public static void create(Long id){
		 
		 User user = CommonFunctions.getLoggedUser(session);
		 List<Tag> tagcloud = Tag.getTagCloud();
		 render(id,user,tagcloud);
	 }
	 
	 public static void save(String placeDesc, String descrizione, String dataInizio,String dataFine, String nome, Boolean isRicorrente, Upload immagine, Long ricorrenza, String tags) throws FileNotFoundException, IOException {
		User user=CommonFunctions.getLoggedUser(session);
		Date dataInizi;
		Date dataFin;
	    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	    tags = tags.replaceAll(" ", "");
	    String[] tagArray = tags.split(",");
	    List<Tag> tagList= new ArrayList<Tag>();
	    for (String string : tagArray) {
			try {
				tagList.add(Tag.findByName(string,session));
			} catch (Exception e) {}
		}
	    Json imageLinks=null;
	    try {
	    	imageLinks=CommonFunctions.uploadImage(immagine);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			dataInizi = formatter.parse(dataInizio);
			dataFin =formatter.parse(dataFine);
			
			Place place = Place.findByString(placeDesc);
			new Event(user,place,nome,descrizione,imageLinks,dataInizi,dataFin,isRicorrente,ricorrenza,tagList).insert();
		} catch (ParseException e) {
			e.printStackTrace();
			flash.error("Errore nel creare evento");
		}
		catch (Exception e) {
				e.printStackTrace();
				flash.error("Errore nel creare evento");
		}
		 
		 index(null);
	 }
	 public static void vote(Long id, Boolean isPositive,Boolean redirectToEvent){
		 Event event = Event.findById(id);
		 User user = CommonFunctions.getLoggedUser(session);
		 try {
			 event.addVote(user,isPositive);
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash.error("Errore nel creare voto: " + e.getMessage());
		}
		 if(redirectToEvent)
			 show(id);
		 else
			 index(null);
	 }
	 
	 public static void show(Long id){
		 Event event = Event.findById(id);
		 int votes=event.points;
		 event.user=User.findById(event.user.id);
		 event.place=Place.findById(event.place.id);
		 List<EventComment> comments = event.comments.fetch();
		 for (EventComment comment : comments) {
			comment.user=User.findById(comment.user.id);
		 }
		 List<Tag> tags = new ArrayList<Tag>();
		 Tag temp=null;
		 for (Long idTag : event.getTags()) {
			 temp=Tag.findById(idTag);
			 if(temp!=null)
				 tags.add(temp);
		 }
		 User user = CommonFunctions.getLoggedUser(session);
		 List<Tag> tagcloud = Tag.getTagCloud();
		 render(event,votes,comments,user,tags,tagcloud);
	 }
	 
	 public static void addComment(Long Id, String comment){
		 Event event = Event.findById(Id);
		 User user = CommonFunctions.getLoggedUser(session);
		 try {
			new EventComment(user, event, comment, new Date()).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash.error("Errore nel creare commento");
		}
		 show(Id);
	 }
  }
