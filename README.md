SOAP is not very popular these days, mostly because of almost non human readable WSDL and protocol that puts a lot of overhead into a simple message. It's rally hard to just fire a message using browser or curl. Not mentioning the fact, that we have to get WSDL somehow
But on the other had side SOAP has some advantages over REST. It gives us possibility to define contract and precisely specify the data that it will process. Once you have such WSLD you can throw it over the hedge and you do not have answer a questions like: how do I delete created resource, what means 403 in this case, or it this call idempotent ?<br />
WSLD contains everything that is needed to define an RPC interface and to document it. I personally would go for REST for most of the cases, but still there are good a use cases for SOAP.<br />
<br />
There is this one thing that I really do not like about SOAP: how do I get right WSLD? Should I use some tool to generate it, or maybe just write it? Honestly.... i really do not want to dig into SOAP to just write WSLD, and I do not want to spend hours playing around with some tools generating WSLD, importing it into my project, binding generated stuff to real implementation only to find out on the end that it does not behave as expected.<br />
<br />
For me the perfect solution would be, to write Java Code, put some annotations on it and generate WSDL out of it. Those annotations should provide enough flexibility to influence WSLD generation so that we can get something that can be delivered to the customers.<br />
<br />
I've tried few Java frameworks and all provide a way to generate WSLD out of Java code, but all of them are missing one fundamental future: you are unable to provide XSD for data types. So it's not possible to specify content of email field, or provide format for a date. We are getting only a half of a possible functionality of an WSDL: method calls, exception handling, documentation and security but there is no way to specify data fromat.<br />
I would like to have a WSLD that is as tight as possible, so that the client can call particular method and will exactly know what is possible and what is not allowed.<br />
<br />
Let's start with standard SOAP Service generated out of Java code. We will use Apache CXF and Spring Boot for it. Source code is here:&nbsp;https://github.com/maciejmiklas/apache-cxf-soap<br />
<br />
It's a simple application, where users can submit registration as a SOAP Request on: http://localhost:8080/soap/Registration:<br />
<script src="https://gist.github.com/maciejmiklas/f6facf99e874c75b3f819f5dfa7f5d5d.js"></script><br />
<br />
This code outpost following WSDL:<br />
<script src="https://gist.github.com/maciejmiklas/45e234e0b626fca359203fa0c4a4e165.js"></script>
Now as you can see, there is not much about data types in this WSDL (lines 9-22). Email is just mandatory, date also, but there is no way to provide further assertions.<br />
The problem lies in JAXB, it's just missing annotations to influence generated XML Schema.<br />
There is a <a href="https://github.com/whummer/jaxb-facets">pull request (jaxb-facets) </a>that would solve this issue but it's already few years old and it does not look like it's going to be integrated any time soon.<br />
<br />
The limitation is not caused by SOAP frameworks, but by the fact, that they are based on JAXB.<br />
<br />
This still does not change the fact, that I am not going to write WSLD by myself, there has to be a batter way!<br />
<br />
We are going to implement simple extension for Apache CXF that will combine generated WSLD with custom XSD. In this case we have to write XSD but CXF will generate WSLD for us and it will include given Schema in it.<br />
XSD is very powerful, so I even prefer writing it by hand, because it's possible to precisely specify specify data format. Using annotations to generate XSD would be convenient, but still it would probably cover only some common areas.<br />
The implementation below has its limitations, you might run into some issues, it might stop working after next CXF update. But! I am using it, it does what I need, so it might be something four you as well ;)<br />
<br />
Now we are going to modify first example. The idea is to write XSD that defines simple types, reference those types in transfer classes, and on the end generate WSLD which combines it all.<br />
For the beginning we have to write Schema that defines types for transfer objects:
<script src="https://gist.github.com/maciejmiklas/37521bd96b4b1e2200efb2576e034c20.js"></script>
The original source code will be modified only it a few places:
<br />
<ul>
<li>we will replace default Apache CXF Data Binding with custom implementation. It reads Schama from file and integrates it into generated WSLD,</li>
<li>some fields in transfer objects are annotated with&nbsp;<i>@XmlSchemaType</i>&nbsp;- this annotations provides connection between Java types and types defined in XSD. For example&nbsp;<i>ExRegistration#email</i>&nbsp;is annotated with&nbsp;<i>@XmlSchemaType(name = "email")</i>, XSD contains&nbsp;<i>email</i>&nbsp;type, and finally&nbsp;<i>email</i>&nbsp;in generated in WSDL references type from provided Schema.,</li>
<li>the classes following pattern <i>*Registration*</i> has been renamed to <i>*ExRegistration*</i></li>
</ul>
<script src="https://gist.github.com/maciejmiklas/cd2f80300a80f9124a3d9f61914ad084.js"></script>
Here is our final WSDL:
<script src="https://gist.github.com/maciejmiklas/8276775d6a47cc7382fb1346f11de6a6.js"></script>