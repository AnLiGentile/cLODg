# cLODg - conference Linked Open Data generator

cLODg implements a methodology to produce Linked Data to describe a scientific conference and its publications, participants and events.
To generate conference metadata we assume that you have initial data available (with some specific [format](#data-acquisition)). We then use use [D2R conversion rules](#data-conversion) to produce metadata described with [Conference Ontology](http://w3id.org/scholarlydata/ontology/conference-ontology.owl). While we are in the process of creating a single-click process, we describe here the current workflow to follow. 

The workflow consists of:

1. [Data acquisition](#data-acquisition)
2. [Data conversion](#data-conversion)
3. [Data augmentation and verification](#data-augmentation-and-verification)
4. [Linked Data Publication](#linked-data-publication)



## Data acquisition
We currently accept data in the following formats:
- rdf data: any conference metadata represented with the [Semantic Web Conference Ontology](http://data.semanticweb.org/ns/swc/swc_2009-05-09.html#ConferenceEvent)
 - example of [ESWC2015 data](./resources/swdf_samples/SWDF_eswc2015.rdf) from the [SWDF](http://data.semanticweb.org/) dataset
- csv files exported from [EasyChair](https://www.easychair.org) 
 - samples of ESWC2016 data exported from easychair about [paper authors](./resources/csv_samples/author_sample.csv), [program committee](./resources/csv_samples/committee_sample.csv), [submitted papers](./resources/csv_samples/submission_sample.csv) and [conference tracks](./resources/csv_samples/track_sample.csv) (Provided data is simplified for privacy reasons)
- csv describing keynote, organizing committee, sessions, talks and special events
 - samples of data from ESWC2016 about [organizing committee](./resources/csv_samples/organising_sample.csv), [program committee](./resources/csv_samples/committee_sample.csv), [keynote talks](./resources/csv_samples/keynote_sample.csv), [conference sessions](./resources/csv_samples/session_sample.csv) and and [conference talks](./resources/csv_samples/talk_sample.csv)
- we still support conversion from XML [EasyChair](https://www.easychair.org) output, although this is currently not provided anymore by easychair (XML [schema](./resources/conference_dump.xml))

## Data conversion
- For csv data, the current workflow requires the manual import into a [MySQL](https://www.mysql.com) database.
Then it is possible to generate [Conference Ontology](http://w3id.org/scholarlydata/ontology/conference-ontology.owl) data via [D2R](http://d2rq.org/d2r-server) [conversion rules](https://github.com/AnLiGentile/cLODg/blob/clodg2/src/main/resources/templates/easychair/d2rq_mapping_pd.ftl).
This is done via specifying the database to use via [property file](https://github.com/AnLiGentile/cLODg/blob/clodg2/eswc2016_example.properties) and using the [LDGenerator](https://github.com/AnLiGentile/cLODg/blob/clodg2/src/main/java/org/scholarlydata/clodg/LDGenerator.java).
Nevertheless, we are in the process of replacing MySQL with [HSQLDB](http://hsqldb.org/) to support a fully integrated Java process (to facilitate data creation for the end user as a single process with cLODg). Code for this will be published ASAP.

We generate data according to the following vocabularies:
- [Conference Ontology](http://w3id.org/scholarlydata/ontology/conference-ontology.owl)
- [Semantic Web Conference Ontology](http://data.semanticweb.org/ns/swc/swc_2009-05-09.html#ConferenceEvent) for backward compatibility


## Data augmentation and verification

We implemented a Web based application to collect user feedback. This is an optional step that a conference can implement to send generated metadata (in a user-friendly format) to people involved and ask them to verify them.

## Linked Data Publication

- Data produced using cLODg can be sent to [Scholarlydata](http://w3id.org/scholarlydata) via the [Dataset loader](http://w3id.org/scholarlydata) facility.
