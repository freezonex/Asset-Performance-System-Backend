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

@Slf4j
@Service
public class LeaderService {

    private volatile boolean isLeader = false;
    @PostConstruct
    public void initLeaderElection() throws IOException {
        log.info("Starting DebeziumListener.");
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
                        log.info("This pod is now the leader. Starting Debezium Engine.");
                    },
                    () -> {
                        isLeader = false;
                        log.info("This pod is no longer the leader. Attempting to stop Debezium Engine.");
                    });
        }, "LeaderElectionThread").start();
        log.info("Leader election thread started.");
    }

    public boolean isLeader() {
        return isLeader;
    }
}
