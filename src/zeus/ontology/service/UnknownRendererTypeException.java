package zeus.ontology.service;

/**
 * Thrown when a request is made for a type of <code>Renderer</code>
 * not available.
 */
public class UnknownRendererTypeException extends Exception {

  public UnknownRendererTypeException(String message) {
    super(message);
  }

}
