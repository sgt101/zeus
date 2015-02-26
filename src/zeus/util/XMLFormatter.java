package zeus.util;

/**
 * Format an XML String
 */
public class XMLFormatter {

  private static final String indent = "  ";

  /**
   * Add formatting to an XML string
   */
  public static String formatXML(String xmlContent) {

    String result = "";

    try {

      for(int indentLevel = 0, index = 0 ;
	  index < xmlContent.length() ;
	  index++) {

	//Seek to next "<"
	index = xmlContent.indexOf("<", index);

	if(index < 0 || index >= xmlContent.length())
	  break;

	//Trim out XML block
	String section =
	  xmlContent.substring(index, xmlContent.indexOf(">", index) + 1);

	if(section.matches("<!--.*-->")) {
	  //Is comment <!--....-->
	  result = indent(result, indentLevel);
	}
	else if(section.matches("<!.*>")) {
	  //Directive
	  result = indent(result, indentLevel);
	}
	else if(section.matches("<\\?.*\\?>")) {
	  //Is directive <?...?>
	  result = indent(result, indentLevel);
	}
	else if(section.matches("<[\\s]*[/\\\\].*>")) {
	  //Is closing tag </...>
	  result = indent(result, --indentLevel);
	}
	else if(section.matches("<.*[/\\\\][\\s]*>")) {
	  //Is standalone tag <.../>
	  result = indent(result, indentLevel);
	}
	else {
	  //Is begin tag <....>
	  result = indent(result, indentLevel++);
	}

	result += section + "\n";
      }
    }
    catch(StringIndexOutOfBoundsException s) {
      s.printStackTrace();
      return "Invalid XML";
    }

    return result;
  }

  public static String indent(String text, int indentLevel) {

    for( int count = indentLevel ; count > 0 ; count--)
      text += indent;

    return text;
  }
}
