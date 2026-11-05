package it.unicam.cs.asdl.essortingsol;

import java.util.List;

/**
 * HeapSort in-place su una List con accesso indicizzato e set.
 * La parte iniziale è un max-heap di dimensione heapSize; la parte finale
 * contiene gli elementi già collocati nella posizione definitiva.
 * La costruzione iniziale del max-heap è bottom-up e richiede O(n):
 * heapify può costare O(log n) alla radice, ma i molti nodi vicini alle
 * foglie hanno altezza piccola. La successiva fase di ordinamento richiede
 * O(n log n). Il conteggio dei confronti riguarda le chiamate a compareTo.
 * 
 * @author Luca Tesei
 *
 * @param <E> tipo degli elementi, confrontabili secondo l’ordinamento naturale
 */
public class HeapSort<E extends Comparable<E>> implements SortingAlgorithm<E> {

    /**
     * Crea un algoritmo HeapSort privo di stato relativo alle sequenze da ordinare.
     * Ogni invocazione di {@link #sort(List)} opera sulla lista ricevuta.
     */
    public HeapSort() {
        // Nessuno stato da inizializzare: la sequenza è fornita a sort().
    }


    // Dimensione della porzione ancora trattata come heap, non della lista intera.
    private int heapSize;

    private int numCompare;

    @Override
    public SortingAlgorithmResult<E> sort(List<E> l) {
        // usa una variante dei metodi della classe
        // MaxHeap in modo da implementare l'algoritmo utilizzando solo un array
        // (arraylist) e alcune variabili locali di appoggio (implementazione
        // cosiddetta "in loco" o "in place", si veda
        // https://it.wikipedia.org/wiki/Algoritmo_in_loco)
        if (l == null)
            throw new NullPointerException(
                    "Tentativo di ordinare una lista null");
        if (l.size() <= 1)
            // per ordinare la lista vuota o con un solo elemento non faccio niente
            return new SortingAlgorithmResult<E>(l, 0);
        // inizializzo heapSize
        this.heapSize = l.size();
        // inizilizzo numCompare
        this.numCompare = 0;
        // Build-Max-Heap bottom-up: a differenza del costruttore di MaxHeap,
        // non inseriamo gli elementi uno alla volta. Partiamo dall'ultimo
        // nodo interno e ripristiniamo la proprietà di heap risalendo verso
        // la radice. Il costo totale è O(n), perché i nodi che possono
        // richiedere discese lunghe sono pochi.
        // il primo nodo che ha almeno un figlio si trova in posizione (l.size()
        // / 2) - 1 dove / è la divisione intera
        for (int i = (l.size() / 2) - 1; i >= 0; i--) {
            heapify(l, i); // heapify fa riferimento alla lunghezza heapSize,
                           // non alla lunghezza effettiva di l
        }
        // Ora l è uno heap
        for (int i = l.size() - 1; i > 0; i--) {
            // il max è sempre in testa a l
            // scambio la testa con l'elemento i
            E app = l.get(i);
            l.set(i, l.get(0));
            l.set(0, app);
            // decremento la heapSize
            this.heapSize--;
            // chiamo heapify
            heapify(l, 0);
        }
        // ora l è ordinata
        return new SortingAlgorithmResult<E>(l, this.numCompare);
    }

    private void heapify(List<E> l, int i) {
        if (!hasLeft(i))
            return;
        int max = i;
        if (l.get(max).compareTo(l.get(leftIndex(i))) < 0)
            max = leftIndex(i);
        this.numCompare++;
        if (hasRight(i)) {
            this.numCompare++;
            if (l.get(max).compareTo(l.get(rightIndex(i))) < 0) {
                max = rightIndex(i);
            }
        }
        if (max == i)
            return; // ho finito
        // scambio i con max e richiamo la funzione ricorsivamente sull'indice
        // del nodo figlio uguale a max
        E app = l.get(i);
        l.set(i, l.get(max));
        l.set(max, app);
        heapify(l, max);

    }

    @Override
    public String getName() {
        return "HeapSort";
    }

    /*
     * Funzione di comodo per calcolare l'indice del figlio sinistro del nodo in
     * posizione i. Si noti che la posizione 0 è significativa e contiene sempre
     * la radice dello heap.
     */
    private int leftIndex(int i) {
        return 2 * i + 1;
    }

    /*
     * Funzione di comodo per calcolare l'indice del figlio destro del nodo in
     * posizione i. Si noti che la posizione 0 è significativa e contiene sempre
     * la radice dello heap.
     */
    private int rightIndex(int i) {
        return 2 * i + 2;
    }

    private boolean hasRight(int i) {
        return rightIndex(i) < this.heapSize;
    }

    private boolean hasLeft(int i) {
        return leftIndex(i) < this.heapSize;
    }

}
