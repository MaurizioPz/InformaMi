package models;

import java.util.Date;

import siena.Column;
import siena.Id;
import siena.Model;

public class Medal extends Model {

	@Id
	public Long id;
	public Date data;
	
    //Relazioni
    @Column("user")
    public User user;
    @Column("medalType")
    public TipoMedaglia medalType;

	/**
	 * @param data
	 * @param medalType 
	 * @param user 
	 * @throws Exception 
	 */
	public Medal(TipoMedaglia medalType, User user,Date data) throws Exception {
		if(user!=null)
			this.user = user;
		else 
			throw new Exception("User can't be null");
		if(medalType!=null)
			this.medalType = medalType;
		else 
			throw new Exception("Tipo Medaglia can't be null");
		this.data = data;
	}
	@Override
	public String toString() {
		return medalType.name;
	}

}
