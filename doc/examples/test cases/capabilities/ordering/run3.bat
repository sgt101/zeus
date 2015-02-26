REM This script runs the non-root servers and other utility agents
start /min java zeus.agents.Facilitator Facilitator0 -o .\essai4.ont -s dns.db -t 5.0
start /min java zeus.visualiser.Visualiser Visualiser0 -s dns.db -o .\essai4.ont -quick
