package hpc_catpcha;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HpcAttack {
	
		
	String attack(BufferedImage img,Integer threadCount) {	
		try {
			InputStream dictionary = new FileInputStream(captchaGenerator.class.getResource("/dictionary.txt").getPath()); 
	        BufferedReader buf = new BufferedReader(new InputStreamReader(dictionary));
	        String line = buf.readLine();
	        ArrayList<String> wordList = new ArrayList<String>();
	        while (line != null) {
	        	wordList.add(line);
	        	line = buf.readLine();
	        }
	        buf.close();
		}catch(Exception e) {
			return(e.getMessage());
		}		
		
        return "try";
	}
	
	public class MyRunnable implements Runnable {
	    public void run() {
	        //Code
	    }
	}

}
