package io;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.Level;

import org.apache.jena.atlas.logging.Log;


/**
 * provides iterator for the dumps to be processed
 */
public class FileHandler {
 
  private File dumpDirectory = null;
  private Stack<File> dumpDirectoryContents = null;
  private String fileEntry;
 
  /**
   * Constructs the object with the specified path
   *
   * @param pathToFolder path to the folder containing the dumps
   */
  public FileHandler(String pathToFolder) {
	 
	  File file = new File("threadfile");
	  
	  try{
	   if(file.mkdir())
		   throw new IOException();
	  } catch (IOException e) {
			Log.error(Level.SEVERE, "Unable to create \"threadfile\"");
		}
	   
    dumpDirectory = new File(pathToFolder);
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
