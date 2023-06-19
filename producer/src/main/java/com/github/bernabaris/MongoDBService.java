package com.github.bernabaris;

import com.github.bernabaris.kafka.KafkaProducerService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class MongoDBService {

    @Value(value = "${spring.application.data.mongodb.database}")
    private String databaseName;

    @Value(value = "${mongodb.collection}")
    private String collectionCfg;

    private final MongoClient mongoClient;
    private final KafkaProducerService kafkaProducer;

    MongoCollection<Document> collection = null;
    ObjectId lastReadId = null;

    MongoDBService(MongoClient mongoClient, KafkaProducerService kafkaProducerService) {
        this.mongoClient = mongoClient;
        kafkaProducer = kafkaProducerService;
    }

    @PostConstruct
    public void init() {
        log.info("{} is started", this.getClass().getSimpleName());
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        collection = db.getCollection(collectionCfg);
        Document lastDoc = collection.find().sort(new Document("_id", -1)).limit(1).first();
        if (lastDoc != null) {
            lastReadId = lastDoc.getObjectId("_id");
        }
    }

    @Scheduled(fixedRate = 10000)
    public void observeCollection() {
        log.info("Observing new documents for collection: {} lastReadId: {}", collectionCfg, lastReadId);

        FindIterable<Document> newDocs;
        if (lastReadId == null) {
            newDocs = collection.find().sort(new Document("_id", 1));
        } else {
            newDocs = collection.find(new Document("_id", new Document("$gt", lastReadId))).sort(new Document("_id", 1));
        }

        for (Document doc : newDocs) {
            lastReadId = doc.getObjectId("_id");
            kafkaProducer.sendMessage(doc.toJson());
        }
    }
}
