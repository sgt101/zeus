REM This script runs the task agents
start /min java ScoobyAgent -o .\nm.ont -s dns.db -e NE_ExternalDbInterface
start /min java BugsAgent -o .\nm.ont -s dns.db -e NE_ExternalDbInterface
start /min java ShaggyAgent -o .\nm.ont -s dns.db -e NE_ExternalDbInterface
start /min java RenAgent -o .\nm.ont -s dns.db -e NE_ExternalDbInterface
start /min java UIA -o .\nm.ont -s dns.db -e UIA_Interface
start /min java FSM -o .\nm.ont -s dns.db
start /min java FTM -o .\nm.ont -s dns.db
start /min java FAA -o .\nm.ont -s dns.db
start /min java VCW -o .\nm.ont -s dns.db
