package models;

import siena.Filter;
import siena.Id;
import siena.Model;
import siena.Query;

public class TipoAzione extends Model{

	@Id
    public Long id;
    
    public String name;
    
	//Relazioni
    @Filter("actionType")
    public Query<Action> actions;
    
	/**
	 * @param name
	 */
	public TipoAzione(String name) {
		this.name = name;
	}

	public static TipoAzione find(String string) {
		TipoAzione tp = all().filter("name", string).get();
		if(tp==null){
			tp = new TipoAzione(string);
			tp.insert();
		}
		return tp;
	}
    private static Query<TipoAzione> all() {
        return Model.all(TipoAzione.class);
    }

	public static TipoAzione findById(Long id) {
        return all().filter("id", id).get();
    }


    

}
