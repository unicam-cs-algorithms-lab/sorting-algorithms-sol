package it.unicam.cs.asdl.essortingsol;

import java.util.List;

/**
 * Risultato di un algoritmo di ordinamento. Contiene la sequenza ordinata di
 * elementi e il numero di operazioni di confronto effettuate.
 * 
 * @author Luca Tesei
 *
 * @param <E>
 *                Tipo degli elementi della sequenza ordinata.
 */
public class SortingAlgorithmResult<E extends Comparable<E>> {

    private List<E> l;

    private int countCompare;

    /**
     * Costruisce il risultato di una esecuzione di ordinamento. La lista viene conservata per riferimento: non e' una copia difensiva.
     * 
     * @param l
     *                         una lista ordinata in maniera crescente
     * @param countCompare
     *                         numero di operazioni di confronto effettuate
     *                         durante l'ordinamento
     */
    public SortingAlgorithmResult(List<E> l, int countCompare) {
        this.l = l;
        this.countCompare = countCompare;
    }

    /**
     * Restituisce la lista associata al risultato. Il riferimento e' condiviso con il chiamante: modificare la lista modifica anche il risultato osservabile.
     * 
     * @return la lista ordinata
     */
    public List<E> getL() {
        return l;
    }

    /**
     * Restituisce il numero di chiamate a compareTo conteggiate dall'algoritmo. Questa misura non coincide necessariamente con il tempo di esecuzione.
     * 
     * @return il numero di confronti effettuati.
     */
    public int getCountCompare() {
        return countCompare;
    }

    /**
     * Controlla l'ordinamento non decrescente tramite confronti tra elementi consecutivi; non verifica che l'output sia una permutazione dell'input.
     * 
     * @return true se la lista è ordinata in maniera crescente, false
     *         altrimenti
     */
    public boolean checkOrder() {
        for (int i = 0; i < l.size() - 1; i++)
            if (this.l.get(i).compareTo(this.l.get(i+1)) > 0)
                return false;
        return true;
    }

}
