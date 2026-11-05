package it.unicam.cs.asdl.essortingsol;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
/** Test comuni alle implementazioni, indipendenti dai dettagli algoritmici. */
class ContrattiOrdinamentoAggiuntiviTest {
 private List<SortingAlgorithm<Integer>> algoritmi() {
   return Arrays.<SortingAlgorithm<Integer>>asList(new BubbleSort<Integer>(),new InsertionSort<Integer>(),new MergeSort<Integer>(),new QuickSort<Integer>(),new QuickSortRandom<Integer>());
 }
 @Test void permutazioneConDuplicati() {
   for (SortingAlgorithm<Integer> a: algoritmi()) {
     // Ordinare non significa soltanto produrre una sequenza monotona:
     // occorre conservare anche le molteplicità degli elementi.
     List<Integer> dati = new ArrayList<Integer>(Arrays.asList(4,1,4,-3,0,-3,9));
     SortingAlgorithmResult<Integer> r=a.sort(dati);
     assertEquals(Arrays.asList(-3,-3,0,1,4,4,9),r.getL(),a.getName());
     assertTrue(r.checkOrder(),a.getName()); assertTrue(r.getCountCompare()>=0);
   }
 }
 @Test void casiMinimi() {
   for (SortingAlgorithm<Integer> a: algoritmi()) {
     assertTrue(a.sort(new ArrayList<Integer>()).checkOrder());
     assertEquals(Arrays.asList(7),a.sort(new ArrayList<Integer>(Arrays.asList(7))).getL());
     assertThrows(NullPointerException.class,()->a.sort(null));
   }
 }
}
