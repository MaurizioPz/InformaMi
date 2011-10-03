import static siena.Json.map;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.CommonFunctions;
import models.Event;
import models.EventComment;
import models.Place;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.modules.siena.SienaFixtures;
import play.test.UnitTest;
import siena.Json;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

public class BasicTest extends UnitTest {
	Map<String, Double> loc= new HashMap<String, Double>();

	@Before
    public void setup() {
		try {
			SienaFixtures.deleteAllModels();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	@Test
    public void createUser() {
        try {
			new User("mauriziopz@gmail.com",
					"Maurizio Pozzobon","1","facebook",null).insert();
		} catch (Exception e) {}
		User user = User.findByEmail("mauriziopz@gmail.com");
		user = User.findById(user.id);
		assertNotNull(user);
		assertEquals("mauriziopz@gmail.com", user.email);
		assertEquals("Maurizio Pozzobon", user.nome);
		assertEquals("1", user.webId);
		assertEquals(null, user.passwordHash);
    }

	@Test
    public void testGeocode() {
        try {
			loc = CommonFunctions.geocode("Rivignano, Italia");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(loc.get("latitude"));
		assertNotNull(loc.get("longitude"));
		assertEquals((Double)13.0425,loc.get("longitude"));
		assertEquals((Double)45.8798518,loc.get("latitude"));
    }
	    
    @Test
    public void createPlace() {
    	createUser();
    	testGeocode();
    	Json json = null;
		json = map().put("original", "http://original")
				.put("small_square", "http://small")
				.put("large_thumbnail", "http://large")
				.put("delete_page", "http://delete");
		
    	User user = User.findByEmail("mauriziopz@gmail.com");
    	assertNotNull(user);
    	Place place=null;
    	
		try {
			place = new Place(user,"Titolo locale",
					"descrizione del locale creato tramite test",json,
					"Rivignano, Italia",loc.get("latitude"),
					loc.get("longitude"));
		} catch (Exception e) {
			assertEquals(0,1);
		}
    	place.insert();
    	user = User.findByEmail("mauriziopz@gmail.com");
    	List<Place> p = user.places.fetch();
    	assertNotNull(p);
    	assertFalse(p.isEmpty());
    	for (Place pl : p) {
			assertEquals("Titolo locale",pl.nome);
			assertEquals("descrizione del locale creato tramite test",
					pl.descrizione);
			assertNotNull(pl.imageLinks);
			
			assertEquals("http://original", 
					pl.imageLinks.get("original").asString());
			assertEquals("http://small", 
					pl.imageLinks.get("small_square").asString());
			assertEquals("http://large", 
					pl.imageLinks.get("large_thumbnail").asString());
			assertEquals("http://delete", 
					pl.imageLinks.get("delete_page").asString());
				
			assertEquals("Rivignano, Italia",pl.indirizzo);
			assertNotNull(pl.latitude);
			assertNotNull(pl.longitude);
		}
    }
    
    @Test
    public void createEvent() {
    	createUser();
    	User user = User.findByEmail("mauriziopz@gmail.com");
    	Place place=null;
		try {
			place = new Place(user,"posto","bel posto",null,null,null,null);
			place.insert();
			assertNotNull(user);
			Event e=null;
			
			e = new Event(user,place, "Festa","Questa è una gran bella festa",null,new Date(),new Date(),false,null,null);
			e.insert();
	    	List<Event> events =user.events.fetch();
	    	assertNotNull(events);
	    	assertFalse(events.isEmpty());
	    	for (Event event : events) {
	    		assertEquals("Festa",event.nome);
	    		assertEquals("Questa è una gran bella festa",event.descrizione);
	    		
			}
			
			
		} catch (Exception e1) {
			assertEquals(1, 0);
		}
    	
		
    }
    @Test
    public void commentTest(){
    	try {
			new User("maur@mail.com","Maurizio","01","facebook","hash").insert();
		} catch (Exception e) {}
    	User user = User.findByEmail("maur@mail.com");
    	Place place;
		try {
			place = new Place(user,"posto","bel posto",null,null,null,null);
			place.insert();
			assertNotNull(user);
			Event e =new Event(user,place, "Festa","Questa è una gran bella festa",null,new Date(),new Date(),false,null,null);
			e.insert();
			assertNotNull(user.nome);
	    	EventComment ec = new EventComment(user, e, "TestComment", new Date());
	    	ec.insert();
	    	List<EventComment> ecs = e.comments.fetch();
	    	for (EventComment comment : ecs) {
	    		assertNotNull(comment.user.id);
	    		User us= User.findById(comment.user.id);
	    		comment.user=us;
	    		assertNotNull(comment.user.nome);
			}
		} catch (Exception e1) {
			assertEquals(1, 0);
		}
    	
    }

}
