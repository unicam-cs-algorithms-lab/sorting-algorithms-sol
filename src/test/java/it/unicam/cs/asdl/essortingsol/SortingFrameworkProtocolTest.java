package it.unicam.cs.asdl.essortingsol;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Test del protocollo di misurazione, indipendenti dai tempi effettivi. */
public class SortingFrameworkProtocolTest {
    @Test
    public void testValidazionePermutazioneConDuplicati() {
        List<Integer> input = Arrays.asList(3, 2, 2);
        assertTrue(SortingAlgorithmEvaluationFramework.isValidResult(input,
            new SortingAlgorithmResult<>(Arrays.asList(2, 2, 3), 3)));
        assertFalse(SortingAlgorithmEvaluationFramework.isValidResult(input,
            new SortingAlgorithmResult<>(Arrays.asList(2, 3, 3), 3)));
        assertFalse(SortingAlgorithmEvaluationFramework.isValidResult(input,
            new SortingAlgorithmResult<>(Arrays.asList(3, 2, 2), 3)));
    }

    @Test
    public void testInputRiproducibili() {
        List<Integer> a = SortingAlgorithmEvaluationFramework.generate("random", 100, 1234L, 1500);
        assertEquals(a, SortingAlgorithmEvaluationFramework.generate("random", 100, 1234L, 1500));
        assertEquals(Arrays.asList(0, 1, 2, 3), SortingAlgorithmEvaluationFramework.generate("ascending", 4, 2L, 10));
        assertEquals(Arrays.asList(3, 2, 1, 0), SortingAlgorithmEvaluationFramework.generate("descending", 4, 2L, 10));
    }

    @Test
    public void testProtocolloCsvConBubbleSort() throws IOException {
        Path dir = Files.createTempDirectory("asdl-framework-");
        SortingAlgorithmEvaluationFramework.main(new String[] {dir.toString(),
            "--min=5", "--max=10", "--step=5", "--samples=2", "--repetitions=3",
            "--warmup=2", "--algorithms=BubbleSort", "--seed=99"});
        List<String> rows = Files.readAllLines(dir.resolve("measurements.csv"), StandardCharsets.UTF_8);
        assertEquals(1 + 2 * 2 * 3, rows.size());
        assertEquals("experimentId,sequenceId,inputFamily,n,algorithm,repetition,timeNs,comparisons,valid", rows.get(0));
        for (String row : rows.subList(1, rows.size())) {
            String[] fields = row.split(",", -1);
            assertEquals(9, fields.length);
            assertEquals("BubbleSort", fields[4]);
            assertTrue(Long.parseLong(fields[6]) >= 0);
            assertEquals("true", fields[8]);
        }
        assertEquals(1 + 4, Files.readAllLines(dir.resolve("sequences.csv"), StandardCharsets.UTF_8).size());
        assertTrue(Files.readAllLines(dir.resolve("metadata.properties"), StandardCharsets.UTF_8)
                .stream().anyMatch(x -> x.equals("measurementCount=12")));
    }


    @Test
    public void testRipetizioniDistribuiteInPassate() throws IOException {
        Path dir = Files.createTempDirectory("asdl-passes-");
        SortingAlgorithmEvaluationFramework.main(new String[] {dir.toString(),
            "--min=10", "--max=10", "--step=5", "--samples=6", "--repetitions=4",
            "--warmup=2", "--algorithms=BubbleSort", "--seed=123"});
        List<String> rows = Files.readAllLines(dir.resolve("measurements.csv"), StandardCharsets.UTF_8);
        assertEquals(1 + 6 * 4, rows.size());
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (int pass = 1; pass <= 4; pass++) {
            java.util.Set<String> samplesInPass = new java.util.HashSet<>();
            for (int i = 0; i < 6; i++) {
                String[] fields = rows.get(1 + (pass - 1) * 6 + i).split(",", -1);
                assertEquals(pass, Integer.parseInt(fields[5]));
                assertTrue(samplesInPass.add(fields[1]));
                assertTrue(seen.add(fields[1] + ":" + pass));
            }
            assertEquals(6, samplesInPass.size());
        }
        assertEquals(24, seen.size());
        assertTrue(Files.readAllLines(dir.resolve("metadata.properties"), StandardCharsets.UTF_8)
                .stream().anyMatch(x -> x.startsWith("measurementSchedule=separate passes")));
    }

    @Test
    public void testParametriNonValidi() {
        assertThrows(IllegalArgumentException.class,
            () -> SortingAlgorithmEvaluationFrameworkParameters.parse(new String[] {"--repetitions=0"}));
        assertThrows(IllegalArgumentException.class,
            () -> SortingAlgorithmEvaluationFrameworkParameters.parse(new String[] {"--families=random,random"}));
    }
}
