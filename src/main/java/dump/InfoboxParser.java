
package dump;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for reading String and checking the availability of infoboxes
 *
 */
public class InfoboxParser {

  /**
   * the infobox text
   */
  

  ArrayList<String> boxes = null;
  ArrayList<String> multiLineBoxes = null;
  /**
   * constructor creates a new InfoboxParser object and keeps only the infobox
   * text
   *
   * @param input the complete revision text
   */
  public InfoboxParser(String input) {
	  this.boxes = new ArrayList<>();
	  this.multiLineBoxes = new ArrayList<>();
	  
    if (input != null) {
      // escape backslash and dollar sign in input
      input = input.replace("\\", "\\\\");
      input = input.replace("$", "\\$");
      
      
      findSingleLineBoxes(input);

      input = removeSingleLineBraces(input);
      findMultiLineBoxes(input);
    }
  }
  
  private void findSingleLineBoxes(String input) {
	  String tmp = "";  
	  
	    try (InputStream searchPatternStream = getClass().getResourceAsStream(
	      "/singleLineBoxes" + ".txt");
	      Scanner in = new Scanner(searchPatternStream, "UTF-8")) {

	      while(in.hasNextLine()) {
	    	  Pattern pattern = Pattern.compile("(?s)\\{\\{"+ in.nextLine() + ".*?}}");
	    	  Matcher matcher = pattern.matcher(input);
	    	  if (matcher.find()) {
	    		  tmp = tmp + input.substring(matcher.start(), matcher.end());
	    		  boxes.add(input.substring(matcher.start(), matcher.end()));
	    		 
	    	    }
	    	   
	    	  
	      }
	   
	    	
	    }
	    catch (IOException e) {
	   /*   Log.error(e, "The file which specifies additional boxes"
	        + "other than the Infobox could not be read!");*/
	    	System.out.println("Fehler InfoboxParser");
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

  /**
   * Second step: Searching all multi lined expressions of the form "{######}"
   * to find the infobox saves the infobox text
   *
   * @param input the whole text of the revision
   */
  private void findMultiLineBoxes(String input) {

	String tmp = "";  
	  
    try (InputStream searchPatternStream = getClass().getResourceAsStream(
      "/multiLineboxes" + ".txt");
      Scanner in = new Scanner(searchPatternStream, "UTF-8")) {

    	while(in.hasNextLine()) {
	    	  Pattern pattern = Pattern.compile("(?s)\\{\\{"+ in.nextLine() + ".*?}}");
	    	  Matcher matcher = pattern.matcher(input);
	    	  if (matcher.find()) {
	    		  tmp = tmp + input.substring(matcher.start(), matcher.end());
	    		  boxes.add(input.substring(matcher.start(), matcher.end()));
	    		 
	    	    }
	    	   
	    	  
	      }
     
    	
    }
    catch (IOException e) {
   /*   Log.error(e, "The file which specifies additional boxes"
        + "other than the Infobox could not be read!");*/
    	System.out.println("Fehler InfoboxParser");
    }

   
  }
  
  
  
  
 
  
 
  public ArrayList<String> getBoxes() {
	  
		  
	   return boxes;
	   
	  
  }

  
}
