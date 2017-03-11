CREATE TABLE PROJECTLABEL(
    id serial NOT NULL PRIMARY KEY,
    label VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE PROJECTTOLABEL(
    PROJECT_LABEL_ID BIGINT NOT NULL REFERENCES PROJECTLABEL(ID) ,
    PROJECT_ID VARCHAR(255) NOT NULL REFERENCES PROJECT(ID),
    PRIMARY KEY (PROJECT_ID, PROJECT_LABEL_ID)
);



INSERT INTO PROJECTLABEL(ID, LABEL) VALUES
(1, 'web'),
(2, 'integration'),
(3, 'messaging'),
(4, 'big-data'),
(5, 'nosql'),
(6, 'document'),
(7, 'security'),
(8, 'oauth'),
(9, 'jdbc'),
(10, 'jpa'),
(11, 'mvc'),
(12, 'sql'),
(13, 'graph'),
(14, 'social'),
(15, 'data'),
(16, 'mobile'),
(17, 'test'),
(18, 'search'),
(19, 'client'),
(20, 'analytics'),
(21, 'iot'),
(22, 'batch'),
(23, 'cloud'),
(24, 'stream'),
(25, 'tools'),
(26, 'microservices'),
(29, 'cache'),
(30, 'api'),
(31, 'se'),
(32, 'ee'),
(33, 'key-value'),
(34, 'rest')
;

insert into projecttolabel(project_id , project_label_id) values('spring-boot', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-boot', select id from projectlabel where label='mvc');
insert into projecttolabel(project_id , project_label_id) values('spring-boot', select id from projectlabel where label='rest');
insert into projecttolabel(project_id , project_label_id) values('spring-boot', select id from projectlabel where label='microservices');
insert into projecttolabel(project_id , project_label_id) values('spring-boot', select id from projectlabel where label='data');
insert into projecttolabel(project_id , project_label_id) values('spring-boot', select id from projectlabel where label='tools');

insert into projecttolabel(project_id , project_label_id) values('spring-data-gemfire', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-data-gemfire', select id from projectlabel where label='cache');
insert into projecttolabel(project_id , project_label_id) values('spring-data-gemfire', select id from projectlabel where label='nosql');

insert into projecttolabel(project_id , project_label_id) values('spring-mobile', select id from projectlabel where label='mobile');
insert into projecttolabel(project_id , project_label_id) values('spring-mobile', select id from projectlabel where label='client');

insert into projecttolabel(project_id , project_label_id) values('spring-data-solr', select id from projectlabel where label='search');
insert into projecttolabel(project_id , project_label_id) values('spring-data-solr', select id from projectlabel where label='nosql');
insert into projecttolabel(project_id , project_label_id) values('spring-data-solr', select id from projectlabel where label='big-data');

insert into projecttolabel(project_id , project_label_id) values('spring-hadoop', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-hadoop', select id from projectlabel where label='batch');
insert into projecttolabel(project_id , project_label_id) values('spring-hadoop', select id from projectlabel where label='nosql');

insert into projecttolabel(project_id , project_label_id) values('spring-ldap', select id from projectlabel where label='security');

insert into projecttolabel(project_id , project_label_id) values('spring-social-facebook', select id from projectlabel where label='social');
insert into projecttolabel(project_id , project_label_id) values('spring-social-facebook', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-social-facebook', select id from projectlabel where label='oauth');

insert into projecttolabel(project_id , project_label_id) values('spring-social-twitter', select id from projectlabel where label='social');
insert into projecttolabel(project_id , project_label_id) values('spring-social-twitter', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-social-twitter', select id from projectlabel where label='oauth');

insert into projecttolabel(project_id , project_label_id) values('spring-social-linkedin', select id from projectlabel where label='social');
insert into projecttolabel(project_id , project_label_id) values('spring-social-linkedin', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-social-linkedin', select id from projectlabel where label='oauth');

insert into projecttolabel(project_id , project_label_id) values('spring-social-github', select id from projectlabel where label='social');
insert into projecttolabel(project_id , project_label_id) values('spring-social-github', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-social-github', select id from projectlabel where label='oauth');

insert into projecttolabel(project_id , project_label_id) values('spring-social', select id from projectlabel where label='social');
insert into projecttolabel(project_id , project_label_id) values('spring-social', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-social', select id from projectlabel where label='oauth');

insert into projecttolabel(project_id , project_label_id) values('spring-social-tripit', select id from projectlabel where label='oauth');
insert into projecttolabel(project_id , project_label_id) values('spring-social-tripit', select id from projectlabel where label='social');
insert into projecttolabel(project_id , project_label_id) values('spring-social-tripit', select id from projectlabel where label='web');

insert into projecttolabel(project_id , project_label_id) values('spring-data', select id from projectlabel where label='nosql');
insert into projecttolabel(project_id , project_label_id) values('spring-data', select id from projectlabel where label='search');
insert into projecttolabel(project_id , project_label_id) values('spring-data', select id from projectlabel where label='batch');
insert into projecttolabel(project_id , project_label_id) values('spring-data', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-data', select id from projectlabel where label='cache');
insert into projecttolabel(project_id , project_label_id) values('spring-data', select id from projectlabel where label='jdbc');
insert into projecttolabel(project_id , project_label_id) values('spring-data', select id from projectlabel where label='jpa');

insert into projecttolabel(project_id , project_label_id) values('spring-data-graph', select id from projectlabel where label='graph');
insert into projecttolabel(project_id , project_label_id) values('spring-data-graph', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-data-graph', select id from projectlabel where label='nosql');

insert into projecttolabel(project_id , project_label_id) values('spring-data-neo4j', select id from projectlabel where label='graph');
insert into projecttolabel(project_id , project_label_id) values('spring-data-neo4j', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-data-neo4j', select id from projectlabel where label='nosql');

insert into projecttolabel(project_id , project_label_id) values('spring-android', select id from projectlabel where label='client');
insert into projecttolabel(project_id , project_label_id) values('spring-android', select id from projectlabel where label='mobile');

insert into projecttolabel(project_id , project_label_id) values('spring-cloud-dataflow', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud-dataflow', select id from projectlabel where label='batch');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud-dataflow', select id from projectlabel where label='stream');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud-dataflow', select id from projectlabel where label='cloud');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud-dataflow', select id from projectlabel where label='analytics');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud-dataflow', select id from projectlabel where label='iot');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud-dataflow', select id from projectlabel where label='integration');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud-dataflow', select id from projectlabel where label='messaging');

insert into projecttolabel(project_id , project_label_id) values('spring-xd', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-xd', select id from projectlabel where label='batch');
insert into projecttolabel(project_id , project_label_id) values('spring-xd', select id from projectlabel where label='stream');
insert into projecttolabel(project_id , project_label_id) values('spring-xd', select id from projectlabel where label='cloud');
insert into projecttolabel(project_id , project_label_id) values('spring-xd', select id from projectlabel where label='analytics');
insert into projecttolabel(project_id , project_label_id) values('spring-xd', select id from projectlabel where label='iot');
insert into projecttolabel(project_id , project_label_id) values('spring-xd', select id from projectlabel where label='integration');
insert into projecttolabel(project_id , project_label_id) values('spring-xd', select id from projectlabel where label='messaging');


insert into projecttolabel(project_id , project_label_id) values('spring-shell', select id from projectlabel where label='client');
insert into projecttolabel(project_id , project_label_id) values('spring-shell', select id from projectlabel where label='tools');

insert into projecttolabel(project_id , project_label_id) values('platform', select id from projectlabel where label='tools');

insert into projecttolabel(project_id , project_label_id) values('spring-plugin', select id from projectlabel where label='tools');

insert into projecttolabel(project_id , project_label_id) values('spring-cloud', select id from projectlabel where label='microservices');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud', select id from projectlabel where label='rest');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud', select id from projectlabel where label='api');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud', select id from projectlabel where label='cloud');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud', select id from projectlabel where label='integration');
insert into projecttolabel(project_id , project_label_id) values('spring-cloud', select id from projectlabel where label='messaging');

insert into projecttolabel(project_id , project_label_id) values('spring-hateoas', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-hateoas', select id from projectlabel where label='rest');
insert into projecttolabel(project_id , project_label_id) values('spring-hateoas', select id from projectlabel where label='api');
insert into projecttolabel(project_id , project_label_id) values('spring-hateoas', select id from projectlabel where label='client');

insert into projecttolabel(project_id , project_label_id) values('spring-data-jpa', select id from projectlabel where label='jpa');
insert into projecttolabel(project_id , project_label_id) values('spring-data-jpa', select id from projectlabel where label='jdbc');
insert into projecttolabel(project_id , project_label_id) values('spring-data-jpa', select id from projectlabel where label='sql');

insert into projecttolabel(project_id , project_label_id) values('spring-flex', select id from projectlabel where label='client');

insert into projecttolabel(project_id , project_label_id) values('spring-batch', select id from projectlabel where label='batch');
insert into projecttolabel(project_id , project_label_id) values('spring-batch', select id from projectlabel where label='nosql');
insert into projecttolabel(project_id , project_label_id) values('spring-batch', select id from projectlabel where label='sql');
insert into projecttolabel(project_id , project_label_id) values('spring-batch', select id from projectlabel where label='jpa');
insert into projecttolabel(project_id , project_label_id) values('spring-batch', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-batch', select id from projectlabel where label='integration');

insert into projecttolabel(project_id , project_label_id) values('spring-integration-dsl-scala', select id from projectlabel where label='tools');

insert into projecttolabel(project_id , project_label_id) values('spring-integration-dsl-groovy', select id from projectlabel where label='tools');

insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='integration');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='messaging');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='rest');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='ee');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='se');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='jpa');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='jdbc');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='api');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='data');
insert into projecttolabel(project_id , project_label_id) values('spring-framework', select id from projectlabel where label='test');

insert into projecttolabel(project_id , project_label_id) values('spring-security', select id from projectlabel where label='security');
insert into projecttolabel(project_id , project_label_id) values('spring-security', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-security', select id from projectlabel where label='api');

insert into projecttolabel(project_id , project_label_id) values('spring-webflow', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-webflow', select id from projectlabel where label='mvc');
insert into projecttolabel(project_id , project_label_id) values('spring-webflow', select id from projectlabel where label='client');

insert into projecttolabel(project_id , project_label_id) values('spring-scala', select id from projectlabel where label='tools');

insert into projecttolabel(project_id , project_label_id) values('spring-security-kerberos', select id from projectlabel where label='security');

insert into projecttolabel(project_id , project_label_id) values('spring-security-oauth', select id from projectlabel where label='security');

insert into projecttolabel(project_id , project_label_id) values('spring-ws', select id from projectlabel where label='api');
insert into projecttolabel(project_id , project_label_id) values('spring-ws', select id from projectlabel where label='web');

insert into projecttolabel(project_id , project_label_id) values('spring-amqp', select id from projectlabel where label='integration');
insert into projecttolabel(project_id , project_label_id) values('spring-amqp', select id from projectlabel where label='messaging');

insert into projecttolabel(project_id , project_label_id) values('spring-data-mongodb', select id from projectlabel where label='nosql');
insert into projecttolabel(project_id , project_label_id) values('spring-data-mongodb', select id from projectlabel where label='data');
insert into projecttolabel(project_id , project_label_id) values('spring-data-mongodb', select id from projectlabel where label='document');

insert into projecttolabel(project_id , project_label_id) values('spring-data-couchbase', select id from projectlabel where label='nosql');
insert into projecttolabel(project_id , project_label_id) values('spring-data-couchbase', select id from projectlabel where label='document');
insert into projecttolabel(project_id , project_label_id) values('spring-data-couchbase', select id from projectlabel where label='data');

insert into projecttolabel(project_id , project_label_id) values('spring-data-jdbc-ext', select id from projectlabel where label='jdbc');
insert into projecttolabel(project_id , project_label_id) values('spring-data-jdbc-ext', select id from projectlabel where label='sql');
insert into projecttolabel(project_id , project_label_id) values('spring-data-jdbc-ext', select id from projectlabel where label='data');

insert into projecttolabel(project_id , project_label_id) values('spring-roo', select id from projectlabel where label='tools');

insert into projecttolabel(project_id , project_label_id) values('spring-integration', select id from projectlabel where label='messaging');
insert into projecttolabel(project_id , project_label_id) values('spring-integration', select id from projectlabel where label='integration');

insert into projecttolabel(project_id , project_label_id) values('spring-session', select id from projectlabel where label='web');

insert into projecttolabel(project_id , project_label_id) values('spring-data-rest', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-data-rest', select id from projectlabel where label='api');
insert into projecttolabel(project_id , project_label_id) values('spring-data-rest', select id from projectlabel where label='data');
insert into projecttolabel(project_id , project_label_id) values('spring-data-rest', select id from projectlabel where label='nosql');
insert into projecttolabel(project_id , project_label_id) values('spring-data-rest', select id from projectlabel where label='sql');

insert into projecttolabel(project_id , project_label_id) values('spring-data-redis', select id from projectlabel where label='key-value');
insert into projecttolabel(project_id , project_label_id) values('spring-data-redis', select id from projectlabel where label='data');
insert into projecttolabel(project_id , project_label_id) values('spring-data-redis', select id from projectlabel where label='cache');
insert into projecttolabel(project_id , project_label_id) values('spring-data-redis', select id from projectlabel where label='nosql');

insert into projecttolabel(project_id , project_label_id) values('spring-data-elasticsearch', select id from projectlabel where label='search');
insert into projecttolabel(project_id , project_label_id) values('spring-data-elasticsearch', select id from projectlabel where label='nosql');
insert into projecttolabel(project_id , project_label_id) values('spring-data-elasticsearch', select id from projectlabel where label='big-data');
insert into projecttolabel(project_id , project_label_id) values('spring-data-elasticsearch', select id from projectlabel where label='data');

insert into projecttolabel(project_id , project_label_id) values('spring-security-saml', select id from projectlabel where label='security');

insert into projecttolabel(project_id , project_label_id) values('spring-data-envers', select id from projectlabel where label='data');

insert into projecttolabel(project_id , project_label_id) values('spring-test-mvc', select id from projectlabel where label='test');
insert into projecttolabel(project_id , project_label_id) values('spring-test-mvc', select id from projectlabel where label='web');
insert into projecttolabel(project_id , project_label_id) values('spring-test-mvc', select id from projectlabel where label='mvc');
insert into projecttolabel(project_id , project_label_id) values('spring-test-mvc', select id from projectlabel where label='rest');
