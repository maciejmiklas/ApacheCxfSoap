package org.ast.apachecxfsoap.extended;

import org.apache.cxf.common.xmlschema.InvalidXmlSchemaReferenceException;
import org.apache.cxf.common.xmlschema.SchemaCollection;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.resource.ResourceManager;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.IntStream;

class SchemaJAXBDataBinding extends JAXBDataBinding {

    private final String[] locations;

    private final static String FILTER_PATH = "//*[@type='tns:{}']";

    public SchemaJAXBDataBinding(String... locations) {
        super(true);
        this.locations = locations;
    }

    @Override
    public XmlSchema addSchemaDocument(ServiceInfo serviceInfo, SchemaCollection col, Document document, String systemId) {
        Arrays.stream(locations).map(this::load).forEach(xsd -> append(document, xsd));
        return super.addSchemaDocument(serviceInfo, col, document, systemId);
    }

    private void append(Document destination, Document extra) {
        Element root = destination.getDocumentElement();
        NodeList childNodes = extra.getDocumentElement().getChildNodes();

        IntStream.range(0, childNodes.getLength()).mapToObj(childNodes::item).
                filter(inNode -> contains(destination, inNode)).forEach(inNode -> {
            root.appendChild(destination.importNode(inNode, true));
        });
    }

    /**
     * filter out XSD types that are not referenced in WSDL.
     */
    private boolean contains(Document doc, Node inNode) {
        if (inNode.getAttributes() == null || inNode.getAttributes().getNamedItem("name") == null) {
            return false;
        }
        Node node = inNode.getAttributes().getNamedItem("name");
        String exprStr = FILTER_PATH.replace("{}", node.getNodeValue());
        try {
            XPathExpression expr = XPathFactory.newInstance().newXPath().compile(exprStr);
            NodeList found = (NodeList) expr.evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
            return found != null && found.getLength() > 0;
        } catch (XPathExpressionException e) {
            throw new InvalidXmlSchemaReferenceException("Error parsing document", e);
        }
    }

    private Document load(String location) {
        ResourceManager resourceManager = getBus().getExtension(ResourceManager.class);
        URL url = resourceManager.resolveResource(location, URL.class);
        if (url == null) {
            throw new InvalidXmlSchemaReferenceException("XSD not found: " + location);
        }
        try {
            Document document = StaxUtils.read(url.openStream());
            return document;
        } catch (IOException | XMLStreamException e) {
            throw new InvalidXmlSchemaReferenceException("Error reading XSD from: " + location, e);
        }
    }

}
