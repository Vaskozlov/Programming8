package org.example.client.commands.core

import application.Application

abstract class ApplicationDependantCommand protected constructor(
    protected var application: Application
) : Command()
