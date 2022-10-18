#!/bin/bash

topics=("learning-vertrag-command" "learning-vertrag-internal-event" "learning-vertrag-validation" "learning-vertrag-external-event" "learning-vertrag-state" "learning-markt-command" "learning-markt-internal-event" "learning-markt-validation" "learning-markt-external-event" "learning-markt-state")

for topic in "${topics[@]}"; do
curl 'http://localhost:30191/api/clusters/local/topics' -X POST -H 'Content-Type: application/json' \
--data '{"name":"'$topic'","partitions":'$1',"replicationFactor":1,"configs":{"cleanup.policy":"delete","retention.ms":"604800000","retention.bytes":"-1","max.message.bytes":"1000012","min.insync.replicas":"1"}}'
done
