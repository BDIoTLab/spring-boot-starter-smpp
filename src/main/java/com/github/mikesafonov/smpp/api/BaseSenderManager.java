package com.github.mikesafonov.smpp.api;

import com.github.mikesafonov.smpp.config.SmscConnection;
import com.github.mikesafonov.smpp.core.sender.SenderClient;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of {@link SenderManager}
 *
 * @author Mike Safonov
 */
public abstract class BaseSenderManager implements SenderManager {
    protected final List<SmscConnection> smscConnections;

    protected BaseSenderManager(@NotNull List<SmscConnection> smscConnections) {
        this.smscConnections = requireNonNull(smscConnections);
    }

    @Override
    public SenderClient getByName(@NotBlank String name) {
        return smscConnections.stream()
                .filter(smscConnection -> smscConnection.getName().equals(name))
                .findFirst()
                .map(SmscConnection::getSenderClient)
                .orElseThrow(NoSenderClientException::new);
    }

    protected boolean isEmpty() {
        return smscConnections.isEmpty();
    }

    protected int size() {
        return smscConnections.size();
    }
}
