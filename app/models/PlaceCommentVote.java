package models;

import siena.Column;
import siena.Id;
import siena.Model;

public class PlaceCommentVote extends Model {
	@Id
    public Long id;
    public Boolean isPositive;
    
    //Relazioni
    @Column("user")
    public User user;
    @Column("placeComment")
    public PlaceComment placeComment;
	/**
	 * @param user utente che ha votato
	 * @param placeComment commentoLocale votato
	 * @param isPositive è un voto positivo?
	 * @throws Exception 
	 */
	public PlaceCommentVote(User user, PlaceComment placeComment, Boolean isPositive) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(placeComment!=null)
			this.placeComment = placeComment;
		else 
			throw new Exception("Place Comment can't be null");
		this.isPositive = isPositive;
	}



}
