package models;

import java.util.Date;

import siena.Column;
import siena.Id;
import siena.Model;

public class Action extends Model {

	@Id
	public Long id;
	public Date data;
	public String URL;
	
	//Relazioni
    @Column("user")
    public User user;
    @Column("actionType")
    public TipoAzione actionType;

	/**
	 * @param data data in cui è stata compiuta l'azione
	 * @param uRL URL che si riferisce all'azione
	 * @param idUser utente che ha compiuto l'azione
	 * @param idActionType Azione compiuta
	 * @throws Exception 
	 */
	public Action(User user, TipoAzione tpAzione, Date data, String uRL) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(user!=null)
			this.actionType = tpAzione;
		else 
			throw new Exception("TipoAzione can't be null");
		this.data = data;
		URL = uRL;
	}
	@Override
	public String toString() {
		return user.nome + " ha " + actionType.name;
	}

	@Override
	public void insert() {
		super.insert();
		try {
			if(user.actions.count()>10){
				if(user.medals.filter("medalType", TipoMedaglia.find("Primino")).count()==0)
					new Medal(TipoMedaglia.find("Primino"),user,new Date()).insert();
			}
			if(user.actions.count()>100){
				if(user.medals.filter("medalType", TipoMedaglia.find("Novizio")).count()==0)
					new Medal(TipoMedaglia.find("Novizio"),user,new Date()).insert();
			}
			if(user.actions.count()>1000){
				if(user.medals.filter("medalType", TipoMedaglia.find("Intermedio")).count()==0)
					new Medal(TipoMedaglia.find("Intermedio"),user,new Date()).insert();
			}
			if(user.actions.count()>10000){
				if(user.medals.filter("medalType", TipoMedaglia.find("Esperto")).count()==0)
					new Medal(TipoMedaglia.find("Esperto"),user,new Date()).insert();
			}
			if(user.events.count()>10){
				if(user.medals.filter("medalType", TipoMedaglia.find("Promotore")).count()==0)
					new Medal(TipoMedaglia.find("Promotore"),user,new Date()).insert();
			}
			if(user.events.count()>100){
				if(user.medals.filter("medalType", TipoMedaglia.find("Organizzatore")).count()==0)
					new Medal(TipoMedaglia.find("Organizzatore"),user,new Date()).insert();
			}
			if(user.events.count()>1000){
				if(user.medals.filter("medalType", TipoMedaglia.find("PR Ufficiale")).count()==0)
					new Medal(TipoMedaglia.find("PR Ufficiale"),user,new Date()).insert();
			}
			if(user.eventComments.count()+user.placeComments.count()>10){
				if(user.medals.filter("medalType", TipoMedaglia.find("Commentatore")).count()==0)
					new Medal(TipoMedaglia.find("Commentatore"),user,new Date()).insert();
			}
			if(user.eventComments.count()+user.placeComments.count()>100){
				if(user.medals.filter("medalType", TipoMedaglia.find("Opinionista")).count()==0)
					new Medal(TipoMedaglia.find("Commentatore"),user,new Date()).insert();
			}
			if(user.eventComments.count()+user.placeComments.count()>1000){
				if(user.medals.filter("medalType", TipoMedaglia.find("Politico")).count()==0)
					new Medal(TipoMedaglia.find("Commentatore"),user,new Date()).insert();
			}
			if(user.eventVotes.count()+user.placeVotes.count()>10){
				if(user.medals.filter("medalType", TipoMedaglia.find("Giudice")).count()==0)
					new Medal(TipoMedaglia.find("Commentatore"),user,new Date()).insert();
			}
			if(user.eventComments.count()+user.placeCommentVotes.count()>10){
				if(user.medals.filter("medalType", TipoMedaglia.find("Giudicatore")).count()==0)
					new Medal(TipoMedaglia.find("Commentatore"),user,new Date()).insert();
			}
			if(user.eventComments.count()+user.placeCommentVotes.count()>10){
				if(user.medals.filter("medalType", TipoMedaglia.find("Votatore estremo")).count()==0)
					new Medal(TipoMedaglia.find("Commentatore"),user,new Date()).insert();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
