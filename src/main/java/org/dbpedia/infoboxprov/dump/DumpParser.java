package org.dbpedia.infoboxprov.dump;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.apache.commons.compress.compressors.CompressorException;
import org.dbpedia.infoboxprov.io.CLParser;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


//
// Class for parsing the DumpFiles using the JacksonFramework
//  
// The fileiterators are oriented on the <page> </page> tags and set up at the
// setParser() methode
//  
// The setParser() methode also set up how many pages will be skipped at every equivalenceclass.
// It means the calls  setParser(path , 0, 3), setParser(path , 1, 3) , setParser(path , 2, 3)
// will lead to the following result.
//  
// The first  iterator reads the pages 0, 3, 6, 9, ...
// The second iterator reads the pages 1, 4, 7, 10, ...
// The third  iterator reads the pages 2, 5, 8, 11, ...
//  
// This equivalenceclasses are determined through the number of used threads
//  
// @author daniel
// 

public class DumpParser {
	
  // timestamp used to determine the offset during processing a singleArticle
	
  private XmlMapper mapper;
  private XMLStreamReader parser;
  private XMLStreamReader filteredParser;
  private Page page;
  private Date[] extractionTimeFrame;
  private BufferedReader reader;
  private ArrayList<String> templates;
  
  /**
   *
   * @param clParser the commandline parser with settings
   */
  public DumpParser(CLParser clParser) {
    this.mapper = new XmlMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
    this.extractionTimeFrame = clParser.getTimeFrame()
			 .getDateArray();
    
    
    if(clParser.getTamplates() == null) {
    
    	this.templates = null;
    	
    } else if(clParser.getTamplates().size() == 0) {
    	
	    this.templates = null;
	   
    }else {
	   
	   this.templates = clParser.getTamplates();
    }
    
    
   
  }
  
  /**
  *
  * @param extractionTimeFrame the time between the revisions will filtered
  * @param templates the templates wich will picked out
  */
  
 public DumpParser(Date[] extractionTimeFrame, ArrayList<String> templates) {
	  
	  this.mapper = new XmlMapper();
	    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
	    this.extractionTimeFrame = extractionTimeFrame;
	   
	    if(templates == null) {
	    	
	    	 this.templates = null;
	    	
	    }else  if(templates.size() == 0) {
	    	
	 	   	 this.templates = null;
	 	   
	    }else {
	 	   
	 	   this.templates = templates;
	    }
  }

  /**
   * @return Page the current Page
   */
  public Page getPage() {
    return page;
  }


  /**
   * opens the file depending on the file extension as text
   * or compressed bz2 archive
   *
   * @param path filepath
   * @throws IOException IOException
   * @throws CompressorException CompressorException
   */
  public void setReader(String path) throws IOException, CompressorException {
 
    // the filename extension
    String extension = "";

    // searching last point
    int i = path.lastIndexOf('.');
    // extension is substring after last point
    if (i >= 0) {
      extension = path.substring(i + 1);
    }

    // looking which extension
    switch (extension) {
      case "xml":
      case "txt":
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)
                , "UTF-8"));
        break;
      case "bz2":
        // calls Bz2Reader
    	
        reader = new Bz2Reader(path);
        
        
        break;
      default:
        // print error message
        System.err.println("The file " + path + " contains unrecognized data / " +
                "unrecognized file extension and is ignored!");
        throw new IOException();
    }

    
  }
  
  /**
   * CASE: default
   * filters revisions so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal
   * @return boolean if new Page (true) or not (false)
   */
  public boolean readPageDefault() {
	
	page = null;
	

	 try {
	    	
	      page = mapper.readValue(parser, Page.class);
	 
	    } catch (java.util.NoSuchElementException | IOException e) {
	     

	      try {
	    	  
			reader.close();
			
		  } catch (IOException e1) {
			  
			System.out.println("Can't close DumpParser reader!");
			
		  }
	     
	      return false;
	    }
    
	
	 
    // reverse order of revisions, so the oldest one ist in index 0
    Collections.sort(page.getRevision(), Collections.reverseOrder());

   
    
    for(int i = 0; i < page.getRevision().size(); i++) {
    	
    	 InfoboxParser infoboxParser = new InfoboxParser(page.getRevision().get(i).getContent(), templates);
   
    	 
    	 if(!infoboxParser.getTemplates().isEmpty()) {
 			
     		for(int j = 0; j<infoboxParser.getTemplates().size(); j++) {
     		 
     			page.getRevision().get(i).getTemplates().add( infoboxParser.getTemplates().get(j));
     		}	
     	}
    	 
    }
    
    
    // filter revisions default
    for (int i = page.getRevision().size()-1; i >= 1; i-- ) {
    	
    	 standardFilter(i);  
    	 
    }
    
   // eventually remove the first revision
   // (which doesn't get filtered by standardFilter())
   if(page.getRevision().get(0).getTemplates().isEmpty()) {
	   page.getRevision().remove(0);
   }
   
    // if no revisions are left, page is irrelevant
   if(!page.getRevision().isEmpty() && page.getRevision().get(0).getTemplates().isEmpty()) {
   	
 	   page.getRevision().remove(0);
    }
    
    // if no revisions are left, page is irrelevant
	if (page.getRevision().isEmpty()) {
		  page = null;
	 }
   
    
    try {
    	
		filteredParser.next();
		
	} catch (XMLStreamException e) {
		
	}
    return true;
  }


  /**
   * CASE: timefiltered
   * filters revisions so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal, and deletes revisions which
   * are not in the timeframe
   * @return boolean if new Page (true) or not (false)
   */
  public boolean readPageTimeFiltered()  {

	page = null;
	  
    try {
    	
      page = mapper.readValue(parser, Page.class);
 
    } catch (java.util.NoSuchElementException | IOException e) {
     

      try {
    	  
		reader.close();
		
	  } catch (IOException e1) {
		  
		System.out.println("Can't close DumpParser reader!");
		
	  }
     
      return false;
    }

    // reverse order of revisions, so the oldest one ist in index 0
    Collections.sort(page.getRevision(), Collections.reverseOrder());

   
    
    for(int i = 0; i < page.getRevision().size(); i++) {
    
    	
    	
     InfoboxParser infoboxParser = new InfoboxParser(page.getRevision().get(i).getContent(), templates);
   	
    	
    
     
   	 if(!infoboxParser.getTemplates().isEmpty()) {
			
    		for(int j = 0; j < infoboxParser.getTemplates().size(); j++) {
    		 
    			page.getRevision().get(i).getTemplates().add( infoboxParser.getTemplates().get(j));
    			
    			 
    		}	
    	}
   	 
    }
    
    //System.out.println(page.getRevision().size());
    
    // filter revisions default
    for (int i = page.getRevision().size()-1; i >= 1; i-- ) {
    	
    	standardFilter(i); 
    	
    	 if(page.getRevision().size() == 1) {
       	  
       	  break;
         }
   	 
    }
    
	// filter revisions time
	for (int i = 0; i < page.getRevision().size(); i++ ) {
    
		 
		i =   dateFilter(i, extractionTimeFrame);
		  
    	 if(page.getRevision().size() == 1) {
          	  
          	  break;
            }
	  	}
    
    // eventually remove the first revision
    // (which doesn't get filtered by standardFilter())
    if(page.getRevision().size() > 1 && page.getRevision().get(0).getTemplates().isEmpty() ) {
    	
    	
    	
 	   page.getRevision().remove(0);
 	   
    }

  
    
    // if no revisions are left, page is irrelevant
    if (page.getRevision().isEmpty()) {
    	
      page = null;
      
    }
   
    try {
    	
		filteredParser.next();
		
	} catch (XMLStreamException e) {
		
	}
    return true;
  }

  

  
  /**
   * set the parser and therefore the path to the current input file
   * @param path contains the path to the next dumpfile
   * @param tmp set the equivalence class
   * @param tmp2 set the equivalence class
   */
  public void setParser(String path, int tmp, int tmp2) {
	    try {
	      setReader(path);
	      this.parser = XMLInputFactory.newInstance()
	              .createXMLStreamReader(reader);
	     // XMLInputFactory.newInstance().createFilteredReader(parser, new Filter());
	      // set up the filter
	      this.filteredParser = XMLInputFactory.newInstance().createFilteredReader(parser, new Filter(tmp, tmp2));
	    } catch (XMLStreamException | IOException | CompressorException e) {
	    
	      //Log.error(e, "Can't read file.");
	    }
	  }
  
  /**
   * removes revisions with
   *    (1) no / an empty infobox
   *    (2) no changes compared to the nearest younger revision
   *
   * @param i index/position of the current revision in the page
   * @return updated index
   */
  private void standardFilter(int i) {
	  
	 // (1)
	if(page.getRevision().get(i).getTemplates().isEmpty()) {
		  
		page.getRevision().remove(i);
	// (2)		
	}else if(page.getRevision().get(i).getTemplates().size() ==  
				 page.getRevision().get(i-1).getTemplates().size())
		
	{
		int tmp = 0;
		for(int j = 0; j<page.getRevision().get(i).getTemplates().size(); j++) {
				
			if(page.getRevision().get(i).getTemplates().get(j)
			   .equalsIgnoreCase(page.getRevision().get(i-1).getTemplates().get(j)  )  )
			tmp ++;
				
		}
			
		if(tmp == page.getRevision().get(i).getTemplates().size()) {
				
			page.getRevision().remove(i);
		}
	}
	  	
  }// end standardFilter

  /**
   * removes revisions which are not in a specified timeframe
   *
   * @param i index/position of the current revision in the page
   * @param extractionTimeFrame start and end date of the relevant revisions
   *                            to keep
   */
  private int  dateFilter(int i, Date[] extractionTimeFrame) {

	 
	  
    // If there is a time restriction revisions out of it will be removed
	  
	  if (page.getRevision().get(i).getTimestamp().after
              (extractionTimeFrame[1])) {
        page.getRevision().remove(i);
        i--;
	  	  
      } else if (page.getRevision().get(i).getTimestamp().before
              (extractionTimeFrame[0])) {
    	 
        page.getRevision().remove(i);
       i--;
      }
    
  return i;
  }// end dateFilter
  
   
   
}// end class
