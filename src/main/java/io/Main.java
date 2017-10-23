package io;


import parallel.*;


import java.util.concurrent.ArrayBlockingQueue;

/**
 * The main class 
 */
public class Main {

	
	/**
	 * Main method for the project
	 *
	 * @param args parameter
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) {
		
	
		CLParser clparser = new CLParser(args);
        clparser.validate();
        FileHandler fh = new FileHandler(clparser.getPath());
        ArrayBlockingQueue<Long> queue = new  ArrayBlockingQueue<Long> (500);
        
    	while (fh.nextFileEntry()) {
    		
    		
    		String path = fh.getFileEntry();
    		
    		Producer producer = new Producer(queue, path);
    		producer.start();
    		for(int i= 0; i < clparser.getThreads(); i++)
    		{
    			
    			new Consumer(queue,
						 				 "Thread_" + (i +1),
						 				 clparser.getLanguage(),
						 				 clparser.getReadvarian(),
						 				 clparser.getTimeFrame().getTimeFrame(),
						 				 clparser.getFinishedArticles(),
						 				 clparser.getVariant(),
						 				 path).start();;
    								
    		}
    		
    		
    		
    		
    	}
		
	}// end main

}// end class