package datn.service.parking.config;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.util.UUID;

@Slf4j
@Configuration
@IntegrationComponentScan
public class MQTTConfig {
    // tạo ra nhiều topic cho các chức năng khác nhau
    private static final String MQTT_BROKER_URL = "tcp://test.mosquitto.org:1883"; // Đổi theo URL broker của bạn
    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static final String TOPIC = "handle";
    private static final String CHECKIN_TOPIC = "checkin";
    private static final String CHECKOUT_TOPIC = "CameraReceiver_checkout";
//    private static final String USER_NAME = "admin";
//    private static final String PASSWD = "12345678";
//$7$101$K/i02LCF2zFujYQK$x8C02EPNKBMkr3x7NF0OhODUbxmN0xQhnl+wLL3dcqhL7iPPxS4+aUv6FXBtN1ecTm6BawJyjJcTMlGGJRqdoA==

    @Bean
    public DefaultMqttPahoClientFactory clientFactory(){
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{MQTT_BROKER_URL});
//        options.setUserName(USER_NAME);
//        options.setPassword(PASSWD.toCharArray());
        options.setCleanSession(true);
        factory.setConnectionOptions(options);

        return factory;
    }
    // Cấu hình kênh để nhận tin nhắn từ MQTT
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }
    // Adapter để kết nối và nhận tin nhắn từ MQTT
    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInboundCheckin(MqttPahoClientFactory mqttPahoClientFactory){
//        String clientId = UUID.randomUUID().toString();
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(CLIENT_ID,mqttPahoClientFactory,CHECKIN_TOPIC);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInboundCheckout(MqttPahoClientFactory mqttPahoClientFactory){
//        String clientId = UUID.randomUUID().toString();
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(CLIENT_ID,mqttPahoClientFactory,CHECKOUT_TOPIC);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


    // XỬ LÝ MESSAGE KHI NHẬN ĐƯỢC
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                log.info(("Received message: "+ message.getPayload()));

            }
        };

    }
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    // Kênh gửi tin nhắn qua MQTT

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler handler =
                new MqttPahoMessageHandler(CLIENT_ID, mqttClientFactory);
        handler.setAsync(true);
        handler.setDefaultTopic(TOPIC);
        return handler;
    }



}
