REM This script runs the task agents
start /min java testForward -o test.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java zeus.actors.fipa.ACC.ACC -s dns.db -o null 