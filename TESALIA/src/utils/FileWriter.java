package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileWriter {

	PrintWriter fw;
	
	public FileWriter(String path){
		try {
			fw=new PrintWriter(new FileOutputStream(new File(path)));
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Imposible to create the output file");
		}
	}
	
	public void write(String data){
		fw.write(data+"\r\n");
		fw.flush();
	}
	
	public void close(){
		fw.close();
	}
	
	public void write(String[] data){
		String res ="";
		for(String str:data){
			res+=str+";";
		}
		write(res);
	}
}
