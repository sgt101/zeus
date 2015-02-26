REM This script runs the task agents
start /min java Agent6 -o .\show.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Agent5 -o .\show.ont -s dns.db
start /min java Agent4 -o .\show.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Agent3 -o .\show.ont -s dns.db
start /min java Agent2 -o .\show.ont -s dns.db
start /min java Agent1 -o .\show.ont -s dns.db
start /min java Agent0 -o .\show.ont -s dns.db
start /min java Agent9 -o .\show.ont -s dns.db
start /min java Agent8 -o .\show.ont -s dns.db
start /min java Agent7 -o .\show.ont -s dns.db
