package flow;

import java.lang.invoke.*;

public class LambdaMetaFactory {
    public static CallSite metaFactory(
        MethodHandles.Lookup caller,
        java.lang.String invokedName,
        MethodType invokedType,
        MethodType samMethodType,
        MethodHandle implMethod,
        MethodType instantiatedMethodType
    ) throws LambdaConversionException {
        return LambdaMetafactory.metafactory(
            caller,
            invokedName,
            invokedType,
            samMethodType,
            implMethod,
            instantiatedMethodType
        );
    }
}