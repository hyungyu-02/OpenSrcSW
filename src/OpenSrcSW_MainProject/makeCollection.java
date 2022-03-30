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

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class makeCollection {
	
	private String data_path;
	private String output_file = "./collection.xml";
	
	public makeCollection(String path) {
		this.data_path = path;
	}
	
	public void makeXml() throws ParserConfigurationException, IOException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();

		File[] files = fileDown(data_path);

		Element docs = doc.createElement("docs");
		doc.appendChild(docs);

		File file = new File("./collection.xml");

		for (int i = 0; i < files.length; i++) {
			org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");
			String titleData = html.title();
			String bodyData = html.body().text();

			Element code = doc.createElement("doc");
			docs.appendChild(code);

			code.setAttribute("id", Integer.toString(i));

			Element title = doc.createElement("title");
			title.appendChild(doc.createTextNode(titleData));
			code.appendChild(title);

			Element body = doc.createElement("body");
			body.appendChild(doc.createTextNode(bodyData));
			code.appendChild(body);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new FileOutputStream(file));
			transformer.transform(source, result);
			
		}
		System.out.println("2주차 실행완료");
	}
	

	private File[] fileDown(String path) {
		// TODO Auto-generated method stub
		File d = new File(path);
		return d.listFiles();
	}

}
