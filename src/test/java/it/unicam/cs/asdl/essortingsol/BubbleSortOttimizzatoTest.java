package it.unicam.cs.asdl.essortingsol;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test della variante con terminazione anticipata, inclusi i confronti. */
class BubbleSortOttimizzatoTest {

    @Test
    void listaNull() {
        assertThrows(NullPointerException.class,
                () -> new BubbleSort<Integer>().sort(null));
    }

    @Test
    void listaVuotaEOggettoSingolo() {
        BubbleSort<Integer> bubble = new BubbleSort<Integer>();
        assertEquals(0, bubble.sort(new ArrayList<Integer>()).getCountCompare());
        assertEquals(0, bubble.sort(new ArrayList<Integer>(Arrays.asList(7))).getCountCompare());
    }

    @Test
    void listaOrdinataTerminaDopoUnaPassata() {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        SortingAlgorithmResult<Integer> result = new BubbleSort<Integer>().sort(list);
        assertSame(list, result.getL());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result.getL());
        assertEquals(4, result.getCountCompare());
    }

    @Test
    void listaInversaRichiedeTuttiIConfronti() {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(5, 4, 3, 2, 1));
        SortingAlgorithmResult<Integer> result = new BubbleSort<Integer>().sort(list);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result.getL());
        assertEquals(10, result.getCountCompare());
    }

    @Test
    void elementiUgualiNonVengonoScambiati() {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(3, 3, 3, 3, 3));
        SortingAlgorithmResult<Integer> result = new BubbleSort<Integer>().sort(list);
        assertEquals(Arrays.asList(3, 3, 3, 3, 3), result.getL());
        assertEquals(4, result.getCountCompare());
    }

    @Test
    void listaParzialmenteOrdinataTerminaInAnticipo() {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 3, 2, 4, 5));
        SortingAlgorithmResult<Integer> result = new BubbleSort<Integer>().sort(list);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result.getL());
        // Quattro confronti nella prima passata, tre nella seconda senza scambi.
        assertEquals(7, result.getCountCompare());
    }

    @Test
    void centoElementiOrdinatiEOpposti() {
        List<Integer> ascending = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) ascending.add(i);
        List<Integer> descending = new ArrayList<Integer>(ascending);
        Collections.reverse(descending);
        BubbleSort<Integer> bubble = new BubbleSort<Integer>();
        assertEquals(99, bubble.sort(ascending).getCountCompare());
        assertEquals(4950, bubble.sort(descending).getCountCompare());
        assertEquals(ascending, descending);
    }
}
