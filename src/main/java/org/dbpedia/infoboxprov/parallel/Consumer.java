package org.dbpedia.infoboxprov.parallel;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.jena.atlas.logging.Log;
import org.dbpedia.infoboxprov.dump.DumpParser;
import org.dbpedia.infoboxprov.io.CLParser;
import org.dbpedia.infoboxprov.rdf.ProvenanceManager;


/**
 * The Consumer Class takes the Fileentries out of the BlockingQueue and starts the 
 * ProvenanceManager
 * 
 * @author daniel
 */

public class Consumer extends Thread implements Runnable{

	
	
protected ArrayBlockingQueue<String> queue = null;	
private CLParser clParser;
private String threadName;
private boolean finished;
private String webAppName;

public Consumer(ArrayBlockingQueue<String> queue, CLParser clParser, String threadName) {
	
	this.threadName = threadName;
	this.queue = queue;
	this.clParser = clParser;
	this.finished = false;
	new File("threadfile/" + threadName).mkdir(); 
	
	if(clParser.getPort() >= 0) {
		
		webAppName = "" + clParser.getTempID();
		
	}else {
		
		webAppName = "/Thread_";
	}

}

public boolean getFinished() {
	return finished;
}

/**
 * First level of parallelization:
 * To process more then one file out of the inputde folder.
 * Using a simple blockingqueue.
 */

@Override
public void run() {
	try{
		
	 while(true) {
			String path = queue.take();
			 
			if(path=="" ){
				  queue.put("");
				  break;
				 
			}
			
			/**
			 * Second level of parallelization:
			 * To process one Dump with more then one XMLstreamreader.
			 * See the description inside the DumpParser class.
			 * Using a threadpoolexecutor
			 */
		
			ExecutorService executor = Executors.newFixedThreadPool(clParser.getThreads());
	 
			for (int i = clParser.getThreads() - 1; i>=0; i-- ){
						Runnable worker = new ProvenanceManager(threadName + "/" + webAppName + i ,
											path			            						  , 
											new DumpParser(clParser)							  , 
											i	  	 	   										  ,
											clParser										      ,
											false										     );
						executor.execute(worker);
			}// end for

			executor.shutdown();
			
			/**
			 * Wait one second until the check if a thread is working 
			 * and process the next one in case the pipe is free
			 */
			try {
				
				while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {		 
			}
				
			/**	
			 * Used for the webinterface. It's necessary to wait until the thread is finished
			 */
				
			finished = true;
			
			} catch (InterruptedException e) {
				Log.info(e, "AWAITING_COMPLETION_OF_THREADS");
			}
			
	 }// end while
	
	}catch (InterruptedException e) {
		Thread.currentThread().interrupt();
	}
	
}// end run

}//end class



