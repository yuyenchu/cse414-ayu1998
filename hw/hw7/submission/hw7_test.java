public class hw7_test{  
  public static void main(String[] args) throws Exception{
      String str = "a";
      str = put(str);
      System.out.println(str);
  }

  public static String put(String str){
      return str += "abc";
  }
}