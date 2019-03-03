package com.github.mikesafonov.starter.smpp.config;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.type.LoggingOptions;
import com.github.mikesafonov.starter.SmppProperties;

import javax.validation.constraints.NotNull;

/**
 * Configuration for sender session
 *
 * @author Mike Safonov
 */
public class TransmitterConfiguration extends SmppSessionConfiguration {

    public TransmitterConfiguration(@NotNull SmppProperties.SMSC smsc) {
        super();

        setType(SmppBindType.TRANSMITTER);
        setHost(smsc.getHost());
        setPort(smsc.getPort());
        setSystemId(smsc.getUsername());
        setPassword(smsc.getPassword());
        setWindowSize(smsc.getWindowSize());
        LoggingOptions loggingOptions = new LoggingOptions();
        loggingOptions.setLogBytes(smsc.isLoggingBytes());
        loggingOptions.setLogPdu(smsc.isLoggingPdu());
        setLoggingOptions(loggingOptions);
    }

    public String configInformation() {
        return String.format("%s host=%s port=%d username=%s windowsSize=%d", "Transmitter", getHost(), getPort(), getSystemId(), getWindowSize());
    }
}
