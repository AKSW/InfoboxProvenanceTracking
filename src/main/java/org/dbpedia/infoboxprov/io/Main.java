package org.dbpedia.infoboxprov.io;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import org.dbpedia.infoboxprov.parallel.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

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
	public static void main(String[] args) throws Exception {
		
		
		
		
		
		
		CLParser clParser = new CLParser(args);
		//CLParser clParser = new CLParser("miele", "en");
		  
		/**
		 * To process multiple files a BlockingQueue is used. The number is controlled through the
		 * threadF parameter and the files are collected from the directory ArticleDumps.
		 */
			
		
		  
		  if(clParser.getPort()>=0) {
			  
			  
			  String[] entries = new File("ArticleDumps").list();
			  
			
			  
              for (int i=0;i<entries.length;i++){
            	  
            	
                  new File("ArticleDumps/" + entries[i]).delete();
           
              }
			  
			  
			  Server server = new Server(clParser.getPort());
			     // Handler for multiple web apps
			     HandlerCollection handlers = new HandlerCollection();

			     
			     // Creating the first web application context
			     WebAppContext webapp = new WebAppContext();
			     webapp.setResourceBase("src/main/webapp");
			     webapp.setContextPath("/provenance");
			     handlers.addHandler(webapp);

			     // Adding the handlers to the server
			     server.setHandler(handlers);
			     
			     // Starting the Server

			     server.start();
			     System.out.println("Server started at URL: localhost:" + clParser.getPort() + "/provenance");
			     
			  
			     server.join();
			   
		  }else {
			  	ArrayBlockingQueue<String> queue = new  ArrayBlockingQueue<String> (clParser.getThreadsF());
			    clParser.validate();
			    new Producer(queue, clParser , clParser.getPath()).start();
			  
			  	for(int i= 0; i < clParser.getThreadsF(); i++)
			  	{
  			
			  		new Consumer(queue, clParser , "File" + i).start();
			  	}
  		
  	  }
		  
		
	}// end main

}// end class