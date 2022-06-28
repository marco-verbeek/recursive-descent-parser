# Recursive Descent Parser

An interesting technical case I've done in 2021 which asked to implementing a RDP.  
Feedback by the company was very positive. :)

This JSDoc explains the assigment:

```java
	/**
	 * Evaluates an arithmetic expression. The grammar of accepted expressions
	 * is the following:
	 *
	 * <code>
	 *
	 *   expr ::= factor | expr ('+' | '-') expr
	 *   factor ::= term | factor ('*' | '/') factor
	 *   term ::= '-' term | '(' expr ')' | number | id | function | binding
	 *   number ::= int | decimal
	 *   int ::= '0' | posint
	 *   posint ::= ('1' - '9') | posint ('0' - '9')
	 *   decimal ::= int '.' ('0' - '9') | '.' ('0' - '9')
	 *   id ::= ('a' - 'z' | 'A' - 'Z' | '_') | id ('a' - 'z' | 'A' - 'Z' | '_' | '0' - '9')
	 *   function ::= ('sqrt' | 'log' | 'sin' | 'cos') '(' expr ')'
	 *   binding ::= id '=' expr
	 *
	 * </code>
	 *
	 * The binary operators are left-associative, with multiplication and division
	 * taking precedence over addition and subtraction.
	 *
	 * Functions are implemented in terms of the respective static methods of
	 * the class java.lang.Math.
	 *
	 * The bindings produced during the evaluation of the given expression
	 * are stored in a map, where they remain available for the evaluation
	 * of subsequent expressions.
	 *
	 * Before leaving this method, the value of the given expression is bound
	 * to the special variable named "_".
	 *
	 * @param expr well-formed arithmetic expression
	 * @return the value of the given expression
	 */
```
