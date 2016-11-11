# ubirch-avatar-service

ubirch device-configuration and -dataflow service

## General Information

TODO

## Scala Dependencies

### `config`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.avatar" %% "config" % "0.2.0-SNAPSHOT"
    )

### `core`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.avatar" %% "core" % "0.2.0-SNAPSHOT"
    )

### `model-rest`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.avatar" %% "model-rest" % "0.2.0-SNAPSHOT"
    )

### `model-db`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.avatar" %% "model-db" % "0.2.0-SNAPSHOT"
    )
        
### `server`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.bintrayRepo("hseeberger", "maven")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.avatar" %% "server" % "0.2.0-SNAPSHOT"
    )
        
### `server`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.avatar" %% "model" % "0.2.0-SNAPSHOT"
    )

### `util`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.avatar" %% "util" % "0.2.0-SNAPSHOT"
    )

## REST Methods

### Welcome / Health

    curl localhost:8080/

If server is healthy response is:

    200 {"version":"1.0","status":"OK","message":"Welcome to the ubirchAvatarService"}

### Device Information

#### TODO: title

TODO: description

    curl -XGET localhost:8080/api/avatarService/v1/device

    curl -XPOST localhost:8080/api/avatarService/v1/device

#### TODO: title

TODO: description

    curl -XGET localhost:8080/api/avatarService/v1/device/<DEVICE_ID>

    curl -XPOST localhost:8080/api/avatarService/v1/device/<DEVICE_ID>

    curl -XDELETE localhost:8080/api/avatarService/v1/device/<DEVICE_ID>

#### Device State

TODO: description

    curl -XGET localhost:8080/api/avatarService/v1/device/<DEVICE_ID>/state

    curl -XPOST localhost:8080/api/avatarService/v1/device/<DEVICE_ID>/state

#### TODO: title

TODO: description

    curl -XGET localhost:8080/api/avatarService/v1/device/stub/<DEVICE_ID>

### Device Data

#### Raw

Raw data comes directly from devices and is not yet human readable.

    curl -XPOST localhost:8080/api/avatarService/v1/device/data/raw -H "Content-Type: application/json" -d '{
      "deviceId": "57a7892e-e707-4256-81e4-2e579213e6b8",
      "messageId": "8aa3d0ec-9ec8-4785-93e9-6fd1705dace6",
      "deviceType": "lightsLamp",
      "timestamp": "2016-06-30T11:39:51Z",
      "deviceTags": [
        "ubirch#0",
        "actor"
      ],
      "deviceMessage": {
        "foo": 23,
        "bar": "ubirch-sensor-data"
      }
    }'

#### History

Historic data is generated by sending in raw data which is then transformed to "processed" data.

The main difference between raw and processed data is simple. Raw data has been generated by devices and is not human 
readable. Applying a device specific transformation to raw data we get processed data which is human readable.

Query historic device data (CAUTION: `from` and `page_size` may be zero or larger).

    curl -XGET localhost:8080/api/avatarService/v1/device/<DEVICE_ID>/data/history

    curl -XGET localhost:8080/api/avatarService/v1/device/<DEVICE_ID>/data/history/<FROM>

    curl -XGET localhost:8080/api/avatarService/v1/device/<DEVICE_ID>/data/history/<FROM>/<PAGE_SIZE>

### Device Types

Devices have types and this set of methods allows us to manage them.

#### Query all available device types

    curl -XGET localhost:8080/api/avatarService/v1/device/deviceType

#### Create device type

    curl -XPOST localhost:8080/api/avatarService/v1/device/deviceType -H "Content-Type: application/json" -d '{
        "key": "trackle",
        "name": {
          "de": "Trackle",
          "en": "Trackle"
        },
        "icon": "trackle",
        "defaults": {
          "properties": {},
          "config": {
            "i": 60
          },
          "tags": [
            "ubirch#1",
            "actor",
            "trackle"
          ]
        }
      }'

##### Response (Success)

    HTTP/1.1 200 OK
    Access-Control-Allow-Origin: *
    Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
    Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent
    Access-Control-Allow-Credentials: true
    Server: ubirch-avatar-service
    Date: Thu, 10 Nov 2016 16:30:51 GMT
    Content-Type: application/json
    Content-Length: 158
    
    {"key":"trackle","name":{"de":"Trackle","en":"Trackle"},"icon":"trackle","defaults":{"properties":{},"config":{"i":60},"tags":["ubirch#1","actor","trackle"]}}

##### Response (Error)

    HTTP/1.1 400 Bad Request
    Access-Control-Allow-Origin: *
    Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
    Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent
    Access-Control-Allow-Credentials: true
    Server: ubirch-avatar-service
    Date: Thu, 10 Nov 2016 16:35:40 GMT
    Content-Type: application/json
    Content-Length: 199
    
    {
      "version" : "1.0",
      "status" : "NOK",
      "errorType" : "CreateError",
      "errorMessage": "another deviceType with key=trackle already exists or otherwise something else on the server went wrong"
    }

#### Update Device Type

    curl -XPUT localhost:8080/api/avatarService/v1/device/deviceType -H "Content-Type: application/json" -d '{
        "key": "trackle",
        "name": {
          "de": "Trackle",
          "en": "Trackle"
        },
        "icon": "trackle",
        "defaults": {
          "properties": {},
          "config": {
            "i": 120
          },
          "tags": [
            "ubirch#0",
            "actor",
            "trackle"
          ]
        }
      }'

##### Response (Success)

    HTTP/1.1 200 OK
    Access-Control-Allow-Origin: *
    Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
    Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent
    Access-Control-Allow-Credentials: true
    Server: ubirch-avatar-service
    Date: Thu, 10 Nov 2016 16:33:24 GMT
    Content-Type: application/json
    Content-Length: 159
    
    {"key":"trackle","name":{"de":"Trackle","en":"Trackle"},"icon":"trackle","defaults":{"properties":{},"config":{"i":120},"tags":["ubirch#0","actor","trackle"]}}

##### Response (Error)

    HTTP/1.1 400 Bad Request
    Access-Control-Allow-Origin: *
    Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
    Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent
    Access-Control-Allow-Credentials: true
    Server: ubirch-avatar-service
    Date: Thu, 10 Nov 2016 16:27:31 GMT
    Content-Type: application/json
    Content-Length: 186
    
    {
      "version" : "1.0",
      "status" : "NOK",
      "errorType" : "UpdateError",
      "errorMessage": "no deviceType with key=trackle exists or otherwise something else on the server went wrong"
    }

#### Create default device types but only if no other types exist in the database:

    curl -XGET localhost:8080/api/avatarService/v1/device/deviceType/init

## Configuration

TODO


## AWS

### AWS CLI

On MacOS you can install the aws-cli tool through brew:

    brew install awscli

To configure it then run:

    aws configure

The default region should be `us-east-1` while the output format can remain None since it's not relevant yet.

### AWS Configuration

The AvatarService opens a connection to AWS which depends on the following environment variables:

    export AWS_ACCESS_KEY_ID=foo
    export AWS_SECRET_ACCESS_KEY=bar


## Deployment Notes

### Elasticsearch

The service requires the following mappings for things to work as expected:

    curl -XPOST 'localhost:9200/ubirch-device-raw-data' -H "Content-Type: application/json" -d '{
      "mappings": {
        "devicemessage" : {
          "properties" : {
            "a" : {
              "type" : "string",
              "index": "not_analyzed"
            },
            "id" : {
              "type" : "string",
              "index": "not_analyzed"
            }
          }
        }
      }
    }'

    curl -XPOST 'localhost:9200/ubirch-device-history' -H "Content-Type: application/json" -d '{
      "mappings": {
        "devicedata" : {
          "properties" : {
            "deviceId" : {
              "type" : "string",
              "index": "not_analyzed"
            },
            "messageId" : {
              "type" : "string",
              "index": "not_analyzed"
            }
          }
        }
      }
    }'

    curl -XPOST 'localhost:9200/ubirch-device-type' -H "Content-Type: application/json" -d '{
      "mappings": {
        "devicetype" : {
          "properties" : {
            "key" : {
              "type" : "string",
              "index": "not_analyzed"
            }
          }
        }
      }
    }'

## Automated Tests

TODO

## Local Setup

TODO

## create docker image

    ./sbt server/docker
