package com.freezonex.aps.config;

import io.kubernetes.client.extended.leaderelection.LeaderElectionConfig;
import io.kubernetes.client.extended.leaderelection.LeaderElector;
import io.kubernetes.client.extended.leaderelection.resourcelock.ConfigMapLock;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
public class LeaderService {

    private volatile boolean isLeader = false;
    private List<Consumer<Boolean>> observers = new ArrayList<>();
    public void addObserver(Consumer<Boolean> observer) {
        observers.add(observer);
    }
    private void notifyObservers(boolean newLeaderStatus) {
        for (Consumer<Boolean> observer : observers) {
            observer.accept(newLeaderStatus);
        }
    }
    @PostConstruct
    public void initLeaderElection() throws IOException {
        log.info("Starting Election.");
        ApiClient client = Config.defaultClient();
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        log.info("Kubernetes API client configured successfully.");

        String appNamespace = "aps";
        String appName = "aps-server";
        String lockIdentity = InetAddress.getLocalHost().getHostAddress();
        ConfigMapLock lock = new ConfigMapLock(appNamespace, appName, lockIdentity);
        log.info("ConfigMap lock created with identity: {}", lockIdentity);

        LeaderElectionConfig leaderElectionConfig = new LeaderElectionConfig(
                lock,
                Duration.ofMillis(10000),
                Duration.ofMillis(8000),
                Duration.ofMillis(2000));
        log.info("Leader election configuration set up successfully.");

        LeaderElector leaderElector = new LeaderElector(leaderElectionConfig);
        new Thread(() -> {
            leaderElector.run(
                    () -> {
                        isLeader = true;
                        log.info("This pod is now the leader. Starting.");
                        notifyObservers(true);
                    },
                    () -> {
                        isLeader = false;
                        log.info("This pod is no longer the leader. Attempting to stop.");
                        notifyObservers(false);
                    });
        }, "LeaderElectionThread").start();
        log.info("Leader election thread started.");
    }

    public boolean isLeader() {
        return isLeader;
    }
}
