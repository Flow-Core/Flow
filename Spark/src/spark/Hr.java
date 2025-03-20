package spark;

import flow.Consumer1;
import spark.containers.Box;
import spark.containers.Column;
import spark.containers.Row;

/**
 * Hierarchy for building UI
 */
public class Hr {
    /**
     * Creates a container using the supplied factory and applies the builder
     *
     * @param supplier the factory for creating the container
     * @param builder  the lambda to configure the container
     * @param <T>      the type of container
     * @return the configured container
     */
    public static <T extends spark.Container> T container(java.util.function.Supplier<T> supplier, Consumer1<T> builder) {
        T cont = supplier.get();
        builder.invoke(cont);
        return cont;
    }

    /**
     * Creates a Column container and applies the builder
     *
     * @param builder the lambda to configure the column
     * @return the configured Column
     */
    public static Column column(Consumer1<Column> builder) {
        return container(Column::new, builder);
    }

    /**
     * Creates a Row container and applies the builder
     *
     * @param builder the lambda to configure the row
     * @return the configured Row
     */
    public static Row row(Consumer1<Row> builder) {
        return container(Row::new, builder);
    }

    /**
     * Creates a Box container and applies the builder
     *
     * @param builder the lambda to configure the box
     * @return the configured Box
     */
    public static Box box(Consumer1<Box> builder) {
        return container(Box::new, builder);
    }
}
