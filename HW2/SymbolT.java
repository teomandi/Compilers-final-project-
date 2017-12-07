import java.util.* ;

public class SymbolT{

  public Map <String, ClassInfo> clmap = new HashMap<String, ClassInfo>();
  public Map <String, ClassMembers> cmmap = new HashMap<String, ClassMembers>();

  public void clputST(String name, ClassInfo cl){
  	clmap.put(name, cl);
  }
  public void cmputST(String name, ClassMembers cm){
  	cmmap.put(name, cm);
  }

  public ClassInfo clgetST(String name){
  	return clmap.get(name);
  }
  public ClassMembers cmgetST(String name){
  	return cmmap.get(name);
  }

  public boolean clcontainsKeyST(String name){
  	if( clmap.containsKey(name))
  		return true;
  	else
  		return false;
  }
  public boolean cmcontainsKeyST(String name){
  	if( cmmap.containsKey(name))
  		return true;
  	else
  		return false;
  }

  public int clsizeST(){
  	return clmap.size();
  }
  public int cmsizeST(){
  	return cmmap.size();
  }


}