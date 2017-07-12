package rdf;

import java.util.ArrayList;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;


//import org.apache.jena.atlas.logging.Log;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Class to find the differences between two RDF graphs and to map the triples
 * that have changed
 *
 * @author nicole
 */
public class RDFDiffer {
	private static Logger logger = Logger.getLogger(ProvenanceManager.class.getName());
  /*
   * contains the old and the new Triple in a Statment Array
   */
  ArrayList<Statement[]> difference = new ArrayList<Statement[]>();
  ArrayList<Statement[]> lastRevison = new ArrayList<Statement[]>();

  /**
   * Method gets two models, finds their differences and maps the triples that
   * have changed
   *
   * @param newModel contains the newer rdf-version of the article
   * @param oldModel contains the older rdf-version of the article
   * @return ArrayList with the old Triples and the corresponding new Triples
   */
  public ArrayList<Statement[]> getDifference( Model newModel, Model oldModel ) {
	  
	  
    Model differenceOldModel = ModelFactory.createDefaultModel();
    Model differenceNewModel = ModelFactory.createDefaultModel();

    /*
     * creating the Iterators to iterate through the Models
     */
    StmtIterator iterNewModel;
    StmtIterator iterOldModel;

    Statement empty = null;

    /*
     * Creates the models, which only contain differences
     */
    if ( !oldModel.isIsomorphicWith( newModel ) ) {
      differenceOldModel = oldModel.difference( newModel );
      differenceNewModel = newModel.difference( oldModel );
    }

    iterNewModel = differenceNewModel.listStatements();


    /*
     * Iterates through a statement list to find the corresponding triples in
     * the other model
     */
    while ( iterNewModel.hasNext() ) {
      
   
    	
      Statement triple = iterNewModel.nextStatement();
      Resource subject = triple.getSubject();
      Property predicate = triple.getPredicate();
      Statement[] statementArray = new Statement[2];

      /*
       * trys to find a matching Triple in the older Model
       */
      try {
        Statement matchedTriple = differenceOldModel.getProperty( subject,
          predicate );
        differenceOldModel.remove( matchedTriple );

        statementArray[0] = triple;
        statementArray[1] = matchedTriple;
      }
      /*
       * if none is found assign an empty Statement
       */
      catch ( NullPointerException npe ) {
        statementArray[0] = triple;
        statementArray[1] = empty;
        //Log.error(npe, "");
        logger.log(Level.INFO, "npe");
      }

      difference.add( statementArray );
    }

    iterOldModel = differenceOldModel.listStatements();

    /*
     * adds the triple to the ArrayList which are deletet from the newer Model
     */
    while ( iterOldModel.hasNext() ) {
      Statement triple = iterOldModel.nextStatement();

      Statement[] statementArray = new Statement[2];
      statementArray[0] = empty;
      statementArray[1] = triple;

      difference.add( statementArray );
    }
    return difference;
  }

  /**
   * assigns empty Statements to the Statemens from the last Revision
   *
   * @param model RDF Graph from the last Revision
   * @return ArrayList where each Statement from the Model ist assignt to an
   * empty Statement
   */
  public ArrayList<Statement[]> assignLastRevison( Model model ) {
    StmtIterator iter = model.listStatements();
    Statement empty = null;
    
    /*
     * iterates though the Model and assigns an empty Statement 
     * to every Statement in the Model
     */
    while ( iter.hasNext() ) {
      Statement triple = iter.nextStatement();

      Statement[] statementArray = new Statement[2];
      statementArray[0] = triple;
      statementArray[1] = empty;

      lastRevison.add( statementArray );
    }

    return lastRevison;
  }
  
  public void clear(){
	  difference.clear();
	  lastRevison.clear();
  }
}
