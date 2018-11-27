Riptide - Netflow v5 generator
==============================

Riptide generates Netflow v5 packets for simulated TCP sessions.


## Compile and Install
Run the following command to build the tool:
```
mvn package
```


## Usage
To start, the following command can be used:
```
java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug \
     -jar target/org.opennms.riptide-1.0-SNAPSHOT-jar-with-dependencies.jar \
     test.yaml \
     1.2.3.4:1234
```

The following options and arguments are allowed:

`FILE`
: The file containing the TCP session definition

`HOST:PORT`
: Host and port of the Netflow receiver - can be specified multiple times

`-flush-interval SECS`
: The interval used to flush out Netflow packets

`-dry-run`
: Do not send Packets

`-source HOST:PORT`
: Spoofs the source address of the Netflow packets


## Source address spoofing
To spoof the source address of outgoing Netflow packets, [RockSaw](https://github.com/mlaccetti/rocksaw) must be installed.
After building RockSaw from source, the following Parameter must be added to the `java` call:
```
-Djava.library.path=/path/to/resulting/library
``` 

In addition, the tool must be run as `root` user.


## TCP Session Definition
The simulated TCP session is described by a `yaml` file.
See `test.yaml` for an example.
