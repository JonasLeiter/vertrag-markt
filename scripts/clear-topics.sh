#!/bin/bash

topics=("learning-vertrag-command" "learning-vertrag-internal-event" "learning-vertrag-validation" "learning-vertrag-external-event" "learning-vertrag-state" "learning-markt-command" "learning-markt-internal-event" "learning-markt-validation" "learning-markt-external-event" "learning-markt-state")

for topic in "${topics[@]}"; do
curl 'http://localhost:30191/api/clusters/local/topics/'$topic'/messages' -X DELETE -H 'Content-Type: application/json'
done
