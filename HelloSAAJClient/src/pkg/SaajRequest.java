/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.*;
import java.util.Iterator;

/**
 *
 * @author Karim
 */
public class SaajRequest {

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

            Name myName = soapEnvelope.createName("msg");
            SOAPElement nameElement = helloDude.addChildElement(myName);
            nameElement.addTextNode("hamada");

            URL address = new URL("http://localhost:8084/HelloSAAJServer/SaajService");
            SOAPMessage response = connection.call(message, address);
            connection.close();
             SOAPBody responseSoapBody = response.getSOAPBody();
            Iterator iterator = responseSoapBody.getChildElements();
            while (iterator.hasNext()){
                SOAPBodyElement responseElement = (SOAPBodyElement)iterator.next();

                Iterator it2 = responseElement.getChildElements();
                while (it2.hasNext()) {
                    SOAPElement child = (SOAPElement) it2.next();
                    System.out.println(child.getTextContent());
                }
            }
        } catch (SOAPException ex) {
            Logger.getLogger(SaajRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(SaajRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SaajRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
