package zeus.ontology.service;

import zeus.concepts.Task;
import zeus.actors.AgentContext;

public interface InstanceRenderer {

  /**
   * Get the <code>String</code> of the service instance description
   * using the supplied attributes
   */
  public String renderInstance(Task task, AgentContext context);

}
