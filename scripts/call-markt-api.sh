#! /usr/bin/bash

for (( i=1; i<=5; i++ ))
do	
	vertragResponse=$(curl --header "Content-Type: application/json" \
 		--request POST \
  		--data '{"bezeichnung":"Vertrag '$i'","beginn":"2023-01-01","ende":"2023-12-01"}' \
  		http://localhost:8082/vertrag/erstellen)
  

	vertragId=$(jq -r '.id' <<< "$vertragResponse")
	
	sleep 1

	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"vertragId": "'$vertragId'","beginn":"2023-03-01"}' \
  		http://localhost:8083/vertrag/beginn

	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"vertragId": "'$vertragId'","ende":"2023-09-01"}' \
  		http://localhost:8082/vertrag/ende
  		
  	sleep 1
  		
	response=$(curl --header "Content-Type: application/json" \
 		--request POST \
  		--data '{"ort":"Berlin","datum":"2011-11-11", "vertragId":"'$vertragId'"}' \
  		http://localhost:8080/markt)
  
	id=$(jq -r '.aggregateId' <<< "$response")
	
	sleep 1

	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"id": "'$id'","datum":"2011-12-19"}' \
  		http://localhost:8081/markt/datum
  
	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"id": "'$id'","ort":"Muenchen"}' \
  		http://localhost:8080/markt/ort

done

 
  
 
