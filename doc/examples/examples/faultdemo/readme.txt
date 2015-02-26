                       The ZEUS Agent Building Toolkit
                             Version 1.0 May 1999

                                --------------
                                The Fault Demo
                                --------------

The Fault demo is a simulation of a federated network of mobile telephone
cells which occasionally develop faults and need to be fixed. The notable
feature of this demo is that the agents are reactive to the environment,
using condition-action rules to trigger their behaviour rather than waiting
for instructions from human users.

In this example there are 5 agents:

* WorldSimulator - this represents a model of the application domain (in this
                   case a matrix of phone cells); it is responsible for the
                   generation of new faults
* North          - an agent with responsibility for a set of cells in the
                   north of the our virtual UK cellphone network
* East           - an agent with responsibility for cells in the east
* West           - an agent with responsibility for cells in the west
* South          - an agent with responsibility for cells in the south

There are also the 3 standard utility agents, a Name Server, a Facilitator,
and a Visualiser, whose roles are described in the ZEUS Technical Manual.


Getting Started
---------------

Assuming that all the agents will be running on the same machine, launching
the application will involve the following process:

· If you haven't already, open a command line/shell and cd into the
  zeus/examples/faultdemo directory

· Enter the command 'run1', this will execute the run1 script and start the
  Agent Name Server (ANS).  
	
Unix users may need to change the permissions of the run scripts to execute
them, especially if they have been copied across from a non-Unix (i.e. DOS)
filing system, e.g. 'chmod 777 run*'

No errors should be reported, and a new Java interpreter process should start
running in the background.  If a problem has occurred, check your system's
configuration (e.g. CLASSPATH setting, install directory in the .zeus.prp
file etc.)

· Enter the command 'run2', this will start the Visualiser and Facilitator
  agents.  Two additional Java processes should be started, and a Visualiser
	Hub window should appear on screen.

· Enter the command 'run3', this will start the WorldSimulator agent and the
  4 network agents. There will also be an Agent Viewer window for the North
	agent and a front-end window for WorldSimulator that displays a map of the
	UK overlaid with a grid of cells.

Once launched the agents will await the development of faults; they will do
nothing autonomously apart from replying to register themselves with the Name
Server and the Facilitator. The agents operate independently of the Visualiser
ans so will not attempt to contact it when they start. Rather the Visualiser
must contact the agents and ask them to inform it of what they are doing,
this involves the following:

· Launch the Visualiser's Society Viewer by clicking its icon on the
  Visualiser Hub. This opens a window with icons for each of the agents. Then
	click on the WorldSimulator agent's icon and select the "Centered" button
	on the layout panel. This should arrange the agents in the middle of the
	window around the WorldSimulator agent.

· To observe how the agents interact with each other select the "Request
  Messages" option from the "Online" menu in the menubar. This will open a
	dialog enabling you to choose which agents' messages are displayed; choose
  'select all' and then 'ok'. Agents will now forward copies of their
	messages to the Society Viewer when they communicate.

· Now launch the Visualiser's Reports Tool by clicking its icon on the
  Visualiser Hub. Just as you did with the Society Viewer you will have to
	register the Reports Tool so it is kept informed of the agents' actions.
	The same procedure is followed, select the "Request Reports" option from
	the "Online" menu and then select all the agents in the dialog window.

Now watch the Society Viewer as the Visualiser agent sends "subscribe"
messages to the chosen agents.

· Then launch the Visualiser's Statistics Tool by clicking its icon on the
  Visualiser Hub and repeat the same "Request Messages" procedure, selecting
	all the agents that are shown in the dialog window.

Again the Society Viewer will show the exchange of messages as the Visualiser
sends "subscribe" requests to the chosen agents.  With the Visualiser now
ready to depict several aspects of the application, we can begin the
simulation, this involves the following:

· Open (or bring to the front) the WorldSimulator window, this is the one
  with the map of the UK, that has a button at the bottom labelled 'Next'.
	Pressing this button randomly generates a fault in the cell matrix, and
	the agent in whose jurisdiction the fault lies will take responsibility
	for repairing it.

Faults can be of two kinds: black and red, with the former being slightly
more difficult to solve, but we shall ignore this aspect at this point.
If the demo has just started and pressing the button appears to have no effect
(apart from the Facilitator in the Society Viewer returning a failure
message), wait until another time-grain passes. By then the Facilitator will
have updated its ability list and the agents will be able to find each other.

The creation of a fault will trigger the 'notify_agent' rule in the
WorldSimulator's rulebase. This rule is stored in the file FaultSimulator.clp,
and looks like this:

  (notify_agent 5
      ?fault <- (Fault (owner ?owner))
      =>
      (send_message (receiver ?owner) (content ?fault) (type inform))
  )

This is a classic condition-action rule: when a fault is detected send a
message to the agent owning the faulty cell. Two aspects to note are the words
prefixed by ?, these are runtime variables, and see how the rule is linked to
the agent's message passing mechanism.

The arrival of the fault message will trigger the 'reaction' rule of the
recipient's rulebase. This rule is stored in the file Reactor.clp, and looks
like this:

   (reaction 5
      (Fault (id ?id) (type ?t))
      =>
      (achieve (fact (Repair (fault ?id) (type ?t))) (end_time 9)
			               (confirm_time 3))
   )

This action part of this rule creates a new goal for the agent, which
starts one of the agent's repair tasks.

If the fault is owned by an agent that has an Agent Viewer window active
you will be able to see the rule that triggers the agent by clicking on the
Rule Engine icon. The result of the rule being fired can then be seen in the
agent's Co-ordination Engine.

Solving the fault requires resources, in this case: engineers. Each agent owns 
engineers of different skills, and often an engineer will have to be hired
from another agent to complete the job. This will involve negotiation between
the agents concerned as they agree some measure of renumeration.

· If you return to the society viewer you will probably see a flurry of
  messages between particular pairs of agents, this indicates that negotiation
  is occuring. If one of these agents has an active Agent Viewer window you
  will be able to see messages related to negotiation by clicking  on the
	"Incoming Mail" icon.

· Still in the agent viewer, click on the "Planner and Scheduler". This
  component serves as the agent's diary with a series of time slots during
  which the agent can schedule the fault repair. If the agent is attempting a
	repair one slot will be coloured yellow, indicating that the agent is
	tentatively planning to repair the fault.

If the yellow slot in the plan diary disappears the agent will have cancelled
its plan to repair; i.e. something will have gone wrong. This may
be due to the necessary resources not being available especially if many
faults occur simultaneously.  If this happens, wait for the faults to clear
and then press the 'Next' button again. If it repeatedly happens there is
probably a problem with one or more of the agents, use the "Close Agents"
option from the Control Tool and restart the application.

Would should happen is that the diary slot will turn from yellow to blue,
indicating that all the resources necessary have been booked and the repair
is now scheduled to proceed.

· To monitor the progress of the repair, open (or bring to the front) the
  Reports Tool you opened earlier.

There should be an entry in the "Current Tasks" list on the left hand side.
Select it if it is not already highlighted, this will display the chain of
tasks, their preconditions and effects, which are necessary for the top-level
task to be completed (in this case a goal called Repair).

One notable aspect of the Report view is the changing task states. Each will
start in the 'Firm' state (cyan), representing that the task has been
scheduled to be performed. When the time comes to perform the task its
state will change to 'Running' (green). This represents the time during which
repair would actually occur. When this is complete the task will move into
the 'Completed' state (white), whereupon it can be used as the precondition
for the next task in the chain.

Once the fault is resolved the 'clear_fault' rule is fired:

  (clear_fault 5
      ?fault <- (Fault (id ?id) (type ?type))
      ?fix <- (Repair (fault ?id) (type ?type))
      =>
      (send_message (receiver WorldSimulator) (content ?fix) (type inform))
      (retract ?fault ?fix)
  )

This rule informs the WorldSimulator the fault has been addressed, and asks
it to update its model. At this point you should see the faulty cell
disappear from the UK map.

· To obtain information on the attributes of a task, (who owns it, how much
  it costs to invoke etc.) double click on the node in the Report View.

Whilst the tasks are executing some interesting statistics can be obtained
through the Statistics tool.

· Try viewing the different statistics and experimenting with the different
  display formats. You should see the statistics change in real-time as
  data accumulates.

Other perspectives on the repair process are available through the Agent
Viewer. For instance try examing the contents of the agent's 'databases',
these hold the information used and generated at run-time. Of particular
interest are the Task and Resource Databases, especially after the Repair
task has completed, as it will contain references to the recently solved
fault and the engineers used to fix it.

When you have finished with the agents and want to end the application open
the Visualiser's Control Tool and click on the Bomb icon, this will send a
message to each of the agents instructing them to terminate. This is the
quickest and cleanest way of ending ZEUS applications.



Where to Learn More
-------------------

* The Network Fault Case Study document describes how this application was
  built, how aspects of it can be modified, and how the application specific
  parts interact with the generic toolkit components.

* The Technical Manual explains how the agent components function, the role
  of utility agents and how aspects like communication and negotiation are
  supported.

* The online help documentation describes how to use the various facilities
  of the Visualiser tool.


--
Questions and Bug Reports concerning the Network Fault demo
should be sent to Jaron Collis <jaron@info.bt.co.uk>
------------------------------------------------------------













