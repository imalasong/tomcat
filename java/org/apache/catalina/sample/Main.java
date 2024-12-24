package org.apache.catalina.sample;


import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * @author xiaochangbai
 * @date 2024-12-23 20:59
 */
public class Main {

    public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    public static void main(String[] args) throws LifecycleException,
            InterruptedException, ServletException {

        Tomcat tomcat = new Tomcat();

        Connector connector = new Connector(DEFAULT_PROTOCOL);
        connector.setThrowOnFailure(true);
        connector.setPort(8080);
        tomcat.setConnector(connector);

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

        RedisUtils.init();;
        Tomcat.addServlet(ctx, "Embedded", new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                Writer w = resp.getWriter();
                w.write("Embedded Tomcat servlet.\n");
                String string = RedisUtils.get("aaa");
                if(string!=null){
                    w.write(string);
                }
                w.flush();
                w.close();
            }
        });

        ctx.addServletMappingDecoded("/hello", "Embedded");

        tomcat.start();
        System.out.println("Tomcat started at http://localhost:8080");
        tomcat.getServer().await();
    }

}
