"""
Server run by FreeCADCmd for the cad-dsl-freecad kernel: it connects to the JVM on 127.0.0.1:$CAD_DSL_PORT and
answers its requests, one JSON object per line.

A request is {"o": handle or null, "m": method, "a": [arguments]}; the answer {"r": result}, or {"e": message,
"t": traceback} when the method failed. Requests on a handle go to the model or assembly it names, the others to the
functions of cad_dsl_freecad. The server stops at the end of its input.

In the GUI, FreeCAD prints its messages in its report view: the errors come back in the answers.
"""
import json
import os
import socket
import sys
import traceback

sys.path.insert(0, os.environ['CAD_DSL_LIB'])

# The GUI, off screen (QT_QPA_PLATFORM=offscreen): the documents get their view, colors and visibility; FreeCADCmd
# alone when it cannot start, or with CAD_DSL_HEADLESS=true
if os.environ.get('CAD_DSL_HEADLESS') != 'true':
    try:
        import FreeCADGui
        FreeCADGui.showMainWindow()
        # Qt sets the numbers of the user's locale (fr_FR: 0,5): the preferences would be read wrong, the deviation
        # of the meshes of the views 0.5 as 0, and the curved faces meshed without end
        import locale
        locale.setlocale(locale.LC_NUMERIC, 'C')
    except Exception as e:
        print('cad-dsl-freecad: no FreeCAD GUI (%s), the documents have no colors' % e)

import cad_dsl_freecad
import faulthandler
faulthandler.dump_traceback_later(60, repeat=True, file=sys.__stderr__)


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
# now, without letting FreeCAD save its preferences (the GUI would save its window)
os._exit(0)
