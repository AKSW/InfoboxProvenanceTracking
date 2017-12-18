
package dump;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Class needed temporary for reading the XML with Jackson
 */
public class Contributor {

  private String username;

  /**
   * @param username from the creator
   * @param ip from the time of creation
   */
  @JsonCreator
  public Contributor(@JacksonXmlProperty(localName = "username") String username,
    @JacksonXmlProperty(localName = "ip") String ip) {
    if (username != null) {
      this.username = username;
    }
    else if (ip != null) {
      this.username = ip;
    }
    else {
      this.username = "";
    }
  }

  /**
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }
}
