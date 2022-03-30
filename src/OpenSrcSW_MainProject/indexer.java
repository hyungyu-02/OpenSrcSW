package OpenSrcSW_MainProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

public class indexer {

	private String input_file_path;
	private String output_file = "./index.post";

	public indexer(String file) {
		this.input_file_path = file;
	}

	public void convertPost() throws ParserConfigurationException, SAXException, IOException, TransformerException,
			ClassNotFoundException {

		File file = new File(input_file_path);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("doc");

		File idxPostFile = new File("./index.post");

		HashMap<String, Integer> indexerMap = new HashMap<>();
		HashMap<String, String> resultMap = new HashMap<String, String>();

		String splitBySharp[];
		String splitByColon[];

		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);

			String idxerBodyData;

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				idxerBodyData = eElement.getElementsByTagName("body").item(0).getTextContent();

				splitBySharp = idxerBodyData.split("#");

				for (int j = 0; j < splitBySharp.length; j++) {
					splitByColon = splitBySharp[j].split(":");
					if (splitByColon.length == 2) {
						if (indexerMap.get(splitByColon[0]) == null) {
							indexerMap.put(splitByColon[0], 1);
						} else {
							indexerMap.put(splitByColon[0], indexerMap.get(splitByColon[0]) + 1);
						}
					}
				}

			}
		}

		for (int i = 0; i < nList.getLength(); i++) {
			HashMap<String, Double> TFIDFMap = new HashMap<String, Double>();

			Node nNode = nList.item(i);

			String idxerBodyData;

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				idxerBodyData = eElement.getElementsByTagName("body").item(0).getTextContent();

				splitBySharp = idxerBodyData.split("#");

				for (int j = 0; j < splitBySharp.length; j++) {
					splitByColon = splitBySharp[j].split(":");
					if (splitByColon.length == 2) {
						int rep = Integer.parseInt(splitByColon[1]);

						if (indexerMap.get(splitByColon[0]) != null) {
							double weight;
							// weight = Math.round(((double)((rep) *
							// Math.log(nList.getLength()/TFIDFMap.get(splitByColon[0])))) * 100)/100.0;
							weight = (double) ((rep) * Math.log(nList.getLength() / indexerMap.get(splitByColon[0])));
							weight = Math.round(weight * 100) / 100.0;

							TFIDFMap.put(splitByColon[0], weight);

							if (resultMap.get(splitByColon[0]) == null) {
								String resultStr = "";
								resultStr += Integer.toString(i) + " " + Double.toString(weight);
								resultMap.put(splitByColon[0], resultStr);
							} else {
								String resultStr = resultMap.get(splitByColon[0]) + " " + Integer.toString(i) + " "
										+ Double.toString(weight);
								resultMap.put(splitByColon[0], resultStr);
							}
						}
					}
				}
			}
			Iterator<String> it = indexerMap.keySet().iterator();
			while (it.hasNext()) {
				String keys = it.next();
				if (TFIDFMap.get(keys) == null) {
					if (resultMap.get(keys) == null) {
						resultMap.put(keys, Integer.toString(i) + " 0.00");
					} else {
						String str = resultMap.get(keys) + " " + Integer.toString(i) + " 0.00";
						resultMap.put(keys, str);
					}
				}
			}
		}

		FileOutputStream fileStream = new FileOutputStream("./index.post");
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);

		objectOutputStream.writeObject(resultMap);
		objectOutputStream.close();

		FileInputStream fstream = new FileInputStream("./index.post");
		ObjectInputStream objectInputstream = new ObjectInputStream(fstream);

		Object obj = objectInputstream.readObject();
		objectInputstream.close();

		HashMap<String, String> hashMap = (HashMap) obj;
		Iterator<String> it = resultMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = resultMap.get(key);
			System.out.println(key + " : " + value);
		}

		System.out.println("4주차 실행완료");
	}

}
