package io;

import dump.DumpParser;
import rdf.ProvenanceManager;

import org.apache.commons.cli.CommandLine;
//import org.apache.jena.atlas.logging.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * The main class of the dbp17 project.
 */
public class Main {
	private static Logger logger = Logger.getLogger(ProvenanceManager.class.getName());
	public static String language;
	public static CommandLine parsedArgs;
	public static FileHandler fh;
	public static int maxthreads;
	public static boolean rerun = false;
	public static TreeSet<Integer> finishedArticles;
	public static Date[] extractionTimeFrame;
	public static final String AWAITING_COMPLETION_OF_THREADS =
			"Awaiting completion of threads.";
	public static final String PARAMETER_CONFIGURATION_NOT_IMPLEMENTED_AT_THE_MOMENT =
			"Parameter configuration not implemented at the moment!";
	public static final String COULD_NOT_READ_OR_WRITE_CONTEXT = 
			"Could not read or write context";
	public static final String PARAMETERS_DO_NOT_MAKE_SENSE =
          "Parameters do not make sense!";
	
	
	private static int maxThreads = Runtime.getRuntime().availableProcessors();
	

	/**
	 * Assign constants to the optional parameters, to detect if they are used
	 * or not
	 */
	public enum MENU {
		MENU_FROM(1), MENU_UNTIL(2), MENU_A(4), MENU_RERUN(8), MENU_LASTCHANGE(
				16);

		private int value;

		private MENU(int value) {
			this.value = value;
		}

		/**
		 *
		 * @return the value from Menu_*
		 */
		public int getValue() {
			return value;
		}

	}

	/**
	 * Main method for the dbp17-project
	 *
	 * @param args parameters as specified in the entwurfsbeschreibung
	 */
	public static void main(String[] args) {

		CLIParser cliParser;
		cliParser = new CLIParser(args);

		// the commandlineParser parses the commandline
		cliParser.parse();

		// from now on the given arguments are accessed through parsedArgs
		parsedArgs = cliParser.getCommandLine();

		// defining the variables that will be filled
		// String article = null;
		Date extractFrom = null;
		Date extractUntil = null;

		// the pathProvider gets filled with the path containing the dump files
		fh = new FileHandler(parsedArgs.getOptionValue("path"));
		maxThreads = Integer.parseInt(parsedArgs.getOptionValue("threads"));
		//maxthreads = Integer.parseInt(parsedArgs.getOptionValue("threads"));
		// defining the language, activating the default case "en"
		language = "en";

		/*
		 * set up an empty integer (...0000)
		 *
		 */
		int menu = 0;

		if (parsedArgs.hasOption("from")) {
			menu |= MENU.MENU_FROM.getValue();
		}
		if (parsedArgs.hasOption("until")) {
			menu |= MENU.MENU_UNTIL.getValue();
		}
		if (parsedArgs.hasOption("a")) {
			menu |= MENU.MENU_A.getValue();
		}
		if (parsedArgs.hasOption("rerun")) {
			menu |= MENU.MENU_RERUN.getValue();
		}
		if (parsedArgs.hasOption("lastchange")) {
			menu |= MENU.MENU_LASTCHANGE.getValue();
		}

		/*
		 * after checking the integer, menu looks like
		 *
		 * (....0001) Parameter: from (....0010) Parameter: until (....0011)
		 * Parameter: from and until
		 *
		 * and so on
		 */
		/*
		 * checking the language paramter
		 */
		if (parsedArgs.hasOption("lang")) {
			language = parsedArgs.getOptionValue("lang");
		}

		/*
		 * the cases which have just an 'until' or just a 'from' parameter will
		 * be skipped (to reduce the complexity). eq.:
		 *
		 * (...1101), (...1110), (...1001), ...
		 *
		 */
		// 1, 2, 5, 6, 9, 10, 13, 14, 17, 18, 21, 22, 25, 26, 30, 31
		if ((menu & MENU.MENU_FROM.getValue()) > 0
				&& (menu & MENU.MENU_UNTIL.getValue()) == 0
				|| (menu & MENU.MENU_FROM.getValue()) == 0
						&& (menu & MENU.MENU_UNTIL.getValue()) > 0) {

			System.out.println("No valid timeframe!!");

			/*
			 * in case there is a one at the last position (odd) a timeFrame is
			 * set. eq.:
			 *
			 * (...0001), (...0101), (...1001), (...1101)
			 */
			// 3, 7, 11, 15, 19, 23, 27, 31
		} else if ((menu & 1) > 0) {

			try {
				extractFrom = new SimpleDateFormat("yyyy-MM-dd")
						.parse(parsedArgs.getOptionValue("from"));

				extractUntil = new SimpleDateFormat("yyyy-MM-dd")
						.parse(parsedArgs.getOptionValue("until"));

			} catch (java.text.ParseException e) {
				//Log.error(e, "Could not parse text!");
				logger.log(Level.SEVERE,"Could not parse text!" , e);
			}

			extractionTimeFrame = new Date[] { extractFrom, extractUntil };

			// start DumpParser with timeFrame
			switch (menu) {
			// Parameter: timeFrame
			case 3: {
				goProvenancerGo(extractionTimeFrame, true);
				break;
			}
			// Parameter: timeFrame, a (singleArticle)
			case 7: {
				 goProvenancerGo(SingleArticle.getPathForDump(parsedArgs
				 .getOptionValue("a"), language), extractionTimeFrame, true);
				break;
			}
			// Parameter: timeFrame, rerun
			case 11: {
				readLogs("log");
				goProvenancerGo(extractionTimeFrame, finishedArticles, true);
				break;
			}
			default: {
				System.out.println(
						PARAMETERS_DO_NOT_MAKE_SENSE);
			}

			}// end switch

			/*
			 * in case there is a zero at the last position (even) no timeFrame
			 * is set. eq.:
			 *
			 * (...0000), (...0100), (...1001), (...1100)
			 */
			// 0, 4, 8, 12, 16, 20, 24, 28
		} else if ((menu & 1) == 0) {

			// start DumpParser without timeFrame
			switch (menu) {
			// Parameter: disable
			case 0: {
				goProvenancerGo(true);
				break;
			}
			// Parameter: a (singleArticle)
			case 4: {
				goProvenancerGo(
						SingleArticle.getPathForDump(
								parsedArgs.getOptionValue("a"), language),
						true);
				break;
			}
			// Parameter: rerun
			case 8: {
				readLogs("log");
				goProvenancerGo(finishedArticles, true);
				break;
			}
			// Parameter: lastchange
			case 16: {
				goProvenancerGo(false);
				break;
			}
			// Parameter: a (singleArticle), lastChange
			case 20: {
				goProvenancerGo(
						SingleArticle.getPathForDump(
								parsedArgs.getOptionValue("a"), language),
						false);
				break;
			}
			// Parameter: rerun, lastChange
			case 24: {
				readLogs("log");
				goProvenancerGo(finishedArticles, false);
				break;
			}
			default: {
				System.out.println(
						PARAMETERS_DO_NOT_MAKE_SENSE);
			}

			}// end switch

		} // end menu

	}// end main

	/**
	 * Depends on the selected parameter the goProvenancerGo methode will set
	 * up needed classvariables in the class DumpParser, start another
	 * readPage() methode and creates the different streamreader to separate
	 * the DumpFile
	 *
	 * @param variant if false, only the last change of every triple will be
     *        extracted
	 */
	// CASE: default
	public static void goProvenancerGo(boolean variant) {
		 
		
		while (fh.nextFileEntry()) {
			
		ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
		String path = fh.getFileEntry();
		
			for (int i = maxThreads - 1; i>=0; i-- ){
				Runnable worker = new ProvenanceManager("Thread_" + i   ,
														path            , 
														new DumpParser(), 
														i   	 	    ,
														maxThreads		,
														language		, 
														variant,
														READVARIANT.ReadDefault);
				executor.execute(worker);
			
			}
			executor.shutdown();
			
			// wait for threads to finish
			
			/*while (!executor.isTerminated()) {
		        }*/
			
			try {
				while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				}
			} catch (InterruptedException e) {
				//Log.info(e, AWAITING_COMPLETION_OF_THREADS);
				logger.log(Level.INFO, AWAITING_COMPLETION_OF_THREADS , e);
			}
			
			
		}
		
	}// end Case

	// CASE: timeFrame
	/**
	 * Depends on the selected parameter the goProvenancerGo methode will set
	 * up needed classvariables in the class DumpParser, start another
	 * readPage() methode and creates the different streamreader to separate
	 * the DumpFile
	 *
	 * @param extractionTimeFrame timeframe in which to extract revisions
	 * @param variant if false, only the last change of every triple will be
     *              extracted
	 */
	public static void goProvenancerGo(Date[] extractionTimeFrame,
			boolean variant) {
		
		
		while (fh.nextFileEntry()) {
			
		ExecutorService executor = Executors.newFixedThreadPool( maxThreads);
		String path = fh.getFileEntry();
			
			for (int i = maxThreads - 1; i>=0; i-- ){
				Runnable worker = new ProvenanceManager("Thread_" + i					   , 
														path							   , 
														new DumpParser(extractionTimeFrame), 
														i								   ,
														maxThreads						   ,
														language						   , 
														variant							   ,
														READVARIANT.ReadTimeFiltered);
				executor.execute(worker);
			}
			executor.shutdown();
			
			// wait for threads to finish
			try {
					while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				}
			} catch (InterruptedException e) {
					//Log.info(e, AWAITING_COMPLETION_OF_THREADS);
					logger.log(Level.INFO, AWAITING_COMPLETION_OF_THREADS , e);
			}
		}//end while
	}// end Case

	
	
	// CASE: rerun, timeFrame
	/**
	 * Depends on the selected parameter the goProvenancerGo methode will set
	 * up needed classvariables in the class DumpParser, start another
	 * readPage() methode and creates the different streamreader to separate
	 * the DumpFile
	 *
	 * @param extractionTimeFrame timeframe in which to extract revisions
	 * @param finishedArticles contains id's of articles already processed in
	 *                         an earlier run
	 * @param variant if false, only the last change of every triple will be
	 *                extracted
	 */
	public static void goProvenancerGo(Date[] extractionTimeFrame,
			TreeSet<Integer> finishedArticles, boolean variant) {
		
		
		
		while (fh.nextFileEntry()) {
			
		ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
		String path = fh.getFileEntry();
			
			for (int i = maxThreads - 1; i>=0; i-- ){
				Runnable worker = new ProvenanceManager("Thread_" + i					  , 
														path							  , 
														new DumpParser(extractionTimeFrame,
																	   finishedArticles)  , 
														i								  ,
														maxThreads						  ,
														language						  , 
														variant							  ,
														READVARIANT.ReadTimeFrameRerun);
				executor.execute(worker);
			}
			executor.shutdown();
			
			// wait for threads to finish
			try {
					while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				}
			} catch (InterruptedException e) {
					//Log.info(e, AWAITING_COMPLETION_OF_THREADS);
					logger.log(Level.INFO, AWAITING_COMPLETION_OF_THREADS , e);
			}
		}//end while
	}// end Case

	// CASE: SingleArticle
	/**
	 * set up needed classvariables in the class DumpParser and start another
	 * readPage() methode with the Parameter singleArticle, creates just one
	 * streamreader
	 *
	 * @param path path to the temporarily downloaded article
	 * @param variant if false, only the last change of every triple will be
   *                extracted
	 */
	public static void goProvenancerGo(String path, boolean variant) {
	
		while (fh.nextFileEntry()) {
			
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
			Runnable worker = new ProvenanceManager("Thread_" + 0		, 
													path				, 
													new DumpParser()    , 
													0					,
													1					,
													language			, 
													variant				,
													READVARIANT.ReadDefault);
			executor.execute(worker);
			executor.shutdown();
			
		// wait for threads to finish
		try {
				while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
			}
		} catch (InterruptedException e) {
				//Log.info(e, AWAITING_COMPLETION_OF_THREADS);
				logger.log(Level.INFO, AWAITING_COMPLETION_OF_THREADS , e);
		}
		}//end while
	}// end Case

	// CASE: rerun
	/**
	 * set up needed classvariables in the class DumpParser and start another
	 * readPage() methode with the Paramter rerun
	 *
	 * @param finishedArticles contains id's of articles already processed in
     *        an earlier run
	 * @param variant if false, only the last change of every triple will be
     *        extracted
	 */
	public static void goProvenancerGo(TreeSet<Integer> finishedArticles,
			boolean variant) {
	
		while (fh.nextFileEntry()) {
			
		ExecutorService executor = Executors.newFixedThreadPool( maxThreads);
		String path = fh.getFileEntry();
			
			for (int i = maxThreads - 1; i>=0; i-- ){
				Runnable worker = new ProvenanceManager("Thread_" + i					  , 
														path							  , 
														new DumpParser(extractionTimeFrame,
																	   finishedArticles)  , 
														i								  ,
														maxThreads						  ,
														language						  , 
														variant							  ,
														READVARIANT.ReadRerun);
				executor.execute(worker);
			}
			executor.shutdown();
			
			// wait for threads to finish
			try {
					while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				}
			} catch (InterruptedException e) {
					//Log.info(e, AWAITING_COMPLETION_OF_THREADS);
					logger.log(Level.INFO, AWAITING_COMPLETION_OF_THREADS , e);
			}
		}//end while
	} // end Case


	/**
	 * set up needed classvariables in the class DumpParser and start another
	 * readPage() methode with the Paramter rerun
	 *
	 * @param path path to the temporarily downloaded article
	 * 
	 * @param extractionTimeFrame timeframe in which to extract revisions
	 * 
	 */

	 //CASE: timeframe, singleArticle
	 public static void goProvenancerGo(String path, Date[]
     extractionTimeFrame, boolean variant) {
		
			
		while (fh.nextFileEntry()) {
				
		ExecutorService executor = Executors.newFixedThreadPool(1);
			
				Runnable worker = new ProvenanceManager("Thread_" + 0					   , 
														path							   , 
														new DumpParser(extractionTimeFrame), 
														0								   ,
														1								   ,
														language						   , 
														variant							   ,
														READVARIANT.ReadTimeFiltered);
				executor.execute(worker);
				executor.shutdown();
				
			// wait for threads to finish
			try {
					while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				}
			} catch (InterruptedException e) {
					//Log.info(e, AWAITING_COMPLETION_OF_THREADS);
				logger.log(Level.INFO, AWAITING_COMPLETION_OF_THREADS , e);
			}
			}//end while
	 }// end Case


	/**
	 * -----------end overloaded goProvenancerGo methods---------------------
	 */
	/**
	 * method to extract the article ids out of log files in /log/
	 *
	 * @param pathToLogs path to the logs created by an earlier run
	 */
	public static void readLogs(String pathToLogs) {
		File logDirectory = new File(pathToLogs);
		finishedArticles = new TreeSet<>();
		String temporaryArticleID;
		for (File logFile : logDirectory.listFiles()) {
			System.out.println(logFile.toString());
			try (BufferedReader br = new BufferedReader(
					new FileReader(logFile))) {
				br.readLine();
				while ((temporaryArticleID = br.readLine()) != null) {
					System.out.println(temporaryArticleID);
					finishedArticles.add(Integer.parseInt(temporaryArticleID));
				}
			} catch (FileNotFoundException e) {
				//Log.error(e, "File not found!");
				logger.log(Level.SEVERE, "File not found!", e);
				continue;
			} catch (IOException e) {
				//Log.error(e, COULD_NOT_READ_OR_WRITE_CONTEXT);
				logger.log(Level.SEVERE, "COULD_NOT_READ_OR_WRITE_CONTEXT", e);
			}
		}
	}// end readLog

}// end class
