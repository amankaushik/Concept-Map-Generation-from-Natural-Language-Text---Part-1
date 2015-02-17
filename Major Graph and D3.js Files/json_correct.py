content = ""
with open("/home/chanakya/NetBeansProjects/Concepto/web/new_graph.json") as f:
    for line in f:
        content += line
f.close()        
a = -1
points = []
while a <= len(content):
    points.append(content.find("]", a+1))
    a += content.find("]", a+1)

point_1 = points[-2]-2
point_2 = points[-1]-2

content = content[0:point_1] + content[(point_1 + 1):point_2] + content[(point_2 + 1):]

f = open('/home/chanakya/NetBeansProjects/Concepto/web/new_graph.json', 'w')
f.write(content)
f.close()