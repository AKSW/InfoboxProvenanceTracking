package dump;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class for easy revision handling
 */
public class Revision implements Comparable<Revision> {

  private int id;
  private String author;
  private Date timestamp;
  private String timestampStr;
  private String content;
  
  ArrayList<String> templates = null;
  
  /**
   * @return the id of this revision
   */
  public int getId() {
    return id;
  }

  public ArrayList<String> getTemplates(){
	  
	  return templates;
	  
  }
  
  /**
   * @return the author of this revision
   */
  public String getAuthor() {
    return author;
  }

  /**
   * @return the timestamp of this revision
   */
  public Date getTimestamp() {
    return timestamp;
  }

  public String getTimestampStr() {
	    return timestampStr;
	  }
  
  /**
   * @return the text of this revision
   */
  public String getContent() {
    return content;
  }
  
  

  /**
   * @param id the id of this new revision
   * @param author the author of this new revision
   * @param timestamp the timestamp of this new revision
   */
  public Revision(int id, String author, Date timestamp) {
    this.id = id;
    this.author = author;
    this.timestamp = timestamp;
  }

  /**
   * @param id the id of this new revision
   * @param author the author of this new revision
   * @param timestamp the timestamp of this new revision as string
   * @throws ParseException Standard ParseException
   */
  public Revision(int id, String author, String timestamp)
    throws ParseException {
    this.id = id;
    this.author = author;
    this.timestampStr = timestamp;
    // used to work around the problem with the timezone
    this.timestamp = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'")
      .parse(timestamp);
  }

  /**
   *
   * @param id the id of this new revision
   * @param timestamp the timestamp of this new revision as string
   * @param contributor for reading the xml file
   * @param text of the infobox
   * @throws ParseException Standard ParseException
   */
  @JsonCreator
  public Revision(@JacksonXmlProperty(localName = "id") int id,
    @JacksonXmlProperty(localName = "timestamp") String timestamp,
    @JacksonXmlProperty(
      localName = "contributor") Contributor contributor,
    @JacksonXmlProperty(localName = "text") Text text)
    throws ParseException {

    this(id, contributor.getUsername(), timestamp);
    
    InfoboxParser infoboxParser = new InfoboxParser(text.getText());
    
    this.templates = new ArrayList<>();
  
    	if(!infoboxParser.getTemplates().isEmpty()) {
    			
    		for(int i = 0; i<infoboxParser.getTemplates().size(); i++) {
    		 
    			templates.add( infoboxParser.getTemplates().get(i));
    		}	
    	}

  }// end constructor

  /**
   * @return a string representation of this object with tab separation
   */
  @Override
  public String toString() {
    /*
     * gets the Offset of the Timezone you are in with needs to be added or
     * subtracted to the UTC time and adds it to the timestamp, so when its
     * parsed back to a String, it shows the right Time
     */
    long difference = TimeZone.getDefault().getRawOffset();
    Date tempDate = new Date(this.timestamp.getTime() + difference);
    return this.id + "\t" + this.author + "\t" + tempDate.toInstant();
  }

  /**
   * @param other the revision to compare with
   * @return a random number between 2 and -2
   */
  @Override
  public int compareTo(Revision other) {
    return other.getTimestamp().compareTo(this.timestamp);
  }

  /**
   * automatically generated; to decide, if two Revisions are equal
   * <strong>ignoring the infobox text</strong>
   * @param o Object
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Revision revision = (Revision) o;

    if (id != revision.id) {
      return false;
    }
    if (author != null ? !author.equals(revision.author)
      : revision.author != null) {
      return false;
    }
    return timestamp != null ? timestamp.equals(revision.timestamp)
      : revision.timestamp == null;
  }

  /**
   * automatically generated; to decide, if two Revisions are equal
   * <strong>ignoring the infobox text</strong>
   */
  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (author != null ? author.hashCode() : 0);
    result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
    return result;
  }
}
