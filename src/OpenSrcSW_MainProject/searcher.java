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
      
      CalcSim(qKwrd, qtf, searched);//���絵 ��� �� ������� ���
      
      System.out.println("5���� ����Ϸ�");
   }
   
   private void CalcSim(String qKwrd[], String qtf[], String[][] searched) throws ParserConfigurationException, SAXException, IOException {
      
      //�ϳ��� String ���� ����� ������ ����ġ�� �ɰ��� ������ �������
      String[][] eachDoc = new String[searched.length][];
      
      for(int i = 0; i < eachDoc.length; i++) {
         eachDoc[i] = searched[i][1].split(" ");
      }
      
      //eachDoc ����Ͽ� Ȯ��
      System.out.println("eachDoc : ");
      for(int i = 0; i < eachDoc.length; i++) {
         for(int j = 0; j < eachDoc[i].length; j++) {
               System.out.print(eachDoc[i][j]+" ");
         }
         System.out.println();
      }
      
      //qKwrd �� searched ���� �ܾ� ���� ����ġ�� ��ġ�ϵ��� ������
      for(int i = 0; i < qKwrd.length; i++) {
         for(int j = 0; j < searched.length; j++) {
            if(qKwrd[i].equals(searched[j][0])) {
               String temp = qKwrd[i];
               qKwrd[i] = qKwrd[j];
               qKwrd[j] = temp;
               temp = qtf[i];
               qtf[i] = qtf[j];
               qtf[j] = temp;
            }
         }
      }
      
      //qKwrd ����Ͽ� ������ �� �Ǿ����� Ȯ��
      System.out.println("qKwrd : ");
      for(int i = 0; i < qKwrd.length; i++) {
         System.out.println(qKwrd[i]);
      }
      System.out.println("qtf : ");
      for(int i = 0; i < qtf.length; i++) {
         System.out.println(qtf[i]);
      }
      
      //������ ���絵�� ����Ͽ� ������ �������
      double[][] sim = new double[eachDoc[0].length/2][2];
      double sum;
      
      
      //������ȣ �Է�, ����ġ ��� �� ����
      for(int i = 0; i < sim.length;i++) {
         sim[i][0] = i;
         sum = 0.0;
         for(int j = 0; j < eachDoc.length; j++) {
            sum += Double.parseDouble(qtf[j]) * Double.parseDouble(eachDoc[j][(i*2) + 1]);
         }
         sim[i][1] = sum;
      }
      
      //sim ����켭 Ȯ��
      System.out.println("sim : ");
      for(int i = 0; i < sim.length; i++) {
         for(int j = 0; j < sim[i].length; j++) {
            System.out.print(sim[i][j] + " ");
         }
         System.out.println();
      }
      
      // �и� �� ||A|| ���
      double sA = 0;
      for(int i = 0; i < qtf.length; i++) {
    	  sA += Double.parseDouble(qtf[i])*Double.parseDouble(qtf[i]);
      }
      sA = Math.sqrt(sA);
      System.out.println("sA : " + sA);
      
      // �и� �� ||B|| ���
      double sB[] = new double[sim.length];
//      System.out.println("sB : ");
//      for(int i = 0; i < sB.length; i++) {
//    	  System.out.println(sB[i]);
//      }
      for(int i = 0; i < sB.length; i++) {
    	  for(int j = 0; j < eachDoc.length; j++) {
    		  sB[i] += Double.parseDouble(eachDoc[j][i*2 + 1])*Double.parseDouble(eachDoc[j][i*2 + 1]);
    	  }
    	  sB[i] = Math.sqrt(sB[i]);
      }
      
      System.out.println("sB : ");
      for(int i = 0; i < sB.length; i++) {
    	  System.out.println(sB[i]);
      }
      
      //Cosine similarity �� ������ �������
      double[][] simForCos = new double[eachDoc[0].length/2][2];
      
      for(int i = 0; i < simForCos.length; i++) {
    	  simForCos[i][0] = i;
			if (sA * sB[i] != 0) {
				simForCos[i][1] = sim[i][1] / (sA * sB[i]);
			}
			else {
				simForCos[i][1] = 0;
			}
      }
      
      
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
      
      //Cosine similarity �߿䵵 ������ �������� ����
      for(int i = 0; i < simForCos.length - 1; i++) {
          for(int j = i+1; j < simForCos.length; j++) {
             if(simForCos[i][1] < simForCos[j][1]) {
                double temp = simForCos[i][1];
                simForCos[i][1] = simForCos[j][1];
                simForCos[j][1] = temp;
                temp = simForCos[i][0];
                simForCos[i][0] = simForCos[j][0];
                simForCos[j][0] = temp;
             }
          }
       }
      
      
      //���� id�� Ÿ��Ʋ ���� ��������(�ϵ� �ڵ�)
      String[] title = new String[simForCos.length];
      
      File file = new File("./collection.xml");
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document document = db.parse(file);
      document.getDocumentElement().normalize();
      NodeList nList = document.getElementsByTagName("doc");
      
      for(int i = 0; i < nList.getLength(); i++) {
         Node nNode = nList.item(i);
         if(nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            title[i] = eElement.getElementsByTagName("title").item(0).getTextContent();
         }
      }
      
      //���� 3������ ��� (���絵�� 0�� ��� ������)
      for(int i = 0; i < sim.length && i < 3; i++)
         if(simForCos[i][1] > 0)
            System.out.printf("%d�� : ����(%d) , Ÿ��Ʋ : (%s) , ���絵 : (%f)\n",i+1,(int)simForCos[i][0],title[(int)simForCos[i][0]], simForCos[i][1]);
      
   }
}