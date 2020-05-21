package pl.javahello;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converts one type of object into another. Used for mapstruct code generation. Usually handles
 * conversion between dto and entity.
 *
 * @param <S> Source type.
 * @param <R> Target type.
 */
public interface Adapter<S, R> {

    /**
     * Converts {@link S} to {@link R}.
     */
    R map(S source);

  /**
   * Converts list of {@link S}.
   */
  default List<R> list(List<S> sources) {
    return sources == null
           ? List.of()
           : sources.stream().map(this::map).collect(Collectors.toList());
  }

  /**
   * Converts set of {@link S}.
   */
  default Set<R> set(Set<S> sources) {
    return sources == null ? Set.of() : sources.stream().map(this::map).collect(Collectors.toSet());
  }
}
