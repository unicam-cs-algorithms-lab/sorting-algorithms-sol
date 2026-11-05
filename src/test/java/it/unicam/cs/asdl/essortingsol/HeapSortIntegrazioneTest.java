package it.unicam.cs.asdl.essortingsol;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Verifiche aggiuntive per integrare HeapSort nel framework comune.
 * Gli stessi test sono presenti nella versione studenti e nella soluzione.
 */
class HeapSortIntegrazioneTest {

    @Test
    void nomeEContratto() {
        SortingAlgorithm<Integer> algoritmo = new HeapSort<Integer>();
        assertEquals("HeapSort", algoritmo.getName());
        assertThrows(NullPointerException.class, () -> algoritmo.sort(null));
    }

    @Test
    void inputVuotoEUnitario() {
        HeapSort<Integer> algoritmo = new HeapSort<Integer>();
        ArrayList<Integer> vuota = new ArrayList<Integer>();
        SortingAlgorithmResult<Integer> r0 = algoritmo.sort(vuota);
        assertSame(vuota, r0.getL());
        assertEquals(0, r0.getCountCompare());
        ArrayList<Integer> unica = new ArrayList<Integer>(Collections.singletonList(42));
        SortingAlgorithmResult<Integer> r1 = algoritmo.sort(unica);
        assertSame(unica, r1.getL());
        assertEquals(Arrays.asList(42), r1.getL());
        assertEquals(0, r1.getCountCompare());
    }

    @Test
    void ordinamentoInPlaceEDuplicati() {
        ArrayList<Integer> lista = new ArrayList<Integer>(Arrays.asList(5, -2, 5, 0, -2, 9, 9, 1));
        List<Integer> atteso = new ArrayList<Integer>(lista);
        Collections.sort(atteso);
        SortingAlgorithmResult<Integer> risultato = new HeapSort<Integer>().sort(lista);
        assertSame(lista, risultato.getL(), "L'ordinamento deve avvenire in-place");
        assertEquals(atteso, risultato.getL());
        assertTrue(risultato.getCountCompare() >= 0);
    }

    @Test
    void molteSequenzeCasualiEReimpiegoDellaStessaIstanza() {
        HeapSort<Integer> algoritmo = new HeapSort<Integer>();
        Random random = new Random(20261105L);
        for (int n = 0; n <= 150; n++) {
            ArrayList<Integer> lista = new ArrayList<Integer>();
            for (int j = 0; j < n; j++) lista.add(random.nextInt(21) - 10);
            List<Integer> atteso = new ArrayList<Integer>(lista);
            Collections.sort(atteso);
            SortingAlgorithmResult<Integer> risultato = algoritmo.sort(lista);
            assertSame(lista, risultato.getL());
            assertEquals(atteso, risultato.getL(), "Lunghezza " + n);
            assertTrue(risultato.getCountCompare() >= 0);
        }
    }

    @Test
    void contatoreCoerenteConCompareTo() {
        List<Contatore> lista = new ArrayList<Contatore>();
        for (int x : new int[] {7, 2, 9, 0, 7, -1, 3}) lista.add(new Contatore(x));
        Contatore.confronti = 0;
        SortingAlgorithmResult<Contatore> risultato = new HeapSort<Contatore>().sort(lista);
        assertEquals(Contatore.confronti, risultato.getCountCompare(),
                "Il risultato deve contare tutte e sole le chiamate a compareTo");
    }

    private static class Contatore implements Comparable<Contatore> {
        static int confronti = 0;
        final int valore;
        Contatore(int valore) { this.valore = valore; }
        @Override public int compareTo(Contatore altro) {
            confronti++;
            return Integer.compare(valore, altro.valore);
        }
    }
}
