# Build or download the AIS client
To get the binary package of the AIS client library, you can either build it yourself or download it from the _Releases_ 
section of this repository.

## Build the client
The AIS client library can be built using Maven. Clone the repository in a local folder of yours and run this command:

```shell
mvn install
```

Maven will build the final packages and install them in your repository. You can also find them in the _target_ folder in the location
where you run the above command. There are 2 packages that are built:

- the _target/itext7-ais-X.X.X.jar_, which contains the binary files of the AIS client, without any of the dependencies
- the _target/release_, which is a folder containing the AIS client in a standalone form, together with all the dependencies (the 
  _libs_ folder) and the Unix and Windows scripts needed to run it. 

## Download the client
The AIS client library can also be downloaded directly, without having to build it yourself. Just head over to the _Releases_ section of
this repository and download the latest version. The package contains the Unix and Windows scripts needed to run the CLI of the AIS client
and all the needed dependencies.
