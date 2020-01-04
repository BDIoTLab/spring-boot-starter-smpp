# spring-boot-starter-smpp

[![codecov](https://codecov.io/gh/MikeSafonov/spring-boot-starter-smpp/branch/master/graph/badge.svg)](https://codecov.io/gh/MikeSafonov/spring-boot-starter-smpp)
[![Travis-CI](https://travis-ci.com/MikeSafonov/spring-boot-starter-smpp.svg?branch=master)](https://travis-ci.com/MikeSafonov/spring-boot-starter-smpp)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=alert_status)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=security_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=bugs)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=code_smells)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=ncloc)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_spring-boot-starter-smpp&metric=sqale_index)](https://sonarcloud.io/dashboard?id=MikeSafonov_spring-boot-starter-smpp)


This Spring Boot starter can be used by any Spring Boot application that wants to send SMS messages 
using [SMPP](https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer). [SMPP v3.4 spec](http://docs.nimta.com/SMPP_v3_4_Issue1_2.pdf)

## Features

- [x] Sending message with delivery receipt 
- [x] Sending datagram message
- [ ] Sending silent message
- [x] Cancel message
- [ ] Multiply SMPP connection
- [ ] SMPP connection load balancing

## Key abstraction

This starter provide several abstraction:

### SenderClient

This interface represents smpp protocol transmitter client. This is entry point to sending any messages.
Spring-boot-starter-smpp comes with several implementations:


<dl>
  <dt>DefaultSenderClient</dt>
  <dd>This is default implementation which create smpp connection and performing all requests</dd>
  
  <dt>TestSenderClient</dt>
  <dd>Implementation of SenderClient which should be used for testing purpose. 
  This client may provide real smpp connection via incoming implementation of SenderClient. 
  Every incoming request will be redirected to real SenderClient only if list of allowed phone numbers contains message destination phone. 
  Otherwise response will be generated via SmppResultGenerator</dd>
    
  <dt>MockSenderClient</dt>
  <dd>Implementation of SenderClient which not perform any connection via smpp and only generate response by using SmppResultGenerator</dd>
</dl>


### SmppResultGenerator

Implementations of this interface is used by MockSenderClient or TestSenderClient clients to generate request response - MessageResponse and cancel request response - CancelMessageRespons. 
Starter by default use **AlwaysSuccessSmppResultGenerator** which always generate success response with random smsc message id.

You can implement own **SmppResultGenerator** to add custom logic.

### TypeOfAddressParser

**DefaultSenderClient** use implementation of this class to detect TON and NPI parameters for source and destination address of message.
Starter provide **DefaultTypeOfAddressParser** and **UnknownTypeOfAddressParser** implementations. 
**DefaultTypeOfAddressParser** supports international and alphanumeric ton parameters, otherwise return UNKNOWN ton/npi. **UnknownTypeOfAddressParser**
always return UNKNOWN ton/npi. This means what your SMS center must detect this parameters by himself.


### ResponseClient

This abstraction represent connection via SMPP with RECEIVER type. Key purpose is listening delivery receipts. Starter implements
this interface by **DefaultResponseClient**. This class keeping smpp connection and pushing delivery receipts to **DeliveryReportConsumer**.

### DeliveryReportConsumer

This class dedicated to handle DeliveryReport on client side. Client may build custom logic on receiving delivery receipts by implementing this interface.
If client doesnt provide any implementation of **DeliveryReportConsumer** then starter use **NullDeliveryReportConsumer** which ignore any delivery receipts.

### SenderManager

This is high level abstraction over sender clients.

This starter comes with one default implementation - **StrategySenderManager**. **StrategySenderManager** holds
list of smsc connections and return sender client based on some rules which implemented by **IndexDetectionStrategy**

There are two default implementation of **IndexDetectionStrategy** - **RandomIndexDetectionStrategy**(return random sender client) and **RoundRobinIndexDetectionStrategy**
(return sender client based on round and robbin algorithm). **RoundRobinIndexDetectionStrategy** strategy used by default.

## Configuration
TODO
