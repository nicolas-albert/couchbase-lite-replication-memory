# couchbase-lite-replication-memory

## CBL 1.4 and 1.3.1, replication of 200k docs : linear consumption of memory

Sample project to see the memory consumption during a CBL replication of 200k.

I add a *final long cap = 20000;* that cancel and redo a new replication each *cap* document replicated : the memory is released.

![chart](cbl-replication-chart)