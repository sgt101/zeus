REM This script runs the root servers and should be started first
start /min tnameserv -ORBInitialPort 2000
start /min tnameserv -ORBInitialPort 1097
start /min java zeus.agents.ACC -s dns.db -o null -transports config.txt
