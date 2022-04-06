package OpenSrcSW_MainProject;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class kuir {

   public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException, ClassNotFoundException {
      // TODO Auto-generated method stub
      
      String command = args[0];
      String path = args[1];
      String command2 = args[2];
      String query = args[3];
      
      if(command.equals("-c")){
         makeCollection collection = new makeCollection(path);
         collection.makeXml();
      }
      else if(command.equals("-k")) {
         makeKeyword keyword = new makeKeyword(path);
         keyword.convertXml();
      }
      else if(command.equals("-i")) {
         indexer index = new indexer(path);
         index.convertPost();
      }
      else if(command.equals("-s")) {
         System.out.print("Query : ");
         if (command2.equals("-q")) {
            searcher search = new searcher(path, query);
            search.query();
         }
      }
   }
}