package rdf;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Extracts the Tripels from an URI
 */
public class TripleExtractor {


  /**
   * function for reading turtle-triples only from an URI
   * @param revisionsNumber revisionsNumber
   * @return Model
   */
  public Model generateModel(int revisionsNumber) {

    Model rdfGraph = ModelFactory.createDefaultModel();

    rdfGraph.read("http://mappings.dbpedia.org/server/extraction/" +
            "en" + "/extract" + "?title=&revid=" + revisionsNumber +
            "&format=turtle-triples&" + "extractors=custom", null, "TURTLE");

    return rdfGraph;

  }


  /**
   * function for reading turtle-triples only from an URI with given language
   * @param revisionsNumber revisionnumber
   * @param language langage
   * @return Model
   */
  public Model generateModel(int revisionsNumber, String language) {

    Model rdfGraph = ModelFactory.createDefaultModel();

    rdfGraph.read("http://mappings.dbpedia.org/server/extraction/" +
            language + "/extract" + "?title=&revid=" + revisionsNumber +
            "&format=turtle-triples&" + "extractors=custom", null, "TURTLE");

    return rdfGraph;

  }


}
