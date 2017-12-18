package io;

import parallel.*;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * The main class 
 */
public class Main {

	public static String timestamp;
	
	
	
	/**
	 * Main method for the project
	 *
	 * @param args parameter
	 */
	public static void main(String[] args) {
		  CLParser clParser = new CLParser(args);
		  clParser.validate();
		
		 
		
		  ArrayBlockingQueue<String> queue = new  ArrayBlockingQueue<String> (clParser.getThreadsF());
		  
		  Producer producer = new Producer(queue, clParser , clParser.getPath());
  		  producer.start();
  		  
  		for(int i= 0; i < clParser.getThreadsF(); i++)
		{
  			
  			new Consumer(queue, clParser , "File" + i).start();
		}
			
	}// end main

}// end class