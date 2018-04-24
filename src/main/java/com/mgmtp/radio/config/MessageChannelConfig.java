package com.mgmtp.radio.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
        return new PublishSubscribeChannel(taskExecutor);
    }
}
