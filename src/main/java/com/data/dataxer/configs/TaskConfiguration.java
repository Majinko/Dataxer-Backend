package com.data.dataxer.configs;

import com.data.dataxer.tasks.RepeatedCosts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class TaskConfiguration {

    @Bean
    public RepeatedCosts costsTask() {
        return new RepeatedCosts();
    }


}
