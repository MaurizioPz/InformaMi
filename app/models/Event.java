package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.mvc.Router;

import com.google.appengine.api.blobstore.BlobKey;


import siena.Column;
import siena.Filter;
import siena.Id;
import siena.Json;
import static siena.Json.*;
import siena.Model;
import siena.Query;
import siena.embed.Embedded;

public class Event extends Model{
		@Id
	    public Long id;
	    
	    public String nome;
	    public String nomePiccolo; //necessario per ricerca 
	    public String descrizione;
	    public Json imageLinks;
	    public Date dataInizio;
	    public Date dataFine;
	    public Boolean isRicorrente;
	    public Long ricorrenza;		//espresso in secondi
	    
	    
	    public Integer points;
	    
	    //Relazioni
	    @Column("user")
	    public User user;
	    @Column("place")
	    public Place place;

	    @Filter("event")
	    public Query<EventComment> comments;
	    @Filter("event")
	    public Query<EventVote> votes;
	    @Filter("event")
	    public Query<Photo> photos;
	    @Embedded
	    private List<Long> idTags= new ArrayList<Long>();
	 
	    
	    
	    /**
	     * @param nome
	     * @param descrizione
	     * @param string
	     * @param dataInizio
	     * @param durata
	     * @param isRicorrente
	     * @param ricorrenza
	     * @param tagList 
	     * @throws Exception 
	     */
	    public Event(User user, Place place,String nome, String descrizione, Json imageLinks2, Date dataInizio,Date dataFine, Boolean isRicorrente, Long ricorrenza, List<Tag> tagList) throws Exception {
			if(user!=null)
				this.user = user;
			else 
				throw new Exception("User can't be null");
			if(place!=null)
				this.place = place;
			else 
				throw new Exception("Place can't be null");

			this.nome = nome;
			nomePiccolo = nome.toLowerCase();
			this.descrizione = descrizione;
			if(imageLinks2!=null)
				this.imageLinks = imageLinks2;
			else{
				this.imageLinks=map().put("original", "http://placekitten.com/600/400")
									.put("small_square", "http://placekitten.com/64/64")
									.put("large_thumbnail", "http://placekitten.com/300/200")
									.put("delete_page", "");
			}
			this.dataInizio = dataInizio;
			this.dataFine = dataFine;
			this.isRicorrente = isRicorrente;
			this.ricorrenza = ricorrenza;
			for (Tag tag : tagList) {
				idTags.add(tag.id);
			}
		}
	    @Override
	    public void insert() {
	    	super.insert();
	    	Tag t;
	    	for (Long id : idTags) {
				t=Tag.findById(id);
				t.addEvent(this.id);
				t.update();
			}
	    	
    		TipoAzione tp = TipoAzione.find("creato un evento");
    		try {
    			new Action(user, tp, new Date(), Router.getFullUrl("Events.show")+"?id="+this.id).insert();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
	    	
	    }

		static Query<Event> all() {
	        return Model.all(Event.class);
	    }
		
		public static Event findById(Long id) {
	        return all().filter("id", id).get();
	    }
	        
	    public String toString() {
	        return nome;
	    }
	    public static List<Event> getAllByPoints(Double latitude, Double longitude){
	    	List<Event> l = getAll(true,latitude,longitude);
	    	Collections.sort(l, new Comparator<Event>() {@Override
	    	public int compare(Event e1, Event e2) {
	    		if(e1.points>e2.points)
	    			return -1;
	    		else
	    			return 1;
	    	}
			}); 
	    	return l;
	    }
	    
	    public static List<Event> getAll(boolean notExpireds,Double latitude, Double longitude) {
	    	List<Event> l;
			if(notExpireds)
				l= all().filter("dataFine>", new Date()).fetch();
			else
				l= all().fetch();
			if((latitude!=null)&&(longitude!=null)){
				List<Event> tempList = new ArrayList<Event>();
				for (Event event : l) {
					event.place=Place.findById(event.place.id); //TODO Better performance
					if(event.place.isNear(latitude,longitude))
						tempList.add(event);
				}
				l=tempList;
			}
			
			return l;
		}
	        
	    
		public int getPoints(){
			List<EventVote> votesP = votes.filter("isPositive", true).fetch();
			List<EventVote> votesN = votes.filter("isPositive", false).fetch();
			this.points= votesP.size()-votesN.size();
			return this.points;
		}
		public void removeTag(Long id) throws Exception{
			if(idTags.contains(id))
				idTags.remove(id);
			else 
				throw new Exception("Tag not found");
		}
		
		public List<Long> getTags(){
			return idTags;
		}
		
		@Override
		public void delete() {
			for (Long id : idTags) {
				try {
					Tag.findById(id).removeEvent(this.id);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.delete();
		}
		public void addVote(User user, Boolean isPositive) throws Exception {
			EventVote e=EventVote.findByUserAndEvent(user,this);
			if(e==null)
				new EventVote(user, this, isPositive).insert();
			else if(!e.isPositive.equals(isPositive))
				e.delete();
			
		}
		public static List<Event> search(){
			return all().search("ritorniamo", "descrizione").fetch();
			
		}
		
		
	}


