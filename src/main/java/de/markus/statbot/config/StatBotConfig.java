package de.markus.statbot.config;
import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatBotConfig {
    @Bean
    EventBus eventBus() {
        return new EventBus();
    }

    @Bean
    EventBusPostProcessor eventBusPostProcessor() {
        return new EventBusPostProcessor();
    }
}
