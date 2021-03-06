# Introduction

In the spirit of TDD this library is intended to help facilitate schema migrations where
table creation scripts, and alter commands are stored in XML and then applied to a
database.

## Overview

The mechanics are simple:

__Step 1__

Read database scripts from XML.  The script with table creation and alter commands
looks like this:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<databases>
    <database>
        <version>1</version>
        <script><![CDATA[
create table user_details (id int);
    ]]></script>
    </database>
    <database>
        <version>2</version>
        <script><![CDATA[
create table something_else (id int);
    ]]></script>
    </database>
</databases>
```

__Step 2__

The library will query the DB to determine the DB's current version via a version
history table.  After that it applies the list of scripts found in the above file
to the DB which cause it to arrive at a 'target version'.

The code using the library to migrate the DB:

```java
    Databases dbs = new XmlIO().read("sql/db-versions.xml", Databases.class);

    DbVersioning v = new DbVersioning(
        new DBI(applicationConnection()),
        new VersionScriptProvider(),
        new TransitionChecks(),
        MIGRATIONS_USER
    );

    v.toTargetVersion(dbs.getDatabases(), 2);
```

__Step 3__

Write tests that guarantee the updates applied via the 2 scripts, and the behavior which
the DB helps to facilitate for a given application.

##  Setup

Currently, the project isn't held in a MVN repo (hopefully I'll update that soon).  So,
it must be installed manually and then a POM dependency can be used by any code base. To
install it manually follow these steps:

- Install Dependencies: Java 8 and Maven.
- Clone this repo somewhere: `%> git clone [this-repo]`
- Run mvn package `%> mvn package`
- Run mvn install `%> mvn install`
- Use this Maven dependency in a project:

```xml
<dependency>
  <groupId>XplorJavaDB</groupId>
  <artifactId>XplorJavaDB</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

After that you should be able to run the code found in __Step 2__ above.







