package org.dbpedia.infoboxprov.rdf;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
   * @param mapping extraction mode from the dbpedia tripleextracktor
   * @param variant track just the last changing or all changings
   * @return Models
   */
  public Model generateModel(int revisionsNumber, String language, String mapping )  {
	
	  try {
		  
			 HttpURLConnection openConnection =(HttpURLConnection)new URL(extractionUrl +
				            language + "/extract" + "?title=&revid=" + revisionsNumber +
				  	      "&format=turtle-triples&" + "extractors=" + mapping).openConnection();
				 
			 openConnection.getResponseCode();
				 
		
			 if(openConnection.getResponseCode() == 500 ) {
				 
					 return null;
			 }
				
		}catch (IOException e) {
				 
		}
	  
	Model tmp = ModelFactory.createDefaultModel();
	Model newModel = ModelFactory.createDefaultModel();
	 
	try {
	
	tmp.read(extractionUrl +
	            language + "/extract" + "?title=&revid=" + revisionsNumber +
	      "&format=turtle-triples&" + "extractors=" + mapping, null, "TURTLE");
	
	}catch(Exception e) {
		
		System.out.println("Couldn't read the RDF statements from DBpedia Extractionframework");
		
		return newModel;
		
	}
   
		StmtIterator stmts = tmp.listStatements();
		while ( stmts.hasNext() ) {
			Statement triple = stmts.nextStatement();
  	  
			if(predicates.size() == 0 || predicates == null) {
				
				newModel.add(triple); 
				
			}else {
  	  
				String tripleStr = triple .getPredicate().toString();
	
				for(int i = 0; i < predicates.size(); i++) {
  	  
					if(tripleStr.contains(predicates.get(i))){
						newModel.add(triple); 
					}
				}
  
			}
  	  
		}
   
	
	
	return newModel;

  }
  
  
  
  
  	
  

}
