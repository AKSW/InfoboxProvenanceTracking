package parallel;


import javax.xml.stream.XMLStreamReader;
import java.util.concurrent.ArrayBlockingQueue;


import dump.DumpParser;

public class Producer extends Thread implements Runnable {

	
	private ArrayBlockingQueue<Long> blockingQueue = null;
	private DumpParser parser = null;
	private String path = null;
	XMLStreamReader localReader;

	public Producer(ArrayBlockingQueue<Long> queue, String path) {
		this.blockingQueue = queue; 
		this.path = path;
		this.parser = new DumpParser(path);
		
	}
  
    @Override
	public void run() {
	
    	try {
    	
    		while(parser.parseDump(path)) {
    				
//    				try {
//    					blockingQueue.add(parser.getOffset());	
//				
//    				}catch (IllegalStateException e1) {
//    					Thread.sleep(5000);
//    				
//    				}
    			
    			blockingQueue.put(parser.getOffset());
			}
    		 
    		
    			blockingQueue.put((long)0);
    	
    	//The catch clause will be forced after all offsets are inside the queue
        } catch (InterruptedException e2) {
        	Thread.currentThread().interrupt();
        }
	}


}