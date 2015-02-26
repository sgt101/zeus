REM This script runs the task agents
start /min java Target -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterTarget -name 0
start /min java Target -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterTarget -name 1
start /min java Target -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterTarget -name 2
start /min java Target -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterTarget -name 3
start /min java Target -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterTarget -name 4

start /min java Source -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterSource -name 0
start /min java Source -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterSource -name 1
start /min java Source -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterSource -name 2
start /min java Source -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterSource -name 3
start /min java Source -o .\messageSpeedTest.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e MessageSpeedTesterSource -name 4
