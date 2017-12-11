package io;

import parallel.*;
import rdf.ProvenanceManager;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import org.apache.jena.atlas.logging.Log;

import dump.SingleArticle;


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
		  
		  Producer producer = new Producer(queue, clParser.getPath());
  		  producer.start();
  		  
  		for(int i= 0; i < clParser.getThreadsF(); i++)
		{
  			
  			new Consumer(queue,clParser, "File" + i).start();
		}
			
	}// end main

}// end class