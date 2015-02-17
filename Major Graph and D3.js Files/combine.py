import subprocess
import os
import webbrowser
import sys

f = open('nodes.txt', 'w')
f.close()
f1 = open('relations.txt', 'w')
f1.close()
vol = ['python', 'add_to_graph.py', '', '', '', '']
vol[4] = sys.argv[1]
vol[5] = sys.argv[2]
vol[2] = 'Chapter/Agricultural_Practices.txt'
vol[3] = '2'
p = subprocess.Popen(vol, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
out, err = p.communicate()
vol[2] = 'Chapter/Sowing.txt'
vol[3] = str(int(out))
p = subprocess.Popen(vol, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
out, err = p.communicate()
vol[2] = 'Chapter/Harvesting.txt'
vol[3] = str(int(out))
p = subprocess.Popen(vol, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
out, err = p.communicate()
vol[2] = 'Chapter/Irrigation.txt'
vol[3] = str(int(out))
p = subprocess.Popen(vol, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
out, err = p.communicate()
vol[2] = 'Chapter/Protection_from_Weeds.txt'
vol[3] = str(int(out))
p = subprocess.Popen(vol, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
out, err = p.communicate()
vol[2] = 'Chapter/Storage.txt'
vol[3] = str(int(out))
p = subprocess.Popen(vol, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
out, err = p.communicate()

with open('nodes.txt', 'rb+') as filehandle:
    filehandle.seek(-2, os.SEEK_END)
    filehandle.truncate()

with open('relations.txt', 'rb+') as filehandle:
    filehandle.seek(-2, os.SEEK_END)
    filehandle.truncate()

orig_stdout = sys.stdout
f2 = open("new_graph.json", "w")
sys.stdout = f2

print """{
	"graph": [],
	"links": [ """

f4 = open('relations.txt', 'r')
print f4.read()

print "],"
print ''' "nodes": [ 
	{"id": "Book", "type": "circle"},'''

f5 = open('nodes.txt', 'r')
print f5.read()

print '''],
  "directed": false,
  "multigraph": false
}'''
f2.close()
webbrowser.open('story.html')
