# Grafana Converter

Grafana is a popular open source visualization platform with plugin support for many timeseries systems.  
Grafana in itself does not provide a timeseries solution, as such you should specify an appropriate
Expression Builder class for whatever timeseries system is used in the dashboard definitions. 

This converter converts dashboards only.

## Grafana Properties
Core properties should have the following values
- `convert.converter=com.wavefront.labs.convert.converter.grafana.GrafanaConverter`

Use an appropriate Expression Builder, if you do not wish to rebuild the queries/expressions used by the 
visualization you can use the SimpleExpressionBuilder
- `convert.expressionBuilder=com.wavefront.labs.convert.SimpleExpressionBuilder`

 
