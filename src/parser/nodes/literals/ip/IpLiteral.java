package parser.nodes.literals.ip;

import parser.nodes.literals.LiteralNode;

public abstract class IpLiteral implements LiteralNode {
    @Override
    public String getClassName() {
        return "Ip";
    }
}
