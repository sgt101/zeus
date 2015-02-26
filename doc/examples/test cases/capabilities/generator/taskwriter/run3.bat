REM This script runs the non-root servers and other utility agents
start /min java zeus.agents.ANServer Nameserver3 -s dns.db
start /min java zeus.agents.Facilitator Facilitator3 -o .\test2.ont -s dns.db -t 5.0
start /min java zeus.visualiser.Visualiser Visualiser3 -s dns.db -o .\test2.ont -quick
