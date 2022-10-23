import syntaxtree.*;
import visitor.*;

public class A4 {
   public static void main(String [] args) {
      try {

         Node root = new microIRParser(System.in).Goal();

         MiniRAConvertor RA = new MiniRAConvertor();

         root.accept(RA, null); 
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}