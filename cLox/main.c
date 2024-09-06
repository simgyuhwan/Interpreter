#include "common.h"
#include "chunk.h"
#include "debug.h"
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <String.h>
#include "vm.h"

static void repl() {
  char line[1024];
  for (;;) {
    printf("> ");

    if (!fgets(line, sizeof(line), stdin)) {
      printf("\n");
      break;
    }

    interpret(line);
  }
}

static void runFile(const char* path) {
  char* source = readFile(path);
  InterpretResult result = interpret(source);
  free(source);

  if(result == INTERPRET_COMPILE_ERROR) exit(65);
  if(result == INTERPRET_RUNTIME_ERROR) exit(70);
}

static char* readFile(const char* path) {
  FILE* file = fopen(path, "rb"); // 바이너리로 읽어옴
  if(file == NULL) {
    fprintf(stderr, "Could not open file \"%s\".\n", path);
    exit(74);
  }

  fseek(file, 0L, SEEK_END);  // 제일 끝 위치로 파일 포인터 이동
  size_t fileSize = ftell(file);  // 파일 포인터 위치로 파일의 크기 확인
  rewind(file);  // 파일 포인터 앞으로 이동

  char* buffer = (char*)malloc(fileSize + 1); // 파일 사이즈만큼 버퍼 동적할당
  if(buffer == NULL) {
    fprintf(stderr, "Not enough memory to read \"%s\".\n", path);
    exit(74);
  }

  size_t bytesRead = fread(buffer, sizeof(char), fileSize, file); // 버퍼에 파일의 내용 읽기
  if(bytesRead < fileSize) {
    fprintf(stderr, "Could not read file \"%s\".\n", path);
    exit(74);
  }
  
  buffer[bytesRead] = '\0'; // 버퍼 마지막에 널을 추가해 문자열의 끝을 추가

  fclose(file); // 리소스 해제
  return buffer; // 버퍼 반환
}

int main(int argc, const char* argv[]) {
  initVM();
  // Chunk chunk;
  // initChunk(&chunk);

  // int constant = addConstant(&chunk, 1.2);
  // writeChunk(&chunk, OP_CONSTANT, 123);
  // writeChunk(&chunk, constant, 123);

  // constant = addConstant(&chunk, 3.4);
  // writeChunk(&chunk, OP_CONSTANT, 123);
  // writeChunk(&chunk, constant, 123);

  // writeChunk(&chunk, OP_ADD, 123);

  // constant = addConstant(&chunk, 5.6);
  // writeChunk(&chunk, OP_CONSTANT, 123);
  // writeChunk(&chunk, constant, 123);
  // writeChunk(&chunk, OP_DIVIDE, 123);

  // writeChunk(&chunk, OP_NEGATE, 123);
  // writeChunk(&chunk, OP_RETURN, 123);

  // disassembleChunk(&chunk, "test chunk");
  // interpret(&chunk);
  // freeVM();

  // freeChunk(&chunk);

  if(argc == 1) {
    repl();
  } else if (argc == 2) {
    runFile(argv[1]);
  } else {
    fprintf(stderr, "Usage: clox [path]\n");
    exit(64);
  }

  freeVM();
  Sleep(10000);
  return 0;
}