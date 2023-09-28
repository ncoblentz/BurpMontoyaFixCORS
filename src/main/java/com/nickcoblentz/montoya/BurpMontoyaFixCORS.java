package com.nickcoblentz.montoya;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.http.sessions.ActionResult;
import burp.api.montoya.http.sessions.SessionHandlingAction;
import burp.api.montoya.http.sessions.SessionHandlingActionData;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction;
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction;
import com.nickcoblentz.montoya.utilities.LogHelper;
import org.json.JSONObject;

public class BurpMontoyaFixCORS  implements BurpExtension, ProxyResponseHandler {

    private MontoyaApi _api;
    private LogHelper _loghelper;



    public void initialize(MontoyaApi api) {
        _api = api;
        _loghelper = LogHelper.GetInstance(api);
        _loghelper.SetLevel(LogHelper.LogLevel.DEBUG);
        _loghelper.Info("Plugin Loading...");
        api.extension().setName("Fix CORS Proxy Response Handler");
        api.proxy().registerResponseHandler(this);
        _loghelper.Info("Plugin Loaded");
    }

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        return ProxyResponseReceivedAction.continueWith(interceptedResponse);
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        _loghelper.Debug("Handling Response..."+interceptedResponse.initiatingRequest().url());
        //isInScope always fails... must be a bug
        //if(interceptedResponse.initiatingRequest().isInScope())
        //{
            _loghelper.Debug("In Scope...");
            if(interceptedResponse.initiatingRequest().hasHeader("Origin"))
            {

                String origin = interceptedResponse.initiatingRequest().headerValue("Origin");
                _loghelper.Debug("Origin Found..."+origin);
                HttpResponse newResponse=interceptedResponse;

                if(!interceptedResponse.hasHeader("Access-Control-Allow-Origin"))
                {
                    _loghelper.Debug("Adding Allow Origin...");
                    newResponse = newResponse.withAddedHeader("Access-Control-Allow-Origin",origin);
                }

                if(!interceptedResponse.hasHeader("Access-Control-Allow-Headers"))
                {
                    _loghelper.Debug("Adding Allow Headers...");
                    newResponse = newResponse.withAddedHeader("Access-Control-Allow-Headers","Authorization, Origin, Content-Type");
                }

                if(!interceptedResponse.hasHeader("Access-Control-Expose-Headers"))
                {
                    _loghelper.Debug("Adding Expose Headers...");
                    newResponse=newResponse.withAddedHeader("Access-Control-Expose-Headers","_token");
                }
                if(!interceptedResponse.hasHeader("Access-Control-Allow-Methods"))
                {
                    _loghelper.Debug("Adding Allow Methods...");
                    newResponse=newResponse.withAddedHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTIONS");
                }

                return ProxyResponseToBeSentAction.continueWith(newResponse);
            }
        //}
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }

}