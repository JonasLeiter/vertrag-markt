for (( i=1; i<=5; i++ ))
do
	response=$(curl --header "Content-Type: application/json" \
 		--request POST \
  		--data '{"ort":"Berlin","datum":"2011-11-11"}' \
  		http://localhost:8080/markt)
  
	id=$(jq -r '.aggregateId' <<< "$response")

	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"id": "'$id'","datum":"2011-12-19"}' \
  		http://localhost:8081/markt/datum
  
	curl --header "Content-Type: application/json" \
  		--request PUT \
  		--data '{"id": "'$id'","ort":"Muenchen"}' \
  		http://localhost:8080/markt/ort

done

 
  
 
