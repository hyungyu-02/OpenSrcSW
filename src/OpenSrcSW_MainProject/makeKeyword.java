package OpenSrcSW_MainProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class makeKeyword {
	
	private String input_file;
	private String output_flie = "./index.xml";
	
	public makeKeyword(String file) {
		this.input_file = file;
	}

	public void convertXml() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		File file = new File(input_file);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("doc");
		
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document doc = docBuilder.newDocument();
		
		Element docs = doc.createElement("docs");
		doc.appendChild(docs);
		
		File idxFile = new File("./index.xml");
		
		String idxBodyData;
		
		for(int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if(nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				
				idxBodyData = "";
				
				//init KeywordExtractor
				KeywordExtractor ke = new KeywordExtractor();
				//extract keywords
				KeywordList kl = ke.extractKeyword(eElement.getElementsByTagName("body").item(0).getTextContent(), true);
				//print result
				for(int j = 0; j < kl.size(); j++) {
					Keyword kwrd = kl.get(j);
					idxBodyData +=kwrd.getString() + ":" + kwrd.getCnt()+"#";
				}
				idxBodyData +="\n";
				
				
				Element code = doc.createElement("doc");
				docs.appendChild(code);
				
				code.setAttribute("id", Integer.toString(i));
				
				Element title = doc.createElement("title");
				title.appendChild(doc.createTextNode(eElement.getElementsByTagName("title").item(0).getTextContent()));
				code.appendChild(title);
				
				Element body = doc.createElement("body");
				body.appendChild(doc.createTextNode(idxBodyData));
				code.appendChild(body);
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();

				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

				
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new FileOutputStream(idxFile));
				transformer.transform(source, result);
				
			}
			
		}
		System.out.println("3주차 실행완료");
	}
}
