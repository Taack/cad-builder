"""
Server run by FreeCADCmd for the cad-dsl-freecad kernel: it connects to the JVM on 127.0.0.1:$CAD_DSL_PORT and
answers its requests, one JSON object per line.

A request is {"o": handle or null, "m": method, "a": [arguments]}; the answer {"r": result}, or {"e": message,
"t": traceback} when the method failed. Requests on a handle go to the model or assembly it names, the others to the
functions of cad_dsl_freecad. The server stops at the end of its input.
"""
import json
import os
import socket
import sys
import traceback

sys.path.insert(0, os.environ['CAD_DSL_LIB'])

import cad_dsl_freecad


def serve():
    connection = socket.create_connection(('127.0.0.1', int(os.environ['CAD_DSL_PORT'])))
    stream = connection.makefile('rwb')
    while True:
        line = stream.readline()
        if not line:
            break
        request = json.loads(line)
        try:
            answer = {'r': cad_dsl_freecad.dispatch(request.get('o'), request['m'], request.get('a') or [])}
        except Exception as e:
            answer = {'e': '%s: %s' % (type(e).__name__, e), 't': traceback.format_exc()}
        stream.write((json.dumps(answer) + '\n').encode('utf-8'))
        stream.flush()
    connection.close()


serve()
