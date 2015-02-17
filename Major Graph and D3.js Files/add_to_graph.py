#!/usr/bin/env python
import sys
import json
from py2neo import neo4j

def parse_output(filename):
    file = open(filename, 'r')
    extractions = file.readlines()
    pairs = []
    print "running"
    for extraction in extractions:
	extraction.strip("\n")
        pairs.append(extraction.split("\t"))
	#print extraction.split("\t")
    add_to_database(pairs)
    return pairs

def add_to_database(pairs):
    #graph_db = neo4j.GraphDatabaseService('http://localhost:7474/db/data/')
    #concepts = graph_db.get_or_create_index(neo4j.Node, 'concepts')
    #relations = graph_db.get_or_create_index(neo4j.Relationship, 'relations')
    i = 0
    arr = []
    data = []
    test = []
    for p in pairs:
        #n1 = concepts.get_or_create('concept_name', p[0], {'name': p[0]})
        #n2 = concepts.get_or_create('concept_name', p[2], {'name': p[2]})

        #rel_tup = (n1, p[1], n2)
        #relations.get_or_create('relation_name', '%s %s %s' % rel_tup, rel_tup)
	arr.append([])
	arr[i].append(p[0])
	arr[i].append(0)
	i=i+1
   # print arr
    for p in pairs:
    	for a in arr:
    		if((p[0] in a) == True):
			a[1] = a[1] + 1
    #print arr
    max = 0
    count = 0
    for b in arr:
	if(b[1]>max):
		max = b[1]
    #print max
    i=0
    j=0
    for c in arr:
    	for b in arr:
		if(b[1] == max-i):
			test.append(pairs[count][0])
			if((pairs[count][0] in data)== False):
				data.append(pairs[count][0])
				j = j+1
				#print j
		count = count+1
	i = i+1
	count = 0
    #print data
    orig_stdout = sys.stdout
    f = open("/home/chanakya/NetBeansProjects/Concepto/web/new_graph.json", "w")
    sys.stdout = f
    print """{
	"graph": [],
	"links": [
{"source": 0, "rel": "Main topic", "target": 1},""" 
    s = 1
    t = 2
    i = 0
    j = 0
    #print test
    for d in test:
	
	if(data[i]==test[j]):
		print "{\"source\":" +str(s) + ", \"rel\": \"" + pairs[j][1] + "\", \"target\": " + str(t) + "},"		
		t = t+1
		j = j+1	
		#print j
	else:
		print "{\"source\":" + "0" + ", \"rel\": \"" + "Topic" + "\", \"target\": " + str(t) + "},"
		s = t+1
		t = s+1
		i = i+1
		print "{\"source\":" +str(s-1) + ", \"rel\": \"" + pairs[j][1] + "\", \"target\": " + str(t-1) + "},"
    print "],"
    print "\"nodes\": ["
    i = 0
    j = 0
    t = 0
    for d in test:
	
	if(data[i]==test[j]):
		if(t==0):
			print "{\"id\": \"" +pairs[j][0] + "\", \"type\": \"circle\"},"
			t=1
		print "{\"id\": \"" +pairs[j][2].rstrip('\n') + "\", \"type\": \"circle\"},"		
		j = j+1	
		#print j
	else:
		t = 0
		i = i+1
		print "{\"id\": \"" +pairs[j][0] + "\", \"type\": \"circle\"},"
		print "{\"id\": \"" +pairs[j][2].rstrip('\n') + "\", \"type\": \"circle\"},"	
    
    print '''],
  "directed": false,
  "multigraph": false
}'''
if __name__ == '__main__':
    if len(sys.argv) < 2:
        print 'No file specified.'
    else:
        parse_output(sys.argv[1])
        