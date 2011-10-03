package models;

import static siena.Json.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import play.mvc.Router;

import com.google.appengine.repackaged.org.json.JSONException;

import lib.CommonFunctions;

import siena.Column;
import siena.Filter;
import siena.Id;
import siena.Json;
import siena.Model;
import siena.Query;

public class Place extends Model {
	
	

	@Id
    public Long id;
    
    public String nome;
    public String nomePiccolo;
    public String descrizione;
    public Json imageLinks;
    public String indirizzo;
    public Integer points;
    
    public Double latitude;
    public Double longitude;
    //Relazioni
    @Column("user")
    public User user;
    @Filter("place")
    public Query<Event> events;
    @Filter("place")
    public Query<PlaceVote> votes;
    @Filter("place")
    public Query<PlaceComment> comments;

	
    
    /**
	 * @param nome indica il nome del locale
	 * @param descrizione
	 * @param imageLinks
	 * @param indirizzo
     * @throws Exception 
	 */
	public Place(User user, String nome, String descrizione, Json imageLinks,String indirizzo,Double latitude, Double longitude) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		this.nome = nome;
		nomePiccolo=nome.toLowerCase();
		this.descrizione = descrizione;
		if((imageLinks!=null)&&(!imageLinks.equals("")))
			this.imageLinks = imageLinks;
		else{
			this.imageLinks=map().put("original", "http://placekitten.com/600/400")
			.put("small_square", "http://placekitten.com/64/64")
			.put("large_thumbnail", "http://placekitten.com/300/200")
			.put("delete_page", "");
		}
		
		this.indirizzo = indirizzo;
		this.latitude=latitude;
		this.longitude=longitude;
	}
	

	static Query<Place> all() {
        return Model.all(Place.class);
    }
	public static List<Place> getAll(Double latitude, Double longitude) {
		List<Place> l = all().fetch();
		if(latitude!=null){
			List<Place> tempList= new ArrayList<Place>();
			for (Place place : l) {
				if(place.isNear(latitude, longitude))
					tempList.add(place);
			}
			l=tempList;
		}
		return l;
			
    }

	public static List<Place> filterByName(String name) {
		
        return all().search(name+"*","nomePiccolo").fetch();
    }
    
	public static Place findById(Long id) {
        return all().filter("id", id).get();
        
    }
	public static Place findByString(String s) {
		String[] split = s.split(":");
        return all().filter("nome", split[0]).filter("indirizzo", split[1]).get();
        
    }
	
    public String toString() {
        return nome;
    }  
	public int getPoints(){
		int votesP = votes.filter("isPositive", true).count();
		int votesN = votes.filter("isPositive", false).count();
		this.points= votesP-votesN;
		return this.points;
	}


	public boolean isNear(Double latitude2, Double longitude2) {
		Double diff = 0.2;
		try{
		if((latitude<latitude2+diff)&&(latitude>latitude2-diff)&&(longitude<longitude2+diff)&&(longitude>longitude2-diff))
			return true;
		}catch (Exception e) {
			if(latitude==null){
				try {
					Map<String, Double> map = CommonFunctions.geocode(indirizzo);
					latitude=map.get("latitude");
					longitude=map.get("longitude");
					this.update();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return false;
	}
	@Override
	public void insert() {
		super.insert();
		TipoAzione tp = TipoAzione.find("creato un locale");
		try {
			new Action(user, tp, new Date(), Router.getFullUrl("Places.show")+"?id="+this.id).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
