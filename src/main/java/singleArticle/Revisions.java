package singleArticle;


import java.util.ArrayList;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;



@JacksonXmlRootElement(localName = "revisions")
public class Revisions {
	
	 private String title;
	
	@JacksonXmlElementWrapper(localName = "rev", useWrapping = false)
	  private ArrayList<Rev> rev;
	
	public ArrayList<Rev> getRev() {
	    return rev;
	  }
	
	
	 @Override
	  public String toString() {
	    String s = "";
	    for (int i = 0; i < rev.size(); i++) {
	      s = s + rev.get(i).toString();
	    }

	    return title + " "  + s;

	  }
	 
	 public void setTitle(String title) {
		 this.title = title;
	 }
	
}