package rdf;


import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;


import dump.DumpParser;
import io.READVARIANT;


/**
 * controls the flow of the second part of the programm
 */
public class ProvenanceManager implements Runnable {

  /**
   * language of article
   */
  private String language = null;

  /**
   * variant if whole provenance or just the last change of a triple
   */
  private boolean variant;

  private DumpParser parser = null;
  private TripleExtractor tripleExtractor = null;
  //private LogWriter logWriter = null;
  private ProvenanceWriter writer = null;
  private READVARIANT readVariant = null;
  private String threadName = null;
  private static Logger logger = Logger.getLogger(ProvenanceManager.class.getName());
  // ArrayList for differences between two Models
  ArrayList<Statement[]> differences = null;
  ArrayList<Statement[]> filteredDifferences = new ArrayList<Statement[]>();
  // ArrayList for already found differences for the newest Model
  ArrayList<Statement> alreadyFoundDifferences = new ArrayList<Statement>();


  /**
   * @param threadName       Name of the thrrad
   * @param path             Path to the Dumpfile
   * @param equivalenceClass used to separate between the different equivalence classes
   * @param language         language of the article
   * @param variant          if whole provenance or just the last change of a triple
   */

  public ProvenanceManager(String threadName,
                           String path,
                           DumpParser parser,
                           int equivalenceClass,
                           int maxThreads,
                           String language,
                           boolean variant,
                           READVARIANT readVariant) {
	 
    this.language = language;
    this.parser = parser;
    this.parser.setParser(path, equivalenceClass, maxThreads);
    this.variant = variant;
    this.readVariant = readVariant;
    this.threadName = threadName;
    this.filteredDifferences = new ArrayList<Statement[]>();
    this.alreadyFoundDifferences = new ArrayList<Statement>();

    this.tripleExtractor = new TripleExtractor();
    
   // this.logWriter = new LogWriter(threadName);
    this.writer = new ProvenanceWriter(threadName, false);
  }

  @Override
  public void run() {

    // checks which variant is wanted
    // variant default: get complete provenance

    if (this.variant) {
    	
    	// variant lastchange: get just the last change of each triple
    	// switch betwenn the different readvariants
        switch (readVariant) {
          case ReadDefault:
            //ProvenanceWriter.newRun(threadName);
           // LogWriter.newRun(threadName);
            lastChangeProvenanceDefault();
            break;
          case ReadTimeFiltered:
          //  ProvenanceWriter.newRun(threadName);
         //   LogWriter.newRun(threadName);
            lastChangeProvenanceTimeFiltered();
            break;
          case ReadTimeFilteredRerun:
            lastChangeProvenanceTimeFrameRerun();
            break;
          case ReadRerun:
            lastChangeProvenanceRerun();
            break;
          default:

        }
      
    }
    else {
    	
    	 // switch betwenn the different readvariants
        switch (readVariant) {
          case ReadDefault:
            ProvenanceWriter.newRun(threadName);
          //  LogWriter.newRun(threadName);
            wholeProvenanceDefault();
            break;
          case ReadTimeFiltered:
        // ProvenanceWriter.newRun(threadName);
        //    LogWriter.newRun(threadName);
            wholeProvenanceTimeFiltered();
            break;
          case ReadTimeFilteredRerun:
            wholeProvenanceTimeFrameRerun();
            break;
          case ReadRerun:
            wholeProvenanceRerun();
            break;
          default:
        }
    	
    }

  }//end run

  // methode wich catch the wohle provenance
  // and uses the readPageDefault methode from the 
  // DumpParser class
  public void wholeProvenanceDefault() {

    try {
      while (parser.readPageDefault()) {

        if (parser.getPage() != null) {

          wholeProvenance();

        }//end if
      }// end while
    }//end try
    catch (IOException | XMLStreamException e) {
     // log.error(e, "Could not read or write context");
    	logger.log( Level.INFO, "Exception occur", e );
    }
  }//end wholeProvenanceDefault


  // methode wich catch the wohle provenance
  // and uses the readPageTimeFiltered methode from the 
  // DumpParser class
  public void wholeProvenanceTimeFiltered() {

    try {
      while (parser.readPageTimeFiltered()) {

        if (parser.getPage() != null) {

          wholeProvenance();

        }//end if
      }// end while
    }//end try
    catch (IOException | XMLStreamException e) {
    	logger.log( Level.INFO, "Exception occur", e );
    }
  }//end wholeProvenanceTimeFiltered
  
  // methode wich catch the wohle provenance
  // and uses the readPageTimeFrameRerun methode from the 
  // DumpParser class
  public void wholeProvenanceTimeFrameRerun() {

    try {
      while (parser.readTimeFilteredRerun()) {

        if (parser.getPage() != null) {

          wholeProvenance();

        }//end if
      }// end while
    }//end try
    catch (IOException | XMLStreamException e) {
    	logger.log( Level.INFO, "Exception occur", e );
    }
  }//end wholeProvenanceTimeFrameRerun

  // methode wich catch the wohle provenance
  // and uses the readPageRerun methode from the 
  // DumpParser class
  public void wholeProvenanceRerun() {

    try {
      while (parser.readPageRerun()) {

        if (parser.getPage() != null) {

          wholeProvenance();

        }//end if
      }// end while
    }//end try
    catch (IOException | XMLStreamException e) {
    	logger.log( Level.INFO, "Exception occur", e );
    }
  }//end wholeProvenanceRerun

  // methode wich catch the last change provenance
  // and uses the readPageDefault methode from the 
  // DumpParser class
  public void lastChangeProvenanceDefault() {
    try {
      while (parser.readPageDefault()) {
        if (parser.getPage() != null) {

          lastChangeProvenance();
        }//end if
      }// end while

    }//end try
    catch (IOException | XMLStreamException e) {
    	logger.log( Level.INFO, "Exception occur", e );
    }
  }//end lastChangeProvenance

  // methode wich catch the last change provenance
  // and uses the readPageTimeFiltered methode from
  // the DumpParser class
  public void lastChangeProvenanceTimeFiltered() {

    try {
      while (parser.readPageTimeFiltered()) {

        if (parser.getPage() != null) {

          lastChangeProvenance();

        }//end if
      }// end while
    }//end try
    catch (IOException | XMLStreamException e) {
    	logger.log( Level.INFO, "Exception occur", e );
    }
  }//end lastChangeProvenanceTimeFiltered

  // methode wich catch the last change provenance
  // and uses the readPageTimeFrameRerun methode from
  // the DumpParser class
  public void lastChangeProvenanceTimeFrameRerun() {

    try {
      while (parser.readTimeFilteredRerun()) {

        if (parser.getPage() != null) {

          lastChangeProvenance();

        }//end if
      }// end while
    }//end try
    catch (IOException | XMLStreamException e) {
    	logger.log( Level.INFO, "Exception occur", e );
    }
  }//end lastChangeProvenanceTimeFrameRerun

  // methode wich catch the last change provenance
  // and uses the readPageRerun methode from
  // the DumpParser class
  public void lastChangeProvenanceRerun() {

    try {
      while (parser.readPageRerun()) {

        if (parser.getPage() != null) {

          lastChangeProvenance();

        }//end if
      }// end while
    }//end try
    catch (IOException | XMLStreamException e) {
      logger.log( Level.INFO, "Exception occur", e );
    }
  }//end lastChangeProvenanceRerun


  /**
   * (variant == true) : goes through the ArrayList of revisions, creates a
   * Model for each Revision with the TripleExtractor, gets the differences of
   * two consecutive revisions and writes them in a file;
   */
  public void wholeProvenance() {
	  
	  	Model  newestModel = tripleExtractor.generateModel(parser.getPage().
	          getRevision().get(parser.getPage().getRevision().size()-1).getId(),
	      this.language);
	  
	
	  
	for (int i = parser.getPage().getRevision().size()-2; i >= 0; i-- ) {
		
		Model compareModel = tripleExtractor.generateModel(parser.getPage().
              getRevision().get(i).getId(), this.language);
		  
		RDFDiffer rdfDiffer = new RDFDiffer(newestModel,compareModel);
	    
		
		rdfDiffer.determineLeftRightDifferences();
		
		for (Statement[] stmt : rdfDiffer.getNewTripleOldTriple()) {
		
			if (stmt[1] == null) {	
			
				writer.writeAdding(stmt, parser.getPage().getRevision().get(i+1));
		
			}else if (stmt[0] == null){
			
				writer.writeDeleting(stmt, parser.getPage().getRevision().get(i+1));
			
			
			}else {
			
				writer.writeDifferences(stmt, parser.getPage().getRevision().get(i+1));
			}
		}
		newestModel = rdfDiffer.getNewModel();
		
		
	}
	   
	writer.writeModel(newestModel,  parser.getPage().getRevision().get(0));
	System.out.println("Finished article: " + parser.getPage().getTitle());
	  
  }


  /**
   * creates a Model for the first and newest revision, then it creates a Model
   * for the each other revisions in the ArrayList. If differences are found,
   * they are saved in differences and are filtered, so the newer Model does not
   * have a null Statement (we do not want those) and the Statement of the newest
   * revision is
   * also saved in alreadyFoundDifferences. For the next revision to compare
   * with, it saves the differences too, but then looks, if there is already a
   * found difference in alreadyFoundDifferences, using method filterStatements().
   * If yes, then it does'nt write
   * the difference again, since we just want the last change and no further;
   */
  public void lastChangeProvenance() {

	  
	Model newestModel = tripleExtractor.generateModel(parser.getPage().
	          getRevision().get(parser.getPage().getRevision().size()-1).getId(),
	      this.language);
	  
	
	  
	for (int i = parser.getPage().getRevision().size()-2; i >= 0; i-- ) {
		//System.out.println(parser.getPage().getRevision().get(i).getId() +"---" +i);
		Model compareModel = tripleExtractor.generateModel(parser.getPage().
              getRevision().get(i).getId(), this.language);
		  
		RDFDiffer rdfDiffer = new RDFDiffer(newestModel,compareModel);
		rdfDiffer.determineLeftDifferences();
		
		for (Statement[] stmt : rdfDiffer.getNewTripleOldTriple()) {
		
			if (stmt[1] == null) {
				
				writer.writeAdding(stmt, parser.getPage().getRevision().get(i+1));
				
			}else {
			
				writer.writeDifferences(stmt, parser.getPage().getRevision().get(i+1));
			}
		}
		
		newestModel = rdfDiffer.getNewModel();
		if(newestModel.isEmpty()) {
			 break;
		 }
	 }
	  
	 writer.writeModel(newestModel,  parser.getPage().getRevision().get(0));
	 System.out.println("Finished article: " + parser.getPage().getTitle());
	   
   }


 
}
