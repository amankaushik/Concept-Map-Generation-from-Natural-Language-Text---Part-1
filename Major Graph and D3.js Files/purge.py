from py2neo import Graph

graph_db = Graph('http://localhost:7474/db/data/')

graph_db.delete_all()
