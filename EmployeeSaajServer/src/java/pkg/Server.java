/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg;

import hibernate.UserData;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Karim
 */
public class Server extends HttpServlet {

    static MessageFactory messageFactory = null;

    static {
        try {
            messageFactory = MessageFactory.newInstance();
        } catch (Exception ex) {
            System.out.println("Exception");
        }
    }

    ;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            MimeHeaders headers = getHeaders(request);
            InputStream is = request.getInputStream();
            SOAPMessage msg = messageFactory.createMessage(headers, is);
            SOAPMessage reply = null;
            reply = onMessage(msg);
            if (reply != null) {
                reply.saveChanges();
                response.setStatus(HttpServletResponse.SC_OK);
                putHeaders(reply.getMimeHeaders(), response);
                OutputStream outputStream = response.getOutputStream();
                reply.writeTo(outputStream);
                outputStream.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (SOAPException ex) {
            ex.printStackTrace();
        }

    }

    public SOAPMessage onMessage(SOAPMessage msg) {
        SOAPMessage message = null;
        ArrayList<String> names = new ArrayList<>();
        try {
            Iterator iterator = msg.getSOAPBody().getChildElements();

            while (iterator.hasNext()) {
                SOAPBodyElement responseElement = (SOAPBodyElement) iterator.next();

                Iterator it2 = responseElement.getChildElements();
                while (it2.hasNext()) {
                    SOAPElement child = (SOAPElement) it2.next();
                    Iterator it3 = child.getChildElements();
                    while (it3.hasNext()) {
                        SOAPElement child3 = (SOAPElement) it3.next();
                        String value = child3.getValue();
                        names.add(value);
                    }
                }
            }
            message = messageFactory.createMessage();

            SOAPBody body = message.getSOAPBody();
            SOAPEnvelope soapEnvelope = message.getSOAPPart().getEnvelope();
            Name bodyName = soapEnvelope.createName("hellofunction");
            SOAPBodyElement helloDude = body.addBodyElement(bodyName);

            Name myName = soapEnvelope.createName("msg");
            SOAPElement nameElement = helloDude.addChildElement(myName);
            nameElement.addTextNode("Saved To Database");
            UserData data = new UserData();
            data.setUserName(names.get(2));
            data.setFullName(names.get(0));
            data.setAddress(names.get(1));
            data.setPassword(names.get(3));
            data.setImage(decodeImage(names.get(4)));
            SessionFactory fact = new Configuration().configure("hibernate\\hibernate.cfg.xml").buildSessionFactory();
            Session session = fact.openSession();
            session.beginTransaction();
            session.persist(data);
            session.getTransaction().commit();
            message.saveChanges();

        } catch (Exception e) {
            System.out.println("Exception");
        }
        return message;
    }
public static byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }
    static void putHeaders(MimeHeaders headers, HttpServletResponse res) {
        Iterator it = headers.getAllHeaders();
        while (it.hasNext()) {
            MimeHeader header = (MimeHeader) it.next();

            String[] values = headers.getHeader(header.getName());
            if (values.length == 1) {
                res.setHeader(header.getName(), header.getValue());
            } else {
                StringBuffer concat = new StringBuffer();
                int i = 0;
                while (i < values.length) {
                    if (i != 0) {
                        concat.append(',');
                    }
                    concat.append(values[i++]);
                }
                res.setHeader(header.getName(), concat.toString());
            }
        }
    }

    static MimeHeaders getHeaders(HttpServletRequest req) {

        Enumeration headerNames = req.getHeaderNames();
        MimeHeaders headers = new MimeHeaders();

        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            String headerValue = req.getHeader(headerName);

            StringTokenizer values = new StringTokenizer(headerValue, ",");
            while (values.hasMoreTokens()) {
                headers.addHeader(headerName, values.nextToken().trim());
            }
        }
        return headers;
    }

    @Override
    public void init(ServletConfig servletConfig)
            throws ServletException {
        super.init(servletConfig);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
