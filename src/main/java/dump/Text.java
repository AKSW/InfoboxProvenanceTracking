package dump;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;


/**
 * provides the Text of the infobox
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