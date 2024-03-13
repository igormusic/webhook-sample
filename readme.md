# Sample webhook processor

Example:

```bash 
curl --location 'http://localhost:8080/webhook' \
--header 'Signature: sha1=39786973e7e79ce163d7273db9e10c0230748f31' \
--header 'Content-Type: application/json' \
--data '{
    "event_id":1209283434,
    "event_type": "NEW_TRANSACTION"
}'
```



