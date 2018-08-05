
package org.dbpedia.infoboxprov.dump;

import java.util.ArrayList;
import java.util.Stack;
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
  
  public String removeBraces(String input) {
	  
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
   * Second step: Searching all multi lined expressions of the form "{{######}}"
   * to find the infobox saves the infobox text
   *
   *
   *In case the InfoboxParser is constructed with templates unequal to null the search is more specific
   *and cutting the template at the wrong position doesn't happens.
   *
   *In case the InfoboxParser is constructed with templates equal to null. The search goes for all templates of the form {{####}}
   *but sometimes the template will cut at the wrong position. Maybe some unequal revisions will recognized as equal.
   *
   * @param input the whole text of the revision
   */
  private void findMultiLineBoxes(String input) {

	String tmp = "";  
	  
	if(templates == null) {
		
		
		findTemplates(input);
		  
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
	
  }
  

  public ArrayList<String> getTemplates() {
	  
		  
	   return foundTemplates;
	   
	  
  }
  
  private int paranthesisCheck(String input) {
	  
      Stack<Integer> stk = new Stack<Integer>();
      String exp = input;        

      int len = exp.length();
 
      int index = 0;
      
      for (int i = 0; i < len; i++){    

          char ch = exp.charAt(i);

          if (ch == '{')

              stk.push(i);

          else if (ch == '}'){

              try{

                  int p = stk.pop() + 1;

              }

              catch(Exception e){
            	 
                  return  i +2;

              }

          }            

      }
      
      return index;
  }
  
  
 private void findTemplates(String input) {
	  
	  	String tmp = "";
		String input2 = "";
		
		 Pattern pattern = Pattern.compile("(?s)\\{\\{.*?}}");
		 Matcher matcher = pattern.matcher(input);
 	  	 while (matcher.find()) {
 	  		 
 	  
 	  		 if(input.substring(matcher.start() + 2, matcher.end()).contains("{{")) {
 	  		 
 	  			 
 	  		 input2 = input.substring(matcher.start() + 2 , input.length());
 	  		 int endIndex = paranthesisCheck(input2) + matcher.start() + 2;	
 	  		 input2 = input.substring(matcher.start() , endIndex );
 	  	
 	  		 foundTemplates.add(input2);
 	  		 input = input.substring(endIndex, input.length());
 	  		 matcher = pattern.matcher(input);
 				
 	  		 }else {
 	  		 
 	  		 	tmp = tmp + input.substring(matcher.start(), matcher.end());
 	  		 	foundTemplates.add(input.substring(matcher.start(), matcher.end()));
 	  		 }
 		 
 	    }
		
	  
	  
  }
  
  
}
