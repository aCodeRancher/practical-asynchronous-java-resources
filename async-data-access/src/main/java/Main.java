
import com.mysql.cj.xdevapi.*;

import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args)  {
          /*   Connection connection  =   DriverManager.getConnection(
                      "jdbc:mysql://localhost:3307/test","root", "test");

             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT * FROM Person");

             if (result.next()){
                  System.out.println("test");
             }*/
        Session mySession = new SessionFactory().getSession("mysqlx://localhost:33060/test?user=root&password=test");

        Schema myDb = mySession.getSchema("test");

         Table table = myDb.getTable("Person");
         SelectStatement statement = table.select("firstname","lastname");
         CompletableFuture<RowResult> future = statement.executeAsync();
         future.whenComplete((result, ex) -> {
              System.out.println("asda");
         });


        mySession.close();
    }
}
