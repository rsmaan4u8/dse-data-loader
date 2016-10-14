
 Questions
==========
# 1. Installation and Configuration of DataStax Enterprise (DSE MAX)
- Registered on datastax site https://academy.datastax.com/downloads/welcome
- Downloaded DSE for MAC
- Tried VirtualBox Sandbox
- Downloaded DevCenter  
- Installed all above and started DSE with solr
--  `./dse cassandra -s`

# 2. Data Model creation
- Created table as below with a few changes for queries
```
CREATE TABLE flights (
            	ID int,
            	YEAR int,
            	DAY_OF_MONTH int,
            	FL_DATE timestamp,
            	AIRLINE_ID int,
            	CARRIER varchar,
            	FL_NUM int,
            	ORIGIN_AIRPORT_ID int,
            	ORIGIN varchar,
            	ORIGIN_CITY_NAME varchar,
            	ORIGIN_STATE_ABR varchar,
            	DEST varchar,
            	DEST_CITY_NAME varchar,
            	DEST_STATE_ABR varchar,
            	DEP_TIME timestamp,
            	ARR_TIME timestamp,
            	ACTUAL_ELAPSED_TIME int,
            	AIR_TIME int,
            	AIR_TIME_BUCKET int,
            	DISTANCE int, PRIMARY KEY((ORIGIN, DEST, CARRIER, ID))
            	);
```
    
# 3. Importing of data into the Cassandra component of DataStax Enterprise
    
- Written Java component using "cassandra-driver-core" and "cassandra-driver-mapping" to load data. https://github.com/rsmaan4u8/dse-data-loader/blob/master/README.md 
     
##Sample Data
```
    cqlsh:flightsdb> select * from flights LIMIT 5;
    
     origin | dest | carrier | id      | actual_elapsed_time | air_time | air_time_bucket | airline_id | arr_time                 | day_of_month | dep_time                 | dest_city_name | dest_state_abr | distance | fl_date                  | fl_num | origin_airport_id | origin_city_name | origin_state_abr | solr_query | year
    --------+------+---------+---------+---------------------+----------+-----------------+------------+--------------------------+--------------+--------------------------+----------------+----------------+----------+--------------------------+--------+-------------------+------------------+------------------+------------+------
        FAI |  SEA |      AS | 1027131 |                 208 |      182 |              18 |      19930 | 2012-01-31 06:24:00+0000 |           31 | 2012-01-31 01:56:00+0000 |        Seattle |             WA |     1533 | 2012-01-31 00:00:00+0000 |    128 |             11630 |        Fairbanks |               AK |       null | 2012
        MSP |  MCO |      DL |  573390 |                 175 |      161 |              16 |      19790 | 2012-01-08 16:53:00+0000 |            8 | 2012-01-08 00:58:00+0000 |        Orlando |             FL |     1310 | 2012-01-08 00:00:00+0000 |   2323 |             13487 |      Minneapolis |               MN |       null | 2012
        KOA |  HNL |      HA |  701788 |                  45 |       29 |               2 |      19690 | 2012-01-15 17:27:00+0000 |           15 | 2012-01-15 16:42:00+0000 |       Honolulu |             HI |      163 | 2012-01-15 00:00:00+0000 |    297 |             12758 |             Kona |               HI |       null | 2012
        CAE |  IAD |      EV |  657064 |                  74 |       57 |               5 |      20366 | 2012-01-14 15:49:00+0000 |           14 | 2012-01-14 14:35:00+0000 |     Washington |             DC |      401 | 2012-01-14 00:00:00+0000 |   5731 |             10868 |         Columbia |               SC |       null | 2012
        PHX |  DEN |      WN |  936035 |                  98 |       74 |               7 |      19393 | 2012-01-24 00:44:00+0000 |           23 | 2012-01-23 11:06:00+0000 |         Denver |             CO |      602 | 2012-01-23 00:00:00+0000 |   2099 |             14107 |          Phoenix |               AZ |       null | 2012
    
    (5 rows)
    
    
```

# 4. Querying of data through Cassandra

## Build a query table to list all flights leaving a particular airport, sorted by time.
- created a materialized view as below which can answer above query

```
CREATE MATERIALIZED VIEW flights_by_time
AS SELECT ORIGIN, DEP_TIME
FROM flights
WHERE ORIGIN IS NOT NULL AND DEP_TIME IS NOT NULL  AND DEST IS NOT NULL
AND CARRIER IS NOT NULL AND ID IS NOT NULL
PRIMARY KEY (ORIGIN, DEP_TIME, DEST, CARRIER, ID);
   ```

### Sample output
```
cqlsh:flightsdb> SELECT * from flights_by_time LIMIT 5 ;

 origin | dep_time                 | dest | carrier | id
--------+--------------------------+------+---------+--------
    EUG | 2012-01-01 00:26:00+0000 |  PDX |      OO | 262712
    EUG | 2012-01-01 00:26:00+0000 |  PDX |      OO | 748845
    EUG | 2012-01-01 00:45:00+0000 |  SFO |      OO | 301386
    EUG | 2012-01-01 00:45:00+0000 |  SFO |      OO | 787519
    EUG | 2012-01-01 01:11:00+0000 |  PDX |      OO | 298811

(5 rows)
```

## List the carrier, origin, and destination airport for a flight based on 10 minute buckets of air_time.
- Created materialized view as below to answer above query

```
CREATE MATERIALIZED VIEW flights_by_air_time
AS SELECT CARRIER, AIR_TIME_BUCKET
FROM flights
WHERE ORIGIN IS NOT NULL AND AIR_TIME_BUCKET IS NOT NULL  AND DEST IS NOT NULL
AND CARRIER IS NOT NULL AND ID IS NOT NULL
PRIMARY KEY (CARRIER, AIR_TIME_BUCKET, ORIGIN, DEST, ID);
```

### Sample output
```
cqlsh:flightsdb> select * from flights_by_air_time LIMIT 5;

 carrier | air_time_bucket | origin | dest | id
---------+-----------------+--------+------+--------
      YV |               1 |    CLT |  GSO | 482759
      YV |               1 |    CLT |  GSO | 968892
      YV |               1 |    GSO |  CLT | 475622
      YV |               1 |    GSO |  CLT | 961755
      YV |               1 |    HNL |  OGG | 474776
```



# 5. Exercise the following queries using either Search or Analytics
- For this part created all the indexes in solr using below command

 `./dsetool create_core flightsdb.flights generateResources=true reindex=true`


Solr link http://localhost:8983/solr/#/flightsdb.flights

## a. How many flights originated from the ‘HNL’ airport code on 2012-01-25

Solr query link http://localhost:8983/solr/flightsdb.flights/select?q=origin%3A%22HNL%22+AND+fl_date%3A%222012-01-13T00%3A00%3A00Z%22&wt=json&indent=true

## b. How many airport codes start with the letter ‘A’

Solr query link http://localhost:8983/solr/flightsdb.flights/select?q=origin%3AA*&group=true&group.field=origin&group.ngroups=true&useFieldCache=true

## c. What originating airport had the most flights on 2012-01-23

Solr query link http://localhost:8983/solr/flightsdb.flights/select?q=fl_date%3A%222012-01-23T00%3A00%3A00Z%22+&start=0&rows=0&wt=json&indent=true&facet=true&facet.query=fl_date%3A%222012-01-23T00%3A00%3A00Z%22&facet.field=origin&useFieldCache=true


