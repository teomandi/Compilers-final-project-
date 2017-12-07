import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.* ;

public class TypeCheckin extends GJDepthFirst<String, String>{

  SymbolT st = new SymbolT() ;
  List<String> check_argu = new ArrayList<String>();

  TypeCheckin(SymbolT s){
    st =s;
  }

  /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
   public String visit(Goal n, String s) {
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      n.f2.accept(this, null);
    return " DONE"; }

   /**
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
   public String visit(MainClass n, String ourclass) {
      ourclass = n.f1.accept(this, null);
      n.f1.accept(this, null);
      n.f3.accept(this, null);
      n.f14.accept(this, null);
      n.f15.accept(this, ourclass);
      return null; 
 }

   /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public String visit(TypeDeclaration n, String ourclass) {
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
   public String visit(ClassDeclaration n, String ourclass) {
      ourclass = n.f1.accept(this, null);
      n.f3.accept(this, ourclass);
      n.f4.accept(this, ourclass);
      return null; }

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
   public String visit(ClassExtendsDeclaration n, String ourclass) {
      ourclass = n.f1.accept(this, null);
      n.f3.accept(this, ourclass);
      n.f5.accept(this, ourclass);
      n.f6.accept(this, ourclass);
      return null; 
    }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, String s) {
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
   public String visit(MethodDeclaration n, String ourclass) {
      String str1 , type,classname;
      type = n.f1.accept(this, null);
      String methodname = n.f2.accept(this, ourclass);
      ourclass = ourclass + " " + methodname;
      n.f4.accept(this, null);
      n.f7.accept(this, null);
      n.f8.accept(this, ourclass);
      str1 = n.f10.accept(this, ourclass);//Check Expresion = return_type
      variable v = null;
      if(type == "int" || type == "ArrayType"){
        if(str1 == "num" || isInteger(str1)){{ System.out.println("Correct return type.");}}
        else if(ourclass.contains(" ")){
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          ClassMembers cm = st.cmgetST(classname);
          Method m = cm.meth.get(methodname);
          if (cm.varExist(str1) != null)
            v=cm.varExist(str1);
          else if(m.arguExist(str1)!= null)
            v=m.arguExist(str1);
          else if(m.lvExist(str1)!= null)
            v=m.lvExist(str1);
          else{
           ClassInfo cl = st.clgetST(classname);
            if(cl.extend != "null"){
              cm = st.cmgetST(cl.extend);
              if (cm.varExist(str1) != null)
                v=cm.varExist(str1);
            }
            else
              throw new Error("Error: variable " + str1 +" has not been decleared");
          }
          if(v.type!="ArrayType" && v.type!="int")
            throw new Error("Error: Wrong return type");
        }
          else{//mainclass
          ClassMembers cm = st.cmgetST(ourclass);
            if(cm.varExist(str1) != null)
            v=cm.varExist(str1);
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");            
         if(v.type!="ArrayType" && v.type!="int" )
            throw new Error("Error: Wrong return type");
       }
      }
      else if(type == "boolean"){
        if(str1 == "true" || str1 == "faLse"){ System.out.println("Correct return type.");}
          else if(ourclass.contains(" ")){
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          ClassMembers cm = st.cmgetST(classname);
          Method m = cm.meth.get(methodname);
          if (cm.varExist(str1) != null)
            v=cm.varExist(str1);
          else if(m.arguExist(str1)!= null)
            v=m.arguExist(str1);
          else if(m.lvExist(str1)!= null)
            v=m.lvExist(str1);
          else{
            ClassInfo cl = st.clgetST(classname);
            if(cl.extend != "null"){
              cm = st.cmgetST(cl.extend);
              if (cm.varExist(str1) != null)
                v=cm.varExist(str1);
            }
            else
              throw new Error("variable " + str1 +" has not been decleared");
          }
          if(v.type!="boolean")
            throw new Error("Error: Wrong return type");
        }
        else{
          ClassMembers cm = st.cmgetST(ourclass);
          if(cm.varExist(str1) != null)
            v=cm.varExist(str1);
          else
            throw new Error("variable " + str1 +" has not been decleared");           
          if(v.type != "boolean" )
            throw new Error("Error: Wrong return type");     
        }
      }
      return null; 
    }

   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, String s) {
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      return null; 
    }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, String s) {
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      return null; 
    }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public String visit(FormalParameterTail n, String s) {
      n.f0.accept(this, null);
      return null; 
    }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public String visit(FormalParameterTerm n, String s) {
      n.f1.accept(this, null);
      return null; 
    }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | StringType()
    *       | Identifier()
    */
   public String visit(Type n, String argu) {
      return n.f0.accept(this, null);
       
    }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(ArrayType n, String argu) {
      return "ArrayType"; 
    }

   /**
    * f0 -> "boolean"
    */
   public String visit(BooleanType n, String argu) {
      return "boolean";
    }

   /**
    * f0 -> "int"
    */
   public String visit(IntegerType n, String argu) {
      return "int"; 
    }
///////////////////////////////////////////////////////////////////////////
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
      String id, str1, classname, methodname;
      
      id = n.f0.accept(this, null);
      variable v1 = null, v = null;
      if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(id) != null)
          v1=cm.varExist(id);
        else if(m.arguExist(id)!= null)
          v1=m.arguExist(id);
        else if(m.lvExist(id)!= null)
          v1=m.lvExist(id);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(id) != null)
              v1=cm.varExist(id);
          }
          else
            throw new Error("Error: variable " + id +" has not been decleared");           
        }
      }
      else{// not in method but in main
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(id) != null)
          v1=cm.varExist(id);
        else
          throw new Error("variable " + id +" has not been decleared");
        if(v1.type == "ArrayType" )
          throw new Error("Error: Incorect type");
      }
      //to v exei to id mas me onoma k typo !!!!!!++++++++++++++++++++++++++++++++++++
      str1 = n.f2.accept(this, ourclass);
      if(v1.type == "int" || v1.type == "ArrayType"){
        if(str1 == "num" || isInteger(str1)){ System.out.println("Correct assignment.");}
        else if(ourclass.contains(" ")){
          String[] parts = ourclass.split(" ");
          classname = parts[0]; 
          methodname = parts[1];
          ClassMembers cm = st.cmgetST(classname);
          Method m = cm.meth.get(methodname);
          if (cm.varExist(str1) != null)
            v=cm.varExist(str1);
          else if(m.arguExist(str1)!= null)
            v=m.arguExist(str1);
          else if(m.lvExist(str1)!= null)
            v=m.lvExist(str1);
          else{
           ClassInfo cl = st.clgetST(classname);
            if(cl.extend != "null"){
              cm = st.cmgetST(cl.extend);
              if (cm.varExist(str1) != null)
                v=cm.varExist(str1);
            }
            else
              throw new Error("Error: variable " + str1 +" has not been decleared");
          }
          if(v.type!="ArrayType" && v.type!="int")
            throw new Error("Error: Incorect type");
        }
        else{//mainclass
          ClassMembers cm = st.cmgetST(ourclass);
          if(cm.varExist(str1) != null)
            v=cm.varExist(str1);
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");            
          if(v.type!="ArrayType" && v.type!="int" )
            throw new Error("Error: Incorect type");
        }
      }
      else if(v1.type == "boolean"){          // boolean type
        if(str1 == "true" || str1 == "false"){ System.out.println("Correct assignment.");}
        else if(ourclass.contains(" ")){
            String[] parts = ourclass.split(" ");
            classname = parts[0]; 
            methodname = parts[1];
            ClassMembers cm = st.cmgetST(classname);
            Method m = cm.meth.get(methodname);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
            else if(m.arguExist(str1)!= null)
              v=m.arguExist(str1);
            else if(m.lvExist(str1)!= null)
              v=m.lvExist(str1);
            else{
              ClassInfo cl = st.clgetST(classname);
              if(cl.extend != "null"){
                cm = st.cmgetST(cl.extend);
                if (cm.varExist(str1) != null)
                  v=cm.varExist(str1);
              }
              else
                throw new Error("Error: variable " + str1 +" has not been decleared");
            }
            if(v.type!="boolean")
              throw new Error("Error: Incorect type");
          }
        else{//mainclass
            ClassMembers cm = st.cmgetST(ourclass);
            if(cm.varExist(str1) != null)
              v=cm.varExist(str1);
            else
              throw new Error("Error: variable " + str1 +" has not been decleared");            
             if(v.type != "boolean" )
              throw new Error("Error: Incorect type");
          }
        }
      else{         //class fasi
        if(st.clcontainsKeyST(str1)){
          if(str1 == v1.type)
            System.out.println("Correct assignment.");
          else
            throw new Error("Error: Incorect type");
        }
        else if(str1 == "this"){
          if(ourclass.contains(" ")){
            String[] parts = ourclass.split(" ");
            classname = parts[0]; 
          }
          else
            classname = ourclass;
          if(v1.type.equals(classname) )
            return null;
          else
            throw new Error("Error: Incorect type");
        }
        else if(ourclass.contains(" ")){
            String[] parts = ourclass.split(" ");
            classname = parts[0]; 
            methodname = parts[1];
            ClassMembers cm = st.cmgetST(classname);
            Method m = cm.meth.get(methodname);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
            else if(m.arguExist(str1)!= null)
              v=m.arguExist(str1);
            else if(m.lvExist(str1)!= null)
              v=m.lvExist(str1);
            else{
              ClassInfo cl = st.clgetST(classname);
              if(cl.extend != "null"){
                cm = st.cmgetST(cl.extend);
                if (cm.varExist(str1) != null)
                  v=cm.varExist(str1);
              }
              else
                throw new Error("Error: variable " + str1 +" has not been decleared");
            }
            if(v.type!=v1.type)
              throw new Error("Error: Incorect type");
          }
        else{//mainclass
            ClassMembers cm = st.cmgetST(ourclass);
            if(cm.varExist(str1) != null)
              v=cm.varExist(str1);
            else
              throw new Error("Error: variable " + str1 +" has not been decleared");            
            if(v.type != v1.type )
              throw new Error("Error: Incorect type");
        } 
      }
      return null; 
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
   public String visit(ArrayAssignmentStatement n, String ourclass) {
      String id, str1, classname, methodname; 
      id = n.f0.accept(this, null);
      variable v=null;
      if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(id) != null)
          v=cm.varExist(id);
        else if(m.arguExist(id)!= null)
          v=m.arguExist(id);
        else if(m.lvExist(id)!= null)
          v=m.lvExist(id);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(id) != null)
              v=cm.varExist(id);
          }
          else
            throw new Error("Error: variable " + id +" has not been decleared");
        }
      }
      else{// not in method but in main
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(id) != null)
          v=cm.varExist(id);
        else
          throw new Error("Error: variable " + id +" has not been decleared");            
      }
      if(v.type != "ArrayType" )
        throw new Error("Error: Incorect type");//+++++++++++++++++++++++++++++++++++++++++++++++++++++++
      str1 = n.f2.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){}
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");            
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      str1 = n.f5.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){ System.out.println("Correct Array assignment.");}
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");            
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
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
      String str1, classname, methodname ;
      variable v =null;
      str1 = n.f2.accept(this, ourclass);
      if(str1 == "true" || str1 == "flase"){ System.out.println("Correct if Expression");}
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="boolean")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");            
        if(v.type != "boolean" )
          throw new Error("Error: Incorect type");
      }
      n.f4.accept(this, ourclass);
      n.f6.accept(this, ourclass);
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
      String str1, classname, methodname ;
      variable v =null;
      str1 = n.f2.accept(this, ourclass);
      if(str1 == "true" || str1 == "flase"){ System.out.println("Correct while Expression");}
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="boolean")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");            
        if(v.type != "boolean" )
          throw new Error("Error: Incorect type");
      }
      n.f4.accept(this, ourclass);
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
    String str1, classname, methodname ;
    variable v =null;
    str1 = n.f2.accept(this, ourclass);
    if(str1 == "num" || isInteger(str1)){ System.out.println("Correct printStatement.");}
    else if (str1 == "true" || str1 == "false")
      throw new Error("Error:  cannot print a boolean type");
    else if(ourclass.contains(" ")){      //einai id
      String[] parts = ourclass.split(" ");
      classname = parts[0]; 
      methodname = parts[1];
      ClassMembers cm = st.cmgetST(classname);
      Method m = cm.meth.get(methodname);
      if (cm.varExist(str1) != null){
        v=cm.varExist(str1);
        if(v.type != "int" && v.type !="ArrayType")
          throw new Error("Error: cannot print a " + v.type + " type");
      }
      else if(m.arguExist(str1)!= null){
        v=m.arguExist(str1);
        if(v.type != "int" && v.type !="ArrayType")
          throw new Error("Error: cannot print a " + v.type + " type");
      }
      else if(m.lvExist(str1)!= null){
        v=m.lvExist(str1);
        if(v.type != "int" && v.type !="ArrayType")
          throw new Error("Error: cannot print a " + v.type + " type");
      }
      else{
        ClassInfo cl = st.clgetST(classname);
        if(cl.extend != "null"){
          cm = st.cmgetST(cl.extend);
          if (cm.varExist(str1) != null){
            v=cm.varExist(str1);
            if(v.type != "int" && v.type !="ArrayType")
              throw new Error("Error: cannot print a " + v.type + " type");
          }
        }
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");
      }
    }
    else{
      ClassMembers cm = st.cmgetST(ourclass);
      if(cm.varExist(str1) != null)
        v=cm.varExist(str1);
        if(v.type != "int" && v.type !="ArrayType")
          throw new Error("Error: cannot print a " + v.type + " type");
      else
        throw new Error("Error: variable " + str1 +" has not been decleared");
    }
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
      String str1, str2, classname, methodname ;
      variable v = null;
      str1 = n.f0.accept(this, ourclass);
      if(str1 == "true" || str1 == "false"){}
      else if(str1 == "num" || isInteger(str1)) 
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="boolean")
          throw new Error("Error: Incorect type");
      }
      else{
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");         
        if(v.type != "boolean" )
          throw new Error("Error: Incorect type");
      }
      str2 = n.f2.accept(this, ourclass);
      if(str2 == "true" || str2 == "false"){ System.out.println("Correct and condition.");}
      else if(str1 == "num" || isInteger(str1))
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str2) != null)
          v=cm.varExist(str2);
        else if(m.arguExist(str2)!= null)
          v=m.arguExist(str2);
        else if(m.lvExist(str2)!= null)
          v=m.lvExist(str2);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str2) != null)
              v=cm.varExist(str2);
          }
          else
            throw new Error("Error: variable " + str2 +" has not been decleared");
        }
        if(v.type!="boolean")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str2) != null)
          v=cm.varExist(str2);
        else
          throw new Error("Error: variable " + str2 +" has not been decleared");        
        if(v.type != "boolean" )
          throw new Error("Error: Incorect type");
      }
      return "true"; 
    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public String visit(CompareExpression n, String ourclass) {
      String str1, str2, classname, methodname;
      variable v=null;
      str1 = n.f0.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){}
      else if(str1 == "true" || str1 == "false" )
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");            
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      str1 = n.f2.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){ System.out.println("Correct Expression.");}
      else if(str1 == "true" || str1 == "false" )
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");           
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      return "true"; 
    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public String visit(PlusExpression n, String ourclass) {
      String str1, str2, classname, methodname;
      variable v=null;
      str1 = n.f0.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){}
      else if(str1 == "true" || str1 == "false" )
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");          
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      str1 = n.f2.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){ System.out.println("Correct Expression.");}
      else if(str1 == "true" || str1 == "false" )
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");        
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      return "num"; 
    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public String visit(MinusExpression n, String ourclass) {
      String str1, str2, classname, methodname;
      variable v=null;
      str1 = n.f0.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){}
      else if(str1 == "true" || str1 == "false" )
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");           
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      str1 = n.f2.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){ System.out.println("Correct Expression.");}
      else if(str1 == "true" || str1 == "false" )
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");           
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      return "num"; 
    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public String visit(TimesExpression n, String ourclass) {
      String str1, str2, classname, methodname;
      variable v=null;
      str1 = n.f0.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){}
      else if(str1 == "true" || str1 == "false" )
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");            
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      str1 = n.f2.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){ System.out.println("Correct Expression.");}
      else if(str1 == "true" || str1 == "false" )
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");         
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      return "num"; 
    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
  public String visit(ArrayLookup n, String ourclass) {
    String str1, classname, methodname;
    variable v = null;
    str1 = n.f0.accept(this, ourclass);
    if(ourclass.contains(" ")){
      String[] parts = ourclass.split(" ");
      classname = parts[0]; 
      methodname = parts[1];
      ClassMembers cm = st.cmgetST(classname);
      Method m = cm.meth.get(methodname);
      if (cm.varExist(str1) != null)
        v=cm.varExist(str1);
      else if(m.arguExist(str1)!= null)
        v=m.arguExist(str1);
      else if(m.lvExist(str1)!= null)
        v=m.lvExist(str1);
      else{
        ClassInfo cl = st.clgetST(classname);
        if(cl.extend != "null"){
          cm = st.cmgetST(cl.extend);
          if (cm.varExist(str1) != null)
            v=cm.varExist(str1);
        }
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");
      }
      if(v.type!="ArrayType")
        throw new Error("Error: Incorect type");
    }
    else{
      ClassMembers cm = st.cmgetST(ourclass);
      if(cm.varExist(str1) != null)
        v=cm.varExist(str1);
      else
        throw new Error("Error: variable " + str1 +" has not been decleared");            
      if(v.type != "ArrayType" )
        throw new Error("Error: Incorect type");
    }
    str1 = n.f2.accept(this, ourclass);
    if(str1 == "num" || isInteger(str1)){ System.out.println("Correct Array Expression.");}
    else if(str1 == "true" || str1== "false")
      throw new Error("Error: Incorect type");
    else if(ourclass.contains(" ")){
      String[] parts = ourclass.split(" ");
      classname = parts[0]; 
      methodname = parts[1];
      ClassMembers cm = st.cmgetST(classname);
      Method m = cm.meth.get(methodname);
      if (cm.varExist(str1) != null)
        v=cm.varExist(str1);
      else if(m.arguExist(str1)!= null)
        v=m.arguExist(str1);
      else if(m.lvExist(str1)!= null)
        v=m.lvExist(str1);
      else{
        ClassInfo cl = st.clgetST(classname);
        if(cl.extend != "null"){
          cm = st.cmgetST(cl.extend);
          if (cm.varExist(str1) != null)
            v=cm.varExist(str1);
        }
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");
      }
      if(v.type!="ArrayType" && v.type!="int")
        throw new Error("Error: Incorect type");
    }
    else{//mainclass
      ClassMembers cm = st.cmgetST(ourclass);
      if(cm.varExist(str1) != null)
        v=cm.varExist(str1);
      else
        throw new Error("Error: variable " + str1 +" has not been decleared");            
      if(v.type!="ArrayType" && v.type!="int" )
        throw new Error("Error: Incorect type");
    }
    return "num"; 
  }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
  public String visit(ArrayLength n, String ourclass) {
    String str1, classname, methodname;
    variable v = null;
    str1 = n.f0.accept(this, ourclass);
    if(str1 == "true" || str1 == "false" || str1 == "num" || isInteger(str1))
      throw new Error("Error: Incorect type");
    else if(str1 == "ArrayType"){System.out.println("Correct Array length Expression call"); return "num";}
    if(ourclass.contains(" ")){
      String[] parts = ourclass.split(" ");
      classname = parts[0]; 
      methodname = parts[1];
      ClassMembers cm = st.cmgetST(classname);
      Method m = cm.meth.get(methodname);
      if (cm.varExist(str1) != null)
        v=cm.varExist(str1);
      else if(m.arguExist(str1)!= null)
        v=m.arguExist(str1);
      else if(m.lvExist(str1)!= null)
        v=m.lvExist(str1);
      else{
        ClassInfo cl = st.clgetST(classname);
        if(cl.extend != "null"){
          cm = st.cmgetST(cl.extend);
          if (cm.varExist(str1) != null)
            v=cm.varExist(str1);
        }
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");
      }
      if(v.type!="ArrayType")
        throw new Error("Error: Incorect type");
    }
    else{//mainclass
      ClassMembers cm = st.cmgetST(ourclass);
      if(cm.varExist(str1) != null)
        v=cm.varExist(str1);
      else
        throw new Error("Error: variable " + str1 +" has not been decleared");           
      if(v.type != "ArrayType" )
        throw new Error("Error: Incorect type");
    } 
    return "num";
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
      String  str1, str, arguments, classname, methodname, argu;
      variable v = null;
      boolean iSthis = false;
      str1 = n.f0.accept(this, ourclass);  //classname
      str = n.f2.accept(this, ourclass);  //methodname
      if(str1 == "this") { 
        iSthis = true; 
      }
      else if( st.clcontainsKeyST(str1)){}
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)
              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type == "ArrayType" || v.type == "int" || v.type == "boolean")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");
        if(v.type == "ArrayType" || v.type == "int" || v.type == "boolean")
          throw new Error("Error: Incorect type");
      }
      Method m =null;
      if(iSthis){
        if(ourclass.contains(" ")){
          String[] parts = ourclass.split(" ");
          classname = parts[0];
        }
        else
          classname = ourclass;
      }
      else if(st.clcontainsKeyST(str1))
        classname =str1;
      else
        classname = v.type;
      str1 = n.f2.accept(this, ourclass);  //methodname
      ClassMembers cm = null;
      if(st.cmcontainsKeyST(classname))
        cm = st.cmgetST(classname);
      else{ //check sta extends
        ClassInfo cl = st.clgetST(classname);
        if(cl.extend != "null")
          cm = st.cmgetST(cl.extend);
        else 
          throw new Error("Error: Method " + str1 +" has not been decleared");
      }
      if(cm.meth.containsKey(str1)){
        m=cm.meth.get(str1);
      }
      else 
        throw new Error("Error: Method " + str1 +" has not been decleared");
      boolean ext = false, found = true;
      String name;
      if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        name = parts[0];
       }
       else
        name = ourclass;
      ClassInfo cl = st.clgetST(name);
      if (cl.extend != "null")
        ext = true;
      n.f4.accept(this, ourclass);  //arguments <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<------------------
      String tpe;
      if(m.arguments.size() !=  check_argu.size())
          throw new Error("Error: Not enought arguments");
      for (Iterator<String> it = check_argu.iterator(); it.hasNext(); ) {     //check stin methodo
          String a = it.next();
          if(a == "true" || a == "false")
            tpe = "boolean" ;
          else if(isInteger(a) || a == "num")
            tpe = "int";
          else if(a == "this"){
            if(ourclass.contains(" ")){
              String[] parts = ourclass.split(" ");
              classname = parts[0]; 
            }
            else 
              classname = ourclass;
            tpe = classname;
          }
          else if(st.clcontainsKeyST(a))
            tpe = a;
          else{
            if(ourclass.contains(" ")){
              String[] parts = ourclass.split(" ");
              classname = parts[0]; 
              methodname = parts[1];
              cm = st.cmgetST(classname);
              Method m2 = cm.meth.get(methodname);
              if (cm.varExist(a) != null)
                v=cm.varExist(a);
              else if(m2.arguExist(a)!= null)
                v=m2.arguExist(a);
              else if(m2.lvExist(a)!= null)
                v=m2.lvExist(a);
              else{
                cl = st.clgetST(classname);
                if(cl.extend != "null"){
                  cm = st.cmgetST(cl.extend);
                  if (cm.varExist(a) != null)
                    v=cm.varExist(a);
                }
                else
                  throw new Error("Error: variable " + a +" has not been decleared" + str1);
              }
            }
            else{//mainclass
              cm = st.cmgetST(ourclass);
              if(cm.varExist(a) != null)
                v=cm.varExist(a);
              else
                throw new Error("Error: variable " + a +" has not been decleared");
            }
            tpe=v.type;
          }
          if(!m.typeExist(tpe)){
            boolean f = false;
            if(st.clcontainsKeyST(tpe)){
              ClassInfo c = st.clgetST(tpe);
              while(c.extend !="null"){
                tpe = c.extend;
                if(m.typeExist(tpe))
                  f = true;
                  c = st.clgetST(tpe);
              }
              if(!f){
                found = false;
                break;
              }
            }
            else{
              found = false;
              break;
            }
          }
      }
      while(found == false && ext == true ){ //den to brike stin knki psaxnei sta ext
        cl = st.clgetST(classname);
        cm = st.cmgetST(cl.extend);
        if(cm.meth.containsKey(str1))
          m = cm.meth.get(str1);
        else
          throw new Error("Error : Method has not been decleared");
        for (Iterator<String> it = check_argu.iterator(); it.hasNext(); ) {     //check stin methodo
          String a = it.next();
          if(a == "true" || a == "false")
            tpe = "boolean" ;
          else if(isInteger(a) || a == "num")
            tpe = "int";
          else if(a == "this"){
            if(ourclass.contains(" ")){
              String[] parts = ourclass.split(" ");
              classname = parts[0]; 
            }
            else 
              classname = ourclass;
            tpe = classname;
          }
          else{
            if(ourclass.contains(" ")){
              String[] parts = ourclass.split(" ");
              classname = parts[0]; 
              methodname = parts[1];
              cm = st.cmgetST(classname);
              Method m1 = cm.meth.get(methodname);
              if (cm.varExist(a) != null)
                v=cm.varExist(a);
              else if(m1.arguExist(a)!= null)
                v=m1.arguExist(a);
              else if(m1.lvExist(a)!= null)
                v=m1.lvExist(a);
              else{
                cl = st.clgetST(classname);
                if(cl.extend != "null"){
                  cm = st.cmgetST(cl.extend);
                  if (cm.varExist(a) != null)
                    v=cm.varExist(a);
                }
                else
                  throw new Error("Error: variable " + a +" has not been decleared");
              }
            }
            else{//mainclass
              cm = st.cmgetST(ourclass);
              if(cm.varExist(a) != null)
                v=cm.varExist(a);
              else
                throw new Error("Error: variable " + a +" has not been decleared");
            }
            tpe=v.type;
          }
          if(!m.typeExist(tpe)){
            boolean f = false;
            if(st.clcontainsKeyST(tpe)){
              ClassInfo c = st.clgetST(tpe);
              while(c.extend !="null"){
                tpe = c.extend;
                if(m.typeExist(tpe))
                  f = true;
                  c = st.clgetST(tpe);
              }
              if(!f){
                found = false;
                break;
              }
            }
            else{
              found = false;
              break;
            }
          }
          else
            found =true;
        }
        cl = st.clgetST(cl.extend);
        if(cl.extend!="null"){
          classname=cl.extend;
          ext =true;
        }
        else
          ext=false;

      }      
      if(found == false)
        throw new Error("Error : Method " + str1 + " did not found");
      check_argu.clear();
      System.out.println("Correct fanction call ~>" +n.f2.accept(this, ourclass));
      if(m.type == "int" || m.type == "ArrayType")
        return "num";
      else if( m.type == "boolean")
        return "true";
      else
        return m.type;
    }


   /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
   public String visit(ExpressionList n, String ourclass) {
      check_argu.add(n.f0.accept(this, ourclass));
      n.f1.accept(this, ourclass);
      return null; 
    }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
   public String visit(ExpressionTail n, String ourclass) {
      n.f0.accept(this, ourclass);
      return null;
    }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public String visit(ExpressionTerm n, String ourclass) {
      check_argu.add(n.f1.accept(this, ourclass));
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
    * f0 -> IntegerStringLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
   public String visit(PrimaryExpression n, String ourclass) {
      return n.f0.accept(this, ourclass); 
    }

   /**
    * f0 -> <Integer_LITERAL>
    */
   public String visit(IntegerLiteral n, String argu) {
      return n.f0.toString(); 
    }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, String argu) {
      return "true"; 
    }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, String argu) {
      return "false"; 
    }

   /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Identifier n, String argu) {
      //System.out.println("!!! " + n.f0.toString());
      return n.f0.toString();
    }

   /**
    * f0 -> "this"
    */
   public String visit(ThisExpression n, String argu) {
      return "this"; 
    }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(ArrayAllocationExpression n, String ourclass) {
      String str1, str2, classname, methodname;
      variable v=null;
      str1 = n.f3.accept(this, ourclass);
      if(str1 == "num" || isInteger(str1)){}
      else if(str1 == "true" || str1 == "false")
        throw new Error("Error: Incorect type");
      else if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        classname = parts[0]; 
        methodname = parts[1];
        ClassMembers cm = st.cmgetST(classname);
        Method m = cm.meth.get(methodname);
        if (cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else if(m.arguExist(str1)!= null)
          v=m.arguExist(str1);
        else if(m.lvExist(str1)!= null)
          v=m.lvExist(str1);
        else{
          ClassInfo cl = st.clgetST(classname);
          if(cl.extend != "null"){
            cm = st.cmgetST(cl.extend);
            if (cm.varExist(str1) != null)

              v=cm.varExist(str1);
          }
          else
            throw new Error("Error: variable " + str1 +" has not been decleared");
        }
        if(v.type!="ArrayType" && v.type!="int")
          throw new Error("Error: Incorect type");
      }
      else{//mainclass
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.varExist(str1) != null)
          v=cm.varExist(str1);
        else
          throw new Error("Error: variable " + str1 +" has not been decleared");         
        if(v.type!="ArrayType" && v.type!="int" )
          throw new Error("Error: Incorect type");
      }
      return "num";
    }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public String visit(AllocationExpression n, String ourclass) {
      String id, classname;
      id = n.f1.accept(this, ourclass);
      if(st.clcontainsKeyST(id))
        return id;
      else
        throw new Error("Error: variable " + id +" has not been decleared");
    }

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
   public String visit(NotExpression n, String ourclass) {
      String str = n.f1.accept(this, ourclass); 
      if(str == "true" || str == "false")
        return "true";
      else if(str == "num" || isInteger(str))
        throw new Error("Error: Incorect type");
      else {
        variable v = null;
        if(ourclass.contains(" ")){
          String[] parts = ourclass.split(" ");
          String classname = parts[0]; 
          String methodname = parts[1];
          ClassMembers cm = st.cmgetST(classname);
          Method m = cm.meth.get(methodname);
          if (cm.varExist(str) != null)
            v=cm.varExist(str);
          else if(m.arguExist(str)!= null)
            v=m.arguExist(str);
          else if(m.lvExist(str)!= null)
            v=m.lvExist(str);
          else{
            ClassInfo cl = st.clgetST(classname);
            if(cl.extend != "null"){
              cm = st.cmgetST(cl.extend);
              if (cm.varExist(str) != null)
                v=cm.varExist(str);
            }
            else
              throw new Error("Error: variable " + str +" has not been decleared");
          }
          if(v.type!="boolean")
            throw new Error("Error: Incorect type");
          else return "true";   //dn mas noiazei arkei na to apodektei
        }
        else{//mainclass
          ClassMembers cm = st.cmgetST(ourclass);
          if(cm.varExist(str) != null)
            v=cm.varExist(str);
          else
            throw new Error("Error: variable " + str +" has not been decleared");           
          if(v.type != "boolean" )
            throw new Error("Error: Incorect type");
          else return "true";   //dn mas noiazei arkei na to apodektei
        }
      }
    }


   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public String visit(BracketExpression n, String ourclass) {
      return n.f1.accept(this, ourclass);
    }

    public int countstr(String s, char c){
      int counter = 0;
      for( int i=0; i<s.length(); i++ ) {
        if( s.charAt(i) == c ) {
          counter++;
        } 
      }
      return counter;
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
}