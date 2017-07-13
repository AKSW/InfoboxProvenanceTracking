
package dump;


import java.io.IOException;
import java.io.InputStream;
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
  private String text;

  /**
   * constructor creates a new InfoboxParser object and keeps only the infobox
   * text
   *
   * @param input the complete revision text
   */
  public InfoboxParser(String input) {

    if (input != null) {
      // escape backslash and dollar sign in input
      input = input.replace("\\", "\\\\");
      input = input.replace("$", "\\$");

      text = removeSingleLineBraces(input);

      findInfobox(text);
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
  private void findInfobox(String input) {

    StringBuilder regex = new StringBuilder();
    try (InputStream searchPatternStream = getClass().getResourceAsStream(
      "/boxes" + ".txt");
      Scanner in = new Scanner(searchPatternStream, "UTF-8")) {

      if (in.hasNextLine()) {
        regex.append("(?s)(\\{\\{").append(in.nextLine()).
          append(".*?}})");
      }

      while (in.hasNextLine()) {
        regex.append("|(\\{\\{").append(in.nextLine()).
          append(".*?}})");
      }
    }
    catch (IOException e) {
   /*   Log.error(e, "The file which specifies additional boxes"
        + "other than the Infobox could not be read!");*/
    	System.out.println("Fehler InfoboxParser");
    }

    Pattern pattern = Pattern.compile(regex.toString());
    Matcher matcher = pattern.matcher(text);

    if (matcher.find()) {
      text = text.substring(matcher.start(), matcher.end());
    }
    else {
      text = null;
    }
  }

  /**
   * @return the content of the infobox or "", if the text contains no infobox
   */
  @Override
  public String toString() {
    return text;
  }
}
