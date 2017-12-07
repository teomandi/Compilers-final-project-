import java.util.* ;
import java.io.*;


public class Writeri{

	public int tempcount;
	public int labelcount=0 ;
	public int out;
	PrintWriter writer;

	Writeri(int x, int y){
		tempcount = x+20;
		out=y;
	}
	
	public void createtxt(){
		try{
			writer=new PrintWriter("output"+out+".spg"); 
			System.out.println("File created: output"+out+".spg ");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generate(String str){
		writer.println("\t" + str);
	}
	public void generateNT(String str){  
		writer.println(str);
	}

	public void generateSL(String str){		//same line
		writer.print("\t" + str);
	}

	public void generateSSL(String str){		
		writer.print(str);
	}

	public void generateLAB(String str){
		writer.println(str);
	}

	public void close(){
		writer.close();
	}

	public String gettemp(){
		tempcount++;
		return "TEMP "+tempcount;
	}

	public String getlabel(){
		labelcount++;
		return "L"+labelcount;
	}


};


