package OpenSrcSW_MainProject;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MidTerm {
	
	private String data_path;
	private String query;

	public MidTerm(String path, String query) {
		this.data_path = path;
		this.query = query;
	}
	
	public void showSnippet() throws ParserConfigurationException, SAXException, IOException {
		
		File file = new File(data_path);
		
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
		
		String[] eachBodyText = new String[nList.getLength()];
		
		for(int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			
			if(nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				
				
				eachBodyText[i] = eElement.getElementsByTagName("body").item(0).getTextContent();
			}
			
		}
		
		String[][] oneChar = new String[eachBodyText.length][];
		
		for(int i = 0; i < eachBodyText.length; i++) {
			oneChar[i] = eachBodyText[i].split("");
		}
		
//		for(int i = 0; i < oneChar.length; i++) {
//			for(int j = 0; j < oneChar[i].length; j++) {
//				System.out.println(oneChar[i][j] + " ");
//			}
//			System.out.println();
//		}
		
		String qKwrd[];// 쿼리 키워드 저장공간
		
		KeywordExtractor ke = new KeywordExtractor();
		// extract keywords
		KeywordList kl = ke.extractKeyword(query, true);

		qKwrd = new String[kl.size()];

		for (int j = 0; j < kl.size(); j++) {
			Keyword kwrd = kl.get(j);

			qKwrd[j] = kwrd.getString();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
