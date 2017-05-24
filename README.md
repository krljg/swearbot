# swearbot #

A simple IRC bot that keeps track of how much users swear

### Build and Run

Use maven to build:

`mvn install`

Run the self-contained Jar file:
  
`java -jar target/swearbot-1.0.0-SNAPSHOT-jar-with-dependencies.jar <name> <server> <channel>`

Where _name_ is the name of the bot, ie "swearbot", _server_ is the address of the server ie irc.dal.net, and _channel_ is the channel to join ie #chat. 

