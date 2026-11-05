package it.unicam.cs.asdl.essortingsol;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Parametri del protocollo sperimentale, configurabili dalla riga di comando.
 * <p>Il profilo completo genera 30 dimensioni, 100 input per dimensione e
 * 5 ripetizioni misurate per ciascun algoritmo e input. Il profilo quick serve
 * a verificare il funzionamento, non a trarre conclusioni statistiche.
 * <p>Usare {@code --help} per visualizzare le opzioni.
 */
public final class SortingAlgorithmEvaluationFrameworkParameters {
    public int minLength = 50;
    public int maxLength = 1500;
    public int step = 50;
    public int samples = 100;
    public int repetitions = 5;
    public int warmupCalls = 200;
    public int maxGeneratedInteger = 1500;
    public long seed = 20261105L;
    public String families = "random";
    public String algorithms = "BubbleSort,HeapSort";
    public boolean quick = false;

    private SortingAlgorithmEvaluationFrameworkParameters() { }

    /** Crea la configurazione a partire da opzioni {@code --nome=valore}. */
    public static SortingAlgorithmEvaluationFrameworkParameters parse(String[] args) {
        SortingAlgorithmEvaluationFrameworkParameters p = new SortingAlgorithmEvaluationFrameworkParameters();
        // Il profilo quick viene impostato prima di leggere le altre opzioni:
        // così i valori espliciti hanno precedenza indipendentemente dall'ordine.
        for (String arg : args) if (arg.equals("--quick")) {
            p.quick = true;
            p.minLength = 50; p.maxLength = 150; p.step = 50;
            p.samples = 5; p.repetitions = 3; p.warmupCalls = 15;
        }
        for (String arg : args) {
            if (arg.equals("--quick")) continue;
            else if (arg.startsWith("--min=")) p.minLength = number(arg);
            else if (arg.startsWith("--max=")) p.maxLength = number(arg);
            else if (arg.startsWith("--step=")) p.step = number(arg);
            else if (arg.startsWith("--samples=")) p.samples = number(arg);
            else if (arg.startsWith("--repetitions=")) p.repetitions = number(arg);
            else if (arg.startsWith("--warmup=")) p.warmupCalls = number(arg);
            else if (arg.startsWith("--domain=")) p.maxGeneratedInteger = number(arg);
            else if (arg.startsWith("--seed=")) p.seed = Long.parseLong(value(arg));
            else if (arg.startsWith("--families=")) p.families = value(arg);
            else if (arg.startsWith("--algorithms=")) p.algorithms = value(arg);
            else throw new IllegalArgumentException("Opzione sconosciuta: " + arg);
        }
        if (p.minLength < 2 || p.maxLength < p.minLength || p.step <= 0
                || p.samples <= 0 || p.repetitions <= 0 || p.warmupCalls < 0
                || p.maxGeneratedInteger <= 0)
            throw new IllegalArgumentException("Parametri numerici non validi");
        checkCsvList(p.families, "random", "ascending", "descending", "duplicates");
        checkCsvList(p.algorithms, "BubbleSort", "HeapSort", "InsertionSort", "MergeSort", "QuickSort", "QuickSortRandom");
        return p;
    }

    private static int number(String arg) { return Integer.parseInt(value(arg)); }
    private static String value(String arg) { return arg.substring(arg.indexOf('=') + 1); }

    private static void checkCsvList(String value, String... allowed) {
        Set<String> valid = new LinkedHashSet<>(Arrays.asList(allowed));
        Set<String> seen = new LinkedHashSet<>();
        if (value.isEmpty()) throw new IllegalArgumentException("Elenco vuoto");
        for (String item : value.split(",", -1)) {
            if (!valid.contains(item) || !seen.add(item))
                throw new IllegalArgumentException("Elemento sconosciuto o ripetuto: " + item);
        }
    }

    public static String help() {
        return "Uso: java ...SortingAlgorithmEvaluationFramework DIRECTORY [opzioni]\n"
            + "--quick  profilo di prova (non scientifico)\n"
            + "--min=N --max=N --step=N --samples=K --repetitions=R --warmup=W\n"
            + "--seed=LONG --domain=D\n"
            + "--families=random,ascending,descending,duplicates\n"
            + "--algorithms=BubbleSort,HeapSort (o altri algoritmi disponibili)\n"
            + "I confronti sono misurati per ogni ripetizione; nessun outlier è eliminato.\n";
    }
}
