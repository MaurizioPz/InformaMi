package models;

import java.util.ArrayList;
import java.util.List;

import siena.Column;
import siena.Filter;
import siena.Id;
import siena.Max;
import siena.Model;
import siena.NotNull;
import siena.Query;
import siena.embed.Embedded;


public class User extends Model {

    @Id
    public Long id;
    
    public String nome;
    public String email;
    public String webId;	//ID of the user in the provider website
    public String service;	//Provider website
    public String passwordHash;
    
    //Relazioni
    @Filter("user")
    public Query<Event> events;
    @Filter("user")
    public Query<Place> places;
    @Filter("user")
    public Query<EventVote> eventVotes;
    @Filter("user")
    public Query<EventComment> eventComments;
    @Filter("user")
    public Query<EventCommentVote> eventCommentVotes;
    @Filter("user")
    public Query<PlaceVote> placeVotes;
    @Filter("user")
    public Query<PlaceComment> placeComments;
    @Filter("user")
    public Query<PlaceCommentVote> placeCommentVotes;
    @Filter("user")
    public Query<Action> actions;
    @Filter("user")
    public Query<Medal> medals;
    @Filter("user")
    public Query<Tag> tags;
    @Filter("user")
    public Query<Presence> presences;
    @Filter("user")
    public Query<Photo> photos;
       
    @Embedded
    private List<Long> idFriends= new ArrayList<Long>();
 
    
    /**
     * 
     * @param email
     * @param name
     * @param webId id of this user in the provider website
     * @param service provider website where the user is signed in (like facebook)
     * @param passwordHash hash of the password
     * @throws Exception
     */
    public User(String email, String name,String webId, String service,String passwordHash) throws Exception {
        if (email!=null)
        	this.email=email;
        else
        	throw new Exception("An email is required");
        if (name!=null)
        	this.nome=name;
        else
        	throw new Exception("A name is required");
        
        this.webId=webId;
        this.passwordHash = passwordHash;
        this.service = service;
    }

	public void setEmail(String email) throws Exception{
    	if (email!=null)
        	this.email=email;
        else
        	throw new Exception("An email is needed");
    }
    
    static Query<User> all() {
        return Model.all(User.class);
    }
    
    public static User findById(Long id) {
        return all().filter("id", id).get();
    }

    public static User findByEmail(String email){
        return all().filter("email", email).get();
    }
        
    public String toString() {
        return nome;
    }

	public void addFriend(User userFriend){
    	if(userFriend!=null){
    		if(!idFriends.contains(userFriend.id)){
    			idFriends.add(userFriend.id);
    			this.update();
    		}
    	}
	}

	public void removeFriend(User userFriend){
		if(userFriend!=null){
    		if(idFriends.contains(userFriend.id)){
    			idFriends.remove(userFriend.id);
    			this.update();
    		}
    	}
	}
	public boolean isFriend(User userFriend){
		if(userFriend!=null){
			if(idFriends.contains(userFriend.id))
				return true;
		}
			return false;
	}
	public List<Long> getFriends(){
		return idFriends;
	}
	public List<Action> getFriendsActions(){
		List<Action> actions = new ArrayList<Action>();
		for (Long id : idFriends) {
			actions.add(User.findById(id).actions.order("-data").get());
		}
		return actions;
	}
	
}

