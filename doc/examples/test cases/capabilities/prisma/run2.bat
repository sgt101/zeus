REM This script runs the task agents
start /min java LoadForecastService -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Logger -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Grosshaendler -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java GAService -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Eon -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java RWE -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Boerse -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Veba -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java Bewag -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java BoFiTService -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
start /min java LoadGenerator -o .\Prisma.ont -s dns.db -gui zeus.agentviewer.AgentViewer
