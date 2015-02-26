REM This script runs the non-root servers and other utility agents
start /min java zeus.agents.Facilitator Facilitator4 -o test.ont -s dns.db -t 1.0
start /min java zeus.visualiser.Visualiser Visualiser4 -s dns.db -o test.ont -quick
