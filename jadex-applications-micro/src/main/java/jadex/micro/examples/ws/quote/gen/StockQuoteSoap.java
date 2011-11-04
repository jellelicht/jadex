
package jadex.micro.examples.ws.quote.gen;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "StockQuoteSoap", targetNamespace = "http://www.webserviceX.NET/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface StockQuoteSoap {


    /**
     * Get Stock quote for a company Symbol
     * 
     * @param symbol
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetQuote", action = "http://www.webserviceX.NET/GetQuote")
    @WebResult(name = "GetQuoteResult", targetNamespace = "http://www.webserviceX.NET/")
    @RequestWrapper(localName = "GetQuote", targetNamespace = "http://www.webserviceX.NET/", className = "jadex.micro.examples.ws.quote.gen.GetQuote")
    @ResponseWrapper(localName = "GetQuoteResponse", targetNamespace = "http://www.webserviceX.NET/", className = "jadex.micro.examples.ws.quote.gen.GetQuoteResponse")
    public String getQuote(
        @WebParam(name = "symbol", targetNamespace = "http://www.webserviceX.NET/")
        String symbol);

}
