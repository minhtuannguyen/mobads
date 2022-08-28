# mobads
contains a minimal set of dns domains which should be blocked on mobile device. See dns.txt

# Sanitize
This task going into the list and remove out-of-date entries by checking them with `nslookup`
It requires `babashka` in order to run

```shell
bb sanitize  
```