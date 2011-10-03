package models;

import java.util.Date;

import play.mvc.Router;

import siena.Column;
import siena.Filter;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.embed.Embedded;

public class EventComment extends Model {

	

	@Id
    public Long id;
    
    public String testo;
    public Date data;
    
    //Relazioni
    @Column("user")
    public User user;
    @Column("event")
    public Event event;
    
    @Filter("eventComment")
    public Query<EventCommentVote> votes;
    	
    
    /**
	 * @param user
	 * @param event
	 * @param testo
	 * @param data
     * @throws Exception 
	 */
	public EventComment(User user, Event event, String testo, Date data) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(event!=null)
			this.event = event;
		else
			throw new Exception("Event can't be null");
		this.testo = testo;
		this.data = data;
	}

	public String toString() {
        return testo;
    }
    
	static Query<EventComment> all() {
        return Model.all(EventComment.class);
    }

	public static EventComment findById(Long id) {
        return all().filter("id", id).get();
    }
	public String getUserName(){
		if(user==null)
			return "NULL";
		else
			return user.nome;
	}
	@Override
	public void insert() {
		super.insert();
		TipoAzione tp = TipoAzione.find("commentato un evento");
		try {
			new Action(user, tp, new Date(), Router.getFullUrl("Events.show")+"?id="+this.event.id).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
