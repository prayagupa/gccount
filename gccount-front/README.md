<b>CASHLESS PAYMENT SYSTEM</b>

[![Build Status](https://codeship.io/projects/aa0d2f90-c70c-0131-5e67-568348087483/status)](https://codeship.io/projects/aa0d2f90-c70c-0131-5e67-568348087483/status)


[Run UnitTestCases](http://stackoverflow.com/a/2219029/432903)
==============
`$ grails test-app unit: TransactionControllerTests.testMethodName -echoOut`

[Run spock test cases](http://grails101.wordpress.com/2012/04/22/test-grails-application-with-spock/)
=====================
`$ grails test-app unit:spock CategorySpec -echoOut`


degug-app
=========

```
$ grails-debug run-app
```

run-app
=======

change database conf first in datasource

```
$ vim +47 gccount-front/grails-app/conf/DataSource.groovy
```

configure mysql username/password for loggin in this app ( in Bootstrap )

```
$ vi +20 grails-app/conf/Bootstrap.groovy
```

run-app

```
$./run-app
```

or
```
$ bash run-app.sh
```



<b>DEMO</b>

![Image Alt](https://raw.githubusercontent.com/iPrayag/gccount/master/gccount-front/doc/main.png)
