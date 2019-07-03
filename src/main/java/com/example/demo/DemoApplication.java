package com.example.demo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.*;
import java.io.*;

import java.io.FileReader;
import java.io.BufferedReader;
import com.opencsv.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import org.bson.Document;
import java.io.IOException;

import com.example.demo.Feed;
import com.example.demo.FeedMessage;
import com.example.demo.RSSFeedParser;

import javax.xml.crypto.Data;


@SpringBootApplication

public class DemoApplication {

	public static void main(String[] args) {
		MongoClient mongo = new MongoClient("localhost", 27017);
		System.out.println("connection successfully");
		MongoDatabase db = mongo.getDatabase("article");
		System.out.println(db.getName());

		//List <String> dbname = mongo.getDatabaseNames();
		//System.out.println(dbname);.

		//db.createCollection("sample");
		MongoCollection<Document> collection = db.getCollection("rssfeed");
		System.out.println("Collection  selected successfully");

		RSSFeedParser parser = new RSSFeedParser(
				"https://news.google.com/rss?hl=en-IN&gl=IN&ceid=IN:en");
		Feed feed = parser.readFeed();
		//System.out.println(feed);

		int count = 0;

		 /*for (FeedMessage message : feed.getMessages()) {
			System.out.println(message);

			Document document;
			document = new Document("id", count + 1)
					.append("title", message.getTitle())
					.append("pub date", message.getPubDate())
					.append("Description", message.getDescription())
					.append("link", message.getLink());

			collection.insertOne(document);
			System.out.println("Document inserted successfully");


			count++;


		}

		MongoDatabase database = mongo.getDatabase("article");
		MongoCollection<Document> collect = database
				.getCollection("rssfeed");

		List<Document> documents = (List<Document>) collect.find().into(new ArrayList<Document>());

		for (Document document : documents) {
			System.out.println(document);


		}
		
		  */

		String csvFile = "/Users/dikshitasalecha/Documents/hope.csv";

		List<String> Groundtruth =new ArrayList<String>() ;
		List<String> Predictions =new ArrayList<String>();



		/* String[] Badwords = {"Modi","Trump","Revenge","Murder","Robbery","Missing","Bomb","anguish",
				"Dies","Protests","Drug","pain","mystery","violence","war","Parliament","chaos","crisis",
				"death", "problem","disease","election","politics","tragedy","conservatives","fire","political",
				"dreadful","danger","rage","sadly","misstep","envy","risk","worries","inflation","revolution",
				"economic","stuck","Tax", "terrible", "bad","doom","unsure","trade","market","Capitalism",
				"sick","obituaries","psychic","hurt", "government","disruptive", "toxic","fail","uninspired",
				"premature","threat","inequality","argument","worse","erode","disastrous","conflict","flaw",
				"flaw","zombie","brexit","activist","lose","confused","?","steroids","ransome","apocalypse",
				"impotence","deficiencies"};

		 */
		String[] Goodwords={"Good","coordination","light","hope","peace","help","wins","optimism",
		                       "caring"};

		CSVReader reader = null;
		try
		{
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			while ((line = reader.readNext()) != null)
			{
				//	System.out.println("\nTitle:  " + line[0] + ",\n type:" + line[1] + " ,\n Description:" + line[2] + "]");

				Groundtruth.add(line[2]);
				String sentence = line[0];
				String sentence2=line[1];
				int w=1;

				for (int i = 0; i < Goodwords.length; i++)
				{
					String search = Goodwords[i];
					if (sentence.toLowerCase().contains(search.toLowerCase()) || sentence2.toLowerCase().contains(search.toLowerCase()))
					{
						w = w + 1;
					}
				}

				if (w >1)
				    {
					 Predictions.add("Positive");
					}

			    else
					{
					 Predictions.add("Negative")   ;
					}

			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

        int tp=0,tn=0,fp=0,fn=0;


		System.out.println(Arrays.toString(Groundtruth.toArray()));
		System.out.println(Arrays.toString(Predictions.toArray()));

		for (int i=0; i<Groundtruth.size(); i++)
		{
			if (Groundtruth.get(i).equals("Positive") && (Predictions.get(i).equals("Positive")))
			{
				tp=tp+1;
			}

			else if (Groundtruth.get(i).equals("Negative")&& Predictions.get(i).equals("Negative"))
			{
				tn=tn+1;
			}

			else if (Groundtruth.get(i).equals("Negative")&& Predictions.get(i).equals("Positive"))
			{
				fp=fp+1;
			}

			//if (Groundtruth.get(i).equals("Good")&& Predictions.get(i).equals("Bad"))
			else
			{
				fn=fn+1;
			}
		}


        double precision=tp*100/(tp+fp);
		double recall = tp*100/ (tp + fn) ;

		System.out.println( "Precision is  "+ precision);
        System.out.println("Recall is " + recall);


	}

}




