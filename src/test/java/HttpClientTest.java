import junit.framework.TestCase;
import org.android.http.client.HttpClient;
import org.android.http.client.request.Get;
import org.android.http.client.response.HttpResponse;
import org.android.http.client.response.HttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Daniel
 */
public class HttpClientTest extends TestCase {

    public void testSyncGet() throws Exception {
        HttpClient client = new HttpClient(null, null, null);
        Map<String, String> params = new HashMap<String, String>();
        params.put("start", "0");
        params.put("end", "2");
        HttpResponse resp = client.executeSync(new Get("http://www.dev.ggg.cn/webservice/rest/1.0/games", params));
        System.out.println(resp.getBody());
        System.out.println(resp.getStatus());
    }

    public void testAsyncGet() {
        HttpClient client = new HttpClient(null, null, null);
        Map<String, String> params = new HashMap<String, String>();
        params.put("start", "0");
        params.put("end", "2");
        client.executeAsync(new Get("http://www.dev.ggg.cn/webservice/rest/1.0/games", params), new HttpResponseHandler(){
            @Override
            public void onComplete(HttpResponse resp) {
                System.out.println(resp.getBody());
                System.out.println(resp.getStatus());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
