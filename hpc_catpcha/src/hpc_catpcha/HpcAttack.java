package hpc_catpcha;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
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
    ArrayList<ArrayList<ArrayList<Double>>> sizeCombinations;
    ArrayList<ArrayList<Integer>> figuresCombinations = new ArrayList<ArrayList<Integer>>();
    
	String attack(BufferedImage img,Integer threadCount) {	
		image = img;
		wordList = readFile();
		wordCount = wordList.size(); 
		sizeCombinations = new ArrayList<ArrayList<ArrayList<Double>>>();
		generateSizeCombinations();
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
	
	private void generateSizeCombinations() {
		for (int i = 1; i < 4; i++) {
			sizeCombinations.add(new ArrayList<ArrayList<Double>>());
			generateSizeCombinations(new ArrayList<Double>(), 0, i); 
		}
	}
	
	@SuppressWarnings("unchecked")
	public void generateSizeCombinations(ArrayList<Double> list, int n, int lenght) {
        if (n == lenght) {
        	sizeCombinations.get(lenght - 1).add((ArrayList<Double>)list.clone());
        	return;
        }
        for (double rotation = -0.4; rotation < 0.5; rotation = rotation + 0.1) {
        	list.add(rotation);
        	generateSizeCombinations(list, n + 1, lenght);
        	list.remove(rotation);
        }
    }

	
	@SuppressWarnings("unchecked")
	private void generateFiguresCombinations() {
		for (int L = 1; L < 25; L++)
		{
			for (int X = 1; X < 160 - L; X++)
			{
				for (int Y = 1; Y < 50 - L; Y++)
				{
					ArrayList<Integer> combinations = new ArrayList<Integer>();
					combinations.add(0, L);
					combinations.add(1, X);
					combinations.add(2, Y);
					figuresCombinations.add((ArrayList<Integer>)combinations.clone());
				}
			}
		}
	}
	
	List<String> getJob(int currentThread){
		try {
			mutex.acquire();
			
			List<String> job = wordList.subList(0, jobSize);
			slavesJob.put(currentThread, job);
			
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
						for (int k = 0; k < sizeCombinations.get(word.length() - 1).size(); k++) {
							Integer L = figuresCombinations.get(j).get(0);
							Integer X = figuresCombinations.get(j).get(1);
							Integer Y = figuresCombinations.get(j).get(2);
							ArrayList<Double> rotations = sizeCombinations.get(word.length() - 1).get(k);
							BufferedImage generatedImage = captchaGenerator.getCaptchaImageFromString(word, L, X, Y, rotations);
							if (generatedImage.equals(image))
							{
								found = true;
								foundWord = word;
							}
						}
					}
				}
			}
		}
	}
}
