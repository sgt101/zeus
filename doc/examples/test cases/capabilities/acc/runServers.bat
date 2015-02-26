REM This script runs the root servers and should be started first
start /min java zeus.agents.ANServer Nameserver4 -t 0.5 -f dns.db
start /min tnameserv
