package models;

import java.util.Date;

import play.mvc.Router;

import siena.Column;
import siena.Filter;
import siena.Id;
import siena.Model;
import siena.Query;

public class PlaceComment extends Model {

	@Id
    public Long id;
    
    public String testo;
    public Date data;
    
    //Relazioni
    @Column("user")
    public User user;
    @Column("place")
    public Place place;
    
    @Filter("placeComment")
    public Query<PlaceCommentVote> votes;

	
    /**
     * 
     * @param user
     * @param place
     * @param testo
     * @param data
     * @throws Exception 
     */
	public PlaceComment(User user, Place place,String testo, Date data) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(place!=null)
			this.place = place;
		else 
			throw new Exception("Place can't be null");
		this.testo = testo;
		this.data = data;

	}
	@Override
	public void insert() {
		super.insert();
		TipoAzione tp = TipoAzione.find("commentato un locale");
		try {
			new Action(user, tp, new Date(), Router.getFullUrl("Places.show")+"?id="+this.place.id).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
