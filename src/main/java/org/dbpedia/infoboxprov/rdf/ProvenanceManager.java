package org.dbpedia.infoboxprov.rdf;


import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.dbpedia.infoboxprov.dump.DumpParser;
import org.dbpedia.infoboxprov.io.CLParser;
import org.dbpedia.infoboxprov.io.READVARIANT;


/**
 * The Class ProvenanceManager works in three steps.
 * 
 * 1. Handle the parameter lastChange to get either the wohle provenance of an article or just the last change of a triple
 * 2. Starts the CLParser determined READVARIANT
 * 3. Hand over the RDF Triple to the writer
 * 
 * @author daniel
 */
public class ProvenanceManager implements Runnable {

  
  private String language = null;
  private boolean variant;
  private DumpParser parser = null;
  private TripleExtractor tripleExtractor = null;
  private ProvenanceWriter writer = null;
  private READVARIANT readVariant = null;

  ArrayList<Statement[]> differences = null;
  ArrayList<Statement[]> filteredDifferences = null;
  ArrayList<Statement> alreadyFoundDifferences = null;
 

  /**
   * @param threadName       Name of the thread
   * @param path             Path to the Dumpfile
   * @param equivalenceClass used to separate between the different equivalence classes
   * @param language         language of the article
   * @param variant          if whole provenance or just the last change of a triple
   */

  public ProvenanceManager(String threadName,
                           String path,
                           DumpParser parser,
                           int equivalenceClass,
                           CLParser clParser,
                           boolean isNewFile) {

	this.language = clParser.getLanguage();
    this.variant = clParser.getVariant();
    this.readVariant = clParser.getReadvarian();
    
    this.parser = parser;
    this.parser.setParser(path, equivalenceClass, clParser.getThreadsF());
  
    
   
    this.filteredDifferences = new ArrayList<Statement[]>();
    this.alreadyFoundDifferences = new ArrayList<Statement>();

    this.tripleExtractor = new TripleExtractor(clParser.getExtractionUrl(), clParser.getPredicates());
    
   // this.logWriter = new LogWriter(threadName);
    this.writer = new ProvenanceWriter(threadName, isNewFile);
   
  }
  
  
  

  @Override
  public void run() {
	  // variant lastchange: get just the last change of each triple
	  if (this.variant) {
    	
		// switch between the different READVARIANT
        switch (readVariant) {
          case ReadDefault:
            lastChangeProvenanceDefault();
            break;
          case ReadTimeFiltered:
        	 
            lastChangeProvenanceTimeFiltered();
            break;
          default:

        }
      
    }
    else {
    	
    	// switch between the different READVARIANT
        switch (readVariant) {
          case ReadDefault:
            wholeProvenanceDefault();
            break;
          case ReadTimeFiltered:
            wholeProvenanceTimeFiltered();
            break;
          default:
        }
    	
    }
  }//end run

  
  public DumpParser getDumpParser() {
	  return parser;
  }
  
  
  
  public void wholeProvenanceDefault() {


      while (parser.readPageDefault()) {
    	
    	
        if (parser.getPage() != null) {

          wholeProvenance();

        }//end if
      }// end while
   
  }//end wholeProvenanceDefault


  public void wholeProvenanceTimeFiltered() {

	 
      while (parser.readPageTimeFiltered()) {

        if (parser.getPage() != null) {

          wholeProvenance();

        }//end if
      }// end while
    
  }//end wholeProvenanceTimeFiltered
  
  

  
  public void lastChangeProvenanceDefault() {
  
      while (parser.readPageDefault()) {
        if (parser.getPage() != null) {
        	
          lastChangeProvenance();
        }//end if
      }// end while

   
  }//end lastChangeProvenance

 
  public void lastChangeProvenanceTimeFiltered() {
	  
	  
  
      while (parser.readPageTimeFiltered()) {

        if (parser.getPage() != null) {

          lastChangeProvenance();

        }//end if
      }// end while
   
  }//end lastChangeProvenanceTimeFiltered

  
  


  /**
   * (variant == true) : goes through the ArrayList of revisions, creates a
   * Model for each Revision with the TripleExtractor, gets the differences of
   * two consecutive revisions and writes them in a file;
   */
  public void wholeProvenance() {
	  
	  	Model  newestModel = tripleExtractor.generateModel(parser.getPage().
	          getRevision().get(parser.getPage().getRevision().size()-1).getId(),
	          this.language,"custom", true );
	  	

	for (int i = parser.getPage().getRevision().size()-2; i >= 0; i-- ) {

		
		
		Model compareModel = tripleExtractor.generateModel(parser.getPage().
              getRevision().get(i).getId(), this.language,"custom", true );
		  
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

  }//end wholeProvenance


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
  public  void lastChangeProvenance() {

	  
	Model newestModel = tripleExtractor.generateModel(parser.getPage().
	          getRevision().get(parser.getPage().getRevision().size()-1).getId(),
	      this.language,"custom", true);
	  
	
	  
	for (int i = parser.getPage().getRevision().size()-2; i >= 0; i-- ) {
		//System.out.println(parser.getPage().getRevision().get(i).getId() +"---" +i);
		Model compareModel = tripleExtractor.generateModel(parser.getPage().
              getRevision().get(i).getId(), this.language, "custom", true);
		  
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
	 
   }//end astChangeProvenance


 
}
