package org.dbpedia.infoboxprov.webinterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dbpedia.infoboxprov.io.CLParser;
import org.dbpedia.infoboxprov.parallel.Consumer;
import org.dbpedia.infoboxprov.parallel.Producer;


public class Servlet extends HttpServlet {
	 
	   @Override
	   public void doGet(HttpServletRequest request, HttpServletResponse response)
	               throws IOException, ServletException {
		   
		   
		   
		   String title = request.getParameter("title");
		   String language = request.getParameter("language");
		   String templates = request.getParameter("templates");
		   String predicates = request.getParameter("predicates");
		   String earlierDate = request.getParameter("earlierDate");
		   String laterDate = request.getParameter("laterDate");
		   String tracking = request.getParameter("tracking");
		   
		   CLParser clParser = new CLParser(title, language, templates, predicates, earlierDate, laterDate, tracking ,request.getLocalPort());
		   clParser.validate();
		   ArrayBlockingQueue<String> queue = new  ArrayBlockingQueue<String> (clParser.getThreadsF());
		   
		   new Producer(queue, clParser ).start();
		   Consumer consumer = new Consumer(queue, clParser , "Web");
		   consumer.start();
		
		   /**
		    * wait until the singleArticle is full downloaded from Wikipedia
		    */
		   while(!consumer.getFinished()) {
			   
			   try {
				   
				Thread.sleep(2000);
				
			   } catch (InterruptedException e) {
				   
				   Thread.currentThread().interrupt();
			   }
			   
		   }
		   
		   BufferedReader br = new BufferedReader(new FileReader("threadfile/Web/" + clParser.getTempID() + "0.tsv"));
		   String tmp = "";
		   
		   // Set the response message's MIME type
		   response.setContentType("text/tab-separated-values; charset=utf-8");
		   	
		   // Allocate a output writer to write the response message into the network socket
		   PrintWriter out = response.getWriter();
		 
		   // Write the response message, in an HTML page
		    try {
		    	  
		    	while((tmp = br.readLine()) != null){
		    		out.println(tmp ); 
		    	
		    	  }
		    	   
		     } finally {
		         out.close(); 
		         br.close();
		         
		         /**
		          * delete the temporarily created files. 
		          * Important: The files will saved at harddisk. Maybe the disk runs 
		          * full in case there are to many parallel processed files. 
		          */
		         new File("threadfile/Web/" + clParser.getTempID() + "0.tsv").delete();
		         new File("ArticleDumps/"+ clParser.getSingleArticle() + clParser.getTempID() +".xml").delete();
		         new File("ArticleDumps/" + clParser.getTempID() +".xml").delete();
		    
		     }
		        
	   }
	   
	   
}