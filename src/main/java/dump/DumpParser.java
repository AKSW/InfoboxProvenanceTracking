package dump;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.apache.commons.compress.compressors.CompressorException;

import javax.xml.stream.FactoryConfigurationError;
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
import java.util.TreeSet;




public class DumpParser {
  private XmlMapper mapper;
  private  XMLStreamReader parser;
  private XMLStreamReader filteredParser;
  XMLStreamReader localReader;
  private long offset;
  private Page page;

  private TreeSet<Integer> finishedArticles;
  private Date[] extractionTimeFrame;
  public static final String CAN_T_READ_MORE_PAGES = "Can't read more pages";


  ArrayList<XMLStreamReader> reader;
  ArrayList<Long> offsets;
  
  //Constructor for parsing Dump
  public DumpParser(String path) {
	  
	  this.mapper = new XmlMapper();
	    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
      // set up the filter
      
	  try {
		this.parser = XMLInputFactory.newInstance()
			          .createXMLStreamReader(getFile(path,0));
		this.filteredParser = XMLInputFactory.newInstance().createFilteredReader(parser, new Filter());
		} catch (XMLStreamException | FactoryConfigurationError | IOException | CompressorException e) {
			System.out.println("Parsing Initialisation Error!");
		}
	    
	  this.reader = new ArrayList<XMLStreamReader>();
	  this.offsets = new ArrayList<Long>();
  }
  
  //Constructors for mapping XML
  public DumpParser(String path, Long offset) {
	  this.mapper = new XmlMapper();
	    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
	  setParser(path, offset);
  }
  
  /**
   *
   * @param extractionTimeFrame timeframe with from and until
   * @param finishedArticles ArrayList with integers as page id
   */
  public DumpParser(String path, Long offset, Date[] extractionTimeFrame,
                    TreeSet<Integer> finishedArticles) {
    this.mapper = new XmlMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
    setParser(path, offset);
    this.finishedArticles = finishedArticles;
    this.extractionTimeFrame = extractionTimeFrame;
  

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
   * @return Bufferedreader
   * @throws IOException IOException
   * @throws CompressorException CompressorException
   */
  public BufferedReader getFile(String path, long offset) throws IOException, CompressorException {
	    BufferedReader br;

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
	        br = new BufferedReader(new InputStreamReader(new FileInputStream(path)
	                , "UTF-8"));
	        break;
	      case "bz2":
	        // calls Bz2Reader
	        br = new Bz2Reader(path);
	        break;
	      default:
	        // print error message
	        System.err.println("The file " + path + " contains unrecognized data / " +
	                "unrecognized file extension and is ignored!");
	        throw new IOException();
	    }
	    br.skip(offset);
	    return br;
	  }
  
  public boolean parseDump(String path) {
	  
	  
	  offset = filteredParser.getLocation().getCharacterOffset();
	
	  try {
		  
		  filteredParser.next();
	  
	  } catch(XMLStreamException e) {
		  System.out.println("XMLParsing Exception");
		  
	  }catch ( java.util.NoSuchElementException e) {
	    	 return false;
	    }
	  
	  
	 try {
		localReader = XMLInputFactory.newInstance()
		             .createXMLStreamReader(getFile(path,offset));
	  } catch (XMLStreamException | FactoryConfigurationError | IOException | CompressorException e) {
		

		  System.out.println("Can't create the localReader");
	  }
	
	 
	  
	  offsets.add(offset);
	  reader.add(localReader);
	  return true;
  }
  
  public ArrayList<XMLStreamReader>  getReader(){
	  return this.reader;
  }
  
  public Long  getOffset(){
	  return this.offset;
  }
  
  public ArrayList<Long>  getOffsets(){
	  return this.offsets;
  }
  
  
  public XMLStreamReader getLocalReader() {
	  return this.localReader;
  }
  
  /**
   * CASE: default
   * filters revisions so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal
   * @return boolean if new Page (true) or not (false)
   * @throws IOException IOException
   * @throws XMLStreamException XMLStreamException
 * @throws CompressorException 
 * @throws FactoryConfigurationError 
   */
  
  public void mapPageDefault() 
   {
	  page = null;
	  
	  try {
		  page = mapper.readValue(parser, Page.class);
		 
	  } catch (IOException |java.util.NoSuchElementException e) {
		  System.out.println("Mapping Error");
		  // Log.error(e, CAN_T_READ_MORE_PAGES); 
	  } 

	  // reverse order of revisions, so the oldest one ist in index 0
	  Collections.sort(page.getRevision(), Collections.reverseOrder());

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
	  if (page.getRevision().isEmpty()) {
		  page = null;
	  }
	 
   }//end Default
    

  /**
   * CASE: timefiltered
   * filters revisions so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal, and deletes revisions which
   * are not in the timeframe
   * @return boolean if new Page (true) or not (false)
   * @throws IOException IOException
   * @throws XMLStreamException XMLStreamException
   */
  public void mapPageTimeFiltered() {

	  page = null;
	  
	  try {
		  page = mapper.readValue(parser, Page.class);
		 
	  } catch (IOException |java.util.NoSuchElementException e) {
		  System.out.println("Mapping Error");
		  // Log.error(e, CAN_T_READ_MORE_PAGES); 
	  } 

    // reverse order of revisions, so the oldest one ist in index 0
    Collections.sort(page.getRevision(), Collections.reverseOrder());

    // filter revisions default
    for (int i = page.getRevision().size()-1; i >= 1; i-- ) {
   	 
    	standardFilter(i); 
   	 
    }

    // filter revisions time
    for (int i = page.getRevision().size()-1; i >= 0; i-- ) {

      i = dateFilter(i, extractionTimeFrame);

    }


    // eventually remove the first revision
    // (which doesn't get filtered by standardFilter())
    if(page.getRevision().get(0).getTemplates().isEmpty()) {
 	   page.getRevision().remove(0);
    }

    // if no revisions are left, page is irrelevant
    if (page.getRevision().isEmpty()) {
      page = null;
    }
  }//end TimeFiltered

  /**
   * CASE: timefiltered, rerun
   * filters revisions, which are not in the finishedArticles,
   * so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal, and deletes revisions which
   * are not in the timeframe
   * @return boolean if new Page (true) or not (false)
   * @throws IOException IOException
   * @throws XMLStreamException XMLStreamException
   */
  
 
  public void mapTimeFilteredRerun() {

	  page = null;
	  
	  try {
		  page = mapper.readValue(parser, Page.class);
		 
	  } catch (IOException |java.util.NoSuchElementException e) {
		  System.out.println("Mapping Error");
		  // Log.error(e, CAN_T_READ_MORE_PAGES); 
	  } 

   
	  if ( !finishedArticles.contains(page.getId())) {

		  // reverse order of revisions, so the oldest one ist in index 0
		  Collections.sort(page.getRevision(), Collections.reverseOrder());

		  // filter revisions default
		  for (int i = page.getRevision().size()-1; i >= 1; i-- ) {
     	 
			  standardFilter(i);  
      
		  }
      
      // filter revisions time
      for (int i = page.getRevision().size()-1; i >= 0; i-- ) {

        i = dateFilter(i, extractionTimeFrame);

      }

      // eventually remove the first revision
      // (which doesn't get filtered by standardFilter())
      if(page.getRevision().get(0).getTemplates().isEmpty()) {
   	   page.getRevision().remove(0);
      }

      // if no revisions are left, page is irrelevant
      if (page.getRevision().isEmpty()) {
        page = null;
      }
    }
    else {
      page = null;
    }
   
  }//end FilteredRerun

  /**
   * CASE: rerun
   * filters revisions, which are not in the finishedArticles,
   * so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal
   * @return boolean if new Page (true) or not (false)
   * @throws IOException IOException
   * @throws XMLStreamException XMLStreamException
   */
  public void mapPageRerun(){

	  page = null;
	  
	  try {
		  page = mapper.readValue(parser, Page.class);
		 
	  } catch (IOException |java.util.NoSuchElementException e) {
		  System.out.println("Mapping Error");
		  // Log.error(e, CAN_T_READ_MORE_PAGES); 
	  } 

    if (!finishedArticles.contains(page.getId())) {

      // reverse order of revisions, so the oldest one ist in index 0
      Collections.sort(page.getRevision(), Collections.reverseOrder());

      for (int i = page.getRevision().size()-1; i >= 1; i-- ) {
     	
    	  standardFilter(i);  
     	 
      }

      // eventually remove the first revision
      // (which doesn't get filtered by standardFilter())
      if(page.getRevision().get(0).getTemplates().isEmpty()) {
   	   page.getRevision().remove(0);
      }

      
      
      // if no revisions are left, page is irrelevant
      if (page.getRevision().isEmpty()) {
        page = null;
      }
    }
    else {
      page = null;
    }
  }//end Rerun

  
  /**
   * set the parser and therefore the path to the current input file
   * @param path contains the path to the next dumpfile
   */
  public void setParser(String path, long offset ) {
	    try {
	      this.parser = XMLInputFactory.newInstance()
	              .createXMLStreamReader(getFile(path, offset));
	     // XMLInputFactory.newInstance().createFilteredReader(parser, new Filter());
	      // set up the filter
	      //this.filteredParser = XMLInputFactory.newInstance().createFilteredReader(parser, new Filter2());
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
  private int dateFilter(int i, Date[] extractionTimeFrame) {

    // If there is a time restriction revisions out of it will be removed
      
	  if (page.getRevision().get(i).getTimestamp().after
              (extractionTimeFrame[1])) {
        page.getRevision().remove(i);
       
	  	  
      } else if (page.getRevision().get(i).getTimestamp().before
              (extractionTimeFrame[0])) {
        page.getRevision().remove(i);
       
      }
    
    return i;
  }// end dateFilter
  
  
//  public static void main(String[] args) {
//	  
//	  FileHandler fh = new FileHandler("/home/daniel/git/InfoboxProvenanceTracking/src/test/resources/inputde");
//	  fh.nextFileEntry();
//	  String path = fh.getFileEntry();
//	  
////	 System.out.println(path);
////	  
////	  
//	  DumpParser parser = new DumpParser(path);
//	
//		
//	
//	  
//	  
//	  //parser.setParser(path, 242749);
//	  
////	  try {
////	  
////	  while(parser.readPageDefault()) {
////		  
////		  if (parser.getPage() != null) {
////		  
////		  System.out.println(parser.getPage().getTitle());
////		  }
////	  }
////	  }
////	  catch (IOException | XMLStreamException e) {
////		  System.out.println(e);
////	    }
//	  
//	
//	  while (parser.parseDump(path) ) {
//	  
//	  }
//	  
//	  System.out.println(parser.getOffsets().size());
//	  System.out.println(parser.getOffsets().get(0));
//	  System.out.println(parser.getOffsets().get(1));
//	  
//	  DumpParser parser2;
//	  parser2 = new DumpParser(path, parser.getOffsets().get(0));
//	  parser2.mapPageDefault();
//	  
//	  System.out.println(parser2.getPage().getTitle());
//	  
////	  parser2.mapPageDefault();
////	  parser.setParser(path,0,1);
////	  BufferedReader br ;
////	 try {
////		br = parser.getFile(path);
////		
////		br.skip(4471);
////	  
////	// 	br.skip(2477);
////		
////	//	br.skip(3220);
////		
////	//	br.skip(4471-93+1);
////		
////	//	br.skip(242749-3103+1);
////		
////		int count =0;	  
////		String tmp;	 
////	  while ((tmp = br.readLine()) != null) {
////			System.out.println(tmp);
////			count++;
////			if(count>100)break;
////		}
////	  
////	} catch ( IOException | CompressorException e) {
////		// TODO Auto-generated catch block
////		e.printStackTrace();
////	} 
//		
//	
//  
//  
//}
   
}// end class
