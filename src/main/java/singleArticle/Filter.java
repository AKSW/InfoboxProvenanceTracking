package singleArticle;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;




class Filter implements StreamFilter {
	

  public boolean accept(XMLStreamReader reader) {
	  
    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
    	
      if (reader.getLocalName().equalsIgnoreCase("revisions") ) {
    	  
    	 return true;
    	
      }

    }
    
    return false;
  }


}