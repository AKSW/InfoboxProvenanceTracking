package singleArticle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import dump.InfoboxParser;







public class SingleArticleParser {
	
	private static FileOutputStream fos;
	private static File tempDir = new File("ArticleDumps");

	private XMLStreamReader parser;
	private XmlMapper mapper;
	
	private Revisions revisions;
	private String language;
	private String title;
    private int rvlimit;
	private String url;
	
	public SingleArticleParser(String title, String language) {
		this.language = language;
		this.title = title;
		this.url = getPathForArticle();
		this.mapper = new XmlMapper();
		setParser();
		
	}
	
	public SingleArticleParser(String title, String language, int rvstartid, int rvlimit) {
		this.language = language;
		this.title = title;
		this.rvlimit = rvlimit;
		this.url = getPathForArticle(rvstartid,rvlimit);
		this.mapper = new XmlMapper();
		setParser();
		
	}
	
	
	public SingleArticleParser(String title) {
		this.title = title;
		this.url = getPathForArticle();
		this.mapper = new XmlMapper();
		setParser();
	}
	
	public Revisions getRevisions() {
	    return revisions;
	  }
	
	
	public boolean readPageDefault() throws IOException,
    XMLStreamException {
		
		revisions = null;
		

		try {
			revisions = mapper.readValue(parser, Revisions.class);	
			
			for(int i = 0; i<revisions.getRev().size(); i++) {
				
				InfoboxParser infoboxParser = new InfoboxParser( revisions.getRev().get(i).getInfobox());
				
				if(!infoboxParser.getTemplates().isEmpty()) {
				revisions.getRev().get(i).setTemplates(infoboxParser.getTemplates());
				}
				
			revisions.setTitle(title);
			
			}
			
		} catch (java.util.NoSuchElementException e) {
			// if no new page is in the dump
			// Log.error(e, CAN_T_READ_MORE_PAGES);
			
			return false;
		}

		return true;
	}
	
	  /**
	   * this method downloads the history of a specific article as xml and returns
	   * the absolute path to the xml file
	   *
	   * @param name of the article you want to download
	   * @param language of the article you want to download
	   * @return path to the downloaded dump file
	   */
	public  String getPathForArticle() {

	    if (language.isEmpty()) {
	      language += "en";
	    }

	    URL url = null;
	    try {
	    	url = new URL("https://"+language+".wikipedia.org/w/api.php?action=query&prop=revisions&format=xml&rvprop=timestamp|user|ids|content&rvlimit=2&rvstartid=811558671&titles="+title);
	    	Channels.newChannel(url.openStream());
	    	
	    }
	    catch ( IOException e) {
	    //  Log.error(e, "Url is malformed!");
	 
	    }

	    tempDir.mkdir();
	    File dump = new File("ArticleDumps/" + title + ".xml");
	    try (ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {
	      fos = new FileOutputStream(dump);
	      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	      fos.close();
	      rbc.close();
	    }
	    catch (IOException e) {
	     // Log.error(e, "Cannot read or write data!");
	      
	    }
		
	    return dump.getAbsoluteFile().toString();

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
	     
	    }
	}
	
	public  String getPathForArticle(int rvstartid, int rvlimit) {

	    if (language.isEmpty()) {
	      language += "en";
	    }

	    URL url = null;
	    try {
	    
	    if(rvstartid==0) {
	    
	   
	    	url = new URL("https://"+language+".wikipedia.org/w/api.php?action=query&prop=revisions&format=xml&rvprop=timestamp|user|ids|content&rvlimit=max&titles="+title);
	    	}
	    else {
	    	url = new URL("https://"+language+".wikipedia.org/w/api.php?action=query&prop=revisions&format=xml&rvprop=timestamp|user|ids|content&rvlimit="+rvlimit+"&rvstartid="+rvstartid+"&titles="+title);
	    }
	    Channels.newChannel(url.openStream());
	    }
	    catch ( IOException e) {
	    //  Log.error(e, "Url is malformed!");
	 
	    }

	    tempDir.mkdir();
	    File dump = new File("ArticleDumps/" + title + ".xml");
	    try (ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {
	      fos = new FileOutputStream(dump);
	      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	      fos.close();
	      rbc.close();
	    }
	    catch (IOException e) {
	     // Log.error(e, "Cannot read or write data!");
	      
	    }
		
	    return dump.getAbsoluteFile().toString();
	 
	}

	
	  /**
	   * set the parser and therefore the path to the current input file
	   * @param path contains the path to the next dumpfile
	   */
	  public void setParser() {
		  BufferedReader br;
		 
		  try {
			  br = new BufferedReader(
		  				 new InputStreamReader(
		  				 new FileInputStream(url)));
			 
			  
			  
			  
			  this.parser = XMLInputFactory.newInstance()
		              .createXMLStreamReader(br);
		
			  if(rvlimit > 50) {
		      // set up the filter
		      XMLInputFactory.newInstance().createFilteredReader(parser, new Filter()).next();
		      }else {
		    	  
		      XMLInputFactory.newInstance().createFilteredReader(parser, new Filter());
		      }
		    } catch (XMLStreamException | IOException e) {
		    
		      //Log.error(e, "Can't read file.");
		    }
		  }
	  
//public static void main(String[] args) {
//		  
//		  
//		  
//		  SingleArticleParser  parser = new  SingleArticleParser("United_States", "en", 0,50);
//		  
//		  
//		
//		  
//		  try {
//			  
////			  parser.readPageDefault();
////			  for(int i = 0; i<parser.getRevisions().getRev().size(); i++) {
////				  System.out.println(parser.getRevisions().getRev().get(i).getId());
////			  }
////			System.out.println(parser.getRevisions().getRev().size());
//			  System.out.println(parser.getRevisions().getRev().get(parser.getRevisions().getRev().size()-1).getId());
//		  } catch (IOException | XMLStreamException e) {
//			System.out.println(e);
//		}
//		  
//}
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
}