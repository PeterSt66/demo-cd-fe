# Demo application for CD - Frontend
This example can be used as a demonstration application for Continuous Delivery in a container environment.
Based on ng-spring-boot (https://github.com/g00glen00b/ng-spring-boot)

## Frameworks

### Front-end

#### Twitter Bootstrap
For rapidly creating prototypes of a web application, a UI toolkit or library will become really handy. For this demo Twitter Bootstrap is choosen.

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
gradle clean bootRepackage
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

## Jenkins and Openshift
The Jenkinsfile (should be used from a multi-branch job) will build the code, package it and construct a Docker container.
Next it will try to deploy the app to Openshift, in one of two ways:

- Clean deploy - delete any existing deployment and rebuild and redeploy. If the branch != master the build will wait for confirmation (look in Jenkins) and proceeds to delete all. This mimics a setup for a popup instance, usefull for api- and integration testing through curl / soapui / other tooling

- Upgrade existing deployment - only done if it's the master branch and a deployment exists. It will do a rolling update. To force a rebuild delete the deployment config from openshift (not the running deployment, the config)

- Jenkins build depend on the OC binary to be available as /usr/bin/oc
