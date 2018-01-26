
package org.dbpedia.infoboxprov.dump;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;


/**
 * The Class Filter is used to skip the Jackson parsed pages and contribute the
 * tasks between the used threads
 * 
 * @author Daniel Pohl
 */
class Filter implements StreamFilter {
	

  private int tmp = 0;
  private int tmp2 = 1;
  
  public Filter(){
	  
  }
  
  public Filter(int tmp, int tmp2){
	  this.tmp = tmp;
	  this.tmp2 = tmp2;
  }

  public boolean accept(XMLStreamReader reader) {
	  
    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
    	
      if (reader.getLocalName().equalsIgnoreCase("page") ) {
    	  
    	 tmp++;
  
    	 if(tmp == tmp2){
    		 tmp = 0; 
    		 return true;
    	  }
    	
      }

    }
    
    return false;
  }


}