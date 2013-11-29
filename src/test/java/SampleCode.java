import junit.framework.TestCase;
import org.android.io.httpclient.HTTPClient;
import org.android.io.httpclient.HTTPClientConfig;
import org.android.io.httpclient.HttpException;
import org.android.io.httpclient.request.HttpRequest;
import org.android.io.httpclient.request.HttpStreamingRequest;
import org.android.io.httpclient.response.HttpCallback;
import org.android.io.httpclient.response.HttpResponse;
import org.android.io.httpclient.response.HttpStreamingResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: Daniel
 * This is not unit test
 */
public class SampleCode extends TestCase {

    public void testSyncGet() throws Exception {
        HTTPClientConfig config = new HTTPClientConfig();
        HTTPClient client = new HTTPClient(config);
        Map<String, String> params = new HashMap<String, String>();
        params.put("start", "0");
        params.put("end", "2");
        HttpResponse resp = client.executeSync(new HttpRequest.Get("http://www.ggg.cn/webservice/rest/1.0/games", params));
        System.out.println(resp.getBody());
        System.out.println(resp.getStatus());
    }

    public void testAsyncGet() throws ExecutionException, InterruptedException, TimeoutException {
        HTTPClientConfig config = new HTTPClientConfig();
        HTTPClient client = new HTTPClient(config);
        Map<String, String> params = new HashMap<String, String>();
        params.put("start", "0");
        params.put("end", "2");
        HTTPClient.FutureResponse resp = client.executeAsync(new HttpRequest.Get("http://www.ggg.cn/webservice/rest/1.0/games", params));

        System.out.println(resp.getFuture().isDone());
        System.out.println(resp.getFuture().get(20, TimeUnit.SECONDS).getBody());
        System.out.println(resp.getFuture().get().getBody());
        System.out.println(resp.getFuture().get().getStatus());

    }

    public void testAsyncGetCallback() {
        HTTPClientConfig config = new HTTPClientConfig();
        HTTPClient client = new HTTPClient(config);
        Map<String, String> params = new HashMap<String, String>();
        params.put("start", "0");
        params.put("end", "2");
        client.executeCallback(new HttpRequest.Get("http://www.ggg.cn/webservice/rest/1.0/games", params), new HttpCallback() {
            @Override
            public void onSuccess(HttpResponse resp) {
                System.out.println(resp.getBody());
                System.out.println(resp.getStatus());
            }

            @Override
            public void onError(HttpResponse resp) {
                System.out.println(resp.getException());
            }

            @Override
            public void onTimeout(HttpResponse e) {

            }

        });
    }

    public void testAsyncHeaders() throws HttpException {
        HTTPClientConfig config = new HTTPClientConfig();
        HTTPClient client = new HTTPClient(config);
        Map<String, String> params = new HashMap<String, String>();
        params.put("start", "0");
        params.put("end", "2");
        HttpResponse resp = client.executeSync(new HttpRequest.Get("http://www.ggg.cn/webservice/rest/1.0/games", params).
                addHeader("name1", "value1").addHeader("name2", "value2"));
        System.out.println(resp.getBody());
        System.out.println(resp.getStatus());
    }


    public void testStreaming() throws HttpException {
        HTTPClientConfig config = new HTTPClientConfig();
        HTTPClient client = new HTTPClient(config);
        HttpStreamingResponse resp = client.executeSync(new HttpStreamingRequest.Get("http://www.ggg.cn/game/download/1", null));
        resp.getInputStream();// download a large file with this stream

    }

}
