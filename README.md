### What is Pathweaver
-------------
Pathweaver is Daylight's L4-L7 Service framework that is actively being developed.

### Wiki
--------
http://wiki.daylight.org/Pathweaver

### Requirements
----------------
1. Java >= 1.5  (note: Java 1.7 seems to have a compatibility issue with JAXB, so until we fix this, better to be avoided).

2. Apache Maven == 2.2.1

3. Apache ActiveMQ == 5.5.0

    Run ActiveMQ on default port.

    `java -jar activemq/bin/run.jar start`

4. MySQL >= 5.x

    * Create a MySQL database named 'loadbalancing'. 
    
    * Optionally create another database called 'loadbalancingplugin' if your plugin extends the default LoadBalancerPluginBase class (instead of simply implementing the LoadBalancerPlugin interface). See the null (fake) plugin as an example for an plugin that uses the default base implementation.

5. Glassfish == 3.0.1 (Optional as Pathweaver comes with an embedded jetty server)


### Getting Started for development
-----------------------------------
1. Build

    Grab the pathweaver/core-api/core-public-web/src/deb/contrib/maven/settings.xml and put it inside your
    ~/.m2 directory:

    `cd pathweaver`

    `./bin/build.sh`

2. Configure

    Copy all of the configuration files from pathweaver/core-api/core-public-web/src/deb/contrib/config
    and put it under your /etc/daylight/pathweaver directory.
    
    Make sure you update /etc/daylight/pathweaver/public-api.conf with:
    
      * your database username/password.
      * If you are not using null (fake) plugin which is the default one configured, choose another plugin.
    

3. Start pathweaver:

    `./bin/run.sh`

    To run the app into debug mode on port 8080 with embedded jetty:

    `./bin/debug.sh`

4. If pathweaver is properly started, it should have created the necessary database tables for you. 

   * Seed the 'loadbalancing' database with some fake data. A sample for testing is here: pathweaver/core-api/core-public-web/src/deb/contrib/db/ directory.
   
   * If your plugin is relying on the default strategy for managing its devices (extending LoadBalancerPluginBase), also seed the 'loadbalancingplugin' database with some fake data (your vips, your devices, etc.). A sample for testing used by the netscaler plugin can be found here: ./core-plugins/core-netscaler-plugin/src/main/resources/core-netscaler-plugin-seed.sql
   
   You should modify the SQL data in these files to match your deployment details. For the 'loadbalancingplugin' DB, the passwords of your hosts (loadbalancing devices) are stored in an encrypted form in your 'plugin_host' table. You can use the 'cryptotool.sh to encrypt a password and store in the database using the following command:
   
       bin/cryptotool.sh -enc <password_in_clear>
    
   Similarly to decrypt a value found in the DB, you can use the following command can be used:
   
       bin/cryptotool.sh -dec <encrypted_password>  
   
   The encryption/decryption key used is the one configured in your public-api.conf file.
   
   

Now you can access the Pathweaver REST API eg. do a GET on [http://localhost:8080/v1.1/1000/loadbalancers](http://localhost:8080/v1.1/1000/loadbalancers)
where 1000 is a tenant_id. What does it return? May be it's time to do a POST. More more operations, [http://wiki.daylight.org/Pathweaver](http://wiki.daylight.org/Pathweaver)


### Deploy under Glassfish Application Server
---------------------------------------------

Alternative to the step 3 above, if you want to use an Application Server like Glassfish instead of the embedded jetty, you could:

    /glassfish/glassfish/bin/asadmin start-domain

    cp pathweaver/core-api/core-public-web/target/core-public-web-1.1.0-SNAPSHOT.war /glassfish/glassfish/domains/domain1/autodeploy



For more information about daylight, go to [daylight.org](http://daylight.org)

