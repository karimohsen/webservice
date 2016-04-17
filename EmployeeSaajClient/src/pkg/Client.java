/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

/**
 *
 * @author Karim
 */
public class Client {
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encode(imageByteArray);
    }
    public static void main(String[] args) {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();

            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();

            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPBody soapBody = soapEnvelope.getBody();

            Name bodyName = soapEnvelope.createName("hellofunction");
            SOAPBodyElement helloDude = soapBody.addBodyElement(bodyName);

            Name myName = soapEnvelope.createName("employee");
            SOAPElement nameElement = helloDude.addChildElement(myName);
            //nameElement.addTextNode("karim");

            Name fname = soapEnvelope.createName("fname");
            SOAPElement fnameElement = nameElement.addChildElement(fname);
            fnameElement.addTextNode("hamada");

            Name add = soapEnvelope.createName("adress");
            SOAPElement addressElement = nameElement.addChildElement(add);
            addressElement.addTextNode("omarasd-giza");

            Name uname = soapEnvelope.createName("uname");
            SOAPElement unameElement = nameElement.addChildElement(uname);
            unameElement.addTextNode("yasdas");

            Name password = soapEnvelope.createName("password");
            SOAPElement passElement = nameElement.addChildElement(password);
            passElement.addTextNode("1234");
            File file = new File("D:\\iti images\\Dish Party_6-11-2014\\IMG_5376.jpg");
            byte[] bFile = new byte[(int) file.length()];

            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                //convert file into array of bytes
                fileInputStream.read(bFile);
                fileInputStream.close();
            } catch (Exception e) {
                System.out.println("Error in reading the image");
            }
            Name img = soapEnvelope.createName("image");
            SOAPElement imgElement = nameElement.addChildElement(img);
            imgElement.addTextNode(encodeImage(bFile));

            URL address = new URL("http://localhost:8084/EmployeeSaajServer/Server");
            SOAPMessage response = connection.call(message, address);
            connection.close();
            SOAPBody responseSoapBody = response.getSOAPBody();
            Iterator iterator = responseSoapBody.getChildElements();
            while (iterator.hasNext()) {
                SOAPBodyElement responseElement = (SOAPBodyElement) iterator.next();

                Iterator it2 = responseElement.getChildElements();
                while (it2.hasNext()) {
                    SOAPElement child = (SOAPElement) it2.next();
                    System.out.println(child.getTextContent());
                }
            }
        } catch (SOAPException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
