package lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
	private final Interpreter interpreter;
	private final Stack<Map<String, Boolean>> scopes = new Stack<>();

	Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		beginScope();
		resolve(stmt.statements);
		endScope();
		return null;
	}

	void resolve(List<Stmt> statements) {
		for (Stmt statement : statements) {
			resolve(statement);
		}
	}

	private void resolve(Stmt stmt) {
		stmt.accept(this);
	}

	private void resolve(Expr expr) {
		expr.accept(this);
	}

	private void beginScope() {
		scopes.push(new HashMap<String, Boolean>());
	}

	private void endScope() {
		scopes.pop();
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		return null;
	}

	@Override
	public Void visitPrintStmt(Stmt.Print stmt) {
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		return null;
	}

	@Override
	public Void visitFunctionStmt(Stmt.Function stmt) {
		return null;
	}

	@Override
	public Void visitIfStmt(Stmt.If stmt) {
		return null;
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		declare(stmt.name);
		if (stmt.initializer != null) {
			resolve(stmt.initializer);
		}
		define(stmt.name);
		return null;
	}

	private void define(Token name) {
		if (scopes.isEmpty())
			return;
		scopes.peek().put(name.lexeme, true);
	}

	private void declare(Token name) {
		if (scopes.isEmpty())
			return;

		Map<String, Boolean> scope = scopes.peek();
		scope.put(name.lexeme, false);
	}

	@Override
	public Void visitWhileStmt(Stmt.While stmt) {
		return null;
	}

	@Override
	public Void visitBinaryExpr(Expr.Binary expr) {
		return null;
	}

	@Override
	public Void visitGroupingExpr(Expr.Grouping expr) {
		return null;
	}

	@Override
	public Void visitCallExpr(Expr.Call expr) {
		return null;
	}

	@Override
	public Void visitLiteralExpr(Expr.Literal expr) {
		return null;
	}

	@Override
	public Void visitLogicalExpr(Expr.Logical expr) {
		return null;
	}

	@Override
	public Void visitUnaryExpr(Expr.Unary expr) {
		return null;
	}

	@Override
	public Void visitVariableExpr(Expr.Variable expr) {
		if (!scopes.isEmpty() && Boolean.FALSE.equals(scopes.peek().get(expr.name.lexeme))) {
			Lox.error(expr.name, "Can't read local variable in its own initializer.");
		}

		resolveLocal(expr, expr.name);
		return null;
	}

	private void resolveLocal(Expr.Variable expr, Token name) {
		for(int i = scopes.size()-1; i >= 0; i--) {
			if(scopes.get(i).containsKey(name.lexeme)) {
				interpreter.resolve(expr, scopes.size()-1-i);
				return;
			}
		}
	}

	@Override
	public Void visitAssignExpr(Expr.Assign expr) {
		return null;
	}
}