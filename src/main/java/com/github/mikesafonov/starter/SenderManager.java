package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.SenderClient;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

/**
 * Holder class for sender clients.
 *
 * @author Mike Safonov
 */
public interface SenderManager {

    /**
     * Return sender client based on it name
     *
     * @param name name of sender client
     * @return sender client
     */
    Optional<SenderClient> getByName(@NotBlank String name);

    /**
     * @return next sender client
     */
    Optional<SenderClient> getClient();
}
