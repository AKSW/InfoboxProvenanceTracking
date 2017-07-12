package io;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.text.SimpleDateFormat;


/**
 * The command line input parser component for the main project of dbp17.
 * It parses the input using Apache Commons CLI.
 */
public class CLIParser {
  private String[] args;
  private Options options = new Options();
  private CommandLine cmd;
  public static final String UNTIL = "until";

  /**
   * @param args arguments given to the program
   */
  public CLIParser(String[] args) {
    this.args = args;

    this.options.addOption("path", true, "Path to the dump containing " +
            "directory");
    this.options.addOption("lang", true, "Dump Language as \"de\" or \"en\"");
    this.options.addOption("a", true, "Article to provenance");
    this.options.addOption("from", true, "Earliest Timestamp(Date in " +
            "yyyy-MM-dd) to extract");
    this.options.addOption(UNTIL, true, "Last Timestamp (Date in " +
            "yyyy-MM-dd) " +
            "to extract");
    this.options.addOption("help", false, "Show help");
    this.options.addOption("threads", true, "Number of threads to run");
    this.options.addOption("rerun", false, "Rerun program after a crash using" +
            " the log");
    this.options.addOption("lastchange", false, "Only last change to an existing" +
            "triple will be saved");
  }

  /**
   * The given arguments are parsed, formats and existence of the given
   * arguments get checked
   */
  public void parse() {
    DefaultParser parser = new DefaultParser();

    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption("help")) {
        help();
      }
      if (!cmd.hasOption("path")) {
        System.out.println("The program needs a path to work properly.");
        help();
      }
      if (!cmd.hasOption("threads")) {
    	  System.out.println("the program needs a valid value of threads to "
    	  		+ "work properly");
    	  help();
      }
      if (cmd.hasOption("lang")) {
        if (!(cmd.getOptionValue("lang").equals("de") || cmd.getOptionValue
                ("lang").equals("en"))) {
          System.out.println("The language tag has to be in the form of " +
                  "\"en\" or \"de\"");
          help();
        }
      }
      try {
        if (cmd.hasOption("from")) {
          new SimpleDateFormat("yyyy-MM-dd").parse(cmd.getOptionValue("from"));
        }
        if (cmd.hasOption(UNTIL)) {
          new SimpleDateFormat("yyyy-MM-dd").parse(cmd.getOptionValue
                  (UNTIL));
        }
      } catch (java.text.ParseException e) {
        e.printStackTrace();
        System.out.println("The given Dates are not in the correct format!");
        help();
      }
    } catch (ParseException e) {
      e.printStackTrace();
      help();
    }
  }

  /**
   * information gets printed, the program exits
   */
  public void help() {
    HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.printHelp("dbp17", "The Parameters have the following " +
            "functionality Parameter: ", options, "");
    System.exit(1);
  }

  /**
   *
   * @return the successfully parsed arguments as commandline
   */
  public CommandLine getCommandLine() {
    return this.cmd;
  }
}
