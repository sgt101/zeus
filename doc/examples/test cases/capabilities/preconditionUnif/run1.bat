REM This script runs the root servers and should be started first
start /min java zeus.agents.ANServer Nameserver5 -t 0.3 -f dns.db
