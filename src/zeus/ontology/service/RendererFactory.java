package zeus.ontology.service;


public class RendererFactory {

  /**
   * Return an instance of a renderer for the type of description
   * named by <code>type</code>.
   * @param type Type of service description to render
   * @throws UnknownRendererTypeException Indicates <code>type</code>
   * is not a valid renderer.
   */
  public static InstanceRenderer getInstanceRenderer(String type)
    throws UnknownRendererTypeException {

    if(type == null) {
      throw new UnknownRendererTypeException("Type cannot be null");
    }

    if(type.equalsIgnoreCase("DAML-S")) {
      return new DAML_S_Renderer();
    }
    else {
      throw new UnknownRendererTypeException("No type " +type+ " available.");
    }

  }

  /**
   * Return a RangeRenderer
   */
  public static RangeRenderer getRangeRenderer(String type)
    throws UnknownRendererTypeException {

    if(type == null) {
      throw new UnknownRendererTypeException("Type cannot be null");
    }

    if(type.equalsIgnoreCase("XSD")) {
      return new SchemaRenderer();
    }
    else {
      throw new UnknownRendererTypeException("Unknown type: " + type);
    }
  }

  public static ProfileRenderer getProfileRenderer(String type)
    throws UnknownRendererTypeException {

    if(type == null) {
      throw new UnknownRendererTypeException("Type cannot be null");
    }

    if(type.equalsIgnoreCase("DAML-S")) {
      return new DAML_S_Renderer();
    }
    else {
      throw new UnknownRendererTypeException("No type " +type+ " available.");
    }

  }

  public static ProcessRenderer getProcessRenderer(String type)
    throws UnknownRendererTypeException {

    if(type == null) {
      throw new UnknownRendererTypeException("Type cannot be null");
    }

    if(type.equalsIgnoreCase("DAML-S")) {
      return new DAML_S_Renderer();
    }
    else {
      throw new UnknownRendererTypeException("No type " +type+ " available.");
    }

  }
}
