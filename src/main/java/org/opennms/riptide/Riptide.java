package org.opennms.riptide;

import java.io.IOException;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.opennms.riptide.args.InetSocketAddressOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class Riptide {
    private final static Logger LOG = LoggerFactory.getLogger(Riptide.class);

    public static class CmdLine {
        @Argument(index = 0, metaVar = "SCRIPT", required = true)
        private Path script;

        @Argument(index = 1, metaVar = "HOST:PORT", multiValued = true, handler = InetSocketAddressOptionHandler.class)
        private List<InetSocketAddress> targets = Lists.newArrayList();

        @Option(name = "-seed", metaVar = "NUMBER")
        private Long seed;
    }

    private final Path script;
    private final List<InetSocketAddress> targets;

    private final Random random;

    private final List<Flow> flows;

    public Riptide(final CmdLine cmdLine) {
        this.script = cmdLine.script;
        this.targets = cmdLine.targets;

        if (cmdLine.seed != null) {
            this.random = new Random(cmdLine.seed);
        } else {
            long seed = new Random().nextLong();

            LOG.info("Using random seed: {}", seed);
            this.random = new Random(seed);
        }

        this.flows = Lists.newArrayList();
    }

    private void run() {
        final Binding binding = new Binding();
        binding.setVariable("riptide", new Context(
                this.flows,
                this.random));

        final GroovyShell shell = new GroovyShell(binding);

        // Execute the script
        try (final Reader r = Files.newBufferedReader(this.script)) {
            shell.evaluate(r);
        } catch (final IOException e) {
            LOG.error("Failed to read script: " + this.script, e);
        }

        // Send out the flows
        for (final Flow flow : this.flows) {
            final byte[] buffer = flow.write().array();

            if (this.targets.isEmpty()) {
                LOG.debug("Would send packet ({} bytes)", buffer.length);

            } else {
                try {
                    final DatagramSocket datagramSocket = new DatagramSocket();
                    for (final InetSocketAddress addr : this.targets) {
                        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr);

                        LOG.debug("Sending packet to {} ({} bytes)", addr, packet.getLength());
                        datagramSocket.send(packet);
                    }
                } catch (final IOException e) {
                    LOG.error("Failed to send packet", e);
                }
            }
        }
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
        }

        final Riptide app = new Riptide(cmdLine);
        app.run();
    }
}
