
import java.util.*;

class variable {
	String type;
	String name;

	variable(String t, String n){
		this.type = t;
		this.name = n;
	}

	public boolean equal(variable x){
		if(this.type == x.type && this.name == x.name)
			return true;
		else
			return false;
	}

	//variable(String t)
}

class Method {
	String name;
	String type;
	Set<variable> arguments = new HashSet<variable>();
	Set<variable> localvars = new HashSet<variable>();

	Method(String T, String N){
		this.type=T;
		this.name=N;
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
	Set<variable> var = new HashSet<variable>();
	Map <String, Method> meth = new HashMap <String, Method>();

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

	public variable varExist(String id){			//if exists return the variable else null
		for (Iterator<variable> it = var.iterator(); it.hasNext(); ) {
        	variable v = it.next();
        	if(v.name == id )
        		return v;
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

