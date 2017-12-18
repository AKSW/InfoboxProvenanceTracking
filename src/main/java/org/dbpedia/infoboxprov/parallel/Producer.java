package org.dbpedia.infoboxprov.parallel;

import java.util.concurrent.ArrayBlockingQueue;

import org.dbpedia.infoboxprov.io.CLParser;
import org.dbpedia.infoboxprov.io.FileHandler;




public class Producer extends Thread implements Runnable {

	
	private ArrayBlockingQueue<String> blockingQueue = null;
	private FileHandler fh = null;
	private CLParser clParser;

	public Producer(ArrayBlockingQueue<String> queue, CLParser clParser , String path) {
		this.blockingQueue = queue; 
		this.clParser = clParser;
		
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