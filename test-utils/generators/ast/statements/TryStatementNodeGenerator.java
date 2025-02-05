package generators.ast.statements;

import parser.nodes.components.BlockNode;
import parser.nodes.statements.TryStatementNode;
import parser.nodes.statements.CatchNode;

import java.util.List;

public class TryStatementNodeGenerator {
    private BlockNode tryBranch;
    private List<CatchNode> exceptionBranches;

    public static TryStatementNodeGenerator builder() {
        return new TryStatementNodeGenerator();
    }

    public TryStatementNodeGenerator tryBranch(BlockNode tryBranch) {
        this.tryBranch = tryBranch;
        return this;
    }

    public TryStatementNodeGenerator exceptionBranches(List<CatchNode> exceptionBranches) {
        this.exceptionBranches = exceptionBranches;
        return this;
    }

    public TryStatementNode build() {
        return new TryStatementNode(tryBranch, exceptionBranches);
    }
}
