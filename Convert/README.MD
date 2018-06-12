# Wavefront Convert Framework

This is a framework, and implementation to convert other metrics monitoring solutions into equivalent Wavefront artifacts. The system works by _Converting_ existing artifacts into equivalent Wavefront models, then _Generating_ the artifacts on the Wavefront platform from the models. Part of the conversaion process is also an ExpressionBuilder which allows you to convert the metric names into what they will be in Wavefront. Some systems have various limitations on what the metric names can be, or pershaps during the conversion process from one platform to another there will be manipulation of that name.  The ExpressionBuilder will do this conversion.


## Supported Source Platforms
Currently there is support for the following platforms to be converted into Wavefront.  More platforms will be added in the future.
- RRD
- Grapher
- Datadog


## Dependencies
Depends on the Wavefront Java SDK which can be generated using swagger-codegen. Sample config and script to generate can be found in swagger folder.


## Framework Architecture
This framework is meant to be layered in that a higher level converter can use the facilities of a lower level one as needed. The Grapher converter makes use of RRD since the internal tooling of Grapher is based on RRD chart definitions and tooling. 


## General Usage
The main Convert class part of the Core package takes 2 main arguments when run via the command line.
1) Path to a properties file
2) The item to convert

`java -cp wavefront-convert.jar com.wavefront.labs.convert.Convert <properties-file> <item-to-convert>`


## Properties
The following properties are part of the Core framework are are required for all conversion types
- `convert.converter`: Class which inherits from com.wavefront.labs.convert.Converter which will do the conversion
- `convert.generator`: Class which inherits from com.wavefront.labs.convert.Generator
- `convert.expression.builder`: Class which inherits from com.wavefront.labs.convert.ExpressionBuilder to manipulate metric name
- `convert.publish.url`: URL to your Wavefront instance (ie: https://try.wavefront.com)
- `convert.publish.token`: Your API token for Wavefront

Individual converters may also include additional properties.  Each converter will describe their respective properties.
