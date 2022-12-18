package com.mycompany.app;

import static com.mongodb.client.model.Filters.eq;

import java.time.LocalDateTime;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
        {
            String uri = "mongodb+srv://manager:manager123@cluster0.di9w0gh.mongodb.net/?retryWrites=true&w=majority";
            try (MongoClient mongoClient = MongoClients.create(uri)) {
                MongoDatabase database = mongoClient.getDatabase("management");
                MongoCollection<Document> collection = database.getCollection("pessoa");

                Document tel1 = new Document("telefone-1", "901020304");
                Document em1 = new Document("email-1", "kkk");
                Document contrato = new Document("comeco", LocalDateTime.of(2022, 6, 10, 17, 0, 0))
                .append("fim", LocalDateTime.of(2023, 6, 10, 17, 0, 0));

                Document pessoa = new Document("nome", "kalil")
                .append("cpf", "21212121213")
                .append("cargo", "desenvolvedor uml")
                .append("telefone", tel1)
                .append("email", em1)
                .append("duracao_contrato", contrato);

                collection.insertOne(pessoa);

                FindIterable<Document> doc = collection.find(eq("cargo", "desenvolvedor uml"));
                if (doc != null) {
                    for (Document element : doc) {
                        System.out.println(element.toJson());
                    }
                } else {
                    System.out.println("No matching documents found.");
                }
            }
    }    

}
