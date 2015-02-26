REM This script runs the task agents
start /min java UIA -o .\nm.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e UIA_Interface
start /min java FSM -o .\nm.ont -s dns.db
start /min java FTM -o .\nm.ont -s dns.db
start /min java FAA -o .\nm.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java VCW -o .\nm.ont -s dns.db
