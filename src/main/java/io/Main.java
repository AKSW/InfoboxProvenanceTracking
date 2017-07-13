package io;

import dump.DumpParser;
import rdf.ProvenanceManager;


import org.apache.jena.atlas.logging.Log;
import com.beust.jcommander.JCommander;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The main class 
 */
public class Main {

	
	/**
	 * Main method for the project
	 *
	 * @param args parameters as specified in the entwurfsbeschreibung
	 */
	public static void main(String[] args) {

		
		CLParser clparser = new CLParser();
        JCommander.newBuilder()
        .addObject(clparser)
        .build()
        .parse(args);
    
        clparser.validate();
        
       
        FileHandler fh = new FileHandler(clparser.getPath());
       
    	while (fh.nextFileEntry()) {
    		
    		ExecutorService executor = Executors.newFixedThreadPool(clparser.getThreads());
    		String path = fh.getFileEntry();
    		
    			for (int i = clparser.getThreads() - 1; i>=0; i-- ){
    				Runnable worker = new ProvenanceManager("Thread_" + i       							,
    														path			            					, 
    														new DumpParser(clparser.getExtractionTimeFrame(),
    																	   clparser.getFinishedArticles()  ), 
    														i	  	 	   									,
    														clparser.getThreads()							,
    														clparser.getLanguage()							, 
    														clparser.getVariant()  							,
    														clparser.getReadvarian()						);
    				executor.execute(worker);
    			
    			}
    			executor.shutdown();
    			
    			
    			
    			try {
    				while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
    				}
    			} catch (InterruptedException e) {
    				Log.info(e, "AWAITING_COMPLETION_OF_THREADS");
    			}
    			
    			
    		}
    		
    	
        
		
		
		
	}// end main

}// end class