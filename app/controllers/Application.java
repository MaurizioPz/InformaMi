package controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import lib.CommonFunctions;
import models.Action;
import models.Event;
import models.EventComment;
import models.Medal;
import models.Photo;
import models.Place;
import models.PlaceComment;
import models.Presence;
import models.Tag;
import models.TipoAzione;
import models.TipoMedaglia;
import models.User;
import play.Logger;
import play.mvc.Controller;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
public class Application extends Controller {
	/**
	   * RestFB Graph API client.
	   */
	private static FacebookClient facebookClient=null;
	
	public static void index(String address) {
    	Events.index(address);
    }
    
    public static void search(String s){
    	User user=CommonFunctions.getLoggedUser(session);
    	List<Tag> tagcloud = Tag.getTagCloud();
    	render(user,s,tagcloud);
    }
    
    public static void DoLogin(String service){
    	com.restfb.types.User user = facebookClient.fetchObject("me", com.restfb.types.User.class);
    	User u = User.findByEmail(user.getEmail());
    	if(u==null){
    		try {
				u= new User(user.getEmail(), user.getName(), user.getId(), service, null);
				u.insert();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	session.put("userID", u.id);
    	index(null);
    }
    public static void doLogout(){
    	session.remove("userID");
    	index(null);
    }
    public static void fbauth(String code){
        if (code!=null) {
        	String authURL = lib.Facebook.getAuthURL(code);
            try {
            URL url = new URL(authURL);
                String result = readURL(url);
                String accessToken = null;
                Integer expires = null;
                String[] pairs = result.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length != 2) {
                        throw new RuntimeException("Unexpected auth response");
                    } else {
                        if (kv[0].equals("access_token")) {
                            accessToken = kv[1];
                        }
                        if (kv[0].equals("expires")) {
                            expires = Integer.valueOf(kv[1]);
                        }
                    }
                }
                if (accessToken != null && expires != null) {
                	facebookClient = new DefaultFacebookClient(accessToken);
                	session.put("accessToken", accessToken);
                	session.put("expires", expires+System.currentTimeMillis()/1000);
                	DoLogin("Facebook");
                	renderTemplate("Events/index.html");
                } else {
                	Logger.error("Access token and expires not found");
                	renderTemplate("Events/index.html");
                }
            } catch (IOException e) {
            	Logger.error(e.toString());
            	renderTemplate("Events/index.html");
            }
        }   	
    	
    }
    private static String readURL(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
    }
    
    public static void profile(Long id){
    	User user=CommonFunctions.getLoggedUser(session);
    	User userProfile = User.findById(id);
    	List<Event> events = userProfile.events.order("-id").limit(10).fetch();
    	List<Place> places = userProfile.places.order("-id").limit(10).fetch();
    	List<EventComment> eventComments = userProfile.eventComments.order("-data").limit(10).fetch();
    	for (EventComment eventComment : eventComments) {
			eventComment.event=Event.findById(eventComment.event.id);
		}
    	List<PlaceComment> placeComments= userProfile.placeComments.order("-data").limit(10).fetch();
    	for (PlaceComment placeComment : placeComments) {
    		placeComment.place=Place.findById(placeComment.place.id);
		}
    	List<Action> actions = userProfile.actions.fetch();
    	for (Action action : actions) {
			action.actionType = TipoAzione.findById(action.actionType.id);
			action.user = User.findById(action.user.id);
		}
    	List<Tag> tagcloud = Tag.getTagCloud();
    	Boolean isFriend = user.isFriend(userProfile);
    	List<Medal> medals = userProfile.medals.fetch();
    	for (Medal medal : medals) {
			medal.medalType = TipoMedaglia.findById(medal.medalType.id);
		}
    	List<Presence> presences = userProfile.presences.fetch();
    	for (Presence presence : presences) {
    		presence.photo = Photo.findById(presence.photo.id);
		}
    	render(user,userProfile,tagcloud,events,places,eventComments,placeComments,isFriend,actions,medals,presences);
    }
    public static void addFriend(Long id){
    	User user=CommonFunctions.getLoggedUser(session);
    	User userFriend = User.findById(id);
    	user.addFriend(userFriend);
    	if(userFriend!=null)
    		profile(userFriend.id);
    	else
    		profile(user.id);
    }
    public static void removeFriend(Long id){
    	User user=CommonFunctions.getLoggedUser(session);
    	User userFriend = User.findById(id);
    	user.removeFriend(userFriend);
    	if(userFriend!=null)
    		profile(userFriend.id);
    	else
    		profile(user.id);
    }
    public static void test(){
    	
    }
    
  }
