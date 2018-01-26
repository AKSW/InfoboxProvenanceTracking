package org.dbpedia.infoboxprov.io;

import java.util.concurrent.ArrayBlockingQueue;

import org.dbpedia.infoboxprov.parallel.*;

/**
 * The main class 
 * 
 * @author daniel
 */
public class Main {


	
	/**
	 * Main method for the project
	 *
	 * @param args parameter
	 */
	public static void main(String[] args) {
		  CLParser clParser = new CLParser(args);
		  clParser.validate();
		
		 /**
		  * To process multiple files a BlockingQueue is used. The number is controlled through the
		  * threadF parameter and the files are collected from the directory ArticleDumps.
		  */
		
		  ArrayBlockingQueue<String> queue = new  ArrayBlockingQueue<String> (clParser.getThreadsF());
		  
		  Producer producer = new Producer(queue, clParser , clParser.getPath());
  		  producer.start();
  		  
  		for(int i= 0; i < clParser.getThreadsF(); i++)
		{
  			
  			new Consumer(queue, clParser , "File" + i).start();
		}
			
	}// end main

}// end class