import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * QuestionBank contains 64 verified, high-quality programming challenges
 * categorized by topic and difficulty level (1 to 5).
 */
public class QuestionBank {
    private static final List<Question> ALL_QUESTIONS = new ArrayList<>();

    static {
        // --- CATEGORY 1: JAVA FUNDAMENTALS (Difficulty 1) ---
        add(new Question(1, "Java Fundamentals", 1,
            "What is the required signature for a standard Java program entry point?",
            null,
            new String[]{
                "public static void main(String[] args)",
                "public void main(String[] args)",
                "static void run(String args)",
                "public static int main()"
            },
            0,
            "The JVM looks specifically for a public static method named 'main' returning void and accepting String[] args.",
            "Remember: it must be static so the JVM can invoke it without instantiating the class first."
        ));

        add(new Question(2, "Java Fundamentals", 1,
            "Which command compiles Java source files into bytecode (.class files)?",
            null,
            new String[]{
                "java",
                "javac",
                "javadoc",
                "jar"
            },
            1,
            "'javac' is the Java compiler. 'java' is the runtime launcher that executes compiled bytecode.",
            "Notice the 'c' at the end of the command name representing 'compiler'."
        ));

        add(new Question(3, "Java Fundamentals", 1,
            "What does the JVM (Java Virtual Machine) execute?",
            null,
            new String[]{
                "Raw Java source code (.java)",
                "Compiled bytecode (.class)",
                "Native machine assembly only",
                "Plain text documentation"
            },
            1,
            "Java source files are compiled by javac into platform-independent bytecode (.class), which the JVM interprets or JIT-compiles.",
            "Write once, run anywhere relies on the JVM interpreting bytecode."
        ));

        add(new Question(4, "Java Fundamentals", 1,
            "Which of the following is a valid single-line comment in Java?",
            null,
            new String[]{
                "# This is a comment",
                "<!-- This is a comment -->",
                "// This is a comment",
                "-- This is a comment"
            },
            2,
            "Java uses '//' for single-line comments and '/* ... */' for multi-line block comments.",
            "Languages like C, C++, and Java share the double slash '//' comment syntax."
        ));

        add(new Question(5, "Java Fundamentals", 1,
            "What is the extension of a compiled Java bytecode file?",
            null,
            new String[]{
                ".java",
                ".class",
                ".exe",
                ".bin"
            },
            1,
            "Source files have a '.java' extension, whereas compiled bytecode files have a '.class' extension.",
            "The compiler produces .class files for each class defined in a source file."
        ));

        add(new Question(6, "Java Fundamentals", 1,
            "Which naming convention is standard for Java class names?",
            null,
            new String[]{
                "camelCase (e.g., gamePanel)",
                "snake_case (e.g., game_panel)",
                "PascalCase / UpperCamelCase (e.g., GamePanel)",
                "kebab-case (e.g., game-panel)"
            },
            2,
            "Java conventions dictate PascalCase (e.g. GamePanel, PlayerRobot) for classes and interfaces, and camelCase for methods and variables.",
            "Class names start with an uppercase letter."
        ));

        // --- CATEGORY 2: VARIABLES & DATA TYPES (Difficulty 1-2) ---
        add(new Question(7, "Variables & Data Types", 1,
            "Which primitive type is used to store true or false values in Java?",
            null,
            new String[]{
                "bool",
                "boolean",
                "BooleanBit",
                "flag"
            },
            1,
            "In Java, the primitive boolean type is written in full: 'boolean'. (Unlike C++ or C# which use 'bool').",
            "Type out the whole word 'boolean'."
        ));

        add(new Question(8, "Variables & Data Types", 1,
            "What is the default value of an uninitialized int field in a Java class?",
            null,
            new String[]{
                "0",
                "1",
                "-1",
                "null"
            },
            0,
            "Numeric instance and static fields in Java default to 0 (or 0.0 for floating-point).",
            "Primitive numbers default to zero; object references default to null."
        ));

        add(new Question(9, "Variables & Data Types", 2,
            "What will be the output of this integer division snippet?",
            "int result = 7 / 2;\nSystem.out.println(result);",
            new String[]{
                "3.5",
                "3",
                "4",
                "Compilation Error"
            },
            1,
            "In Java, integer division truncates any fractional portion toward zero. 7 / 2 evaluates to 3.",
            "When both operands are integers, the result is truncated to an integer."
        ));

        add(new Question(10, "Variables & Data Types", 2,
            "How do you properly declare a constant variable in Java?",
            null,
            new String[]{
                "const int MAX_LIVES = 3;",
                "final int MAX_LIVES = 3;",
                "readonly int MAX_LIVES = 3;",
                "static immutable int MAX_LIVES = 3;"
            },
            1,
            "Java uses the 'final' keyword to indicate that a variable's value cannot be reassigned once initialized.",
            "Look for the keyword that means 'cannot be changed after initialization'."
        ));

        add(new Question(11, "Variables & Data Types", 2,
            "Which of the following data types uses 64 bits to store an integer value?",
            null,
            new String[]{
                "int",
                "short",
                "long",
                "byte"
            },
            2,
            "byte is 8-bit, short is 16-bit, int is 32-bit, and long is 64-bit.",
            "It holds the largest integer range among Java primitives."
        ));

        add(new Question(12, "Variables & Data Types", 2,
            "What is the result of casting a double to an int in the following code?",
            "double energy = 9.85;\nint truncated = (int) energy;",
            new String[]{
                "10 (rounded)",
                "9 (fraction dropped)",
                "9.85",
                "Throws RuntimeException"
            },
            1,
            "Explicit casting from double to int performs truncation (drops the decimal part), yielding 9.",
            "Casting does not round; it simply cuts off the decimal."
        ));

        // --- CATEGORY 3: OPERATORS & BOOLEAN LOGIC (Difficulty 2) ---
        add(new Question(13, "Operators & Logic", 2,
            "What is the output of the modulo operator in this expression?",
            "int remainder = 17 % 5;",
            new String[]{
                "2",
                "3",
                "3.4",
                "1"
            },
            0,
            "17 divided by 5 is 3 with a remainder of 2. 17 % 5 = 2.",
            "5 * 3 = 15; 17 - 15 = 2."
        ));

        add(new Question(14, "Operators & Logic", 2,
            "Which operator performs a short-circuit logical AND evaluation in Java?",
            null,
            new String[]{
                "&",
                "&&",
                "AND",
                "|"
            },
            1,
            "'&&' evaluates the left operand first; if false, it does not evaluate the right operand (short-circuit).",
            "Single '&' evaluates both sides; double '&&' short-circuits."
        ));

        add(new Question(15, "Operators & Logic", 2,
            "What does the following logical expression evaluate to?",
            "boolean hasRedKey = false;\nboolean isOverride = true;\nboolean access = hasRedKey || isOverride;",
            new String[]{
                "false",
                "true",
                "null",
                "Error"
            },
            1,
            "In logical OR (||), if at least one operand is true, the overall expression evaluates to true.",
            "false OR true yields true."
        ));

        add(new Question(16, "Operators & Logic", 2,
            "What is the value of 'x' after executing this compound assignment?",
            "int x = 10;\nx += 5 * 2;",
            new String[]{
                "30",
                "20",
                "25",
                "17"
            },
            1,
            "Multiplication has higher precedence than compound assignment. 5 * 2 = 10; then x = 10 + 10 = 20.",
            "Operations on the right side of '+=' are evaluated first."
        ));

        add(new Question(17, "Operators & Logic", 2,
            "What is the result of the ternary conditional operator here?",
            "int shield = 40;\nString status = (shield > 50) ? \"HIGH\" : \"CRITICAL\";",
            new String[]{
                "HIGH",
                "CRITICAL",
                "null",
                "Compilation Error"
            },
            1,
            "The condition (shield > 50) is false (40 is not > 50), so the expression selects the value after the colon: 'CRITICAL'.",
            "syntax: condition ? valueIfTrue : valueIfFalse."
        ));

        add(new Question(18, "Operators & Logic", 2,
            "What is the difference between prefix '++i' and postfix 'i++' in an expression?",
            null,
            new String[]{
                "++i increments after returning value; i++ increments before",
                "++i increments before returning value; i++ increments after returning value",
                "There is no difference in any context",
                "i++ cannot be used in expressions"
            },
            1,
            "Prefix '++i' updates the value before it is evaluated in the enclosing expression; postfix 'i++' evaluates the current value first, then increments.",
            "Prefix = pre-increment; Postfix = post-increment."
        ));

        // --- CATEGORY 4: IF/ELSE & SWITCH CONDITIONS (Difficulty 2-3) ---
        add(new Question(19, "Conditionals", 2,
            "In a security checkpoint, which branch executes given the variables below?",
            "int clearance = 2;\nif (clearance > 3) {\n    System.out.println(\"ADMIN\");\n} else if (clearance >= 2) {\n    System.out.println(\"OPERATOR\");\n} else {\n    System.out.println(\"GUEST\");\n}",
            new String[]{
                "ADMIN",
                "OPERATOR",
                "GUEST",
                "OPERATOR and GUEST"
            },
            1,
            "clearance is 2, which satisfies 'clearance >= 2', printing 'OPERATOR'. Once a branch matches, subsequent else-if branches are skipped.",
            "Check the relational operator '>=' on the second condition."
        ));

        add(new Question(20, "Conditionals", 2,
            "What happens if a switch case in Java does not contain a 'break' statement?",
            null,
            new String[]{
                "The program crashes with a SwitchException",
                "Execution falls through to the next case automatically",
                "The switch block restarts from the top",
                "The compiler produces a syntax error"
            },
            1,
            "Without a break statement, control falls through and executes the code of subsequent cases until a break or the end of the switch block is reached.",
            "This behavior is known as switch fall-through."
        ));

        add(new Question(21, "Conditionals", 3,
            "What will this code print for door access?",
            "boolean hasKey = true;\nboolean alarmActive = true;\nif (hasKey && !alarmActive) {\n    System.out.println(\"OPEN\");\n} else {\n    System.out.println(\"LOCKDOWN\");\n}",
            new String[]{
                "OPEN",
                "LOCKDOWN",
                "No output",
                "Both OPEN and LOCKDOWN"
            },
            1,
            "!alarmActive evaluates to false because alarmActive is true. true && false is false, so the else branch executes.",
            "Notice the negation operator '!' in front of alarmActive."
        ));

        add(new Question(22, "Conditionals", 2,
            "Which condition correctly checks if a coordinate (x, y) is inside a grid of width W and height H?",
            null,
            new String[]{
                "x >= 0 && x <= W && y >= 0 && y <= H",
                "x >= 0 && x < W && y >= 0 && y < H",
                "x > 0 && x < W && y > 0 && y < H",
                "x == W && y == H"
            },
            1,
            "Grid indices are 0-indexed, meaning valid indices range from 0 up to (length - 1). Therefore: x >= 0 && x < W && y >= 0 && y < H.",
            "Indices start at 0 and must be strictly less than the dimension."
        ));

        add(new Question(23, "Conditionals", 3,
            "What is the output of this nested condition?",
            "int power = 80;\nboolean override = false;\nif (power > 50) {\n    if (override) {\n        System.out.println(\"LEVEL 3\");\n    } else {\n        System.out.println(\"LEVEL 2\");\n    }\n} else {\n    System.out.println(\"LEVEL 1\");\n}",
            new String[]{
                "LEVEL 3",
                "LEVEL 2",
                "LEVEL 1",
                "LEVEL 2 and LEVEL 1"
            },
            1,
            "power > 50 is true (80 > 50), entering the outer if. Inside, override is false, so the else block triggers, printing 'LEVEL 2'.",
            "Trace the outer if first, then the inner if-else."
        ));

        add(new Question(24, "Conditionals", 2,
            "How do you properly compare the contents of two String variables in Java?",
            null,
            new String[]{
                "str1 == str2",
                "str1.equals(str2)",
                "str1 = str2",
                "str1.compare(str2)"
            },
            1,
            "'==' checks reference identity (whether both refer to the exact same memory address). '.equals()' compares the actual characters of the strings.",
            "Always use .equals() for string content comparison."
        ));

        // --- CATEGORY 5: LOOPS & ITERATION (Difficulty 2-3) ---
        add(new Question(25, "Loops & Iteration", 2,
            "How many times will this for-loop execute?",
            "for (int i = 0; i < 5; i++) {\n    // robot step\n}",
            new String[]{
                "4",
                "5",
                "6",
                "Infinite"
            },
            1,
            "The loop runs for i = 0, 1, 2, 3, 4. When i increments to 5, i < 5 becomes false. Total iterations = 5.",
            "Count from 0 up to 4 inclusive."
        ));

        add(new Question(26, "Loops & Iteration", 2,
            "What will be the final value of sum after the loop finishes?",
            "int sum = 0;\nfor (int i = 1; i <= 4; i++) {\n    sum += i;\n}",
            new String[]{
                "10",
                "4",
                "6",
                "15"
            },
            0,
            "sum adds 1 + 2 + 3 + 4 = 10.",
            "1 + 2 = 3; 3 + 3 = 6; 6 + 4 = 10."
        ));

        add(new Question(27, "Loops & Iteration", 3,
            "What does the 'break' statement do inside a loop?",
            null,
            new String[]{
                "Skips to the next iteration of the loop",
                "Immediately terminates the loop and resumes after it",
                "Restarts the loop from index 0",
                "Pauses execution for 1000 milliseconds"
            },
            1,
            "'break' immediately exits the innermost enclosing loop or switch. 'continue' skips to the next iteration.",
            "Break breaks out completely; continue continues with the next pass."
        ));

        add(new Question(28, "Loops & Iteration", 3,
            "What does this while loop output?",
            "int count = 1;\nwhile (count < 8) {\n    count *= 2;\n}\nSystem.out.println(count);",
            new String[]{
                "4",
                "7",
                "8",
                "16"
            },
            2,
            "Iteration 1: count becomes 2 (2 < 8: loop continues). Iteration 2: count becomes 4 (4 < 8: loop continues). Iteration 3: count becomes 8 (8 < 8: false). Loop terminates. count is 8.",
            "Powers of 2: 1 -> 2 -> 4 -> 8. At 8, the condition fails."
        ));

        add(new Question(29, "Loops & Iteration", 3,
            "What is the key characteristic of a 'do-while' loop compared to a 'while' loop?",
            null,
            new String[]{
                "It runs faster in the JVM",
                "Its body is guaranteed to execute at least once before testing the condition",
                "It does not require braces",
                "It cannot contain break statements"
            },
            1,
            "A do-while loop evaluates its condition at the bottom (post-test), so the body always executes at least once.",
            "The condition is checked after each pass, not before the first pass."
        ));

        add(new Question(30, "Loops & Iteration", 3,
            "What is the total number of print operations in this nested loop?",
            "for (int r = 0; r < 3; r++) {\n    for (int c = 0; c < 4; c++) {\n        System.out.print(\"*\");\n    }\n}",
            new String[]{
                "7",
                "12",
                "9",
                "16"
            },
            1,
            "Outer loop runs 3 times; for each outer iteration, inner loop runs 4 times. Total = 3 * 4 = 12.",
            "Multiply the outer count by the inner count."
        ));

        add(new Question(31, "Loops & Iteration", 3,
            "What will this loop with a 'continue' statement print?",
            "for (int i = 0; i < 4; i++) {\n    if (i == 2) continue;\n    System.out.print(i + \" \");\n}",
            new String[]{
                "0 1 2 3",
                "0 1 3",
                "0 1",
                "2"
            },
            1,
            "When i == 2, 'continue' skips the rest of the loop body (the print statement) and proceeds to i = 3.",
            "Notice that iteration 2 is skipped."
        ));

        add(new Question(32, "Loops & Iteration", 3,
            "Which of the following causes an infinite loop?",
            null,
            new String[]{
                "for (int i = 10; i > 0; i--)",
                "int x = 0; while (x < 5) { x++; }",
                "for (int i = 0; i >= 0; i++)",
                "int i = 0; do { i++; } while (i < 3);"
            },
            2,
            "for (int i = 0; i >= 0; i++) starts at 0 and increments continuously, staying >= 0 indefinitely (until overflow occurs at Integer.MAX_VALUE).",
            "A loop where the increment moves in the same direction as the condition never terminates normally."
        ));

        // --- CATEGORY 6: ARRAYS & 2D GRIDS (Difficulty 2-3) ---
        add(new Question(33, "Arrays & Grids", 2,
            "What is the index of the first element in a Java array?",
            null,
            new String[]{
                "-1",
                "0",
                "1",
                "null"
            },
            1,
            "Java arrays are zero-indexed, meaning the first element is located at index 0.",
            "Indexing begins at zero in Java, C, Python, and C++."
        ));

        add(new Question(34, "Arrays & Grids", 2,
            "Given: int[] scores = {10, 20, 30, 40}; what expression returns the number of elements?",
            null,
            new String[]{
                "scores.length()",
                "scores.length",
                "scores.size()",
                "scores.count"
            },
            1,
            "In Java, an array has a public final field '.length' (without parentheses). Strings use '.length()', Collections use '.size()'.",
            "Arrays have a .length field, not a method."
        ));

        add(new Question(35, "Arrays & Grids", 3,
            "What exception is thrown when accessing index 5 of an array with 4 elements?",
            null,
            new String[]{
                "NullPointerException",
                "ArrayIndexOutOfBoundsException",
                "IllegalArgumentException",
                "ClassCastException"
            },
            1,
            "Accessing an index >= array.length or < 0 results in an ArrayIndexOutOfBoundsException at runtime.",
            "The error name describes exactly what went wrong: array index out of bounds."
        ));

        add(new Question(36, "Arrays & Grids", 3,
            "In a 2D grid 'char[][] grid', which syntax accesses the cell at row r and column c?",
            null,
            new String[]{
                "grid[r, c]",
                "grid[r][c]",
                "grid(r)(c)",
                "grid.get(r, c)"
            },
            1,
            "Java uses double square brackets 'grid[r][c]' to index into multi-dimensional arrays.",
            "Each dimension uses its own pair of brackets."
        ));

        add(new Question(37, "Arrays & Grids", 3,
            "What is the output of this array snippet?",
            "int[] data = {5, 10, 15};\ndata[1] = data[0] + data[2];\nSystem.out.println(data[1]);",
            new String[]{
                "10",
                "20",
                "15",
                "25"
            },
            1,
            "data[0] is 5 and data[2] is 15. data[0] + data[2] = 20, which is assigned to data[1].",
            "Calculate 5 + 15."
        ));

        add(new Question(38, "Arrays & Grids", 3,
            "Which loop correctly iterates through all elements of an array 'int[] items' using an enhanced for-loop?",
            null,
            new String[]{
                "for (item in items)",
                "for (int item : items)",
                "for (int item -> items)",
                "foreach (int item in items)"
            },
            1,
            "Java's enhanced for-each loop syntax is: for (Type element : collectionOrArray).",
            "Use the colon ':' syntax."
        ));

        // --- CATEGORY 7: METHODS & PARAMETERS (Difficulty 3) ---
        add(new Question(39, "Methods", 2,
            "What return type indicates that a Java method does not return any value?",
            null,
            new String[]{
                "null",
                "empty",
                "void",
                "never"
            },
            2,
            "The 'void' keyword in a method declaration specifies that the method does not return a value to the caller.",
            "It means 'empty of return value'."
        ));

        add(new Question(40, "Methods", 3,
            "How does Java pass primitive variables (e.g. int, double) into methods?",
            null,
            new String[]{
                "Pass-by-reference",
                "Pass-by-value (a copy is passed)",
                "Pass-by-pointer",
                "Pass-by-name"
            },
            1,
            "Java is strictly pass-by-value. For primitives, a copy of the actual value is passed; modifying the parameter inside the method does not alter the caller's variable.",
            "Java always passes a copy of the argument's bits."
        ));

        add(new Question(41, "Methods", 3,
            "What is method overloading in Java?",
            null,
            new String[]{
                "Defining two methods with the same name and identical parameter lists",
                "Defining methods with the same name but different parameter lists within the same class",
                "Overriding a method in a subclass",
                "Calling a method too many times causing a stack overflow"
            },
            1,
            "Method overloading allows multiple methods to share the same name provided their parameter lists (types, count, or order) differ.",
            "Same name, different parameters."
        ));

        add(new Question(42, "Methods", 3,
            "What will this method call return?",
            "public int compute(int a, int b) {\n    return a * 2 + b;\n}\n// Called with: compute(4, 3);",
            new String[]{
                "11",
                "14",
                "20",
                "7"
            },
            0,
            "compute(4, 3) evaluates: 4 * 2 = 8; 8 + 3 = 11.",
            "Multiply 4 by 2 first, then add 3."
        ));

        add(new Question(43, "Methods", 3,
            "Can a private method in a class be called directly from outside that class?",
            null,
            new String[]{
                "Yes, any method can be called from outside",
                "No, private methods are accessible only within the class where they are declared",
                "Yes, but only if the caller is in the same package",
                "Yes, by using the import keyword"
            },
            1,
            "The 'private' access modifier restricts visibility strictly to the enclosing class.",
            "Private encapsulation hides internal methods from other classes."
        ));

        add(new Question(44, "Methods", 3,
            "What does the 'return' statement do inside a void method?",
            null,
            new String[]{
                "It is illegal and causes a compiler error",
                "It immediately exits the method and returns control to the caller",
                "It returns 0 by default",
                "It restarts the method"
            },
            1,
            "A plain 'return;' statement without a value can be used in a void method for early exit.",
            "You can return early from void methods without supplying an expression."
        ));

        // --- CATEGORY 8: OBJECT-ORIENTED PROGRAMMING (Difficulty 3-4) ---
        add(new Question(45, "Object-Oriented Programming", 3,
            "What is the purpose of a constructor in a Java class?",
            null,
            new String[]{
                "To destroy an object when memory is full",
                "To initialize the state of a newly created object",
                "To convert bytecode into native machine code",
                "To import packages from the standard library"
            },
            1,
            "A constructor has the same name as the class, has no return type, and is invoked via 'new' to initialize a new instance.",
            "Constructors construct and initialize new instances."
        ));

        add(new Question(46, "Object-Oriented Programming", 3,
            "What does the 'this' keyword refer to inside an instance method?",
            null,
            new String[]{
                "The parent superclass",
                "The current object instance on which the method is called",
                "The static class definition",
                "The previous method in the call stack"
            },
            1,
            "'this' is a reference to the current object instance, often used to disambiguate field names from parameter names (e.g., this.x = x).",
            "It points to 'this' instance right here."
        ));

        add(new Question(47, "Object-Oriented Programming", 3,
            "Which OOP principle hides internal representation and exposes access only via methods?",
            null,
            new String[]{
                "Encapsulation",
                "Polymorphism",
                "Inheritance",
                "Compilation"
            },
            0,
            "Encapsulation bundles data with the methods that operate on it and restricts direct access using private fields and public getters/setters.",
            "Think of a capsule shielding internal data."
        ));

        add(new Question(48, "Object-Oriented Programming", 4,
            "Which keyword is used by a subclass to extend a parent class in Java?",
            null,
            new String[]{
                "implements",
                "extends",
                "inherits",
                "super"
            },
            1,
            "'extends' is used to inherit from a class. 'implements' is used to implement an interface.",
            "A subclass extends a superclass."
        ));

        add(new Question(49, "Object-Oriented Programming", 4,
            "What happens when you declare a method as 'static'?",
            null,
            new String[]{
                "The method belongs to the class itself rather than to any specific instance",
                "The method cannot be called more than once",
                "The method can access instance fields without an object reference",
                "The method runs in a separate thread"
            },
            0,
            "Static methods belong to the class namespace and can be invoked directly (e.g. Math.sqrt()) without creating an instance.",
            "Static members do not require an instantiated object."
        ));

        add(new Question(50, "Object-Oriented Programming", 4,
            "How does a subclass invoke the constructor of its superclass?",
            null,
            new String[]{
                "this()",
                "super()",
                "parent()",
                "base()"
            },
            1,
            "'super()' invokes the immediate superclass constructor and must be the first statement in the subclass constructor.",
            "Look for the keyword that refers to the superclass."
        ));

        // --- CATEGORY 9: DEBUGGING & COMMON ERRORS (Difficulty 3-4) ---
        add(new Question(51, "Debugging", 3,
            "What error occurs when calling a method on a variable that holds 'null'?",
            null,
            new String[]{
                "NullPointerException (NPE)",
                "StackOverflowError",
                "ClassCastException",
                "NumberFormatException"
            },
            0,
            "Attempting to dereference a null reference (invoke a method or access a field) throws a NullPointerException.",
            "It occurs when a reference points to nothing (null)."
        ));

        add(new Question(52, "Debugging", 3,
            "Identify the bug in this array search method:",
            "int[] arr = {1, 2, 3};\nfor (int i = 0; i <= arr.length; i++) {\n    System.out.println(arr[i]);\n}",
            new String[]{
                "Array cannot be initialized with braces",
                "Off-by-one error: 'i <= arr.length' will throw ArrayIndexOutOfBoundsException on last pass",
                "System.out.println cannot print ints",
                "The loop will never execute"
            },
            1,
            "Valid indices are 0 to (arr.length - 1). Using '<=' allows i to reach arr.length, triggering an out-of-bounds exception.",
            "Check the loop condition comparison operator: should be <, not <=."
        ));

        add(new Question(53, "Debugging", 4,
            "Why is comparing two floating point numbers using '==' often unsafe?",
            null,
            new String[]{
                "Java does not support '==' on doubles",
                "Binary representation rounding errors can make 0.1 + 0.2 != 0.3",
                "It always throws an ArithmeticException",
                "Float comparisons always return false"
            },
            1,
            "Floating-point numbers in IEEE 754 format cannot represent all decimal fractions exactly, leading to minute rounding differences.",
            "Compare doubles using Math.abs(a - b) < EPSILON."
        ));

        add(new Question(54, "Debugging", 3,
            "What causes a StackOverflowError in Java?",
            null,
            new String[]{
                "Creating too many large arrays in heap memory",
                "Unbounded or excessively deep recursive method calls",
                "Opening too many files without closing them",
                "Declaring too many static variables"
            },
            1,
            "Each method call allocates a stack frame. Infinite recursion consumes all call stack memory, triggering a StackOverflowError.",
            "Think of recursive calls piling up on the execution stack."
        ));

        add(new Question(55, "Debugging", 4,
            "What is the bug in this door unlocking logic?",
            "boolean hasRedKey = false;\nboolean hasBlueKey = true;\nif (hasRedKey = true) {\n    unlockRedDoor();\n}",
            new String[]{
                "unlockRedDoor() cannot be called inside an if statement",
                "Assignment operator '=' was used instead of comparison operator '=='",
                "hasRedKey cannot be reassigned",
                "The code fails to compile because booleans cannot be assigned"
            },
            1,
            "'hasRedKey = true' assigns true to hasRedKey, and the expression itself evaluates to true, inadvertently unlocking the door!",
            "Look closely: single '=' is assignment; double '==' is equality check."
        ));

        add(new Question(56, "Debugging", 3,
            "Which block is guaranteed to execute whether an exception is thrown or caught?",
            null,
            new String[]{
                "try",
                "catch",
                "finally",
                "throw"
            },
            2,
            "The 'finally' block always executes upon exiting a try-catch block, making it ideal for cleaning up resources.",
            "It runs 'finally' after try and catch."
        ));

        // --- CATEGORY 10: ALGORITHMS & ROUTE PLANNING (Difficulty 3-4) ---
        add(new Question(57, "Algorithms & Logic", 3,
            "Which algorithm is best suited to find the shortest path in an unweighted grid maze?",
            null,
            new String[]{
                "Breadth-First Search (BFS)",
                "Depth-First Search (DFS)",
                "Bubble Sort",
                "Binary Search"
            },
            0,
            "BFS explores nodes in waves of increasing distance from the start, guaranteeing the shortest path in unweighted grids.",
            "Breadth explores level by level."
        ));

        add(new Question(58, "Algorithms & Logic", 3,
            "What is the time complexity of searching for an element in an unsorted array of size N using Linear Search?",
            null,
            new String[]{
                "O(1)",
                "O(log N)",
                "O(N)",
                "O(N^2)"
            },
            2,
            "Linear search inspects each element one by one from start to finish, requiring at most N comparisons in the worst case: O(N).",
            "In the worst case, you must check all N elements."
        ));

        add(new Question(59, "Algorithms & Logic", 3,
            "How do you correctly swap two integer variables 'a' and 'b' using a temporary variable?",
            null,
            new String[]{
                "a = b; b = a;",
                "int temp = a; a = b; b = temp;",
                "b = a; a = b;",
                "int temp = b; a = temp; b = a;"
            },
            1,
            "Storing 'a' in 'temp' preserves its value before 'a' is overwritten with 'b'. Then 'b' receives 'temp'.",
            "If you overwrite 'a' before saving it, its original value is lost."
        ));

        add(new Question(60, "Algorithms & Logic", 4,
            "In a grid where you can only move Up, Down, Left, and Right, what distance formula is used?",
            null,
            new String[]{
                "Euclidean distance: sqrt(dx^2 + dy^2)",
                "Manhattan distance: |x1 - x2| + |y1 - y2|",
                "Chebyshev distance: max(|dx|, |dy|)",
                "Hamming distance"
            },
            1,
            "Manhattan (taxicab) distance measures grid travel along perpendicular axes without diagonal movement: |x1 - x2| + |y1 - y2|.",
            "Think of navigating city street blocks."
        ));

        add(new Question(61, "Algorithms & Logic", 4,
            "What prerequisite must be met before performing a Binary Search on an array?",
            null,
            new String[]{
                "The array size must be a power of 2",
                "The elements in the array must be sorted",
                "The array must not contain duplicate values",
                "The array must be stored as a linked list"
            },
            1,
            "Binary search repeatedly halves the search space based on whether the target is greater or less than the middle element, requiring sorted data.",
            "You cannot determine which half to discard if the array is unsorted."
        ));

        add(new Question(62, "Algorithms & Logic", 4,
            "Which data structure follows the First-In, First-Out (FIFO) principle used in BFS pathfinding?",
            null,
            new String[]{
                "Stack",
                "Queue",
                "Tree",
                "HashMap"
            },
            1,
            "A Queue operates on FIFO (First-In, First-Out). Stacks operate on LIFO (Last-In, First-Out).",
            "Like people waiting in a line (queue)."
        ));

        // --- CATEGORY 11: ADVANCED JAVA & COLLECTIONS (Difficulty 3-4) ---
        add(new Question(63, "Advanced Java", 3,
            "Why are Java Strings considered immutable?",
            null,
            new String[]{
                "Once created, the character contents of a String object cannot be modified",
                "Strings cannot be passed as method arguments",
                "Strings can only hold alphanumeric characters",
                "Strings cannot be compared using .equals()"
            },
            0,
            "String objects cannot be changed after creation. Any method like replace() or toUpperCase() creates and returns a brand-new String.",
            "Immutable means 'unchangeable'."
        ));

        add(new Question(64, "Advanced Java", 4,
            "Which interface in java.util provides a dynamically resizing array?",
            null,
            new String[]{
                "java.util.List (implemented by ArrayList)",
                "java.util.Set",
                "java.util.Map",
                "java.util.Queue"
            },
            0,
            "ArrayList implements the List interface, offering fast random access and dynamic automatic resizing.",
            "Standard indexed, order-preserving dynamic collection."
        ));
    }

    private static void add(Question q) {
        ALL_QUESTIONS.add(q);
    }

    public static List<Question> getAllQuestions() {
        return Collections.unmodifiableList(ALL_QUESTIONS);
    }

    public static int getCount() {
        return ALL_QUESTIONS.size();
    }

    public static Question getById(int id) {
        for (Question q : ALL_QUESTIONS) {
            if (q.getId() == id) return q;
        }
        return ALL_QUESTIONS.get(0);
    }

    /**
     * Gets a list of questions appropriate for a given level (1 to 12).
     */
    public static List<Question> getQuestionsForLevel(int level) {
        List<Question> result = new ArrayList<>();
        // Target difficulty and topics based on level
        int targetDiff = 1 + (level - 1) / 3; // level 1-3: diff 1-2; 4-6: diff 2-3; 7-9: diff 3-4; 10-12: diff 3-5
        for (Question q : ALL_QUESTIONS) {
            if (Math.abs(q.getDifficulty() - targetDiff) <= 1) {
                result.add(q);
            }
        }
        if (result.isEmpty()) {
            return ALL_QUESTIONS;
        }
        return result;
    }
}
