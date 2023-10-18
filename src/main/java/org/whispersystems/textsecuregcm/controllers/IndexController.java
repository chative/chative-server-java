package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class IndexController {

    @Timed
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String indexGet() {
        return getIndexHtml();
    }
    @Timed
    @POST
    @Produces(MediaType.TEXT_HTML)
    public String indexPOST() {
        return getIndexHtml();
    }
    @Timed
    @DELETE
    @Produces(MediaType.TEXT_HTML)
    public String indexDelete() {
        return getIndexHtml();
    }
    @Timed
    @PUT
    @Produces(MediaType.TEXT_HTML)
    public String indexPut() {
        return getIndexHtml();
    }


    public String getIndexHtml(){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Welcome to nginx!</title>\n" +
                "<style>\n" +
                "    body {\n" +
                "        width: 35em;\n" +
                "        margin: 0 auto;\n" +
                "        font-family: Tahoma, Verdana, Arial, sans-serif;\n" +
                "    }\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Welcome to nginx!</h1>\n" +
                "<p>If you see this page, the nginx web server is successfully installed and\n" +
                "working. Further configuration is required.</p>\n" +
                "\n" +
                "<p>For online documentation and support please refer to\n" +
                "<a href=\"http://nginx.org/\">nginx.org</a>.<br/>\n" +
                "Commercial support is available at\n" +
                "<a href=\"http://nginx.com/\">nginx.com</a>.</p>\n" +
                "\n" +
                "<p><em>Thank you for using nginx.</em></p>\n" +
                "</body>\n" +
                "</html>";
    }
}
