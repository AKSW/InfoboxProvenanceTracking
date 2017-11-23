package singleArticle;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;



public class Rev implements Comparable<Rev>{
	
	ArrayList<String> templates = null;
	
	@JacksonXmlProperty(localName = "revid", isAttribute = true)
	private int revid;

	@JacksonXmlProperty(localName = "parentid", isAttribute = true)
	private int parentid;
	
	@JacksonXmlProperty(localName = "user", isAttribute = true)
	private String user;
	
	@JacksonXmlProperty(localName = "timestamp", isAttribute = true)
	private String timestamp;
	
	@JacksonXmlProperty(localName = "contentformat", isAttribute = true)
	private String contentformat;
	
	@JacksonXmlProperty(localName = "contentmodel", isAttribute = true)
	private String contentmodel;
	
	@JacksonXmlProperty(localName = "space", isAttribute = true, namespace = "xml")
	private String space;
	
	
	@JacksonXmlText private String content;
	
	public String getInfobox() {
		
	    return content;
	}
	
	public void setContent(String content) {
		
		this.content=content;
	}
	
	public void setTemplates(ArrayList<String> templates) {
		
		
		this.templates = templates;
	}
	
	
	public ArrayList<String> getTemplates() {
		
		return this.templates;
	 }
	
	
	
	/**
	   * @return the id of this revision
	   */
	  public int getId() {
	    return revid;
	  }
	  
	  public String getAuthor() {
		    return user;
		  }
	
	  public Date getTimestamp() {
		  
		try {
			return  new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'")
			  .parse(timestamp);
		} catch (ParseException e) {
			return null;
		}
		  
		  
	  }
	  
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
	    Date tempDate = new Date(this.getTimestamp().getTime() + difference);
	    return this.revid + "\t" + this.user + "\t" + tempDate.toInstant();
	  }
	  
	  
	  /**
	   * @param other the revision to compare with
	   * @return a random number between 2 and -2
	   */
	  @Override
	  public int compareTo(Rev other) {
	    return other.getTimestamp().compareTo(getTimestamp());
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

	    Rev rev = (Rev) o;

	    if (revid != rev.revid) {
	      return false;
	    }
	    if (user != null ? !user.equals(rev.user)
	      : rev.user != null) {
	      return false;
	    }
	    return getTimestamp() != null ? getTimestamp().equals(rev.getTimestamp())
	      : rev.getTimestamp() == null;
	  }

	  /**
	   * automatically generated; to decide, if two Revisions are equal
	   * <strong>ignoring the infobox text</strong>
	   */
	  @Override
	  public int hashCode() {
	    int result = revid;
	    result = 31 * result + (user != null ? user.hashCode() : 0);
	    result = 31 * result + (getTimestamp()!= null ? getTimestamp().hashCode() : 0);
	    return result;
	  }
	
}