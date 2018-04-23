package org.dbpedia.infoboxprov.dump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.dbpedia.infoboxprov.io.CLParser;
import org.dbpedia.infoboxprov.io.TimeFrame;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * class for getting the history of one specific article as a xml dump
 * 
 * @author daniel
 */
public class SingleArticle {
  
  final private int MIN = 1;
  final private int MAX = 1000;
	
  private FileOutputStream fos;
  private String timestamp;
  private TimeFrame timeFrame ;
  private File dump;
  private boolean success;  
  private boolean begin;
  private boolean firstRun;
  private int limit;
  private String name = null; 
  private String language = null; 
  private String path = null;
  private Page page;
  private UUID tempID;
  private boolean daemon = false;
  
  public SingleArticle(CLParser clParser) {
	this.name = clParser.getSingleArticle();
	this.language = clParser.getLanguage();
	this.timeFrame = clParser.getTimeFrame();
	this.tempID = clParser.getTempID();
	
	this.begin = false;

	this.success = false;
	this.firstRun = false;
	
	if(clParser.getPort()>=0) {
	
		daemon = true;
	}
	
	if(!new File("ArticleDumps").isDirectory())
	{
		new File("ArticleDumps").mkdir();
	}
	
  }
  
  public SingleArticle(String name, String language) {
	  
	  this.name = name;
	  this.language = language;
	  this.daemon = false;
	  
  }
  
   
  public String getTimestampt() {
	    return timestamp;
}
  
  public String getPath() {
	  return this.path;
  }
  
  public boolean getSuccess() {
	  return this.success;
  }
  
  public boolean getBegin() {
	  return this.begin;
  }
  
  
  
  public boolean getFirtsrun() {
	  return this.firstRun;
  }
  
  public int getLimit() {
	  return this.limit;
  }
  
  public boolean  setPathForArticle(String offset, 
		  							boolean a_success, 
		  							boolean a_begin, 
		  							boolean a_end, 
		  							boolean a_firstrun,
		  							int a_limit) {

  if(firstRun && timeFrame.getTimeFrame() == null) {
	 
  	URL url = null;
  	try {
	      url = new URL("https://" + language
	        + ".wikipedia.org/w/index.php?title=Special:Export&pages="
	        + name + "&history");
	}
	    catch (MalformedURLException e) {
	    	
	    	System.out.println(e);
	 
	}
  	
  	
  	HttpURLConnection connection;
  	int code = 0;
	try {
		
		connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		
		code = connection.getResponseCode();
		
	} catch (IOException e) {
		
	} 
  	
	if(code != 200) {
	
		postRequest(offset, a_success, a_begin, a_end, a_limit);
		firstRun=false;
		return true;
	}
  
  	if(daemon) {
  		dump = new File("ArticleDumps/"+ name + tempID +".xml");
  	}else {
  	
	dump = new File("ArticleDumps/"+ name +".xml");
	}
	    
	try (ReadableByteChannel rbc = Channels.newChannel(url.openStream())) {
	      fos = new FileOutputStream(dump);
	       fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	        fos.close();
	        rbc.close();
	 }catch (IOException e) {
	        System.out.println(e);
	 }

	return false;
	
  }else {
	 
	  postRequest(offset, a_success, a_begin, a_end, a_limit);
	  return true;
  }
	
  }//end setPathForArticle
  
  
  
  public void postRequest(String offset, boolean a_success, boolean a_begin, boolean a_end, int a_limit) {
	 
	  
	  if(a_success) {
		  limit = a_limit + 50;
		  if (limit > MAX) { limit = MAX;}
	  }
	  
	  
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
	         	 
	        	  limit = a_limit - 200;
	        	  if(limit < MIN) {limit = MIN;}
	        	  
	          }else {
	        	 
	        	  success = true;
	        	 
	        	  done = false;
	          }
	          
	          
		      }while(done);  
	          
		    
		      tmp = new File("ArticleDumps/" + tempID + ".xml");
		     
	          ReadableByteChannel rbc = Channels.newChannel(response.getEntity().getContent()); 
	          fos = new FileOutputStream(tmp);
	          fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	          fos.close();
	          rbc.close();
	            
	          
	          BufferedReader br;
	          br = new BufferedReader(new InputStreamReader(new FileInputStream("ArticleDumps/" + tempID + ".xml")
	                  , "UTF-8"));
	          
	          if(daemon) {
	        	  
	        	  dump = new File("ArticleDumps/" + name + tempID + ".xml"); 
	        	  
	          }else {
	        	  
	        	  dump = new File("ArticleDumps/" + name  + ".xml");
	          
	          }
	          
	          List<String> lines = new ArrayList<String>();
	          
	          String in ="";
	          while((in=br.readLine())!=null) {
	        	  lines.add(in);
	        	 
	          }
	          br.close();
	          if(lines.size()<60 && !lines.toString().contains("<revision>")) {
	          
	          a_end = true;
	          
	          
	          
	          }
	        
	          if(a_begin) {
	        	
	        	  lines.remove(lines.size()-1);
	              lines.remove(lines.size()-1);
	        	  begin = false;
	        	  
	        	  PrintWriter wr = new PrintWriter(new FileWriter(dump,true));
	              for (String line : lines)
	                  wr.println(line);
	              wr.close();
	              
	          }else if(!a_end){
	        
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
		
	 
	      // set up the filter
	      XMLInputFactory.newInstance().createFilteredReader(parser, new Filter(0, 1));
	      
	      page = null;
		
		  page = mapper.readValue(parser, Page.class);
			
		  timestamp = page.getRevision().get(page.getRevision().size()-1).getTimestampStr();
		  br.close();
		  
		}catch(com.fasterxml.jackson.databind.exc.InvalidDefinitionException e) {
			
			System.out.println("SingleArticle: InvalidDefinitionException" );
			
		} catch (java.util.NoSuchElementException e) {

			
			
		}catch (XMLStreamException | IOException e ) {
			
			System.out.println(e);
			
		}
		
	}// end readPageDefault
  
  
}
