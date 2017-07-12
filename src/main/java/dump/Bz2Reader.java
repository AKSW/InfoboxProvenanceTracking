/**
 * Copyright (c) 2017 DBP17
 * All rights reserved
 */
package dump;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Class for reading bzip2 compressed files
 */
public class Bz2Reader extends BufferedReader {

  /**
   * 
   * constructor creates a new bz2 reader
   *
   * @param path - Filepath
   * @throws IOException Standard IOException
   * @throws CompressorException Standard CompressorException
   */
  public Bz2Reader(String path) throws IOException, CompressorException {

    super(new BufferedReader(
      new InputStreamReader(
        new CompressorStreamFactory().createCompressorInputStream(
          new BufferedInputStream(
            new FileInputStream(path))), "UTF-8")));
  }

  //Debugging
  
//    public static void main(String[] args) {
//    
//    	
//    try {
//   
//    Bz2Reader bz2reader = new Bz2Reader("src/test/resources/Bz2Test.bz2"); String line = "";
//   
//    while ((line = bz2reader.readLine()) != null) { System.out.println(line); }
//   
//    bz2reader.close();
//   
//    } catch (IOException | CompressorException e) {	}
//	}
   
}
