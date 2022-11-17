package cmu.edu;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import org.bson.Document;

import javax.print.Doc;

import static com.mongodb.client.model.Sorts.descending;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class MongoClass {
    public static MongoDatabase database;
    public static MongoCollection<Document> myCollection;

    public static void main(String[] args){
        Mongo();
        Scanner sc = new Scanner(System.in);
        System.out.println("Write to database: ");
        String input = sc.nextLine();
//        insert(input);
        getAll();
    }

    public static MongoDatabase Mongo(){
        if(database!=null){return database;}
        ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:Qwer1234@cluster0.fcteh9s.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoClass.database = database;
        MongoClass.myCollection = database.getCollection("logs");
        return database;
    }

    public static void insert(String input){
        Mongo();
        Document single = new Document();
        single.append("log", input);
        MongoClass.myCollection = database.getCollection("logs");
        myCollection.insertOne(single);
        System.out.println("inserted: "+ single.toJson());
    }

    public static ArrayList<String> getAll(){
        Mongo();

//        MongoClass.myCollection = database.getCollection("myCollection");
        ArrayList<String> ans = new ArrayList<>();
        FindIterable<Document> iterDoc = myCollection.find();
        Iterator<Document> it = iterDoc.iterator();
        System.out.println("Inside database");
        while (it.hasNext()) {
            Document a = it.next();
            ans.add(a.toString());
            System.out.println(a.toString());
        }
        return ans;

    }
}
