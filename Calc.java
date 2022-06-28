import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Map;

/**
 * A simple calculator program reading arithmetic expressions from the standard
 * input, evaluating them, and printing the results on the standard output.
 *
 * <p>Implements a <a href="https://en.wikipedia.org/wiki/Recursive_descent_parser">recursive descent parser</a>.
 * Other exemplary implementations are <a href="https://github.com/sirthias/parboiled">Parboiled</a> and <a href="https://github.com/javacc/javacc">JavaCC</a></p>
 */
public class Calc {
	/**
	 * String representation of the arithmetic expression to parse.
	 */
	private String calc;

	/**
	 * Current parser position in the expression.
	 * -1 indicates that it has not started yet.
	 */
	private int currentPos = -1;

	/**
	 * Integer representation of the character currently being parsed.
	 */
	private int currentChar;

	/**
	 * Moves currentPos by one and sets currentChar to the next character to evaluate.
	 * <p>Note: currentChar is set to -1 when currentPos is out of calc's bounds, meaning that the entire expression has been parsed.</p>
	 * @return boolean indicating whether the move was successful.
	 */
	private boolean move() {
		currentPos++;
		currentChar = (currentPos < calc.length()) ? calc.charAt(currentPos) : -1;

		return currentChar != -1;
	}

	/**
	 * Consumes the currently-evaluated character if it is equal to the provided character.
	 * @param character integer representation of the character to consume.
	 * @return boolean indicating whether the character got consumed or not.
	 */
	private boolean consume(int character) {
		return currentChar == character && move();
	}

	/**
	 * Checks if the provided character (as integer) is a number.
	 * @param character the character to check.
	 * @return boolean indicating whether the provided character is a number.
	 */
	private boolean charIsNumber(int character){
		return character >= '0' && character <= '9';
	}

	/**
	 * Checks if the provided character (as integer) is an alphabetic character.
	 * @param character the character to check.
	 * @return boolean indicating whether the provided character is an alphabetic character.
	 */
	private boolean charIsAlphabetChar(int character){
		return character >= 'a' && character <= 'z' || character >= 'A' && character <= 'Z';
	}

	/**
	 * Adds or subtracts two parsed doubles, depending on the consumed character(s), if there are any.
	 * Consumes the + and - characters.
	 * @return the result of the addition(s) and/or subtraction(s), or the parsed Double.
	 */
	private double parseExpression(){
		double x = parseTerm();

		for(;;)
			if(consume('+')) x += parseTerm();
			else if(consume('-')) x -= parseTerm();
			else break;

		return x;
	}

	/**
	 * Attempts to multiply or divide two parsed doubles, depending on the consumed character(s).
	 * Consumes the * and / characters.
	 * @return the result of the multiplication(s) or division(s), or the parsed Double.
	 */
	private double parseTerm(){
		double x = parseFactor();

		for(;;)
			if (consume('*')) x *= parseFactor();
			else if (consume('/')) x /= parseFactor();
			else break;

		return x;
	}

	/**
	 * Attempts to parse:
	 * <ul>
	 *     <li>Parentheses</li>
	 *     <li>Numbers</li>
	 *     <li>Bindings</li>
	 *     <li>Functions</li>
	 * </ul>
	 * @return the result of the operations between parentheses OR the parsed numbers OR the Double associated with the parsed binding ID OR the result of a call to the provided function
	 */
	private double parseFactor(){
		// Evaluate multiple positives/negations, ex. 2+-1
		if(consume('+')) return parseFactor();
		if(consume('-')) return -parseFactor();

		double x;
		int startPos = currentPos;

		// Evaluate parentheses
		if(consume('(')) {
			x = parseExpression();
			consume(')');

			return x;
		}

		// Evaluate number(s)
		if(charIsNumber(currentChar) || currentChar == '.'){
			while(charIsNumber(currentChar) || currentChar == '.') move();

			return Double.parseDouble(calc.substring(startPos, currentPos));
		}

		// Evaluate special binding _
		if(currentChar == '_' && bindings().containsKey("_"))
			return bindings().get("_");

		// Evaluate function
		if(charIsAlphabetChar(currentChar)){
			while(charIsAlphabetChar(currentChar)) move();

			String name = calc.substring(startPos, currentPos);

			if(consume('=')){
				startPos = currentPos;

				while(charIsNumber(currentChar) || currentChar == '.') move();

				double numValue = Double.parseDouble(calc.substring(startPos, currentPos));
				bindings().put(name, numValue);

				return numValue;
			}

			// Binding contains a specific value
			if(bindings().containsKey(name))
				return bindings().get(name);

			// parseFactor evaluates what is between parentheses ; which is exactly what is needed here.
			x = parseFactor();

			x = switch(name){
				case "sqrt" -> Math.sqrt(x);
				case "log" -> Math.log(x);
				case "sin" -> Math.sin(x);
				case "cos" -> Math.cos(x);

				default -> throw new RuntimeException(String.format("Unknown function (%s)", name));
			};

			return x;
		}

		throw new RuntimeException(String.format("Could not parse factor '%c' at position %d", (char)currentChar, currentPos));
	}

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
	public double eval(String expr) {
		this.calc = expr;
		currentPos = -1;

		if(move()){
			double x = parseExpression();

			bindings().put("_", x);
			return x;
		}

		throw new RuntimeException(String.format("Could not parse the provided expression (%s)", calc));
	}
	
	public Map<String,Double> bindings() {
		return bindings;
	}
	
	private final Map<String,Double> bindings = new TreeMap<>();

	public static void main(String[] args) throws IOException {
		Calc calc = new Calc();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				PrintWriter out = new PrintWriter(System.out, true)) {
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				try {
					if (!line.startsWith(":")) {
						// handle expression
						out.println(calc.eval(line));
					} else {
						// handle command
						String[] command = line.split("\\s+", 2);
						switch (command[0]) {
							case ":vars":
								calc.bindings().forEach((name, value) ->
										out.println(name + " = " + value));
								break;
							case ":clear":
								if (command.length == 1) {
									// clear all
									calc.bindings().clear();
								} else {
									// clear requested
									calc.bindings().keySet().removeAll(Arrays.asList(command[1].split("\\s+")));
								}
								break;
							case ":exit":
							case ":quit":
								System.exit(0);
								break;
							default:
								throw new RuntimeException("unrecognized command: " + line);
						}
					}
				} catch (Exception ex) {
					System.err.println("*** ERROR: " + ex.getMessage());
				}
			}
		}
	}
}
