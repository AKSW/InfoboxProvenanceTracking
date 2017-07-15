package io;

import java.io.File;
import java.util.Stack;

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
