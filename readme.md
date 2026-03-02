# MongoAuthServerTest

This is just a quick assemble of my existing POC code to illustrate a problem with native image on Spring Authorization
Server + native image + nimbus token encoder/decoder + graalvm native maven plugin, please don't mind the messy
project setup and dependency management.

## What is included in this project
This project includes:
1. infradocker project with shellscript to setup the infrastructure needed (single instance valkey in default docker
network + 3 nodes MongoDB replica set within a docker network)
2. mongodbauthserver project - the spring security authorization server, running on MongoDB, with mongock db migration
to setup a test account
3. apigateway project - the api gateway for intended FE app (in this case, the provided postman collection, see point 6)
4. resourceserver project - the api connected directly to apigateway, it calls the simplewebresource api (see point 5)
to perform some aggregation and be consumed by the postman collection (see point 6)
5. simpleresourceserver project - a simple api to be consumed by resourceserver project (see point 5)
6. postman project - includes a test postman collection for testing, contains the login, Books and logout call

## The Issue
When I build a native image for the mongoauthserver project, run it under docker desktop and do the following: 
1. Login through mongoauthserver
2. Call resourceserver through apigateway
3. Wait for the access token to expire (currently set to 1 minute for quick test, so wait for 2 minutes should be enough)
4. Call resourceserver through apigateway again
5. The following error will happen

```
com.nimbusds.jose.shaded.gson.JsonIOException: Failed making field 'java.time.Instant#seconds' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type.
See https://github.com/google/gson/blob/main/Troubleshooting.md#reflection-inaccessible⁠
	at com.nimbusds.jose.shaded.gson.internal.reflect.ReflectionHelper.makeAccessible(ReflectionHelper.java:76) ~[na:na]
	at com.nimbusds.jose.shaded.gson.internal.bind.ReflectiveTypeAdapterFactory.getBoundFields(ReflectiveTypeAdapterFactory.java:388) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jose.shaded.gson.internal.bind.ReflectiveTypeAdapterFactory.create(ReflectiveTypeAdapterFactory.java:161) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jose.shaded.gson.Gson.getAdapter(Gson.java:628) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jose.shaded.gson.internal.bind.TypeAdapterRuntimeTypeWrapper.write(TypeAdapterRuntimeTypeWrapper.java:57) ~[na:na]
	at com.nimbusds.jose.shaded.gson.internal.bind.MapTypeAdapterFactory$Adapter.write(MapTypeAdapterFactory.java:222) ~[na:na]
	at com.nimbusds.jose.shaded.gson.internal.bind.MapTypeAdapterFactory$Adapter.write(MapTypeAdapterFactory.java:158) ~[na:na]
	at com.nimbusds.jose.shaded.gson.Gson.toJson(Gson.java:944) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jose.shaded.gson.Gson.toJson(Gson.java:899) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jose.shaded.gson.Gson.toJson(Gson.java:848) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jose.shaded.gson.Gson.toJson(Gson.java:825) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jose.util.JSONObjectUtils.toJSONString(JSONObjectUtils.java:547) ~[na:na]
	at com.nimbusds.jose.Payload.toString(Payload.java:363) ~[na:na]
	at com.nimbusds.jose.Payload.toBytes(Payload.java:395) ~[na:na]
	at com.nimbusds.jose.Payload.toBase64URL(Payload.java:412) ~[na:na]
	at com.nimbusds.jose.JWSObject.composeSigningInput(JWSObject.java:193) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jose.JWSObject.<init>(JWSObject.java:112) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:10.4]
	at com.nimbusds.jwt.SignedJWT.<init>(SignedJWT.java:60) ~[na:na]
	at org.springframework.security.oauth2.jwt.NimbusJwtEncoder.serialize(NimbusJwtEncoder.java:215) ~[na:na]
	...
	Caused by: java.lang.reflect.InaccessibleObjectException: Unable to make field private final long java.time.Instant.seconds accessible: module java.base does not "opens java.time" to unnamed module @2ef3cc2f
	at java.base@25.0.2/java.lang.reflect.AccessibleObject.throwInaccessibleObjectException(AccessibleObject.java:353) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:na]
	at java.base@25.0.2/java.lang.reflect.AccessibleObject.checkCanSetAccessible(AccessibleObject.java:329) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:na]
	at java.base@25.0.2/java.lang.reflect.AccessibleObject.checkCanSetAccessible(AccessibleObject.java:277) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:na]
	at java.base@25.0.2/java.lang.reflect.Field.checkCanSetAccessible(Field.java:179) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:na]
	at java.base@25.0.2/java.lang.reflect.Field.setAccessible(Field.java:173) ~[org.example.mongodbauthserver.MongoDbAuthServerApplication:na]
	at com.nimbusds.jose.shaded.gson.internal.reflect.ReflectionHelper.makeAccessible(ReflectionHelper.java:68) ~[na:na]
	... 141 common frames omitted
```

Note that in the mongodbauthserver's plugin settings, I have already included the followings for graalvm native maven plugin:
```
<plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <!-- these settings do not work... -->
                <configuration>
                    <buildArgs>
                        <buildArg>
                            --add-opens=java.base/java.time=ALL-UNNAMED
                        </buildArg>
                    </buildArgs>
                    <runtimeArgs>
                        <runtimeArg>-H:AdditionalRuntimeOptions=--add-opens=java.base/java.time=ALL-UNNAMED</runtimeArg>
                    </runtimeArgs>
                </configuration>
            </plugin>
```

But both of the add opens are not working, as shown in the stacktrace.

## How to setup
To setup this project, please do the followings:
1. Run the infrasetup.sh in infradocker project
2. Clean-install mongodbauthserver
3. Try running mongodbauthserver with test profile activated in IDE to setup the mongodb collections and testing documents
4. Run the spring-boot-build-image maven goal with native profile turned on
5. Run the docker-run.sh in mongodbauthserver to have the native image started in docker desktop
6. Repeat step 2, 4 and 5 for apigateway, resourceserver and simpleresource while mongodbauthserver is running

## How to test
1. Import the postman collection from postman/src
2. Run each of the requests, of cause, always run login to setup the session cookie