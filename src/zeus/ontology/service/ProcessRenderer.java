package zeus.ontology.service;

import zeus.concepts.Task;
import zeus.actors.AgentContext;

public interface ProcessRenderer {

  /**
   * Render a service process
   */
  public String renderProcess(Task task, AgentContext context);

}
