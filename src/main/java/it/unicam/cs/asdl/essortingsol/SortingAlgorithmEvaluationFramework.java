package it.unicam.cs.asdl.essortingsol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Framework didattico per confrontare ordinamenti mediante confronti e tempi.
 * <p>Protocollo: input riproducibili, warm-up non misurato, copie fresche
 * prima di ogni sort, ripetizioni distribuite in passate distinte, ordine
 * di input e algoritmi rimescolato riproducibilmente e validazione fuori dalla misura.
 * I dati grezzi non sono filtrati: media, mediana, quartili e individuazione
 * degli outlier sono demandate al foglio di analisi.
 * <p>Il tempo copre soltanto la chiamata {@code sort(copia)}; comprende il
 * lavoro e le allocazioni interne all'algoritmo, ma non la copia dell'input.
 * Non è un sostituto di JMH per benchmarking professionale della JVM.
 * <p>Il metodo main accetta una directory di destinazione seguita da opzioni
 * {@code --nome=valore}; usare {@code --help} per i dettagli.
 */
public final class SortingAlgorithmEvaluationFramework {
    private SortingAlgorithmEvaluationFramework() { }

    /** Punto di ingresso del benchmark; genera measurements.csv, sequences.csv e metadata.properties. */
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && "--help".equals(args[0])) {
            System.out.println(SortingAlgorithmEvaluationFrameworkParameters.help());
            return;
        }
        String dir = args.length > 0 && !args[0].startsWith("--") ? args[0] : ".";
        int offset = args.length > 0 && !args[0].startsWith("--") ? 1 : 0;
        SortingAlgorithmEvaluationFrameworkParameters p =
                SortingAlgorithmEvaluationFrameworkParameters.parse(Arrays.copyOfRange(args, offset, args.length));
        run(Paths.get(dir), p);
    }

    /** Esegue l'esperimento con la configurazione ricevuta. */
    public static void run(Path directory, SortingAlgorithmEvaluationFrameworkParameters p) throws IOException {
        Files.createDirectories(directory);
        LinkedHashMap<String, SortingAlgorithm<Integer>> algorithms = chooseAlgorithms(p.algorithms);
        String id = "asdl-" + Long.toUnsignedString(p.seed) + "-" + System.currentTimeMillis();
        long start = System.nanoTime();
        // Warm-up separato: eseguiamo ogni algoritmo su più dimensioni e input.
        for (Map.Entry<String, SortingAlgorithm<Integer>> entry : algorithms.entrySet()) {
            warmUp(entry.getValue(), p, entry.getKey());
        }
        int sequenceCount = 0;
        long measurementCount = 0;
        try (BufferedWriter measurements = Files.newBufferedWriter(directory.resolve("measurements.csv"), StandardCharsets.UTF_8);
             BufferedWriter sequences = Files.newBufferedWriter(directory.resolve("sequences.csv"), StandardCharsets.UTF_8)) {
            measurements.write("experimentId,sequenceId,inputFamily,n,algorithm,repetition,timeNs,comparisons,valid\n");
            sequences.write("sequenceId,inputFamily,n,sequenceSeed,values\n");
            for (String family : p.families.split(",")) {
                for (int n = p.minLength; n <= p.maxLength; n += p.step) {
                    // Generiamo gli input della dimensione una sola volta.
                    // La stessa sequenza verrà riutilizzata in tutte le passate,
                    // ma ciascun ordinamento riceverà una copia fresca.
                    List<List<Integer>> inputs = new ArrayList<>(p.samples);
                    List<String> sequenceIds = new ArrayList<>(p.samples);
                    for (int sample = 0; sample < p.samples; sample++) {
                        String sequenceId = family + "_" + n + "_" + sample;
                        long sequenceSeed = seedFor(p.seed, family, n, sample);
                        List<Integer> input = generate(family, n, sequenceSeed, p.maxGeneratedInteger);
                        sequenceIds.add(sequenceId);
                        inputs.add(input);
                        sequences.write(sequenceId + "," + family + "," + n + "," + sequenceSeed + "," + joinValues(input) + "\n");
                        sequenceCount++;
                    }
                    // Passate separate: tutti gli input sono misurati una volta
                    // prima di iniziare la passata successiva. L'ordine degli
                    // input e quello degli algoritmi variano, con seed riproducibili.
                    for (int repetition = 1; repetition <= p.repetitions; repetition++) {
                        List<Integer> sampleOrder = new ArrayList<>(p.samples);
                        for (int sample = 0; sample < p.samples; sample++) sampleOrder.add(sample);
                        Collections.shuffle(sampleOrder,
                                new Random(seedFor(p.seed, "sample-order-" + family, n, repetition)));
                        for (int sample : sampleOrder) {
                            String sequenceId = sequenceIds.get(sample);
                            List<Integer> input = inputs.get(sample);
                            List<String> order = new ArrayList<>(algorithms.keySet());
                            Collections.shuffle(order,
                                    new Random(seedFor(p.seed, "algorithm-order-" + family,
                                            n, sample * 10007 + repetition)));
                            for (String name : order) {
                                SortingAlgorithm<Integer> algorithm = algorithms.get(name);
                                // Copia e verifica FUORI dalla finestra temporizzata.
                                List<Integer> copy = new ArrayList<>(input);
                                long begin = System.nanoTime();
                                SortingAlgorithmResult<Integer> result = algorithm.sort(copy);
                                long elapsed = System.nanoTime() - begin;
                                boolean valid = isValidResult(input, result);
                                if (!valid)
                                    throw new SortingException("Ordinamento errato: " + name + " su " + sequenceId
                                            + ", ripetizione " + repetition + ". Controllare i TODO.");
                                measurements.write(id + "," + sequenceId + "," + family + "," + n + "," + name + ","
                                        + repetition + "," + elapsed + "," + result.getCountCompare() + ",true\n");
                                measurementCount++;
                            }
                        }
                    }
                    System.out.println("Completata dimensione " + n + " (famiglia " + family + ")");
                }
            }
        }
        writeMetadata(directory.resolve("metadata.properties"), id, p, algorithms, sequenceCount, measurementCount,
                System.nanoTime() - start);
        System.out.println("Completato: " + sequenceCount + " sequenze, " + measurementCount + " misurazioni in " + directory);
    }

    private static LinkedHashMap<String, SortingAlgorithm<Integer>> chooseAlgorithms(String requested) {
        LinkedHashMap<String, SortingAlgorithm<Integer>> selected = new LinkedHashMap<>();
        for (String name : requested.split(",")) {
            switch (name) {
                case "BubbleSort": selected.put(name, new BubbleSort<>()); break;
                case "HeapSort": selected.put(name, new HeapSort<>()); break;
                case "InsertionSort": selected.put(name, new InsertionSort<>()); break;
                case "MergeSort": selected.put(name, new MergeSort<>()); break;
                case "QuickSort": selected.put(name, new QuickSort<>()); break;
                case "QuickSortRandom": selected.put(name, new QuickSortRandom<>()); break;
                default: throw new IllegalArgumentException("Algoritmo sconosciuto: " + name);
            }
        }
        return selected;
    }

    private static void warmUp(SortingAlgorithm<Integer> algorithm,
                               SortingAlgorithmEvaluationFrameworkParameters p, String name) {
        Random random = new Random(seedFor(p.seed, "warmup-" + name, p.maxLength, 0));
        int[] sizes = {p.minLength, p.minLength + (p.maxLength - p.minLength) / 2, p.maxLength};
        for (int i = 0; i < p.warmupCalls; i++) {
            int n = sizes[i % sizes.length];
            List<Integer> input = generate("random", n, random.nextLong(), p.maxGeneratedInteger);
            SortingAlgorithmResult<Integer> result = algorithm.sort(new ArrayList<>(input));
            if (!isValidResult(input, result))
                throw new SortingException("Errore nel warm-up di " + name + ": completare gli eventuali TODO.");
        }
    }

    /** Genera una sequenza ricostruibile dai suoi parametri e dal seed. */
    public static List<Integer> generate(String family, int n, long seed, int domain) {
        if (n < 0 || domain <= 0) throw new IllegalArgumentException("Dimensione o dominio non validi");
        Random random = new Random(seed);
        List<Integer> values = new ArrayList<>(n);
        switch (family) {
            case "random":
                for (int i = 0; i < n; i++) values.add(random.nextInt(domain));
                break;
            case "ascending":
                for (int i = 0; i < n; i++) values.add(i);
                break;
            case "descending":
                for (int i = n - 1; i >= 0; i--) values.add(i);
                break;
            case "duplicates":
                for (int i = 0; i < n; i++) values.add(random.nextInt(Math.min(domain, 5)));
                break;
            default: throw new IllegalArgumentException("Famiglia sconosciuta: " + family);
        }
        return values;
    }

    /** Verifica sia l'ordinamento sia la conservazione di ogni molteplicità. */
    public static boolean isValidResult(List<Integer> input, SortingAlgorithmResult<Integer> result) {
        if (result == null || result.getL() == null || result.getL().size() != input.size()
                || result.getCountCompare() < 0 || !result.checkOrder()) return false;
        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer value : input) counts.put(value, counts.getOrDefault(value, 0) + 1);
        for (Integer value : result.getL()) {
            Integer count = counts.get(value);
            if (count == null || count == 0) return false;
            if (count == 1) counts.remove(value);
            else counts.put(value, count - 1);
        }
        return counts.isEmpty();
    }

    private static long seedFor(long base, String family, int n, int sample) {
        // Mix esplicito e deterministico, indipendente dal numero di input già generati.
        long x = base ^ (0x9E3779B97F4A7C15L * (long) family.hashCode());
        x ^= 0xBF58476D1CE4E5B9L * (n + 1L);
        x ^= 0x94D049BB133111EBL * (sample + 1L);
        x ^= x >>> 30; x *= 0xBF58476D1CE4E5B9L;
        x ^= x >>> 27; x *= 0x94D049BB133111EBL;
        return x ^ (x >>> 31);
    }

    private static String joinValues(List<Integer> input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.size(); i++) {
            if (i > 0) sb.append('|');
            sb.append(input.get(i));
        }
        return sb.toString();
    }

    private static void writeMetadata(Path filename, String id,
                                      SortingAlgorithmEvaluationFrameworkParameters p,
                                      LinkedHashMap<String, SortingAlgorithm<Integer>> algorithms,
                                      int sequences, long measurements, long totalNs) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filename, StandardCharsets.UTF_8)) {
            writer.write("experimentId=" + id + "\n");
            writer.write("completedUtc=" + Instant.now().toString() + "\n");
            writer.write("algorithmNames=" + String.join(",", algorithms.keySet()) + "\n");
            writer.write("inputFamilies=" + p.families + "\n");
            writer.write("minLength=" + p.minLength + "\nmaxLength=" + p.maxLength + "\nstep=" + p.step + "\n");
            writer.write("samplesPerLength=" + p.samples + "\nrepetitions=" + p.repetitions + "\n");
            writer.write("warmupCallsPerAlgorithm=" + p.warmupCalls + "\ninputDomain=" + p.maxGeneratedInteger + "\n");
            writer.write("seed=" + p.seed + "\nquickProfile=" + p.quick + "\n");
            writer.write("sequenceCount=" + sequences + "\nmeasurementCount=" + measurements + "\n");
            writer.write("benchmarkWallTimeNs=" + totalNs + "\n");
            writer.write("javaVersion=" + System.getProperty("java.version") + "\n");
            writer.write("javaVmName=" + System.getProperty("java.vm.name") + "\n");
            writer.write("osName=" + System.getProperty("os.name") + "\n");
            writer.write("osArch=" + System.getProperty("os.arch") + "\n");
            writer.write("availableProcessors=" + Runtime.getRuntime().availableProcessors() + "\n");
            writer.write("maxMemoryBytes=" + Runtime.getRuntime().maxMemory() + "\n");
            writer.write("timeBoundary=algorithm.sort(copy) only; excludes input generation/copy/verification/CSV\n");
            writer.write("outlierPolicy=retain all raw observations; flag in spreadsheet\n");
            writer.write("measurementSchedule=separate passes per family and size; seeded shuffle of inputs and algorithms\n");
        }
    }
}
