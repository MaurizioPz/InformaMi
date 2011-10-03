package models;

import java.util.Date;

import play.mvc.Router;
import siena.Column;
import siena.Id;
import siena.Model;

public class Presence extends Model {
	@Id
    public Long id;
    public Integer PosizioneX;
    public Integer PosizioneY;
    
    //Relazioni
    @Column("user")
    public User user;
    @Column("photo")
    public Photo photo;
    
	/**
	 * @param posizioneX indica la posizione sull'asse delle x del utente da cui l'idUser
	 * @param posizioneY indica la posizione sull'asse delle y del utente da cui l'idUser
	 * @param user utente presente nella foto da cui photo
	 * @param photo foto al quale si riferisce questa presenza
	 */
	public Presence(User user, Photo photo,Integer posizioneX, Integer posizioneY) {
		PosizioneX = posizioneX;
		PosizioneY = posizioneY;
		this.photo=photo;
		this.user=user;

	}
	@Override
	public void insert() {
		super.insert();
		TipoAzione tp = TipoAzione.find("taggato se stesso in una foto");
		try {
			new Action(user, tp, new Date(), Router.getFullUrl("Photos.show")+"?id="+this.photo.id).insert();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}
