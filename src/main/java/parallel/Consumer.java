package parallel;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import dump.DumpParser;

import rdf.ProvenanceWriter;
import rdf.RDFDiffer;
import rdf.TripleExtractor;
import io.READVARIANT;



public class Consumer extends Thread implements Runnable {

	  protected ArrayBlockingQueue<Long> queue = null;
	  private String language = null;
	  /**
	   * variant if whole provenance or just the last change of a triple
	   */
	  private static volatile boolean done;
	  private boolean variant;
	  private DumpParser parser = null;
	  private TripleExtractor tripleExtractor = null;
	  //private LogWriter logWriter = null;
	  private ProvenanceWriter writer = null;
	  private READVARIANT readVariant = null;
	  private String threadName = null;
	  ArrayList<Statement[]> differences = null;
	  ArrayList<Statement[]> filteredDifferences = null;
	  ArrayList<Statement> alreadyFoundDifferences = null;
	  private TreeSet<Integer> finishedArticles = null;
	  private Date[] extractionTimeFrame;
	  private String path;
	  
	  public Consumer(ArrayBlockingQueue<Long> queue,
			  String threadName,
              String language,
              READVARIANT readVariant,
              Date[] extractionTimeFrame,
              TreeSet<Integer> finishedArticles, 
              boolean variant,
              String path) {
	   this.queue = queue;
	   this.language = language;
	   this.variant = variant;
	   this.readVariant = readVariant;
	   this.threadName = threadName;
	   this.extractionTimeFrame = extractionTimeFrame;
	   this.finishedArticles = finishedArticles;
	   this.filteredDifferences = new ArrayList<Statement[]>();
	   this.alreadyFoundDifferences = new ArrayList<Statement>();
	   this.tripleExtractor = new TripleExtractor();
	   // this.logWriter = new LogWriter(threadName);
	  
	   Consumer.done = true;
	   this.path= path;
	 
	  }
	  
	  @Override
	  public void run() {

	  try {
		  
	  	
	  while(done) {
		long offset = queue.take();
		if(offset==0 ){
			  queue.put((long) 0);
			  break;
		}
		 
		this.parser = new DumpParser(path,offset,extractionTimeFrame,  finishedArticles);
		this.writer = new ProvenanceWriter(threadName, false);
		
		//checks which variant is wanted
		//variant default: get complete provenance	  
	    if (this.variant) {
	    	
	    	//variant lastchange: get just the last change of each triple
	    	//switch betwenn the different readvariants
	        switch (readVariant) {
	          case ReadDefault:
	        	 
	            //ProvenanceWriter.newRun(threadName);
	            //LogWriter.newRun(threadName);
	            lastChangeProvenanceDefault();
	            break;
	          case ReadTimeFiltered:
	            //ProvenanceWriter.newRun(threadName);
	            //LogWriter.newRun(threadName);
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
	    	
	    	//switch betwenn the different readvariants
	        switch (readVariant) {
	          case ReadDefault:
	          //  ProvenanceWriter.newRun(threadName);
	            //LogWriter.newRun(threadName);
	            wholeProvenanceDefault();
	            break;
	          case ReadTimeFiltered:
	          //ProvenanceWriter.newRun(threadName);
	          //LogWriter.newRun(threadName);
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
	    
	    
		}//end while	
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			//System.out.println("Interruption Exception");
		}
	    
	  }//end run
	 
	 public void wholeProvenanceDefault() {
		 parser.mapPageDefault();
		
		 if (parser.getPage() != null) {wholeProvenance();}//end if
	 }


	 // methode wich catch the wohle provenance
	 // and uses the readPageTimeFiltered methode from the 
	 // DumpParser class
	 public void wholeProvenanceTimeFiltered() {
		 parser.mapPageTimeFiltered();
		 if(parser.getPage() != null) {wholeProvenance();}//end if
		    
	  }//end wholeProvenanceTimeFiltered
		  
	  // methode wich catch the wohle provenance
	  // and uses the readPageTimeFrameRerun methode from the 
	  // DumpParser class
	  public void wholeProvenanceTimeFrameRerun() {
		  parser.mapTimeFilteredRerun();
		  if(parser.getPage() != null) {wholeProvenance();}//end if 
	  }//end wholeProvenanceTimeFrameRerun

	  // methode wich catch the wohle provenance
	  // and uses the readPageRerun methode from the 
	  // DumpParser class
	  public void wholeProvenanceRerun() {
		  
		   parser.mapPageRerun();
		   if(parser.getPage() != null) {wholeProvenance();}//end if 
		   
	  }//end wholeProvenanceRerun

	  // methode wich catch the last change provenance
	  // and uses the readPageDefault methode from the 
	  // DumpParser class
	  public void lastChangeProvenanceDefault() {
			 parser.mapPageDefault();
			 if (parser.getPage() != null) {lastChangeProvenance();}//end if
	  }//end lastChangeProvenance

	  // methode wich catch the last change provenance
	  // and uses the readPageTimeFiltered methode from
	  // the DumpParser class
	  public void lastChangeProvenanceTimeFiltered() {
		     parser.mapPageTimeFiltered();
			 if(parser.getPage() != null) {lastChangeProvenance();}//end if
	  }//end lastChangeProvenanceTimeFiltered

	   // methode wich catch the last change provenance
	   // and uses the readPageTimeFrameRerun methode from
	   // the DumpParser class
	   public void lastChangeProvenanceTimeFrameRerun() {
			  parser.mapTimeFilteredRerun();
			  if(parser.getPage() != null) {lastChangeProvenance();}//end if 
	   }//end lastChangeProvenanceTimeFrameRerun

	   // methode wich catch the last change provenance
	   // and uses the readPageRerun methode from
	   // the DumpParser class
	   public void lastChangeProvenanceRerun() {
		   parser.mapPageRerun();
		   if(parser.getPage() != null) {lastChangeProvenance();}//end if 
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