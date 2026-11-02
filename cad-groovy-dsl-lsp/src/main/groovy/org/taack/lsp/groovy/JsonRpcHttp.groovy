package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler
import org.eclipse.lsp4j.jsonrpc.messages.*
import org.eclipse.lsp4j.jsonrpc.services.GenericEndpoint
import org.eclipse.lsp4j.jsonrpc.services.ServiceEndpoints
import org.eclipse.lsp4j.services.LanguageServer

import java.util.concurrent.ExecutionException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * JSON-RPC over HTTP, one message per POST, as the Taack editor sends them.
 */
@CompileStatic
final class JsonRpcHttp {
    private static final Logger log = Logger.getLogger(JsonRpcHttp.name)
    private static final MessageJsonHandler jsonHandler = new MessageJsonHandler(ServiceEndpoints.getSupportedMethods(LanguageServer))

    private JsonRpcHttp() {}

    /**
     * @param body The JSON-RPC message, the body of the POST
     * @return The JSON response, null for a notification
     */
    static String handle(String body, LanguageServer server) {
        Message message = jsonHandler.parseMessage(body)
        GenericEndpoint endpoint = new GenericEndpoint(server)

        if (message instanceof NotificationMessage) {
            NotificationMessage notification = (NotificationMessage) message
            endpoint.notify(notification.method, notification.params)
            return null
        }
        if (!(message instanceof RequestMessage)) return null

        RequestMessage request = (RequestMessage) message
        ResponseMessage response = new ResponseMessage()
        response.jsonrpc = '2.0'
        response.rawId = request.rawId
        try {
            response.result = endpoint.request(request.method, request.params).get()
        } catch (ExecutionException e) {
            Throwable cause = e.cause
            if (cause instanceof ResponseErrorException) {
                response.error = ((ResponseErrorException) cause).responseError
            } else {
                log.log(Level.WARNING, "LSP ${request.method} failed", cause)
                response.error = new ResponseError(ResponseErrorCode.InternalError, String.valueOf(cause?.message), null)
            }
        }
        jsonHandler.serialize(response)
    }
}
