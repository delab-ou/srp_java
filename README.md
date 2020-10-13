This repository is a source code in Java for secure routing protocols,
ISDSR[1], SRDP[2], and SRP.

All of them need the mcl elliptic curve library(https://github.com/herumi/mcl) and
the Gson library to read the config.json.

Some experiments were conducted on Mininet-wifi(https://github.com/intrig-unicamp/mininet-wifi).
After installing the Mininet-wifi, we run the python script "python/location.py" as a command below,

sudo python python/location -w:4 -h:2 -p:isdsr_mcl_bn254

This means the python script generates an envirnment which include 8 nodes and nodes are placed the width 4 x the height 2 grid form.
Then the script runs Mininet-wifi and generate a file "cmd.txt".

After running Mininet-wifi, we execute a command

mininet-wifi>source cmd.txt 

mininet-wifi>xterm sta1

In the console of the sta1, first, we set an environment value for the mcl library.
Then we execute route establishment processes of the target secure routing protocol.

export LD_LIBRARY_PATH=(a directory including the mcl library)

java -cp ./mcl.jar:gson-2.8.6.jar:./bin ou.ist.de.protocol.Main -dest:10.0.0.4 -config-protoid:101 -default-setting:on

The value of "-config-protoid" is described in the file "config.txt".
"101" means using isdsr with bn254 elliptic curves' parameters in the mcl library.
