package com.datastax;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {
    public static void main(String[] args) throws IOException {
        if(args.length == 0 || args[0] == null || args[0].isEmpty()){
            System.out.println("Specify CSV file path to load");
            return;
        }
        Cluster cluster = null;
        BufferedReader br = new BufferedReader(new FileReader(args[0]));

        try {
            cluster = Cluster.builder()
                    .addContactPoint("127.0.0.1")
                    .build();
            Session session = cluster.connect();
            session.execute("use flightsdb");
            MappingManager manager = new MappingManager(session);
            Mapper<Flight> mapper = manager.mapper(Flight.class);

            String line = null;
            long count = 0;
            long failed = 0;
            while ((line = br.readLine()) != null) {
                try {
                    mapper.save(flight(line));
                } catch (Exception ex) {
                    ++failed;
                    System.out.println("Failed to load line : "+line);
                    ex.printStackTrace();
                }
                ++count;
                if(count % 5000 == 0) {
                    System.out.println("Processed "+count+" records");
                }
            }

            if(failed < count) {
                System.out.println("Total " + (count - failed) + " records loaded");
            }
            if(failed > 0) {
                System.out.println(""+failed+" records failed to load");
            }

        } finally {
            if (cluster != null) {
                cluster.close();
            }
            if(br != null) {
                br.close();
            }
        }

    }

    private static Flight flight(String line) {
        return new Flight(line);
    }
}
