package edu.sabanciuniv.cs308ecommercebackend;

import com.mongodb.*;
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
        String mongoConnectionUrl = System.getenv("MONGO_CONNECTION_URL");

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoConnectionUrl))
                .serverApi(serverApi)
                .build();
    }

}
