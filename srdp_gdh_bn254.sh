#!/bin/bash
export LD_LIBRARY_PATH=~/lib:$LD_LIBRARY_PATH
java -cp ./bin:./gson-2.8.6.jar:./mcl.jar \
ou.ist.de.protocol.Main \
-default-setting:on \
-config-protoid:202
