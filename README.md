tcs-hq-product-plugin
=====================

This is the Pivotal tc Server Hyperic monitoring plugin. This version supports Hyperic 5.0+ and tc Server 3.0 or newer.

Build Instructions
==================

This project depends on maven 3.0 and Java 1.6.

To build the plugin:
$ mvn clean package

To publish to a s3 repository:
$ mvn clean package publish -Ds3.url=<your s3 url>

You will need to configure your ~/.m2/settings.xml similar to the following:

```xml
 <server>
  <id>s3.release</id>
  <username>REPLACE_WITH_S3_USERNAME</username>
  <password>REPLACE_WITH_S3_SECRET</password>
 </server>
```

Hyperic Deployment
==================

This plugin is intended to co-exist with the springsource-tcserver-plugin.jar which ships with Hyperic. It is not necessary
to undeploy that plugin to install this one.

Be sure the plugin name is pivotal-tcserver-plugin.jar, otherwise Hyperic may experience an issue deploying the plugin.

Use Plugin Manager to deploy the plugin. 

License
=======
This project is licensed under the Apache 2.0 license. See LICENSE and NOTICE for details.
