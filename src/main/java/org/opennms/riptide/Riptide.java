package org.opennms.riptide;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.opennms.riptide.args.InetSocketAddressOptionHandler;
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

        @Argument(index = 1, metaVar = "HOST:PORT", multiValued = true, handler = InetSocketAddressOptionHandler.class, required = true)
        private List<InetSocketAddress> targets = Lists.newArrayList();

        @Option(name = "-flush-interval", metaVar = "SECS", required = false)
        private long flushInterval = 30;

        @Option(name = "-dry-run", required = false)
        private boolean dryRun = false;
    }

    private final Path file;
    private final List<InetSocketAddress> targets;
    private final Duration flushInterval;
    private final boolean dryRun;

    public Riptide(final CmdLine cmdLine) {
        this.file = cmdLine.file;
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
            sender = new Simulation.Sender() {
                @Override
                public void send(Flow flow) {
                    LOG.info("Sending flow {} ", flow);
                }
            };
        } else {
            final DatagramSocket datagramSocket;
            try {
                datagramSocket = new DatagramSocket();
            } catch (SocketException e) {
                LOG.error("Failed to initialize datagram socket", e);
                System.exit(1);
                return;
            }

            sender = new Simulation.Sender() {
                @Override
                public void send(Flow flow) {
                    try {
                        final byte[] buffer = flow.write().array();
                        for (final InetSocketAddress addr : Riptide.this.targets) {
                            final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr);

                            LOG.debug("Sending packet to {} ({} bytes)", addr, packet.getLength());
                            datagramSocket.send(packet);
                        }
                    } catch (final IOException e) {
                        LOG.error("Failed to send packet", e);
                    }
                }
            };
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
