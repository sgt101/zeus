REM This script runs the task agents
start /min java Receiver -o .\sendRec.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Sender -o .\sendRec.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e SenderExternal
