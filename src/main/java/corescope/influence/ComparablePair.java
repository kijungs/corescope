/* =================================================================================
 *
 * CoreScope: Graph Mining Using k-Core Analysis - Patterns, Anomalies, and Algorithms
 * Authors: Kijung Shin, Tina Eliassi-Rad, and Christos Faloutsos
 *
 * Version: 1.0
 * Date: May 24, 2016
 * Main Contact: Kijung Shin (kijungs@cs.cmu.edu)
 *
 * This software is free of charge under research purposes.
 * For commercial purposes, please contact the author.
 *
 * =================================================================================
 */

package corescope.influence;

/**
 * Pair with a comparable value type
 * @author Kijung Shin
 * @param <K>   key
 * @param <V>   value
 */
public class ComparablePair<K, V extends Comparable<V>> implements Comparable<ComparablePair<K,V>>{
    private final K key;
    private final V value;

    public ComparablePair(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(!(o instanceof ComparablePair)) {
            return false;
        } else {
            boolean var10000;
            label43: {
                label29: {
                    ComparablePair oP = (ComparablePair)o;
                    if(this.key == null) {
                        if(oP.key != null) {
                            break label29;
                        }
                    } else if(!this.key.equals(oP.key)) {
                        break label29;
                    }

                    if(this.value == null) {
                        if(oP.value == null) {
                            break label43;
                        }
                    } else if(this.value.equals(oP.value)) {
                        break label43;
                    }
                }

                var10000 = false;
                return var10000;
            }

            var10000 = true;
            return var10000;
        }
    }

    public int hashCode() {
        int result = this.key == null?0:this.key.hashCode();
        int h = this.value == null?0:this.value.hashCode();
        result = 37 * result + h ^ h >>> 16;
        return result;
    }

    public int compareTo(ComparablePair<K, V> o) {
        return value.compareTo(o.value);
    }
}
