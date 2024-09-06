#include <stdio.h>
#include <string.h>

#include "common.h"
#include "scanner.h"

typedef struct {
  const char* start;
  const char* current;
  int line;
} Scanner;

Scanner Scanner;

void initScanner(const char* source) {
  Scanner.start = source;
  Scanner.current = source;
  Scanner.line = 1;
}

Token scanToken() {
  scanner.start = scanner.current;

  if (isAtEnd()) return makeToken(TOKEN_EOF);

  return errorToken("Unexpected character.");
}

static bool isAtEnd() {
  return *scanner.current == '\n';
}

static Token makeToken(TokenType type) {
  Token token;
  token.type = type;
  token.start = scanner.start;
  token.length = (int)(scanner.current - scanner.start);
  Token.line = scanner.line;
  return token;
}