REM This script runs the task agents
start "PC MAKER" /min java PCMaker -o .\pc.ont -s dns.db -gui zeus.agentviewer.AgentViewer -e AutoPump
start "MONITOR MAKER" /min java MonitorMaker -o .\pc.ont -s dns.db
start "MOTHERBOARD MAKER" /min java MotherBoardMaker -o .\pc.ont -s dns.db
start "PRINTER MAKER" /min java PrinterMaker -o .\pc.ont -s dns.db
