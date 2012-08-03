package x73.p20601.dim;

public class Attribute {
	
	private int id;
	private Object type;
	
	/*
	 *  we need an attribute ID because there are pairs of different attributes, 
	 *  which use the same class, but not the same ID from the nomenclature.
	 *  Without this id, there is no way for differencing them.
	 *  
	 *  i.e.: MDC_ATTR_SYS_TYPE(TYPE.class) and MDC_ATTR_ID_TYPE(TYPE.class)
	 *  @see utils.ASNUtils auxiliar class.
	 */
	
	public Attribute ( int id, Object type) throws Exception
	{
		if (id < 0 || id > 65536){
			throw new Exception("Attribute ID not valid (0-65536): "+id);
		}
		
		this.id = id;
		this.type = type;
	}
	
	
	
	public int getAttributeID (){
		return this.id;
	}
	

	public Object getAttributeType(){
		return this.type;
	}
	
}
