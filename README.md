# Algoritmi di ordinamento e valutazione numerica — ASDL 2026/2027

## Introduzione

Questa esercitazione prosegue il lavoro svolto sugli heap e su HeapSort e riguarda l'implementazione e la **valutazione numerica degli algoritmi di ordinamento**.

Il progetto contiene diverse implementazioni dell'interfaccia generica `SortingAlgorithm<E>` e un framework che consente di raccogliere dati sperimentali sul loro comportamento.

L'esercitazione comprende **due attività obbligatorie**:

1. **Implementazione:** completare gli algoritmi di ordinamento mancanti e verificarne la correttezza.
2. **Valutazione numerica:** eseguire il framework sugli algoritmi disponibili, analizzare i risultati con un foglio elettronico e produrre grafici e confronti.

Il lavoro deve permettere di collegare le previsioni della teoria della complessità con i risultati sperimentali, distinguendo sempre il **numero di confronti** dal **tempo di esecuzione**.

## 1. Algoritmi da implementare

Il progetto contiene le seguenti classi:

| Algoritmo | Stato iniziale | Attività richiesta |
|---|---|---|
| `BubbleSort` | Già implementato | Utilizzare nella valutazione numerica |
| `HeapSort` | Già implementato | Utilizzare nella valutazione numerica |
| `InsertionSort` | Da completare | Implementazione obbligatoria |
| `MergeSort` | Da completare | Implementazione obbligatoria |
| `QuickSort` | Da completare | Implementazione obbligatoria |
| `QuickSortRandom` | Da completare | Implementazione obbligatoria |

**Devono essere implementati tutti e quattro gli algoritmi mancanti**, rispettando le indicazioni e i contratti riportati nelle rispettive classi Java.

Le implementazioni devono:

- rispettare l'interfaccia generica `SortingAlgorithm<E>`;
- ordinare gli elementi in ordine non decrescente;
- conservare tutti gli elementi della sequenza originale, comprese eventuali ripetizioni;
- conteggiare correttamente le chiamate a `compareTo` eseguite durante l'ordinamento;
- rispettare le ulteriori condizioni specificate nel Javadoc e nei test.

La classe `BubbleSort` fornita è la versione **ottimizzata**, che termina quando una passata non effettua scambi. Corrisponde alla versione presentata nelle slide teoriche L10.

La classe `HeapSort`, già implementata, deriva dall'esercitazione precedente sugli heap.

Le due implementazioni costituiscono il riferimento iniziale per la valutazione numerica.

## 2. Verifica delle implementazioni

Prima di eseguire il benchmark, verificare che gli algoritmi implementati superino i rispettivi test.

Dalla directory principale del progetto Maven:

```bash
mvn test
```

Finché alcune classi contengono TODO non implementati, è possibile che alcuni test falliscano.

Per compilare il progetto senza eseguire i test:

```bash
mvn -DskipTests compile
```

Il superamento dei test è una verifica importante, ma non sostituisce il rispetto delle specifiche delle classi.

## 3. Obiettivi della valutazione numerica

Per ogni algoritmo vogliamo studiare come variano le prestazioni al crescere della dimensione della sequenza.

Misuriamo due grandezze:

**Numero di confronti.** È il numero di chiamate a `compareTo` eseguite durante l'ordinamento. Costituisce il principale indicatore del lavoro algoritmico e permette di confrontare i risultati con le previsioni della complessità teorica.

**Tempo di esecuzione.** È misurato in nanosecondi mediante `System.nanoTime()`. Dipende anche dalla JVM, dal compilatore JIT, dalle cache, dal sistema operativo e dalle condizioni della macchina.

Le due misure devono essere analizzate separatamente.

## 4. Il framework sperimentale

La classe principale è:

```text
SortingAlgorithmEvaluationFramework
```

Nella versione studenti il suo nome completo è:

```text
it.unicam.cs.asdl.essorting.SortingAlgorithmEvaluationFramework
```

Nella versione soluzione è:

```text
it.unicam.cs.asdl.essortingsol.SortingAlgorithmEvaluationFramework
```

Il framework genera sequenze di lunghezza crescente, esegue gli algoritmi selezionati e registra i risultati.

### Protocollo sperimentale completo

| Parametro | Valore |
|---|---|
| Dimensione minima | 50 |
| Dimensione massima | 1500 |
| Incremento | 50 |
| Dimensioni considerate | 30 |
| Sequenze casuali per dimensione | 100 |
| Ripetizioni per sequenza e algoritmo | 5 |
| Invocazioni preliminari di warm-up per algoritmo | 200 |
| Seed iniziale | `20261105` |
| Famiglia principale di input | `random` |

Per ogni algoritmo vengono quindi eseguite:

**30 dimensioni × 100 sequenze × 5 ripetizioni = 15.000 misurazioni temporali.**

Il numero complessivo di misurazioni dipende dal numero di algoritmi selezionati.

### Warm-up e ripetizioni

Prima delle misurazioni, il framework esegue un insieme di ordinamenti preliminari (*warm-up*). Queste esecuzioni non vengono incluse nei risultati.

Il warm-up serve a ridurre alcuni effetti iniziali della JVM, in particolare quelli legati alla compilazione JIT, ma non garantisce l'eliminazione delle interferenze.

Ogni algoritmo viene poi eseguito cinque volte sulla stessa sequenza, ripartendo sempre da una nuova copia dell'input originale.

Le ripetizioni sono distribuite in **cinque passate separate**. A ogni passata vengono variati, in modo riproducibile, l'ordine delle sequenze e quello degli algoritmi.

Non vengono modificati gli elementi delle sequenze: tutti gli algoritmi ricevono gli stessi input.

Le cinque ripetizioni consentono di ottenere una misura temporale più robusta per ciascuna sequenza, mentre le 100 sequenze differenti permettono di studiare la variabilità dovuta agli input.

## 5. Esecuzione del framework

### Esecuzione rapida di prova

Dopo aver compilato il progetto:

```bash
java -cp target/classes \
  it.unicam.cs.asdl.essorting.SortingAlgorithmEvaluationFramework \
  dati-prova --quick
```

Il profilo `--quick` utilizza:

- tre dimensioni: 50, 100 e 150;
- cinque sequenze per dimensione;
- tre ripetizioni per sequenza;
- 15 invocazioni preliminari di warm-up per algoritmo.

Questa modalità serve **soltanto a verificare il funzionamento** del framework. Non deve essere utilizzata come esperimento principale per l'analisi statistica.

### Esecuzione completa con BubbleSort e HeapSort

Per ottenere i dati relativi ai due algoritmi già implementati:

```bash
java -cp target/classes \
  it.unicam.cs.asdl.essorting.SortingAlgorithmEvaluationFramework \
  dati-bubble-heap \
  --algorithms=BubbleSort,HeapSort
```

Il framework utilizza il protocollo completo e salva i risultati nella directory `dati-bubble-heap`.

### Esecuzione con tutti gli algoritmi implementati

Dopo aver completato le implementazioni, eseguire il framework selezionando tutti gli algoritmi funzionanti.

Per esempio, se sono stati implementati tutti:

```bash
java -cp target/classes \
  it.unicam.cs.asdl.essorting.SortingAlgorithmEvaluationFramework \
  dati-completi \
  --algorithms=BubbleSort,HeapSort,InsertionSort,MergeSort,QuickSort,QuickSortRandom
```

**È obbligatorio tentare l'implementazione di tutti gli algoritmi mancanti.**

Per la valutazione numerica, invece, devono essere inclusi BubbleSort, HeapSort e **tutti gli altri algoritmi che si è riusciti a implementare correttamente**.

Non includere nel benchmark algoritmi ancora incompleti o che producono risultati non corretti. Indicare nel materiale consegnato gli eventuali algoritmi esclusi e il motivo dell'esclusione.

### Altri parametri

Le opzioni del framework, visualizzabili con `--help`, comprendono:

```text
--min=50
--max=1500
--step=50
--samples=100
--repetitions=5
--warmup=200
--seed=20261105
--domain=1500
--algorithms=BubbleSort,HeapSort
--families=random
```

Sono disponibili anche le famiglie di input:

```text
ascending
descending
duplicates
```

Per esempio:

```text
--families=random,ascending,descending,duplicates
```

L'analisi principale richiesta riguarda gli **input casuali uniformi**, che possono contenere valori ripetuti.

Gli esperimenti su altre famiglie sono facoltativi e devono essere analizzati separatamente, senza mescolare i risultati di distribuzioni differenti.

## 6. File generati dal framework

Per ogni esperimento il framework produce tre file.

### `measurements.csv`

Contiene una riga per ogni esecuzione misurata, con le colonne:

```text
experimentId,sequenceId,inputFamily,n,algorithm,repetition,timeNs,comparisons,valid
```

Registra quindi, per ciascun algoritmo e input, le singole ripetizioni, il tempo in nanosecondi, il numero di confronti e l'esito della validazione.

### `sequences.csv`

Contiene le informazioni necessarie per identificare e ricostruire le sequenze utilizzate, inclusi i relativi seed.

### `metadata.properties`

Contiene i parametri del benchmark, le informazioni sull'ambiente Java, il numero di sequenze e il numero di misurazioni.

**Conservare questi file senza modificarne i dati originali.**

Il framework non elimina automaticamente i tempi anomali e non produce statistiche filtrate.

## 7. Elaborazione dei risultati nel foglio elettronico

È disponibile un foglio elettronico di esempio, contenente l'analisi completa di **BubbleSort e HeapSort**.

Il foglio costituisce il punto di partenza per il lavoro richiesto.

Gli studenti devono **estenderlo**, aggiungendo i dati e l'analisi degli altri algoritmi implementati correttamente.

### 7.1 Aggregazione dei tempi

Per ciascun algoritmo, dimensione e sequenza:

1. Considerare i cinque tempi registrati.
2. Calcolarne la **mediana**.
3. Utilizzare tale mediana come tempo rappresentativo dell'ordinamento di quella sequenza.

Si ottengono così 100 tempi rappresentativi per ogni algoritmo e dimensione.

Su questi 100 valori calcolare almeno:

- mediana;
- media aritmetica;
- primo quartile (Q1);
- terzo quartile (Q3);
- deviazione standard;
- minimo e massimo campionari.

Le cinque ripetizioni dello stesso input non devono essere interpretate come cinque input indipendenti.

### 7.2 Analisi del numero di confronti

Per ciascun algoritmo e dimensione, calcolare almeno:

- numero medio di confronti tra le sequenze;
- minimo e massimo campionari;
- deviazione standard;
- mediana.

La **media del numero di confronti** deve essere utilizzata come riferimento principale per studiare la crescita algoritmica.

Per algoritmi randomizzati, come `QuickSortRandom`, anche il numero di confronti può variare tra ripetizioni dello stesso input. In tal caso occorre aggregare i confronti delle ripetizioni per ogni sequenza e successivamente analizzare le diverse sequenze, senza confondere queste due fonti di variabilità.

### 7.3 Grafici di crescita

Per ogni algoritmo analizzato, produrre grafici che mostrino:

- il numero medio di confronti al variare della dimensione `n`;
- il tempo mediano al variare della dimensione `n`;
- il comportamento dei confronti normalizzati rispetto a una funzione coerente con la complessità teorica attesa.

Per esempio:

\[
\frac{\overline C(n)}{n^2}
\qquad\text{oppure}\qquad
\frac{\overline C(n)}{n\log_2 n}.
\]

La normalizzazione consente di verificare empiricamente se il costo misurato presenta un andamento compatibile con una funzione di crescita teorica.

La stabilizzazione di un rapporto su un intervallo finito non costituisce una dimostrazione della complessità asintotica.

### 7.4 Confronti tra coppie di algoritmi

Il foglio di esempio contiene già il confronto tra **BubbleSort e HeapSort**.

È richiesto di aggiungere **almeno altri due confronti tra coppie di algoritmi differenti**, scegliendo tra quelli implementati correttamente.

Per ciascuna coppia:

1. Utilizzare i tempi relativi alle **stesse sequenze di input**.
2. Calcolare, per ogni sequenza, il rapporto tra le mediane temporali dei due algoritmi.
3. Calcolare la mediana dei rapporti sulle 100 sequenze per ciascuna dimensione.
4. Rappresentare l'andamento del rapporto al crescere di `n`.
5. Commentare brevemente i risultati.

Questo procedimento è un **confronto appaiato**, perché i due algoritmi sono valutati sugli stessi input.

Un rapporto maggiore di 1 indica che l'algoritmo al numeratore ha impiegato più tempo di quello al denominatore.

Le due coppie aggiuntive devono essere diverse dalla coppia BubbleSort–HeapSort già analizzata.

**Il confronto di tutte le coppie possibili è facoltativo.**

### 7.5 Interpretazione e commento

Il foglio consegnato deve contenere anche brevi commenti interpretativi, direttamente nei fogli di analisi o in un'apposita sezione.

In particolare, discutere:

- se la crescita del numero di confronti è coerente con la teoria;
- come cambiano i tempi all'aumentare della dimensione;
- quali differenze emergono dai confronti tra algoritmi;
- quali limiti presentano le misurazioni temporali.

Non confondere il minimo e il massimo osservati sui campioni casuali con i casi migliore e peggiore teorici.

Gli eventuali outlier temporali possono essere segnalati, ad esempio mediante la soglia

\[
Q_3+3(Q_3-Q_1),
\]

ma **non devono essere eliminati automaticamente**. Le osservazioni originali devono rimanere disponibili.

## 8. Modalità di consegna

La consegna dell'esercitazione avviene tramite **Moodle**, secondo le modalità previste per le altre esercitazioni del laboratorio.

Devono essere consegnati:

1. **Tutte le classi Java degli algoritmi di ordinamento implementati** e completati durante l'esercitazione. Non è necessario consegnare le classi già fornite (`BubbleSort` e `HeapSort`).
2. **Il foglio elettronico con l'elaborazione dei dati**, scegliendo una delle due modalità seguenti:
  - **Excel:** consegnare il file `.xlsx` contenente dati, statistiche, grafici e confronti richiesti.
  - **Google Sheets:** consegnare un file di testo denominato esattamente `Link.txt`, contenente il collegamento al foglio elettronico.

Nel caso di Google Sheets, **prima della consegna** è necessario impostare la condivisione in lettura con **chiunque abbia il link all'interno dell'Università di Camerino**. Verificare che il documento sia effettivamente accessibile senza richiedere autorizzazioni individuali.

### Contenuto richiesto nel foglio elettronico

Il foglio consegnato deve contenere:

- i dati e le statistiche di BubbleSort e HeapSort;
- i dati e le statistiche di tutti gli altri algoritmi implementati correttamente;
- i grafici della crescita del numero di confronti e dei tempi di esecuzione;
- i grafici normalizzati rispetto alle funzioni teoriche appropriate;
- il confronto appaiato BubbleSort–HeapSort già presente nel foglio di esempio;
- **almeno due ulteriori confronti appaiati** tra coppie di algoritmi differenti;
- brevi commenti sui risultati ottenuti e sui limiti dell'analisi sperimentale.

Il confronto di tutte le coppie possibili è facoltativo.

Se alcuni algoritmi non sono stati completati correttamente, devono comunque essere consegnate le classi con il lavoro svolto. L'analisi numerica deve invece includere soltanto gli algoritmi effettivamente funzionanti.

Non è necessario consegnare le classi del framework, i test o l'intero progetto Maven. La consegna su Moodle deve comprendere le classi degli algoritmi e **un solo file per l'elaborazione dei risultati: il file Excel oppure `Link.txt`**.
## 9. Riferimenti alla teoria

Per la progettazione, l'implementazione e l'interpretazione dei risultati fare riferimento alle slide del corso di Algoritmi e Strutture Dati:

- **L10 — Algoritmi di ordinamento:** BubbleSort, InsertionSort, MergeSort e principi generali dell'ordinamento.
- **L11 — Quicksort e Counting Sort:** QuickSort, analisi dei casi e limiti degli algoritmi di ordinamento per confronti.
- **Lezione sugli heap:** struttura heap, costruzione bottom-up e HeapSort.

Il codice di BubbleSort fornito corrisponde alla variante ottimizzata con terminazione anticipata.

## 10. Limiti dell'esperimento

Il framework è uno strumento **didattico**, non un sostituto di strumenti specializzati di benchmarking come JMH (*Java Microbenchmark Harness*).

La riproducibilità degli input mediante seed non implica la riproducibilità esatta dei tempi. Le prestazioni possono dipendere dalla JVM, dall'hardware e dalle condizioni del sistema.

Per questo motivo:

- le conclusioni principali sulla complessità devono basarsi soprattutto sul numero di confronti;
- i tempi devono essere interpretati come misurazioni sperimentali;
- i risultati temporali ottenuti su computer differenti non devono essere confrontati direttamente come se fossero acquisiti nelle stesse condizioni;
- la valutazione numerica può fornire evidenze coerenti con la teoria, ma non sostituisce l'analisi matematica della complessità.