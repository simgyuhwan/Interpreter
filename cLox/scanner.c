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