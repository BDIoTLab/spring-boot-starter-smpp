package com.github.mikesafonov.starter.smpp.reciever;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.github.mikesafonov.starter.smpp.config.ReceiverConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Mike Safonov
 */
@Slf4j
public class DefaultResponseClient implements ResponseClient {

    private static final String SESSION_SUCCESS_MESSAGE = "SESSION SUCCESSFUL REBINDED";

    /**
     * Delivery report handler
     */
    private ResponseSmppSessionHandler sessionHandler;
    /**
     * SMPP receiver configuration
     */
    private final ReceiverConfiguration sessionConfiguration;
    /**
     * SMPP client
     */
    private final SmppClient client;
    /**
     * Scheduled reconnection timer
     */
    private ScheduledExecutorService timer;
    /**
     * ScheduledFuture for {@link ResponseClientRebindTask}.
     */
    private ScheduledFuture<?> rebindTask;
    /**
     * reconnection period in seconds
     */
    private final long rebindPeriod;
    /**
     * SMPP session.
     */
    private SmppSession session;

    private volatile boolean inProcess = false;

    /**
     * Create {@link DefaultResponseClient} with {@link DefaultSmppClient}.
     *
     * @param receiverConfiguration smpp receiver configuration
     * @param rebindPeriod          reconnection period in seconds
     */
    protected DefaultResponseClient(@NotNull ReceiverConfiguration receiverConfiguration, long rebindPeriod) {
        client = new DefaultSmppClient();
        this.sessionConfiguration = receiverConfiguration;
        this.rebindPeriod = rebindPeriod;
    }

    /**
     * Create {@link DefaultResponseClient} with {@link DefaultSmppClient}.
     *
     * @param receiverConfiguration smpp receiver configuration
     * @param rebindPeriod          reconnection period in seconds
     */
    public static DefaultResponseClient of(@NotNull ReceiverConfiguration receiverConfiguration, long rebindPeriod) {
        return new DefaultResponseClient(receiverConfiguration, rebindPeriod);
    }

    @Override
    public void setup(ResponseSmppSessionHandler sessionHandler) throws ResponseClientBindException {
        this.sessionHandler = sessionHandler;
        bind();
        if (session == null) {
            throw new ResponseClientBindException(String.format("Unable to bind with configuration: %s ", sessionConfiguration.configInformation()));
        }
        setupRebindTask();
    }

    /**
     * Closing session by {@link #closeSession()} and bind again by {@link #bind()}
     */
    @Override
    public void reconnect() {
        closeSession();
        bind();
    }

    /**
     * Destroying {@link DefaultResponseClient}. Closing session by {@link #closeSession()},
     * destroying smpp client and shutdown {@link #timer}
     */
    @Override
    public void destroyClient() {
        closeSession();
        interruptIfNotNull();
        client.destroy();
        timer.shutdown();
    }

    @Override
    public SmppSession getSession() {
        return session;
    }

    @Override
    public boolean isInProcess() {
        return inProcess;
    }

    @Override
    public void setInProcess(boolean inProcess) {
        this.inProcess = inProcess;
    }

    /**
     * Creting SMPP session and connecting to SMSC.
     *
     * @see SmppClient#bind(SmppSessionConfiguration, SmppSessionHandler)
     */
    private void bind() {
        try {
            session = client.bind(sessionConfiguration, sessionHandler);
            log.debug(SESSION_SUCCESS_MESSAGE);
        } catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            session = null;
        }
    }

    /**
     * Creating running {@link ResponseClientRebindTask} on single thread {@link ScheduledExecutorService}
     *
     * @see Executors#newSingleThreadScheduledExecutor()
     */
    private void setupRebindTask() {
        if (timer == null) {
            timer = Executors.newSingleThreadScheduledExecutor();
        }
        interruptIfNotNull();
        rebindTask = timer.scheduleAtFixedRate(new ResponseClientRebindTask(this),
                5, rebindPeriod, TimeUnit.SECONDS);
    }

    /**
     * Interrupts {@link #rebindTask} if it not null
     */
    private void interruptIfNotNull() {
        if (rebindTask != null) {
            rebindTask.cancel(true);
        }
    }

    /**
     * Closing {@link SmppSession}.
     * Calling {@link SmppSession#close()} and {@link SmppSession#destroy()} if {@link #session} not null
     */
    private void closeSession() {
        if (session != null) {
            session.close();
            session.destroy();
            session = null;
        }
    }

}
