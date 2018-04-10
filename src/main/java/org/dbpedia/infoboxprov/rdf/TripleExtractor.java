package org.dbpedia.infoboxprov.rdf;

import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Extracts the Tripels from an URI
 * 
 * @author daniel
 */
public class TripleExtractor {

	
	private String extractionUrl = null;
	private ArrayList<String> predicates = null;
	
	TripleExtractor(String extractionUrl, ArrayList<String> predicates){
		
		this.extractionUrl = extractionUrl;
		this.predicates = predicates;
		
	}
	
  /**
   * function for reading turtle-triples only from an URI with given language
   * @param revisionsNumber revisionnumber
   * @param language langage
   * @return Model
 * @throws FileNotFoundException 
   */
  public Model generateModel(int revisionsNumber, String language )  {
	
	Model tmp = ModelFactory.createDefaultModel();
    Model newModel = ModelFactory.createDefaultModel();

    tmp.read(extractionUrl +
            language + "/extract" + "?title=&revid=" + revisionsNumber +
      "&format=turtle-triples&" + "extractors=custom", null, "TURTLE");
    
    StmtIterator stmts = tmp.listStatements();
	 
    
    
    while ( stmts.hasNext() ) {
  	  Statement triple = stmts.nextStatement();
  	  String tripleStr = triple .getPredicate().toString();
  	  //System.out.println(tripleStr);
  	  
  	  for(int i = 0; i < predicates.size(); i++) {
  	  
  		  if(tripleStr.contains(predicates.get(i))){
  			  newModel.add(triple); 
  		  }
  	  }
  	  
  	/*  if(tripleStr.contains("http://"+language+".dbpedia.org/property")&&
  			!tripleStr.contains("wikiPageUsesTemplate")  )
  	  {
  		 
  		 
  		newModel.add(triple);  }*/
  	  
    }
   

   return newModel;

  }


}
