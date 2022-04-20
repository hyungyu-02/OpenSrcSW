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
      
      String qKwrd[];//쿼리 키워드 저장공간
      String qtf[];//쿼리 weight 저장공간
      
      //꼬꼬마
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
      
      //qKwrd 랑 qtf 출력해서 확인
//      for(int i = 0; i < qKwrd.length; i++) {
//         System.out.println(qKwrd[i]);
//         System.out.println(qtf[i]);
//      }
      
      // index.post 파일에서 해쉬맵 읽어오기
      FileInputStream fstream = new FileInputStream(data_path);
      ObjectInputStream objectInputstream = new ObjectInputStream(fstream);

      Object obj = objectInputstream.readObject();
      objectInputstream.close();
      
      HashMap<String, String> hashMap = (HashMap) obj;
      Iterator<String> it = hashMap.keySet().iterator();
      
      //쿼리로 검색된 단어들만 분류하여 단어들이 가지는 가중치를 저장할 저장공간
      String[][] searched = new String[qKwrd.length][2];
      
      int count = 0;
      while (it.hasNext()) {
         String key = it.next();
         String value = hashMap.get(key);
         //System.out.println(key + " : " + value);
         
         for(int i = 0; i < qKwrd.length; i++) {
            if(qKwrd[i].equals(key)) {
               searched[count][0] = key; //단어
               searched[count++][1] = value; //문서별로 정렬된 가중치들
            }
         }
      }
      
      //searched 출력하여 확인
//      System.out.println("searched : ");
//      for(int i = 0; i < searched.length; i++) {
//         for(int j = 0; j < searched[i].length; j++) {
//            System.out.println(searched[i][j]);
//         }
//      }
      
      CalcSim(qKwrd, qtf, searched);//유사도 계산 및 순위대로 출력
      
      System.out.println("5주차 실행완료");
   }
   
   private void CalcSim(String qKwrd[], String qtf[], String[][] searched) throws ParserConfigurationException, SAXException, IOException {
      
      //하나의 String 으로 저장된 문서별 가중치를 쪼개어 저장할 저장공간
      String[][] eachDoc = new String[searched.length][];
      
      for(int i = 0; i < eachDoc.length; i++) {
         eachDoc[i] = searched[i][1].split(" ");
      }
      
      //eachDoc 출력하여 확인
      System.out.println("eachDoc : ");
      for(int i = 0; i < eachDoc.length; i++) {
         for(int j = 0; j < eachDoc[i].length; j++) {
               System.out.print(eachDoc[i][j]+" ");
         }
         System.out.println();
      }
      
      //qKwrd 와 searched 간의 단어 순서 불일치를 일치하도록 정렬함
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
      
      //qKwrd 출력하여 정렬이 잘 되었는지 확인
      System.out.println("qKwrd : ");
      for(int i = 0; i < qKwrd.length; i++) {
         System.out.println(qKwrd[i]);
      }
      System.out.println("qtf : ");
      for(int i = 0; i < qtf.length; i++) {
         System.out.println(qtf[i]);
      }
      
      //문서별 유사도를 계산하여 저장할 저장공간
      double[][] sim = new double[eachDoc[0].length/2][2];
      double sum;
      
      
      //문서번호 입력, 가중치 계산 및 저장
      for(int i = 0; i < sim.length;i++) {
         sim[i][0] = i;
         sum = 0.0;
         for(int j = 0; j < eachDoc.length; j++) {
            sum += Double.parseDouble(qtf[j]) * Double.parseDouble(eachDoc[j][(i*2) + 1]);
         }
         sim[i][1] = sum;
      }
      
      //sim 출력헤서 확인
      System.out.println("sim : ");
      for(int i = 0; i < sim.length; i++) {
         for(int j = 0; j < sim[i].length; j++) {
            System.out.print(sim[i][j] + " ");
         }
         System.out.println();
      }
      
      // 분모에 들어갈 ||A|| 계산
      double sA = 0;
      for(int i = 0; i < qtf.length; i++) {
    	  sA += Double.parseDouble(qtf[i])*Double.parseDouble(qtf[i]);
      }
      sA = Math.sqrt(sA);
      System.out.println("sA : " + sA);
      
      // 분모에 들어갈 ||B|| 계산
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
      
      //Cosine similarity 를 저장할 저장공간
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
      
      
      //중요도 순으로 내림차순 정렬
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
      
      //Cosine similarity 중요도 순으로 내림차순 정렬
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
      
      
      //문서 id별 타이틀 정보 가져오기(하드 코딩)
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
      
      //상위 3위까지 출력 (유사도가 0인 경우 제외함)
      for(int i = 0; i < sim.length && i < 3; i++)
         if(simForCos[i][1] > 0)
            System.out.printf("%d등 : 문서(%d) , 타이틀 : (%s) , 유사도 : (%f)\n",i+1,(int)simForCos[i][0],title[(int)simForCos[i][0]], simForCos[i][1]);
      
   }
}