package cmu.edu;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.util.ByteBufWriter;
import io.activej.http.AsyncServlet;
import io.activej.http.HttpResponse;
import io.activej.http.RoutingServlet;
import io.activej.inject.annotation.Provides;
import io.activej.launcher.Launcher;
import io.activej.launchers.http.HttpServerLauncher;
import io.activej.promise.Promise;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static cmu.edu.MyService.applyTemplate;
import static io.activej.http.HttpMethod.GET;
import static java.util.Collections.emptyMap;

public class WebApp extends HttpServerLauncher {


    @Provides
    AsyncServlet servlet() {
        Mustache mainView = new DefaultMustacheFactory().compile("index.html");
//        context.put("log", Arrays.asList("firstline", "second line"));
        return RoutingServlet.create()
                .map("/test", request ->
                        HttpResponse.ok200()
                                .withPlainText("Healthy dashboard instance!"))
                .map(GET, "/", httpRequest -> httpRequest.loadBody()
                        .map($ -> {
                            Map<String, Object> context = new HashMap<>();
                            context.put("log",MongoClass.getAll());
                            return HttpResponse.ok200()
                                    .withBody(applyTemplate(mainView, context));
                        })
                )
                .map(GET, "/qrcode", httpRequest -> httpRequest.loadBody()
                        .map($ -> {
                            String data = httpRequest.getQueryParameter("data");
                            String url = MyService.getQrCode(data);
                            return HttpResponse.ok200().withPlainText(url);
                        })
                );
    }

    public static void main(String[] args) throws Exception {
        Launcher launcher = new WebApp();
        launcher.launch(args);
    }
}
