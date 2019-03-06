package com.github.mikesafonov.starter.smpp;

import com.github.mikesafonov.starter.clients.AlwaysSuccessSmppResultGenerator;
import com.github.mikesafonov.starter.clients.MockSenderClient;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.smpp.dto.CancelMessage;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageType;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.starter.smpp.util.Randomizer.randomString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class MockSenderClientTest {

    @Test
    void shouldThrowNPE() {

        assertThrows(NullPointerException.class, () -> new MockSenderClient(null, randomString()));
        assertThrows(NullPointerException.class, () -> new MockSenderClient(new AlwaysSuccessSmppResultGenerator(), null));
    }

    @Test
    void shouldContainExpectedId() {
        String id = randomString();
        MockSenderClient senderClient = new MockSenderClient(new AlwaysSuccessSmppResultGenerator(), id);

        assertEquals(id, senderClient.getId());
    }

    @Test
    void shouldCallGenerator() {
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        String id = randomString();
        MockSenderClient senderClient = new MockSenderClient(smppResultGenerator, id);

        Message message = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        senderClient.send(message);

        verify(smppResultGenerator, times(1)).generate(id, message);
    }

    @Test
    void shouldCallGeneratorForCancel() {
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        String id = randomString();
        MockSenderClient senderClient = new MockSenderClient(smppResultGenerator, id);

        CancelMessage message = new CancelMessage(randomString(), randomString(), randomString());
        senderClient.cancel(message);

        verify(smppResultGenerator, times(1)).generate(id, message);
    }
}
