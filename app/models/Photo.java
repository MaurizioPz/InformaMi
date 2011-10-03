package models;

import java.util.Date;

import play.mvc.Router;

import siena.Column;
import siena.Filter;
import siena.Id;
import siena.Json;
import siena.Model;
import siena.Query;

public class Photo extends Model {


	
	@Id
    public Long id;
    public Date data;
    public Json imageLinks;
   
    //Relazioni
    @Column("user")
    public User user;
    @Column("event")
    public Event event;
    
    @Filter("photo")
    public Query<Presence> presences;

    
    /**
	 * @param data Quanto è stata caricata la foto
	 * @param uRL URL dove si trova la foto
	 * @param idUser id dell'utente che ha caricato la foto
	 * @param idEvent id dell'evento al quale appartiene la foto
     * @throws Exception 
	 */
	public Photo(User user, Event event, Date data, Json imageLinks) throws Exception {
		this.data = data;
		this.imageLinks = imageLinks;
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(event!=null)
			this.event = event;
		else 
			throw new Exception("Event can't be null");
	}
	
    private static Query<Photo> allModel() {
        return Model.all(Photo.class);
    }

	public static Photo findById(Long id) {
        return allModel().filter("id", id).get();
    }
	@Override
	public void insert() {
		super.insert();
		TipoAzione tp = TipoAzione.find("caricato una foto");
		try {
			new Action(user, tp, new Date(), Router.getFullUrl("Photos.show")+"?id="+this.id).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
