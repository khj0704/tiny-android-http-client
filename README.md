Tiny Android HTTP Client
===============================
Since android 2.2 Google has deprecated the apache http client and suggest developers to use URLConnection.
he goal of this project is to create a lightweight http library based on URLConnection on android,
with pluggable cookie manager and cache manager, allow large requests or responses processed by the streaming API.

This library has zero dependency and provides three models to issue http requests:

Model 1: Async request with Callback
------------------
This is the simplest call we use in most cases.
```
HTTPClientConfig config = new HTTPClientConfig();
HTTPClient client = new HTTPClient(config);
Map<String, String> params = new HashMap<String, String>();
params.put("start", "0");
params.put("end", "2");
client.executeAsync(new HttpRequest.Get("http://abc.com/ws", params), new HttpCallback(){
    @Override
    public void onSuccess(HttpResponse resp) {
        System.out.println(resp.getBody());
        System.out.println(resp.getStatus());
    }
    @Override
    public void onError(HttpResponse resp) {
        System.out.println(resp.getException());
    }
});
```
Note, when issuing an http request, if your screen rotates the activity will be re-created, your code to update the
user interface in the onSuccess callback will not work. You need a fragment activity to solve this problem.

Model 2: Async request with Future
------------------
This is a semi auto rifle, the API returns a standard Future object, so you can check if the work is done or
cancel the long tasks in another thread. The ideal solution for this is a file download manager, or avatar uploader.
```
HTTPClientConfig config = new HTTPClientConfig();
HTTPClient client = new HTTPClient(config);
Map<String, String> params = new HashMap<String, String>();
params.put("start", "0");
params.put("end", "2");
HTTPClient.FutureResponse resp = client.executeAsync(new HttpRequest.Get("http://abc.com/ws", params));
System.out.println(resp.getFuture().isDone()); // check if it's done
System.out.println(resp.getFuture().get(20, TimeUnit.SECONDS).getBody()); // block at most 20 seconds
System.out.println(resp.getFuture().get().getBody()); // getFuture() will block until you get the result.
```

Model 3: Send a sync request
------------------
This is barebone solution. If you have multiple http requests to send conditionally, e,g. issue request A first,
depends on the response issue request B or C, finally issue reuest D. If you use the callback solution mentioned above
you will run into callback chains, in that case you can start an AsyncTask to group the requests together.
```
HTTPClientConfig config = new HTTPClientConfig();
HTTPClient client = new HTTPClient(config);
Map<String, String> params = new HashMap<String, String>();
params.put("start", "0");
params.put("end", "2");
// the call below will block, don't do it in main UI thread
HttpResponse resp = client.executeSync(new HttpRequest.Get("http://abc.com/ws", params));
System.out.println(resp.getBody());
System.out.println(resp.getStatus());
```

Customize Headers
------------------
```
HttpResponse resp = client.executeSync(new HttpRequest.Get("http://abc.com/ws", params).
        addHeader("name1", "value1").addHeader("name2", "value2"));
```

Streaming API
------------------
To download a large file, or upload user avatar, you might use the streaming API.
```
HttpStreamingResponse resp = client.executeSync(
    new HttpStreamingRequest.Get("http://d.com/test.apk", null));
resp.getInputStream();// download a large file with this stream
```

Error Handling
------------------
In the callback model, if a request succeeds the callback onSuccess will be called first, then depends on the status code,
 the callback onSuccess1xx/onSuccess2xx/onSuccess3xx could be called. If the request fails, the callback onError will be
 called first, then onError4xx/onError5xx/onTimeout will be called.

Roadmap
------------------
The following features will be supported soon:
1. a pluggable cache manager which supports 304, max-age, expires etc.
2. a pluggable persistent cookie manager

Who is using
------------------
This lib has been distributed on 11+ millions android devices by Nov 2013 in various apps developed by Shanda games.
