package parallel;

import io.FileHandler;

import java.util.concurrent.ArrayBlockingQueue;




public class Producer extends Thread implements Runnable {

	
	private ArrayBlockingQueue<String> blockingQueue = null;
	private FileHandler fh = null;
;

	public Producer(ArrayBlockingQueue<String> queue, String path) {
		this.blockingQueue = queue; 
	
		this.fh = new FileHandler(path);
		
	}
  
    @Override
	public void run() {
	
    	try {
    	
    		while(fh.nextFileEntry()) {
    				
//    				try {
//    					blockingQueue.add(parser.getOffset());	
//				
//    				}catch (IllegalStateException e1) {
//    					Thread.sleep(5000);
//    				
//    				}
    			
    			blockingQueue.put(fh.getFileEntry());
			}
    		 
    		
    			blockingQueue.put("");
    	
    	//The catch clause will be forced after all offsets are inside the queue
        } catch (InterruptedException e2) {
        	Thread.currentThread().interrupt();
        }
	}


}