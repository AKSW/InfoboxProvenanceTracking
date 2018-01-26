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


public Consumer(ArrayBlockingQueue<String> queue, CLParser clParser, String threadName) {
	
	this.threadName = threadName;
	this.queue = queue;
	this.clParser = clParser;
	new File("threadfile/" + threadName).mkdir(); 
	

}

@Override
public void run() {
	try
	{
	 while(true) {
			String path = queue.take();
			 
			if(path=="" ){
				  queue.put("");
				  break;
				 
			}
			
			
		
			ExecutorService executor = Executors.newFixedThreadPool(clParser.getThreads());
	 
			for (int i = clParser.getThreads() - 1; i>=0; i-- ){
						Runnable worker = new ProvenanceManager(threadName + "/Thread_" + i ,
											path			            					, 
											new DumpParser(clParser.getTimeFrame()
																			 .getTimeFrame(),
														   clParser.getFinishedArticles())  , 
											i	  	 	   									,
											clParser.getThreads()							,
											clParser.getLanguage()							, 
											clParser.getVariant()  							,
											clParser.getReadvarian()						,
											false										     );
						executor.execute(worker);
			}// end for

			executor.shutdown();



			try {
				while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {		 
				}

			} catch (InterruptedException e) {
				Log.info(e, "AWAITING_COMPLETION_OF_THREADS");
			}
	}// end while
	}catch (InterruptedException e) {
		Thread.currentThread().interrupt();
	}
	
		
}// end run


}//end class



