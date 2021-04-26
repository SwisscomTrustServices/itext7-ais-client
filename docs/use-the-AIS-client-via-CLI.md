# Use the AIS client via CLI
The AIS client can be used from the command line as a standalone tool.

## Usage
The following is a help listing with the available parameters: 
```text
--------------------------------------------------------------------------------
Swisscom AIS Client - Command line interface
--------------------------------------------------------------------------------
Usage: ./bin/ais-client.sh [OPTIONS]

Options:
    -init                                               - Create sample configuration files in the current folder

    -input [FILE]                                       - Source PDF file to sign. You can use this parameter several times, to sign multiple
                                                          documents at once. Using multiple inputs forces the usage of suffix (see below) as it is
                                                          difficult to give both the input and the output files as parameters.

    -output [FILE]                                      - Output PDF file, where the signed document should be written. This parameter can be
                                                          used only when one single input is given. For more than one input, use suffix.

    -suffix [SUFFIX]                                    - Suffix for output file(s), composed using the input file plus the suffix, as alternative to
                                                          specifying the output file entirely. Default is "-signed-#time", where "#time" is replaced
                                                          with current time (yyyyMMdd-hhMMss).

    -type [static|ondemand|ondemand-stepup|timestamp]   - The type of signature to create

    -config [PROPERTIES FILE]                           - The properties file that provides the extra configuration parameters. Use -init to create a
                                                          sample file. If you don't specify a file, by default sign-pdf.properties is used.

    -help                                               - This help text
    
    -v                                                  - Be verbose about what is going on (sets Logback config to info)

    -vv                                                 - Be EXTRA verbose about what is going on (sets Logback config to debug, without HTTP and TLS traffic)

    -vvv                                                - Be EXTRA-EXTRA verbose about what is going on (sets Logback config to debug, with HTTP and TLS traffic)

Use cases:
    1. > ./bin/ais-client.sh -init   => Have the config files generated for you in the current folder
    2. Edit the files accordingly
    3. > ./bin/ais-client.sh -type timestamp -config config.properties -input fileIn.pdf -output fileOut.pdf
    4. > ./bin/ais-client.sh -type timestamp -input file1.pdf -input file2.pdf -input file3.pdf
```

Use the _-init_ parameter to create a set of configuration files in the local folder. These files can then be customized for your own case.

Use the _-v_, _-vv_ and _-vvv_ parameters to have more detailed logs in the console, including the exchanged HTTP request and response fragments. 

## Examples
Start with a fresh set of configuration files:
```shell
./bin/ais-client.sh -init
```

Timestamp a PDF document:
```shell
./bin/ais-client.sh -type timestamp -input local-sample-doc.pdf -output timestamp-output.pdf
```

Timestamp several PDF documents at once:
```shell
./bin/ais-client.sh -v -type timestamp -input doc1.pdf -input doc2.pdf -input doc3.pdf -config config.properties
```

Sign a PDF document with a static signature:
```shell
./bin/ais-client.sh -v -type static -input doc1.pdf
```

Sign a PDF document with a static signature with extra logging info and specifying the output and config files:
```shell
./bin/ais-client.sh -vv -type static -input doc1.pdf -output doc1Out.pdf -config "/custom/sign-pdf.properties"
```

Sign a PDF document with an On Demand signature:
```shell
./bin/ais-client.sh -type ondemand -input doc1.pdf -output doc1Out.pdf
```

Sign a PDF document with an On Demand signature with Step Up and complete extra logging:
```shell
./bin/ais-client.sh -vvv -type ondemand-stepup -input local-sample-doc.pdf -output signature-output.pdf
```

Notes:

- if you use more input files, then the _output_ parameter cannot be used. Instead, you can rely on the _suffix_ parameter to define a 
  suffix to be added to the name of the input files when generating the output ones. For the _suffix_, you can also use a token like 
  _#time_ to have it replaced with the current date and time. By default, the _suffix_ is "-signed-#time".
  