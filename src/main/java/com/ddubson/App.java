package com.ddubson;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@SpringBootApplication
public class App implements CommandLineRunner {
    private Stack<Integer> stack = new Stack<>();

    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public Meter requestMetrics() {
        return metricRegistry().meter("requests");
    }

    @Override
    public void run(String... args) throws Exception {
        //simpleMetricsExample();

        createAndStartReporter();
        metricRegistry().register(
                MetricRegistry.name("mainStack", "size"),
                (Gauge<Integer>) () -> stack.size());

        Stream.iterate(0, (n) -> {
            waitNSec(0.5);
            return n + 1;
        }).forEach(i -> stack.push(i));

        waitNSec(5);
    }


    private void simpleMetricsExample() throws InterruptedException {
        createAndStartReporter();
        requestMetrics().mark();
        waitNSec(5);
    }

    private void createAndStartReporter() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private void waitNSec(double sec) {
        try {
            Thread.sleep((int) (sec * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
