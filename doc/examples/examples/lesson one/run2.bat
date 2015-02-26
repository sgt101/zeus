REM This script runs the task agents
start /min java InfoSupplier -o lesson1.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Displayer -o lesson1.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e DisplayerGui
