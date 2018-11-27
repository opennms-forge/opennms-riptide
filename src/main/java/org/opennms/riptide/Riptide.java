package org.opennms.riptide;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.opennms.riptide.args.Inet4SockAddrOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;

public class Riptide {
    private final static Logger LOG = LoggerFactory.getLogger(Riptide.class);

    public static class CmdLine {
        @Argument(index = 0, metaVar = "FILE", required = true)
        private Path file;

        @Argument(index = 1, metaVar = "HOST:PORT", multiValued = true, handler = Inet4SockAddrOptionHandler.class, required = true)
        private List<Inet4SockAddr> targets = Lists.newArrayList();

        @Option(name = "-source", metaVar = "HOST:PORT", handler = Inet4SockAddrOptionHandler.class, required = false)
        private Inet4SockAddr source = null;

        @Option(name = "-flush-interval", metaVar = "SECS", required = false)
        private long flushInterval = 30;

        @Option(name = "-dry-run", required = false)
        private boolean dryRun = false;
    }

    private final Path file;
    private final Inet4SockAddr source;
    private final List<Inet4SockAddr> targets;
    private final Duration flushInterval;
    private final boolean dryRun;

    public Riptide(final CmdLine cmdLine) {
        this.file = cmdLine.file;
        this.source = cmdLine.source;
        this.targets = cmdLine.targets;
        this.flushInterval = Duration.ofSeconds(cmdLine.flushInterval);
        this.dryRun = cmdLine.dryRun;
    }

    private void run() {
        // TODO: Exception-Handling
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new JavaTimeModule());

        final TcpSession tcpSession;

        try {
            tcpSession = mapper.readValue(this.file.toFile(), TcpSession.class);
        } catch (final IOException e) {
            LOG.error("Failed to read file: " + this.file, e);
            System.exit(1);
            return;
        }

        final Simulation.Sender sender;
        if (dryRun) {
            sender = new DummySender();
        } else {
            try {
                if (this.source == null) {
                    sender = new SimpleSender(this.targets);
                } else {
                    sender = new SpoofingSender(this.source, this.targets);
                }
            } catch (IOException e) {
                LOG.error("Failed to initialize datagram socket", e);
                System.exit(1);
                return;
            }
        }

        new Simulation(tcpSession, this.flushInterval).simulate(sender);
    }

    public static void main(final String... args) {
        final CmdLine cmdLine = new CmdLine();
        final CmdLineParser parser = new CmdLineParser(cmdLine);
        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println();
            System.exit(1);
        }

        final Riptide app = new Riptide(cmdLine);
        app.run();
    }
}
