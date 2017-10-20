package io;

import java.io.File;
import java.io.IOException;
import java.util.Stack;


/**
 * provides iterator for the dumps to be processed
 */
public class FileHandler {
 
  private File dumpDirectory = null;
  private File threadFile = null;
  private Stack<File> dumpDirectoryContents = null;
  private String fileEntry;
 
  /**
   * Constructs the object with the specified path
   *
   * @param pathToFolder path to the folder containing the dumps
   */
  public FileHandler(String pathToFolder){
	  
	  threadFile = new File("threadfile");
	  
	  try{
		  
	   if(pathToFolder == null)
		   throw new NullPointerException("The dump path is null");
		  
	   dumpDirectory = new File(pathToFolder);
	   
	   if(threadFile.mkdir())
		   throw new IOException("Unable to create \"threadfile\"");
	   
	   if(!dumpDirectory.exists())
			throw new IOException("Unreadable path \"" + pathToFolder + "\"");
	   
	  } catch (IOException | NullPointerException e){
		  
		System.out.println(e.getMessage());	
	
		System.exit(1);
			
	  } 
	 
	 
    dumpDirectoryContents = new Stack<File>();

    for (File singleDumpFile : dumpDirectory.listFiles()) {
    	
      dumpDirectoryContents.push(singleDumpFile);
    }
  }

  
  public String getFileEntry() {
    return fileEntry;
  }

  /**
   * @return next Fileentry
   */
  public boolean nextFileEntry() {

	  if(!dumpDirectoryContents.empty() ){
		  
		  fileEntry = dumpDirectoryContents.pop().getPath();
		  
		  return true;
	  }
	  else {
		  
		  return false;
	  } 
  }
  
}
