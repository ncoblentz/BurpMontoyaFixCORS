# BurpMontoyaFixCORS

This plugin was written for a particular application that didn't append the CORS HTTP response headers needed to interact with the API. I realized session actions/plugins can't add response headers. I learned match/replace proxy rules can't add newlines even when REGEX is checked. And some other similar limitations in existing plugins through the BAPP Store. So I wrote this to append those headers. If you want to use it for your application, you will need to modify the following lines to do what you wish:
```java
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
```

## How to Build It
### Setup Github Token to Access Packages

1. Log into your personal github account and create an access token that can "Read Packages"
2. `export GHUSERNAME="yourusernamehere"`
3. `export GHTOKEN="youraccestokenhere"`

### Customize
1. Review the URLs hardcoded into the plugin and adjust to fit the application you are testing
2. Review the way the access token is updated in the request and make sure it matches your application

### Build
#### Via Command-Line
```bash
$ ./gradlew fatJar
```
#### Via InteliJ
1. Open the project in Intellij
2. Open the Gradle sidebar on the right hand side
3. Choose Tasks -> Other -> fatJar

## How to add this plugin to Burp
1. Open Burp Suite
2. Go to Extensions -> Installed -> Add
    - Extension Type: Java
    - Extension file: build/libs/burpmontoyfixcors-0.1-fatjar.jar