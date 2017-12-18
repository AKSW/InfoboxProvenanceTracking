package org.dbpedia.infoboxprov.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Extracts the Tripels from an URI
 */
public class TripleExtractor {


  /**
   * function for reading turtle-triples only from an URI with given language
   * @param revisionsNumber revisionnumber
   * @param language langage
   * @return Model
 * @throws FileNotFoundException 
   */
  public Model generateModel(int revisionsNumber, String language)  {
	
	Model tmp = ModelFactory.createDefaultModel();
    Model newModel = ModelFactory.createDefaultModel();

    
    tmp.read("http://mappings.dbpedia.org/server/extraction/" +
            language + "/extract" + "?title=&revid=" + revisionsNumber +
      "&format=turtle-triples&" + "extractors=custom", null, "TURTLE");
    
    StmtIterator stmts = tmp.listStatements();
	 
    
    
    while ( stmts.hasNext() ) {
  	  Statement triple = stmts.nextStatement();
  	  String tripleStr = triple .getPredicate().toString();
  	  if(tripleStr.contains("http://"+language+".dbpedia.org/property")&&
  			!tripleStr.contains("wikiPageUsesTemplate")  )
  	  {
  		 
  		 
  		newModel.add(triple);  }
  	  
    }
   

   return newModel;

  }


}
