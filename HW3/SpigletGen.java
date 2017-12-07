import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.* ;


public class SpigletGen extends GJDepthFirst<String, String> {

  SymbolT st = new SymbolT() ;
  int tempInit;
  int outnum;
  Writeri w ;
  List<String> argu_list = new ArrayList<String>();
  

  SpigletGen(SymbolT s, int x, int z ){
    st =s;
    tempInit = x;
    outnum = z;
  }



/**
 * Grammar production:
 * f0 -> MainClass()
 * f1 -> ( TypeDeclaration() )*
 * f2 -> <EOF>
 */
 
  public String visit(Goal n, String ourclass){
    w = new Writeri(tempInit, outnum);
    w.createtxt();
	  n.f0.accept(this, null);
  	n.f1.accept(this, null);
    w.close();
	  return "DONE";
  }

/**
 * Grammar production:
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> "public"
 * f4 -> "static"
 * f5 -> "void"
 * f6 -> "main"
 * f7 -> "("
 * f8 -> "String"
 * f9 -> "["
 * f10 -> "]"
 * f11 -> Identifier()
 * f12 -> ")"
 * f13 -> "{"
 * f14 -> ( VarDeclaration() )*
 * f15 -> ( Statement() )*
 * f16 -> "}"
 * f17 -> "}"
 */

  public String visit(MainClass n, String ourclass){ 
    w.generateLAB("MAIN");
    String classname, mname;
	  classname = n.f1.accept(this, null);
    mname ="main";
    ourclass = classname + " " + mname;
	  n.f11.accept(this, null);
    n.f14.accept(this, ourclass);
    n.f15.accept(this, ourclass);
    w.generateLAB("END");
  	return null;
	}

 
 
 /**
 * f0 -> ClassDeclaration()
 *       | ClassExtendsDeclaration()
 */
 
  public String visit(TypeDeclaration n, String ourclass){
	  n.f0.accept(this, null);
	  return null;
  }
 
 /**
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "{"
 * f3 -> ( VarDeclaration() )*
 * f4 -> ( MethodDeclaration() )*
 * f5 -> "}"
 */

  public String visit(ClassDeclaration n, String ourclass){
    ourclass = n.f1.accept(this, null);
		n.f3.accept(this, null);
	  n.f4.accept(this, ourclass);
	  return null;
  }
 
 /**
 * f0 -> "class"
 * f1 -> Identifier()
 * f2 -> "extends"
 * f3 -> Identifier()
 * f4 -> "{"
 * f5 -> ( VarDeclaration() )*
 * f6 -> ( MethodDeclaration() )*
 * f7 -> "}"
 */

  public String visit(ClassExtendsDeclaration n, String ourclass){
	  ourclass = n.f1.accept(this, null);
	  n.f3.accept(this, null);
  	n.f5.accept(this, null);
	  n.f6.accept(this, ourclass);
	  return null;
  }
 
 /**
 * f0 -> Type()
 * f1 -> Identifier()
 * f2 -> ";"
 */

  public String visit(VarDeclaration n, String ourclass){				
	  n.f0.accept(this, null);
	  n.f1.accept(this, null);
	  return null;
  }
 
 /**
 * f0 -> "public"
 * f1 -> Type()
 * f2 -> Identifier()
 * f3 -> "("
 * f4 -> ( FormalParameterList() )?
 * f5 -> ")"
 * f6 -> "{"
 * f7 -> ( VarDeclaration() )*
 * f8 -> ( Statement() )*
 * f9 -> "return"
 * f10 -> Expression()
 * f11 -> ";"
 * f12 -> "}"
 */

  public String visit(MethodDeclaration n, String ourclass){
    String exp, temp, classname, methodname;
    boolean foundclassvar=false;
	  n.f1.accept(this, null); 
  	String name = n.f2.accept(this, null);
    ClassMembers cm = st.cmgetST(ourclass);
    Method m = cm.meth.get(name);
    int x = m.arguments.size()+1;
    variable v=null;
    w.generateLAB(ourclass + "_" + name + " [ " + x +" ]");
    ourclass = ourclass + " " + name;
    w.generateLAB(" BEGIN");
	  n.f4.accept(this, ourclass);
  	n.f7.accept(this, ourclass);
	  n.f8.accept(this, ourclass);
    exp = n.f10.accept(this, ourclass);
    temp = w.gettemp();
    if(isInteger(exp)){
        w.generate("MOVE " + temp + " " + exp);
      }
    else if(exp.contains("TEMP")){
      w.generate("MOVE " + temp + " " + exp);
    }
    else{       //variable
        v=null;
        cm = null;
        m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        //WTFF?
            v = m.lvExist(exp);
            if(v== null)
              v=m.arguExist(exp);
            if(v==null){
              v=cm.varExist(exp);
              if(v!=null) foundclassvar=true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp);
         if(v!=null) foundclassvar=true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp);
            if(v!=null) {foundclassvar=true;break;}
          }
        }
        String temp1;
        if(foundclassvar)
          temp1 = getfromVtable(ourclass, exp);
        else
          temp1=v.temp_name;
        w.generate("MOVE " + temp + " " + temp1);
    }
    w.generateLAB(" RETURN ");
    w.generate(temp);
    w.generateLAB("END");
	  return null;
  }
	
	
 /**
 * f0 -> FormalParameter()
 * f1 -> FormalParameterTail()
 */

  public String visit(FormalParameterList n, String ourclass){
	  n.f0.accept(this, null);
  	n.f1.accept(this, null);
  	return null;
	}

 /**
 * f0 -> Type()
 * f1 -> Identifier()
 */

  public String visit(FormalParameter n, String ourclass){
	  n.f0.accept(this, null);
	  n.f1.accept(this, null);
    return null;
  }

/**
* f0 -> ( FormalParameterTerm() )*
*/

 public String visit(FormalParameterTail n, String ourclass){
	n.f0.accept(this, null);
	  return null;
	}

/**
* f0 -> ","
* f1 -> FormalParameter()
*/	

 public String visit(FormalParameterTerm n, String ourclass){
	n.f1.accept(this, null);
	  return null;
	}

 /**
 * Grammar production:
 * f0 -> ArrayType()
 *       | BooleanType()
 *       | IntegerType()
 *       | Identifier()
 */
 
  public String visit(Type n, String ourclass){
	  return (n.f0.accept(this, null));
	}
	
 /**
 * Grammar production:
 * f0 -> "int"
 * f1 -> "["
 * f2 -> "]"
 */
 
  public String visit(ArrayType n, String ourclass){
	  return n.f0.toString();
	}
 
 /**
 * Grammar production:
 * f0 -> "boolean"
 */
 
  public String visit(BooleanType n, String ourclass){
	  return n.f0.toString();
	}
 
 /**
 * Grammar production:
 * f0 -> "int"
 */
 
  public String visit(IntegerType n, String ourclass){
	  return n.f0.toString();
	}

   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
  public String visit(Statement n, String ourclass) {
      n.f0.accept(this, ourclass);
      return null;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
  public String visit(Block n, String ourclass) {
      n.f1.accept(this, ourclass);
      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
  public String visit(AssignmentStatement n, String ourclass) {
      String id, exp2, tempname, t, classname, methodname;
      int x1;
      boolean foundclassvar = false, foundclassvar1 = false;
      id = n.f0.accept(this, ourclass);
      variable v=null;
      ClassMembers cm = null;
      Method m =null;
      if(ourclass.contains(" ")){ 
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        if(methodname.equals("main")){
          cm = st.cmgetST(classname);
          m = cm.meth.get("main");
          v = m.lvExist(id);
        }
        else{
          cm = st.cmgetST(classname);
          m = cm.meth.get(methodname);       
          v = m.lvExist(id);
          if(v== null)
            v=m.arguExist(id);
          if(v==null){
            v=cm.varExist(id);
            if(v!=null)
              foundclassvar = true;
          }
        }
      }
      else{
        cm = st.cmgetST(ourclass);
        v=cm.varExist(id);
        if(v!=null) foundclassvar = true;
      }
      if(v==null){//ext
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0];
        }
        else
          classname = ourclass;
        cm = st.cmgetST(classname);
        while(cm.extend != null ){
          cm = st.cmgetST(cm.extend);
          v = cm.varExist(id);
          if(v!=null){foundclassvar = true; break;}
        }
      } 
      if(foundclassvar)
        tempname = getfromVtable(ourclass,id);
      else 
        tempname = v.temp_name;
      exp2 = n.f2.accept(this, ourclass);
      t =w.gettemp();
      if(isInteger(exp2)){
        w.generate("MOVE " + t + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + t + " " + exp2);
      }
      else{       //variable
        v=null;
        cm = null;
        m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);       
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar1=true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null) foundclassvar1=true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null) {foundclassvar1=true;break;}
          }
        }
        if(foundclassvar1)
          t=getfromVtable(ourclass, exp2);
        else
          t=v.temp_name;
    }
    if(!foundclassvar)
      w.generate("MOVE " + tempname + " " + t);
    else{
      x1 = getnumVtable(ourclass,id);
      w.generate("HSTORE " + tempname + " " + x1*4 + t);
    }
    return tempname;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
  public String visit(ArrayAssignmentStatement n, String ourclass) {  //++store
      String id, exp, exp2, arrayloc, exp3 , tmp3, classname, methodname, temp, tempname;
      int x, x1;
      boolean foundclassvar = false, foundclassvar1 = false;
      id = n.f0.accept(this, ourclass);
      variable v=null;
      ClassMembers cm = null;
      Method m =null;
      if(ourclass.contains(" ")){ 
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        if(methodname.equals("main")){
          cm = st.cmgetST(classname);
          m = cm.meth.get("main");
          v = m.lvExist(id);
        }
        else{
          cm = st.cmgetST(classname);
          m = cm.meth.get(methodname);        
          v = m.lvExist(id);
          if(v== null)
            v=m.arguExist(id);
          if(v==null){
            v=cm.varExist(id);
            if(v!=null)
              foundclassvar1=true;
          }
        }
      }
      else{
       cm = st.cmgetST(ourclass);
       v=cm.varExist(id);
       if(v!=null)  foundclassvar1=true;
      }
      if(v==null){//ext
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0];
        }
        else
          classname = ourclass;
        cm = st.cmgetST(classname);
        while(cm.extend != null ){
          cm = st.cmgetST(cm.extend);
          v = cm.varExist(id);
          if(v!=null) {foundclassvar1 = true; break;}
        }
      }
      if(foundclassvar1)
        tempname=getfromVtable(ourclass, id);
      else
        tempname = v.temp_name;
      arrayloc = w.gettemp();
      temp = w.gettemp();
      w.generate("MOVE " + arrayloc + " " + tempname);
      exp2 = n.f2.accept(this, ourclass);
      if(isInteger(exp2)){
        w.generate("MOVE " + temp + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + temp + " " + exp2);
      }
      else{       //variable
        v=null;
        cm = null;
        m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);      
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar=true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null) foundclassvar=true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null) {foundclassvar=true;break;}
          }
        } 
        if(foundclassvar)
          temp=getfromVtable(ourclass, exp2);
          foundclassvar=false;
      }
      foundclassvar=false;
      //aray lock + temp exei to exp2
      String t1, t2, t3, t4, t5, t6, t7, t8, t9, l1;
      t1 = w.gettemp();
      l1= w.getlabel();
      w.generate("MOVE " + t1 + " LT " + temp + " 0");
      w.generate("CJUMP " + t1 + " " + l1);
      w.generate("ERROR");
      w.generateLAB(l1+"\tNOOP");
      t2 = w.gettemp();
      w.generate("HLOAD " + t2 + " " + arrayloc + " 0");
      l1 = w.getlabel();
      t3 = w.gettemp();
      w.generate("MOVE " + t3 + " 1");
      t4 = w.gettemp();
      w.generate("MOVE " + t4 + " LT " + temp + " " + t2 );
      t5=w.gettemp();
      w.generate("MOVE " + t5 + " MINYS " + t3 + " " + t4);
      w.generate("CJUMP " + t5 + " " + l1);
      w.generate("ERROR");
      w.generateLAB(l1+"\tNOOP");
      t6=w.gettemp();
      w.generate("MOVE " +t6 + " TIMES " + temp + " 4" );
      t7 = w.gettemp();
      w.generate("MOVE " + t7 + " PLUS " + t6 + " 4");
      t8 = w.gettemp();
      t9 = w.gettemp();
      w.generate("MOVE " + t8 + " PLUS " + arrayloc+ " " + t7);
      exp3 = n.f5.accept(this, ourclass);
      tmp3 = w.gettemp();
      if(isInteger(exp3)){
        w.generate("MOVE " + tmp3 + " " + exp2);
      }
      else if(exp3.contains("TEMP")){
        w.generate("MOVE " + tmp3 + " " + exp3);
      }
      else{       //variable
        v=null;
        cm = null;
        m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp3);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);       
            v = m.lvExist(exp3);
            if(v== null)
              v=m.arguExist(exp3);
            if(v==null){
              v=cm.varExist(exp3);
              if(v!=null)
                foundclassvar=true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp3);
         if(v!=null)
          foundclassvar=true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp3);
            if(v!=null) {foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          tmp3 = getfromVtable(ourclass, exp3);       /////////////////////////ama 8elei k store sto vtable
        else
          tmp3 = v.temp_name;
      }
      w.generate("HSTORE " + t8 + " 0 " + tmp3);
      return null;
   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
  public String visit(IfStatement n, String ourclass) {
      boolean foundclassvar = false;
      String exp2, ELSE, END,t, classname, methodname, vtemp, tempname, findc=null;
      exp2 = n.f2.accept(this, ourclass);
      ELSE = w.getlabel();
      END = w.getlabel();
      t=w.gettemp();
      ClassMembers cm;
      variable v;
      Method m;
      if(isInteger(exp2)){
        w.generate("MOVE " + t + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + t + " " + exp2);
      }
      else{       //variable
        v=null;
        cm = null;
        m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar=true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null) foundclassvar=true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null) {foundclassvar=true;break;}
          }
        }
        if(foundclassvar)
          t=getfromVtable(ourclass, exp2);
        else
          t=v.temp_name;
      }
      w.generate("CJUMP " + t + " " + ELSE);
      n.f4.accept(this, ourclass);
      w.generate("JUMP " + END);
      w.generateLAB(ELSE + "\tNOOP");
      n.f6.accept(this, ourclass);
      w.generate("JUMP " + END);
      w.generateLAB(END + "\tNOOP");
      return null;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
  public String visit(WhileStatement n, String ourclass) {
      boolean foundclassvar = false;
      String exp2, END, WHILE,t, classname, methodname, vtemp, tempname, findc=null;
      END = w.getlabel();
      WHILE = w.getlabel();
      exp2 = n.f2.accept(this, ourclass);
      t=w.gettemp();
      ClassMembers cm;
      variable v;
      Method m;
      if(isInteger(exp2)){
        w.generate("MOVE " + t + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + t + " " + exp2);
      }
      else{       //variable
        v=null;
        cm = null;
        m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar=true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null) foundclassvar=true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null) {foundclassvar=true;break;}
          }
        }
        if(foundclassvar)
          t= getfromVtable(ourclass,exp2);
        else
          t = v.temp_name;
      }

      w.generateLAB(WHILE + "\tNOOP");
      w.generate("CJUMP " + t + " " + END);
      n.f4.accept(this, ourclass);
      w.generate("JUMP " + WHILE);
      w.generateLAB(END + "\tNOOP");
      return null;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
  public String visit(PrintStatement n, String ourclass) {
      String forp, exp1, classname, methodname,tempname;
      boolean foundclassvar = false;
      forp = w.gettemp();
      exp1 = n.f2.accept(this, ourclass);
      if(isInteger(exp1)){
        w.generate("MOVE " + forp + " " + exp1);
      }
      else if(exp1.contains("TEMP")){
        w.generate("MOVE " + forp + " " + exp1);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp1);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp1);
            if(v== null)
              v=m.arguExist(exp1);
            if(v==null){
              v=cm.varExist(exp1);
              if(v!=null) foundclassvar=true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp1);
         if(v!=null) foundclassvar=true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp1);
            if(v!=null){foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          tempname = getfromVtable(ourclass,exp1);
        else
          tempname =v.temp_name;
        w.generate("MOVE " + forp + " " + tempname);
      }
      w.generate("PRINT " + forp);
      return null;
   }

   /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | Clause()
    */
  public String visit(Expression n, String ourclass) {
        return n.f0.accept(this, ourclass);
    }

   /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
  public String visit(AndExpression n, String ourclass) {
      String cl1,cl2,t1,t2,temp,end1,end2,classname, methodname;
      boolean foundclassvar= false;
      temp=w.gettemp();
      t1=w.gettemp();
      t2=w.gettemp();
      end1=w.getlabel();
      end2=w.getlabel();
      cl1=n.f0.accept(this, ourclass);
      if(isInteger(cl1)){
        w.generate("MOVE " + t1 + " " + cl1);
      }
      else if(cl1.contains("TEMP")){
        w.generate("MOVE " + t1 + " " + cl1);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(cl1);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(cl1);
            if(v== null)
              v=m.arguExist(cl1);
            if(v==null){
              v=cm.varExist(cl1);
              if(v!=null) foundclassvar =true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(cl1);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(cl1);
            if(v!=null){foundclassvar = true; break;}
          }
        }
        if(foundclassvar)
          t1=getfromVtable(ourclass, cl1);
        else
          t1=v.temp_name;
      }
      foundclassvar = false;
      cl2=n.f2.accept(this, ourclass);
      if(isInteger(cl2)){
        w.generate("MOVE " + t2 + " " + cl2);
      }
      else if(cl2.contains("TEMP")){
        w.generate("MOVE " + t2 + " " + cl2);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(cl2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(cl2);
            if(v== null)
              v=m.arguExist(cl2);
            if(v==null){
              v=cm.varExist(cl2);
              if(v!=null) foundclassvar =true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(cl2);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(cl2);
            if(v!=null){foundclassvar = true; break;}
          }
        }
        if(foundclassvar)
          t2=getfromVtable(ourclass, cl2);
        else
          t2=v.temp_name;
      }
      w.generate("CJUMP " + t1 + " " + end1);
      w.generate("CJUMP " + t2 + " " + end1);
      w.generate("MOVE " + temp + " 1");
      w.generate("JUMP " + end2);
      w.generateLAB(end1 + "\tNOOP");
      w.generate("MOVE " + temp + " 0");
      w.generate("JUMP " + end2);
      w.generateLAB(end2 + "\tNOOP");
      return temp;   
    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
  public String visit(CompareExpression n, String ourclass) {
      String exp1, exp2, result, t1, t2, classname, methodname;
      boolean foundclassvar = false;
      t1 = w.gettemp();
      t2 = w.gettemp();
      exp1=n.f0.accept(this, ourclass);
      if(isInteger(exp1)){
        w.generate("MOVE " + t1 + " " + exp1);
      }
      else if(exp1.contains("TEMP")){
        w.generate("MOVE " + t1 + " " + exp1);
      }
      else{       
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp1);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp1);
            if(v== null)
              v=m.arguExist(exp1);
            if(v==null){
              v=cm.varExist(exp1);
              if(v!=null) foundclassvar =true;
            } 
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp1);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp1);
            if(v!=null) {foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          t1=getfromVtable(ourclass, exp1);
        else
          t1=v.temp_name;

      }
      foundclassvar = false;
      exp2=n.f2.accept(this, ourclass);
      if(isInteger(exp2)){
        w.generate("MOVE " + t2 + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + t2 + " " + exp2);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null)  foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null){foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          t2=getfromVtable(ourclass, exp2);
        else
          t2=v.temp_name;
      }
      result=w.gettemp();
      w.generate("MOVE " + result + " LT " + t1 + " " + t2);
      return result;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
  public String visit(PlusExpression n, String ourclass) {
      String exp1, exp2, t1, t2, result, classname, methodname;;
      boolean foundclassvar = false;
      t1 = w.gettemp();
      t2 = w.gettemp();
      exp1=n.f0.accept(this, ourclass);
      if(isInteger(exp1)){
        w.generate("MOVE " + t1 + " " + exp1);
      }
      else if(exp1.contains("TEMP")){
        w.generate("MOVE " + t1 + " " + exp1);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp1);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp1);
            if(v== null)
              v=m.arguExist(exp1);
            if(v==null){
              v=cm.varExist(exp1);
              if(v!=null) foundclassvar =true;
            } 
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp1);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp1);
            if(v!=null) {foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          t1=getfromVtable(ourclass, exp1);
        else
          t1=v.temp_name;
      }
      foundclassvar = false;
      exp2=n.f2.accept(this, ourclass);
      if(isInteger(exp2)){
        w.generate("MOVE " + t2 + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + t2 + " " + exp2);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);       
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null)  foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null){foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          t2=getfromVtable(ourclass, exp2);
        else
          t2=v.temp_name;
      }
      result = w.gettemp();
      w.generate("MOVE " + result + " PLUS " + t1 + " " + t2 );
      return result;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
  public String visit(MinusExpression n, String ourclass) {
      String exp1, exp2, t1, t2, result, classname, methodname;;
      boolean foundclassvar = false;
      t1 = w.gettemp();
      t2 = w.gettemp();
      exp1=n.f0.accept(this, ourclass);
      if(isInteger(exp1)){
        w.generate("MOVE " + t1 + " " + exp1);
      }
      else if(exp1.contains("TEMP")){
        w.generate("MOVE " + t1 + " " + exp1);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp1);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp1);
            if(v== null)
              v=m.arguExist(exp1);
            if(v==null){
              v=cm.varExist(exp1);
              if(v!=null) foundclassvar =true;
            } 
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp1);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp1);
            if(v!=null) {foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          t1=getfromVtable(ourclass, exp1);
        else
          t1=v.temp_name;
      }
      foundclassvar = false;
      exp2=n.f2.accept(this, ourclass);
      if(isInteger(exp2)){
        w.generate("MOVE " + t2 + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + t2 + " " + exp2);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null)  foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null){foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          t2=getfromVtable(ourclass, exp2);
        else
          t2=v.temp_name;
      }
      result = w.gettemp();
      w.generate("MOVE " + result + " MINUS " + t1 + " " + t2 );
      return result;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
  public String visit(TimesExpression n, String ourclass) {
      String exp1, exp2, t1, t2, result, classname, methodname;
      boolean foundclassvar = false;
      t1 = w.gettemp();
      t2 = w.gettemp();
      exp1=n.f0.accept(this, ourclass);
      if(isInteger(exp1)){
        w.generate("MOVE " + t1 + " " + exp1);
      }
      else if(exp1.contains("TEMP")){
        w.generate("MOVE " + t1 + " " + exp1);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp1);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);       
            v = m.lvExist(exp1);
            if(v== null)
              v=m.arguExist(exp1);
            if(v==null){
              v=cm.varExist(exp1);
              if(v!=null) foundclassvar =true;
            } 
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp1);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp1);
            if(v!=null) {foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          t1=getfromVtable(ourclass, exp1);
        else
          t1=v.temp_name;

      }
      foundclassvar = false;
      exp2=n.f2.accept(this, ourclass);
      if(isInteger(exp2)){
        w.generate("MOVE " + t2 + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + t2 + " " + exp2);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null)  foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null){foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          t2=getfromVtable(ourclass, exp2);
        else
          t2=v.temp_name;
      }
      result = w.gettemp();
      w.generate("MOVE " + result + " TIMES " + t1 + " " + t2 );
      return result;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
  public String visit(ArrayLookup n, String ourclass) {
      String temp, temp1 ,temp2, exp, exp2,classname, methodname;
      boolean foundclassvar = false;
      exp = n.f0.accept(this, ourclass);
      temp = w.gettemp();
      temp1 = w.gettemp();
      if(exp.contains("TEMP")){
        w.generate("MOVE " + temp + " " + exp);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp);
            if(v== null)
              v=m.arguExist(exp);
            if(v==null){
              v=cm.varExist(exp);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp);
            if(v!=null){foundclassvar = true; break;}
          }
        }
        if(foundclassvar)
          temp=getfromVtable(ourclass, exp);
        else
          temp=v.temp_name;
      }
      foundclassvar = false;
      exp2 = n.f2.accept(this, ourclass);
      if(isInteger(exp2)){
        w.generate("MOVE " + temp1 + " " + exp2);
      }
      else if(exp2.contains("TEMP")){
        w.generate("MOVE " + temp1 + " " + exp2);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp2);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp2);
            if(v== null)
              v=m.arguExist(exp2);
            if(v==null){
              v=cm.varExist(exp2);
              if(v!=null) foundclassvar = true;
            };
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp2);
         if(v!=null) foundclassvar = true;;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp2);
            if(v!=null){foundclassvar = true; break;}
          }
        }
        if(foundclassvar)
          temp1=getfromVtable(ourclass, exp2);
        else
          temp1=v.temp_name;
      }
      String l1, t, t1, t2, t3, one;
      temp2 = w.gettemp();
      l1 = w.getlabel();
      w.generate("MOVE " + temp2 + " LT " + temp1 + " 0");
      w.generate("CJUMP " + temp2 + " " + l1 );
      w.generate("ERROR");
      w.generateLAB(l1+"\tNOOP");
      t1 = w.gettemp();
      t2 = w.gettemp();
      t3 = w.gettemp();
      w.generate("HLOAD " + t1 + " " + temp + " 0 " );   
      one = w.gettemp();
      w.generate("MOVE " + one + " 1");
      w.generate("MOVE " + t2 + " LT " + temp1 + " " + t1);
      w.generate("MOVE " + t3 + " MINUS " + one + " " + t2);
      l1 = w.getlabel();
      w.generate("CJUMP " + t3 + " " + l1 );
      w.generate("ERROR");
      w.generateLAB(l1+"\tNOOP");
      t = w.gettemp();
      t2 = w.gettemp();
      t3 = w.gettemp();
      w.generate("MOVE " + t + " TIMES " + temp1 + " 4");
      w.generate("MOVE " + t2 + " PLUS " + t + " 4");
      w.generate("MOVE " + t3 + " PLUS " + temp + " " + t2);
      w.generate("HLOAD " + t1 + " " + t3 + " 0");
      return t1;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
  public String visit(ArrayLength n, String ourclass) {
      String exp, temp, result, classname, methodname;
      boolean foundclassvar = false;
      temp = w.gettemp();
      exp = n.f0.accept(this, ourclass);
      if(exp.contains("TEMP")){
        w.generate("MOVE " + temp + " " + exp);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(exp);
            if(v== null)
              v=m.arguExist(exp);
            if(v==null){
              v=cm.varExist(exp);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp);
            if(v!=null) { foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          temp=getfromVtable(ourclass, exp);
        else
          temp=v.temp_name;
      }
      result = w.gettemp();
      w.generate("HLOAD " + result + " " + temp + " 0");
      return result;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
  public String visit(MessageSend n, String ourclass) {
      String pexp, temp, temp2, funcname, methodname, classname, t, typ;
      boolean foundclassvar = false;
      int xx;
      variable v=null;
      ClassMembers cm = null;
      Method m =null;
      pexp = n.f0.accept(this, ourclass);
      temp = w.gettemp();
      temp2 = w.gettemp();
      if(pexp.contains("TEMP")){
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0];
        }
        else
          classname=ourclass;
        typ=classname;
        w.generate("MOVE " + temp + " " + pexp );
      }
      else if(st.cmcontainsKeyST(pexp)){
        typ=pexp;
        cm=st.cmgetST(pexp);
        w.generate("MOVE " + temp + " " +cm.temp_add);
      }
      else{       //variable
         v=null;
         cm = null;
         m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(pexp);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(pexp);
            if(v== null)
              v=m.arguExist(pexp);
            if(v==null){
              v=cm.varExist(pexp);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(pexp);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(pexp);
            if(v!=null) { foundclassvar=true; break;}
          }
        }
        typ=v.type;
        if(foundclassvar)
          temp=getfromVtable(ourclass, pexp);
        else
          temp=v.temp_name;
      }
      funcname = n.f2.accept(this, ourclass);
      xx=0;
      cm = st.cmgetST(typ);
      int sze,i;
      boolean itsfunc = false;
      Stack<String> stck = new Stack<String>();
      stck.push(cm.name);
      if(cm.meth.containsKey(funcname))
        itsfunc = true;
      while(cm.extend != null){
        cm = st.cmgetST(cm.extend);
        stck.push(cm.name);
      }
      while(!stck.empty()){
        String clss = stck.pop();
        cm = st.cmgetST(clss);
        sze = cm.meth.size();
        t = w.gettemp();
        for(i=1; i<=sze; i++){
          methodname = cm.methOrder(i);
          if(methodname == null) throw new Error("Error");
          if(methodname.equals(funcname)){
            xx = xx + i; 
            w.generate("HLOAD " + t + " " + temp + " 0");
            w.generate("HLOAD " + temp2 + " " + t + " " + 4*(xx-1));
            break;
          }
        }
        xx = xx + sze;
      }
      n.f4.accept(this, ourclass);
      String temp3;
      temp3 = w.gettemp();
      w.generateSL("MOVE " + temp3 + " CALL " + temp2 + " ( " +  temp + " ");
      for (Iterator<String> it = argu_list.iterator(); it.hasNext(); ) {    
        String a = it.next();
        w.generateSSL(a + " ");
      }
      argu_list.clear();
      w.generateNT(")");
      return temp3;
   }

   /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
  public String visit(ExpressionList n, String ourclass) {
      String s, temp, classname, methodname;
      boolean foundclassvar = false;
      s = n.f0.accept(this, ourclass);//to prwto orisma 
      temp = w.gettemp();
      if(isInteger(s)){
        w.generate("MOVE " + temp + " " + s);
      }
      else if(s.contains("TEMP")){
        w.generate("MOVE " + temp + " " + s);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(s);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(s);
            if(v== null)
              v=m.arguExist(s);
            if(v==null){
              v=cm.varExist(s);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(s);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(s);
            if(v!=null) { foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          temp=getfromVtable(ourclass, s);
        else
          temp=v.temp_name;
      }
      argu_list.add(temp);
      n.f1.accept(this, ourclass);
      return null;
   }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
  public String visit(ExpressionTail n, String ourclass) {
      String s;
      s=n.f0.accept(this, ourclass);
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
  public String visit(ExpressionTerm n, String ourclass) {
      String s, temp, classname, methodname;
      boolean foundclassvar = false;
      s=n.f1.accept(this, ourclass);
      temp = w.gettemp();
      if(isInteger(s)){
        w.generate("MOVE " + temp + " " + s);
      }
      else if(s.contains("TEMP")){
        w.generate("MOVE " + temp + " " + s);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(s);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);    
            v = m.lvExist(s);
            if(v== null)
              v=m.arguExist(s);
            if(v==null){
              v=cm.varExist(s);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(s);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(s);
            if(v!=null) { foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          temp=getfromVtable(ourclass, s);
        else
          temp=v.temp_name;
      }
      argu_list.add(temp);     
      return null;
   }

   /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
  public String visit(Clause n, String ourclass) {
      return n.f0.accept(this, ourclass);
   }

   /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
  public String visit(PrimaryExpression n, String ourclass) {
     return  n.f0.accept(this, ourclass);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
  public String visit(IntegerLiteral n, String ourclass) {
      return n.f0.toString();
   }

   /**
    * f0 -> "true"
    */
  public String visit(TrueLiteral n, String ourclass) {
      return "1";
   }

   /**
    * f0 -> "false"
    */
  public String visit(FalseLiteral n, String ourclass) {
      return "0";
   }

   /**
    * f0 -> <IDENTIFIER>
   */
  public String visit(Identifier n, String ourclass) {
        return n.f0.toString();
   }

   /**
    * f0 -> "this"
    */
  public String visit(ThisExpression n, String ourclass) {
      return "TEMP "+0;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
  public String visit(ArrayAllocationExpression n, String ourclass) {
      String  t, exp, temp, classname, methodname;
      boolean foundclassvar = false;
      temp = w.gettemp();
      exp = n.f3.accept(this, ourclass);
      if(isInteger(exp)){
        w.generate("MOVE " + temp + " " + exp);
      }
      else if(exp.contains("TEMP")){
        w.generate("MOVE " + temp + " " + exp);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(exp);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);       
            v = m.lvExist(exp);
            if(v== null)
              v=m.arguExist(exp);
            if(v==null){
              v=cm.varExist(exp);
              if(v!=null) foundclassvar=true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(exp);
         if(v!=null) foundclassvar=true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(exp);
            if(v!=null) {foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          temp=getfromVtable(ourclass, exp);
        else
          temp=v.temp_name;
      }
      String temp1, temp2, temp3, arrayloc, l1, l2, four, zero;
      temp1 = w.gettemp();
      l1 = w.getlabel();
      l2 = w.getlabel();
      w.generate("MOVE " + temp1 + " LT " + temp + " 0");
      w.generate("CJUMP " + temp1 + " " + l1);
      w.generate("ERROR");
      w.generateLAB(l1 + "\tNOOP");
      temp2 = w.gettemp();
      temp3 = w.gettemp();
      arrayloc = w.gettemp();
      four =w.gettemp();
      w.generate("MOVE " + temp2 + " PLUS " + temp + " 1");
      w.generate("MOVE " + temp3 + " TIMES " + temp2 + " 4");
      w.generate("MOVE " + arrayloc + " HALLOCATE " + temp3);
      w.generate("MOVE " + four + " 4");
      w.generateLAB(l2 +"\tNOOP");
      temp1 = w.gettemp();
      temp2 = w.gettemp();
      temp3 = w.gettemp();
      l1 = w.getlabel();
      w.generate("MOVE " + temp1 + " PLUS " + temp + " 1" );
      w.generate("MOVE " + temp2 + " TIMES " + temp1 + " 4");
      w.generate("MOVE " + temp3 + " LT " + four + " " + temp2 );
      w.generate("CJUMP " + temp3 + " " + l1);
      temp1 = w.gettemp();
      w.generate("MOVE " + temp1 + " PLUS " + arrayloc + " " + four);
      zero = w.gettemp();
      w.generate("MOVE " + zero + " 0");
      w.generate("HSTORE " + temp1 + " 0 " + zero);
      w.generate("MOVE " + four + " PLUS " + four + " 4");
      w.generate("JUMP " + l2);
      w.generateLAB(l1+"\tNOOP");
      w.generate("HSTORE " + arrayloc + " 0 " + temp);
      return arrayloc;
  }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
  public String visit(AllocationExpression n, String ourclass) {
      String classname, vtable1, table2, lab1, lab2, methodname, temp, temp2, temp3, temp4, zero; 
      classname = n.f1.accept(this, ourclass);
      vtable1 = w.gettemp();
      table2 = w.gettemp();//the one shoud be returned!!!
      ClassMembers cm = st.cmgetST(classname);
      int methsnum, varsnum, i;
      Stack<String> stck = new Stack<String>();
      methsnum = cm.meth.size();
      varsnum = cm.var.size();
      stck.push(classname);
      while(cm.extend != null){
        stck.push(cm.extend);
        cm = st.cmgetST(cm.extend);
        methsnum = methsnum + cm.meth.size();
        varsnum = varsnum + cm.var.size();
      }      
      w.generate("MOVE " + vtable1 + " HALLOCATE " + 4*methsnum );
      w.generate("MOVE " + table2 + " HALLOCATE " + (4*varsnum+4) );

      int j=0, sze;
      while(!stck.empty()){ 
        String clss = stck.pop();
        cm = st.cmgetST(clss);
        sze = cm.meth.size();
        for(i=1; i<=sze; i++){
          methodname = cm.methOrder(i);
          if(methodname == null) throw new Error("Error");
          temp = w.gettemp();
          w.generate("MOVE " + temp + " " + clss + "_" + methodname);
          w.generate("HSTORE " + vtable1 + " " + (i-1)*4 + " " + temp);
          j = j +sze;
        }
      }
      temp = w.gettemp();
      temp4 = w.gettemp();
      zero = w.gettemp();
      if(varsnum != 0 ){
        w.generate("MOVE " + temp + " 4");
        lab1 = w.getlabel();
        lab2 = w.getlabel();
        w.generateLAB(lab1 + "\tNOOP");       
        temp2 = w.gettemp();
        temp3 = w.gettemp();                    
        
        w.generate("MOVE " + temp2 + " " + (4*varsnum+4));
        w.generate("MOVE " + temp3 + " LT " + temp + " " + temp2 );
        w.generate("CJUMP " + temp3 + " " + lab2);
        w.generate("MOVE " + temp4 + " PLUS " + table2 + " " + temp);
        w.generate("MOVE " + zero + " 0");
        w.generate("MOVE " + temp + " PLUS " + temp + " 4");
        w.generate("JUMP " + lab1);
        w.generateLAB(lab2 + "\tNOOP");
        w.generate("HSTORE " + temp4 + " 0 " + zero);
      }
      w.generate("HSTORE " + table2 + " 0 " + vtable1);
      cm = null;
      cm = st.cmgetST(classname);
      cm.temp_add = table2;
      st.cmputST(classname, cm);
      return classname;
  }

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
  public String visit(NotExpression n, String ourclass) {
      String temp, cl, zero,end, classname, methodname;
      boolean foundclassvar = false;
      temp = w.gettemp();
      zero =w.getlabel();
      end =w.getlabel();
      cl = n.f1.accept(this, ourclass);
      if(isInteger(cl)){
        w.generate("MOVE " + temp + " " + cl);
      }
      else if(cl.contains("TEMP")){
        w.generate("MOVE " + temp + " " + cl);
      }
      else{       //variable
        variable v=null;
        ClassMembers cm = null;
        Method m =null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          if(methodname.equals("main")){
            cm = st.cmgetST(classname);
            m = cm.meth.get("main");
            v = m.lvExist(cl);
          }
          else{
            cm = st.cmgetST(classname);
            m = cm.meth.get(methodname);        
            v = m.lvExist(cl);
            if(v== null)
              v=m.arguExist(cl);
            if(v==null){
              v=cm.varExist(cl);
              if(v!=null) foundclassvar = true;
            }
          }
        }
        else{
         cm = st.cmgetST(ourclass);
         v=cm.varExist(cl);
         if(v!=null) foundclassvar = true;
        }
        if(v==null){//ext
          if(ourclass.contains(" ")){ 
            String[] parts = ourclass.split(" ");
            classname = parts[0];
          }
          else
            classname = ourclass;
          cm = st.cmgetST(classname);
          while(cm.extend != null ){
            cm = st.cmgetST(cm.extend);
            v = cm.varExist(cl);
            if(v!=null) { foundclassvar=true; break;}
          }
        }
        if(foundclassvar)
          temp=getfromVtable(ourclass, cl);
        else
          temp=v.temp_name;
      }
      w.generate("CJUMP " + temp + " " + zero);
      w.generate("MOVE " + temp + " 0");
      w.generate("JUMP " + end);
      w.generateLAB(zero + "\tNOOP");
      w.generate("MOVE " + temp + " 1");
      w.generateLAB(end + "\tNOOP");
      return temp;
  }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
  public String visit(BracketExpression n, String ourclass) {
      return n.f1.accept(this, ourclass); 
  }



  public static boolean isInteger(String str) {
      try {
        Integer.parseInt(str);
        return true;
      }
      catch (NumberFormatException nfe) {
        return false;
      }
    }



  public  String getfromVtable( String ourclass, String exp){
        String vtemp, findc=null, temp, classname,tempname ;
        ClassMembers cm = null;
        int x=0;
        tempname = w.gettemp();
        temp = w.gettemp();
        Stack<String> s = new Stack<String>();
        boolean f = true;
        variable v=null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0];
          vtemp = "TEMP 0";
        }
        else
          classname = ourclass;
        cm=st.cmgetST(classname);
        vtemp = cm.temp_add;
        s.push(cm.name);
        v = cm.varExist(exp);
        if(v!=null && f){
            f=false;
            findc = cm.name;
          }
        while(cm.extend!=null){
          cm=st.cmgetST(cm.extend);
          v = cm.varExist(exp);
          if(v!=null && f){
            f=false;
            findc = cm.name;
          }
        }
        while(!s.empty()){
          String str = s.pop();
          if(str == findc){
            cm = st.cmgetST(str);
            v = cm.varExist(exp);
            x=x+v.order; break;
          }
          else{
            cm = st.cmgetST(str);
            x = x + cm.var.size();
          }
        }
        w.generate("HLOAD " + tempname + " " + vtemp + " " + x*4);
        w.generate("MOVE " + temp + " " + tempname);
        return temp;
      }


     public Integer getnumVtable(String ourclass,String exp){

        String vtemp, findc=null, temp, classname,tempname ;
        ClassMembers cm = null;
        int x=0;
        tempname = w.gettemp();
        temp = w.gettemp();
        Stack<String> s = new Stack<String>();
        boolean f = true;
        variable v=null;
        if(ourclass.contains(" ")){ 
          String[] parts = ourclass.split(" ");
          classname = parts[0];
          vtemp = "TEMP 0";
        }
        else
          classname = ourclass;
        cm=st.cmgetST(classname);
        vtemp = cm.temp_add;
        s.push(cm.name);
        v = cm.varExist(exp);
        if(v!=null && f){
            f=false;
            findc = cm.name;
          }
        while(cm.extend!=null){
          cm=st.cmgetST(cm.extend);
          v = cm.varExist(exp);
          if(v!=null && f){
            f=false;
            findc = cm.name;
          }
        }
        while(!s.empty()){
          String str = s.pop();
          if(str == findc){
            cm = st.cmgetST(str);
            v = cm.varExist(exp);
            x=x+v.order; break;
          }
          else{
            cm = st.cmgetST(str);
            x = x + cm.var.size();
          }
        }
        return x;
      }
}

	