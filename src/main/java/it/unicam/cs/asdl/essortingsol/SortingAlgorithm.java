package it.unicam.cs.asdl.essortingsol;

import java.util.List;

/**
 * Contratto comune degli algoritmi di ordinamento per confronto.
 * <p>Il framework sperimentale puo' trattare uniformemente algoritmi diversi
 * grazie al polimorfismo: ogni implementazione ordina una lista e restituisce
 * un risultato che contiene anche il numero di confronti effettuati.
 *
 * @param <E> tipo degli elementi, dotati di ordinamento naturale
 */
public interface SortingAlgorithm<E extends Comparable<E>> {

    /**
     * Ordina una lista di elementi in accordo all'ordinamento totale naturale
     * definito nella classe degli elementi. Non sono ammessi elementi null
     * nella lista da ordinare.
     * 
     * @param l
     *              la lista da ordinare (dovrebbe essere una ArrayList)
     * @return un oggetto contentente la lista ordinata e il numero di
     *         operazioni di comparazione effettuate dall'algoritmo.
     * @throws NullPointerException
     *                                  se la lista l è null oppure se qualche
     *                                  elemento della lista è null
     */
    public SortingAlgorithmResult<E> sort(List<E> l);

    /**
     * Restituisce il nome dell'algoritmo di ordinamento.
     * 
     * @return il nome dell'algoritmo
     */
    public String getName();

}