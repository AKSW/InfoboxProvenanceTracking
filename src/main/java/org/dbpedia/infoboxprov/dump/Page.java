
package org.dbpedia.infoboxprov.dump;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;

/**
 * Creates Page Object with revison, id, titel
 */

@JacksonXmlRootElement(localName = "page")
public class Page {

  @JacksonXmlProperty(localName = "id")
  private int id;

  @JacksonXmlProperty(localName = "title")
  private String title;

  @JacksonXmlElementWrapper(localName = "revision", useWrapping = false)
  private ArrayList<Revision> revision;

  public ArrayList<Revision> getRevision() {
    return revision;
  }

  public int getId() {
    return this.id;
  }

  public String getTitle() {
    return this.title;
  }

  @Override
  public String toString() {
    String s = "";
    for (int i = 0; i < revision.size(); i++) {
      s = s + revision.get(i).toString();
    }

    return title + " " + " " + id + " " + s;

  }

}
