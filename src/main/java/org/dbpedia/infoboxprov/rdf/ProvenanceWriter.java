package org.dbpedia.infoboxprov.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.dbpedia.infoboxprov.dump.Revision;



/**
 * class for writing the output in a file it gets the actual differences between
 * 2 revision numbers and the revision object it checks whether the object is a
 * literal or an URI and writes the statements it gets in n-triples. In each
 * line you have at first the new triple followed up by the old triple, so you
 * can see what has changed. After the 2 triples you always have the revision
 * object to see when the change was made and by who, you also get the exact
 * revision number of that change too.
 * 
 * @author daniel
 */
public class ProvenanceWriter {
	
  private static Logger logger = Logger.getLogger(ProvenanceManager.class.getName());
  private File outputFile = null;
 
  ArrayList<String> wholeDifferernces = new ArrayList<>();

  /**
   * constructor creates a new ProvenanceWriter object, which creates a new file
   * as .tsv, with UTF-8 encoding
   *
   * @param fileName the name of file
   * @param isNewFile creates new file if true
   */
  public ProvenanceWriter(String fileName, boolean isNewFile) {
	  
    try {
      this.outputFile = new File("threadfile//" + fileName + ".tsv");
      if (isNewFile) {
        this.outputFile.delete();
      }
      
      outputFile.createNewFile();
    }
    catch (Exception e) {
    	
      System.out.println("Could not create the TSV-File");
    }
  }
  
  

  /**
   * Simply iterate through the 2d array and write each line in the file created
   * at the constructor
   *
   * To write in N-Triples the variables temp and temp2 are for formatting the
   * objects to the right N-Triples format
   *
   * if one of the two triples are missing causing by deleting or creating
   * triples it returns a comment in the form: # triple does not exist
   *
   * saves differences in ArrayList
   *
   * @param revisionOfChange revision object with revision number, author,
   * timestamp
   * @param stmt statement wich will be written to the revision
   */
  
  public void writeAdding(Statement[] stmt, Revision revisionOfChange) {
			    	
		   String line = "";
		   line += "<" + stmt[0].getSubject()   + "> ";
		   line += "<" + stmt[0].getPredicate() + "> ";
		   line += getObjectAsNTriples(stmt[0]) + "\t";
		   line += "# triple added"				+ "\t";
		   line += revisionOfChange.toString()  + "\n";
		   wholeDifferernces.add(line);
		   write();
		  }
  /**
  * @param revisionOfChange revision object with revision number, author,
  * timestamp
  * @param stmt statement wich will be deleted from the revision
  */
  public void writeDeleting(Statement[] stmt, Revision revisionOfChange) {
  	
	   String line = "";
	   line += "<" + stmt[1].getSubject()   + "> ";
	   line += "<" + stmt[1].getPredicate() + "> ";
	   line += getObjectAsNTriples(stmt[1]) + "\t";
	   line += "# triple delete"			+ "\t";
	   line += revisionOfChange.toString()  + "\n";
	   wholeDifferernces.add(line);
	   write();
  }
  
  public void writeDifferences(Statement[] stmt,
    Revision revisionOfChange) {
	 
      String line = "";
      line += "<" + stmt[0].getSubject() 		+ "> ";
      line += "<" + stmt[0].getPredicate() 	+ "> ";
      line += getObjectAsNTriples(stmt[0]) 	+ "\t";
      line += "<" + stmt[1].getSubject() 		+ "> ";
      line += "<" + stmt[1].getPredicate() 	+ "> ";
      line += getObjectAsNTriples(stmt[1]) 	+ "\t";
      line += revisionOfChange.toString() 	+ "\n";
      
      wholeDifferernces.add(line);
      write();
  }
  
  public void writeModel (Model model, Revision revisionOfChange){
	  
	  if(model.isEmpty())return;
	  StmtIterator stmts = model.listStatements();
	  
	  while ( stmts.hasNext() ) {
		  String line = "";
		  Statement triple = stmts.nextStatement();
		  line += "<" + triple.getSubject()   + "> ";
	      line += "<" + triple.getPredicate() + "> ";
	      line += getObjectAsNTriples(triple) + "\t";
	      line += "# triple added"			  + "\t";
	      line += revisionOfChange.toString() + "\n";
	      wholeDifferernces.add(line);
	  }
	  write();
  }
  
  public void writeModel (Model model){
	  
	  if(model.isEmpty())return;
	  StmtIterator stmts = model.listStatements();
	  
	  while ( stmts.hasNext() ) {
		  String line = "";
		  Statement triple = stmts.nextStatement();
		  line += "<" + triple.getSubject() + "> ";
	      line += "<" + triple.getPredicate() + "> ";
	      line += getObjectAsNTriples(triple) + "\t";
	      line += "# triple does not exist\t";
	      wholeDifferernces.add(line);    
	  }
	  write();
  }

  /**
   * this function converts an object from a given statement to N-Triples, by
   * manually parsing it
   *
   * @param stmt Object from the given statement to be converted
   * @return Object from the given statement as N-Triples
   */
  private String getObjectAsNTriples(Statement stmt) {

    String temp;
    if (stmt == null) {
      temp = "";
    }
    else {
      temp = "~!°" + stmt.getObject() + "°!~";
    }

    if (temp.startsWith("~!°http")) {
      temp = temp.replace("~!°", "");
      temp = temp.replace("http", "<http");
      temp = temp.replace("°!~", "> .");
    }
    else if (temp.contains("^^http")) {
      temp = temp.replace("~!°", "\"");
      temp = temp.replace("^^http", "\"^^<http");
      temp = temp.replace("°!~", "> .");
    }
    else if (temp.contains("@")) {
      temp = temp.replace("~!°", "\"");
      temp = temp.replace("@", "\"@");
      temp = temp.replace("°!~", " .");
    }
    else {
      temp = temp.replace("~!°", "\"");
      temp = temp.replace("°!~", "\" .");
    }

    return temp;
  }

  /**
   * writes the ArrayList in a file
   */
  public void write() {
	  
    try (Writer fw = new OutputStreamWriter(new FileOutputStream(outputFile,
      true), StandardCharsets.UTF_8)) {
    
      for (String line : wholeDifferernces) {
    	  //System.out.println(line);
        fw.write(line);
       
      }
      
      wholeDifferernces.clear();
     
    } catch (FileNotFoundException e) {
    	
      System.out.println("TSV-File not found!");
      logger.log(Level.SEVERE,"File not found!" , e);
      
    } catch (IOException e) {
    	
      System.out.println("Could not write TSV-File!");
      logger.log(Level.SEVERE,"Could not read or write file!" , e);
    }
  }

}// end class
