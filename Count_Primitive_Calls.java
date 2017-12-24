import components.statement.Statement;
import components.statement.StatementKernel;

/**
 * Utility class with method to count the number of calls to primitive
 * instructions (move, turnleft, turnright, infect, skip) in a given
 * {@code Statement}.
 *
 * @author Henrique Painhas
 *
 */
public final class CountPrimitiveCalls {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private CountPrimitiveCalls() {
    }

    /**
     * Reports the number of calls to primitive instructions (move, turnleft,
     * turnright, infect, skip) in a given {@code Statement}.
     *
     * @param s
     *            the {@code Statement}
     * @return the number of calls to primitive instructions in {@code s}
     * @ensures <pre>
     * countOfPrimitiveCalls =
     *  [number of calls to primitive instructions in s]
     * </pre>
     */
    public static int countOfPrimitiveCalls(Statement s) {
        int count = 0;
        switch (s.kind()) {
            case BLOCK: {
                /*
                 * Add up the number of calls to primitive instructions in each
                 * nested statement in the BLOCK.
                 */

                int blockLength = s.lengthOfBlock();

                for (int i = 0; i < blockLength; i++) {
                    Statement child = s.removeFromBlock(i);
                    count += countOfPrimitiveCalls(child);
                    s.addToBlock(i, child);
                }

                break;
            }
            case IF: {
                /*
                 * Find the number of calls to primitive instructions in the
                 * body of the IF.
                 */

                Statement block = s.newInstance();
                StatementKernel.Condition condition = s.disassembleIf(block);
                count = countOfPrimitiveCalls(block);
                s.assembleIf(condition, block);

                break;
            }
            case IF_ELSE: {
                /*
                 * Add up the number of calls to primitive instructions in the
                 * "then" and "else" bodies of the IF_ELSE.
                 */

                Statement block1 = s.newInstance();
                Statement block2 = s.newInstance();
                StatementKernel.Condition condition = s
                        .disassembleIfElse(block1, block2);
                count = countOfPrimitiveCalls(block1)
                        + countOfPrimitiveCalls(block2);
                s.assembleIfElse(condition, block1, block2);

                break;
            }
            case WHILE: {
                /*
                 * Find the number of calls to primitive instructions in the
                 * body of the WHILE.
                 */

                Statement block = s.newInstance();
                StatementKernel.Condition condition = s.disassembleWhile(block);
                count = countOfPrimitiveCalls(block);
                s.assembleWhile(condition, block);

                break;
            }
            case CALL: {
                /*
                 * This is a leaf: the count can only be 1 or 0. Determine
                 * whether this is a call to a primitive instruction or not.
                 */

                String call = s.disassembleCall();
                if (call.equals("move") || call.equals("turnleft")
                        || call.equals("turnright") || call.equals("infect")
                        || call.equals("skip")) {

                    count++;
                }
                s.assembleCall(call);

                break;
            }
            default: {
                // this will never happen...can you explain why?
                // Because there is no more kind of statement
                break;
            }
        }
        return count;
    }

}
