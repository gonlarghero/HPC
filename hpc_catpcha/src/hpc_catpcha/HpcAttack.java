package hpc_catpcha;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class HpcAttack {
	
	BufferedImage image;
	List<String> wordList;
	int currentWord = 0;
	int jobSize = 1;
	int wordCount;
	boolean found;
	String foundWord = "noup";
	ArrayList<Slave> threadList;
    Semaphore mutex = new Semaphore(1);
    Map<Integer, List<String>> slavesJob = new HashMap<Integer, List<String>>();
    ArrayList<ArrayList<ArrayList<Double>>> angleCombinations = new ArrayList<ArrayList<ArrayList<Double>>>();
    ArrayList<ArrayList<ArrayList<Integer>>> figuresCombinations = new ArrayList<ArrayList<ArrayList<Integer>>>();
    
	String attack(BufferedImage img,Integer threadCount) throws IOException {	
		image = img;
		wordList = readFile();
		wordCount = wordList.size(); 
		generateAngleCombinations();
		generateFiguresCombinations();
		threadList = new ArrayList<Slave>();
		found = false;
		
		for (int i = 0; i < threadCount; i++) {
			threadList.add(new Slave(i));
		}
		
		 try {
			Thread.sleep(1000000);
			return "hola";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "hola";
		}
	}
	
	private void generateAngleCombinations() {
		for (int i = 1; i < 4; i++) {
			angleCombinations.add(new ArrayList<ArrayList<Double>>());
			generateAngleCombinations(new ArrayList<Double>(), 0, i); 
		}
	}
	
	@SuppressWarnings("unchecked")
	public void generateAngleCombinations(ArrayList<Double> list, int n, int lenght) {
        if (n == lenght) {
        	angleCombinations.get(lenght - 1).add((ArrayList<Double>)list.clone());
        	return;
        }
        for (double rotation = -0.4; rotation < 0.5; rotation = rotation + 0.1) {
			rotation = Math.round(rotation * 10) / 10.0;
        	list.add(rotation);
        	generateAngleCombinations(list, n + 1, lenght);
        	list.remove(rotation);
        }
    }

	
	@SuppressWarnings("unchecked")
	private void generateFiguresCombinations() throws IOException {
		ArrayList<String> positions = readFile("/noise-positions.txt");
		for (int i = 0; i < positions.size(); i++)
		{
			ArrayList<ArrayList<Integer>> squares = new ArrayList<ArrayList<Integer>>();
			String chosePos = positions.get(i);
			String[] pos = chosePos.split(":");
			String[] aux;
			int index = 0;
			for(String s:pos) {
				ArrayList<Integer> coords = new ArrayList<Integer>();
				aux = s.split(",");	
				coords.add(0, Integer.parseInt(aux[0]));
				coords.add(1, Integer.parseInt(aux[1]));
				coords.add(2, Integer.parseInt(aux[2]));
				squares.add(index, coords);
				index++;				
			}
			figuresCombinations.add(i,squares);
		}
		
	}
	
	List<String> getJob(int currentThread){
		try {
			mutex.acquire();
			
			List<String> job = wordList.subList(currentWord, currentWord + jobSize);
			slavesJob.put(currentThread, job);
			currentWord = currentWord + jobSize;
			
			mutex.release();
			
			return job;
        }
		catch (Exception x) {
			x.printStackTrace();
			return new ArrayList<String>();
		}
	}

	ArrayList<String> readFile() {
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
	        return wordList;

		}catch(Exception e) {
			return new ArrayList<String>();
		}	
	}
	
	private static ArrayList<String> readFile(String name) throws IOException {
		ArrayList<String> wordList = new ArrayList<String>();
		InputStream is = new FileInputStream(captchaGenerator.class.getResource(name).getPath());
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		String line = buf.readLine();

		while (line != null) {
			wordList.add(line);
			line = buf.readLine();
		}
		buf.close();
		return wordList;
	}
	
	public class Slave implements Runnable {
		Thread t;
		int id;
		List<String> currentJob;
		   
		Slave(int threadId){
		    id = threadId; 
		    t = new Thread(this, Integer.toString(id));
		    t.start();
		}
		
		public void run(){
			currentJob = getJob(id);
			while (currentJob.size() > 0 && !found)
			{
				for (int i = 0; i < currentJob.size(); i++)
				{
					String word = currentJob.get(i);
					for (int j = 0; j < figuresCombinations.size(); j++)
					{
						for (int k = 0; k < angleCombinations.get(word.length() - 1).size(); k++) {
							ArrayList<Double> rotations = angleCombinations.get(word.length() - 1).get(k);
							ArrayList<ArrayList<Integer>> squares = figuresCombinations.get(j);
							BufferedImage generatedImage = captchaGenerator.getCaptchaImageFromString(word, squares, rotations);
							if (compareImages(generatedImage, image))
							{
								found = true;
								foundWord = word;
							}
						}
					}
				}
				currentJob = getJob(id);
			}
		}
		
		public boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
			  // The images must be the same size.
			  if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
			    return false;
			  }

			  int width  = imgA.getWidth();
			  int height = imgA.getHeight();

			  // Loop over every pixel.
			  for (int y = 0; y < height; y++) {
			    for (int x = 0; x < width; x++) {
			      // Compare the pixels for equality.
			      if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
			        return false;
			      }
			    }
			  }

			  return true;
			}
	}
}
