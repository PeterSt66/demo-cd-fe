package demo.cd.fe;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.junit.Test;

import lombok.SneakyThrows;

public class Testje {

    @Test
    public void testIt() throws Exception {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> sleepIt("F1", 10, 500));
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> sleepIt("F2", 12, 1000));
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> sleepIt("F3", 100, 50));

        // String allRes1 = f1.thenCompose(this::doCompose).thenCombine(f2, (r1,
        // r2) -> {
        // return String.format("Returns: r1=%s, r2=%s", r1, r2);
        // }).get();

        List<String> allRes = sequence(f1.thenCompose(this::doCompose), f2, f3).get();

        System.out.println("allRes=" + allRes);

        List<String> transAll = allRes.stream().flatMap((s) -> {
            return Arrays.asList(s + "-T1", s + "-T2").stream();
        }).collect(Collectors.toList());
        
        System.out.println("allTrans=" + transAll);
    }

    private CompletableFuture<String> doCompose(String res0) {
        return CompletableFuture.supplyAsync(() -> sleepIt(res0 + " - Comp1", 2, 1000));
    }

    @SneakyThrows
    private String sleepIt(String fname, int cycles, int sleepTime) {
        for (int i = 0; i < cycles; i++) {
            Thread.sleep(sleepTime);
            System.out.println(fname + "-" + i);
        }
        return fname;
    }

    @SafeVarargs
    private static <T> CompletableFuture<List<T>> sequence(CompletableFuture<T>... futures) {
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures);
        return allDoneFuture.thenApply(v -> Arrays.stream(futures).map(future -> future.join()).collect(Collectors.<T>toList()));
    }

    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<T>[] futureArray = futures.toArray(new CompletableFuture[futures.size()]);
        return sequence(futureArray);
    }
}