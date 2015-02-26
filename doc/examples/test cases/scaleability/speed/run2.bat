REM This script runs the task agents
start /min java Target -o .\messageSpeedTest.ont -s dns.db -e MessageSpeedTesterTarget -gui zeus.agentviewer.AgentViewer
start /min java Source -o .\messageSpeedTest.ont -s dns.db -e MessageSpeedTesterSource -gui zeus.agentviewer.AgentViewer
