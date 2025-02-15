package datn.service.parking.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class MQTT {
    @Autowired
    private MessageChannel mqttOutboundChannel;

    public void sendMessagetoTopic(String payload, String topic) {
        mqttOutboundChannel.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("mqtt_topic", topic)
                        .build()
        );
    }
//    public void sendMessagetoCheckout(String payload, String topic) {
//        mqttOutboundChannel.send(
//                MessageBuilder.withPayload(payload)
//                        .setHeader("mqtt_topic", topic)
//                        .build()
//        );
//    }
}
