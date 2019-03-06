package com.github.mikesafonov.starter.clients;


import com.github.mikesafonov.starter.smpp.dto.CancelMessage;
import com.github.mikesafonov.starter.smpp.dto.CancelMessageResponse;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Implementation of {@link SmppResultGenerator} which always generate success {@link MessageResponse}/{@link CancelMessageResponse} with
 * random smsc message id.
 *
 * @author Mike Safonov
 */
public class AlwaysSuccessSmppResultGenerator implements SmppResultGenerator {

    @Override
    public MessageResponse generate(String smscId, Message message) {
        return MessageResponse.success(message, smscId, randomHexId());
    }

    @Override
    public @NotNull CancelMessageResponse generate(@NotNull String smscId, @NotNull CancelMessage cancelMessage) {
        return CancelMessageResponse.success(cancelMessage, smscId);
    }


    private String randomHexId() {
        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return Long.toHexString(timestamp);
    }
}
