REM This script runs the task agents
start /min java Agent1 -o .\subsuper.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Agent0 -o .\subsuper.ont -s dns.db -gui zeus.agentviewer.AgentViewer
