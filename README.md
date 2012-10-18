
Axemblr Provisionr
==================

Pool management service for 10s or 100s of VMs.

Features
--------

Can provision 10s or 100s of VMs accross multipe clouds
Fully persistent granular internal workflows
Can be restarted while starting VMs

Specification - Draft 1 -
-------------------------

Endpoints

GET /pools - list all pools
GET /pools/<id> - retrieve a specific pool
POST /pools - create new pool
DELETE /pools/<id> - delete pool

Request elements

- Image ID (real or pseudo)
- Hardware ID (real or pseudo)
- Network (vpc subnet, security group, firewall)
- Location (region, placement group)
- Provider credentials
- Max Size for Batch operations
- Time between retries
- Max number of retries
- Min / Max size 
- Bootstrap Timeout
- Extra 

Pseudo - config defined 
One provisionr instance can usually work with a single cloud provider


