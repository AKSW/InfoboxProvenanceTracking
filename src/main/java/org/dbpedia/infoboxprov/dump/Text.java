package org.dbpedia.infoboxprov.dump;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;


/**
 * Class needed temporary for reading the XML with Jackson and deserialized mapping
 * 
 * @author daniel
 */
public class Text {

  @JacksonXmlProperty(localName = "xml:space", isAttribute = true)
  private String space;

  @JacksonXmlText
  private String text;



  /**
   *
   * @return space 
   */
  public String getSpace() {
    return space;
  }

  /**
   *
   * @return text
   */
  public String getText() {
    return text;
  }

}