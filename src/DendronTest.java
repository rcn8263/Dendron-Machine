import dendron.ParseTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Run a test of the dendron language system.
 *
 * @author RIT CS
 */
public class DendronTest {

    private static List< List< String > > programs = Arrays.asList(
            new LinkedList<>( Arrays.asList( "#", "5" ) ),
            new LinkedList<>( Arrays.asList( "#", "_", "5" ) ),
            new LinkedList<>( Arrays.asList( "#", "%", "5" ) ),
            new LinkedList<>( Arrays.asList( "#", "%", "25" ) ),
            new LinkedList<>( Arrays.asList( "#", "+", "5", "25" ) ),
            new LinkedList<>( Arrays.asList( "#", "-", "5", "25" ) ),
            new LinkedList<>( Arrays.asList( "#", "*", "25", "5" ) ),
            new LinkedList<>( Arrays.asList( "#", "/", "25", "5" ) ),
            new LinkedList<>( Arrays.asList( ":=", "x", "55" ) ),
            new LinkedList<>( Arrays.asList(
                    ":=", "able", "77",
                    ":=", "baker", "3",
                    ":=", "charlie", "/", "able", "baker"
            ) ),
            new LinkedList<>( Arrays.asList(
                    ":=", "a", "3",
                    ":=", "b", "4",
                    ":=", "c", "5",
                    ":=", "result", "+", "*", "b", "b", "_", "*", "*", "4", "a",
                    "c"
            ) ),
            new LinkedList<>( Arrays.asList(
                    ":=", "x", "1",
                    ":=", "x", "+", "x", "x",
                    ":=", "x", "*", "+", "x", "x", "x",
                    "#", "x",
                    ":=", "x", "-", "2", "_", "x",
                    ":=", "x", "/", "x", "-2",
                    ":=", "Leicester", "%", "+", "19", "x",
                    "#", "Leicester"
            ) ),
            new LinkedList<>( Arrays.asList(
                    ":=", "a", "1",
                    ":=", "b", "_", "1",
                    ":=", "c", "_", "6",
                    ":=", "root", "/", "+", "_", "b", "%", "-", "*",
                    "b", "b", "*", "*", "4", "a", "c", "*", "2", "a",
                    ":=", "root2", "/", "-", "_", "b", "%", "-", "*",
                    "b", "b", "*", "*", "4", "a", "c", "*", "2", "a"
            ) ),
            // Now for the bad ones...
            new LinkedList<>( Arrays.asList( "#", "/", "5", "0" ) ),
            new LinkedList<>( Arrays.asList( ":=", "42", "+", "5", "0" ) ),
            new LinkedList<>( Arrays.asList( "#", "abracadabra" ) ),
            new LinkedList<>( Arrays.asList( ":=", "x", "9", "+", "7", "9" ) ),
            new LinkedList<>( Arrays.asList( ":=", "x", "9", ":=" ) ),
            new LinkedList<>( Arrays.asList( ":=", "y" ) ),
            new LinkedList<>( Arrays.asList( ":=", "x", "9", "%" ) )
    );

    /*
    🌳 := a 3
    🌳 := b 77
    🌳 # + 20 + a b
    🌳 # / b a
     */

    public static int NUM_TESTS = programs.size();

    /**
     * Run a test on the Dendron programming system
     * @param args if a single number and the number is less than the number of
     *             private stored tests, run the private test corresponding
     *             to that number;<br>
     *             if a single argument that is not a number, assume it is
     *             the name of a directory whose contents are Dendron
     *             programs to be read in and processed;<br>
     *             if two numbers and they are both in the range of the number
     *             of private stored tests, run the private tests corresponding
     *             to that range;<br>
     *             if other args, consider them tokens
     *             of a Dendron program and run tests on that program;<br>
     *             if no arguments, read the source program from standard
     *             input.
     */
    public static void main( String... args ) {
        try ( PrintWriter console = new PrintWriter( System.out ) ){
            switch ( args.length ) {
                case 0 -> {
                    List< String > tokenList = new LinkedList<>();
                    try ( Scanner text = new Scanner( System.in ) ) {
                        System.out.print( "🌳 " );
                        while ( text.hasNextLine() ) {
                            String line = text.nextLine();
                            if ( line.equals( "." ) ) break; // For IntelliJ console
                            tokenList.addAll(
                                    Arrays.asList( line.split( "\\s+" ) ) );
                            System.out.print( "🌳 " );
                        }
                    }
                    runOneTest( tokenList, console );
                }
                case 1 -> {
                    if ( args[ 0 ].matches( "\\d+" ) ) {
                        int testNum = getTestNumber( args[ 0 ] );
                        System.out.println(
                                "TEST #" + testNum + System.lineSeparator()
                        );
                        List< String> tokenList = programs.get( testNum );
                        runOneTest( tokenList, console );
                    }
                    else {
                        //
                        // Assume argument is a directory of tests.
                        //
                        runDirOfTests( args[ 0 ], console );
                        System.exit( 0 );
                    }
                }
                case 2 -> {
                    if ( args[ 0 ].matches( "\\d+" ) &&
                         args[ 1 ].matches( "\\d+" ) ) {
                        int startNum = getTestNumber( args[ 0 ] );
                        int endNum = getTestNumber( args[ 1 ] );
                        List< String > tokenList;
                        System.out.println( "\n_________________________" +
                                            "_________________________" +
                                            "_________________________" );
                        for ( int testNum = startNum;
                              testNum <= endNum; ++testNum ) {
                            System.out.println(
                                    "TEST #" + testNum + System.lineSeparator()
                            );
                            tokenList = programs.get( testNum );
                            runOneTest( tokenList, console );
                            System.out.println( "\n_________________________" +
                                                "_________________________" +
                                                "_________________________" );
                        }
                    }
                }
                default -> {
                    List< String > tokenList =
                            new LinkedList<>( Arrays.asList( args ) );
                    runOneTest( tokenList, console );
                }
            }
        }
    }

    /**
     * Run a single Dendron program. There are three phases.
     * <ol>
     *     <li>Redisplay the program in infix notation.</li>
     *     <li>Interpret the program.</li>
     *     <li>Show the Soros assembly code for this program.</li>
     * </ol>
     * <p>
     * Note that either of the first two phases could fail,
     * which will mean later phases will not run.
     * @param tokenList the string tokens that make up the program
     * @param out where output should go from the interpreting phase
     */
    private static void runOneTest( List< String > tokenList, PrintWriter out )
    {
        ParseTree tree = new ParseTree( tokenList );

        tree.displayProgram();

        tree.interpret();

        tree.compileTo( out );

        out.flush();
    }

    /**
     * Run a whole set of tests from files containing Dendron code.
     * Abort the program if the directory doesn't work out.
     * @param dirName the name of the directory containing the files
     */
    private static void runDirOfTests( String dirName, PrintWriter out ) {
        List< String > tokenList = null;
        String[] dummy = new String[ 0 ];
        File dir = new File( dirName );
        File[] files = dir.listFiles();
        if ( files == null ) {
            System.err.println( "Provided directory " +
                                dirName +
                                " does not exist." );
            System.exit( 1 );
        }
        else {
            for ( File file : files ) {
                System.out.println( "\nTest File " +
                                    file.getName() + ":\n" );
                tokenList = new ArrayList<>();
                try ( Scanner fileIn = new Scanner( file ) ) {
                    fileIn.forEachRemaining( tokenList::add );
                }
                catch( FileNotFoundException fnfe ) {
                    System.err.println( fnfe );
                    continue;
                }
                runOneTest( tokenList, out );
                System.out.println( "\n_________________________" +
                                    "_________________________" +
                                    "_________________________" );
            }
        }
    }

    /**
     * Convert string to test number. If out of bounds, abort.
     * @param testNumStr the test number from the command line
     * @return the test number as an integer
     */
    private static int getTestNumber( String testNumStr ) {
        int testNum;
        testNum = Integer.parseInt( testNumStr );
        if (testNum < 0 || testNum >= NUM_TESTS) {
            System.err.println( "Test number out of range: " + testNumStr );
            System.exit( 2 );
        }
        return testNum;
    }
}
