#how to use?
# this is modifiying whisper-fetch to read each whisper file and sending it to wavefront proxy. I'm using port 2003 because i'm too lazy to create a ACL. Please change the port as well.


1) find all whisper files and append to a list:
find -type f  /data/graphite/storage/whisper/ >> file.out


2) adjust wavefront server to your server. Line 24.

3) edit the pathshortname to your graphite whisper path
4) edit get_tag function to match your tagging. Hopefully the naming metric can be converted to tags. 


5)run script: python graphite-wavefront.py file.out


*side note. i'm not sending all metrics. I only picked the metrics that matter to us. That is the reason why it's a big "if" statement. 


