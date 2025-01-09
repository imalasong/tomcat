package org.apache.catalina.sample;


import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.jasper.compiler.JspUtil;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.apache.jasper.servlet.JasperInitializer;

/**
 * @author xiaochangbai
 * @date 2024-12-23 20:59
 */
public class Main {

    public static final String DEFAULT_PROTOCOL = Http11NioProtocol.class.getName();
    public static final String DEFAULT_PROTOCOL2 = Http11Nio2Protocol.class.getName();

    public static void main(String[] args) throws LifecycleException,
            InterruptedException, ServletException {

        Tomcat tomcat = new Tomcat();

        //1、配置连接器
        Connector connector = new Connector(DEFAULT_PROTOCOL);
        connector.setPort(8081);
        //把connector加入Service类中的数组，并且调用connector的start方法
        tomcat.setConnector(connector);
        Connector connector2 = new Connector(DEFAULT_PROTOCOL2);
        connector2.setPort(8071);
        tomcat.setConnector(connector2);


        //2、配置处理器
        // JSP初始化（非必要项，如果不要jsp的话就不需要下面这行代码）
        new JasperInitializer();
        String webs = new File("webs").getAbsolutePath();
        Context ctx = tomcat.addContext("/",webs);
        tomcat.initWebappDefaults("/");
        Tomcat.addServlet(ctx, "Embedded", new MyServlet());
        ctx.addServletMappingDecoded("/hello", "Embedded");


        //3、启动tomcat
        tomcat.start();
        System.out.println("Tomcat started at http://localhost:8081/hello");
        tomcat.getServer().await();
    }

    public static class MyServlet extends HttpServlet implements SingleThreadModel {
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
            Writer w = resp.getWriter();
            w.write(this.toString()+":Embedded Tomcat servlet.\n");

            w.flush();
            w.close();
        }
    }

}
