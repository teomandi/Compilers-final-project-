import syntaxtree.*;
import java.util.* ;
import visitor.GJDepthFirst;



public class EvalVisitor extends GJDepthFirst<String,String>{

  SymbolT st = new SymbolT();

  public SymbolT EvalST(){
    return st;
  }

  public int maxtemp(){
    return integer;
  }

  int vcounter = 0;
  int mcounter = 0;
  int integer = 20;
  int arg;
	/**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
   public String visit(Goal n, String argu) {
      n.f0.accept(this, null);
      n.f1.accept(this, null);
  	  return " DONE!"; 
    }

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
   public String visit(MainClass n, String argu) {
      String id = n.f1.accept(this, null);
      ClassInfo cll = new ClassInfo(id, "null");
      ClassMembers cm = new ClassMembers(id, null);
      Method m = new Method("void","main",1);
      cm.meth.put("main",m);
      n.f11.accept(this, null);
      st.clputST(id, cll);
      st.cmputST(id, cm);
      n.f14.accept(this, id+" main");
      vcounter=0;
      n.f15.accept(this, id+" main");
      return null; 
    }

   /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public String visit(TypeDeclaration n, String argu) {
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
   public String visit(ClassDeclaration n, String argu) {
    String id = n.f1.accept(this, null);
    ClassInfo cll = new ClassInfo(id, "null");
    ClassMembers cm = new ClassMembers(id, null);
    st.cmputST(id, cm);
    n.f3.accept(this, id);
    vcounter=0;
    n.f4.accept(this, id);
    mcounter =0;
    st.clputST(id, cll);
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
   public String visit(ClassExtendsDeclaration n, String argu) {
      String id = n.f1.accept(this, null);
      String ext = n.f3.accept(this, null);
      
      /*if(!st.clcontainsKeyST(ext))
        throw new Error("Error: extended class: " + ext + " hasnt been decleared!");*/
      
      ClassInfo cll = new ClassInfo(id, ext);
      ClassMembers cm = new ClassMembers(id, ext);
      st.cmputST(id, cm);
      n.f5.accept(this, id);
      vcounter=0;
      n.f6.accept(this, id);
      mcounter=0;
      st.clputST(id, cll);
      return null; 
    }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, String ourclass) {
      String type, name;
      type = n.f0.accept(this, null);
      name = n.f1.accept(this, null);
      if(!ourclass.contains(" ")){        //class members variables
        vcounter ++;
        integer++;
        variable x = new variable(type, name, vcounter, integer);
        ClassMembers cm = st.cmgetST(ourclass);
        if(cm.checkSameVar(x)){
          System.out.println("Variable already exists");
          return null;
        }
        cm.var.add(x);
        st.cmputST(ourclass, cm);
      }
      else{                               //method's variables
        integer++;
        variable x = new variable(type, name, integer);
        String[] parts = ourclass.split(" ");
        String classname = parts[0]; 
        String methodname = parts[1];
        ClassMembers cm=st.cmgetST(classname);
        Method m=cm.meth.get(methodname);
        m.localvars.add(x);
        cm.meth.put(methodname, m);
        st.cmputST(classname, cm);
      }
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
      String type, name;
      mcounter++;
      type = n.f1.accept(this, null);
      name = n.f2.accept(this, null);
      Method m = new Method(type, name, mcounter);
      ClassMembers cm = st.cmgetST(ourclass);
      cm.meth.put(name, m);
      st.cmputST(ourclass, cm);
      String clmeth = ourclass + " " + name;
      arg = 0;
      n.f4.accept(this, clmeth);
      n.f7.accept(this, clmeth);
      n.f8.accept(this, clmeth);
      n.f10.accept(this, null);  //Check Expresion = return_type
      return null; 
    }

   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, String ourclass) {
      n.f0.accept(this, ourclass);
      n.f1.accept(this, ourclass);
      return null; 
    }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, String ourclass) {
      if(ourclass.contains(" ")){
        String[] parts = ourclass.split(" ");
        String classname = parts[0]; 
        String methodname = parts[1];
        ClassMembers cm=st.cmgetST(classname);
        Method m=cm.meth.get(methodname);
        String type, name;
        type = n.f0.accept(this, null);
        name = n.f1.accept(this, null);
        arg++;
        variable x = new variable(type, name, arg );
        if(m.checkSameArgu(x))
          throw new Error("Error: Argument already excists.");
        m.arguments.add(x);
        cm.meth.put(methodname, m);
        st.cmputST(classname, cm);
        return null;
      }
      else
        throw new Error("Error");
       
    }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public String visit(FormalParameterTail n, String ourclass) {
      n.f0.accept(this, ourclass);
      return null; 
    }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public String visit(FormalParameterTerm n, String ourclass) {
      n.f1.accept(this, ourclass);
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
//////////////////////////////////////////////////////////////////////////////////////
   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
   public String visit(Statement n, String s) {
      n.f0.accept(this, null);
      return null;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public String visit(Block n, String s) {
      n.f1.accept(this, null);
      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
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
   public String visit(ArrayAssignmentStatement n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
      n.f5.accept(this, null);
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
   public String visit(IfStatement n, String s) {
      n.f2.accept(this, null);
      n.f4.accept(this, null);
      n.f6.accept(this, null);
      return null;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public String visit(WhileStatement n, String s) {
      n.f2.accept(this, null);
      n.f4.accept(this, null);
      return null;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public String visit(PrintStatement n, String s) {
      n.f2.accept(this, null);
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
   public String visit(Expression n, String s) {
      n.f0.accept(this, null);
      return null;
   }

   /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
   public String visit(AndExpression n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public String visit(CompareExpression n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public String visit(PlusExpression n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public String visit(MinusExpression n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public String visit(TimesExpression n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public String visit(ArrayLookup n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public String visit(ArrayLength n, String s) {
      n.f0.accept(this, null);
      return null;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public String visit(MessageSend n, String s) {
      n.f0.accept(this, null);
      n.f2.accept(this, null);
      n.f4.accept(this, null);
      return null;
   }

   /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
   public String visit(ExpressionList n, String s) {
      n.f0.accept(this, null);
      n.f1.accept(this, null);
      return null;
   }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
   public String visit(ExpressionTail n, String s) {
      n.f0.accept(this, null);
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public String visit(ExpressionTerm n, String s) {
      n.f1.accept(this, null);
      return null;
   }

   /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
   public String visit(Clause n, String s) {
      n.f0.accept(this, null);
      return null;
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
   public String visit(PrimaryExpression n, String argu) {
      return n.f0.accept(this, null); 
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
   public String visit(ArrayAllocationExpression n, String s) {
      n.f3.accept(this, null);
      return null;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public String visit(AllocationExpression n, String s) {
      n.f1.accept(this, null);
      return null;
   }

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
   public String visit(NotExpression n, String s) {
      n.f1.accept(this, null);
      return null;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public String visit(BracketExpression n, String s) {
      n.f1.accept(this, null);
      return null;
   }
  
}