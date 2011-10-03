package controllers;

import java.util.Date;
import java.util.List;

import lib.CommonFunctions;
import models.Event;
import models.Photo;
import models.Presence;
import models.User;
import play.data.Upload;
import play.mvc.Controller;
import siena.Json;

public class Photos extends Controller {
	 public static void save(Long eventId, Upload photo) throws Exception{
		 User user = CommonFunctions.getLoggedUser(session);
		 Event event = Event.findById(eventId);
		 Json json = CommonFunctions.uploadImage(photo);
		 try {
			new Photo(user,event,new Date(), json).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flash.error("Impossibile creare foto");
		}
		 Photos.showEvent(eventId);
	 }
	 
	 public static void showEvent(Long eventId) {
		 Event event = Event.findById(eventId);
		 User user = CommonFunctions.getLoggedUser(session);
		 List<Photo> photos = event.photos.fetch();
		 render(photos,user,event);
	 }
	 
	 public static void show(Long photoId){
		 User user = CommonFunctions.getLoggedUser(session);
		 Photo photo = Photo.findById(photoId);
		 photo.event=Event.findById(photo.event.id);
		 List<Presence> presences = photo.presences.fetch();
		 
		 for (Presence presence : presences) {
			 presence.user=User.findById(presence.user.id);
		 }
		 render(user,photo,presences);
	 }
	 public static void addPresence(Long photoId, Integer x, Integer y){
		 User user = CommonFunctions.getLoggedUser(session);
		 Photo photo = Photo.findById(photoId);
		 new Presence(user, photo, x, y).insert();
		 
	 }

}
