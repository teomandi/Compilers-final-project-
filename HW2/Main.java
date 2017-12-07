import syntaxtree.*;
import visitor.*;
import java.io.*;
import java.util.*;

class Main {
    public static void main (String [] args){
	if(args.length != 1){
	    System.err.println("Usage: java Driver <inputFile>");
	    System.exit(1);
	}
	FileInputStream fis = null;
	try{
		
	    fis = new FileInputStream(args[0]);
	    MiniJavaParser parser = new MiniJavaParser(fis);
	    System.err.println("Program parsed successfully.");
	    EvalVisitor eval = new EvalVisitor();
	    Goal root = parser.Goal();
	    System.out.print("First visit to fill our Symbolt Table: ");
	    System.out.println("~> " + root.accept(eval, null));
	    System.out.println("Second visit to check :");
	    TypeCheckin tc = new TypeCheckin(eval.EvalST());
	    System.out.println("~> " + root.accept(tc, null));
	    System.out.println("***** Program checked successfully! *****");
	}
	catch(ParseException ex){
	    System.out.println(ex.getMessage());
	}
	catch(FileNotFoundException ex){
	    System.err.println(ex.getMessage());
	}
	finally{
	    try{
		if(fis != null) fis.close();
	    }
	    catch(IOException ex){
		System.err.println(ex.getMessage());
	    }
	}
	System.out.println("End");
    }
}
