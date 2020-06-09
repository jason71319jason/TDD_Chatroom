# TDD Chatroom
[![Build Status](https://travis-ci.org/jason71319jason/TDD_Chatroom.svg?branch=master)](https://travis-ci.org/jason71319jason/TDD_Chatroom)

## Load Github Project to IntelliJ

1. Open IntelliJ
2. Click `Get from Version Control`
3. Login your Github account
4. Refresh until you see TDD_Chatroom
5. Clone it


## Load dependencies through maven

1. View > Tool Windows > Maven
2. Click `Reimport All Maven Projects` (like cycle icon)

## Test client, server locally (without test)

1. Open Client.java
2. Click `Edit configurations...` on the right top of window
3. Click `Allow parallel run`

## Git Add
* Auto add if you click auto-add 

## Git Commit

1. VSC > Commit
2. Type some "Commit message"

## Git Push

1. VSC > Git > Push

if fail to push, you might pull repo first and merge it. Then push it again.

## Git Pull

1. VSC > Git > Pull

## Naming Policy

Try you best to follow https://google.github.io/styleguide/javaguide.html#s5-naming

## Goal

* ChatRoom
    * Multi-threading Server
        1. Open socket and wait for accepting new client
        2. When client is accepted, create a new thread (handler) for client.
        3. When client send message, handler need to broadcast it or deliver to someone.
    * Client
        1. Open socket and connect with server
        2. A handler handle communication