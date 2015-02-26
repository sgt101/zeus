                       The ZEUS Agent Building Toolkit
                         Version 1.01 September 1999

                          -------------------------
                          The Multi-Agent Maze Demo
                          -------------------------

                  Created by Simon Thompson and Jaron Collis


The Multi-Agent Maze Demo (a.k.a 'multimaze') is an example of a Shared
Information Space that has several Subscriber agents. In this application
there are 3 subscribers, called Red, Green and Blue, which are trapped in
a maze and must find their way to the exit. Whilst the Environment agent
holds the representation of the maze, and answers questions about it.

The Multimaze demo differs from the example in the 'mazedemolite' directory,
(which has only one Subscriber), in that the former has more features, and has
been more extensively tested. Hence we would recommend using the Multimaze
example as a basis for your own modifications, rather than the 'mazedemolite'
version.


Getting Started
---------------

Assuming that all the agents will be running on the same machine, launching
the application will involve the following process:

· If you haven't already, open a command line/shell and cd into the
  zeus/examples/multimaze directory

· Enter the command 'run1', this will execute the run1 script and start the
  Agent Name Server (ANS).

Unix users may need to change the permissions of the run scripts to execute
them, especially if they have been copied across from a non-Unix (i.e. DOS)
filing system, e.g. 'chmod 777 run*'

No errors should be reported, and a new Java interpreter process should start
running in the background.  If a problem has occurred, check your system's
configuration (e.g. CLASSPATH setting, install directory in the .zeus.prp
file etc.)

· If you want to use the facilities of the Visualiser enter the command 'run2'
  - this is optional however, and you find it is best not to run the Visualiser
  if you have less than 64mb of memory on the host machine.

· Enter the command 'run3', this starts the Red, Green, Blue and Environment
  agents, resulting in four additional Java processes running in the
  background.  Also appearing on screen should be Agent Viewer windows for
  each agent, a window containing a maze and 3 smaller status windows for the
  Red, Green and Blue agents.

Once launched, the Subscriber agents will register themselves with the Name
Server, their default start-up behaviour. Contacting the maze Environment
agent is not part of their default behaviour, and so they will each need to
be instructed to do so.

· If you have a Visualiser agent active and would like to see how the agents
  interact visually, launch its Society Viewer. Then select the "Request
  Messages" option from the "Online" menu, then choose the 'select all' option
  in the dialog that appears and click 'ok'. All agents will now forward
  copies of any messages sent to the Society Viewer for visualisation.

Now instruct the Red, Green and Blue agents to register with the maze
Environment by clicking on the "Register" buttons contained within their
individual status windows.

· If you have a Society Viewer open you will see the registering agent sending
  "inform" messages to the Environment agent, and receiving a reply.

· Alternatively if you look at the Agent Viewer windows of the registering
  agent or the Environment you can see the messages involved by looking at
  the In-Box and Out-Box components. You will also be able to see that the
  Rule Engine has fired the rules relating to the registration process.

· Another informative component within each agent is the Resource Database,
  this contains the facts that will trigger the invocation of rules.

Once the agents have registered the Start button of the maze Environment
agent will become active. Once this is clicked the Environment will send a
message to each registered agent notifying them of the obstacles nearby.

On receiving an obstacle notification message rules in each Subscriber agent
will fire determining what move to make in response. This move is then sent
back to the Environment agent, which updates the agent's position in the maze
and notifies it of the obstacles at its new location.

This process continues until an agent reaches the maze exit (shown as the
yellow square in the maze display).

When you have finished with the agents and want to end the application you
can either open the Visualiser's Control Tool and click on the Bomb icon, or
close each agent window manually.



Where to Learn More
-------------------

* The Maze Demo Case Study document describes how this application was
  built, how aspects of it can be modified, and how the application specific
  parts interact with the generic toolkit components.

* The Technical Manual explains how the agent components function, the role
  of utility agents and how the communication mechanism functions.

* The online help documentation describes how to use the various facilities
  of the Visualiser tool.


--
Please send any questions and bug reports concerning this
example to zeus@mailbase.ac.uk
---------------------------------------------------------













