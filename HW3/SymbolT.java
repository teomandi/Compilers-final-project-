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

  public void printST(){
    for (String key: cmmap.keySet()) {
      ClassMembers cm = cmmap.get(key);
      System.out.println("****CLASS: " + cm.name + " Ex: " + cm.extend);
      System.out.println("vars: " + cm.var.size());
      for (Iterator<variable> it3 = cm.var.iterator(); it3.hasNext(); ) {
        variable v = it3.next();
        System.out.println("~" + v.name + " ORDER: " + v.order );
      }
      System.out.println("meths: " + cm.meth.size());
      for (String key2: cm.meth.keySet()) {
        Method m = cm.meth.get(key2);
        System.out.println("~" + m.name + " ORDER: " + m.order );
      }
    }
  }

}