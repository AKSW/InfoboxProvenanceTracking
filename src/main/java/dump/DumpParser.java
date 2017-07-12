/**
 * Copyright (c) 2017 DBP17
 * All rights reserved
 */

package dump;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.apache.commons.compress.compressors.CompressorException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.TreeSet;


public class DumpParser {
  private XmlMapper mapper;
  private XMLStreamReader parser;
  private XMLStreamReader filteredParser;
  private Page page;
  private TreeSet<Integer> finishedArticles;
  private Date[] extractionTimeFrame;
  public static final String CAN_T_READ_MORE_PAGES = "Can't read more pages";


  /**
   * default constructor
   */
  public DumpParser() {
    this.mapper = new XmlMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
  }


  /**
   * Case: TimeFrame
   *
   * @param extractionTimeFrame timeframe with from and until
   */
  public DumpParser(Date[] extractionTimeFrame) {
    this.mapper = new XmlMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
    this.extractionTimeFrame = extractionTimeFrame;
  }



  /**
   * Case: rerun
   *
   * @param extractionTimeFrame timeframe with from and until
   * @param finishedArticles ArrayList with integers as page id
   */
  public DumpParser(Date[] extractionTimeFrame,
                    TreeSet<Integer> finishedArticles) {
    this.mapper = new XmlMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(DeserializationFeature.WRAP_EXCEPTIONS);
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
  public BufferedReader getFile(String path) throws IOException, CompressorException {
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

    return br;
  }

  /**
   * CASE: default
   * filters revisions so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal
   * @return boolean if new Page (true) or not (false)
   * @throws IOException IOException
   * @throws XMLStreamException XMLStreamException
   */
  public boolean readPageDefault() throws IOException,
          XMLStreamException {
	
	page = null;
	

    try {
      page = mapper.readValue(parser, Page.class);
    } catch (java.util.NoSuchElementException e) {
      // if no new page is in the dump
     // Log.error(e, CAN_T_READ_MORE_PAGES);
      return false;
    }

    // reverse order of revisions, so the oldest one ist in index 0
    Collections.sort(page.getRevision(), Collections.reverseOrder());

    // filter revisions default
    for (int i = 0; i < page.getRevision().size() - 1; i++) {

      i = standardFilter(i);

    }

    // eventually remove the last revision
    // (which doesn't get filtered by standardFilter())
    if (page.getRevision().get(page.getRevision().size() - 1).getText() == null) {
      page.getRevision().remove(page.getRevision().size() - 1);
    }

    // if no revisions are left, page is irrelevant
    if (page.getRevision().isEmpty()) {
      page = null;
    }
    filteredParser.next();
    return true;
	/*  
    return filteredParser.hasNext();*/
  }


  /**
   * CASE: timefiltered
   * filters revisions so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal, and deletes revisions which
   * are not in the timeframe
   * @return boolean if new Page (true) or not (false)
   * @throws IOException IOException
   * @throws XMLStreamException XMLStreamException
   */
  public boolean readPageTimeFiltered() throws IOException,
          XMLStreamException {

    try {
      page = mapper.readValue(parser, Page.class);
    } catch (java.util.NoSuchElementException e) {
      // if no new page is in the dump
      // Log.error(e, CAN_T_READ_MORE_PAGES);
      return false;
    }

    // reverse order of revisions, so the oldest one ist in index 0
    Collections.sort(page.getRevision(), Collections.reverseOrder());

    // filter revisions default
    for (int i = 0; i < page.getRevision().size() - 1; i++) {

      i = standardFilter(i);

    }

    // filter revisions time
    for (int i = 0; i < page.getRevision().size() - 1; i++) {

      i = dateFilter(i, extractionTimeFrame);

    }


    // eventually remove the last revision
    // (which doesn't get filtered by standardFilter())
    if (!page.getRevision().isEmpty()
            && page.getRevision().get(page.getRevision().size()- 1).getText() == null) {
      page.getRevision().remove(page.getRevision().size() - 1);
    }

    // if no revisions are left, page is irrelevant
    if (page.getRevision().isEmpty()) {
      page = null;
    }

    filteredParser.next();
    return true;
  }

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
  public boolean readPageTimeFrameRerun() throws
          IOException, XMLStreamException {

    try {
      page = mapper.readValue(parser, Page.class);
    } catch (java.util.NoSuchElementException e) {
      // if no new page is in the dump
      // Log.error(e, CAN_T_READ_MORE_PAGES);
      return false;
    }

    if ( !finishedArticles.contains(page.getId())) {

      // reverse order of revisions, so the oldest one ist in index 0
      Collections.sort(page.getRevision(), Collections.reverseOrder());

      // filter revisions default
      for (int i = 0; i < page.getRevision().size() - 1; i++) {

        i = standardFilter(i);

      }

      // filter revisions time
      for (int i = 0; i < page.getRevision().size() - 1; i++) {

        i = dateFilter(i, extractionTimeFrame);

      }

      // eventually remove the last revision
      // (which doesn't get filtered by standardFilter())
      if (page.getRevision().get(page.getRevision().size() - 1).getText() == null) {
        page.getRevision().remove(page.getRevision().size() - 1);
      }

      // if no revisions are left, page is irrelevant
      if (page.getRevision().isEmpty()) {
        page = null;
      }
    }
    else {
      page = null;
    }
    filteredParser.next();
    return true;
  }

  /**
   * CASE: rerun
   * filters revisions, which are not in the finishedArticles,
   * so there are just pages which have infoboxes left and
   * deletes revisions where the infobox is equal
   * @return boolean if new Page (true) or not (false)
   * @throws IOException IOException
   * @throws XMLStreamException XMLStreamException
   */
  public boolean readPageRerun() throws
          IOException, XMLStreamException {

    try {
      page = mapper.readValue(parser, Page.class);
    } catch (java.util.NoSuchElementException e) {
      // if no new page is in the dump
      // Log.error(e, CAN_T_READ_MORE_PAGES);
      return false;
    }

    if (!finishedArticles.contains(page.getId())) {

      // reverse order of revisions, so the oldest one ist in index 0
      Collections.sort(page.getRevision(), Collections.reverseOrder());

      for (int i = 0; i < page.getRevision().size() - 1; i++) {

        i = standardFilter(i);

      }

      // eventually remove the last revision
      // (which doesn't get filtered by standardFilter())
      if (page.getRevision().get(page.getRevision().size() - 1).getText() == null) {
        page.getRevision().remove(page.getRevision().size() - 1);
      }

      // if no revisions are left, page is irrelevant
      if (page.getRevision().isEmpty()) {
        page = null;
      }
    }
    else {
      page = null;
    }
    filteredParser.next();
    return true;
  }

  /**
   * set the parser and therefore the path to the current input file
   * @param path contains the path to the next dumpfile
   */

  public void setParser(String path, int tmp, int tmp2) {
	    try {
	      this.parser = XMLInputFactory.newInstance()
	              .createXMLStreamReader(getFile(path));
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
  private int standardFilter(int i) {
    if (page.getRevision().get(i).getText() == null) {
      // (1)
      page.getRevision().remove(i);
      i--;
    } else if (page.getRevision().get(i).getText()
            .equalsIgnoreCase(page.getRevision().get(i + 1).getText())) {
      // (2)
      page.getRevision().remove(i);
      i--;
    }
    return i;
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
    if (extractionTimeFrame[0] != null || extractionTimeFrame[1] != null) {
      if (page.getRevision().get(i).getTimestamp().before
              (extractionTimeFrame[0])) {
        page.getRevision().remove(i);
        i--;
      } else if (page.getRevision().get(i).getTimestamp().after
              (extractionTimeFrame[1])) {
        page.getRevision().remove(i);
        i--;
      }
    }

    return i;
  }// end dateFilter
  
  
  /*	public static void main(String[] args) throws IOException, XMLStreamException {

	DumpParser parser = new DumpParser();
	parser.setParser("src/test/resources/Bz2Test.bz2", 0, 1);
	
	
	while(parser.readPageDefault())
	{
	 if(parser.getPage()!=null) {
		
	 System.out.println(parser.getPage());
	 }
	}
  		
  	}*/
  
   
}
