package org.ast.apachecxfsoap.extended;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.Objects;

import static org.ast.apachecxfsoap.extended.Constants.NAMESPACE;

@XmlRootElement
class ExRegistration {

    @XmlSchemaType(name = "email", namespace = NAMESPACE)
    @XmlElement(required = true)
    private String email;

    @XmlElement(required = true)
    private String name;

    @XmlSchemaType(name = "phone", namespace = NAMESPACE)
    private String phone;

    @XmlSchemaType(name = "age", namespace = NAMESPACE)
    private Integer age;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExRegistration that = (ExRegistration) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
