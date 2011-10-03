package models;

import java.util.Date;

import play.mvc.Router;
import siena.Column;
import siena.Id;
import siena.Model;

public class PlaceVote extends Model {

	@Id
    public Long id;
    public Boolean isPositive;
    
    //Relazioni
    @Column("user")
    public User user;
    @Column("place")
    public Place place;

	/**
	 * @param user
	 * @param place
	 * @param isPositive
	 * @throws Exception 
	 */
	public PlaceVote(User user, Place place, Boolean isPositive) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(place!=null)
			this.place = place;
		else 
			throw new Exception("Place can't be null");
		this.isPositive = isPositive;
	}
	@Override
	public void insert() {
		super.insert();
		TipoAzione tp = TipoAzione.find("votato un locale");
		try {
			new Action(user, tp, new Date(), Router.getFullUrl("Places.show")+"?id="+this.place.id).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
