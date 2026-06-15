package com.ventastech.pedido.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Collections;

@Slf4j
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Value("${app.mongodb.uri:mongodb://localhost:27017}")
    private String mongoUri;

    @Value("${app.mongodb.database:db_pedido}")
    private String database;

    @Value("${spring.application.name:pedido-service}")
    private String appName;

    @Bean
    public MongoClient mongoClient() {
        log.info(">>> Conectando a MongoDB: {} | BD: {}", mongoUri, database);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .applicationName(appName)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), database);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate template = new MongoTemplate(mongoDatabaseFactory());
        log.info(">>> MongoTemplate apuntando a BD: {}", template.getDb().getName());
        return template;
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }
}
