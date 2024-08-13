package lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {

  final Environment enclosing;
  private final Map<String, Object> values = new HashMap<>();

  public Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  public Environment() {
    enclosing = null;
  }

  void define(String name, Object value) {
    values.put(name, value);
  }

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    if (enclosing != null) {
      return enclosing.get(name);
    }
    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }
    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

  public Object getAt(Integer distance, String name) {
    return ancestor(distance).values.get(name);
  }

  Environment ancestor(Integer distance) {
    Environment environment = this;
    for(int i=0; i<distance; i++) {
      assert environment != null;
      environment = environment.enclosing;
    }
    return environment;
  }
}
