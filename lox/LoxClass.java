package lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable{
	final String name;
	private final Map<String, LoxFunction> methods;

	public LoxClass(String name, Map<String, LoxFunction> methods) {
		this.name = name;
		this.methods = methods;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		return new LoxInstance(this);
	}

	@Override
	public int arity() {
		return 0;
	}

	LoxFunction findMethod(String name) {
		if(methods.containsKey(name)) {
			return methods.get(name);
		}

		return null;
	}
}
