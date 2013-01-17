Axemblr Provisionr
==================

*Simple Service for Managing Pools of 10s or 100s of Virtual Machines*

With Provisionr we want to solve the problem of cloud portability by hiding completely the API and only focusing on building a cluster that matches the same set of assumptions on all clouds, assumptions like: running a specific operating system (e.g. Ubuntu LTS), having the same set of pre-installed packages and binaries, sane dns settings (forward & reverse ip resolution - as needed for [Hadoop](http://hadoop.apache.org/)), ntp settings, networking settings, ssh admin access, vpn access etc.

[![Build Status](https://buildhive.cloudbees.com/job/axemblr/job/axemblr-provisionr/badge/icon)](https://buildhive.cloudbees.com/job/axemblr/job/axemblr-provisionr/)
Features
--------

* Can provision 10s or 100s of virtual machines across multiple clouds
* Fully persistent granular internal workflows based on [Activiti](http://activiti.org/)
* The application server can be restarted at any time with no impact
* Has a modular architecture based on OSGi and [Apache Karaf](http://karaf.apache.org/)
* Can be used as an interactive tool or as a REST service (work in progress) 

How does it look like?
----------------------

For each cloud provider we define an Activiti pool management process similar to this:

![Sample pool management process](http://people.apache.org/~asavu/sample.bpmn20.png)

You can use Activiti Explorer to inspect the process while running:

![Activiti Explorer](http://people.apache.org/~asavu/explorer.png)

... and manage everything from an interactive shell:

![Shell](http://people.apache.org/~asavu/shell.png)

Who is behind this?
-------------------

Core developers:

* Andrei Savu - asavu@axemblr.com - [LinkedIn](http://www.linkedin.com/in/sandrei), [Github](https://github.com/andreisavu)
* Ioan Eugen Stan - ieugen@axemblr.com - [LinkedIn](http://www.linkedin.com/in/ieugen), [Github](https://github.com/ieugen)

License
-------

Apache License Version 2.0
http://apache.org/licenses/LICENSE-2.0.txt

Get involved in the community 
-----------------------------

For those of you who really dig our software, we have created two mailing lists that yo can join depending on your involvement: 

* If you are a user join: [provisionr-users@googlegroups.com](https://groups.google.com/forum/?fromgroups#!forum/provisionr-users)
* If you wish to develop Provisionr go to [provisionr-dev@googlegroups.com](https://groups.google.com/forum/?fromgroups#!forum/provisionr-dev)

Join us on IRC channel *#axemblr* on *irc.freenode.net*.

We appreciate any kind of help so don't heasitate. 


Supported cloud providers
-------------------------

We are working on making all this run as expected on [Amazon EC2](http://aws.amazon.com/ec2/) & [CloudStack](http://incubator.apache.org/cloudstack/) 

How to create a distribution?
-----------------------------

You can build a binary release by running:

    $ ./scripts/create_distribution.sh 

And you will find the .tar.gz file in:

    karaf/assembly/target/com.axemblr.provisionr-*.tar.gz

How to run?
-----------

Extract the custom Karaf distribution:

    $ cd karaf/assembly/target/
    $ tar xvfz com.axemblr.provisionr-0.0.1-SNAPSHOT.tar.gz
    $ cd com.axemblr.provisionr-0.0.1-SNAPSHOT

Start and configure the Amazon provisionr:

    $ ./bin/provisionr
    provisionr [0.0.1-SNAPSHOT] $ config:edit com.axemblr.provisionr.amazon
    provisionr [0.0.1-SNAPSHOT] $ config:proplist
        service.pid = com.axemblr.provisionr.amazon
        secretKey = secret
        felix.fileinstall.filename = file:[...]/etc/com.axemblr.provisionr.amazon.cfg
        region = us-east-1
        accessKey = access
    provisionr [0.0.1-SNAPSHOT] $ config:propset accessKey "XXXXXXX"
    provisionr [0.0.1-SNAPSHOT] $ config:propset secretKey "XXXXXXX"
    provisionr [0.0.1-SNAPSHOT] $ config:update
    provisionr [0.0.1-SNAPSHOT] $ config:list "(service.pid=com.axemblr.provisionr.amazon)"

Create a pool:

    provisionr [0.0.1-SNAPSHOT] $ provisionr:create --id amazon --key mypool --size 2 --hardware-type m1.small
    provisionr [0.0.1-SNAPSHOT] $ provisionr:pools 
    Pool{provider=Provider{id='amazon', endpoint='', accessKey='XXXXXXX', options='{region=us-east-1}'}, 
    network=Network{type=default, ingress=[Rule{cidr=0.0.0.0/0, ports=(-1?-1], protocol=ICMP}, 
    Rule{cidr=0.0.0.0/0, ports=[22?22], protocol=TCP}], options={}}, adminAccess=AdminAccess{username='andreisavu', 
    publicKey='ssh-rsa ....'}, software=Software{baseOperatingSystem='default', files={}, 
    packages=[git-core, vim], options={}}, hardware=Hardware{type='t1.micro', options={}}, minSize=1, 
    cacheBaseImage=false, expectedSize=1, bootstrapTimeInSeconds=900, options={}}
    Business Key: mypool

You should see an Activiti process in execution:

    provisionr [0.0.1-SNAPSHOT] $ activiti:list

    BPMN Deployments
    ----------------
     ID   Name                               Deployment Time         
    [1  ][com.axemblr.provisionr.amazon    ][Dec 13, 2012 1:52:05 PM]
    [5  ][com.axemblr.provisionr.cloudstack][Dec 13, 2012 1:52:07 PM]

    BPMN Process Definitions
    -------------------------
     Definition ID   Name            Ver  Resource                                
    [amazon:1:4    ][Amazon Process][1  ][OSGI-INF/activiti/amazon.bpmn20.xml    ]
    [cloudstack:1:8][cloudstack    ][1  ][OSGI-INF/activiti/cloudstack.bpmn20.xml]

    History of BPMN Process Instances
    ---------------------------------
     Def  Ins  Sta  End 

    Active BPMN Process Instances
    -----------------------------
     Definition  Ins  Executions    
    [amazon:1:4][9  ][13,14,16,19,9]

Make sure you also check the Activiti Explorer at (login kermit:kermit): 

    http://localhost:8181/activiti-explorer/

If you want to manage Apache Karaf you can access the webconsole at

    http://localhost:8181/system/console

And don't forget to destroy the pool

    provisionr [0.0.1-SNAPSHOT] $ provisionr:destroy --id amazon --key mypool    

How to build?
-------------

Maven as usual:

    $ mvn clean install 

This will download dependencies, compile the sources and run unit tests and some of the integration tests (karaf feature install)

How to test?
------------

All the unit tests are executed as part of the normal build.

For SSH tests we assume the current user can do "ssh localhost" and authenticate
automatically using the local ssh keys. 

You can run tests against a specific cloud provider for individual activities by running:

    $ ./scripts/activities_test.sh ID # (amazon or cloudstack)

Or you can the test the pool management process as a whole by running:

    $ ./scripts/process_test.sh ID # (amazon or cloudstack) 

Your cloud provider credentials should be in ~/.m2/settings.xml or sent as system properties

```xml
<profiles>
    <profile>
        <id>provisionr-credentials</id>
        <activation><activeByDefault>true</activeByDefault></activation>
        <properties>
            <test.cloudstack.provider.accessKey>cs-key</test.cloudstack.provider.accessKey>
            <test.cloudstack.provider.secretKey>cs-secret</test.cloudstack.provider.secretKey>
            <test.cloudstack.provider.endpoint>...</test.cloudstack.provider.endpoint>
            <test.cloudstack.provider.zoneId>1</test.cloudstack.provider.zoneId>
            <test.cloudstack.provider.templateId>3012</test.cloudstack.provider.templateId>
            <test.cloudstack.provider.serviceOffering>105</test.cloudstack.provider.serviceOffering>

            <test.amazon.provider.accessKey>xxxxx</test.amazon.provider.accessKey>
            <test.amazon.provider.secretKey>xxxxx</test.amazon.provider.secretKey>
            <test.amazon.provider.region>us-east-1</test.amazon.provider.region>
        </properties>
    </profile>
</profiles>
```

Thanks! 

