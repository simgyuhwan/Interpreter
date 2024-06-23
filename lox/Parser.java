package lox;

import static lox.TokenType.AND;
import static lox.TokenType.BANG;
import static lox.TokenType.BANG_EQUAL;
import static lox.TokenType.ELSE;
import static lox.TokenType.EOF;
import static lox.TokenType.EQUAL;
import static lox.TokenType.EQUAL_EQUAL;
import static lox.TokenType.FALSE;
import static lox.TokenType.GREATER;
import static lox.TokenType.GREATER_EQUAL;
import static lox.TokenType.IDENTIFIER;
import static lox.TokenType.IF;
import static lox.TokenType.LEFT_BRACE;
import static lox.TokenType.LEFT_PAREN;
import static lox.TokenType.LESS;
import static lox.TokenType.LESS_EQUAL;
import static lox.TokenType.MINUS;
import static lox.TokenType.NIL;
import static lox.TokenType.NUMBER;
import static lox.TokenType.OR;
import static lox.TokenType.PLUS;
import static lox.TokenType.PRINT;
import static lox.TokenType.RIGHT_BRACE;
import static lox.TokenType.RIGHT_PAREN;
import static lox.TokenType.SEMICOLON;
import static lox.TokenType.SLASH;
import static lox.TokenType.STAR;
import static lox.TokenType.STRING;
import static lox.TokenType.TRUE;
import static lox.TokenType.VAR;
import static lox.TokenType.WHILE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class Parser {

  private final List<Token> tokens;

  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  private static class ParseError extends RuntimeException {

  }
  //  Expr parse() {
  //    try {
  //      return expression();
  //    } catch (ParseError error) {
  //      return null;
  //    }

  //  }


  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
//			statements.add(statement());
      statements.add(declaration());
    }
    return statements;
  }

  private Stmt declaration() {
    try {
      if (match(VAR)) {
        return varDeclaration();
      }
      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }

  }

  private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new Stmt.Var(name, initializer);
  }

  private Stmt statement() {
    if (match(IF)) {
      return ifStatement();
    }
    if (match(PRINT)) {
      return printStatement();
    }
    if (match(WHILE)) {
      return whileStatement();
    }
    if (match(LEFT_BRACE)) {
      return new Stmt.Block(block());
    }
    return expressionStatement();
  }

  private Stmt whileStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'while'");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ') after condition");
    Stmt body = statement();

    return new Stmt.While(condition, body);
  }

  private Stmt ifStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after if condition");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }
    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expect ') after block");
    return statements;
  }

  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(SEMICOLON, "Expect ';', after, expression");
    return new Stmt.Expression(expr);
  }

  private Stmt printStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }


  private Expr expression() {
    return assignment();
  }

  private Expr equality() {
    return parseLeftAssociativeBinaryExpr(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
  }

  private Expr comparison() {
    return parseLeftAssociativeBinaryExpr(this::term, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
  }

  private Expr term() {
    return parseLeftAssociativeBinaryExpr(this::factor, MINUS, PLUS);
  }

  private Expr factor() {
    return parseLeftAssociativeBinaryExpr(this::unary, SLASH, STAR);
  }

  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

    return primary();
  }

  private Expr primary() {
    if (match(FALSE)) {
      return new Expr.Literal(false);
    }
    if (match(TRUE)) {
      return new Expr.Literal(true);
    }
    if (match(NIL)) {
      return new Expr.Literal(null);
    }
    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression");
  }

  private Expr parseLeftAssociativeBinaryExpr(Supplier<Expr> operandParser,
      TokenType... operatorTypes) {
    Expr expr = operandParser.get();

    while (match(operatorTypes)) {
      Token operator = previous();
      Expr right = operandParser.get();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr assignment() {
    Expr expr = or();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable) expr).name;
        return new Expr.Assign(name, value);
      }
      error(equals, "Invalid assignment target.");
    }
    return expr;
  }

  private Expr or() {
    Expr expr = and();

    while (match(OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, operator, right);
    }
    return expr;
  }

  private Expr and() {
    Expr expr = equality();

    while (match(AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new Expr.Logical(expr, operator, right);
    }
    return expr;
  }

  private Token consume(TokenType type, String message) {
    if (check(type)) {
      return advance();
    }

    throw error(peek(), message);
  }

  private ParseError error(Token Token, String message) {
    Lox.error(Token, message);
    return new ParseError();
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) {
        return;
      }

      switch (peek().type) {
        case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
        }
      }

      advance();
    }
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) {
      return false;
    }
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) {
      current++;
    }
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
}
