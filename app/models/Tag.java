package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import play.mvc.Router;
import play.mvc.Scope.Session;

import com.sun.jndi.url.ldaps.ldapsURLContextFactory;

import siena.Column;
import siena.Filter;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.embed.Embedded;
import siena.remote.Common;

import lib.*;

public class Tag extends Model implements Comparable<Tag> {
	
	@Id
    public Long id;
    public String nome;
    
    //Relazioni
    @Column("user")
    public User user;
    @Embedded
    private List<Long> idEvents= new ArrayList<Long>();
 
    
	/**
	 * @param nome indica il nome del Tag
	 * @param user indica l'utente che ha creato questo tag
	 * @throws Exception 
	 */
	public Tag(User user,String nome) throws Exception {
		this.nome = nome;
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
	}
	
	public static Query<Tag> all() {
        return Model.all(Tag.class);
    }
	
	public static Tag findById(Long id) {
        return all().filter("id", id).get();
    }

	public static List<Tag> filterByName(String search) {
		return all().search(search+"*", "nome").fetch();
	}
	public static Tag findByName(String search, Session session) throws Exception {
		Tag t = all().search(search.replace(" ", "-"), "nome").get();
		
		if(t==null){
			if(session!=null){
				t=new Tag(CommonFunctions.getLoggedUser(session),search.replace(" ", "-"));
				t.insert();
			}
		}
		return t;
	}
	
	public void removeEvent(Long id) throws Exception {
		if(idEvents.contains(id))
			idEvents.remove(id);
		else
			throw new Exception("Event not found");
	}
	public void addEvent(Long id) {
		if(!idEvents.contains(id))
			idEvents.add(id);
		
	}
	
	@Override
	public void delete() {
		for (Long id : idEvents) {
			try {
				Event.findById(id).removeTag(this.id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.delete();
	}

	public List<Event> getEvents() {
		List<Event> events= new ArrayList<Event>();
		Event tmp=null;
		for (Long idEvent : idEvents) {
			tmp = Event.findById(idEvent);
			if(tmp!=null)
				events.add(tmp);
		}
		//if(events.size()==0)
			//events=null;
		return events;
	}
	
	public static List<Tag> getTagCloud(){
		List<Tag> l = all().fetch();
		Collections.sort(l);
		return l;
	}

	@Override
	public int compareTo(Tag o) {
		if(this.idEvents.size()<o.idEvents.size())
			return 1;
		else if(this.idEvents.size()==o.idEvents.size())
			return 0;
		else
			return -1;
	}
	@Override
	public void insert() {
		super.insert();
		TipoAzione tp = TipoAzione.find("creato un etichetta");
		try {
			new Action(user, tp, new Date(), "#").insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
