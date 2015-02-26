REM This script runs the task agents
start /min java  Agent1 -o .\test.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Agent0 -o .\test.ont -s dns.db -gui zeus.agentviewer.AgentViewer
