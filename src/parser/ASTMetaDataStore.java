package parser;

import parser.nodes.ASTNode;

import java.util.IdentityHashMap;
import java.util.Map;

public final class ASTMetaDataStore {
    private static final ASTMetaDataStore INSTANCE = new ASTMetaDataStore();
    private final Map<ASTNode, ASTMetaData> metadataMap = new IdentityHashMap<>();

    private ASTMetaDataStore() {}

    public static ASTMetaDataStore getInstance() {
        return INSTANCE;
    }

    public ASTNode addMetadata(ASTNode node, int line, String file) {
        metadataMap.put(node, new ASTMetaData(line, file));
        return node;
    }

    public ASTMetaData getMetadata(ASTNode node) {
        return metadataMap.get(node);
    }

    public int getLine(ASTNode node) {
        return metadataMap.containsKey(node) ? metadataMap.get(node).line() : -1;
    }

    public String getFile(ASTNode node) {
        return metadataMap.containsKey(node) ? metadataMap.get(node).file() : null;
    }
}
