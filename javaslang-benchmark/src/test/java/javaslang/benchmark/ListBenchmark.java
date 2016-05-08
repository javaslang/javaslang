package javaslang.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static javaslang.benchmark.BenchmarkResultAggregator.displayRatios;

public class ListBenchmark {
    public static void main(String... args) throws Exception { /* main is more reliable than a test */
        final Options opts = new OptionsBuilder()
                .include(ListBenchmark.class.getSimpleName())
                .shouldDoGC(false)
                .shouldFailOnError(true)
                .build();

        final Collection<RunResult> results = new Runner(opts).run();
        displayRatios(results, "^slang.+$");
    }

    @State(Scope.Benchmark)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1, jvmArgsAppend = { "-XX:+UseG1GC", "-Xss100m", "-Xms1g", "-Xmx1g", "-disableassertions" }) /* set fork to 0 if you want to debug */
    public static class Base {
        @Param({ "10", "100", "1000", "10000" })
        public int CONTAINER_SIZE;

        public Integer[] ELEMENTS;

        @Setup
        public void setup() {
            final Random random = new Random(0);

            ELEMENTS = new Integer[CONTAINER_SIZE];
            for (int i = 0; i < CONTAINER_SIZE; i++) {
                final int value = random.nextInt(CONTAINER_SIZE) - (CONTAINER_SIZE / 2);
                ELEMENTS[i] = value;
            }
        }

        protected static <T> void assertEquals(T a, T b) {
            if (!Objects.equals(a, b)) {
                throw new IllegalStateException(a + " != " + b);
            }
        }
    }

    public static class AddAll extends Base {
        @Benchmark
        @SuppressWarnings("ManualArrayToCollectionCopy")
        public void java_mutable() {
            final java.util.ArrayList<Integer> values = new java.util.ArrayList<>(ELEMENTS.length);
            for (Integer element : ELEMENTS) {
                values.add(element);
            }
            assertEquals(values.size(), CONTAINER_SIZE);
        }

        @Benchmark
        @SuppressWarnings("ManualArrayToCollectionCopy")
        public void java_mutable_linked() {
            final java.util.LinkedList<Integer> values = new java.util.LinkedList<>();
            for (Integer element : ELEMENTS) {
                values.add(element);
            }
            assertEquals(values.size(), CONTAINER_SIZE);
        }

        @Benchmark
        public void scala_mutable() {
            final scala.collection.mutable.MutableList<Integer> values = new scala.collection.mutable.MutableList<>();
            for (Integer element : ELEMENTS) {
                values.prependElem(element);
            }
            assertEquals(values.size(), CONTAINER_SIZE);
        }

        @Benchmark
        public void scala_persistent() {
            scala.collection.immutable.List<Integer> values = scala.collection.immutable.List$.MODULE$.empty();
            for (Integer element : ELEMENTS) {
                values = values.$colon$colon(element);
            }
            assertEquals(values.size(), CONTAINER_SIZE);
        }

        @Benchmark
        public void fjava_persistent() {
            fj.data.List<Integer> values = fj.data.List.list();
            for (Integer element : ELEMENTS) {
                values = values.cons(element);
            }
            assertEquals(values.length(), CONTAINER_SIZE);
        }

        @Benchmark
        public void pcollections_persistent() {
            org.pcollections.PStack<Integer> values = org.pcollections.ConsPStack.empty();
            for (Integer element : ELEMENTS) {
                values = values.plus(element);
            }
            assertEquals(values.size(), CONTAINER_SIZE);
        }

        @Benchmark
        public void slang_persistent() {
            javaslang.collection.List<Integer> values = javaslang.collection.List.empty();
            for (Integer element : ELEMENTS) {
                values = values.prepend(element);
            }
            assertEquals(values.size(), CONTAINER_SIZE);
        }
    }

    public static class Iterate extends Base {
        @State(Scope.Thread)
        public static class Initialized {
            final java.util.ArrayList<Integer> javaMutable = new java.util.ArrayList<>();
            final java.util.LinkedList<Integer> javaMutableLinked = new java.util.LinkedList<>();
            final scala.collection.mutable.MutableList<Integer> scalaMutable = new scala.collection.mutable.MutableList<>();

            int expectedAggregate = 0;
            fj.data.List<Integer> fjavaPersistent = fj.data.List.list();
            org.pcollections.PStack<Integer> pcollectionsPersistent = org.pcollections.ConsPStack.empty();
            scala.collection.immutable.List<Integer> scalaPersistent = scala.collection.immutable.List$.MODULE$.empty();
            javaslang.collection.List<Integer> slangPersistent = javaslang.collection.List.empty();

            @Setup(Level.Invocation)
            public void initializeMutable(Base state) {
                assertEquals(javaMutable.size(), 0);
                Collections.addAll(javaMutable, state.ELEMENTS);
                assertEquals(javaMutable.size(), state.CONTAINER_SIZE);

                assertEquals(javaMutableLinked.size(), 0);
                Collections.addAll(javaMutableLinked, state.ELEMENTS);
                assertEquals(javaMutableLinked.size(), state.CONTAINER_SIZE);

                assertEquals(scalaMutable.size(), 0);
                for (Integer element : state.ELEMENTS) {
                    scalaMutable.prependElem(element);
                }
                assertEquals(scalaMutable.size(), state.CONTAINER_SIZE);

                if (expectedAggregate == 0) {
                    for (Integer element : state.ELEMENTS) {
                        expectedAggregate ^= element;
                    }

                    assertEquals(fjavaPersistent.length(), 0);
                    assertEquals(pcollectionsPersistent.size(), 0);
                    assertEquals(scalaPersistent.size(), 0);
                    assertEquals(slangPersistent.size(), 0);
                    for (Integer element : state.ELEMENTS) {
                        fjavaPersistent = fjavaPersistent.cons(element);
                        pcollectionsPersistent = pcollectionsPersistent.plus(element);
                        scalaPersistent = scalaPersistent.$colon$colon(element);
                        slangPersistent = slangPersistent.prepend(element);
                    }
                    assertEquals(fjavaPersistent.length(), state.CONTAINER_SIZE);
                    assertEquals(pcollectionsPersistent.size(), state.CONTAINER_SIZE);
                    assertEquals(scalaPersistent.size(), state.CONTAINER_SIZE);
                    assertEquals(slangPersistent.size(), state.CONTAINER_SIZE);
                }
            }

            @TearDown(Level.Invocation)
            public void tearDown() {
                javaMutable.clear();
                javaMutableLinked.clear();
                scalaMutable.clear();
            }
        }

        @Benchmark
        @SuppressWarnings("ForLoopReplaceableByForEach")
        public void java_mutable(Initialized state) {
            int aggregate = 0;
            for (final Iterator<Integer> iterator = state.javaMutable.iterator(); iterator.hasNext(); ) {
                aggregate ^= iterator.next();
            }
            assertEquals(aggregate, state.expectedAggregate);
        }

        @Benchmark
        @SuppressWarnings("ForLoopReplaceableByForEach")
        public void java_mutable_linked(Initialized state) {
            int aggregate = 0;
            for (final Iterator<Integer> iterator = state.javaMutableLinked.iterator(); iterator.hasNext(); ) {
                aggregate ^= iterator.next();
            }
            assertEquals(aggregate, state.expectedAggregate);
        }

        @Benchmark
        public void scala_mutable(Initialized state) {
            int aggregate = 0;
            for (final scala.collection.Iterator<Integer> iterator = state.scalaMutable.iterator(); iterator.hasNext(); ) {
                aggregate ^= iterator.next();
            }
            assertEquals(aggregate, state.expectedAggregate);
        }

        @Benchmark
        public void scala_persistent(Initialized state) {
            int aggregate = 0;
            for (final scala.collection.Iterator<Integer> iterator = state.scalaPersistent.iterator(); iterator.hasNext(); ) {
                aggregate ^= iterator.next();
            }
            assertEquals(aggregate, state.expectedAggregate);
        }

        @Benchmark
        @SuppressWarnings("ForLoopReplaceableByForEach")
        public void fjava_persistent(Initialized state) {
            int aggregate = 0;
            for (final Iterator<Integer> iterator = state.fjavaPersistent.iterator(); iterator.hasNext(); ) {
                aggregate ^= iterator.next();
            }
            assertEquals(aggregate, state.expectedAggregate);
        }

        @Benchmark
        @SuppressWarnings("ForLoopReplaceableByForEach")
        public void pcollections_persistent(Initialized state) {
            int aggregate = 0;
            for (final Iterator<Integer> iterator = state.pcollectionsPersistent.iterator(); iterator.hasNext(); ) {
                aggregate ^= iterator.next();
            }
            assertEquals(aggregate, state.expectedAggregate);
        }

        @Benchmark
        @SuppressWarnings("ForLoopReplaceableByForEach")
        public void slang_persistent(Initialized state) {
            int aggregate = 0;
            for (final Iterator<Integer> iterator = state.slangPersistent.iterator(); iterator.hasNext(); ) {
                aggregate ^= iterator.next();
            }
            assertEquals(aggregate, state.expectedAggregate);
        }
    }
}