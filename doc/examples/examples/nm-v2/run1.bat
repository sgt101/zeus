REM This script runs the root servers and should be started first
start /min java zeus.agents.ANServer Nameserver0 -t 1.0 -f dns.db
