package org.ast.apachecxfsoap.extended;

import org.apache.cxf.annotations.SchemaValidation;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.HashSet;
import java.util.Set;

import static org.apache.cxf.annotations.SchemaValidation.SchemaValidationType.BOTH;
import static org.ast.apachecxfsoap.extended.Constants.NAMESPACE;
import static org.ast.apachecxfsoap.extended.Constants.SERVICE_NAME;

// http://localhost:8080/soap/ExRegistration?wsdl
@WebService(targetNamespace = NAMESPACE, serviceName = SERVICE_NAME)
@SchemaValidation(type = BOTH)
@SOAPBinding
public class ExRegistrationService {

    private final Set<ExRegistration> registered = new HashSet<>();

    @WebMethod
    public void register(ExRegistration registration) throws AlreadyRegisteredException {
        if (registered.add(registration)) {
            throw new AlreadyRegisteredException();
        }
    }
}
