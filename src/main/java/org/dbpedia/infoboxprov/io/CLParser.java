package org.dbpedia.infoboxprov.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;
import java.util.UUID;

import org.dbpedia.infoboxprov.dump.SingleArticle;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Class for parsing the comandline and determine the wanted READVARIANT
 * 
 * @author daniel
 */

public class CLParser extends JCommander {
	
	 private String newDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	
	 @Parameter(names={"-help", "-h"} , description = "Print help information and exit", required = false)
	 private boolean showHelp = false;
	 @Parameter(names={"-name", "-a"} , description = "Name of the Article", required = false)
	 private String singleArticle = null;
	 @Parameter(names={"-later", "-l"} , description = "Last timestamp(Date in yyyy-MM-dd) to extract", required = false)
	 private String later = newDate;
	 @Parameter(names={"-earlier", "-e"} , description = "Earliest timestamp (Date in yyyy-MM-dd) to extract", required = false)
	 private String earlier = "2001-01-02";
	 @Parameter(names="-path" , description = "Path to the dump containing directory", required = false)
	 private String path = null;
	 @Parameter(names={"-language", "-lang"}, description = "Dump Language", required = false)
	 private String language = "en";
	 @Parameter(names={"-threads", "-t"} , description = "Number of threads to run", required = false)
	 private int threads = 1;
	 @Parameter(names={"-threadsF", "-tf"} , description = "Number of parallel processed files", required = false)
	 private int threadsF = 1;
	 @Parameter(names={"-lastchange", "-last"} , description = "Only last change to an existing triple will be saved", required = false)
	 private boolean lastChange = false;
	 @Parameter(names={"-daemon"} , description = "Port for the webinterface", required = false)
	 private int daemon = -1;
	 @Parameter(names={"-config", "-c"} , description = "Path to the configfile", required = false)
	 private String config = "src/main/resources/config.txt";
	 @Parameter(names={"-url", "-u"} , description = "Url of the dbpedia OnlineExtractor", required = false)
	 private String extractionUrl = "http://mappings.dbpedia.org/server/extraction/";
	 
	 private READVARIANT readvariant = READVARIANT.ReadDefault;
	 private TimeFrame timeFrame = null;
	 private TreeSet<Integer> finishedArticles = null;
	 private JCommander jCommander = null;
	 private UUID tempID;
	 private String url = "/provenance";
	 
	 private ArrayList<String> templates = null;
	 private ArrayList<String> predicates = null;
	 
	 /**
	  * Constructor for the Comandlinetool
	  * @param args the arguments from the command line
	  */
	 public CLParser(String[] args) {
		this.tempID = UUID.randomUUID();
		parseConfig(config);
		
		jCommander = new JCommander(this);
		try {
			
			jCommander.parse(args);
			
			if (this.showHelp) {
                help();
                System.exit(0);
            }

		}catch(ParameterException e) {
			
			if(e.getMessage().contains("Was passed main")) {
				
			  System.out.println("Wrong parameter name");
			}
			else {
	
			  System.out.println(e.getMessage());
			}
			
			help();
			System.exit(1);
		}
	 }
	 
	 /**
	  * Constructor for the Webinterface
	  * @param singleArticle path to the singleArticle
	  * @param language extraction language e.q. en, de, ...
	  * @param templates specific templates wich will filtered
	  * @param predicates specific predicates wich will filtered
	  * @param earlierDate first point of the timeframe
	  * @param laterDate second point of the timeframe
	  * @param tracking the tracking mode lastChange or all changes
	  * @param port the portnumber
	  */
	 public CLParser(String singleArticle, 
			 		 String language, 
			 		 String templates, 
			 		 String predicates, 
			 		 String earlierDate,
			 		 String laterDate,
			 		 String tracking,
			 		 int port) {
		 this.singleArticle = singleArticle;
		 this.language = language;
		 this.daemon = port;
		 this.tempID = UUID.randomUUID();
		 this.templates = new ArrayList<String>();
		 this.predicates = new ArrayList<String>();
		 
		 String[] tokens;
		 
		 tokens = templates.split("\\|");
			
		 for(String s:tokens){
					
			this.templates.add(s);
		 }
		 
		 tokens = predicates.split("\\|");
		 
		 for(String s:tokens){
				
				this.predicates.add(s);
		 }
	
		
		if(earlierDate.length() > 0  ) {
			
			
			earlier = earlierDate;
			
		} if(laterDate.length() > 0 ) {
			
			later = laterDate;
			
		}
		
		if(tracking.equalsIgnoreCase("last")) {
			
			lastChange = true;
		}
		
		 System.out.println("Started Article: " + singleArticle);
		 System.out.println("TimeFrame earlier: " + earlier);
		 System.out.println("TimeFrame later: " + later);
		 System.out.println("Last Change: " + lastChange);
	
	 }
	 
	 public CLParser getCLParser(){
		 return this;
	 }
	 
	 public String getPath(){
		 return path;
	 }
	 
	 public String getLanguage(){
		 return language;
	 }
	 
	 public int getThreads(){
		 return threads;
	 }
	 
	 public int getThreadsF(){
		 return threadsF;
	 }
	 
	 public READVARIANT getReadvarian(){
		 return readvariant;
	 }
	 
	 public TreeSet<Integer> getFinishedArticles(){
		 return finishedArticles;
	 }
	 
	 public boolean getVariant(){
		 return lastChange;
	 }
	 
	 public TimeFrame getTimeFrame() {
		 return timeFrame;
	 }
	 
	 public String getSingleArticle() {
		 return singleArticle;
	 }
	 
	 public int getPort() {
		 return daemon;
	 }
	 
	 public UUID getTempID() {
		 return tempID;
	 }
	 
	 public ArrayList<String> getTamplates(){
		 return templates;
	 }
	 
	 public ArrayList<String> getPredicates(){
		 return predicates;
	 }
	 
	 public String getURL(){
		 
		 return url;
	 }
	 
	 public String getExtractionUrl() {
		 return extractionUrl;
	 }
	 
	 public void validate(){
		 
		 
		 this.timeFrame = new TimeFrame(earlier, later);
		 
		 
		 if(timeFrame.getDateArray() != null){
		
			 readvariant = READVARIANT.ReadTimeFiltered;
			
		 } 
		 
		 try{
			 
			 if(daemon < 0) {
				 if(singleArticle == null && path == null) 
					 throw new ParameterException("Article name or dump path needed");  	 
			 
				 if(singleArticle != null && path != null)
					 throw new ParameterException("Parameter singleArticle doesn't need a path"); 
			 
				 if(threadsF <= 0) {
					 
					 threadsF = 1;
					 System.out.println("Set maxthreadF to 1 in case of single Article");
				 }	 
			 
				 if(singleArticle != null && threads != 1) {
					 
					 threads = 1;
					 threadsF = 1;
					 System.out.println("Set maxthread to 1 in case of single Article");
					 System.out.println("Set maxthreadF to 1 in case of single Article");
					 
				 }
			 
		    }else {
				 
		    	 if(singleArticle == null) { 
		    		 
		    		throw new ParameterException("Article name needed"); 
		    		
		    	 }
		    	
		    	threads = 1;
		    	threadsF = 1;
			 
		    }
			 
			if(singleArticle != null) {
				
				 SingleArticle article;
				 String timestamp;
				 
				 /**
				  *  Replace an old Version of the singleArticle dump
				  */
				 if(new File("ArticleDumps/"+singleArticle+".xml").exists() && daemon < 0) {
					 
					 new File("ArticleDumps/"+singleArticle+".xml").delete();
				 }
				 
				 if(timeFrame.getDateArray() != null){
			
					 timestamp = later + "T00:00:00Z";
					
				 }else {
					 
					 timestamp = "";
				 }
				 
				 /**
				  * Need for checking: postRequest success and update the chuncksize,
				  *	postRequest first chunck don't add the haeder every time
				  * postRequest last chunck add </page> and </mediawiki> at the end
				  * firstrun, check to load the article in one chunck
				  * postlimt, limit the chuncksize
				  */
				  boolean postSucess = false;
				  boolean postBegin = true;
				  boolean postEnd = false;
				  boolean firstrun = true;
				  int 	  postLimit = 1000;
				 
				  /**
				   * load the singleArticle dump
				   */
				  while(true) {
				 
					  article = new SingleArticle(this);
					  if(!article.setPathForArticle(timestamp, postSucess, postBegin, postEnd, firstrun, postLimit) ) {
						 
						  break;
					  }
				
					  postSucess = article.getSuccess();
					  postBegin = article.getBegin();
					  firstrun = article.getFirtsrun();
					  postLimit = article.getLimit();
					  
					  article.readPageDefault();
					
					  timestamp = article.getTimestampt();
				
					  if(timestamp == null) {
						
						PrintWriter wr;
						try {
							
							if(daemon >= 0) {
								
								wr = new PrintWriter(new FileWriter(new File("ArticleDumps/"+singleArticle + tempID +".xml"),true));
							
							}else {
							
								wr = new PrintWriter(new FileWriter(new File("ArticleDumps/"+singleArticle+".xml"),true));
							}
						  
							wr.println("</page>");
							wr.println("</mediawiki>");
							wr.close();
			        	  
						 } catch (IOException e) {
		
							System.out.println("CLParser: Writer");
						 }
						
						 new File("ArticleDumps/" + tempID + ".xml").delete();
						 break;
					   }
					  
					   try{
						  
						   Date timestampToDate = new SimpleDateFormat("yyyy-MM-dd")
											.parse(timestamp);
						
						   Date earlierToDate = new SimpleDateFormat("yyyy-MM-dd")
									.	parse(earlier);
							
						   if(timestampToDate.before(earlierToDate)) {
						  
							   PrintWriter wr;
							   try {
								
								   if(daemon >= 0) {
									
									   wr = new PrintWriter(new FileWriter(new File("ArticleDumps/"+singleArticle + tempID +".xml"),true));
							
								   }else {
							
									   wr = new PrintWriter(new FileWriter(new File("ArticleDumps/"+singleArticle+".xml"),true));
								   }
							 
								wr.println("</page>");
								wr.println("</mediawiki>");
								wr.close();
				        	  
							   }catch (IOException e) {
			
								   System.out.println("CLParser: Writer");
							   }
						
							   new File("ArticleDumps/" + tempID + ".xml").delete();
					
							   break;
						   	}
					  
					  } catch (ParseException e1) {
							System.out.println("CLParser: Date parse Exception");
					  }
					  
					  
				  }// end while
				  
				  if(daemon >= 0) {
					
					  
					  path = "ArticleDumps/"+singleArticle + tempID + ".xml";
				  
				  }else {
					  
					  new File("ArticleDumps/tmp.xml").delete();
					  path = "ArticleDumps/"+singleArticle + ".xml";
				  }
			}
			
			 
		 }catch(ParameterException e){
			 System.out.println(e.getMessage());
			 help();
			 System.exit(1);
		 }
		 
		
	    
		 
	}// end validate
	 
	private void parseConfig(String path) {
		
		templates = new ArrayList<String>();
		predicates = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		
			String tmp = null;
			String configLine = null;
			String[] tokens;
			
			while ((tmp = br.readLine()) != null)  {
			 
				if(tmp.charAt(0) == '#') {
					continue;
				}
				
				tokens = tmp.split("=");
				configLine = tokens[0];
				tmp = tokens[1];
				tokens = tmp.split("\\|");
				
				for(String s:tokens){
						
						if(configLine.contains("TemplateFilter")) {
						
							templates.add(s);
					
						}else if(configLine.contains("PredicateFilter")) {
							
							predicates.add(s);
							
						}else if (configLine.equalsIgnoreCase("Url")) {
							
							url = tmp;
							
						}else if(configLine.contains("Port")) {
							
							daemon = Integer.parseInt(tmp);
							
						}else if(configLine.contains("ExtractionUrl")) {
							
							extractionUrl = tmp;
						
						}
				}
					
			}
			
			
			br.close();
			
		  }catch (FileNotFoundException e) {
			
			  System.out.println("Configfile not found!");

		  }catch (ArrayIndexOutOfBoundsException | IOException e) {
			  
			System.out.println("Couldn't read the config file");
			System.exit(0);
		  }
	}
	 
	 
	 public void help() {
		 
		 System.out.println();
		 jCommander.usage();
	 }
	 
	 
	 
	 
	 
}