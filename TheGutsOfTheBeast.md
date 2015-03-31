# Introduction #

The Validator Service is a J2EE application, packaged to deploy&run on Tomcat servers. Tests are implemented as a series of stylesheet (XSL) transformations. This application has a web user interface (no command line or standalone application at the moment).
The current version of the application features logging and file management capabilities using industry-standard [Apache projects](http://www.apache.org/).



## Architecture ##

The application consists of 2 main building blocks.
  * servlets and JSP
  * business services
Business services work as abstraction entities dealing with the internal singularities of specific formats or processes. Servlets and Java Server Pages handle user requests / interaction.

The solution is built on top of servlets 3.0 and the ideal application stack is Java 7+ and Tomcat 7+. While I would love to see this baby running on [WebLogic](http://www.oracle.com/technetwork/middleware/weblogic/overview/index.html), this application requires a servlet container implementing the Servlet 3.0 specification. WLS 10.3.6 implements v2.5.



### Recent overhaul ###

In April 2014, the structure and code of this application was changed dramatically to improve efficiency, make it easier to maintain and extend. Logging was introduced using a standard framework to facilitate debugging and troubleshooting.



### Industry packages used ###

File upload through [Apache Commons FileUpload](http://commons.apache.org/proper/commons-fileupload/)

Logging through [Log4j2](http://logging.apache.org/log4j/2.x/)

JSP code including [JSTL](https://jstl.java.net/)

As mentioned in the introduction, tests are implemented using [stylesheets](http://www.w3.org/TR/xslt)



# Package and Code Organisation #

![http://analytics-validator-service.googlecode.com/svn/trunk/CodeOrganisation.png](http://analytics-validator-service.googlecode.com/svn/trunk/CodeOrganisation.png)


### Packages: ###

  * org.validator.services: business services such as the validation (test) engine, test loader and metadata loaders.

  * org.validator.servlets: all servlets handling user requests.

  * org.validator.utils: all other Plain Old Java Classes (Objects) handling XML and access to filesystem.


### Other components: ###

  * WEB-INF: Web interface implemented using Java Server Pages.

  * WEB-INF/Views: stylesheets used to produce result pages (UI).

  * WEB-INF/Tests: stylesheets used to validate metadata.

  * WEB-INF/lib: Apache Commons and JSTL libraries (jar files).