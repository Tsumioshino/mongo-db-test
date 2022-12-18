package com.mycompany.app;

import java.util.Arrays;
import java.util.List;

import java.time.LocalDateTime;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.AggregateIterable;


import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.model.Updates;

public class App 
{
    //
    //    
     public static void readQueryReferencia(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("projeto"); 
        List<Bson> pipeline = Arrays.asList(
            Aggregates.unwind("$equipe"),
            Aggregates.lookup("pessoa", "equipe", "_id", "p"),
            Aggregates.match(Filters.eq("nome", "jira-clone")),
            Aggregates.project(Projections.fields(Projections.include("p.nome")))
        );

        AggregateIterable<Document> result = collection.aggregate(pipeline);

        for (Document answer : result) {
            System.out.println(answer);
        }
    }

    public static void main( String[] args )
        {
            String uri = "mongodb+srv://manager:manager123@cluster0.di9w0gh.mongodb.net/?retryWrites=true&w=majority";
            try (MongoClient mongoClient = MongoClients.create(uri)) {
                MongoDatabase database = mongoClient.getDatabase("management");
                MongoCollection<Document> collection = database.getCollection("pessoa");
                //queryReferencia();

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

                /* 
                FindIterable<Document> doc = collection.find(eq("cargo", "desenvolvedor uml"));
                if (doc != null) {
                    for (Document element : doc) {
                        System.out.println(element.toJson());
                    }
                } else {
                    System.out.println("No matching documents found.");
                }
                */
                /* 
                MongoIterable<String> names = database.listCollectionNames();
                for (String name : names) {
                    System.out.println(name);
                }
                */

                
                insertOneTask(database);

                deleteOneTask(database);

                updateOneTask(database);
                
                /* 
                // PROCURANDO PROJETO 
                System.out.println("Procurando projetos\n\n");

                // Não deve encontrar nenhum projeto porque não existe projeto com esse nome
                FindIterable<Document> allProjects = database.getCollection("projeto").find(eq("nome", "android"));
                for (Document project : allProjects) {
                    System.out.println(project);
                }
                */

            }
    }  
    
    public static void showDocuments(FindIterable<Document> documents) {
        for (Document doc : documents) {
            System.out.println(doc);
            System.out.println("\n");
        }
    }

    public static void insertOneTask(MongoDatabase database) {
        System.out.println("\n TASKS BEFORE DELETION\n");

                // Show all tasks
                FindIterable<Document> oldTasks = database.getCollection("tarefa").find();
                showDocuments(oldTasks);

                FindIterable<Document> docs = database.getCollection("projeto").find(eq("nome","jira-clone"));
                Document task = new Document()
                .append("projeto", docs.first().getObjectId("_id"))
                .append("responsavel", "Marcus")
                .append("status", "a fazer")
                .append("descrição", "Implementar fluxo de login")
                .append("prazo", new Document()
                                    .append("prazo-inicio", LocalDateTime.of(2023, 6, 10, 17, 0, 0))
                                    .append("prazo-fim", LocalDateTime.of(2023, 6, 10, 17, 0, 0))
                );
                database.getCollection("tarefa").insertOne(task);

                System.out.println("\n TASKS AFTER INSERTION\n");
                
                // Show all tasks
                FindIterable<Document> newTasks = database.getCollection("tarefa").find();
                showDocuments(newTasks);
    }

    public static void deleteOneTask(MongoDatabase database) {
        database.getCollection("tarefa").deleteOne(eq("responsavel", "Marcus"));
                
                System.out.println("\n TASKS AFTER DELETION\n");
                // Show all tasks
                FindIterable<Document> tasksAfterDeletion = database.getCollection("tarefa").find();
                showDocuments(tasksAfterDeletion);
    }

    public static void updateOneTask(MongoDatabase database) {
        Bson updates = Updates.combine(
            Updates.set("status", "fazendo")
            );

        database.getCollection("tarefa").updateOne(eq("status", "feito"), updates);

        System.out.println("\n TASKS AFTER UPDATE\n");

        // Show all tasks
        FindIterable<Document> tasksAfterUpdate = database.getCollection("tarefa").find();
        showDocuments(tasksAfterUpdate);
    }

}
