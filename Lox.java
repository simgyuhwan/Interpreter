import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Lox {

  public static void main(String[] args) {
    if(args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    }else if(args.length == 1){
      runFile(args[0]);
    }else {
      runPrompt();
    }
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for(;;) {
      System.out.println("> ");
      String line = reader.readLine();
      if(line == null) break;
      run(line);
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    
  }
}
