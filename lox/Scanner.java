package lox;

import static lox.TokenType.COMMA;
import static lox.TokenType.DOT;
import static lox.TokenType.EOF;
import static lox.TokenType.LEFT_BRACE;
import static lox.TokenType.LEFT_PAREN;
import static lox.TokenType.MINUS;
import static lox.TokenType.RIGHT_BRACE;
import static lox.TokenType.RIGHT_PAREN;
import static lox.TokenType.SEMICOLON;
import static lox.TokenType.STAR;

import java.util.ArrayList;
import java.util.List;

class Scanner {

  private final String source;
  private int start = 0;
  private final List<Token> tokens = new ArrayList<>();
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    // end
    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(' -> addToken(LEFT_PAREN);
      case ')' -> addToken(RIGHT_PAREN);
      case '{' -> addToken(LEFT_BRACE);
      case '}' -> addToken(RIGHT_BRACE);
      case ';' -> addToken(COMMA);
      case '.' -> addToken(DOT);
      case '-' -> addToken(MINUS);
      case '+' -> addToken(SEMICOLON);
      case '*' -> addToken(STAR);
    }
  }

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
  }
}
