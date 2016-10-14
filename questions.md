
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
- created a materialized view as below which can answer above query as data is ordered by DEP_TIME becuase it is clustering column

```
CREATE MATERIALIZED VIEW flights_by_time
AS SELECT ORIGIN, DEP_TIME
FROM flights
WHERE ORIGIN IS NOT NULL AND DEP_TIME IS NOT NULL  AND DEST IS NOT NULL
AND CARRIER IS NOT NULL AND ID IS NOT NULL
PRIMARY KEY (ORIGIN, DEP_TIME, DEST, CARRIER, ID);
   ```

### Sample output
-
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

cqlsh:flightsdb> SELECT * from flights_by_time where origin = 'ATL' LIMIT 50;

 origin | dep_time                 | dest | carrier | id
--------+--------------------------+------+---------+---------
    ATL | 2012-01-01 00:00:00+0000 |  AGS |      EV |  151116
    ATL | 2012-01-01 00:00:00+0000 |  AGS |      EV |  637249
    ATL | 2012-01-01 00:00:00+0000 |  MKE |      DL |   73343
    ATL | 2012-01-01 00:00:00+0000 |  MKE |      DL |  559476
    ATL | 2012-01-01 00:00:00+0000 |  MKE |      DL | 1045609
    ATL | 2012-01-01 00:01:00+0000 |  CMH |      DL |   73156
    ATL | 2012-01-01 00:01:00+0000 |  CMH |      DL |  559289
    ATL | 2012-01-01 00:01:00+0000 |  CMH |      DL | 1045422
    ATL | 2012-01-01 00:01:00+0000 |  EYW |      DL |   73183
    ATL | 2012-01-01 00:01:00+0000 |  EYW |      DL |  559316
    ATL | 2012-01-01 00:01:00+0000 |  EYW |      DL | 1045449
    ATL | 2012-01-01 00:02:00+0000 |  MGM |      EV |  160516
    ATL | 2012-01-01 00:02:00+0000 |  MGM |      EV |  646649
    ATL | 2012-01-01 00:02:00+0000 |  XNA |      EV |  153539
    ATL | 2012-01-01 00:02:00+0000 |  XNA |      EV |  639672
    ATL | 2012-01-01 00:03:00+0000 |  MGM |      EV |  161921
    ATL | 2012-01-01 00:03:00+0000 |  MGM |      EV |  648054
    ATL | 2012-01-01 00:03:00+0000 |  SDF |      DL |   74107
    ATL | 2012-01-01 00:03:00+0000 |  SDF |      DL |  560240
    ATL | 2012-01-01 00:03:00+0000 |  SDF |      DL | 1046373
    ATL | 2012-01-01 00:03:00+0000 |  SJU |      FL |  192620
    ATL | 2012-01-01 00:03:00+0000 |  SJU |      FL |  678753
    ATL | 2012-01-01 00:04:00+0000 |  DFW |      AA |   23928
    ATL | 2012-01-01 00:04:00+0000 |  DFW |      AA |  510061
    ATL | 2012-01-01 00:04:00+0000 |  DFW |      AA |  996194
    ATL | 2012-01-01 00:05:00+0000 |  IAD |      DL |   73405
    ATL | 2012-01-01 00:05:00+0000 |  IAD |      DL |  559538
    ATL | 2012-01-01 00:05:00+0000 |  IAD |      DL | 1045671
    ATL | 2012-01-01 00:05:00+0000 |  MDW |      DL |   73778
    ATL | 2012-01-01 00:05:00+0000 |  MDW |      DL |  559911
    ATL | 2012-01-01 00:05:00+0000 |  MDW |      DL | 1046044
    ATL | 2012-01-01 00:07:00+0000 |  FLL |      FL |  192503
    ATL | 2012-01-01 00:07:00+0000 |  FLL |      FL |  678636
    ATL | 2012-01-01 00:07:00+0000 |  IAD |      YV |  474204
    ATL | 2012-01-01 00:07:00+0000 |  IAD |      YV |  960337
    ATL | 2012-01-01 00:07:00+0000 |  PIT |      DL |   73097
    ATL | 2012-01-01 00:07:00+0000 |  PIT |      DL |  559230
    ATL | 2012-01-01 00:07:00+0000 |  PIT |      DL | 1045363
    ATL | 2012-01-01 00:09:00+0000 |  CRW |      EV |  164786
    ATL | 2012-01-01 00:09:00+0000 |  CRW |      EV |  650919
    ATL | 2012-01-01 00:09:00+0000 |  DAB |      DL |   73369
    ATL | 2012-01-01 00:09:00+0000 |  DAB |      DL |  559502
    ATL | 2012-01-01 00:09:00+0000 |  DAB |      DL | 1045635
    ATL | 2012-01-01 00:10:00+0000 |  RSW |      DL |   73622
    ATL | 2012-01-01 00:10:00+0000 |  RSW |      DL |  559755
    ATL | 2012-01-01 00:10:00+0000 |  RSW |      DL | 1045888
    ATL | 2012-01-01 00:11:00+0000 |  EWR |      EV |  141199
    ATL | 2012-01-01 00:11:00+0000 |  EWR |      EV |  627332
    ATL | 2012-01-01 00:13:00+0000 |  ROC |      FL |  192609
    ATL | 2012-01-01 00:13:00+0000 |  ROC |      FL |  678742

(50 rows)
```

## List the carrier, origin, and destination airport for a flight based on 10 minute buckets of air_time.
- Created materialized view as below which can answer above query as data is ordered by air_time_bucket becuase it is clustering column. Please note this column value is calculated
while data is loaded into table

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

cqlsh:flightsdb> select * from flights_by_air_time where carrier= 'AA' LIMIT 5;

 carrier | air_time_bucket | origin | dest | id
---------+-----------------+--------+------+--------
      AA |               2 |    DFW |  AUS | 498069
      AA |               2 |    DFW |  AUS | 984202
      AA |               2 |    DFW |  OKC | 499694
      AA |               2 |    DFW |  OKC | 499709
      AA |               2 |    DFW |  OKC | 499711

(5 rows)
cqlsh:flightsdb> select * from flights_by_air_time where carrier= 'YV' LIMIT 5;

 carrier | air_time_bucket | origin | dest | id
---------+-----------------+--------+------+--------
      YV |               1 |    CLT |  GSO | 482759
      YV |               1 |    CLT |  GSO | 968892
      YV |               1 |    GSO |  CLT | 475622
      YV |               1 |    GSO |  CLT | 961755
      YV |               1 |    HNL |  OGG | 474776

(5 rows)
```



# 5. Exercise the following queries using either Search or Analytics
- For this part created all the indexes in solr using below command

 `./dsetool create_core flightsdb.flights generateResources=true reindex=true`


Solr link http://localhost:8983/solr/#/flightsdb.flights

## a. How many flights originated from the ‘HNL’ airport code on 2012-01-25

Solr query link which return "numFound"
'http://localhost:8983/solr/flightsdb.flights/select?q=origin%3A%22HNL%22+AND+fl_date%3A%222012-01-13T00%3A00%3A00Z%22&rows=0&wt=json&indent=true'

### Sample output
```
{
  "responseHeader":{
    "status":0,
    "QTime":3},
  "response":{"numFound":308,"start":0,"docs":[]
  }}
```

## b. How many airport codes start with the letter ‘A’

Solr query link which return "ngroups : 22"
'http://localhost:8983/solr/flightsdb.flights/select?q=origin%3AA*&group=true&group.field=origin&group.ngroups=true&useFieldCache=true&wt=json&indent=true&rows=1'

```
{
  "responseHeader":{
    "status":0,
    "QTime":12},
  "grouped":{
    "origin":{
      "matches":88926,
      "ngroups":22,
      "groups":[{
          "groupValue":"ATL",
          "doclist":{"numFound":64165,"start":0,"docs":[
              {
                "_uniqueKey":"[\"ATL\",\"IAH\",\"OO\",\"271854\"]",
                "air_time_bucket":11,
                "distance":689,
                "year":2012,
                "origin":"ATL",
                "airline_id":20304,
                "dest":"IAH",
                "day_of_month":18,
                "origin_city_name":"Atlanta",
                "dest_state_abr":"TX",
                "dest_city_name":"Houston",
                "actual_elapsed_time":156,
                "air_time":112,
                "id":271854,
                "origin_airport_id":10397,
                "origin_state_abr":"GA",
                "fl_num":5227,
                "dep_time":"2012-01-18T00:09:00Z",
                "carrier":"OO",
                "fl_date":"2012-01-18T00:00:00Z",
                "arr_time":"2012-01-18T13:45:00Z"}]
          }}]}}}
```



## c. What originating airport had the most flights on 2012-01-23

Solr query link which return '"ATL",2155'
`http://localhost:8983/solr/flightsdb.flights/select?q=fl_date%3A%222012-01-23T00%3A00%3A00Z%22+&start=0&rows=0&wt=json&indent=true&facet=true&facet.query=fl_date%3A%222012-01-23T00%3A00%3A00Z%22&facet.field=origin&useFieldCache=true`

```

{
  "responseHeader":{
    "status":0,
    "QTime":6},
  "response":{"numFound":35239,"start":0,"docs":[]
  },
  "facet_counts":{
    "facet_queries":{
      "fl_date:\"2012-01-23T00:00:00Z\"":35239},
    "facet_fields":{
      "origin":[
        "ATL",2155,
        "ORD",1860,
        "DFW",1827,
        "DEN",1342,
        "LAX",1287,
        "PHX",1035,
        "SFO",946,
        "IAH",941,
        "LAS",786,
        "MCO",720,
        "BOS",688,
        "CLT",682,
        "EWR",665,
        "JFK",647,
        "SLC",629,
        "SEA",621,
        "LGA",611,
        "MIA",589,
        "MSP",575,
        "BWI",545,
        "DTW",473,
        "PHL",462,
        "SAN",441,
        "DCA",440,
        "MDW",436,
        "FLL",433,
        "IAD",403,
        "TPA",403,
        "STL",345,
        "BNA",306,
        "HOU",296,
        "PDX",296,
        "HNL",294,
        "MCI",282,
        "DAL",266,
        "OAK",259,
        "CLE",255,
        "RDU",255,
        "SMF",254,
        "SNA",249,
        "AUS",246,
        "SJC",229,
        "MEM",228,
        "MKE",222,
        "MSY",221,
        "SAT",219,
        "PIT",178,
        "ABQ",176,
        "RSW",176,
        "PBI",165,
        "BUR",164,
        "SJU",157,
        "IND",152,
        "CMH",147,
        "JAX",144,
        "TUS",141,
        "CVG",136,
        "ONT",135,
        "OGG",133,
        "OKC",125,
        "ELP",124,
        "ANC",119,
        "OMA",119,
        "BUF",118,
        "BDL",111,
        "LGB",108,
        "TUL",104,
        "BHM",101,
        "LIT",101,
        "RNO",101,
        "RIC",98,
        "SDF",88,
        "PSP",87,
        "CHS",74,
        "KOA",72,
        "COS",71,
        "XNA",71,
        "DAY",70,
        "DSM",70,
        "ORF",70,
        "PVD",68,
        "LIH",67,
        "FAT",64,
        "HPN",64,
        "ICT",64,
        "BOI",62,
        "TYS",62,
        "GEG",61,
        "GRR",60,
        "SBA",60,
        "GSP",56,
        "PNS",56,
        "ROC",52,
        "CAK",48,
        "GSO",48,
        "MSN",48,
        "CRP",46,
        "HSV",46,
        "JAN",46,
        "MAF",46]},
    "facet_dates":{},
    "facet_ranges":{},
    "facet_intervals":{}}}
```
