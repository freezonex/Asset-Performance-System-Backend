package com.freezonex.aps.listener;

import com.freezonex.aps.config.LeaderService;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.freezonex.aps.modules.asset.service.MqttSender;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class DebeziumListener {

    @Autowired
    private MqttSender mqttSender;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    @Resource
    private LeaderService leaderService;

    public DebeziumListener(Configuration customerConnectorConfiguration) {
        log.info("Initializing DebeziumListener with configuration: {}", customerConnectorConfiguration.asProperties());
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(customerConnectorConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        log.info("Debezium Engine initialized successfully.");
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        log.debug("Received a change event: {}", sourceRecordRecordChangeEvent);
        if (!leaderService.isLeader()) {
            log.info("Skipping change event processing because this pod is not the leader.");
            return;
        }

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
    private void start() {
        if (leaderService.isLeader()) {
            executor.execute(debeziumEngine);
        }
    }

    @PreDestroy
    private void stop() {
        log.info("Stopping Debezium Engine.");
        if (this.debeziumEngine != null) {
            try {
                this.debeziumEngine.close();
                log.info("Debezium Engine stopped successfully.");
            } catch (IOException e) {
                log.error("Error stopping Debezium Engine.", e);
            }
        } else {
            log.warn("Attempted to stop Debezium Engine but it was already null.");
        }
    }

}