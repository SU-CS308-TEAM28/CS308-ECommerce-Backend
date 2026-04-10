package edu.sabanciuniv.cs308ecommercebackend;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import org.bson.UuidRepresentation;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;

@Configuration
@EnableMongoRepositories(basePackages = "edu.sabanciuniv.cs308ecommercebackend.repositories")
public class MongoDBConfiguration extends AbstractMongoClientConfiguration {

    @Override
    public String getDatabaseName() {
        return "CS308DB";
    }

    @Override
    protected MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder()
                .applyToClusterSettings(settings  -> {
                    settings.hosts(singletonList(new ServerAddress("localhost", 32768)));
                })
                .build();
    }

}
