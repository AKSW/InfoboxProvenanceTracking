package org.dbpedia.infoboxprov.rdf;

import java.util.ArrayList;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;


public class TripleExtractor {
	
	private String extractionUrl = null;
	private ArrayList<String> predicates = null;
	
	
	
	public TripleExtractor(String extractionUrl, ArrayList<String> predicates){
		
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
  public Model generateModel(int revisionsNumber, String language, String mapping, boolean variant )  {
	
	Model tmp = ModelFactory.createDefaultModel();
	Model newModel = ModelFactory.createDefaultModel();
	 
	tmp.read(extractionUrl +
	            language + "/extract" + "?title=&revid=" + revisionsNumber +
	      "&format=turtle-triples&" + "extractors=" + mapping, null, "TURTLE");
	
	if(variant) {
	
   
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
  
  	
  	  
		}
   
	} else {
		
	
		StmtIterator stmts = tmp.listStatements();
	    while ( stmts.hasNext() ) {
	  	  Statement triple = stmts.nextStatement();
	  	  String tripleStr = triple .getPredicate().toString();
	  	
	  	newModel.add(triple); 
	  	  
	  	//System.out.println(predicates.size() );
	  	  
	  	  for(int i = 0; i < predicates.size(); i++) {
	  	  
	  		//System.out.println(tripleStr + "     " + predicates.get(i) );
	  		  
	  		  if(tripleStr.contains(predicates.get(i))){
	  			  
	  			
	  	  	  
	  			newModel.remove(triple); 
	  		  }
	  	  }
	  
	  	
	  	  
	    }
		
	
	}
	
	return newModel;

  }
  
  
  
  
  	
  

}
