package models;

import siena.Column;
import siena.Id;
import siena.Model;

public class EventCommentVote extends Model {
	@Id
    public Long id;
    public Boolean isPositive;
    
    //Relazioni
    @Column("user")
    public User user;
    @Column("eventComment")
    public EventComment eventComment;
	/**
	 * @param user
	 * @param eventComment
	 * @param isPositive è un commento positivo?
	 * @throws Exception 
	 */
	public EventCommentVote(User user, EventComment eventComment, Boolean isPositive) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(eventComment!=null)
			this.eventComment = eventComment;
		else 
			throw new Exception("Event Comment can't be null");
		this.isPositive = isPositive;
	}

}
