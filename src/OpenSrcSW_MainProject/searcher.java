package OpenSrcSW_MainProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
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

public class searcher {
   
   private String data_path;
   private String query;

   public searcher(String path, String query) {
      this.data_path = path;
      this.query = query;
   }

   public void query() throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
      
      String qKwrd[];//���� Ű���� �������
      String qtf[];//���� weight �������
      
      //������
      //init KeywordExtractor
      KeywordExtractor ke = new KeywordExtractor();
      //extract keywords
      KeywordList kl = ke.extractKeyword(query, true);
      
      qKwrd = new String[kl.size()];
      qtf = new String[kl.size()];
      
      for(int j = 0; j < kl.size(); j++) {
         Keyword kwrd = kl.get(j);
         
         qKwrd[j] = kwrd.getString();
         qtf[j] = Integer.toString(kwrd.getCnt());
      }
      
      //qKwrd �� qtf ����ؼ� Ȯ��
//      for(int i = 0; i < qKwrd.length; i++) {
//         System.out.println(qKwrd[i]);
//         System.out.println(qtf[i]);
//      }
      
      // index.post ���Ͽ��� �ؽ��� �о����
      FileInputStream fstream = new FileInputStream(data_path);
      ObjectInputStream objectInputstream = new ObjectInputStream(fstream);

      Object obj = objectInputstream.readObject();
      objectInputstream.close();
      
      HashMap<String, String> hashMap = (HashMap) obj;
      Iterator<String> it = hashMap.keySet().iterator();
      
      //������ �˻��� �ܾ�鸸 �з��Ͽ� �ܾ���� ������ ����ġ�� ������ �������
      String[][] searched = new String[qKwrd.length][2];
      
      int count = 0;
      while (it.hasNext()) {
         String key = it.next();
         String value = hashMap.get(key);
         //System.out.println(key + " : " + value);
         
         for(int i = 0; i < qKwrd.length; i++) {
            if(qKwrd[i].equals(key)) {
               searched[count][0] = key; //�ܾ�
               searched[count++][1] = value; //�������� ���ĵ� ����ġ��
            }
         }
      }
      
      //searched ����Ͽ� Ȯ��
//      System.out.println("searched : ");
//      for(int i = 0; i < searched.length; i++) {
//         for(int j = 0; j < searched[i].length; j++) {
//            System.out.println(searched[i][j]);
//         }
//      }
      
      //InnerProduct(qKwrd, qtf, searched);//���絵 ��� �� ������� ���
      
      System.out.println("5���� ����Ϸ�");
   }
   
   private double[][] InnerProduct(String[] qtf, String[][] eachDoc) throws ParserConfigurationException, SAXException, IOException {

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
	   
	   
	   
	   
	   
      //�ϳ��� String ���� ����� ������ ����ġ�� �ɰ��� ������ �������
//      String[][] eachDoc = new String[searched.length][];
//      
//      for(int i = 0; i < eachDoc.length; i++) {
//         eachDoc[i] = searched[i][1].split(" ");
//      }
//      
//      
//      //qKwrd �� searched ���� �ܾ� ���� ����ġ�� ��ġ�ϵ��� ������
//      for(int i = 0; i < qKwrd.length; i++) {
//         for(int j = 0; j < searched.length; j++) {
//            if(qKwrd[i].equals(searched[j][0])) {
//               String temp = qKwrd[i];
//               qKwrd[i] = qKwrd[j];
//               qKwrd[j] = temp;
//               temp = qtf[i];
//               qtf[i] = qtf[j];
//               qtf[j] = temp;
//            }
//         }
//      }
//      
//      
//      //������ ���絵�� ����Ͽ� ������ �������
//      double[][] sim2 = new double[eachDoc[0].length/2][2];
//      double sum;
//      
//      
//      //������ȣ �Է�, ����ġ ��� �� ����
//      for(int i = 0; i < sim.length;i++) {
//         sim[i][0] = i;
//         sum = 0.0;
//         for(int j = 0; j < eachDoc.length; j++) {
//            sum += Double.parseDouble(qtf[j]) * Double.parseDouble(eachDoc[j][(i*2) + 1]);
//         }
//         sim2[i][1] = sum;
//      }
//      return sim2;
      
      
      //�߿䵵 ������ �������� ����
//      for(int i = 0; i < sim.length - 1; i++) {
//         for(int j = i+1; j < sim.length; j++) {
//            if(sim[i][1] < sim[j][1]) {
//               double temp = sim[i][1];
//               sim[i][1] = sim[j][1];
//               sim[j][1] = temp;
//               temp = sim[i][0];
//               sim[i][0] = sim[j][0];
//               sim[j][0] = temp;
//            }
//         }
//      }
      
      
      //���� id�� Ÿ��Ʋ ���� ��������(�ϵ� �ڵ�)
//      String[] title = new String[sim.length];
//      
//      File file = new File("./collection.xml");
//      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//      DocumentBuilder db = dbf.newDocumentBuilder();
//      Document document = db.parse(file);
//      document.getDocumentElement().normalize();
//      NodeList nList = document.getElementsByTagName("doc");
//      
//      for(int i = 0; i < nList.getLength(); i++) {
//         Node nNode = nList.item(i);
//         if(nNode.getNodeType() == Node.ELEMENT_NODE) {
//            Element eElement = (Element) nNode;
//            title[i] = eElement.getElementsByTagName("title").item(0).getTextContent();
//         }
//      }
      
      //���� 3������ ��� (���絵�� 0�� ��� ������)
//      for(int i = 0; i < sim.length && i < 3; i++)
//         if(sim[i][1] > 0)
//            System.out.printf("%d�� : ����(%d) , Ÿ��Ʋ : (%s) , ���絵 : (%f)\n",i+1,(int)sim[i][0],title[(int)sim[i][0]], sim[i][1]);
      
   }
}