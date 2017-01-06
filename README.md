# Demo application for CD - Frontend
This example can be used as a demonstration application for Continuous Delivery in a container environment.

## Frameworks

### Front-end

#### Twitter Bootstrap
For rapidly creating prototypes of a web application, a UI toolkit or library will become really handy. There are many choices available, and for this example I chose Twitter Bootstrap.

#### AngularJS
AngularJS is a MVC based framework for web applications, written in JavaScript. It makes it possible to use the Model-View-Controller pattern on the front-end. 

### Back-end

#### Spring Boot
One of the hassles while creating web applications using the Spring Framework is that it involves a lot of configuration. Spring Boot makes it possible to write configuration-less web application because it does a lot for you out of the box.

## Installation
Installation is quite easy, first you will have to install some front-end dependencies using Bower:
```
bower install
```

Then you can run Gradle to package the application:
```
mvn clean bootRepackage
```

Now you can run the Java application quite easily:
```
java -jar build/libs/fe-1.0.0.jar
```

Alternatively, you could use Spring-boot to start the application in development mode:
```
gradle bootRun
```
## Docker
This project contains a Docker buildfile but it's not build-enabled.

## Correlation ID's
To demo correlation ID's this application accepts, logs and retransmits CID's. The partner backend, Demo-be, will accept the CID's and also log them as part of the log context.

# Obfuscate left
- Jenkinsfile
- Dockerfile