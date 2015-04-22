# Steps #

Prerequisites: the Analytics Validator Service requires Java version 7. To confirm the required Java version is available you can visit [Java Verify](http://www.java.com/verify/)

  1. Tomcat 7 installation
  1. Analytics Validator Service installation



## Apache Tomcat ##

Tomcat is an open source web server (and servlet container) developed by the [Apache Software Foundation](http://apache.org). We recommend running Analytics Validator Service on Tomcat version 7. Download the right version for your operating system.

The installation takes less than 5 minutes. Just follow the instructions of the wizard.


## Deploy Analytics Validator Service ##

  1. Download the latest version of the validator service ('war' file) from [BinTray](https://bintray.com/danielgalassi/oracle-bi-utilities/analytics-validator-service/view).
  1. Start Tomcat and go to http://localhost:8080/manager/http
  1. Scroll down to the **WAR file to deploy** section
  1. Select the WAR file and hit **Deploy**.
  1. Done!
