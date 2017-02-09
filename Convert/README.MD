#Wavefront Convert Framework

This is a framework, and implementation to convert other metrics monitoring solutions into equivalent Wavefront artifacts.  
The system works by _Converting_ existing artifacts into equivalent Wavefront models, then _Generating_ the artifacts on the Wavefront platform from the models.
Part of the conversaion process is also an ExpressionBuilder which allows you to convert the metric names into what they will be in Wavefront.  
Some systems have various limitations on what the metric names can be, or pershaps during the conversion process from one platform to another there will be manipulation of that name.  
The ExpressionBuilder will do this conversion.


##Supported Source Platforms
Currently there is support for the following platforms to be converted into Wavefront.  More platforms will be added in the future.
- RRD

