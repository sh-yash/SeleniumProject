package com.seleniumProject.Libraries.DatabaseConnectivity;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoConnectivity {

	private MongoClient mongoClient;
	private MongoDatabase mdb;
	private static final String BASEURL = "localhost";
	private static final int PORT = 27017;
	private static String USERNAME = "localhost";
	private static String DATABASE = "admin";
	private static char[] PASSWORD = { 'a', 'b', 'c', 'd', 'e' };

	public MongoDatabase createMongoDbConnection(String dbName) {
		mongoClient = new MongoClient(BASEURL, PORT);
		MongoDatabase mdb = mongoClient.getDatabase(dbName);
		return mdb;
	}

	public void createMongoConnectionWithAuth() {
		MongoCredential credential = MongoCredential.createCredential(USERNAME, DATABASE, PASSWORD);
		mongoClient = new MongoClient(new ServerAddress("localhost", 27017), Arrays.asList(credential));
		mdb = mongoClient.getDatabase("admin");
	}

	public MongoCollection<Document> performMongoCollectionOperation(String collectionName) {
		MongoCollection<Document> coll = mdb.getCollection(collectionName);
		return coll;
	}

	public ArrayList<String> getCollectionList() {
		ArrayList<String> colName = new ArrayList<String>();
		MongoIterable<String> list = mdb.listCollectionNames();
		for (String string : list) {
			colName.add(string);
		}
		return colName;
	}

}
