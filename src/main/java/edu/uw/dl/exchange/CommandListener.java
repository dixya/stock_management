/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.exchange;

import edu.uw.ext.framework.exchange.StockExchange;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newCachedThreadPool;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accepts command requests and dispatches them to a CommandHandler.
 *
 * @author dixya
 */
public final class CommandListener implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CommandListener.class);
    private int commandPort;
    private volatile boolean listening = true;
    private ServerSocket servSocket;
    private StockExchange realExchange;
    private ExecutorService requestExecutor = newCachedThreadPool();

    /**
     * Constructor.
     *
     * @param commandPort the port to listen for connections on
     * @param realExchange the "real" exchange to be used to execute the
     * commands
     */
    public CommandListener(int commandPort, StockExchange realExchange) {
        this.commandPort = commandPort;
        this.realExchange = realExchange;
        // requestExecutor=new cachedThreadPool();
    }

    /**
     * Accept connections, and create a CommandExecutor for dispatching the
     * command.
     */
    @Override
    public void run() {
        try {
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("Server is ready for accepting connections at port %d", commandPort));
            }
            servSocket = new ServerSocket(commandPort);
            while (listening) {
                Socket sock = null;
                try {
                    sock = servSocket.accept();
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Accepted connection at port %d", commandPort);
                    }
                } catch (final SocketException ex) {
                    if (servSocket != null && !servSocket.isClosed()) {
                        LOG.warn("Error accepting connection", ex);
                    }
                }
                if (sock == null) {
                    continue;
                }
                requestExecutor.execute(new CommandHandler(sock, realExchange));
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                terminate();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Terminates this thread gracefully.
     *
     * @throws java.io.IOException
     */
    public void terminate() throws IOException {
        listening = false;
        try {
            if (servSocket != null && servSocket.isClosed()) {
                LOG.info("Closing server socket");
                servSocket.close();
            }
            servSocket = null;
            if (!requestExecutor.isShutdown()) {
                requestExecutor.shutdown();
                requestExecutor.awaitTermination(1L, TimeUnit.SECONDS);
            }

        } catch (final InterruptedException iex) {
            LOG.info("Interrupted awaiting executor termination");
        }

    }

}
