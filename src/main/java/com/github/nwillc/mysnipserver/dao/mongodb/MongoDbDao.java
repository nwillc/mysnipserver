package com.github.nwillc.mysnipserver.dao.mongodb;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Entity;
import com.github.nwillc.mysnipserver.util.JsonMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class MongoDbDao<T extends Entity> implements Dao<T>, JsonMapper {
    private final Class<T> tClass;
    private final MongoCollection<Document> collection;

    public MongoDbDao(final MongoClient client, final Class<T> tClass) {
        this.tClass = tClass;
        collection =  client.getDatabase("snippets").getCollection(tClass.getSimpleName());
    }

    @Override
    public Optional<T> findOne(String key) {
        Document document = collection.find(eq("key",key)).first();
        return document == null ? Optional.empty() : Optional.of(fromJson(document.toJson(), tClass));
    }

    @Override
    public Stream<T> findAll() {
        return StreamSupport.stream(collection.find().spliterator(), false).map(d -> fromJson(d.toJson(),tClass));
    }

    @Override
    public Stream<T> find(Predicate<T> predicate) {
        return findAll().filter(predicate);
    }

    @Override
    public void save(T entity) {
        Optional<T> one = findOne(entity.getKey());
        Document document = Document.parse(toJson(entity));
        if (one.isPresent()) {
            document = new Document("$set", document);
            collection.updateMany(eq("key",entity.getKey()), document);
        } else {
            collection.insertOne(document);
        }
    }

    @Override
    public void delete(String key) {
        collection.deleteMany(eq("key",key));
    }
}
