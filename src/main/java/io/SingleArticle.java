package io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FileUtils;
//import org.apache.jena.atlas.logging.Log;

import rdf.ProvenanceManager;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * class for getting the history of one specific article as a xml dump
 */
public class SingleArticle {
  
  private static Logger logger = Logger.getLogger(ProvenanceManager.class.getName());
  private static FileOutputStream fos;

  /**
   * variable for creating and deleting the articledumps directory
   */
  private static File tempDir = new File("ArticleDumps");

  /**
   * this method downloads the history of a specific article as xml and returns
   * the absolute path to the xml file
   *
   * @param name of the article you want to download
   * @param language of the article you want to download
   * @return path to the downloaded dump file
   */
  public static String getPathForDump(String name, String language) {

    if (language.isEmpty()) {
      language += "en";
    }

    URL url = null;
    try {
      url = new URL("https://" + language
        + ".wikipedia.org/w/index.php?title=Special:Export&pages="
        + name + "&history");
    }
    catch (MalformedURLException e) {
    //  Log.error(e, "Url is malformed!");
      logger.log(Level.SEVERE, "Url is malformed!", e);
    }
    File dump2 = new File("ArticleDUmps");
    tempDir.mkdir();
    File dump = new File("ArticleDumps/" + name + ".xml");
    try (ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {
      fos = new FileOutputStream(dump);
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
      fos.close();
      rbc.close();
    }
    catch (IOException e) {
     // Log.error(e, "Cannot read or write data!");
      logger.log(Level.SEVERE, "Cannot read or write data!", e);
    }
	
    return dump.getAbsoluteFile().getParent();
  }

  /**
   * deleting the directory and all files in it
   */
  public static void delete() {
    try {
      FileUtils.deleteDirectory(tempDir);
    }
    catch (IOException e) {
      //Log.error(e, "Cannot delete files or directory!");
      logger.log(Level.SEVERE, "Cannot delete files or directory!", e);
    }
  }
}
