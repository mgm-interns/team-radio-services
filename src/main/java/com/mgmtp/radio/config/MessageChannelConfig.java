package com.mgmtp.radio.config;

import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.service.station.StationOnlineService;
import com.mgmtp.radio.service.station.StationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration
public class MessageChannelConfig {

    private static final String TASK_EXECUTOR = "task-executor";

    @Bean(name = TASK_EXECUTOR)
    public TaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("Pub-Sub-");
        taskExecutor.setCorePoolSize(5);
        return taskExecutor;
    }

    @Bean
    MessageChannel registerChannel(@Qualifier(TASK_EXECUTOR) TaskExecutor taskExecutor) {
        return new PublishSubscribeChannel(taskExecutor);
    }

    @Bean
    MessageChannel forgotPasswordChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    MessageChannel resetPasswordChannel(@Qualifier(TASK_EXECUTOR) TaskExecutor taskExecutor) {
        return new PublishSubscribeChannel(taskExecutor);
    }

    @Bean
    MessageChannel historyChannel(@Qualifier(TASK_EXECUTOR) TaskExecutor taskExecutor){
        return MessageChannels.publishSubscribe(taskExecutor).get();
    }

    @Bean
    SubscribableChannel allStationChannel(){
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    MessageSource<Map<String, StationDTO>> allStationMessageSource(StationService stationService){
        return () -> MessageBuilder.withPayload(stationService.getOrderedStations()).build();
    }

    @Bean
    IntegrationFlow allStationFlow(StationService stationService){
        return IntegrationFlows
                .from(allStationMessageSource(stationService),
                    resourcePolling -> resourcePolling.poller(Pollers.fixedRate(5 * 1000).maxMessagesPerPoll(1)))
                .channel(allStationChannel())
                .get();
    }

    @Bean
    MessageChannel shiftSongChannel(@Qualifier(TASK_EXECUTOR) TaskExecutor taskExecutor){
        return new PublishSubscribeChannel(taskExecutor);
    }

    @Bean
    SubscribableChannel onlineUserOnlineChannel(){
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    MessageSource<Map<String, Object>> onlineUserMessageSource(StationService stationService){
        return () -> MessageBuilder.withPayload(stationService.getAllStationInfo()).build();
    }

    @Bean
    IntegrationFlow onlineUserFlow(StationService stationService){
        return IntegrationFlows
                .from(onlineUserMessageSource(stationService),
                    resourcePolling -> resourcePolling.poller(Pollers.fixedRate(3 * 1000).maxMessagesPerPoll(1)))
                .channel(onlineUserOnlineChannel())
                .get();
    }
}
