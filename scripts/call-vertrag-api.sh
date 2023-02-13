#! /usr/bin/bash

for (( i=1; i<=5; i++ ))
do
	response=$(curl --header "Content-Type: application/json" \
 		--request POST \
  		--data '{"bezeichnung":"Vertrag '$i'","beginn":"2023-01-01","ende":"2023-12-01"}' \
  		http://localhost:8082/vertrag/erstellen)

	id=$(jq -r '.id' <<< "$response")

  sleep 0.1

	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"vertragId": "'$id'","beginn":"2023-03-01"}' \
  		http://localhost:8083/vertrag/beginn

  sleep 0.1

	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"vertragId": "'$id'","ende":"2023-09-01"}' \
  		http://localhost:8082/vertrag/ende

  sleep 0.1

	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"vertragId": "1234","beginn":"2023-03-01"}' \
  		http://localhost:8083/vertrag/beginn

  sleep 0.1

done

 
  
 
