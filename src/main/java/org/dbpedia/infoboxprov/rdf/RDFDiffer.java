package org.dbpedia.infoboxprov.rdf;

import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Class to find the differences between two RDF graphs and to map the triples
 * that have changed
 *
 */
public class RDFDiffer {
	
  /*
   * contains the old and the new Triple in a Statment Array
   * 
   */
  Model newModel = null;
  Model oldModel = null;
	
  ArrayList<Statement[]> newTripleOldTriple = null;
  ArrayList<Statement[]> addedTriple = null;

  public RDFDiffer(Model newModel, Model oldModel) {


	  this.newModel = newModel;
	  this.oldModel = oldModel;
	  this.newTripleOldTriple = new ArrayList<Statement[]>();
	
  }
  
  public RDFDiffer(Model newModel, Model oldModel ,ArrayList<Statement[]> addedTriple) {


	  this.newModel = newModel;
	  this.oldModel = oldModel;
	  this.addedTriple = addedTriple;
	
  }
  
  
  public Model getNewModel() {
	  return newModel;
  }
  
  public ArrayList<Statement[]> getNewTripleOldTriple(){
	  return newTripleOldTriple;
  }
  
  /**
   * Method gets two models, finds their differences and maps the triples that
   * have changed
   *
   * @param newModel contains the newer rdf-version of the article
   * @param oldModel contains the older rdf-version of the article
   * @return ArrayList with the old Triples and the corresponding new Triples
   */
  public void determineLeftDifferences(  ) {
	 
    /*
     * creating the Iterators to iterate through the Models
     */
    StmtIterator iterLeftDifference;
    StmtIterator iterOldModel;
    
    
    /*
     * Creates the models, which only contain differences
     */
    if ( !oldModel.isIsomorphicWith( newModel ) ) {
   
    	iterLeftDifference = newModel.difference( oldModel ).listStatements();
    	
    }else {
    	
    	return;
    }

        
        

    while ( iterLeftDifference.hasNext() ) {
  		 
  	 Statement  stmt = iterLeftDifference.nextStatement();
  	
  	
  	 
  	 Property match = oldModel.createProperty( stmt.getPredicate().toString() );
  	 iterOldModel = oldModel.listStatements(null, match, (RDFNode)null );
  	 int countMatches = 0;
  	 while ( iterOldModel.hasNext() ) {
  		
  		 countMatches++;
  		 Statement stmt2 = iterOldModel.nextStatement();
  		
  		 Statement[] entry = new Statement[2];
  		 entry[0] = stmt;
  		 entry[1] = stmt2; 
  		 newTripleOldTriple.add(entry);
  		 
  	 }
	 if(countMatches == 0) {
		
	 Statement[] entry = new Statement[2];
	 entry[0] = stmt;
	 entry[1] = null;
	 newTripleOldTriple.add(entry); 
	 }
  	 
  	newModel.remove(stmt);
  	}
    
    return;
  }
  
  public void determineLeftRightDifferences(  ) {
		 
	 
	  
	    StmtIterator iterLeftDifference;
	    StmtIterator iterRightDifference;
	    
	    /*
	     * Creates the models, which only contain differences
	     */
	    if ( !oldModel.isIsomorphicWith( newModel ) ) {
	   
	    	iterLeftDifference = newModel.difference( oldModel ).listStatements();
	    	iterRightDifference = oldModel.difference( newModel ).listStatements();
	      
	    }else {
	    	
	    	return;
	    }

	          

	    while ( iterLeftDifference.hasNext() ) {
	  		 
	  	 Statement  stmt = iterLeftDifference.nextStatement();
	  	
	  	
	  	 
	  	 Property match = oldModel.createProperty( stmt.getPredicate().toString() );
	  	 StmtIterator iterOldModel = oldModel.listStatements(null, match, (RDFNode)null );
	  	 int countMatches = 0;
	  	 while ( iterOldModel.hasNext() ) {
	  		
	  		 countMatches++;
	  		 Statement stmt2 = iterOldModel.nextStatement();
	  		
	  		 newModel.remove(stmt);
	  		 newModel.add(stmt2);
	  		 
	  		 Statement[] entry = new Statement[2];
	  		 entry[0] = stmt;
	  		 entry[1] = stmt2; 
	  		 
	  		 newTripleOldTriple.add(entry);
	  		 
	  	 }
		 if(countMatches == 0) {
		
			 newModel.remove(stmt);
		 
			 Statement[] entry = new Statement[2];
			 entry[0] = stmt;
			 entry[1] = null;
			 
			 newTripleOldTriple.add(entry); 
		 }
	  	 
		 
	  	}
	    
	    while(iterRightDifference.hasNext()) {
	  	
	     Statement stmt = iterRightDifference.nextStatement();
			 
		 Property match = newModel.createProperty( stmt.getPredicate().toString() );
		 StmtIterator iterNewModel = newModel.listStatements(null, match, (RDFNode)null );
		 
		 int countMatches = 0;
		 while ( iterNewModel.hasNext() ) {
		  		 
		  	countMatches++;
		  	iterNewModel.nextStatement();
		  		 
		 }
		 if(countMatches == 0) {
			 newModel.add(stmt);
			 Statement[] entry = new Statement[2];
			 entry[0] = null;
			 entry[1] = stmt;
			 
			 newTripleOldTriple.add(entry); 
		 }
			 
		 }
	    
	    return;
	  }
 
}
