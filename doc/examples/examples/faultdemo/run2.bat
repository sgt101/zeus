REM This script runs the task agents
start /min java WorldSimulator -o .\faultdemo.ont -s dns.db -e WorldSim
start /min java East -o .\faultdemo.ont -s dns.db
start /min java West -o .\faultdemo.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java South -o .\faultdemo.ont -s dns.db
start /min java North -o .\faultdemo.ont -s dns.db -gui zeus.agentviewer.AgentViewer
