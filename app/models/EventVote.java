package models;

import java.util.Date;
import java.util.List;

import play.mvc.Router;

import siena.Column;
import siena.Id;
import siena.Model;
import siena.Query;

public class EventVote extends Model {

	@Id
    public Long id;
    public Boolean isPositive;
    
    //Relazioni
    @Column("user")
    public User user;
    @Column("event")
    public Event event;
    
	/**
	 * @param user
	 * @param event
	 * @param isPositive è un commento positivo?
	 * @throws Exception 
	 */
	public EventVote(User user, Event event, Boolean isPositive) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(event!=null)
			this.event = event;
		else 
			throw new Exception("Event can't be null");
		this.event = event;
		this.isPositive = isPositive;
	}
	
	static Query<EventVote> all() {
        return Model.all(EventVote.class);
    }
	public static EventVote findById(Long id) {
        return all().filter("id", id).get();
    }
	public static EventVote findByUserAndEvent(User user, Event event) {
		return all().filter("user", user).filter("event", event).get();
		
	}
	@Override
	public void insert() {
		super.insert();
		TipoAzione tp = TipoAzione.find("votato un evento");
		try {
			new Action(user, tp, new Date(), Router.getFullUrl("Events.show")+"?id="+this.event.id).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
