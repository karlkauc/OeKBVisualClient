package common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class XMLHelperSecurityTest {

	// ========== createSecureDocumentBuilderFactory ==========

	@Test
	@DisplayName("Secure factory should reject XXE with external entity")
	void secureFactory_rejectsXxeExternalEntity() throws Exception {
		DocumentBuilderFactory factory = XMLHelper.createSecureDocumentBuilderFactory();
		DocumentBuilder builder = factory.newDocumentBuilder();

		// XXE payload trying to read /etc/passwd
		String xxePayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<!DOCTYPE foo [ <!ENTITY xxe SYSTEM \"file:///etc/passwd\"> ]>" + "<data>&xxe;</data>";

		assertThrows(SAXParseException.class,
				() -> builder.parse(new ByteArrayInputStream(xxePayload.getBytes(StandardCharsets.UTF_8))));
	}

	@Test
	@DisplayName("Secure factory should reject XXE billion laughs attack")
	void secureFactory_rejectsBillionLaughs() throws Exception {
		DocumentBuilderFactory factory = XMLHelper.createSecureDocumentBuilderFactory();
		DocumentBuilder builder = factory.newDocumentBuilder();

		String billionLaughs = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<!DOCTYPE lolz ["
				+ "  <!ENTITY lol \"lol\">" + "  <!ENTITY lol2 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">"
				+ "]>" + "<data>&lol2;</data>";

		assertThrows(SAXParseException.class,
				() -> builder.parse(new ByteArrayInputStream(billionLaughs.getBytes(StandardCharsets.UTF_8))));
	}

	@Test
	@DisplayName("Secure factory should reject XXE with parameter entity")
	void secureFactory_rejectsParameterEntity() throws Exception {
		DocumentBuilderFactory factory = XMLHelper.createSecureDocumentBuilderFactory();
		DocumentBuilder builder = factory.newDocumentBuilder();

		String paramEntityPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<!DOCTYPE foo [ <!ENTITY % xxe SYSTEM \"file:///etc/hostname\"> %xxe; ]>" + "<data>test</data>";

		assertThrows(SAXParseException.class,
				() -> builder.parse(new ByteArrayInputStream(paramEntityPayload.getBytes(StandardCharsets.UTF_8))));
	}

	@Test
	@DisplayName("Secure factory should parse valid XML without DOCTYPE")
	void secureFactory_parsesValidXml() throws Exception {
		DocumentBuilderFactory factory = XMLHelper.createSecureDocumentBuilderFactory();
		DocumentBuilder builder = factory.newDocumentBuilder();

		String validXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><child>text</child></root>";
		var doc = builder.parse(new ByteArrayInputStream(validXml.getBytes(StandardCharsets.UTF_8)));

		assertNotNull(doc);
		assertEquals("root", doc.getDocumentElement().getTagName());
		assertEquals("text", doc.getElementsByTagName("child").item(0).getTextContent());
	}

	@Test
	@DisplayName("Secure factory should have XInclude disabled")
	void secureFactory_xincludeDisabled() throws Exception {
		DocumentBuilderFactory factory = XMLHelper.createSecureDocumentBuilderFactory();
		assertFalse(factory.isXIncludeAware());
	}

	// ========== createSecureTransformerFactory ==========

	@Test
	@DisplayName("Secure TransformerFactory should be created successfully")
	void secureTransformerFactory_createsSuccessfully() {
		TransformerFactory tf = XMLHelper.createSecureTransformerFactory();
		assertNotNull(tf);
	}

	@Test
	@DisplayName("Secure TransformerFactory should produce a working Transformer")
	void secureTransformerFactory_producesWorkingTransformer() throws Exception {
		TransformerFactory tf = XMLHelper.createSecureTransformerFactory();
		var transformer = tf.newTransformer();
		assertNotNull(transformer);
	}

	// ========== getFileType rejects XXE ==========

	@Test
	@DisplayName("getFileType should reject XML with DOCTYPE (XXE attempt)")
	void getFileType_rejectsXxe() {
		String xxeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<!DOCTYPE foo [ <!ENTITY xxe SYSTEM \"file:///etc/passwd\"> ]>"
				+ "<Funds><Fund>&xxe;</Fund></Funds>";

		assertThrows(RuntimeException.class, () -> XMLHelper.getFileType(xxeXml));
	}

	@Test
	@DisplayName("isOfiFile should reject XML with DOCTYPE (XXE attempt)")
	void isOfiFile_rejectsXxe() {
		String xxeXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<!DOCTYPE foo [ <!ENTITY xxe SYSTEM \"file:///etc/passwd\"> ]>"
				+ "<Funds><Fund><CountrySpecificData><AT><OeNB>" + "<Meldungstyp>&xxe;</Meldungstyp>"
				+ "</OeNB></AT></CountrySpecificData></Fund></Funds>";

		// Should return false (XXE parsing fails gracefully), not leak file contents
		assertFalse(XMLHelper.isOfiFile(xxeXml));
	}
}
