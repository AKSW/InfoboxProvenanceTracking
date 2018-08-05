package org.dbpedia.infoboxprov.io;

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
	 * @throws Exception maybe something goes wrong
	 */
	public static void main(String[] args) throws Exception {
		
		
		
		
		
		
		CLParser clParser = new CLParser(args);
		
		/**
		 * To process multiple files a BlockingQueue is used. The number is controlled through the
		 * threadF parameter and the files are collected from the directory ArticleDumps.
		 * 
		 * 
		 * To parse dumps and not single Article set the portnumber at the config file to -1
		 */
			
		
		  
		  if(clParser.getPort()>=0) {
			  
			/** empty the folder ArticleDumps
			    String[] entries = new File("ArticleDumps").list();
			  
              for (int i=0;i<entries.length;i++){
            	  
            	
                  new File("ArticleDumps/" + entries[i]).delete();
           
              }*/
			  
			  
			  Server server = new Server(clParser.getPort());
			     // Handler for multiple web apps
			     HandlerCollection handlers = new HandlerCollection();

			     
			     // Creating the first web application context
			     WebAppContext webapp = new WebAppContext();
			     webapp.setResourceBase("src/main/webapp");
			     webapp.setContextPath(clParser.getURL());
			     handlers.addHandler(webapp);

			     // Adding the handlers to the server
			     server.setHandler(handlers);
			     
			     // Starting the Server
			    
			     server.start();
			     System.out.println("Server started at URL: localhost:" + clParser.getPort() + clParser.getURL());
			     
			  
			     server.join();
			   
		  }else {
			  	ArrayBlockingQueue<String> queue = new  ArrayBlockingQueue<String> (clParser.getThreadsF());
			    clParser.validate();
			    new Producer(queue, clParser ).start();
			  
			  	for(int i= 0; i < clParser.getThreadsF(); i++)
			  	{
  			
			  		new Consumer(queue, clParser , "File" + i).start();
			  	}
  		
  	  }
		  
		
	}// end main

}// end class