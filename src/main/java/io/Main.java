package io;

import parallel.*;
import rdf.ProvenanceManager;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLStreamException;

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
		  
		  SingleArticle singelArticel = new SingleArticle(clParser);
		  singelArticel.setPathForArticle("United_States", "en","2017-01-21T08%3A08%3A19Z");
		  
		  
		  ExecutorService executor = Executors.newFixedThreadPool(clParser.getThreads());
		  
		  ProvenanceManager provenanceManager = singelArticel.createProvenanceManager();
		  
		  Runnable worker = provenanceManager;
		  executor.execute(worker);
		  
		  executor.shutdown();

		  try {
				while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {		 
				}

		  } catch (InterruptedException e) {
				Log.info(e, "AWAITING_COMPLETION_OF_THREADS");
		 }
		  
		 
		  System.out.println(provenanceManager.getDumpParser().getTimestampt());
		  
		  
		  
		  
//		  ArrayBlockingQueue<String> queue = new  ArrayBlockingQueue<String> (clParser.getThreadsF());
//		  
//		  Producer producer = new Producer(queue, clParser.getPath());
//  		  producer.start();
//  		  
//  		for(int i= 0; i < clParser.getThreadsF(); i++)
//		{
//  			
//  			new Consumer(queue,clParser, "File" + i).start();
//		}
		
	}// end main

}// end class