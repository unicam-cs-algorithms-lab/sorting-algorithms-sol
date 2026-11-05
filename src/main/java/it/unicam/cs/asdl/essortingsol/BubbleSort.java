package it.unicam.cs.asdl.essortingsol;

import java.util.List;

/**
 * Implementazione del BubbleSort con terminazione anticipata: se una passata
 * non effettua alcuno scambio, la lista è già ordinata.
 *
 * <p>La versione corrisponde alla seconda variante dell'algoritmo presentata
 * nelle slide teoriche sull'ordinamento (L10, pagina 19). Il numero di
 * confronti è lineare nel caso migliore e quadratico nel caso peggiore.
 *
 * @param <E> tipo di elemento, ordinabile mediante {@link Comparable}
 * @author Luca Tesei
 */
public class BubbleSort<E extends Comparable<E>>
        implements SortingAlgorithm<E> {

    /**
     * Ordina in-place la lista secondo l'ordine naturale.
     *
     * <p>Ad ogni passata confronta coppie adiacenti nella porzione ancora da
     * ordinare; se non avvengono scambi, interrompe immediatamente il lavoro.
     * Il conteggio include esclusivamente i confronti tra elementi eseguiti
     * durante l'ordinamento.
     *
     * @param l lista da ordinare, non nulla
     * @return risultato con la stessa lista ordinata e il numero di confronti
     * @throws NullPointerException se la lista è nulla
     */
    @Override
    public SortingAlgorithmResult<E> sort(List<E> l) {
        if (l == null)
            throw new NullPointerException("Tentativo di ordinare una lista null");

        int countCompare = 0;
        for (int i = l.size() - 1; i > 0; i--) {
            boolean scambiEffettuati = false;
            for (int j = 0; j < i; j++) {
                countCompare++;
                if (l.get(j).compareTo(l.get(j + 1)) > 0) {
                    E appoggio = l.get(j);
                    l.set(j, l.get(j + 1));
                    l.set(j + 1, appoggio);
                    scambiEffettuati = true;
                }
            }
            // Nessuno scambio significa che anche la porzione residua è ordinata.
            if (!scambiEffettuati)
                break;
        }
        return new SortingAlgorithmResult<E>(l, countCompare);
    }

    @Override
    public String getName() {
        return "BubbleSort";
    }
}
