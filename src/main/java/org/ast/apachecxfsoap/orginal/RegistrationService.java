package org.ast.apachecxfsoap.orginal;

import org.apache.cxf.annotations.SchemaValidation;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.HashSet;
import java.util.Set;

import static org.apache.cxf.annotations.SchemaValidation.SchemaValidationType.BOTH;
import static org.ast.apachecxfsoap.orginal.Constants.NAMESPACE;
import static org.ast.apachecxfsoap.orginal.Constants.SERVICE_NAME;

// http://localhost:8080/soap/Registration?wsdl
@WebService(targetNamespace = NAMESPACE, serviceName = SERVICE_NAME)
@SchemaValidation(type = BOTH)
@SOAPBinding
public class RegistrationService {

    private final Set<Registration> registered = new HashSet<>();

    @WebMethod
    public void register(Registration registration) throws AlreadyRegisteredException {
        if (registered.add(registration)) {
            throw new AlreadyRegisteredException();
        }
    }
}
