package models;

import java.util.HashSet;
import java.util.Set;

import siena.Filter;
import siena.Id;
import siena.Model;
import siena.Query;

public class TipoMedaglia extends Model{

	@Id
    public Long id;
    
    public String name;
    public Integer value;
    
	//Relazioni
    @Filter("medalType")
    public Query<Medal> medals;
    
    
    /**
	 * @param name
	 * @param value
	 */
	public TipoMedaglia(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public static TipoMedaglia find(String string) {
		TipoMedaglia tp = all().filter("name", string).get();
		if(tp==null){
			tp = new TipoMedaglia(string,1);
			tp.insert();
		}
		return tp;
	}
    private static Query<TipoMedaglia> all() {
        return Model.all(TipoMedaglia.class);
    }

	public static TipoMedaglia findById(Long id) {
        return all().filter("id", id).get();
    }
	
}
