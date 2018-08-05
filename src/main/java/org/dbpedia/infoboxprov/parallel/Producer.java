package org.dbpedia.infoboxprov.parallel;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import org.dbpedia.infoboxprov.io.CLParser;
import org.dbpedia.infoboxprov.io.FileHandler;


/**
 * The Producer class collects all the files from the ArticleDump directory and
 * send it to the BlockingQueue
 * 
 * @author daniel
 */

public class Producer extends Thread implements Runnable {

	
	private ArrayBlockingQueue<String> blockingQueue = null;
	private FileHandler fh = null;
	private CLParser clParser = null;
	private File threadFile = null;
	
	public Producer(ArrayBlockingQueue<String> queue, CLParser clParser ) {
		this.blockingQueue = queue; 
		this.clParser = clParser;
	
		
		 threadFile = new File("threadfile");
		 if(!threadFile.exists()) {
			 
			 threadFile.mkdirs();}
		
		if(clParser.getSingleArticle() == null) {
			
		this.fh = new FileHandler(clParser.getPath());
		
		}
	}
  
    @Override
	public void run() {
	
    	try {
    		
    		if(clParser.getSingleArticle()!=null) {
    			blockingQueue.put(clParser.getPath());
    			blockingQueue.put("");
    		}else {
    			
    			while(fh.nextFileEntry()) {
    				
    			
    			blockingQueue.put(fh.getFileEntry());
			}
    		 
    			blockingQueue.put("");
    		}
    	//The catch clause will be forced after all offsets are inside the queue
        } catch (InterruptedException e2) {
        	Thread.currentThread().interrupt();
        }
    	
    	
	}

   
}