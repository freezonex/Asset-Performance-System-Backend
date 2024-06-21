package com.freezonex.aps.listener;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.kubernetes.client.extended.leaderelection.LeaderElectionConfig;
import io.kubernetes.client.extended.leaderelection.LeaderElector;
import io.kubernetes.client.extended.leaderelection.resourcelock.ConfigMapLock;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.freezonex.aps.modules.asset.service.MqttSender;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class DebeziumListener {

    @Autowired
    private MqttSender mqttSender;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private volatile boolean isLeader = false;

    public DebeziumListener(Configuration customerConnectorConfiguration) throws Exception {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
            .using(customerConnectorConfiguration.asProperties())
            .notifying(this::handleChangeEvent)
            .build();
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        if (!isLeader) return;  // Only process events if this pod is the leader

        var sourceRecord = sourceRecordRecordChangeEvent.record();
        var sourceRecordChangeValue = (Struct) sourceRecord.value();
        String payload = String.format("Key = %s, Value = %s; SourceRecordChangeValue = '%s'", sourceRecord.key(), sourceRecord.value(), sourceRecordChangeValue);
        log.info("payload = '{}'", payload);

        mqttSender.sendMessage("dataChange", payload);

        // if (sourceRecordChangeValue != null) {
        //     Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

        // Operation.READ operation events are always triggered when application initializes
        // We're only interested in CREATE operation which are triggered upon new insert registry
        //     if(operation != Operation.READ) {
        //         String record = operation == Operation.DELETE ? BEFORE : AFTER; // Handling Update & Insert operations.

        //         Struct struct = (Struct) sourceRecordChangeValue.get(record);
        //         Map<String, Object> payload = struct.schema().fields().stream()
        //             .map(Field::name)
        //             .filter(fieldName -> struct.get(fieldName) != null)
        //             .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
        //             .collect(toMap(Pair::getKey, Pair::getValue));

        //         // this.customerService.replicateData(payload, operation);
        //         log.info("Updated Data: {} with Operation: {}", payload, operation.name());
        //     }
        // }
    }

    @PostConstruct
    private void start() throws IOException {
        ApiClient client = Config.defaultClient();
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);

        String appNamespace = "aps";
        String appName = "apsServer";

        String lockIdentity = InetAddress.getLocalHost().getHostAddress();
        ConfigMapLock lock = new ConfigMapLock(appNamespace, appName, lockIdentity);

        LeaderElectionConfig leaderElectionConfig = new LeaderElectionConfig(
                lock,
                Duration.ofMillis(10000),
                Duration.ofMillis(8000),
                Duration.ofMillis(2000));

        LeaderElector leaderElector = new LeaderElector(leaderElectionConfig);

        leaderElector.run(
                () -> {
                    isLeader = true;
                    log.info("This pod is now the leader. Starting Debezium Engine.");
                    executor.execute(debeziumEngine);
                },
                () -> {
                    isLeader = false;
                    log.info("This pod is no longer the leader. Stopping Debezium Engine.");
                    try {
                        stop();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @PreDestroy
    private void stop() throws IOException {
        if (Objects.nonNull(this.debeziumEngine)) {
            this.debeziumEngine.close();
        }
    }

}