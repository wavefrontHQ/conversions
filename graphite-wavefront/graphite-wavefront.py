#!/usr/bin/python
from datetime import datetime
from sys import argv
import sys
import re
import os
import time
import signal
import optparse
from socket import socket


if len(sys.argv) == 2:
    filename = argv[1]
    txt = open(filename)
else:
    print "must enter only 1 filename"
    sys.exit(2)


###wavefront send

def send_wave(list_wave):
    wavefront= "wfront-server"
    wavefront_port= 2003
    sock=socket()
    try:
        sock.connect( (wavefront,wavefront_port) )
    except:
        print "Couldn't connect to %(server)s on port %(port)d, is wavefront proxy running?" % { 'server':wavefront, 'port':wavefront_port}
        sys.exit(1)
    print ctime(), "Sent", len(list_wave), "metrics to wavefront"
    message = '\n'.join(list_wave)
    #message = '\n'.join(list_wave) + '\n'
    print message
    sock.sendall(message)
    sock.close()

def ctime():
    return datetime.strftime(datetime.now(), '%Y-%m-%d %H:%M:%S')

#### wavefront send


def get_tag(splitdir):
     tag01= splitdir[0]
     tag02= splitdir[1]
     tag03= splitdir[2]
     tag04= splitdir[3]
     tag05= splitdir[4]
     type= splitdir[5]
     hostname= splitdir[6]
     plugin = splitdir[7]
     tag = "tag01=%s tag02=%s tag03=%s tag04=%s tag05=%s" % (tag01,tag02,tag03,tag04,tag05)
     return (hostname,tag,plugin,type)

def get_acpu(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_context(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_df(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_disk(splitdir):
    if "pending_operations" in splitdir[-1]:
      wsp= splitdir[-1].replace(".wsp","")
      pathwsp2= splitdir[-2]
      metric= "%s.%s" % (pathwsp2,wsp)
      return metric
    else:
      wsp= splitdir[-1].replace(".wsp","")
      pathwsp2= splitdir[-3]+"."+splitdir[-2]
      metric= "%s.%s" % (pathwsp2,wsp)
      return metric

def get_interface(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-3]+"."+splitdir[-2]
    #pathwsp2= splitdir[-4]+"."+splitdir[-3]+"."+splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_load(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_memory(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_processes(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_protocols(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_swap(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_tail(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_uptime(splitdir):
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    metric= "%s.%s" % (pathwsp2,wsp)
    return metric

def get_newrelic(splitdir):
    #if "qbo" in splitdir[1]:
    wsp= splitdir[-1].replace(".wsp","")
    pathwsp2= splitdir[-2]
    pathwsp3= splitdir[-3]
    metric= "%s" % (pathwsp2)
    return metric
    


def mainscript():
    count=1
    prefix="test.collectd"
    with open(filename,"r") as ifile:
        for line in ifile:
            list_wave=[]
            print "line number is:",count,"\twhisper file:",line.rstrip()
            count +=1
            ##### edit this with your whisper path. every split will become a tag
            pathshortname = line.replace('/data/graphite/storage/whisper/','').rstrip()
            splitdir= pathshortname.split("/")
            ###### end
            path=line.rstrip()
            try:
                hostname,tag,plugin,type = get_tag(splitdir)
            except:
                print "skipping hostname parse failed"
                continue


            from_time = 1452697215
            #from_time = now - (60 * 60 * 24)

            until_time = now

            try:
              (timeInfo, values) = whisper.fetch(path, from_time, until_time)
            except whisper.WhisperException, exc:
              raise SystemExit('[ERROR] %s' % str(exc))
                #print line.rstrip()
            (start,end,step) = timeInfo

            
            t = start
            for value in values:
                timestr = str(t)
                if value is None:
                    t += step
                    continue
                    #valuestr = "None"
                else:
                    valuestr = "%f" % value
                    #print line
                    #print hostname,tag,plugin
                    #print("%s\t%s" % (timestr,valuestr))
                    if "aggregation-cpu-average" in plugin:
                        metric= get_acpu(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "context" in plugin:
                        metric= get_context(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "df" in plugin:
                        metric= get_df(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "disk" in plugin:
                        metric= get_disk(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "interface" in plugin:
                        metric= get_interface(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "load" in plugin:
                        metric= get_load(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "memory" in plugin:
                        metric= get_memory(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "processes" in plugin:
                        metric= get_processes(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "protocol" in plugin:
                        metric= get_protocols(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "swap" in plugin:
                        metric= get_swap(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "tail" in plugin:
                        metric= get_tail(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                    elif "uptime" in plugin:
                        metric= get_uptime(splitdir)
                        wave_metricname= "%s.%s %s %s source=%s %s" % (prefix,metric,valuestr,timestr,hostname,tag)
                        list_wave.append(wave_metricname)
                t += step
            send_wave(list_wave)
    

if __name__ == "__main__":
    try:
      import whisper
    except ImportError:
      raise SystemExit('[ERROR] Please make sure whisper is installed properly')
    
    # Ignore SIGPIPE
    signal.signal(signal.SIGPIPE, signal.SIG_DFL)
    
    now = int( time.time() )
    print now
    mainscript()
