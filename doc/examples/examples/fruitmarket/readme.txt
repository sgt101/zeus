                       The ZEUS Agent Building Toolkit
                             Version 1.0 May 1999

                             --------------------
                             The FruitMarket Demo
                             --------------------

The FruitMarket demo is an example of simple marketplace application where
people can use their agents to participate in the virtual trade of several
kinds of tasty fruit.

The marketplace can consist of an arbitrary number of agents, but for
simplicity the demo has been configured to have just 3 traders, called
OrchardBot, SupplyBot and ShopBot. There are also 3 utility agents, a Name
Server, a Broker, and a Visualiser, their roles are described in the ZEUS
Technical Manual.


Getting Started
---------------

Assuming that all the agents will be running on the same machine, launching
the application will involve the following process:

· If you haven't already, open a command line/shell and cd into the
  zeus/examples/fruitmarket directory

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

· Enter the command 'run3', this will start the OrchardBot, SupplyBot and
  ShopBot agents, as a result three more Java processes should now be running
	in the background.  Whilst on screen an Agent Viewer window and a Trading
	GUI window will appear for each agent.

Once launched the FruitMarket agents will display their front-end GUIs and
await user instructions; they will do nothing autonomously apart from
replying to register themselves with the Name Server and the Broker. But as
the agents can operate independently of the Visualiser they will not attempt
to contact it when they start. Instead the Visualiser must contact the agents
and ask them to inform it of what they are doing, this involves the following:

· Launch the Visualiser's Society Viewer by clicking its icon on the
  Visualiser Hub. This opens a window with icons for each of the agents. Then
	click on one of the agents and select on the "Centered" button on the layout
	panel. This should arrange the agents in the middle of the window.

· To observe how the agents interact with each other select the "Request
  Messages" option from the "Online" menu in the menubar. This will open a
	dialog enabling you to choose which agents' messages are displayed; choose
  'select all' and then 'ok'. Agents will now forward copies of their
	messages to the Society Viewer when they communicate.

· Now launch the Visualiser's Statistics Tool by clicking its icon on the
  Visualiser Hub. Just as you did with the Society Viewer you will have to
	register the Statistics Tool with the agents so that their messages are
	visualised. The same procedure is followed, select the "Request Messages"
	option from the "Online" menu and then select all the agents in the dialog
	window.

· Now watch the Society Viewer as the Visualiser agent sends "subscribe"
  messages to all the agents.

As this application contains no tasks, the Report Tool does not need to be
launched. With the Visualiser ready we can try trading an item of fruit.

· Open (or bring to the front) the OrchardBot window (this is the one with
  "OrchardBot" in its titlebar and a graphic of hands juggling some fruit).

· To offer an item for sale go to the "Sell Fruit" tab pane and click on the
  "Choose" button.  This will open a dialog displaying the application
	ontology, expand the entity node by clicking on it, then select the 'apple'
	node and then press the OK button. 'Apple' will now appear in the
	'commodity to trade' field.

· Next you can enter the reserve price (this is the lowest price you would be
  willing to accept for the apple). This value will be kept secret and used
	by the negotiation strategy to create an asking price, which will be the
	price quoted to potential buyers. The default value of 10 is adequate.

· The elapsed time before the sale should be completed can be entered in the
  Deadline field, this sets the period of time that potential buyers have to
	submit bids and negotiate. Use the default value of 3.

When you click on the Trade button the agent will notify the broker of the
item for sale, and thus any interested parties. Once offered for sale the
agent will wait for incoming bids until the deadline period expires,
whereupon the item will be withdrawn from sale. (How the agent actually
responds to incoming bids is described in the case study document).

So, with an item on offer we can instruct another agent to buy it.

· Open (or bring to the front) the ShopBot window (identified by "ShopBot"
  in its titlebar). Then go to the "Buy Fruit" tab pane and choose the
	'apple' concept just as you did with the selling agent.

· Next enter the highest price you would be willing to pay for this item in
  the Maximum Price field.  This value will be kept secret and used by the
	negotiation strategy to create a bid price, which will offered to the
	seller. Change the value here to 12.

· The elapsed time before the purchase should be completed can be entered in
  the Deadline field, this sets the period of time by which negotiation should
  have concluded. Use the default value of 3.

· Finally click on the Trade button; the agent will then consult the broker
  to find other agents offering the desired item for sale.

If the commodity to buy is currently on the market the broker will send the
interested bidder the identity of the seller, which it can contact directly
and begin negotiation. Otherwise the bidding agent will ask to be informed if
any appropriate items are placed on the market.  Hence the order in which
items are placed on the market is not significant - bidders can bid before
sellers offer and vice versa.

· Assuming the seller's deadline has not already passed a flurry of messages
  will be seen in the Society Viewer between the OrchardBot and ShopBot
	agents as they negotiate the terms of the trade.

· A more informative view is available through the Statistics tool. Bring its
  window to the front and select the "Inter Agent Negotiation Graphs" option
  from the Statistics menu.

· A dialog will now open, and assuming that negotiation has started you will
  see a goal on the right hand side and the two agent names on the left hand
	side. Select both entries and then the OK button.

· A graph	will now be displayed, with time on the X-axis and price on the
  Y-axis.  Two lines of different colours represent the negotiation histories
	of both agents, and as time passes you will see the fluctuations in price.

Another perspective on the negotiation is available through the Agent Viewer
windows. These are different to the front end windows, consisting of an
palette of icons on the left hand side and a blank area on the right. By
clicking on the associated icon the viewer provides a means of inspecting
the state of the each of the agent's internal components. For instance:

· Clicking on "Incoming Mail" shows the messages the agent has received.
  The type of message and its content should be immediately apparent.
  The same information is available for outgoing messages.  Whilst the
	"Message Handler" option shows incoming messages and the response the agent
	took to them.

· If the agent has performed an action (known as a goal) it can be seen by
  inspecting the Co-ordination Engine. This is the finite state machine
  that determines the agents behaviour and interactions with other agents.

Each agent also contains several 'databases' that hold information it will
use at run-time. For instance, look at the Resource and Ontology Databases.
Some, like the Acquaintance and Task databases are empty, as neither are
applicable in this application.

When you have finished with the agents and want to end the application open
the Visualiser's Control Tool and click on the Bomb icon, this will send a
message to each of the agents instructing them to terminate. This is the
quickest and cleanest way of ending ZEUS applications.



Where to Learn More
-------------------

* The FruitMarket Case Study document describes how this application was
  built, how aspects of it can be modified, and how the application specific
  parts interact with the generic toolkit components.

* The Technical Manual explains how the agent components function, the role
  of utility agents and how aspects like communication and negotiation are
  supported.

* The online help documentation describes how to use the various facilities
  of the Visualiser tool.


--
Questions and Bug Reports concerning the FruitMarket demo
should be sent to Jaron Collis <jaron@info.bt.co.uk>
---------------------------------------------------------













