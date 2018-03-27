package com.example.demo;


import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;






import com.datastax.driver.core.Session;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;




public class CassandraCacheClient {




	public CassandraCacheClient() {

	}


	public static void main(String args[]) {



		Scanner sc = new Scanner(System.in);

		//configuring cache with maximum size=10 and time to live= 1 minute
		LoadingCache<Long, Object> messageCache = CacheBuilder.newBuilder().maximumSize(10).expireAfterWrite(60, TimeUnit.SECONDS).build(new CacheLoader<Long, Object>() {

			@Override
			public Object load(Long arg0) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
		});





		//Connecting to DB With containerIP=11.10.10.10 and port=9042
		CassandraConnector connector = new CassandraConnector();
		connector.connect("11.10.10.10", 9042);
		Session session = connector.getSession();


		//		Creating Keyspace "library" replication factor=1 strategy="SimpleStrategy"
		KeyspaceRepository sr = new KeyspaceRepository(session);
		//		sr.createKeyspace("library", "SimpleStrategy", 1);
		sr.useKeyspace("library");





		//		Creating table in Keyspace library
		MessageRepository br = new MessageRepository(session);
        br.deleteTable("message_table");
		br.createTable();


		System.out.println("How many message to store in DB");
		int i=sc.nextInt();

		while(i!=0) {
			//Inserting messages in DB
			Messages book = new Messages(i, "custom message "+i);
			br.insertbook(book);
			System.out.println("row inserted");
			i=i-1;
		}


		//fetching from DB and putting in cache
		Long key= new Long(1000);
		Long startKey=key+1;
		System.out.println("startKey="+startKey);
		Long endKey = null;
		List<Messages> books = br.selectAll();
		System.out.println("messages:");
		Iterator< Messages> bookIt = books.iterator();
		while(bookIt.hasNext()) {

			Messages obj = bookIt.next();
			key++;
			messageCache.asMap().put(key, obj);
			System.out.println(obj.toString()+"cached with key="+key);
			endKey= key;	
		}


		//fetch from cache to console
		Long nkey= new Long(1000);
		Long nstartKey=nkey+1;
		Long nendKey = endKey;
		int k=0;
		while(nstartKey!=nendKey+1)
		{k++;
		System.out.println("Message "+ k +" in Cache:- " + messageCache.asMap().get(nstartKey)+" Key="+nstartKey);
		nstartKey++;
		
		
		}
		
		System.out.println("fetch all at once:");
		System.out.println(messageCache.asMap().values());
		
		
		

		try {
			Thread.sleep(70000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//trying to fetch after expiry
		Long nxkey= new Long(1000);
		Long nxstartKey=nxkey+1;
		Long nxendKey = endKey;
		int kx=0;
		while(nxstartKey!=nxendKey+1)
		{kx++;
		System.out.println("Message "+ kx +" in Cache:- " + messageCache.asMap().get(nxstartKey)+" Key="+nxstartKey);
		nxstartKey++;
		}
		



		//		sr.deleteKeyspace("library");



		connector.close();
		sc.close();

	}

}
