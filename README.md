# A Kafka Streams based stateful stream processing application on Oracle Application Container Cloud

Check the two-part blog series for details

- Part I - concepts, architecture, code
- Part II - setup, configuration, test 

## Build

- `git clone https://github.com/abhirockzz/accs-kafka-streams.git`
- `mvn clean install` - creates `acc-ehcs-kafka-streams-dist.zip` in `target` directory

## Deploy to Oracle Cloud

- Use Oracle Developer Cloud - read [the blog](tbd)
- Use Oracle Application Container Cloud [console](http://docs.oracle.com/en/cloud/paas/app-container-cloud/csjse/exploring-application-deployments-page.html#GUID-5E4472B1-F5C6-4556-908C-D76C4C14FC60)
- Use Oracle Application Container Cloud [REST APIs](http://docs.oracle.com/en/cloud/paas/app-container-cloud/apcsr/op-paas-service-apaas-api-v1.1-apps-%7BidentityDomainId%7D-post.html)
- Use Oracle Application Container Cloud [PSM APIs](https://docs.oracle.com/en/cloud/paas/java-cloud/pscli/accs-push.html)

## Test

Refer to [this section](tbd) from the blog
