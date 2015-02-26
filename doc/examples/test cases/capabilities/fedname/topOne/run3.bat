REM This script runs the non-root servers and other utility agents
start /min java zeus.agents.ANServer Nameserver0 -s ..\dns.db -f dns.db
start /min java zeus.agents.Facilitator Facilitator0 -o ..\bottom.ont -s dns.db -t 5.0
start /min java zeus.visualiser.Visualiser Visualiser0 -s dns.db -o ..\bottom.ont -quick
