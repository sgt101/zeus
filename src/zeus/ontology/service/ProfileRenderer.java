package zeus.ontology.service;

import zeus.concepts.Task;
import zeus.actors.AgentContext;

public interface ProfileRenderer {

  /**
   * Render a service profile
   */
  public String renderProfile(Task task, AgentContext context);

}
