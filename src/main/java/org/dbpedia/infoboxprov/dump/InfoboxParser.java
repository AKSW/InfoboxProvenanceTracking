
package org.dbpedia.infoboxprov.dump;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





/**
 * Class for reading String and checking the availability of infoboxes
 *
 * @author daniel
 */






public class InfoboxParser {

  ArrayList<String> templates = null;
  ArrayList<String> foundTemplates = null;
 
  
  public InfoboxParser(String input,  ArrayList<String> templates) {
	  this.foundTemplates = new ArrayList<>();
	  this.templates = templates;
	  
    if (input != null) {
      // escape backslash and dollar sign in input
      input = input.replace("\\", "\\\\");
      input = input.replace("$", "\\$");
      input = input.replaceAll("&(?!amp;)", "&amp;");
      input = input.replaceAll("<", "");
      input = input.replaceAll(">", ""); 
				
    		  
      //findSingleLineBoxes(input);

      //input = removeSingleLineBraces(input);
      findMultiLineBoxes(input);
    }
  }
  
  private void findSingleLineBoxes(String input) {
	  String tmp = "";  
	  
	    	  Pattern pattern = Pattern.compile("\\{\\{.*?}}");
	    	  Matcher matcher = pattern.matcher(input);
	    	  if (matcher.find()) {
	    		  tmp = tmp + input.substring(matcher.start(), matcher.end());
	    		  foundTemplates.add(input.substring(matcher.start(), matcher.end()));
	    		 
	    	    }
  }
  
  

  /**
   * First step: Replacing all single lined expressions of the form "{{####}}"
   * with "####" to avoid cutting the infobox at the wrong position
   *
   * @param input the whole text of the revision
   * @return text with replaced brace-expressions
   */
  private String removeSingleLineBraces(String input) {
    Pattern pattern = Pattern.compile("\\{\\{.*?}}");
    Matcher matcher = pattern.matcher(input);
    StringBuffer sb = new StringBuffer();

    while (matcher.find()) {
      // remove the two braces at beginning and start of the match
      matcher.appendReplacement(sb, input.substring(matcher.start() + 2,
        matcher.end() - 2));
    }
    // append the rest of the string after the last match
    matcher.appendTail(sb);

    return sb.toString();
  }
  
  private String removeBraces(String input) {
	  
	    Pattern pattern = Pattern.compile("(?s)\\{\\{.*?\\}\\}");
	    Matcher matcher = pattern.matcher(input);
	    int offset = 0;
	    
	    while (matcher.find()) {
	    	
	    	input = input.substring(0, matcher.start()) + 
					   input.substring(matcher.start()+2-offset, matcher.end() - 2 -offset) +
					   input.substring(matcher.end()-offset,input.length());
	    	offset = offset +4;
	    }
	    
	
	
	    return input;
	  }

  /**
   * Second step: Searching all multi lined expressions of the form "{######}"
   * to find the infobox saves the infobox text
   *
   * @param input the whole text of the revision
   */
  private void findMultiLineBoxes(String input) {

	String tmp = "";  
	  
	if(templates == null) {
		
		findSingleLineBoxes(input);
	    input = removeSingleLineBraces(input);
		
		String[] in = {""};
		for (String string : in)
		{
	    	  Pattern pattern = Pattern.compile("(?s)\\{\\{"+ string + ".*?}}");
	    	  Matcher matcher = pattern.matcher(input);
	    	  while (matcher.find()) {
	    		  tmp = tmp + input.substring(matcher.start(), matcher.end());
	    		  
	    		  foundTemplates.add(input.substring(matcher.start(), matcher.end()));
	    		 
	    	    }
	    	   
	    	  
	      }
		
		  
		return;
	}
	
	
	
	for(int i = 0; i < templates.size(); i++) {
		 String patt = templates.get(i);
		 
		  int index1 =0;
 		 
		  
		  if(input.contains("{{" + patt )) {
			  index1 =  input.indexOf(patt);
			
			  tmp = removeSingleLineBraces(input.substring(index1, input.length()));
			  tmp = removeSingleLineBraces(tmp);
			  tmp = removeBraces(tmp);
			  
			
			  
			  Pattern pattern = Pattern.compile("(?s)"+ patt + ".*?}}");
	    	  Matcher matcher = pattern.matcher(tmp);
			  
	    	  if (matcher.find()) {
	    		
	    		  tmp = "{{" + tmp + tmp.substring(matcher.start(), matcher.end());

	    		  foundTemplates.add(tmp.substring(matcher.start(), matcher.end()));
	    		 
	    	    }
	    	  
		  }
		 
	}
	
   /* try (InputStream searchPatternStream = getClass().getResourceAsStream(
      "/templates" + ".txt");
      Scanner in = new Scanner(searchPatternStream, "UTF-8")) {

    	while(in.hasNextLine()) {
    		  String patt = in.nextLine();
    		 
    		
    		  
    		
    	
    		  int index1 =0;
    		 
    		  
    		  if(input.contains("{{" + patt )) {
    			  index1 =  input.indexOf(patt);
    			
    			  tmp = removeSingleLineBraces(input.substring(index1, input.length()));
    			  tmp = removeSingleLineBraces(tmp);
    			  tmp = removeBraces(tmp);
    			  
    			
    			  
    			  Pattern pattern = Pattern.compile("(?s)"+ patt + ".*?}}");
    	    	  Matcher matcher = pattern.matcher(tmp);
    			  
    	    	  if (matcher.find()) {
    	    		
    	    		  tmp = "{{" + tmp + tmp.substring(matcher.start(), matcher.end());
    	    		//  System.out.println(tmp.substring(matcher.start(), matcher.end()));
    	    		  foundTemplates.add(tmp.substring(matcher.start(), matcher.end()));
    	    		 
    	    	    }
    	    	  
    		  }
    	}
    
    }
    catch (IOException e) {
      Log.error(e, "The file which specifies additional boxes"
        + "other than the Infobox could not be read!");
    	System.out.println("Fehler InfoboxParser");
    }*/

   
  }
  
  
  
  
 
  
 
  public ArrayList<String> getTemplates() {
	  
		  
	   return foundTemplates;
	   
	  
  }

  
}
