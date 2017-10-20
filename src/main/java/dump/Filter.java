
package dump;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;


/**
 * @author Daniel Pohl
 * @version 1.0 April 2017
 */
class Filter implements StreamFilter {
	

  
  
  public Filter(){
	  
  }
  
  

  public boolean accept(XMLStreamReader reader) {
	  
    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
    	
      if (reader.getLocalName().equalsIgnoreCase("page") ) {
    	  
    	

    		 return true;
    	
    	
      }

    }
    
    return false;
  }


}