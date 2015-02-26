REM This script runs the non-root servers and other utility agents
start /min java zeus.agents.Facilitator Broker -o .\fruit.ont -s dns.db -t 0.0
start /min java zeus.visualiser.Visualiser Visual -s dns.db -o .\fruit.ont -quick
start /min java zeus.agents.DbProxy DbProxy0 -p zeus.ext.FlatFile -s dns.db
