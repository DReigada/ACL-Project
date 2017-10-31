package variables;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

final public class VarMap {
  final static BiMap<Variable, Integer> map = HashBiMap.create();

  public static Variable getById(int id) {
    return map.inverse().get(id).unNegated();
  }
}