package zeus.ontology.service;

import java.util.List;
import zeus.actors.AgentContext;
import zeus.concepts.Task;

public interface RangeRenderer {

  public String renderRange(Task task, AgentContext context);

}
