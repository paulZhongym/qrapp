package cmu.edu;

import com.github.mustachejava.Mustache;
import com.mongodb.client.MongoDatabase;
import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.util.ByteBufWriter;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Calendar;
import java.util.Map;

public class MyService {
    private static String organization  = "215665";
    private static String apiKey = "934931645808d632d82b7dba11b2e26d0c487c98";
    public static void main(String[] args){
        try {
           String a= getQrCode("in");
            System.out.println(a);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ByteBuf applyTemplate(Mustache mustache, Map<String, Object> scopes) {
        ByteBufWriter writer = new ByteBufWriter();
        mustache.execute(writer, scopes);
        return writer.getBuf();
    }
    public static String getQrCode(String input) throws IOException {
        String URI = "https://api.beaconstac.com/api/2.0/qrcodes/";

        HttpClient client = HttpClients.custom().build();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();

        String body = "{\n" +
                "    \"name\": \"Static text QR Code\",\n" +
                "    \"organization\": " + organization +" ,\n" +
                "    \"qr_type\": 1,\n" +
                "    \"fields_data\": {\n" +
                "        \"qr_type\": 6,\n" +
                "        \"text\": \"" + input + "\"\n" +
                "    },\n" +
                "    \"attributes\":{\n" +
                "        \"color\":\"#2595ff\",\n" +
                "        \"colorDark\":\"#2595ff\",\n" +
                "        \"margin\":80,\n" +
                "        \"isVCard\":false,\n" +
                "        \"frameText\":\"BEACONSTAC\",\n" +
                "        \"logoImage\":\"https://d1bqobzsowu5wu.cloudfront.net/15406/36caec11f02d460aad0604fa26799c50\",\n" +
                "        \"logoScale\":0.1992,\n" +
                "        \"frameColor\":\"#2595FF\",\n" +
                "        \"frameStyle\":\"banner-bottom\",\n" +
                "        \"logoMargin\":10,\n" +
                "        \"dataPattern\":\"square\",\n" +
                "        \"eyeBallShape\":\"circle\",\n" +
                "        \"gradientType\":\"none\",\n" +
                "        \"eyeFrameColor\":\"#2595FF\",\n" +
                "        \"eyeFrameShape\":\"rounded\"\n" +
                "    }\n" +
                "}";
        String type = "application/json";
        StringEntity json = new StringEntity(body);
        json.setContentType(type);

        HttpUriRequest request = RequestBuilder.post().setUri(URI)
                .setHeader(HttpHeaders.AUTHORIZATION, "Token " + apiKey)
                .setHeader(HttpHeaders.CONTENT_TYPE,"application/json")
                .setEntity(json)
                .build();
        HttpResponse response = client.execute(request);
        // get the qrcode id from response
        String id = EntityUtils.toString( response.getEntity()).split(":")[1];
        id = id.substring(0,id.indexOf(","));
        System.out.println(id);
        String uri = "https://api.beaconstac.com/api/2.0/qrcodes/"+id + "/download";
        HttpUriRequest image = RequestBuilder.get()
                .setUri(uri)
                .setHeader(HttpHeaders.AUTHORIZATION, "Token " + apiKey)
                .build();
        HttpResponse imgaeRes = client.execute(image);
        String url = EntityUtils.toString(imgaeRes.getEntity()).split("\"")[5];
        System.out.println("response url is: " + url);

        String log = " data: " + input
                + "; time" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
                + "; device: " + "android"
                + "; reply: " + url
                + "; content type: " + type
                + "; path: " + uri
                ;
        MongoClass.insert(log);

        return url;

}

}
