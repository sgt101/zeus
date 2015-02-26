                       The ZEUS Agent Building Toolkit
                             Version 1.0 May 1999

                           -----------------------
                           The PC Manufacture Demo
                           -----------------------

The PC Manufacture demo is a simulation of the assembly of Personal Computers
(PCs) through the acquisition and integration of the necessary components.
As several manufacturers are involved it provides a good example of a supply
chain, where certain components must be acquired before the more complex
components of which they are a part are assembled.

In this example there are 5 agents:

* Dell          - a computer manufacturer
                - needs Keyboards, Monitors, Motherboards and Printers
* Taxan         - a monitor manufacturer
* Intel         - a motherboard manufacturer
* HP            - a printer manufacturer
                - needs printer cartridges
* CartridgeCorp - a printer cartridge manufacturer

There are also the 3 standard utility agents, a Name Server, a Facilitator,
and a Visualiser, whose roles are described in the ZEUS Technical Manual.


Getting Started
---------------

Assuming that all the agents will be running on the same machine, launching
the application will involve the following process:

· If you haven't already, open a command line/shell and cd into the
  zeus/examples/pc directory

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

· Enter the command 'run3', this will start the 5 PC component making agents,
  launching five more Java processes into the background. There will also be
	an Agent Viewer window for the Dell agent and a small window with a single
	button called DellPump.

Once launched the agents will await user instructions, they will do nothing
autonomously apart from replying to register themselves with the Name Server
and the Facilitator. As the agents can operate independently of the
Visualiser they will not attempt to contact it when they start. Instead the
Visualiser must contact the agents and ask them to inform it of what they
are doing, this involves the following:

· Launch the Visualiser's Society Viewer by clicking its icon on the
  Visualiser Hub. This opens a window with icons for each of the agents. Then
	click on the Dell agent's icon and select the "Centered" button on the
	layout panel. This should arrange the agents in the middle of the window
  around the Dell agent.

· To observe how the agents interact with each other select the "Request
  Messages" option from the "Online" menu in the menubar. This will open a
	dialog enabling you to choose which agents' messages are displayed; choose
  'select all' and then 'ok'. Agents will now forward copies of their
	messages to the Society Viewer when they communicate.

· Now launch the Visualiser's Reports Tool by clicking its icon on the
  Visualiser Hub. Just as you did with the Society Viewer you will have to
	register the Reports Tool so it is kept informed of the agents' actions.
	The same procedure is followed, select the "Request Reports" option from
	the "Reports" menu and then select all the agents in the dialog window.

Now watch the Society Viewer as the Visualiser agent sends "subscribe"
messages to the chosen agents.

· Then launch the Visualiser's Statistics Tool by clicking its icon on the
  Visualiser Hub and repeat the same "Request Messages" procedure, selecting
	all the agents that are shown in the dialog window.

Again the Society Viewer will show the exchange of messages as the Visualiser
sends "subscribe" requests to the chosen agents.  With the Visualiser now
ready to depict several aspects of the application, we can begin the
simulation, this involves the following:

· Open (or bring to the front) the DellPump window, this is the small one
  entitled "DellPump" that consists of a button labelled 'Next'. Pressing
	the DellPump button instructs the Dell agent to build another PC.

As this activity requires resources that it does not possess locally,
(namely a Monitor, a Motherboard and a Printer), the Dell agent will need to
find the appropriate suppliers. Hence the first messages you will have seen
transmitted are from Dell to the Facilitator, which replies back again. Here
the Facilitator (a Yellow Pages type service) is being asked about who supply
particular resources.

Next you will see the agents suggested by the Facilitator being contacted
directly by Dell. Note how communication is achieved on a peer-to-peer basis
rather than being centralised through the Facilitator (which is, after all,
just playing the role of an index).

· The flurry of messages between all the agents indicates that negotiation
  for the resources is occuring. For instance, open the Agent Viewer window
	of the Dell agent and click on the "Incoming Mail" icon. This displays the
	messages received by the agent as they arrival, notice how they all concern
	proposals related to supply of a particular resource.

· Still in the agent viewer, click on the "Planner and Scheduler". This
  component serves as the agent's diary with a series of time slots during
  which the agent can schedule the actual manufacture of the PC. One slot
  will be coloured yellow, indicating that the agent is tentatively planning
  to produce the PC by then.

If the yellow slot in the plan diary disappears the agent will have cancelled
its plan to manufacture the PC; i.e. something will have gone wrong. This may
be due to the necessary suppliers not being active, or requesting too much
money (the prices are slightly randomised).  If this happens, press the
DellPump button again. If it repeatedly happens there is probably a problem
with one or more of the agents, use the "Close Agents" option from the Control
Tool and restart the application.

Would should happen is that the diary slot will turn from yellow to blue,
indicating that all the resources necessary have been booked and production
is now scheduled to proceed.

· To monitor the progress of the PC production open (or bring to the front)
  the Reports Tool you opened earlier.

There should be an entry in the "Current Tasks" list on the left hand side.
Select it if it is not already highlighted, this will display the chain of
tasks, their preconditions and effects, which are necessary for the top-level
task to be completed (in this case MakeComputer).

One notable aspect of the Report view is the changing task states. Each will
start in the 'Firm' state (cyan), representing that the task has been
scheduled to be performed. When the time comes to perform the task its
state will change to 'Running' (green). This represents the time during which
production would actually occur. When this is complete the task will move
into the 'Completed' state (white), whereupon it can be used as the
precondition for the next task in the chain.

· To obtain information on the attributes of a task, (who owns it, how much
  it costs to invoke etc.) double click on the node in the Report View.

Whilst the tasks are executing some interesting statistics can be obtained
through the Statistics tool.

· Bring its window to the front and select the "Inter Agent Negotiation
  Graphs" option from the Statistics menu. A dialog will now open, with a
  goal on the right hand side and pairs of agent names on the left hand
	side. Select an entry from each side and then the OK button.

· A graph	will now be displayed, with time on the X-axis and price on the
  Y-axis.  Two lines of different colours represent the negotiation histories
	of both agents, and as time passes you will see the fluctuations in price.

· Try viewing the other statistics and experimenting with the different
  display formats. You should see the statistics change in real-time as
  data accumulates.

Another perspective on the production process is available through the Agent
Viewer. For instance:

· Clicking on "Incoming Mail" will show the messages that have been received
  confirming the supply of the necessary resources.

· Any actions performed by the agent (known as goals) can be seen by
  inspecting the Co-ordination Engine. This is the finite state machine
  that determines the agents behaviour and interactions with other agents.

Each agent also contains several 'databases' that hold information it will
use at run-time. For instance, browse through the Task and Ontology databases.
Of particular interest is the Resource Database, especially after the
MakeComputer task has completed, as it will contain a reference to the newly
created resource.

When you have finished with the agents and want to end the application open
the Visualiser's Control Tool and click on the Bomb icon, this will send a
message to each of the agents instructing them to terminate. This is the
quickest and cleanest way of ending ZEUS applications.



Where to Learn More
-------------------

* The PC Manufacture Case Study document describes how this application was
  built, how aspects of it can be modified, and how the application specific
  parts interact with the generic toolkit components.

* The Technical Manual explains how the agent components function, the role
  of utility agents and how aspects like communication and negotiation are
  supported.

* The online help documentation describes how to use the various facilities
  of the Visualiser tool.


--
Questions and Bug Reports concerning the PC Manufacture demo
should be sent to Jaron Collis <jaron@info.bt.co.uk>
------------------------------------------------------------













