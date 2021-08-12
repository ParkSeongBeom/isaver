package com.icent.isaver.core.component;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

/**
 * The type MQTT socket svc.
 */
@Slf4j
public class MqttCore implements MqttCallbackExtended {
    @Value("${mqtt.clientId}")
    private String clientId;
    @Value("${mqtt.domain}")
    private String domain;
    @Value("${mqtt.port}")
    private Integer port;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;

    private static MqttAsyncClient Client;
    private static MqttMessage message;
    private static MemoryPersistence persistence;
    private static MqttConnectOptions connOpts;
    private Boolean isConnect = false;
    private final List<String> topicList = Lists.newArrayList();

    public void addTopic(String[] topicArr){
        topicList.addAll(Arrays.asList(topicArr));
    }

    public void addTopic(String topic){
        topicList.add(topic);
    }

    public void connect(){
        try {
            persistence = new MemoryPersistence();
            connOpts = new MqttConnectOptions();
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            connOpts.setKeepAliveInterval(10);
            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(3000);
//            connOpts.setAutomaticReconnect(true);
            message = new MqttMessage();
            setMqttAsyncClient();
            initServerConnectCheck();
        } catch(MqttException me) {
            log.error("[MqttCore] Connect Failure reason : {}, msg : {}, loc : {}, cause : {}, excep : {}",me.getReasonCode(),me.getMessage(),me.getLocalizedMessage(),me.getCause(),me);
        } catch(Exception e){
            log.error("[MqttCore] Connect Failure reason : {}",e.getMessage());
        }
    }

    private void setMqttAsyncClient(){
        try {
            Client = new MqttAsyncClient("tcp://"+domain+":"+port, clientId, persistence);
            Client.setCallback(this);

            InetAddress address = InetAddress.getByName(domain);
            log.info("[MqttCore] setMqttAsyncClient - {}:{} (IP:{})",domain,port,address.getHostAddress());
        } catch(Exception e){
            log.error("[MqttCore] getClient Failure reason : {}",e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        this.disconnect();
    }

    /**
     * mqtt server connect check.
     *
     * @throws Exception the exception
     */
    public void initServerConnectCheck() throws Exception {
        Thread thread = new Thread() {
            public void run() {
                boolean initFlag = false;
                int reConnectCount = 0;
                while (true) {
                    if(!Client.isConnected() && !isConnect){
                        try {
                            if (initFlag) {
                                if(reConnectCount>4){
                                    Client.disconnect();
                                    Client.close();
                                    setMqttAsyncClient();
                                    reConnectCount = 0;
                                    log.info("[MqttCore] New Client Connecting to broker - {}:{}",domain,port);
                                    Client.connect(connOpts);
                                }else{
                                    reConnectCount++;
                                    log.info("[MqttCore] Connecting retry({}) to broker - {}:{}",reConnectCount,domain,port);
                                    Client.reconnect();
                                }
                            }else{
                                log.info("[MqttCore] init Connecting to broker - {}:{}",domain,port);
                                Client.connect(connOpts);
                            }
                        } catch (MqttException e) {
                            if(e.getReasonCode()!=32110){ // 이미 연결 진행중
                                log.error(e.getMessage());
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                        initFlag = true;
                    }else{
                        reConnectCount = 0;
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        };
        thread.setName("Mqtt HeartBeat Thread");
        thread.start();
    }

    public void disconnect(){
        try {
            Client.disconnect();
            Client.close();
            isConnect = false;
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void publish(String topic, String msg, int qos){
        message.setQos(qos);
        message.setPayload(msg.getBytes());

        try {
            Client.publish(topic, message);
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void subscribe(String topic, int qos){
        try {
            Client.subscribe(topic,qos);
            log.info("[MqttCore][{}] subscribe",topic);
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.info("[MqttCore][{}] message arrived - {}",topic,mqttMessage.getPayload());
    }

    @Override
    public void connectionLost(Throwable cause) {
        isConnect = false;
        log.info("[MqttCore] Lost Connection cause : {}", cause.getMessage());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        try {
            log.debug("[MqttCore]{} Message delivered. - {}", iMqttDeliveryToken.getTopics(), iMqttDeliveryToken.getMessage());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        isConnect = true;
        log.info("[MqttCore] Connected : {}",serverURI);
        for(String topic : topicList){
            this.subscribe(topic,0);
        }
    }
}
