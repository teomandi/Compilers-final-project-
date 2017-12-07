import java.util.*;

class variable {
	String type;
	String name;

	String temp_name;
	int order;
	
	
	variable(String t, String n, int i){
		this.type = t;
		this.name = n;
		this.temp_name = "TEMP "+i;
	}


	variable(String t, String n, int num, int i){
		this.type = t;
		this.name = n;
		this.order = num;
		this.temp_name = "TEMP "+i;
	}

	public boolean equal(variable x){
		if(this.type == x.type && this.name == x.name)
			return true;
		else
			return false;
	}

}

class Method {
	String name;
	String type;
	Set<variable> arguments = new HashSet<variable>();
	Set<variable> localvars = new HashSet<variable>();

	int order;

	Method(String T, String N, int num){
		this.type=T;
		this.name=N;

		this.order = num;
	}
	public void addarguments(variable var){
		arguments.add(var);
	}
	public void addlocalvars(variable var){
		localvars.add(var);
	}

	public boolean checkSameArgu(variable x){
		for (Iterator<variable> it = arguments.iterator(); it.hasNext(); ) {
        	variable v = it.next();
        	if(v.equal(x))
        		return true;
        }
        return false;
	}

	public variable arguExist(String id){			//if exists return the variable else null
		for (Iterator<variable> it = arguments.iterator(); it.hasNext(); ) {
        	variable v = it.next();
        	if(v.name == id )
        		return v;
        }
        return null;
	}

	public boolean typeExist(String tp){
		for (Iterator<variable> it = arguments.iterator(); it.hasNext(); ) {
        	variable vt = it.next();
        	if(vt.type == tp )
        		return true;
        }
        return false;
	}

	public variable lvExist(String id){			//if exists return the variable else null
		for (Iterator<variable> it = localvars.iterator(); it.hasNext(); ) {
        	variable v = it.next();
        	if(v.name == id )
        		return v;
        }
        return null;
	}	
	
}

class ClassMembers{
	String temp_add;

	String name;
	String extend;


	Set<variable> var = new HashSet<variable>();
	Map <String, Method> meth = new HashMap <String, Method>();

	ClassMembers(String n, String ex){
		this.name=n;
		this.extend = ex;
		this.temp_add = null;
	}

	public void printvar(){
		for (Iterator<variable> it = var.iterator(); it.hasNext(); ) {
        	variable v = it.next();
        	System.out.print(" " + v.type+"-"+v.name+" ");
        }
	}

	public boolean checkSameVar(variable x){
		for (Iterator<variable> it = var.iterator(); it.hasNext(); ) {
        	variable v = it.next();
        	if(v.equal(x))
        		return true;
        }
        return false;
	}

	public variable varExist(String id){		
		for (Iterator<variable> it = var.iterator(); it.hasNext(); ) {
        	variable v = it.next();
        	if(v.name == id )
        		return v;
        }
        return null;
	}

	public String methOrder(int i){
		for (String key: meth.keySet()) {
      		Method m = meth.get(key);
      		if(m.order == i)
      			return m.name;
      	}
      	return null; 
	}

	public String varOrder(int i){
		for (Iterator<variable> it = var.iterator(); it.hasNext(); ) {
        	variable v = it.next();
        	if(v.order == i )
        		return v.name;
        }
        return null;
	}

}

public class ClassInfo{

	String name; 
	String extend;
	Set <String> objects = new HashSet<String>();

	ClassInfo(String N, String E){
		this.name = N;
		this.extend = E;
	}
	
};

