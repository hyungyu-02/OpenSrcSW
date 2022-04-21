package OpenSrcSW_MainProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;

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

	public void showSnippet() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {

		String qKwrd[];// ���� Ű���� �������
		String qtf[];// ���� weight �������

		// ������
		// init KeywordExtractor
		KeywordExtractor ke = new KeywordExtractor();
		// extract keywords
		KeywordList kl = ke.extractKeyword(query, true);

		qKwrd = new String[kl.size()];
		qtf = new String[kl.size()];

		for (int j = 0; j < kl.size(); j++) {
			Keyword kwrd = kl.get(j);

			qKwrd[j] = kwrd.getString();
			qtf[j] = Integer.toString(kwrd.getCnt());
		}

		// qKwrd �� qtf ����ؼ� Ȯ��
//      for(int i = 0; i < qKwrd.length; i++) {
//         System.out.println(qKwrd[i]);
//         System.out.println(qtf[i]);
//      }

		// index.post ���Ͽ��� �ؽ��� �о����
		FileInputStream fstream = new FileInputStream("index.post");
		ObjectInputStream objectInputstream = new ObjectInputStream(fstream);

		Object obj = objectInputstream.readObject();
		objectInputstream.close();

		HashMap<String, String> hashMap = (HashMap) obj;
		Iterator<String> it = hashMap.keySet().iterator();

		// ������ �˻��� �ܾ�鸸 �з��Ͽ� �ܾ���� ������ ����ġ�� ������ �������
		String[][] searched = new String[qKwrd.length][2];

		int count = 0;
		while (it.hasNext()) {
			String key = it.next();
			String value = hashMap.get(key);
			// System.out.println(key + " : " + value);

			for (int i = 0; i < qKwrd.length; i++) {
				if (qKwrd[i].equals(key)) {
					searched[count][0] = key; // �ܾ�
					searched[count++][1] = value; // �������� ���ĵ� ����ġ��
				}
			}
		}

		// searched ����Ͽ� Ȯ��
//      System.out.println("searched : ");
//      for(int i = 0; i < searched.length; i++) {
//         for(int j = 0; j < searched[i].length; j++) {
//            System.out.println(searched[i][j]);
//         }
//      }

		CalcSim(qKwrd, qtf, searched);// ���絵 ��� �� ������� ���

//		File file = new File(data_path);
//		
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		Document document = db.parse(file);
//		document.getDocumentElement().normalize();
//		NodeList nList = document.getElementsByTagName("doc");
//		
//		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//		
//		Document doc = docBuilder.newDocument();
//		
//		Element docs = doc.createElement("docs");
//		doc.appendChild(docs);
//		
//		String[] eachBodyText = new String[nList.getLength()];
//		
//		String[][] indexBodyData = new String[nList.getLength()][];
//		String idxBodyData;
//		
//		
//		for(int i = 0; i < nList.getLength(); i++) {
//			Node nNode = nList.item(i);
//			
//			if(nNode.getNodeType() == Node.ELEMENT_NODE) {
//				Element eElement = (Element) nNode;
//				
//				
//				eachBodyText[i] = eElement.getElementsByTagName("body").item(0).getTextContent();
//				
//				//init KeywordExtractor
//				KeywordExtractor ke = new KeywordExtractor();
//				//extract keywords
//				KeywordList kl = ke.extractKeyword(eElement.getElementsByTagName("body").item(0).getTextContent(), true);
//				//print result
//				
//				idxBodyData = "";
//				
//				for(int j = 0; j < kl.size(); j++) {
//					Keyword kwrd = kl.get(j);
//					idxBodyData +=kwrd.getString() + ":" + kwrd.getCnt()+"#";
//				}
//				indexBodyData[i] = idxBodyData.split("#");
//				
//				
//				
//			}
//			
//		}
//		
//		String[][] oneChar = new String[eachBodyText.length][];
//		
//		for(int i = 0; i < eachBodyText.length; i++) {
//			oneChar[i] = eachBodyText[i].split("");
//		}
//		
////		for(int i = 0; i < oneChar.length; i++) {
////			for(int j = 0; j < oneChar[i].length; j++) {
////				System.out.println(oneChar[i][j] + " ");
////			}
////			System.out.println();
////		}
//		
//		String qKwrd[];// ���� Ű���� �������
//		
//		KeywordExtractor ke = new KeywordExtractor();
//		// extract keywords
//		KeywordList kl = ke.extractKeyword(query, true);
//
//		qKwrd = new String[kl.size()];
//
//		for (int j = 0; j < kl.size(); j++) {
//			Keyword kwrd = kl.get(j);
//
//			qKwrd[j] = kwrd.getString();
//		}

	}

	private void CalcSim(String qKwrd2[], String qtf2[], String[][] searched2)throws ParserConfigurationException, SAXException, IOException {

		// �ϳ��� String ���� ����� ������ ����ġ�� �ɰ��� ������ �������
		String[][] eachDoc2 = new String[searched2.length][];

		for (int i = 0; i < eachDoc2.length; i++) {
			eachDoc2[i] = searched2[i][1].split(" ");
		}

		// qKwrd �� searched ���� �ܾ� ���� ����ġ�� ��ġ�ϵ��� ������
		for (int i = 0; i < qKwrd2.length; i++) {
			for (int j = 0; j < searched2.length; j++) {
				if (qKwrd2[i].equals(searched2[j][0])) {
					String temp2 = qKwrd2[i];
					qKwrd2[i] = qKwrd2[j];
					qKwrd2[j] = temp2;
					temp2 = qtf2[i];
					qtf2[i] = qtf2[j];
					qtf2[j] = temp2;
				}
			}
		}

		// ������ ���絵�� ����Ͽ� ������ �������
		double[][] simC = new double[eachDoc2[0].length / 2][2];
		// double sumC;
		simC = InnerProduct(qtf2, eachDoc2);

		// ������ȣ �Է�, ����ġ ��� �� ����
//      for(int i = 0; i < simC.length;i++) {
//    	  simC[i][0] = i;
//    	  sumC = 0.0;
//         for(int j = 0; j < eachDoc2.length; j++) {
//        	 sumC += Double.parseDouble(qtf2[j]) * Double.parseDouble(eachDoc2[j][(i*2) + 1]);
//         }
//         simC[i][1] = sumC;
//      }
		// simC = InnerProduct(qKwrd2, qtf2, searched2, simC);

		// �и� �� ||A|| ���
		double sA = 0;
		for (int i = 0; i < qtf2.length; i++) {
			sA += Double.parseDouble(qtf2[i]) * Double.parseDouble(qtf2[i]);
		}

		// �и� �� ||B|| ���
		double sB[] = new double[simC.length];

		for (int i = 0; i < sB.length; i++) {
			for (int j = 0; j < eachDoc2.length; j++) {
				sB[i] += Double.parseDouble(eachDoc2[j][i * 2 + 1]) * Double.parseDouble(eachDoc2[j][i * 2 + 1]);
			}
			sB[i] = Math.sqrt(sB[i]);
		}

		// Cosine similarity �� ������ �������
		double[][] simForCos = new double[eachDoc2[0].length / 2][2];

		for (int i = 0; i < simForCos.length; i++) {
			simForCos[i][0] = i;
			if (sA * sB[i] != 0) {
				simForCos[i][1] = simC[i][1] / (sA * sB[i]);
			} else {
				simForCos[i][1] = 0;
			}
		}

		// Cosine similarity �߿䵵 ������ �������� ����
		for (int i = 0; i < simForCos.length - 1; i++) {
			for (int j = i + 1; j < simForCos.length; j++) {
				if (simForCos[i][1] < simForCos[j][1]) {
					double temp2 = simForCos[i][1];
					simForCos[i][1] = simForCos[j][1];
					simForCos[j][1] = temp2;
					temp2 = simForCos[i][0];
					simForCos[i][0] = simForCos[j][0];
					simForCos[j][0] = temp2;
				}
			}
		}

		// ���� id�� Ÿ��Ʋ ���� ��������(�ϵ� �ڵ�)
		String[] title = new String[simForCos.length];

		File file = new File("./collection.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("doc");

		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				title[i] = eElement.getElementsByTagName("title").item(0).getTextContent();
			}
		}

		// ���� 3������ ��� (���絵�� 0�� ��� ������)
		for (int i = 0; i < simC.length && i < 3; i++)
			if (simForCos[i][1] > 0)
				System.out.printf("%d�� : ����(%d) , Ÿ��Ʋ : (%s) , ���絵 : (%f)\n", i + 1, (int) simForCos[i][0],
						title[(int) simForCos[i][0]], simForCos[i][1]);

	}

	private double[][] InnerProduct(String[] qtf, String[][] eachDoc)throws ParserConfigurationException, SAXException, IOException {

		double[][] sim = new double[eachDoc[0].length / 2][2];
		double sum;

		// ������ȣ �Է�, ����ġ ��� �� ����
		for (int i = 0; i < sim.length; i++) {
			sim[i][0] = i;
			sum = 0.0;
			for (int j = 0; j < eachDoc.length; j++) {
				sum += Double.parseDouble(qtf[j]) * Double.parseDouble(eachDoc[j][(i * 2) + 1]);
			}
			sim[i][1] = sum;
		}

		return sim;

	}

}
