package dump;

import io.CLParser;
import rdf.ProvenanceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.logging.Logger;


import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.util.ArrayList;
import java.util.List;
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
  private String timestamp;
  private File tempDir ;
  private File dump;
  private static boolean success = false;  
  private static boolean begin = true;
  private static boolean end = false;
  private static int limit = 500;
  private String name = null; 
  private String language = null; 
  private String path;
  
  
  
  
  
  private Page page;
  
  
  public SingleArticle(CLParser clParser) {
	this.name = clParser.getSingleArticle();
	this.language = clParser.getLanguage();
	
	
	if(!new File("ArticleDumps").isDirectory())
	{
		new File("ArticleDumps").mkdirs();
	}
	
  }
  
   
  public String getTimestampt() {
	    return timestamp;
}
  
  public String getPath() {
	  return this.path;
  }
  
  
  public void setPathForArticle(String offset) {
  
 
  if(success) {limit = limit + 50;}
 
	  
  File tmp = null;
  HttpResponse response = null;

  
	    try {
	    	
	    boolean done = true;	
	     
	      do { 
	      CloseableHttpClient client = HttpClients.createDefault();
	      HttpPost post;
	      
	     
	      
	      if(offset.isEmpty()) {
	    	 post = new HttpPost("https://"+language+".wikipedia.org/w/index.php?title=Special:Export&pages="+name+"&dir=desc&limit="+limit+"&action=submit");
	      
	      }else {
	      
	         post = new HttpPost("https://"+language+".wikipedia.org/w/index.php?title=Special:Export&pages="+name+"&dir=desc&limit="+limit+"&offset="+offset+"&action=submit");  
	      } 
          List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
          nameValuePairs.add(new BasicNameValuePair("-d",""));
          nameValuePairs.add(new BasicNameValuePair("-H","'Accept-Encoding: gzip,deflate'"));
          nameValuePairs.add(new BasicNameValuePair("--compressed",""));
          post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
          response = client.execute(post);
           
          
          if(response.getStatusLine().getStatusCode()!=200) {
        	  success = false;
         	 
        	  limit = limit - 200;
        	  
          }else {
        	  success = true;
        	  done = false;
          }
          
          
	      }while(done);  
          
	    
	      tmp = new File("ArticleDumps/tmp.xml");
	     
          ReadableByteChannel rbc = Channels.newChannel(response.getEntity().getContent()); 
          fos = new FileOutputStream(tmp);
          fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
          fos.close();
          rbc.close();
            
          
          BufferedReader br;
          br = new BufferedReader(new InputStreamReader(new FileInputStream("ArticleDumps/tmp.xml")
                  , "UTF-8"));
          dump = new File("ArticleDumps/"+name+".xml");
          List<String> lines = new ArrayList<String>();
          
          String in ="";
          while((in=br.readLine())!=null) {
        	  lines.add(in);
        	 
          }
          
          if(lines.size()<60 && !lines.toString().contains("<revision>")) {
          
          end = true;
          }
        
          if(begin) {
        	  
        	  lines.remove(lines.size()-1);
              lines.remove(lines.size()-1);
        	  begin = false;
        	  
        	  PrintWriter wr = new PrintWriter(new FileWriter(dump,true));
              for (String line : lines)
                  wr.println(line);
              wr.close();
              
          }else if(!end){
        	  
        	  while(true ){
        		
        		  	if(!lines.get(0).contains("<revision>")) {
        		  		lines.remove(0);
        		  	}else {
        		  		break;
        		  	}
        		  
        	  
        		  	
        	  }
        	  
        	  lines.remove(lines.size()-1);
              lines.remove(lines.size()-1);
              
              PrintWriter wr = new PrintWriter(new FileWriter(dump,true));
              for (String line : lines)
                  wr.println(line);
              wr.close();
        	  
          }
          
          br.close();
          
        
      } catch (IOException e) {
    	  System.out.println(e);
      }
  	 
	path = tmp.getAbsoluteFile().toString();
	
	
	
	
  }
  
  
public void readPageDefault(){

		XmlMapper mapper = new XmlMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
		XMLStreamReader parser;
		BufferedReader br;
		
		try {
		
		br = new BufferedReader(new InputStreamReader(new FileInputStream(path)
                , "UTF-8"));
		parser = XMLInputFactory.newInstance()
	              .createXMLStreamReader(br);
		
	     // XMLInputFactory.newInstance().createFilteredReader(parser, new Filter());
	      // set up the filter
	      XMLInputFactory.newInstance().createFilteredReader(parser, new Filter(0, 1));
	      
	     page = null;
		
			page = mapper.readValue(parser, Page.class);
			
			timestamp = page.getRevision().get(page.getRevision().size()-1).getTimestampStr();
			System.out.println(timestamp);
		}catch(com.fasterxml.jackson.databind.exc.InvalidDefinitionException e) {
			
			System.out.println("SingleArticle: InvalidDefinitionException" );
			
		} catch (java.util.NoSuchElementException e) {

			
			
		}catch (XMLStreamException | IOException e ) {
			
			System.out.println(e);
			
		}
		
}
  
  
  
  /**
   * deleting the directory and all files in it
   */
  public void delete() {
    try {
      FileUtils.deleteDirectory(tempDir);
    }
    catch (IOException e) {
      //Log.error(e, "Cannot delete files or directory!");
      logger.log(Level.SEVERE, "Cannot delete files or directory!", e);
    }
  }
  
  
}
