## URL Shortener

This is a service, which provides the functionality to replace the long URLs with short aliases 
and acts as a proxy for decoding them back.

## Application stack:
 * Scala 2.12.6
 * Akka
 * Akka-Http
 * MongoDb

## Requirements:
1. A user should be able to load the index page of your site and be presented with an input field 
where they can enter a URL.
2. Upon entering the URL, a "shortened" version of that url is created and shown to the user as a URL to the site 
you're building.
3. When visiting that "shortened" version of the URL, the user is redirected to the original URL. 
4. Additionally, if a URL has already been shortened by the system, and it is entered a second time, the first 
shortened URL should be given back to the user.
 
**Note**: Some of them were omitted wittingly. See more details at **Motivation** section.

###How to run the application:
**Pre-requisites**: 
Mongo DB is required to run this service. 
By default, it looks for running Mongo server at `mongodb://localhost:27017` and creates a `url-shortener` DB on it.
Alternatively, one may set the respective environment variable to connect somewhere else.
See more detailed at the **Configuration** section.
1. `cd url-shortener`
2. `sbt run`

##How to use it:
There are two endpoint available with no UI. You need to use `curl` or applications like Postman to give it a try.
The examples below are given with use of `curl`.
1. **POST** to `/` with a URL provided as a payload creates a shorten alias for that URL. 
The correspondence between them is stored persistently in the DB.
######Example: 
```
curl -X POST -d "https://ain.ua/2015/02/02/programmirovanie-otstoj-a-programmisty-psixi-mnenie-insajdera" http://localhost:9000
```
######Output with default configuration: `http://localhost:9000/8d6c8e61`

2.**GET** to `/{key}`, where key is alias, generated via POST method. 
Usually it causes a redirect to the long version of URL that was hidden behind alias. 
It may not, if the original URL was leading nowhere.
######Example: 
```curl -X GET  http://localhost:9000/8d6c8e61```
######Output: `The request, and all future requests should be repeated using <a href="https://ain.ua/2015/02/02/programmirovanie-otstoj-a-programmisty-psixi-mnenie-insajdera">this URI</a>.`

**Note**: One may want to try GET in the browser, because browser *actually redirects* to the target page.

##Configuration:
Configuration is done via environment variables, each variable has its default value, targeting to local setup.
There are 4 variables to configure:
1. **HTTP_HOST** - host name at which service is being runned. Default: **localhost**
2. **HTTP_PORT** - port, at which application is being runned. Default: **9000**
3. **MONGO_URI** - connection string to Mongo server. Default: **mongodb://localhost:27017**
4. **URL_SHORTENER_DB_NAME** - database name at Mongo server. Default: **url-shortener**

##Implementation Motivation:
- **Scala** was chosen as a language I want to continue work professionally.
- **Akka + Akka-Http** bundle was chosen due to my familiarity with it. 
Originally, I was considering to use Http4s + Monix (for service layer), 
but with given time frame, I was not sure about picking tools I am little familiar with.
*Note*: I *do* realize that Java + SpringBoot solution would take a lot less code to implement, but c'mon it is Java! :)
- **MongoDb** as a database. I find this task fits perfectly for NoSql database solution. 
I was thinking about Reddis, but see time frame comment above :)
- I *do use a hash function* to generate the aliases after all.
The motivation for it was dictated by 4th requirement: 
*Additionally, if a URL has already been shortened by the system, and it is entered a second time, the first 
 shortened URL should be given back to the user.*
I am not aware of any other more or less safe solution, which would give the same alias for repeating calls. 
I see two options to do it: 
  1. Generate aliases based on the original URL (hashing)
  2. Search for the original URL presence in the DB (Not an option, given the possible complexity of the target URLs)
If there is any other solution, I would be glad to learn it :)
Also, I considered a classic approach to this task, which is incremental id generation and Bijection conversion to Base64.
But this approach clearly disobeys the aforementioned requirement
- *I witterly did not implement UI*. 
Although, it is rather simple requirement, given that last time I made something on UI was five years ago, 
it did provided a lot of fuss for me. 
I have invested a big portion of time to finally recall how it is done, but apparently not quite :)
In the end it broke again and I decided to move on with other tasks, 
which I consider more important since everything is still available via API.