#include <stdio.h>

#include "common.h"
#include "compiler.h"
#include "scanner.h"

void compile(const char* source) {
  initScanner(source);
  int line = -1;
  for(;;) {
    Token Token = scanToken();
    if (Token.line != line) {
      printf("%4d ", Token.line);
      line = Token.line;
    } else {
      printf("    | ");
    }
    printf("%2d '%.*s'\n", Token.type, Token.length, Token.start);

    if(Token.type == TOKEN_EOF) break;
  }
}