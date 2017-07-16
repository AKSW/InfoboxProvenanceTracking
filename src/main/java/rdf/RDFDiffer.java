package rdf;

import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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
  Model newModel = ModelFactory.createDefaultModel();
  Model oldModel = ModelFactory.createDefaultModel();
  //Model differences = null;
	
  ArrayList<Statement[]> newTripleOldTriple = null;

  public RDFDiffer(Model newModel, Model oldModel) {
	  
	  this.newModel = newModel;
	  this.oldModel = oldModel;
	  this.newTripleOldTriple = new ArrayList<Statement[]>();
	
  }
  
  public Model getReducedModel() {
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
  public void determineDifferences(  ) {
	 
	  
    Model differenceNewModel = ModelFactory.createDefaultModel();
    
    /*
     * creating the Iterators to iterate through the Models
     */
    StmtIterator iterNewModel;
    StmtIterator iterOldModel;
    
    /*
     * Creates the models, which only contain differences
     */
    if ( !oldModel.isIsomorphicWith( newModel ) ) {
   
    	differenceNewModel = newModel.difference( oldModel );
    	
      
    }else {
    	
    	return;
    }

        
    iterNewModel = differenceNewModel.listStatements();
    
//    while ( iterNewModel.hasNext() ) {
//	
//   	 Statement  stmt = iterNewModel.nextStatement();
//   	
//   	 iterOldModel = oldModel.listStatements(  );
//   	 
//   	 int countAll = 0;
//   	 int countMissmatches = 0;
//   	 
//   	 while ( iterOldModel.hasNext() ) {
//   		 countAll++;
//   		 Statement stmt2 = iterOldModel.nextStatement();
//   		 if(stmt.getPredicate().toString().equalsIgnoreCase(stmt2.getPredicate().toString())){
//   			 
//   		 Statement[] entry = new Statement[2];
//   		 entry[0] = stmt;
//   		 entry[1] = stmt2; 
//   		 newTripleOldTriple.add(entry);
//   		 }else {
//   			 countMissmatches++;
//   		 } 
//   	 }
//   	 if(countAll == countMissmatches) {
//   		 Statement[] entry = new Statement[2];
//			 entry[0] = null;
//			 entry[1] = stmt;
//			 newTripleOldTriple.add(entry); 
//		     
//   	 }
//   	 
//   	 newModel.remove(stmt);
//   }
    

  while ( iterNewModel.hasNext() ) {
  		 
  	 Statement  stmt = iterNewModel.nextStatement();
  	
  	
  	 
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
	 entry[0] = null;
	 entry[1] = stmt;
	 newTripleOldTriple.add(entry); 
	 }
  	 
  	newModel.remove(stmt);
  	}
    
    return;
  }
 
}
