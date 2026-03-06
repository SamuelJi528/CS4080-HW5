//> Functions lox-function
package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Token name;
  private final List<Token> params;
  private final List<Stmt> body;
//> closure-field
  private final Environment closure;
  
//< closure-field
//> Classes is-initializer-field
  private final boolean isInitializer;

  // Constructor for Stmt.Function declarations
  LoxFunction(Stmt.Function declaration, Environment closure,
              boolean isInitializer) {
    this.name = declaration.name;
    this.params = declaration.params;
    this.body = declaration.body;
    this.closure = closure;
    this.isInitializer = isInitializer;
  }

  // Constructor for Expr.Function expressions (anonymous)
  LoxFunction(Expr.Function expr, Environment closure) {
    this.name = null;
    this.params = expr.params;
    this.body = expr.body;
    this.closure = closure;
    this.isInitializer = false;
  }

  // Private constructor for bind()
  private LoxFunction(Token name, List<Token> params, List<Stmt> body,
                      Environment closure, boolean isInitializer) {
    this.name = name;
    this.params = params;
    this.body = body;
    this.closure = closure;
    this.isInitializer = isInitializer;
  }
//< Classes is-initializer-field
//> Classes bind-instance
  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define("this", instance);
    return new LoxFunction(name, params, body, environment,
                           isInitializer);
  }
//< Classes bind-instance
//> function-to-string
  @Override
  public String toString() {
    if (name == null) return "<fn>";
    return "<fn " + name.lexeme + ">";
  }
//< function-to-string
//> function-arity
  @Override
  public int arity() {
    return params.size();
  }
//< function-arity
//> function-call
  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
//> call-closure
    Environment environment = new Environment(closure);
//< call-closure
    for (int i = 0; i < params.size(); i++) {
      environment.defineAt(i, arguments.get(i));
    }

//> catch-return
    try {
      interpreter.executeBlock(body, environment);
    } catch (Return returnValue) {
//> Classes early-return-this
      if (isInitializer) return closure.getAt(0, "this");

//< Classes early-return-this
      return returnValue.value;
    }
//< catch-return
//> Classes return-this

    if (isInitializer) return closure.getAt(0, "this");
//< Classes return-this
    return null;
  }
//< function-call
}
