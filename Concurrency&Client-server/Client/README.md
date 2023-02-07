# Client

## Client Description

### Execute instructions

To executor this application from IDE, you can create a "Run Configuration". 
In Intellij, the run configuration needs to use JDK 17 with main class point to 
org.neu.cs6650.client.Main. Then you need to fill the CLI argument with the following 
parameters: 

    <Server_ip> <Thread_pool_size> <Load_size>
For example, the target server has an ip address 18.237.117.228, and we want to use 350 thread to
send 500k request. 

    18.237.117.228 350 500000

Note: thread_pool_size=350 give the best performance with around 9000~10000 req/sec.

### Major Classes, packages, relationships

Main class under org.neu.cs6650.client is the entry point of this project. 
It governs the client's life cycle. It will read 3 command line arguments,
which are server_ip, thread_pool_size and load_size. The main class inits an ExecutorService
instance with the given thread_pool_size in order to control the concurrency. 

The Main class inits a HttpService instance that contains an apache http client
with connection pool size equals thread_pool_size. The required retry is built within 
the client. This service class handles all http requests. 

A SummaryService instance is initialized by the main class to handle recording all values 
and finalizing all statistic analysis at the end.

Within Main class, it will create a number(load_size) of RequestThread instances and submit 
them into the ExecutorService instances for processing. Within a RequestThread instance, it will 
call HttpService to send one request and record the elapsedTime into a SummaryService instance.  

After all RequestInstances are submitted, main will shut down the ExecutorService and use a
CountDownLatch instance to block the process until all request are processed. At the end of 
main, the SummaryService instance will perform statistic analysis and print out the result
to console.

All model classes are within org.neu.cs6650.model package and all service classes are within
org.neu.cs6650.service package. 

### UML Document
![UML image](UML.png)

## Client example report (Client 1 & 2)
### Regular Tail
On EC2 instance with 350 threads and 350 connections

![Regular Tail image](ClientScreenShot.png)

## Performance Plot
![Performance Plot](PerformancePlot.png)

## Spring boot result
Here's performance when I use spring boot to build the remote server. 

![Spring boot performance plot](SpringbootClientScreenShot.png)

Spring boot performance result is similar to the performance result when I use 120 threads in 
my local client with customized servlet on remote server. Here's my client's performance result with 
120 threads:

![Client with 120 threads](Client_120.png)